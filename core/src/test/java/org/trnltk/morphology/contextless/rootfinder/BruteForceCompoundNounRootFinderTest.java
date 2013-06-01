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
import org.junit.Test;
import org.trnltk.model.lexicon.DynamicRoot;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.PrimaryPos;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class BruteForceCompoundNounRootFinderTest extends BaseRootFinderTest<DynamicRoot> {

    @Override
    protected RootFinder createRootFinder() {
        return new BruteForceCompoundNounRootFinder();
    }

    @Test
    public void shouldFindNoRootsOnInvalidCases() {
        assertThat(findRootsForPartialInput(null, null), hasSize(0));
        assertThat(findRootsForPartialInput("", null), hasSize(0));
        assertThat(findRootsForPartialInput(null, ""), hasSize(0));
        assertThat(findRootsForPartialInput("", ""), hasSize(0));
        assertThat(findRootsForPartialInput("a", null), hasSize(0));
        assertThat(findRootsForPartialInput("a", ""), hasSize(0));
        assertThat(findRootsForPartialInput("ab", "a"), hasSize(0));
        assertThat(findRootsForPartialInput("ab", "ad"), hasSize(0));
        assertThat(findRootsForPartialInput("ab", "ada"), hasSize(0));
    }

    @Test
    public void shouldFindNoRootsEvenIfWholeSurfaceStartsWithPartialInput() {
        assertThat(findRootsForPartialInput("abc", "abcdef"), hasSize(0));
        assertThat(findRootsForPartialInput("a", "anu"), hasSize(0));
        assertThat(findRootsForPartialInput("an", "anu"), hasSize(0));
        assertThat(findRootsForPartialInput("anu", "anu"), hasSize(0));
        assertThat(findRootsForPartialInput("a", "anun"), hasSize(0));
        assertThat(findRootsForPartialInput("an", "anun"), hasSize(0));
        assertThat(findRootsForPartialInput("anu", "anun"), hasSize(0));
        assertThat(findRootsForPartialInput("anun", "anun"), hasSize(0));
        assertThat(findRootsForPartialInput("t", "tatın"), hasSize(0));
        assertThat(findRootsForPartialInput("ta", "tatın"), hasSize(0));
        assertThat(findRootsForPartialInput("tat", "tatın"), hasSize(0));
        assertThat(findRootsForPartialInput("tatı", "tatın"), hasSize(0));
        assertThat(findRootsForPartialInput("tatın", "tatın"), hasSize(0));
        assertThat(findRootsForPartialInput("suborusu", "suborusun"), hasSize(0));
    }

    @Test
    public void should_create_roots_without_consontant_insertion_s() {
        // most of the following words are made up!

        // no orthographic changes, no consontant insertion 's'
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("bacakkalemi", "bacakkalemini");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("bacakkalem"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("bacakkalemi"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("bacakkalemi"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg)));

            // with explicit NoVoicing
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("adamotu", "adamotunu");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("adamot"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("adamotu"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("adamotu"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.NoVoicing)));

            // with possible voicing
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("aslankuyruğu", "aslankuyruğundan");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("aslankuyruğ"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("aslankuyruğu"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("aslankuyruğu"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("aslankuyrug"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("aslankuyruğu"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("aslankuyruğu"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("aslankuyruk"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("aslankuyruğu"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("aslankuyruğu"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg)));

            // with InverseHarmony
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("dünyahali", "dünyahaline");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("dünyahal"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("dünyahali"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("dünyahali"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony)));

            // with InverseHarmony and possible voicing
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("abcvaadi", "abcvaadini");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("abcvaad"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("abcvaadi"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("abcvaadi"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("abcvaat"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("abcvaadi"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("abcvaadi"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony)));

            // with InverseHarmony and explicit NoVoicing
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("anaşefkati", "anaşefkatini");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("anaşefkat"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("anaşefkati"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("anaşefkati"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony, LexemeAttribute.NoVoicing)));

            // with doubling
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("gönülsırrı", "gönülsırrına");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("gönülsırr"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("gönülsırrı"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("gönülsırrı"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("gönülsır"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("gönülsırrı"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("gönülsırrı"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.Doubling)));

            // with doubling and explicit NoVoicing
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("müşterihakkı", "müşterihakkına");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("müşterihakk"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("müşterihakkı"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("müşterihakkı"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("müşterihak"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("müşterihakkı"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("müşterihakkı"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.NoVoicing, LexemeAttribute.Doubling)));

            // with doubling and InverseHarmony
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("olaymahalli", "olaymahalline");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("olaymahall"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("olaymahalli"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("olaymahalli"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("olaymahal"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("olaymahalli"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("olaymahalli"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony, LexemeAttribute.Doubling)));

            // with doubling, possible voicing and inverse harmony
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yaşhaddi", "yaşhaddinden");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yaşhadd"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yaşhaddi"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yaşhaddi"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("yaşhad"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("yaşhaddi"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("yaşhaddi"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony, LexemeAttribute.Doubling)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("yaşhat"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("yaşhaddi"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("yaşhaddi"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(2).getLexeme().getAttributes(),
                    equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony, LexemeAttribute.Doubling)));
        }
    }

    @Test
    public void should_create_roots_with_consontant_insertion_s() {
        // most of the following words are made up!
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("suborusu", "suborusuna");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("suborus"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("suborusu"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("suborusu"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("suboru"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("suborusu"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("suborusu"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg)));
        }
        // InverseHarmony and consonant 's' doesn't work together.
        // Compound gets the 's' if it ends with a vowel.
        // However, a word ending with a vowel cannot have InverseHarmony.
        // Thus, this is an invalid case!
        //}{final List<DynamicRoot> roots = findRootsForPartialInput("abcdesı", "abcdesına");
        //assertThat(roots, hasSize(2));
        //assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("abcdes"));
        //assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("abcdesı"));
        //assertThat(roots.get(0).getLexeme().getLemma(), equalTo("abcdesı"));
        //assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
        //assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony)));
        //assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("abcde"));
        //assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("abcdesı"));
        //assertThat(roots.get(1).getLexeme().getLemma(), equalTo("abcdesı"));
        //assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
        //assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.InverseHarmony}))
        //}
    }

}
