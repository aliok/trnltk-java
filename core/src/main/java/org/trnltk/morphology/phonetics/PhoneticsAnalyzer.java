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
import zemberek3.lexicon.tr.PhonAttr;
import zemberek3.structure.TurkicLetter;

import java.util.Collection;
import java.util.EnumSet;

public class PhoneticsAnalyzer {

    public EnumSet<PhonAttr> calculatePhoneticAttributes(final String surface, Collection<LexemeAttribute> lexemeAttributes) {
        return this.calculatePhoneticAttributes(new TurkishSequence(surface), lexemeAttributes);
    }

    public EnumSet<PhonAttr> calculatePhoneticAttributes(final TurkishSequence surface, Collection<LexemeAttribute> lexemeAttributes) {
        final EnumSet<PhonAttr> phonAttrs = this.calculatePhoneticAttributesOfPlainSequence(surface);
        if (CollectionUtils.isEmpty(lexemeAttributes))
            return phonAttrs;

        if (lexemeAttributes.contains(LexemeAttribute.InverseHarmony)) {
            if (phonAttrs.contains(PhonAttr.LastVowelBack)) {
                phonAttrs.remove(PhonAttr.LastVowelBack);
                phonAttrs.add(PhonAttr.LastVowelFrontal);
            } else if (phonAttrs.contains(PhonAttr.LastVowelFrontal)) {
                phonAttrs.remove(PhonAttr.LastVowelFrontal);
                phonAttrs.add(PhonAttr.LastVowelBack);
            }
        }

        return phonAttrs;
    }

    EnumSet<PhonAttr> calculatePhoneticAttributesOfPlainSequence(final TurkishSequence surface) {
        final EnumSet<PhonAttr> attributes = EnumSet.noneOf(PhonAttr.class);
        final TurkishChar lastVowelChar = surface.getLastVowel();
        final TurkishChar lastChar = surface.getLastChar();
        final TurkicLetter lastLetter = lastChar.getLetter();

        if (lastVowelChar != null) {
            final TurkicLetter lastVowelLetter = lastVowelChar.getLetter();
            if (lastVowelLetter.isRounded())
                attributes.add(PhonAttr.LastVowelRounded);
            else
                attributes.add(PhonAttr.LastVowelUnrounded);

            if (lastVowelLetter.isFrontal())
                attributes.add(PhonAttr.LastVowelFrontal);
            else
                attributes.add(PhonAttr.LastVowelBack);
        }

        if (lastLetter.isVowel())
            attributes.add(PhonAttr.LastLetterVowel);
        else
            attributes.add(PhonAttr.LastLetterConsonant);

        if (lastLetter.isVoiceless()) {
            attributes.add(PhonAttr.LastLetterVoiceless);
            if (lastLetter.isStopConsonant() && !lastLetter.isVowel())
                attributes.add(PhonAttr.LastLetterVoicelessStop);
        } else {
            attributes.add(PhonAttr.LastLetterNotVoiceless);
        }

        return attributes;
    }
}