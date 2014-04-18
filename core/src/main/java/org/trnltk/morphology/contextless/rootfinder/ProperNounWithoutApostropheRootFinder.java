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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.trnltk.model.letter.TurkishAlphabet;
import org.trnltk.model.letter.TurkishChar;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.Arrays;
import java.util.List;

public class ProperNounWithoutApostropheRootFinder implements RootFinder {
    private static final char APOSTROPHE = '\'';
    private static final TurkishChar TURKISH_CHAR_E_UPPERCASE = TurkishAlphabet.getChar('E');

    private final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence wholeSurface) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (wholeSurface == null || wholeSurface.isBlank())
            return false;

        // the case with apostrophe is handled by ProperNounFromApostropheRootFinder
        final String wholeSurfaceUnderlyingString = wholeSurface.getUnderlyingString();
        //noinspection RedundantIfStatement
        if (!Character.isUpperCase(wholeSurface.charAt(0).getCharValue()) || wholeSurfaceUnderlyingString.contains(String.valueOf(APOSTROPHE))) {
            return false;
        }

        return true;
    }

    @Override
    public List<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input) {
        final String partialInputUnderlyingString = partialInput.getUnderlyingString();

        if (partialInput.equals(input) && StringUtils.isAllUpperCase(partialInputUnderlyingString)) {
            final Lexeme abbreviationLexeme = new ImmutableLexeme(partialInputUnderlyingString, partialInputUnderlyingString, PrimaryPos.Noun, SecondaryPos.Abbreviation, null);
            if (!partialInput.getLastChar().getLetter().isVowel()) {
                // if last letter is not vowel (such as PTT, THY), then add char 'E' to the end and then calculate the phonetics
                final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(partialInput.append(TURKISH_CHAR_E_UPPERCASE), null));
                return Arrays.asList(new ImmutableRoot(partialInput, abbreviationLexeme, phoneticAttributes, null));

            } else {
                final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, null));
                return Arrays.asList(new ImmutableRoot(partialInput, abbreviationLexeme, phoneticAttributes, null));
            }
        } else {
            ///XXX : REALLY SMALL SUPPORT!

            // XXX: might be a known proper noun like "Turkce" or "Istanbul". no support for them yet

            // XXX: might be a known proper noun with implicit P3sg. like : Eminonu, Kusadasi.
            // it is important since :
            // 1. Ankara'_y_a but Eminonu'_n_e    : Since this case has apostrophe, it is handled in ProperNounFromApostropheRootFinder
            // 2: P3sg doesn't apply to these words: onun Kusadasi, onun Eminonu
            // 3. Possessions are applied to 'root' : benim Kusadam etc. SKIP this case!

            final Lexeme properNounLexeme = new ImmutableLexeme(partialInputUnderlyingString, partialInputUnderlyingString, PrimaryPos.Noun, SecondaryPos.ProperNoun, null);

            final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, null));
            final ImmutableRoot properNounRoot = new ImmutableRoot(partialInput, properNounLexeme, phoneticAttributes, null);
            return Arrays.asList(properNounRoot);
        }
    }
}
