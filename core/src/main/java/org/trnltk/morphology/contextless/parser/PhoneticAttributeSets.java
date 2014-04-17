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

package org.trnltk.morphology.contextless.parser;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.trnltk.model.lexicon.PhoneticAttribute;
import org.trnltk.model.lexicon.PhoneticAttributeMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores all possible phonetic attributes combinations.
 * <p/>
 * This is to be pre-computed.
 * <p/>
 * Finding possible phonetic attributes are done as the following way:
 * <ol>
 * <li>Compute power set of all {@link PhoneticAttribute}s</li>
 * <li>Discard sets that are not valid</li>
 * </ol>
 * <p/>
 * Validity of a combination is checked using the {@link PhoneticAttributeMetadata} where rules are defined for each
 * {@link PhoneticAttribute} in terms of what other {@link PhoneticAttribute}s could make sense with an attribute.
 * <p/>
 * Quality of the metadata rules are regularly checked in the build system.
 * That means, valid {@link PhoneticAttribute} combinations are checked against found natural combinations from a big
 * corpus. There must be no possible combination found with the metadata which does not exist in the corpus.
 */
public class PhoneticAttributeSets {

    private final ImmutableMap<Long, Set<PhoneticAttribute>> validPhoneticAttributeSetsMap;
    private final ImmutableCollection<Set<PhoneticAttribute>> validPhoneticAttributeSets;

    public PhoneticAttributeSets() {
        this.validPhoneticAttributeSetsMap = this.findValidSets();
        this.validPhoneticAttributeSets = validPhoneticAttributeSetsMap.values();
    }

    protected ImmutableMap<Long, Set<PhoneticAttribute>> findValidSets() {

        final Set<Set<PhoneticAttribute>> phoneticAttributePowerSets = Sets.powerSet(new HashSet<PhoneticAttribute>(Lists.newArrayList(PhoneticAttribute.values())));

        final ImmutableMap.Builder<Long, Set<PhoneticAttribute>> validPhoneticAttributeSetsMapBuilder = new ImmutableMap.Builder<Long, Set<PhoneticAttribute>>();
        for (Set<PhoneticAttribute> set : phoneticAttributePowerSets) {
            if (PhoneticAttributeMetadata.isValid(set)) {
                validPhoneticAttributeSetsMapBuilder.put(getNumberForSet(set), set);
            }
        }

        return validPhoneticAttributeSetsMapBuilder.build();
    }

    public ImmutableMap<Long, Set<PhoneticAttribute>> getValidPhoneticAttributeSetsMap() {
        return validPhoneticAttributeSetsMap;
    }

    public ImmutableCollection<Set<PhoneticAttribute>> getValidPhoneticAttributeSets() {
        return validPhoneticAttributeSets;
    }


    public Set<PhoneticAttribute> internSet(Set<PhoneticAttribute> set) {
        return validPhoneticAttributeSetsMap.get(this.getNumberForSet(set));
    }

    public long getNumberForSet(Set<PhoneticAttribute> set) {
        long number = 0L;
        for (PhoneticAttribute phoneticAttribute : PhoneticAttribute.values()) {
            if (set.contains(phoneticAttribute))
                number = number | 0x1L;

            number = number << 1;
        }

        number = number >> 1;

        return number;
    }
}
