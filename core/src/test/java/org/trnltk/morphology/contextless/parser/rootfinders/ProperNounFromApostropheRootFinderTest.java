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
import zemberek3.lexicon.PrimaryPos;
import zemberek3.lexicon.tr.PhonAttr;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static zemberek3.lexicon.tr.PhonAttr.*;

public class ProperNounFromApostropheRootFinderTest {
    ProperNounFromApostropheRootFinder rootFinder;
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        this.rootFinder = new ProperNounFromApostropheRootFinder();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void shouldRecognizeAbbreviations() {
        assertRecognizedCorrectly("TR'", "TR", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("MB'", "MB", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("POL'", "POL", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
//        assertRecognizedCorrectly("KAFA1500'", "KAFA1500", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(Something));  //XXX
//        assertRecognizedCorrectly("1500KAFA'", "1500KAFA", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(Something));  //XXX
        assertRecognizedCorrectly("İŞÇĞÜÖ'", "İŞÇĞÜÖ", SecondaryPos.ABBREVIATION, Sets.immutableEnumSet(LastLetterVowel, LastVowelFrontal, LastVowelRounded, LastLetterNotVoiceless));
        assertThat(rootFinder.findRootsForPartialInput(new TurkishSequence("123'"), new TurkishSequence("123'e")), hasSize(0));
    }

    @Test
    public void shouldRecognizeProperNouns() {
        assertRecognizedCorrectly("Ahmet'", "Ahmet", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterConsonant, LastVowelFrontal, LastVowelUnrounded, LastLetterVoiceless, LastLetterVoicelessStop));
        assertRecognizedCorrectly("Mehmed'", "Mehmed", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterConsonant, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("A123a'", "A123a", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterVowel, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("AvA'", "AvA", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterVowel, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("AAxxAA'", "AAxxAA", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterVowel, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("İstanbul'", "İstanbul", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterConsonant, LastVowelBack, LastVowelRounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Çanakkale'", "Çanakkale", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Ömer'", "Ömer", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterConsonant, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Şaban'", "Şaban", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterConsonant, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
        assertRecognizedCorrectly("Ümmühan'", "Ümmühan", SecondaryPos.ProperNoun, Sets.immutableEnumSet(LastLetterConsonant, LastVowelBack, LastVowelUnrounded, LastLetterNotVoiceless));
    }

    @Test
    public void shouldNotRecognizeProperNounIfFirstCharIsNotUppercase() {
        assertThat(rootFinder.findRootsForPartialInput(new TurkishSequence("aaa'"), new TurkishSequence("aaa'e")), hasSize(0));
        assertThat(rootFinder.findRootsForPartialInput(new TurkishSequence("aAAAA'"), new TurkishSequence("aAAAA'e")), hasSize(0));
        assertThat(rootFinder.findRootsForPartialInput(new TurkishSequence("1aa'"), new TurkishSequence("1aa'e")), hasSize(0));
        assertThat(rootFinder.findRootsForPartialInput(new TurkishSequence("a111'"), new TurkishSequence("a111'e")), hasSize(0));
        assertThat(rootFinder.findRootsForPartialInput(new TurkishSequence("şaa'"), new TurkishSequence("şaa'e")), hasSize(0));
    }

    private void assertRecognizedCorrectly(final String partialInput, final String expectedLemmaAndRoot, final SecondaryPos expectedSecondaryPos,
                                           ImmutableSet<PhonAttr> expectedPhonAttrs) {

        final Lexeme expectedLemma = new Lexeme(expectedLemmaAndRoot, expectedLemmaAndRoot, PrimaryPos.Noun, expectedSecondaryPos, null);
        final ImmutableRoot expectedRoot = new ImmutableRoot(new TurkishSequence(expectedLemmaAndRoot), expectedLemma, expectedPhonAttrs, null);

        final List<? extends Root> rootsForPartialInput = rootFinder.findRootsForPartialInput(new TurkishSequence(partialInput), null);
        assertThat(rootsForPartialInput, hasSize(1));

        final Root retrievedRoot = rootsForPartialInput.get(0);
        assertThat(retrievedRoot, Matchers.<Root>equalTo(expectedRoot));
    }
}
