package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticAttributeMetadata;

import java.util.HashSet;
import java.util.Set;

public class PhoneticAttributeSets {

    private final ImmutableMap<Long, Set<PhoneticAttribute>> validPhoneticAttributeSetsMap;
    private final ImmutableCollection<Set<PhoneticAttribute>> validPhoneticAttributeSets;

    public PhoneticAttributeSets() {
        this.validPhoneticAttributeSetsMap = this.findValidSets();
        this.validPhoneticAttributeSets = validPhoneticAttributeSetsMap.values();
    }

    protected ImmutableMap<Long, Set<PhoneticAttribute>> findValidSets() {

        final Set<Set<PhoneticAttribute>> phoneticAttributePowerSets = Sets.powerSet(new HashSet<>(Lists.newArrayList(PhoneticAttribute.values())));

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
