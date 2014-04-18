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

package org.trnltk.morphology.morphotactics;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;
import org.trnltk.model.suffix.SuffixFormSequence;
import org.trnltk.model.lexicon.PhoneticAttribute;

import java.util.Set;

public class SuffixFormSequenceApplier {
    public String apply(final SuffixFormSequence suffixFormSequence, final Set<PhoneticAttribute> phoneticAttributesOfSurface) {
        final StringBuilder builder = new StringBuilder();
        for (SuffixFormSequence.SuffixFormSequenceRule rule : suffixFormSequence.getRules()) {
            final Character c = rule.apply(phoneticAttributesOfSurface);
            if (c != null)
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
