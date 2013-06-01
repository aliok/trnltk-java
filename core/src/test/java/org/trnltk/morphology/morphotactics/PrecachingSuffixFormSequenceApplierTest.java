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
import org.trnltk.morphology.model.suffixbased.SuffixFormSequence;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PrecachingSuffixFormSequenceApplierTest {
    PrecachingSuffixFormSequenceApplier applier;
    SuffixFormSequenceApplier delegate = new SuffixFormSequenceApplier();

    @Before
    public void setUp() throws Exception {
        final BasicSuffixGraph suffixGraph = new BasicSuffixGraph();
        suffixGraph.initialize();
        applier = new PrecachingSuffixFormSequenceApplier(suffixGraph, delegate);
    }

    @Test
    public void shouldHaveValue() throws Exception {
        // check only once, cannot test all!
        {
            final String str = applier.apply(new SuffixFormSequence("+Im"), ImmutableSet.of(PhoneticAttribute.LastLetterVowel));
            assertThat(str, equalTo("m"));
        }
        {
            final String str = applier.apply(new SuffixFormSequence("+Im"), ImmutableSet.of(PhoneticAttribute.LastLetterVowel, PhoneticAttribute.FirstLetterVowel));
            assertThat(str, equalTo("m"));
        }
    }
}
