package zemberek3.shared.lexicon.tr;

import zemberek3.shared.structure.StringEnum;
import zemberek3.shared.structure.StringEnumMap;

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
