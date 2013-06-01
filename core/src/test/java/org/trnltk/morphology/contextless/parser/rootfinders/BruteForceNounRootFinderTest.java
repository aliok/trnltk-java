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

package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.trnltk.morphology.model.DynamicRoot;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class BruteForceNounRootFinderTest extends BaseRootFinderTest<DynamicRoot> {


    @Override
    protected RootFinder createRootFinder() {
        return new BruteForceNounRootFinder();
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
    public void should_create_no_roots() {
        assertThat(findRootsForPartialInput("b", "be"), hasSize(0));
        assertThat(findRootsForPartialInput("b", "ben"), hasSize(0));
    }

    @Test
    public void should_create_roots_without_orthographic_changes() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("a", "a");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("a"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("a"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("a"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("b", "b");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("b"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("b"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("b"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ab", "ab");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ba", "ba");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ba"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ba"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ba"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("atağ", "atağ");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("atağ"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("atağ"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("atağ"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("abc", "abc");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("abc"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("abc"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("abc"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("abc", "abcdef");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("abc"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("abc"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("abc"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
        }

    }

    @Test
    public void should_create_roots_with_voicing() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ab", "aba");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ab"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("ap"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ap"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ad", "adımı");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ad"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ad"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ad"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ad"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("at"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("at"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

            // skipped the case where nK voices to nG as in cenk->cengi
            //}{final List<DynamicRoot> roots = findRootsForPartialInput("ang", "anga");
            //assertThat(roots, hasSize(2));
            //assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ang"));
            //assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ang"));
            //assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ang"));
            //assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            //assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ank"));
            //assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("ank"));
            //assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ank"));
            //assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ağ", "ağa");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ağ"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ağ"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ağ"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ağ"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("ag"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ag"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("ağ"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("ak"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("ak"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ac", "acımdan");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ac"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ac"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ac"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ac"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("aç"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("aç"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
        }
    }

    @Test
    public void should_create_roots_with_explicit_no_voicing() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ap", "apa");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ap"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ap"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ap"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("at", "atana");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("at"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("at"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("at"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ak", "aka");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("aç", "açarak");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("aç"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("aç"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("aç"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
        }
    }

    @Test
    public void should_create_roots_with_inverse_harmony_when_vowel_is_next_letter() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ab", "abe");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ab"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("ap"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ap"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("hal", "halimden");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("oy", "oyümü");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yup", "yupö");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony, LexemeAttribute.NoVoicing)));
        }
    }

    @Test
    public void should_create_roots_with_inverse_harmony_when_vowel_is_the_letter_after_next_letter() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ab", "abdeki");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("hal", "haldik");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("oy", "oypü");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yup", "yupsö");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));
        }
    }

    @Test
    public void should_create_roots_with_inverse_harmony_when_vowel_is_the_letter_two_after_next_letter() {
        //// the ones below doesn't make sense, since no suffix can have the form
        //// Consonant+Consontant+Vowel applied when the root ends with a vowel.
        //// supported just in case that there is such a form I can't think of

        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ab", "abrzeklm");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ab"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("hal", "haltdi");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("hal"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("oy", "oykpüxyz");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("oy"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yup", "yupfsö");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yup"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));
        }
    }

    @Test
    public void should_create_roots_with_inverse_harmony_and_explicit_no_voicing() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ap", "ape");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ap"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ap"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ap"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony, LexemeAttribute.NoVoicing)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yot", "yotüne");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yot"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yot"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yot"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony, LexemeAttribute.NoVoicing)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ak", "akimi");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony, LexemeAttribute.NoVoicing)));

        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("kuç", "kuçö");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("kuç"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("kuç"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("kuç"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony, LexemeAttribute.NoVoicing)));
        }

    }

    @Test
    public void should_create_roots_with_doubling() {
        // simple doubling
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("hiss", "hissi");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("hiss"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("hiss"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("hiss"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) new HashSet<LexemeAttribute>()));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("hiss"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("his"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("his"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Doubling)));

            // doubling with Voicing and NoVoicing
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("tıbb", "tıbbın");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("tıbb"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("tıbb"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("tıbb"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) new HashSet<LexemeAttribute>()));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("tıbb"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("tıb"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("tıb"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Doubling)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("tıbb"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("tıp"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("tıp"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Doubling)));

            // doubling with NoVoicing
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("hakk", "hakka");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("hakk"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("hakk"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("hakk"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("hakk"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("hak"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("hak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Doubling)));

            // doubling with no {Voicing and NoVoicing} and InverseHarmony
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("hall", "hallini");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("hall"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("hall"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("hall"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("hall"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("hal"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("hal"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Doubling, LexemeAttribute.InverseHarmony)));

            // doubling with {Voicing and NoVoicing} and {InverseHarmony}
            // ignore the case "serhadt"
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("serhadd", "serhaddime");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("serhadd"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("serhadd"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("serhadd"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.InverseHarmony)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("serhadd"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("serhad"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("serhad"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Doubling, LexemeAttribute.InverseHarmony)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("serhadd"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("serhat"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("serhat"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Noun));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Doubling, LexemeAttribute.InverseHarmony)));
        }
    }

}
