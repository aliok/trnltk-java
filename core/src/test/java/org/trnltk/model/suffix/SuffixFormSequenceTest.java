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

package org.trnltk.model.suffix;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SuffixFormSequenceTest {

    @Test
    public void shouldCreateSimpleRules() {
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("n");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('n'));
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("a");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('a'));
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("and");
            assertThat(sequence.getRules(), hasSize(3));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('a'));
            assertThat(sequence.getRules().get(1).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER));
            assertThat(sequence.getRules().get(1).getCharToAdd().getCharValue(), equalTo('n'));
            assertThat(sequence.getRules().get(2).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER));
            assertThat(sequence.getRules().get(2).getCharToAdd().getCharValue(), equalTo('d'));
        }
    }

    @Test
    public void shouldCreateInsertVowelWithHarmonyRules() {
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("A");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY));
            assertThat(sequence.getRules().get(0).getCharToAdd(), nullValue());
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("aI");
            assertThat(sequence.getRules(), hasSize(2));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('a'));
            assertThat(sequence.getRules().get(1).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY));
            assertThat(sequence.getRules().get(1).getCharToAdd(), nullValue());
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("abA");
            assertThat(sequence.getRules(), hasSize(3));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('a'));
            assertThat(sequence.getRules().get(1).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER));
            assertThat(sequence.getRules().get(1).getCharToAdd().getCharValue(), equalTo('b'));
            assertThat(sequence.getRules().get(2).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY));
            assertThat(sequence.getRules().get(2).getCharToAdd(), nullValue());
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("l!I");
            assertThat(sequence.getRules(), hasSize(2));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('l'));
            assertThat(sequence.getRules().get(1).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY_AND_NO_ROUNDING));
            assertThat(sequence.getRules().get(1).getCharToAdd(), nullValue());
        }
    }

    @Test
    public void shouldCreateInsertOptionalLetterRules() {
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("+y");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_CONSONANT));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('y'));
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("+a");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('a'));
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("+A");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_A_WITH_HARMONY));
            assertThat(sequence.getRules().get(0).getCharToAdd(), nullValue());
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("+Im");
            assertThat(sequence.getRules(), hasSize(2));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_I_WITH_HARMONY));
            assertThat(sequence.getRules().get(0).getCharToAdd(), nullValue());
            assertThat(sequence.getRules().get(1).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER));
            assertThat(sequence.getRules().get(1).getCharToAdd().getCharValue(), equalTo('m'));
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("+uA");
            assertThat(sequence.getRules(), hasSize(2));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('u'));
            assertThat(sequence.getRules().get(1).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY));
            assertThat(sequence.getRules().get(1).getCharToAdd(), nullValue());
        }
    }

    @Test
    public void shouldCreateDevoicableLetterRules() {
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("b");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_DEVOICABLE_LETTER));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('b'));
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("c");
            assertThat(sequence.getRules(), hasSize(1));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_DEVOICABLE_LETTER));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('c'));
        }
        {
            final SuffixFormSequence sequence = new SuffixFormSequence("+ydI");
            assertThat(sequence.getRules(), hasSize(3));
            assertThat(sequence.getRules().get(0).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_CONSONANT));
            assertThat(sequence.getRules().get(0).getCharToAdd().getCharValue(), equalTo('y'));
            assertThat(sequence.getRules().get(1).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_DEVOICABLE_LETTER));
            assertThat(sequence.getRules().get(1).getCharToAdd().getCharValue(), equalTo('d'));
            assertThat(sequence.getRules().get(2).getRuleType(), equalTo(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY));
            assertThat(sequence.getRules().get(2).getCharToAdd(), nullValue());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenOptionalLetterIsNotFirst() {
        new SuffixFormSequence("a+");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNoRoundingIsAddedButLetterIsNot_I() {
        new SuffixFormSequence("!A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenHarmonyLetterAddedButItIsNotAllowed_sc1() {
        new SuffixFormSequence("U");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenHarmonyLetterAddedButItIsNotAllowed_sc2() {
        new SuffixFormSequence("O");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenHarmonyLetterAddedButItIsNotAllowed_sc3() {
        new SuffixFormSequence("E");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenHarmonyLetterAddedButItIsNotAllowed_sc4() {
        new SuffixFormSequence("K");
    }
}
