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

package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.trnltk.model.lexicon.PhoneticAttribute;
import org.trnltk.model.lexicon.PhoneticAttributeMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockPhoneticAttributeSets extends PhoneticAttributeSets {

    private static final List<Long> SETS_TO_INCLUDE = Arrays.asList(
            1618L,
            // [LastVowelFrontal, FirstLetterConsonant, LastLetterNotVoiceless, LastVowelUnrounded, LastLetterConsonant]
            // gazel, gazelciğ, keleğ

            1682L,
            // [LastVowelFrontal, LastVowelRounded, LastLetterConsonant, FirstLetterConsonant, LastLetterNotVoiceless]
            // tedavül

            2642L,
            // [LastLetterVowel, LastVowelFrontal, LastVowelUnrounded, LastLetterNotVoiceless, FirstLetterConsonant]
            // gazeli

            1642L,
            // [LastLetterConsonant, LastVowelFrontal, LastVowelUnrounded, LastLetterVoiceless, LastLetterVoicelessStop, FirstLetterConsonant]
            // gazelcik

            1426L
            // [LastLetterConsonant, LastVowelFrontal, LastVowelRounded, LastLetterNotVoiceless, FirstLetterConsonant]
            // geliyor

    );

    protected ImmutableMap<Long, Set<PhoneticAttribute>> findValidSets() {

        final Set<Set<PhoneticAttribute>> phoneticAttributePowerSets = Sets.powerSet(new HashSet<PhoneticAttribute>(Lists.newArrayList(PhoneticAttribute.values())));

        final ImmutableMap.Builder<Long, Set<PhoneticAttribute>> validPhoneticAttributeSetsMapBuilder = new ImmutableMap.Builder<Long, Set<PhoneticAttribute>>();
        for (Set<PhoneticAttribute> set : phoneticAttributePowerSets) {
            if (PhoneticAttributeMetadata.isValid(set)) {
                final long numberForSet = getNumberForSet(set);
                if (SETS_TO_INCLUDE.contains(numberForSet))
                    validPhoneticAttributeSetsMapBuilder.put(numberForSet, set);
            }
        }

        return validPhoneticAttributeSetsMapBuilder.build();
    }
}
