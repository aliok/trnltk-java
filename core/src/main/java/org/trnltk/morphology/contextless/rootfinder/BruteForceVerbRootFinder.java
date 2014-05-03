/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.trnltk.morphology.contextless.rootfinder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.trnltk.model.letter.TurkicLetter;
import org.trnltk.model.letter.TurkishAlphabet;
import org.trnltk.model.letter.TurkishChar;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.*;
import org.trnltk.model.suffix.SuffixFormSequence;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.*;

/**
 * Finds the possible roots by brute force.
 * <p/>
 * Checks for the signs of the orthographic changes, and finds roots according to that.
 * Considers progressive vowel drop (başla+ıyor -> başlıyor), voicing (git+er -> gider), aorist A (yap+ar), aorist I (dikil+ir),
 * causatives and passives.
 * <p/>
 * Returns phonetically valid verbs. For example 'ürk' and 'büyült' are valid, but 'zanh' is not valid.
 * <p/>
 * In verbs voicing only occurs on roots ending with 't', so others (pçk) are ignored.
 * Ignores inverse harmony, since verbs don't have it.
 */
public class BruteForceVerbRootFinder implements RootFinder {
    private final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(new SuffixFormSequenceApplier());
    private final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
    private static final SuffixFormSequence INFINITIVE_SUFFIX_FORM = new SuffixFormSequence("mAk");

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence wholeSurface) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (wholeSurface == null || wholeSurface.isBlank())
            return false;

        if (!wholeSurface.startsWith(partialInput))
            return false;

        if (partialInput.length() < 2) // not possible except (d,diyor) and (y,yiyor). but they are already in the dictionary
            return false;

        final TurkishChar lastVowel = partialInput.getLastVowel();
        if (lastVowel == null)
            return false;

        if (wholeSurface.length() > partialInput.length()) {
            final TurkishChar firstCharAfterPartialInput = wholeSurface.charAt(partialInput.length());
            if (Character.isUpperCase(firstCharAfterPartialInput.getCharValue()))
                return false;
        }

        return true;
    }

    @Override
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions"})
    public Collection<DynamicRoot> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence wholeSurface) {
        final TurkishChar lastVowel = partialInput.getLastVowel();

        final TurkishSequence rootSeq = partialInput;
        final TurkishSequence lemmaSeq = rootSeq;
        final TurkishSequence lemmaRootSeq = lemmaSeq;
        final PrimaryPos primaryPos = PrimaryPos.Verb;
        final SecondaryPos secondaryPos = null;
        final EnumSet<LexemeAttribute> lexemeAttributes = EnumSet.noneOf(LexemeAttribute.class);
        final DynamicLexeme lexeme = new DynamicLexeme(lemmaSeq.getUnderlyingString(), lemmaRootSeq.getUnderlyingString(), primaryPos, secondaryPos, lexemeAttributes);
        final EnumSet<PhoneticExpectation> phoneticExpectations = EnumSet.noneOf(PhoneticExpectation.class);
        final EnumSet<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, lexemeAttributes);

        final DynamicRoot defaultAttrRoot = new DynamicRoot(rootSeq, lexeme, phoneticAttributes, phoneticExpectations);

        final TurkishChar lastChar = partialInput.getLastChar();
        final TurkicLetter lastLetter = lastChar.getLetter();

        final boolean partialSurfaceCanBeRootOfAVerb = this.seemsLikeAValidVerbRoot(partialInput);

        final String wholeSurfaceStr = wholeSurface.getUnderlyingString();
        final String partialInputStr = partialInput.getUnderlyingString();

        final int vowelCount = TurkishAlphabet.vowelCount(partialInputStr);

        final boolean mightHaveProgressiveVowelDrop = !lastLetter.isVowel()
                && strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("iyor", "ıyor", "uyor", "üyor"));

        // see LexemeCreator for the reason of the following condition!
        final boolean mightHaveAorist_I = !lastLetter.isVowel() && vowelCount > 1;

        // cannot have Aorist_A and Aorist_I at the same time
        // and a verb must have one of them!
        final boolean mightHaveAorist_A = !mightHaveAorist_I;

        if (mightHaveAorist_A)
            defaultAttrRoot.getLexeme().getAttributes().add(LexemeAttribute.Aorist_A);
        else
            defaultAttrRoot.getLexeme().getAttributes().add(LexemeAttribute.Aorist_I);

        this.setLexemeAndPhoneticAttributes(Arrays.asList(defaultAttrRoot));
        this.setLemma(Arrays.asList(defaultAttrRoot));

        if (wholeSurface.equals(partialInput))
            return partialSurfaceCanBeRootOfAVerb ? Arrays.asList(defaultAttrRoot) : Collections.<DynamicRoot>emptyList();

        final TurkishChar firstCharAfterPartialInput = wholeSurface.charAt(partialInput.length());
        final TurkicLetter firstLetterAfterPartialInput = firstCharAfterPartialInput.getLetter();

        // for other letters, no voicing in verbs. {git+er->gider} vs {yapar, açar, diker}
        final boolean voicingMightHaveHappened = lastLetter.equals(TurkishAlphabet.L_d) && firstLetterAfterPartialInput.isVowel();

        final Set<DynamicRoot> possibleProgressiveVowelDropRoots = mightHaveProgressiveVowelDrop
                ? this.getProgressiveDropRoots(defaultAttrRoot, lastVowel)
                : new HashSet<DynamicRoot>();

        final Set<DynamicRoot> possibleCausativeRoots = this.getPossibleCausativeRoots(lastLetter, partialInput, wholeSurface, defaultAttrRoot);
        final Set<DynamicRoot> possiblePassiveRoots = this.getPossiblePassiveRoots(lastLetter, partialInput, wholeSurface, defaultAttrRoot);

        if (voicingMightHaveHappened) {
            Function<DynamicRoot, DynamicRoot> voicingRootFunction = new Function<DynamicRoot, DynamicRoot>() {
                @Override
                public DynamicRoot apply(DynamicRoot input) {
                    return getPossibleVoicingRoot(input);
                }
            };

            final Collection<DynamicRoot> possibleProgressiveVowelDropRoots_voicing = Collections2.transform(ImmutableSet.copyOf(possibleProgressiveVowelDropRoots), voicingRootFunction);
            possibleProgressiveVowelDropRoots.addAll(possibleProgressiveVowelDropRoots_voicing);

            final Collection<DynamicRoot> possibleCausativeRoots_voicing = Collections2.transform(ImmutableSet.copyOf(possibleCausativeRoots), voicingRootFunction);
            possibleCausativeRoots.addAll(possibleCausativeRoots_voicing);

            final Collection<DynamicRoot> possiblePassiveRoots_voicing = Collections2.transform(ImmutableSet.copyOf(possiblePassiveRoots), voicingRootFunction);
            possiblePassiveRoots.addAll(possiblePassiveRoots_voicing);
        }

        final HashSet<DynamicRoot> generatedRoots = new HashSet<DynamicRoot>();

        generatedRoots.add(defaultAttrRoot);

        if (voicingMightHaveHappened)
            generatedRoots.add(this.getPossibleVoicingRoot(defaultAttrRoot));

        generatedRoots.addAll(possibleProgressiveVowelDropRoots);
        generatedRoots.addAll(possibleCausativeRoots);
        generatedRoots.addAll(possiblePassiveRoots);

        this.setLexemeAndPhoneticAttributes(generatedRoots);
        this.setLemma(generatedRoots);

        return Collections2.filter(generatedRoots, new Predicate<DynamicRoot>() {
            @Override
            public boolean apply(DynamicRoot input) {
                return seemsLikeAValidVerbRoot(new TurkishSequence(input.getLexeme().getLemmaRoot()));
            }
        });
    }

    private void setLexemeAndPhoneticAttributes(Collection<DynamicRoot> generatedRoots) {
        for (DynamicRoot generatedRoot : generatedRoots) {
            final DynamicLexeme lexeme = generatedRoot.getLexeme();
            final TurkishSequence rootSeq = generatedRoot.getSequence();
            final String rootStr = rootSeq.getUnderlyingString();
            generatedRoot.setPhoneticAttributes(this.phoneticsAnalyzer.calculatePhoneticAttributes(rootSeq, lexeme.getAttributes()));
            if (rootStr.endsWith("d") && lexeme.getLemmaRoot().endsWith("t")) {
                lexeme.getAttributes().remove(LexemeAttribute.NoVoicing);
                lexeme.getAttributes().add(LexemeAttribute.Voicing);
            } else {
                lexeme.getAttributes().remove(LexemeAttribute.Voicing);
                lexeme.getAttributes().add(LexemeAttribute.NoVoicing);
            }
        }
    }

    private void setLemma(Collection<DynamicRoot> generatedRoots) {
        for (DynamicRoot generatedRoot : generatedRoots) {
            final DynamicLexeme lexeme = generatedRoot.getLexeme();
            final Pair<TurkishSequence, String> applicationPair = this.phoneticsEngine.apply(new TurkishSequence(lexeme.getLemmaRoot()), generatedRoot.getPhoneticAttributes(),
                    INFINITIVE_SUFFIX_FORM, lexeme.getAttributes());
            final TurkishSequence word = applicationPair.getLeft();
            final String appliedSuffixForm = applicationPair.getRight();
            Validate.isTrue(!word.isBlank());
            Validate.notBlank(appliedSuffixForm);
            lexeme.setLemma(word.getUnderlyingString() + appliedSuffixForm);
        }
    }

    private boolean seemsLikeAValidVerbRoot(TurkishSequence partialInput) {
        final TurkishChar lastChar = partialInput.getLastChar();
        final TurkicLetter lastLetter = lastChar.getLetter();
        final TurkishChar previousChar = partialInput.charAt(partialInput.length() - 2);
        final TurkicLetter previousLetter = previousChar.getLetter();

        return lastLetter.isVowel()
                || previousLetter.isVowel()
                || (Arrays.asList(TurkishAlphabet.L_l, TurkishAlphabet.L_r, TurkishAlphabet.L_n).contains(previousLetter) && !lastLetter.isContinuant());
    }

    private Set<DynamicRoot> getProgressiveDropRoots(DynamicRoot defaultAttrRoot, TurkishChar lastVowel) {
        /*
        başla - +Iyor --> başlıyor
        elle  - +Iyor --> elliyor
        oyna  - +Iyor --> oynuyor
        söyle - +Iyor --> söylüyor
        kazı  - +Iyor --> kazıyor
        kaz   - +Iyor --> kazıyor

        başıyor   : başlamak or başlımak (skip başlumak)
        elliyor   : ellemek or ellimek (skip ellümek)
        oynuyor   : oynamak or oynumak (skip oynımak)
        söylüyor  : söylemek or söylümek (skip söylimek)
        kazıyor   : kazamak or kazımak (skip kazumak)
        */

        final List<Character> droppedVowels = new ArrayList<Character>();
        final boolean lastVowelIsFrontal = lastVowel.getLetter().isFrontal();
        final boolean lastVowelIsRounded = lastVowel.getLetter().isRounded();

        // since there is no inverse harmony in verbs, we can determine the dropped vowel
        if (!lastVowelIsFrontal) {
            droppedVowels.add('a');
            if (!lastVowelIsRounded)
                droppedVowels.add('ı');
            else
                droppedVowels.add('u');
        } else {
            droppedVowels.add('e');
            if (!lastVowelIsRounded)
                droppedVowels.add('i');
            else
                droppedVowels.add('ü');
        }

        final HashSet<DynamicRoot> generatedRoots = new HashSet<DynamicRoot>();

        for (Character droppedVowel : droppedVowels) {
            final DynamicRoot generatedRoot = new DynamicRoot(defaultAttrRoot);
            generatedRoot.getLexeme().setLemmaRoot(generatedRoot.getLexeme().getLemmaRoot() + droppedVowel);
            generatedRoot.getLexeme().getAttributes().add(LexemeAttribute.ProgressiveVowelDrop);

            generatedRoots.add(generatedRoot);
        }

        return generatedRoots;
    }

    private Set<DynamicRoot> getPossibleCausativeRoots(TurkicLetter lastLetter, TurkishSequence partialInput, TurkishSequence wholeSurface, DynamicRoot defaultAttrRoot) {
        // no voicing can happen on causative_t
        final String wholeSurfaceStr = wholeSurface.getUnderlyingString();
        final String partialInputStr = partialInput.getUnderlyingString();

        final boolean mightHaveCausative_t = wholeSurfaceStr.startsWith(partialInputStr + 't') && (lastLetter.isContinuant() || lastLetter.isVowel());

        final boolean mightHaveCausative_Ir = this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("ır", "ir", "ur", "ür"));

        // no voicing can happen on causative_It
        final boolean mightHaveCausative_It = this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("ıt", "it", "ut", "üt"));

        final boolean mightHaveCausative_Ar = this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("ar", "er"));

        final boolean mightHaveCausative_dIr = this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("dır", "dir", "dur", "dür", "tır", "tir", "tur", "tür"));

        final ImmutableMap<LexemeAttribute, Boolean> mightHaveCausatives = new ImmutableMap.Builder<LexemeAttribute, Boolean>()
                .put(LexemeAttribute.Causative_t, mightHaveCausative_t)
                .put(LexemeAttribute.Causative_Ir, mightHaveCausative_Ir)
                .put(LexemeAttribute.Causative_It, mightHaveCausative_It)
                .put(LexemeAttribute.Causative_Ar, mightHaveCausative_Ar)
                .put(LexemeAttribute.Causative_dIr, mightHaveCausative_dIr)
                .build();

        final HashSet<DynamicRoot> causativeRoots = new HashSet<DynamicRoot>();

        for (Map.Entry<LexemeAttribute, Boolean> lexemeAttributeBooleanEntry : mightHaveCausatives.entrySet()) {
            final LexemeAttribute causativeAttr = lexemeAttributeBooleanEntry.getKey();
            final Boolean mightHaveHappened = lexemeAttributeBooleanEntry.getValue();

            if (!mightHaveHappened)
                continue;

            // cannot have other causatives at the same time
            // cannot have any other passive at the same time
            // cannot have progressive vowel drop at the same time
            // needs to have aorist_A or aorist_I but not both

            final DynamicRoot generatedRoot = new DynamicRoot(defaultAttrRoot);

            final EnumSet<LexemeAttribute> lexemeAttributes = EnumSet.of(causativeAttr);
            lexemeAttributes.addAll(defaultAttrRoot.getLexeme().getAttributes());
            generatedRoot.getLexeme().setAttributes(lexemeAttributes);

            generatedRoot.setPhoneticAttributes(this.phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, generatedRoot.getLexeme().getAttributes()));

            causativeRoots.add(generatedRoot);

        }

        return causativeRoots;
    }

    private Set<DynamicRoot> getPossiblePassiveRoots(TurkicLetter lastLetter, TurkishSequence partialInput, TurkishSequence wholeSurface, DynamicRoot defaultAttrRoot) {
        final String wholeSurfaceStr = wholeSurface.getUnderlyingString();
        final String partialInputStr = partialInput.getUnderlyingString();

        final boolean mightHavePassive_Il =
                (!lastLetter.isVowel() && this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("ıl", "il", "ul", "ül")))
                        || (lastLetter.isVowel() && wholeSurfaceStr.startsWith(partialInputStr + 'l'));

        final boolean mightHavePassive_In =
                (!lastLetter.isVowel() && this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("ın", "in", "un", "ün")))
                        || (lastLetter.isVowel() && wholeSurfaceStr.startsWith(partialInputStr + 'n'));

        final boolean mightHavePassive_InIl =
                (!lastLetter.isVowel() && this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("ınıl", "inil", "unul", "ünül")))
                        || (lastLetter.isVowel() && this.strStartsWithAnyAdditionOfStr(wholeSurfaceStr, partialInputStr, Arrays.asList("nıl", "nil", "nul", "nül")));

        final ImmutableMap<LexemeAttribute, Boolean> mightHavePassives = new ImmutableMap.Builder<LexemeAttribute, Boolean>()
                .put(LexemeAttribute.Passive_Il, mightHavePassive_Il)
                .put(LexemeAttribute.Passive_In, mightHavePassive_In)
                .put(LexemeAttribute.Passive_InIl, mightHavePassive_InIl)
                .build();

        final HashSet<DynamicRoot> passiveRoots = new HashSet<DynamicRoot>();

        for (Map.Entry<LexemeAttribute, Boolean> lexemeAttributeBooleanEntry : mightHavePassives.entrySet()) {
            final LexemeAttribute passiveAttr = lexemeAttributeBooleanEntry.getKey();
            final Boolean mightHaveHappened = lexemeAttributeBooleanEntry.getValue();

            if (!mightHaveHappened)
                continue;

            // cannot have other passives at the same time
            // cannot have any other causative at the same time
            // cannot have progressive vowel drop at the same time
            // needs to have aorist_A or aorist_I but not both

            final DynamicRoot generatedRoot = new DynamicRoot(defaultAttrRoot);

            final EnumSet<LexemeAttribute> lexemeAttributeSet = EnumSet.of(passiveAttr);
            lexemeAttributeSet.addAll(defaultAttrRoot.getLexeme().getAttributes());
            generatedRoot.getLexeme().setAttributes(lexemeAttributeSet);

            generatedRoot.setPhoneticAttributes(this.phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, generatedRoot.getLexeme().getAttributes()));

            passiveRoots.add(generatedRoot);

        }

        return passiveRoots;
    }

    private DynamicRoot getPossibleVoicingRoot(DynamicRoot root) {
        // return only the reverse_voiced root
        Validate.isTrue(root.getSequence().getLastChar().getLetter().equals(TurkishAlphabet.L_d),
                "This is weird! This method should have been called after possible voicing was already checked.");
        final DynamicRoot cloneRoot = new DynamicRoot(root);
        // ignoring Voicing+ProgressiveVowelDrop
        final String orgLemmaRoot = cloneRoot.getLexeme().getLemmaRoot();
        cloneRoot.getLexeme().setLemma(orgLemmaRoot.substring(0, orgLemmaRoot.length() - 1) + TurkishAlphabet.L_t.charValue());
        cloneRoot.getLexeme().setLemmaRoot(cloneRoot.getLexeme().getLemma());
        cloneRoot.getLexeme().getAttributes().add(LexemeAttribute.Voicing);
        return cloneRoot;
    }

    private boolean strStartsWithAnyAdditionOfStr(String wholeSurfaceStr, String partialInputStr, List<String> suffixes) {
        for (String suffix : suffixes) {
            if (wholeSurfaceStr.startsWith(partialInputStr + suffix))
                return true;
        }
        return false;
    }
}