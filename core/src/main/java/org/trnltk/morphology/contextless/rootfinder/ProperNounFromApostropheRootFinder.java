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
import org.trnltk.model.letter.TurkishSequence;

import org.trnltk.model.lexicon.*;
import org.trnltk.model.letter.TurkishAlphabet;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.model.letter.TurkishChar;

import java.util.Arrays;
import java.util.List;

public class ProperNounFromApostropheRootFinder implements RootFinder {
    private static final char APOSTROPHE = '\'';
    private static final TurkishChar TURKISH_CHAR_E_UPPERCASE = TurkishAlphabet.getChar('E');

    private final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence input) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (partialInput.length() < 2)
            return false;

        if (Character.isUpperCase(partialInput.charAt(0).getCharValue()) && partialInput.getLastChar().getCharValue() == APOSTROPHE)
            return true;

        return false;
    }

    @Override
    public List<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input) {
        final TurkishSequence properNounCandidate = partialInput.subsequence(0, partialInput.length() - 1);

        final String properNounCandidateUnderlyingString = properNounCandidate.getUnderlyingString();

        if (StringUtils.isAllUpperCase(properNounCandidateUnderlyingString)) {
            final Lexeme lexeme = new ImmutableLexeme(properNounCandidateUnderlyingString, properNounCandidateUnderlyingString, PrimaryPos.Noun, SecondaryPos.Abbreviation, null);

            if (!properNounCandidate.getLastChar().getLetter().isVowel()) {
                // if last letter is not vowel (such as PTT, THY), then add char 'E' to the end and then calculate the phonetics
                final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(properNounCandidate.append(TURKISH_CHAR_E_UPPERCASE), null));
                return Arrays.asList(new ImmutableRoot(properNounCandidate, lexeme, phoneticAttributes, null));

            } else {
                final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(properNounCandidate, null));
                return Arrays.asList(new ImmutableRoot(properNounCandidate, lexeme, phoneticAttributes, null));
            }
        } else {
            final Lexeme lexeme = new ImmutableLexeme(properNounCandidateUnderlyingString, properNounCandidateUnderlyingString, PrimaryPos.Noun, SecondaryPos.ProperNoun, null);

            final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(properNounCandidate, null));
            return Arrays.asList(new ImmutableRoot(properNounCandidate, lexeme, phoneticAttributes, null));
        }
    }
}
