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

import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.lexicon.DynamicRoot;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.lexicon.SecondaryPos;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class ProperNounWithoutApostropheRootFinderTest extends BaseRootFinderTest<DynamicRoot> {
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    protected RootFinder createRootFinder() {
        return new ProperNounWithoutApostropheRootFinder();
    }

    @Test
    public void shouldRecognizeProperNouns() {
        {
            final List<? extends Root> roots = findRootsForPartialInput("A", "Ali");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("A"));
            assertThat(roots.get(0).getLexeme().getSecondaryPos(), equalTo(SecondaryPos.ProperNoun));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("Al", "Ali");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("Al"));
            assertThat(roots.get(0).getLexeme().getSecondaryPos(), equalTo(SecondaryPos.ProperNoun));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("Ali", "Ali");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("Ali"));
            assertThat(roots.get(0).getLexeme().getSecondaryPos(), equalTo(SecondaryPos.ProperNoun));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("Ali8", "Ali8192");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("Ali8"));
            assertThat(roots.get(0).getLexeme().getSecondaryPos(), equalTo(SecondaryPos.ProperNoun));
        }
    }

    @Test
    public void shouldNotRecognizeProperNouns_whenInputHasApostrophe() {
        {
            final List<? extends Root> roots = findRootsForPartialInput("A", "Ali'ye");
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("Al", "Ali'ye");
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("Ali", "Ali'ye");
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("Ali'", "Ali'ye");
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("Ali'y", "Ali'ye");
            assertThat(roots, hasSize(0));
        }
    }

    @Test
    public void shouldNotRecognizeProperNouns_whenInputDoesntStartWithUppercase() {
        {
            final List<? extends Root> roots = findRootsForPartialInput("a", "ali");
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("al", "ali");
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("ali", "ali");
            assertThat(roots, hasSize(0));
        }
        {
            final List<? extends Root> roots = findRootsForPartialInput("123A", "123A");
            assertThat(roots, hasSize(0));
        }
    }

}
