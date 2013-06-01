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
import zemberek3.shared.lexicon.PrimaryPos;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class CardinalDigitsRootFinderTest extends BaseRootFinderTest<Root> {
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    protected RootFinder createRootFinder() {
        return new CardinalDigitsRootFinder();
    }

    @Test
    public void testShouldRecognizeSimpleNumbersWithoutSuffixes() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("", "13");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3", "13");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("13", "13");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("13"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("on üç"));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("0", "0");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("0"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("sıfır"));
        }

    }

    @Test
    public void testShouldRecognizeSimpleNumbersWithSuffixes() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1", "13'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("13", "13'e");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("13"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("on üç"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("13'", "13'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("13'e", "13'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }

    }

    @Test
    public void testShouldRecognizeNumbersWithSignWithoutSuffixes() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-", "-1");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-1", "-1");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("-1"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("eksi bir"));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+", "+3");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+3", "+3");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("+3"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("üç"));
        }
    }

    @Test
    public void testShouldRecognizeNumbersWithSignWithSuffixes() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-", "-1'i");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-1", "-1'i");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("-1"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("eksi bir"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-1'", "-1'i");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-1'i", "-1'i");
            assertThat(rootsForPartialInput, hasSize(0));
        }


        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+", "+3'ü");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+3", "+3'ü");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("+3"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("üç"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+3'", "+3'ü");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+3'ü", "+3'ü");
            assertThat(rootsForPartialInput, hasSize(0));
        }
    }


    @Test
    public void testShouldRecognizeNumbersWithDotOrCommaWithoutSuffixes() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3", "3,5");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3,", "3,5");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3,5", "3,5");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("3,5"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("üç virgül beş"));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,1", "-99,101");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,10", "-99,101");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,101", "-99,101");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("-99,101"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("eksi doksan dokuz virgül yüz bir"));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,1", "-99,101");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,10", "-99,101");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,101", "-99,101");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("-99,101"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("eksi doksan dokuz virgül yüz bir"));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.", "+99.000,12");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.000,", "+99.000,12");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.000,12", "+99.000,12");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("+99.000,12"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("doksan dokuz bin virgül on iki"));
        }
    }

    @Test
    public void testShouldRecognizeNumbersWithDotOrCommaWithSuffixes() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3", "3,5'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3,", "3,5'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3,5", "3,5'e");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("3,5"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("üç virgül beş"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3,5'", "3,5'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3,5'e", "3,5'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }

        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,1", "-99,101'i");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,10", "-99,101'i");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,101", "-99,101'i");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("-99,101"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("eksi doksan dokuz virgül yüz bir"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("-99,101'", "-99,101'i");
            assertThat(rootsForPartialInput, hasSize(0));
        }


        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.", "+99.000,12'si");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.000,", "+99.000,12'si");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.000,12", "+99.000,12'si");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("+99.000,12"));
            assertThat(rootsForPartialInput.get(0), hasCardinalDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("doksan dokuz bin virgül on iki"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.000,12'", "+99.000,12'si");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("+99.000,12'si", "+99.000,12'si");
            assertThat(rootsForPartialInput, hasSize(0));
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

    private Matcher<? super Root> hasCardinalDigitLexeme() {
        return new BaseMatcher<Root>() {
            @Override
            public boolean matches(Object item) {
                NumeralRoot root = (NumeralRoot) item;
                return PrimaryPos.Numeral.equals(root.getLexeme().getPrimaryPos()) &&
                        SecondaryPos.CARDINAL_DIGITS.equals(root.getLexeme().getSecondaryPos());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A numeral root with PrimaryPos Numeral and SecondaryPos DigitsC");
            }
        };
    }

}
