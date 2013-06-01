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

package org.trnltk.morphology.model.lexicon.tr;

import org.trnltk.morphology.model.structure.StringEnum;
import org.trnltk.morphology.model.structure.StringEnumMap;

/**
 * These represents attributes of morphemes.
 */
public enum RootAttribute implements StringEnum {
    // verb related
    Aorist_I, Aorist_A,
    ProgressiveVowelDrop,
    Passive_In,
    Causative_t,

    // phonetic
    Voicing, NoVoicing,
    InverseHarmony, Doubling,

    // noun related
    LastVowelDrop,
    CompoundP3sg,

    // other
    Special,
    NoSuffix,
    Plural,

    NounConsInsert_n,
    NoQuote,
    NonTransitive,
    CompoundP3sgRoot,
    Compound,
    Reflexive,
    Reciprocal,
    Ext;

    int index;

    RootAttribute() {
        this.index = this.ordinal();
    }

    private static StringEnumMap<RootAttribute> shortFormToPosMap = StringEnumMap.get(RootAttribute.class);

    public static StringEnumMap<RootAttribute> converter() {
        return shortFormToPosMap;
    }

    public String getStringForm() {
        return this.name();
    }
}
