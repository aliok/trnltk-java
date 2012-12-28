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

package org.trnltk.morphology.morphotactics;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.SuffixFormSequence;
import org.trnltk.morphology.model.SuffixFormSequenceRuleStub;
import zemberek3.lexicon.tr.PhonAttr;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class SuffixFormSequenceRuleApplierTest {

    SuffixFormSequenceRuleApplier applier;

    @Before
    public void setUp() throws Exception {
        this.applier = new SuffixFormSequenceRuleApplier();
    }

    @Test
    public void shouldApplySimpleRules() {
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('m', SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER);
            ImmutableSet<PhonAttr> phonAttrs = mock(ImmutableSet.class);
            assertThat(applier.apply(rule, phonAttrs), equalTo('m'));
            verifyZeroInteractions(phonAttrs);
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('a', SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY);
            ImmutableSet<PhonAttr> phonAttrs = mock(ImmutableSet.class);
            assertThat(applier.apply(rule, phonAttrs), equalTo('a'));
            verifyZeroInteractions(phonAttrs);
        }
    }

    @Test
    public void shouldInsertVowel_A_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of();
            assertThat(applier.apply(rule, phonAttrs), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), equalTo('e'));
        }
    }

    @Test
    public void shouldInsertVowel_I_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of();
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), equalTo('i'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ü'));
        }
    }

    @Test
    public void shouldInsertVowel_I_WithHarmony_ButNoRounding() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY_AND_NO_ROUNDING;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of();
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), equalTo('i'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('i'));
        }
    }

    @Test
    public void shouldInsertOptionalVowel_A_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_A_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of();
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterVowel);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterVowel, PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterVowel, PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant);
            assertThat(applier.apply(rule, phonAttrs), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant, PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant, PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), equalTo('e'));
        }
    }

    @Test
    public void shouldInsertOptionalVowel_I_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_I_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of();
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelBack, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastVowelFrontal, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant, PhonAttr.LastVowelBack);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant, PhonAttr.LastVowelFrontal);
            assertThat(applier.apply(rule, phonAttrs), equalTo('i'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant, PhonAttr.LastVowelBack, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant, PhonAttr.LastVowelFrontal, PhonAttr.LastVowelRounded);
            assertThat(applier.apply(rule, phonAttrs), equalTo('ü'));
        }
    }

    @Test
    public void shouldInsertOptionalVowel() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('a', RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of();
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('o', RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterVowel);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('e', RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant);
            assertThat(applier.apply(rule, phonAttrs), equalTo('e'));
        }
    }

    @Test
    public void shouldInsertOptionalConsonant() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_CONSONANT;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('k', RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of();
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('l', RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterConsonant);
            assertThat(applier.apply(rule, phonAttrs), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('m', RULE_TYPE);
            ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.of(PhonAttr.LastLetterVowel);
            assertThat(applier.apply(rule, phonAttrs), equalTo('m'));
        }
    }
}
