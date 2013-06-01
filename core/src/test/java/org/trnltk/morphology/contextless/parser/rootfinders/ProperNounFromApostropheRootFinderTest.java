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
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import zemberek3.shared.lexicon.PrimaryPos;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static zemberek3.shared.lexicon.tr.PhoneticAttribute.*;

public class ProperNounFromApostropheRootFinderTest extends BaseRootFinderTest<DynamicRoot> {
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    protected RootFinder createRootFinder() {
        return new ProperNounFromApostropheRootFinder();
    }

    @Test
    public void shouldRecognizeAbbreviations() {
        assertRecognizedCorrectly("TR'", "TR", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(FirstLetterConsonant, LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("MB'", "MB", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(FirstLetterConsonant, LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("POL'", "POL", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(FirstLetterConsonant, LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
//        assertRecognizedCorrectly("KAFA1500'", "KAFA1500", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(Something));  //XXX
//        assertRecognizedCorrectly("1500KAFA'", "1500KAFA", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(Something));  //XXX
        assertRecognizedCorrectly("İŞÇĞÜÖ'", "İŞÇĞÜÖ", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(FirstLetterVowel, LastLetterVowel, LastVowelFrontal, LastVowelRounded, LastLetterNotVoiceless));
        assertThat(findRootsForPartialInput("123'", "123'e"), hasSize(0));
    }

    @Test
    public void shouldRecognizeProperNouns() {
        assertRecognizedCorrectly("Ahmet'", "Ahmet", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterVowel, LastLetterConsonant, LastVowelFrontal, LastVowelUnrounded, LastLetterVoiceless, LastLetterVoicelessStop));
        assertRecognizedCorrectly("Mehmed'", "Mehmed", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterConsonant, LastLetterConsonant, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("A123a'", "A123a", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterVowel, LastLetterVowel, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("AvA'", "AvA", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterVowel, LastLetterVowel, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("AAxxAA'", "AAxxAA", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterVowel, LastLetterVowel, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("İstanbul'", "İstanbul", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterVowel, LastLetterConsonant, LastVowelBack, LastVowelRounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Çanakkale'", "Çanakkale", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterConsonant, LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Ömer'", "Ömer", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterVowel, LastLetterConsonant, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Şaban'", "Şaban", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterConsonant, LastLetterConsonant, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Ümmühan'", "Ümmühan", SecondaryPos.ProperNoun, Sets.immutableEnumSet(FirstLetterVowel, LastLetterConsonant, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
    }

    @Test
    public void shouldNotRecognizeProperNounIfFirstCharIsNotUppercase() {
        assertThat(findRootsForPartialInput("aaa'", "aaa'e"), hasSize(0));
        assertThat(findRootsForPartialInput("aAAAA'", "aAAAA'e"), hasSize(0));
        assertThat(findRootsForPartialInput("1aa'", "1aa'e"), hasSize(0));
        assertThat(findRootsForPartialInput("a111'", "a111'e"), hasSize(0));
        assertThat(findRootsForPartialInput("şaa'", "şaa'e"), hasSize(0));
    }

    private void assertRecognizedCorrectly(final String partialInput, final String expectedLemmaAndRoot, final SecondaryPos expectedSecondaryPos,
                                           ImmutableSet<PhoneticAttribute> expectedPhoneticAttributes) {

        final Lexeme expectedLemma = new ImmutableLexeme(expectedLemmaAndRoot, expectedLemmaAndRoot, PrimaryPos.Noun, expectedSecondaryPos, null);
        final ImmutableRoot expectedRoot = new ImmutableRoot(new TurkishSequence(expectedLemmaAndRoot), expectedLemma, expectedPhoneticAttributes, null);

        final List<? extends Root> rootsForPartialInput = findRootsForPartialInput(partialInput, "");
        assertThat(rootsForPartialInput, hasSize(1));

        final Root retrievedRoot = rootsForPartialInput.get(0);
        assertThat(retrievedRoot, Matchers.<Root>equalTo(expectedRoot));
    }
}
