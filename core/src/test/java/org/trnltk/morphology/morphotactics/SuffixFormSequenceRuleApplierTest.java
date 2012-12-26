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
import org.trnltk.morphology.phonetics.PhoneticAttribute;

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
            ImmutableSet<PhoneticAttribute> phoneticAttributes = mock(ImmutableSet.class);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('m'));
            verifyZeroInteractions(phoneticAttributes);
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('a', SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = mock(ImmutableSet.class);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('a'));
            verifyZeroInteractions(phoneticAttributes);
        }
    }

    @Test
    public void shouldInsertVowel_A_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of();
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('e'));
        }
    }

    @Test
    public void shouldInsertVowel_I_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of();
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('i'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ü'));
        }
    }

    @Test
    public void shouldInsertVowel_I_WithHarmony_ButNoRounding() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY_AND_NO_ROUNDING;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of();
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('i'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('i'));
        }
    }

    @Test
    public void shouldInsertOptionalVowel_A_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_A_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of();
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterVowel);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterVowel, PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterVowel, PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant, PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('a'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant, PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('e'));
        }
    }

    @Test
    public void shouldInsertOptionalVowel_I_WithHarmony() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_I_WITH_HARMONY;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of();
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelBack, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastVowelFrontal, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant, PhoneticAttribute.LastVowelBack);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ı'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant, PhoneticAttribute.LastVowelFrontal);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('i'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant, PhoneticAttribute.LastVowelBack, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('u'));
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub(RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant, PhoneticAttribute.LastVowelFrontal, PhoneticAttribute.LastVowelRounded);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('ü'));
        }
    }

    @Test
    public void shouldInsertOptionalVowel() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('a', RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of();
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('o', RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterVowel);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('e', RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('e'));
        }
    }

    @Test
    public void shouldInsertOptionalConsonant() {
        final SuffixFormSequence.SuffixFormSequenceRuleType RULE_TYPE = SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_CONSONANT;
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('k', RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of();
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('l', RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterConsonant);
            assertThat(applier.apply(rule, phoneticAttributes), nullValue());
        }
        {
            final SuffixFormSequence.SuffixFormSequenceRule rule = new SuffixFormSequenceRuleStub('m', RULE_TYPE);
            ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.of(PhoneticAttribute.LastLetterVowel);
            assertThat(applier.apply(rule, phoneticAttributes), equalTo('m'));
        }
    }
}
