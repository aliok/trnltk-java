package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class PhoneticAttributeSetsTest {

    @Ignore
    @Test
    public void printValidSets() {
        final PhoneticAttributeSets sets = new PhoneticAttributeSets();
        final ImmutableMap<Long, Set<PhoneticAttribute>> map = sets.getValidPhoneticAttributeSetsMap();
        for (Map.Entry<Long, Set<PhoneticAttribute>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue().toString());
        }
    }

    @Ignore
    @Test
    public void printSetForWord() {
        final PhoneticAttributeSets sets = new PhoneticAttributeSets();
        final EnumSet<PhoneticAttribute> set = new PhoneticsAnalyzer().calculatePhoneticAttributes("keleğ", null);
        System.out.println(sets.getNumberForSet(set));
        System.out.println(set);
    }

}
