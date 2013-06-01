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

package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.morphology.model.structure.StringEnum;
import org.trnltk.morphology.model.structure.StringEnumMap;

public enum SecondaryPos implements StringEnum<SecondaryPos> {
    Demonstrative("Demons"),
    Time("Time"),
    Quantitive("Quant"),
    Question("Ques"),
    ProperNoun("Prop"),
    Personal("Pers"),
    Reflexive("Reflex"),
    None("None"),
    Unknown("Unk"),
    Ordinal("Ord"),
    Cardinal("Card"),
    Percentage("Percent"),
    Ratio("Ratio"),
    Range("Range"),
    Real("Real"),
    Distribution("Dist"),
    Clock("Clock"),
    Date("Date"),

    // used only in trnltk
    ABBREVIATION("Abbr"),
    CARDINAL_DIGITS("DigitsC"),
    ORDINAL_DIGITS("DigitsO"),

    // not used in Trnltk too
    DUPLICATOR("Dup"),
    POST_POSITIVE("Postp");

    public static final ImmutableSet<SecondaryPos> NUMERAL_APPLICABLE = Sets.immutableEnumSet(Cardinal, Ordinal, Range, CARDINAL_DIGITS, ORDINAL_DIGITS); //TODO-INTEGRATION: more stuff above

    private final static StringEnumMap<SecondaryPos> shortFormToPosMap = StringEnumMap.get(SecondaryPos.class);

    private final String shortForm;

    private SecondaryPos(String shortForm) {
        this.shortForm = shortForm;
    }

    @Override
    public String getStringForm() {
        return shortForm;
    }

    public static StringEnumMap<SecondaryPos> converter() {
        return shortFormToPosMap;
    }
}
