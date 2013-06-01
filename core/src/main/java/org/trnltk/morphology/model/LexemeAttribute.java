package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import zemberek3.shared.structure.StringEnum;
import zemberek3.shared.structure.StringEnumMap;

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
