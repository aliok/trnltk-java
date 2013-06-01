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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.NumeralRoot;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class OrdinalDigitsRootFinderTest extends BaseRootFinderTest<Root> {
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    protected RootFinder createRootFinder() {
        return new OrdinalDigitsRootFinder();
    }

    @Test
    public void testShouldRecognizeOrdinals() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3.", "3.5");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3.", "3.a");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("12.22.", "12.22.");
            assertThat(rootsForPartialInput, hasSize(0));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1.123.", "1.123.456.");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-1,10.", "-1,10.");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,101.", "-99,101.");
            assertThat(rootsForPartialInput, hasSize(0));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,1.", "-99,1.01");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,10.", "-99,101.");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99.101.", "-99.101.");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("-99.101."));
            assertThat(rootsForPartialInput.get(0), hasOrdinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("eksi doksan dokuz bin yüz birinci"));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.", "+99.'ya");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("+99."));
            assertThat(rootsForPartialInput.get(0), hasOrdinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("doksan dokuzuncu"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.000,", "+99.000,12");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("444.", "444.'ün");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("444."));
            assertThat(rootsForPartialInput.get(0), hasOrdinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("dört yüz kırk dördüncü"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1113.", "1113.");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1113."));
            assertThat(rootsForPartialInput.get(0), hasOrdinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bin yüz on üçüncü"));
        }
    }

    private Matcher<? super Root> hasUnderlyingNumeralText(final String str) {
        return new BaseMatcher<Root>() {
            @Override
            public boolean matches(Object item) {
                NumeralRoot root = (NumeralRoot) item;
                return root.getUnderlyingNumeralText().equals(str) && root.getPhoneticAttributes().equals(phoneticsAnalyzer.calculatePhoneticAttributes(str, null));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A numeral root with underlyingNumeralText " + str + " and also the phonetic attributes of " + str);
            }
        };
    }

    private Matcher<? super Root> hasRootStr(final String rootStr) {
        return new BaseMatcher<Root>() {
            @Override
            public boolean matches(Object item) {
                NumeralRoot root = (NumeralRoot) item;
                return root.getSequence().getUnderlyingString().equals(rootStr) &&
                        root.getLexeme().getLemma().equals(rootStr) &&
                        root.getLexeme().getLemmaRoot().equals(rootStr);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A numeral root with rootStr " + rootStr);
            }
        };
    }

    private Matcher<? super Root> hasOrdinalDigitLexeme() {
        return new BaseMatcher<Root>() {
            @Override
            public boolean matches(Object item) {
                NumeralRoot root = (NumeralRoot) item;
                return PrimaryPos.Numeral.equals(root.getLexeme().getPrimaryPos()) &&
                        SecondaryPos.ORDINAL_DIGITS.equals(root.getLexeme().getSecondaryPos());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A numeral root with PrimaryPos Numeral and SecondaryPos DigitsO");
            }
        };
    }

}
