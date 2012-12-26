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

import org.apache.commons.collections.CollectionUtils;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.Collection;
import java.util.EnumSet;

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

        return phoneticAttributes;
    }

    EnumSet<PhoneticAttribute> calculatePhoneticAttributesOfPlainSequence(final TurkishSequence surface) {
        final EnumSet<PhoneticAttribute> attributes = EnumSet.noneOf(PhoneticAttribute.class);
        final TurkishChar lastVowelChar = surface.getLastVowel();
        final TurkishChar lastChar = surface.getLastChar();
        final TurkishLetter lastLetter = lastChar.getLetter();

        if (lastVowelChar != null) {
            final TurkishLetter lastVowelLetter = lastVowelChar.getLetter();
            if (lastVowelLetter.isRounded())
                attributes.add(PhoneticAttribute.LastVowelRounded);
            else
                attributes.add(PhoneticAttribute.LastVowelUnrounded);

            if (lastVowelLetter.isFrontal())
                attributes.add(PhoneticAttribute.LastVowelFrontal);
            else
                attributes.add(PhoneticAttribute.LastVowelBack);
        }

        if (lastLetter.isVowel())
            attributes.add(PhoneticAttribute.LastLetterVowel);
        else
            attributes.add(PhoneticAttribute.LastLetterConsonant);

        if (lastLetter.isVoiceless()) {
            attributes.add(PhoneticAttribute.LastLetterVoiceless);
            if (!lastLetter.isContinuant() && !lastLetter.isVowel())
                attributes.add(PhoneticAttribute.LastLetterVoicelessStop);
        } else {
            attributes.add(PhoneticAttribute.LastLetterNotVoiceless);
            if (!lastLetter.isContinuant() && !lastLetter.isVowel())
                attributes.add(PhoneticAttribute.LastLetterVoicedStop);
        }

        if (lastLetter.isContinuant())
            attributes.add(PhoneticAttribute.LastLetterContinuant);
        else
            attributes.add(PhoneticAttribute.LastLetterNotContinuant);

        return attributes;
    }
}