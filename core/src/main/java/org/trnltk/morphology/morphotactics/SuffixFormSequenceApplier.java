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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.trnltk.morphology.model.SuffixFormSequence;
import org.trnltk.morphology.phonetics.PhoneticAttribute;

import java.util.Set;

public class SuffixFormSequenceApplier {
    private static final ImmutableSet<SuffixFormSequence.SuffixFormSequenceRuleType> OPTIONAL_VOWEL_RULE_TYPES = Sets.immutableEnumSet(
            SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL,
            SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_A_WITH_HARMONY,
            SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_I_WITH_HARMONY
    );

    private static final ImmutableSet<SuffixFormSequence.SuffixFormSequenceRuleType> VOWEL_RULE_TYPES = Sets.immutableEnumSet(
            SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY,
            SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY,
            SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY
    );

    public String apply(final SuffixFormSequence suffixFormSequence, final Set<PhoneticAttribute> phoneticAttributesOfSurface) {
        final StringBuilder builder = new StringBuilder();
        for (SuffixFormSequence.SuffixFormSequenceRule rule : suffixFormSequence.getRules()) {
            final Character c = rule.apply(phoneticAttributesOfSurface);
            if (c == null)
                continue;
            else
                builder.append(c);
        }

        return builder.toString().trim();
    }

    public boolean isApplicable(final SuffixFormSequence suffixFormSequence, final Set<PhoneticAttribute> phoneticAttributesOfSurface) {
        final ImmutableList<SuffixFormSequence.SuffixFormSequenceRule> rules = suffixFormSequence.getRules();
        if (CollectionUtils.isEmpty(rules))
            return true;

        // the only case where the suffix form is not applicable is, having two vowels together
        // following code (unfortunately) assumes, in the suffix form, there are no 2 vowels in a row!

        final boolean lastSurfaceLetterIsVowel = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterVowel);

        if (!lastSurfaceLetterIsVowel)
            return true;

        final SuffixFormSequence.SuffixFormSequenceRule firstRule = rules.get(0);

        return !firstRule.getRuleType().equals(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY) &&
                !firstRule.getRuleType().equals(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY) &&
                !firstRule.getRuleType().equals(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY) &&
                !firstRule.getRuleType().equals(SuffixFormSequence.SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY_AND_NO_ROUNDING);

    }

}
