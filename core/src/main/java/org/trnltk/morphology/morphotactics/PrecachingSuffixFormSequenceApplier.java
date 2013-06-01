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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.morphology.model.suffixbased.SuffixForm;
import org.trnltk.morphology.model.suffixbased.SuffixFormSequence;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import java.util.Collection;
import java.util.Set;

public class PrecachingSuffixFormSequenceApplier extends SuffixFormSequenceApplier {

    private SuffixGraph suffixGraph;
    private SuffixFormSequenceApplier delegate;
    private HashBasedTable<SuffixFormSequence, ImmutableSet<PhoneticAttribute>, String> suffixFormSequenceTable;

    private static final ImmutableSet<PhoneticAttribute> MODIFIER_ATTRIBUTES = Sets.immutableEnumSet(
            PhoneticAttribute.LastVowelBack,
            PhoneticAttribute.LastVowelFrontal,
            PhoneticAttribute.LastVowelUnrounded,
            PhoneticAttribute.LastVowelRounded,
            PhoneticAttribute.LastLetterConsonant,
            PhoneticAttribute.LastLetterVowel,
            PhoneticAttribute.LastLetterVoiceless
    );

    public PrecachingSuffixFormSequenceApplier(SuffixGraph suffixGraph, SuffixFormSequenceApplier delegate) {
        this.suffixGraph = suffixGraph;
        this.delegate = delegate;

        this.initialize();
    }

    private void initialize() {
        final Collection<Suffix> allSuffixes = suffixGraph.getAllSuffixes();
        final Set<Set<PhoneticAttribute>> modifierAttributesPowerSet = Sets.powerSet(MODIFIER_ATTRIBUTES);

        int expectedRowCount = allSuffixes.size();
        int expectedColumnCount = modifierAttributesPowerSet.size();
        this.suffixFormSequenceTable = HashBasedTable.create(expectedRowCount, expectedColumnCount);

        for (Suffix suffix : allSuffixes) {
            final Set<SuffixForm> suffixForms = suffix.getSuffixForms();
            for (SuffixForm suffixForm : suffixForms) {
                final SuffixFormSequence suffixFormSequence = suffixForm.getForm();
                for (Set<PhoneticAttribute> phoneticAttributes : modifierAttributesPowerSet) {
                    final ImmutableSet<PhoneticAttribute> phoneticAttributeImmutableSet = Sets.immutableEnumSet(phoneticAttributes);
                    final String appliedSuffixFormStr = this.delegate.apply(suffixFormSequence, phoneticAttributeImmutableSet);
                    this.suffixFormSequenceTable.put(suffixFormSequence, phoneticAttributeImmutableSet, appliedSuffixFormStr);
                }
            }
        }

    }

    @Override
    public String apply(SuffixFormSequence suffixFormSequence, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
        final Sets.SetView<PhoneticAttribute> intersection = Sets.intersection(MODIFIER_ATTRIBUTES, phoneticAttributesOfSurface);
        return this.suffixFormSequenceTable.get(suffixFormSequence, intersection);
    }
}
