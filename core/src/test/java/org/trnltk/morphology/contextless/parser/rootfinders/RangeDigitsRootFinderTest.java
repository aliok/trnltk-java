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

public class RangeDigitsRootFinderTest extends BaseRootFinderTest<Root> {
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    protected RootFinder createRootFinder() {
        return new RangeDigitsRootFinder();
    }

    @Test
    public void shouldNotRecognizeInvalidCases() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3", "3-41");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3-", "3-41");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3-4", "3-41");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3-41", "3-412");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3-a", "3-a");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("3-12.", "3-12.");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("2-3'", "2-3'e");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("a-2", "a-2");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1-2-", "1-2-3");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1-2-3", "1-2-34");
            assertThat(rootsForPartialInput, hasSize(0));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1-2-3-", "1-2-3-4");
            assertThat(rootsForPartialInput, hasSize(0));
        }
    }

    @Test
    public void shouldParseTwoPartRanges() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1-2", "1-2");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1-2"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bir iki"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1.000-10.000", "1.000-10.000");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1.000-10.000"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bin on bin"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1000-100.000", "1000-100.000");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1000-100.000"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bin yüz bin"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("2-3", "2-3'e");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("2-3"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("iki üç"));
        }
    }

    @Test
    public void shouldRangesWithSuffixes() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1-2", "1-2.");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1-2"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bir iki"));
        }
        {
            // following is stated invalid by TDK
            // hard to support, so ignored
//            final List<Root> rootsForPartialInput = findRootsForPartialInput("1.-2.", "1.-2.");
//            assertThat(rootsForPartialInput, hasSize(1));
//            assertThat(rootsForPartialInput.get(0), hasRootStr("1.-2."));
//            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
//            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bir iki"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1.000-10.000", "1.000-10.000.");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1.000-10.000"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bin on bin"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1000-100.000", "1000-100.000'de");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1000-100.000"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bin yüz bin"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("2-3", "2-3.'de");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("2-3"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("iki üç"));
        }
    }

    @Test
    public void shouldParseMultiPartRanges() {
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1-2-3", "1-2-3");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1-2-3"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bir iki üç"));
        }
        {
            final List<Root> rootsForPartialInput = findRootsForPartialInput("1-2-3-4-5", "1-2-3-4-5");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0), hasRootStr("1-2-3-4-5"));
            assertThat(rootsForPartialInput.get(0), hasRangeDigitLexeme());
            assertThat(rootsForPartialInput.get(0), hasUnderlyingNumeralText("bir iki üç dört beş"));
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

    private Matcher<? super Root> hasRangeDigitLexeme() {
        return new BaseMatcher<Root>() {
            @Override
            public boolean matches(Object item) {
                NumeralRoot root = (NumeralRoot) item;
                return PrimaryPos.Numeral.equals(root.getLexeme().getPrimaryPos()) &&
                        SecondaryPos.Range.equals(root.getLexeme().getSecondaryPos());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A numeral root with PrimaryPos Numeral and SecondaryPos Range");
            }
        };
    }

}
