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

import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondarySyntacticCategory;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class ProperNounWithoutApostropheRootFinderTest {
    ProperNounWithoutApostropheRootFinder rootFinder;
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        this.rootFinder = new ProperNounWithoutApostropheRootFinder();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void shouldRecognizeProperNouns() {
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("A"), new TurkishSequence("Ali"));
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("A"));
            assertThat(roots.get(0).getLexeme().getSecondarySyntacticCategory(), equalTo(SecondarySyntacticCategory.PROPER_NOUN));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("Al"), new TurkishSequence("Ali"));
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("Al"));
            assertThat(roots.get(0).getLexeme().getSecondarySyntacticCategory(), equalTo(SecondarySyntacticCategory.PROPER_NOUN));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("Ali"), new TurkishSequence("Ali"));
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("Ali"));
            assertThat(roots.get(0).getLexeme().getSecondarySyntacticCategory(), equalTo(SecondarySyntacticCategory.PROPER_NOUN));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("Ali8"), new TurkishSequence("Ali8192"));
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("Ali8"));
            assertThat(roots.get(0).getLexeme().getSecondarySyntacticCategory(), equalTo(SecondarySyntacticCategory.PROPER_NOUN));
        }
    }

    @Test
    public void shouldNotRecognizeProperNouns_whenInputHasApostrophe() {
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("A"), new TurkishSequence("Ali'ye"));
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("Al"), new TurkishSequence("Ali'ye"));
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("Ali"), new TurkishSequence("Ali'ye"));
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("Ali'"), new TurkishSequence("Ali'ye"));
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("Ali'y"), new TurkishSequence("Ali'ye"));
            assertThat(roots, hasSize(0));
        }
    }

    @Test
    public void shouldNotRecognizeProperNouns_whenInputDoesntStartWithUppercase() {
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("a"), new TurkishSequence("ali"));
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("al"), new TurkishSequence("ali"));
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("ali"), new TurkishSequence("ali"));
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = rootFinder.findRootsForPartialInput(new TurkishSequence("123A"), new TurkishSequence("123A"));
            assertThat(roots, hasSize(0));
        }
    }

}
