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
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.NumeralRoot;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class NumeralRootFinderTest {
    NumeralRootFinder finder;
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        this.finder = new NumeralRootFinder();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void testShouldRecognizeSimpleInteger() {
        assertRecognizedCorrectly("3", "üç");
        assertRecognizedCorrectly("0", "sıfır");
        assertRecognizedCorrectly("-1", "eksi bir");
        assertRecognizedCorrectly("+3", "üç");
        assertRecognizedCorrectly("3,5", "üç virgül beş");
        assertRecognizedCorrectly("-99,101", "eksi doksan dokuz virgül yüz bir");
        assertRecognizedCorrectly("+99.000,12", "doksan dokuz bin virgül on iki");
    }

    private void assertRecognizedCorrectly(final String digits, final String digitsWords) {
        final Collection<Root> rootsForPartialInput = finder.findRootsForPartialInput(new TurkishSequence(digits), null);
        assertThat(rootsForPartialInput, hasSize(1));
        assertThat(rootsForPartialInput.iterator().next(), Matchers.<Root>equalTo(new NumeralRoot(new TurkishSequence(digits), digitsWords, SecondaryPos.DIGITS,
                ImmutableSet.copyOf(phoneticsAnalyzer.calculatePhoneticAttributes(digitsWords, null)))));
    }
}
