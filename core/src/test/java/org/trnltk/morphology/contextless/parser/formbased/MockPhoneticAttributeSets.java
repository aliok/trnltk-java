package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticAttributeMetadata;

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

        final Set<Set<PhoneticAttribute>> phoneticAttributePowerSets = Sets.powerSet(new HashSet<>(Lists.newArrayList(PhoneticAttribute.values())));

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
