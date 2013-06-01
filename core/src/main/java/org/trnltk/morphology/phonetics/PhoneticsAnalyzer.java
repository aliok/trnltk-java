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

package org.trnltk.morphology.phonetics;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.structure.TurkicLetter;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class PhoneticsAnalyzer {

    public EnumSet<PhoneticAttribute> calculatePhoneticAttributes(final String surface, Collection<LexemeAttribute> lexemeAttributes) {
        return this.calculatePhoneticAttributes(new TurkishSequence(surface), lexemeAttributes);
    }

    public EnumSet<PhoneticAttribute> calculatePhoneticAttributes(final TurkishSequence surface, Collection<LexemeAttribute> lexemeAttributes) {
        final EnumSet<PhoneticAttribute> phoneticAttributes = this.calculatePhoneticAttributesOfPlainSequence(surface);
        if (CollectionUtils.isEmpty(lexemeAttributes))
            return phoneticAttributes;

        if (lexemeAttributes.contains(LexemeAttribute.InverseHarmony)) {
            if (phoneticAttributes.contains(PhoneticAttribute.LastVowelBack)) {
                phoneticAttributes.remove(PhoneticAttribute.LastVowelBack);
                phoneticAttributes.add(PhoneticAttribute.LastVowelFrontal);
            } else if (phoneticAttributes.contains(PhoneticAttribute.LastVowelFrontal)) {
                phoneticAttributes.remove(PhoneticAttribute.LastVowelFrontal);
                phoneticAttributes.add(PhoneticAttribute.LastVowelBack);
            }
        }

        if (lexemeAttributes.contains(LexemeAttribute.EndsWithAyn)) {
            Validate.isTrue(phoneticAttributes.contains(PhoneticAttribute.LastLetterVowel));
            phoneticAttributes.remove(PhoneticAttribute.LastLetterVowel);
            phoneticAttributes.add(PhoneticAttribute.LastLetterConsonant);
        }

        return phoneticAttributes;
    }

    EnumSet<PhoneticAttribute> calculatePhoneticAttributesOfPlainSequence(final TurkishSequence surface) {
        final EnumSet<PhoneticAttribute> attributes = EnumSet.noneOf(PhoneticAttribute.class);
        final TurkishChar lastVowelChar = surface.getLastVowel();
        final TurkishChar firstChar = surface.charAt(0);
        final TurkicLetter firstLetter = firstChar.getLetter();
        final TurkishChar lastChar = surface.getLastChar();
        final TurkicLetter lastLetter = lastChar.getLetter();

        if (firstLetter.isVowel())
            attributes.add(PhoneticAttribute.FirstLetterVowel);
        else
            attributes.add(PhoneticAttribute.FirstLetterConsonant);

        if (lastVowelChar != null) {
            final TurkicLetter lastVowelLetter = lastVowelChar.getLetter();
            if (lastVowelLetter.isRounded())
                attributes.add(PhoneticAttribute.LastVowelRounded);
            else
                attributes.add(PhoneticAttribute.LastVowelUnrounded);

            if (lastVowelLetter.isFrontal())
                attributes.add(PhoneticAttribute.LastVowelFrontal);
            else
                attributes.add(PhoneticAttribute.LastVowelBack);
        } else {
            attributes.add(PhoneticAttribute.HasNoVowel);
        }

        if (lastLetter.isVowel())
            attributes.add(PhoneticAttribute.LastLetterVowel);
        else
            attributes.add(PhoneticAttribute.LastLetterConsonant);

        if (lastLetter.isVoiceless()) {
            attributes.add(PhoneticAttribute.LastLetterVoiceless);
            if (lastLetter.isStopConsonant() && !lastLetter.isVowel())
                attributes.add(PhoneticAttribute.LastLetterVoicelessStop);
        } else {
            attributes.add(PhoneticAttribute.LastLetterNotVoiceless);
        }

        return attributes;
    }

    public ImmutableSet<PhoneticAttribute> calculateNewPhoneticAttributes(Set<PhoneticAttribute> phoneticAttributes, char charToApply) {
        final TurkishChar turkishChar = TurkishAlphabet.getChar(charToApply);
        final TurkicLetter letter = turkishChar.getLetter();

        final EnumSet<PhoneticAttribute> newAttributes = EnumSet.copyOf(phoneticAttributes);
        if (letter.isVowel()) {
            newAttributes.remove(PhoneticAttribute.LastLetterConsonant);
            newAttributes.remove(PhoneticAttribute.LastLetterVoiceless);
            newAttributes.remove(PhoneticAttribute.LastLetterVoicelessStop);
            newAttributes.remove(PhoneticAttribute.HasNoVowel);

            newAttributes.remove(PhoneticAttribute.LastVowelFrontal);
            newAttributes.remove(PhoneticAttribute.LastVowelBack);
            newAttributes.remove(PhoneticAttribute.LastVowelRounded);
            newAttributes.remove(PhoneticAttribute.LastVowelUnrounded);

            newAttributes.add(PhoneticAttribute.LastLetterVowel);
            newAttributes.add(PhoneticAttribute.LastLetterNotVoiceless);

            if (letter.isFrontal())
                newAttributes.add(PhoneticAttribute.LastVowelFrontal);
            else
                newAttributes.add(PhoneticAttribute.LastVowelBack);

            if (letter.isRounded())
                newAttributes.add(PhoneticAttribute.LastVowelRounded);
            else
                newAttributes.add(PhoneticAttribute.LastVowelUnrounded);
        } else {
            newAttributes.remove(PhoneticAttribute.LastLetterVowel);
            newAttributes.remove(PhoneticAttribute.LastLetterVoiceless);
            newAttributes.remove(PhoneticAttribute.LastLetterNotVoiceless);
            newAttributes.remove(PhoneticAttribute.LastLetterVoicelessStop);

            newAttributes.add(PhoneticAttribute.LastLetterConsonant);
            if (letter.isVoiceless()) {
                newAttributes.add(PhoneticAttribute.LastLetterVoiceless);

                if (!letter.isContinuant())
                    newAttributes.add(PhoneticAttribute.LastLetterVoicelessStop);
            } else {
                newAttributes.add(PhoneticAttribute.LastLetterNotVoiceless);
            }
        }

        return Sets.immutableEnumSet(newAttributes);
    }

    public ImmutableSet<PhoneticAttribute> calculateNewPhoneticAttributes(ImmutableSet<PhoneticAttribute> phoneticAttributes, String suffixForm) {
        if (StringUtils.isBlank(suffixForm))
            return phoneticAttributes;

        ImmutableSet<PhoneticAttribute> currentPhoneticAttributes = phoneticAttributes;
        for (char aChar : suffixForm.toCharArray()) {
            currentPhoneticAttributes = this.calculateNewPhoneticAttributes(currentPhoneticAttributes, aChar);
        }

        return currentPhoneticAttributes;
    }
}