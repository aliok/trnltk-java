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

package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.trnltk.morphology.contextless.parser.RootFinder;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.phonetics.PhoneticAttribute;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.TurkishAlphabet;
import org.trnltk.morphology.phonetics.TurkishChar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProperNounWithoutApostropheRootFinder implements RootFinder {
    private static final char APOSTROPHE = '\'';
    private static final TurkishChar TURKISH_CHAR_E_UPPERCASE = TurkishAlphabet.getChar('E');

    private final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    @Override
    public List<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input) {
        if (partialInput == null || partialInput.isBlank())
            //noinspection unchecked
            return Collections.EMPTY_LIST;

        // the case with apostrophe is handled by ProperNounFromApostropheRootFinder
        final String inputUnderlyingString = input.getUnderlyingString();
        if (!Character.isUpperCase(input.charAt(0).getCharValue()) || inputUnderlyingString.contains(String.valueOf(APOSTROPHE))) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }

        final String partialInputUnderlyingString = partialInput.getUnderlyingString();

        if (partialInput.equals(input) && StringUtils.isAllUpperCase(partialInputUnderlyingString)) {
            final Lexeme abbreviationLexeme = new Lexeme(partialInputUnderlyingString, partialInputUnderlyingString, SyntacticCategory.NOUN, SecondarySyntacticCategory.ABBREVIATION, null);
            if (!partialInput.getLastChar().getLetter().isVowel()) {
                // if last letter is not vowel (such as PTT, THY), then add char 'E' to the end and then calculate the phonetics
                final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(partialInput.append(TURKISH_CHAR_E_UPPERCASE), null));
                return Arrays.asList(new ImmutableRoot(partialInput, abbreviationLexeme, phoneticAttributes, null));

            } else {
                final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, null));
                return Arrays.asList(new ImmutableRoot(partialInput, abbreviationLexeme, phoneticAttributes, null));
            }
        }
        {
            ///XXX : REALLY SMALL SUPPORT!

            // XXX: might be a known proper noun like "Turkce" or "Istanbul". no support for them yet

            // XXX: might be a known proper noun with implicit P3sg. like : Eminonu, Kusadasi.
            // it is important since :
            // 1. Ankara'_y_a but Eminonu'_n_e    : Since this case has apostrophe, it is handled in ProperNounFromApostropheRootFinder
            // 2: P3sg doesn't apply to these words: onun Kusadasi, onun Eminonu
            // 3. Possessions are applied to 'root' : benim Kusadam etc. SKIP this case!

            final Lexeme properNounLexeme = new Lexeme(partialInputUnderlyingString, partialInputUnderlyingString, SyntacticCategory.NOUN, SecondarySyntacticCategory.PROPER_NOUN, null);

            final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, null));
            final ImmutableRoot properNounRoot = new ImmutableRoot(partialInput, properNounLexeme, phoneticAttributes, null);
            return Arrays.asList(properNounRoot);
        }
    }
}
