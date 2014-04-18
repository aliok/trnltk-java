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

package org.trnltk.experiment.morphology.contextless.parser;

import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.morphology.contextless.parser.PhoneticAttributeSets;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.model.lexicon.PhoneticAttribute;

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
