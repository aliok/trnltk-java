/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.morphology.lexicon;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.ImmutableLexeme;
import org.trnltk.morphology.model.Lexeme;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.model.structure.TurkicLetter;
import org.trnltk.morphology.phonetics.TurkishAlphabet;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LexemeCreator {

    //XXX: this part needs a rewrite!
    Lexeme createLexemeFromLine(final String _line) {
        if (StringUtils.isBlank(_line))
            return null;

        final String line = CharMatcher.WHITESPACE.trimAndCollapseFrom(_line, ' ');

        String primaryPosStr = null;
        String secondaryPosStr = null;
        Set<String> lexemeAttributeStrs = new HashSet<String>();

        String rootStr;
        String meta;

        // XXX: horrible implementation
        if (line.contains(" ")) {

            final String[] splits = line.split(" ", 2);
            Validate.isTrue(splits.length == 2, "Line contains more than one space : '%s'", line);
            rootStr = splits[0].trim();
            meta = splits[1].trim();
            Validate.isTrue(meta.startsWith("[") && meta.endsWith("]"));
            meta = meta.substring(1, meta.length() - 1);
        } else {
            rootStr = line;
            meta = null;
        }

        String lemma = rootStr;

        if (StringUtils.isNotBlank(meta)) {
            final Iterable<String> metaParts = Splitter.on(';').omitEmptyStrings().trimResults().split(meta);
            for (String metaPart : metaParts) {
                if (metaPart.startsWith("P:")) {
                    metaPart = metaPart.substring("P:".length());
                    if (metaPart.contains(",")) {
                        final List<String> metaPartItems = Lists.newArrayList(Splitter.on(',').omitEmptyStrings().trimResults().split(metaPart));
                        Validate.isTrue(metaPartItems.size() == 2);
                        primaryPosStr = metaPartItems.get(0);
                        secondaryPosStr = metaPartItems.get(1);
                    } else {
                        primaryPosStr = metaPart;
                    }
                } else if (metaPart.startsWith("A:")) {
                    metaPart = metaPart.substring("A:".length());
                    lexemeAttributeStrs = Sets.newHashSet(Splitter.on(',').omitEmptyStrings().trimResults().split(metaPart));
                } else if (metaPart.startsWith("R:")) {
                    metaPart = metaPart.substring("R:".length());
                    rootStr = metaPart;
                } else if (metaPart.startsWith("S:")) {
                    // TODO: skip, Z3 uses some special stuff!
                } else {
                    throw new RuntimeException("Unable to parse line : " + line);
                }
            }
        }

        final PrimaryPos primaryPos = StringUtils.isNotBlank(primaryPosStr) ? PrimaryPos.converter().getEnum(primaryPosStr) : null;
        final SecondaryPos secondaryPos = StringUtils.isNotBlank(secondaryPosStr) ? SecondaryPos.converter().getEnum(secondaryPosStr) : null;

        return this.createLexeme(lemma, rootStr,
                primaryPos,
                secondaryPos,
                Sets.newEnumSet(LexemeAttribute.converter().getEnums(lexemeAttributeStrs), LexemeAttribute.class));
    }

    private Lexeme createLexeme(String lemma, String rootStr, PrimaryPos primaryPos, SecondaryPos secondaryPos,
                                Set<LexemeAttribute> lexemeAttributes) {
        String lemmaRoot = rootStr;

        if (primaryPos == null) {
            if (rootStr.endsWith("mek") || rootStr.endsWith("mak")) {
                primaryPos = PrimaryPos.Verb;
                lemmaRoot = rootStr.substring(0, rootStr.length() - 3);
            } else {
                primaryPos = PrimaryPos.Noun;
            }
        }

        lexemeAttributes = this.inferMorphemicAttributes(lemmaRoot, primaryPos, lexemeAttributes);

        return new ImmutableLexeme(lemma, lemmaRoot, primaryPos, secondaryPos, Sets.immutableEnumSet(lexemeAttributes));
    }

    private int vowelCount(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (TurkishAlphabet.getLetterForChar(c).isVowel())
                count++;
        }
        return count;
    }

    Set<LexemeAttribute> inferMorphemicAttributes(final String lemmaRoot, final PrimaryPos primaryPos, final Set<LexemeAttribute> _lexemeAttributes) {
        final char lastChar = lemmaRoot.charAt(lemmaRoot.length() - 1);
        final TurkicLetter lastLetter = TurkishAlphabet.getLetterForChar(lastChar);
        final int vowelCount = this.vowelCount(lemmaRoot);

        final HashSet<LexemeAttribute> lexemeAttributes = new HashSet<LexemeAttribute>(_lexemeAttributes);

        final boolean isVerb = PrimaryPos.Verb.equals(primaryPos);
        final boolean isNoun = PrimaryPos.Noun.equals(primaryPos);
        final boolean isAdjective = PrimaryPos.Adjective.equals(primaryPos);
        final boolean isNounCompound = isNoun && lexemeAttributes.contains(LexemeAttribute.CompoundP3sg);

        if (isVerb) {
            inferVerbMorphemicAttributes(lastLetter, vowelCount, lexemeAttributes);
        } else if (isNounCompound) {
            inferNounCompoundMorphemicAttributes(lexemeAttributes);
        } else if (isNoun || isAdjective) {
            inferNounOrAdjectiveMorphemicAttributes(lemmaRoot, lastLetter, vowelCount, lexemeAttributes);
        }
        return lexemeAttributes;
    }

    private void inferNounOrAdjectiveMorphemicAttributes(String lemmaRoot, TurkicLetter lastLetter, int vowelCount, HashSet<LexemeAttribute> lexemeAttributes) {
        if (lexemeAttributes.contains(LexemeAttribute.VoicingOpt)) {
            lexemeAttributes.remove(LexemeAttribute.Voicing);
            lexemeAttributes.remove(LexemeAttribute.NoVoicing);
        } else {
            if (vowelCount > 1 && lastLetter.isStopConsonant()
                    && !lexemeAttributes.contains(LexemeAttribute.NoVoicing) && !lexemeAttributes.contains(LexemeAttribute.InverseHarmony))
                lexemeAttributes.add(LexemeAttribute.Voicing);
            else if (lemmaRoot.endsWith("nk") || lemmaRoot.endsWith("og") || lemmaRoot.endsWith("rt"))
                lexemeAttributes.add(LexemeAttribute.Voicing);
            else if (!lexemeAttributes.contains(LexemeAttribute.Voicing))
                lexemeAttributes.add(LexemeAttribute.NoVoicing);
        }
    }

    private void inferNounCompoundMorphemicAttributes(HashSet<LexemeAttribute> lexemeAttributes) {
        if (lexemeAttributes.contains(LexemeAttribute.VoicingOpt)) {
            lexemeAttributes.remove(LexemeAttribute.Voicing);
            lexemeAttributes.remove(LexemeAttribute.NoVoicing);
        } else if (!lexemeAttributes.contains(LexemeAttribute.Voicing)) {
            lexemeAttributes.add(LexemeAttribute.NoVoicing);
        }
    }

    private void inferVerbMorphemicAttributes(TurkicLetter lastLetter, int vowelCount, HashSet<LexemeAttribute> lexemeAttributes) {
        if (lastLetter.isVowel()) {
            lexemeAttributes.add(LexemeAttribute.ProgressiveVowelDrop);
            lexemeAttributes.add(LexemeAttribute.Passive_In);
        }

        if (vowelCount > 1 && !lexemeAttributes.contains(LexemeAttribute.Aorist_A))
            lexemeAttributes.add(LexemeAttribute.Aorist_I);

        if (vowelCount == 1 && !lexemeAttributes.contains(LexemeAttribute.Aorist_I))
            lexemeAttributes.add(LexemeAttribute.Aorist_A);

        if (lastLetter.equals(TurkishAlphabet.L_l))
            lexemeAttributes.add(LexemeAttribute.Passive_In);

        if (!CollectionUtils.containsAny(lexemeAttributes, LexemeAttribute.CAUSATIVES)) {
            if (lastLetter.isVowel() ||
                    ((lastLetter.equals(TurkishAlphabet.L_l) || lastLetter.equals(TurkishAlphabet.L_r)) && vowelCount > 1)) {
                lexemeAttributes.add(LexemeAttribute.Causative_t);
            } else if (lastLetter.equals(TurkishAlphabet.L_t) && vowelCount < 2) {
                lexemeAttributes.add(LexemeAttribute.Causative_Ir);
            } else {
                lexemeAttributes.add(LexemeAttribute.Causative_dIr);
            }
        }

        if (lexemeAttributes.contains(LexemeAttribute.ProgressiveVowelDrop))
            lexemeAttributes.add(LexemeAttribute.NoVoicing);

        if (!lexemeAttributes.contains(LexemeAttribute.Voicing) && !lexemeAttributes.contains(LexemeAttribute.NoVoicing))
            lexemeAttributes.add(LexemeAttribute.NoVoicing);
    }

}