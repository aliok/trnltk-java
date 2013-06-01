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

package org.trnltk.model.lexicon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.common.structure.StringEnum;
import org.trnltk.common.structure.StringEnumMap;

public enum LexemeAttribute implements StringEnum<LexemeAttribute> {

    // verb related
    Aorist_I, Aorist_A,
    ProgressiveVowelDrop,
    Passive_Il, Passive_In, Passive_InIl,
    Causative_t, Causative_Ir, Causative_It, Causative_Ar, Causative_dIr,

    // phonetic
    NoVoicing, Voicing, VoicingOpt,
    InverseHarmony, Doubling,
    EndsWithAyn,

    // noun related
    CompoundP3sg,
    LastVowelDrop,

    // other
    Special,
    NoSuffix,
    Plural;

    public static final ImmutableSet<LexemeAttribute> CAUSATIVES = Sets.immutableEnumSet(Causative_t, Causative_dIr, Causative_Ar, Causative_Ir, Causative_It);

    private final static StringEnumMap<LexemeAttribute> shortFormToPosMap = StringEnumMap.get(LexemeAttribute.class);

    @Override
    public String getStringForm() {
        return this.name();
    }

    public static StringEnumMap<LexemeAttribute> converter() {
        return shortFormToPosMap;
    }

}
