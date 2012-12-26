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

public class ProperNounFromApostropheRootFinder implements RootFinder {
    private static final char APOSTROPHE = '\'';
    private static final TurkishChar TURKISH_CHAR_E_UPPERCASE = TurkishAlphabet.getChar('E');

    private final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    @Override
    public List<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input) {
        if (partialInput == null || partialInput.isBlank())
            return Arrays.asList();

        if (partialInput.getLastChar().getCharValue() == APOSTROPHE) {
            final TurkishSequence properNounCandidate = partialInput.subsequence(0, partialInput.length() - 1);
            if (!properNounCandidate.isBlank()) {

                final String properNounCandidateUnderlyingString = properNounCandidate.getUnderlyingString();

                if (StringUtils.isAllUpperCase(properNounCandidateUnderlyingString)) {
                    final Lexeme lexeme = new Lexeme(properNounCandidateUnderlyingString, properNounCandidateUnderlyingString, SyntacticCategory.NOUN, SecondarySyntacticCategory.ABBREVIATION, null);

                    if (!properNounCandidate.getLastChar().getLetter().isVowel()) {
                        // if last letter is not vowel (such as PTT, THY), then add char 'E' to the end and then calculate the phonetics
                        final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(properNounCandidate.append(TURKISH_CHAR_E_UPPERCASE), null));
                        return Arrays.asList(new ImmutableRoot(properNounCandidate, lexeme, phoneticAttributes, null));

                    } else {
                        final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(properNounCandidate, null));
                        return Arrays.asList(new ImmutableRoot(properNounCandidate, lexeme, phoneticAttributes, null));
                    }
                } else if (Character.isUpperCase(properNounCandidate.charAt(0).getCharValue())) {
                    final Lexeme lexeme = new Lexeme(properNounCandidateUnderlyingString, properNounCandidateUnderlyingString, SyntacticCategory.NOUN, SecondarySyntacticCategory.PROPER_NOUN, null);

                    final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(properNounCandidate, null));
                    return Arrays.asList(new ImmutableRoot(properNounCandidate, lexeme, phoneticAttributes, null));
                }
            }
        }

        //noinspection unchecked
        return Collections.EMPTY_LIST;
    }
}
