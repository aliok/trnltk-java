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
import org.trnltk.morphology.model.Suffix;
import org.trnltk.morphology.model.SuffixForm;
import org.trnltk.morphology.model.SuffixFormSequence;
import zemberek3.lexicon.tr.PhonAttr;

import java.util.Collection;
import java.util.Set;

public class PrecachingSuffixFormSequenceApplier extends SuffixFormSequenceApplier {

    private SuffixGraph suffixGraph;
    private SuffixFormSequenceApplier delegate;
    private HashBasedTable<SuffixFormSequence, ImmutableSet<PhonAttr>, String> suffixFormSequenceTable;

    private static final ImmutableSet<PhonAttr> MODIFIER_ATTRIBUTES = Sets.immutableEnumSet(
            PhonAttr.LastVowelBack,
            PhonAttr.LastVowelFrontal,
            PhonAttr.LastVowelUnrounded,
            PhonAttr.LastVowelRounded,
            PhonAttr.LastLetterConsonant,
            PhonAttr.LastLetterVowel,
            PhonAttr.LastLetterVoiceless
    );

    public PrecachingSuffixFormSequenceApplier(SuffixGraph suffixGraph, SuffixFormSequenceApplier delegate) {
        this.suffixGraph = suffixGraph;
        this.delegate = delegate;

        this.initialize();
    }

    private void initialize() {
        final Collection<Suffix> allSuffixes = suffixGraph.getAllSuffixes();
        final Set<Set<PhonAttr>> modifierAttributesPowerSet = Sets.powerSet(MODIFIER_ATTRIBUTES);

        int expectedRowCount = allSuffixes.size();
        int expectedColumnCount = modifierAttributesPowerSet.size();
        this.suffixFormSequenceTable = HashBasedTable.create(expectedRowCount, expectedColumnCount);

        for (Suffix suffix : allSuffixes) {
            final Set<SuffixForm> suffixForms = suffix.getSuffixForms();
            for (SuffixForm suffixForm : suffixForms) {
                final SuffixFormSequence suffixFormSequence = suffixForm.getForm();
                for (Set<PhonAttr> phonAttrs : modifierAttributesPowerSet) {
                    final ImmutableSet<PhonAttr> phonAttrImmutableSet = Sets.immutableEnumSet(phonAttrs);
                    final String appliedSuffixFormStr = this.delegate.apply(suffixFormSequence, phonAttrImmutableSet);
                    this.suffixFormSequenceTable.put(suffixFormSequence, phonAttrImmutableSet, appliedSuffixFormStr);
                }
            }
        }

    }

    @Override
    public String apply(SuffixFormSequence suffixFormSequence, Set<PhonAttr> phoneticAttributesOfSurface) {
        final Sets.SetView<PhonAttr> intersection = Sets.intersection(MODIFIER_ATTRIBUTES, phoneticAttributesOfSurface);
        return this.suffixFormSequenceTable.get(suffixFormSequence, intersection);
    }
}
