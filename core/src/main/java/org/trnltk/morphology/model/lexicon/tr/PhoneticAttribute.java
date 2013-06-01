package org.trnltk.morphology.model.lexicon.tr;

import org.trnltk.morphology.model.structure.StringEnum;
import org.trnltk.morphology.model.structure.StringEnumMap;

public enum PhoneticAttribute implements StringEnum<PhoneticAttribute> {
    LastLetterVowel("LLV"),
    LastLetterConsonant("LLC"),

    LastVowelFrontal("LVF"),
    LastVowelBack("LVB"),
    LastVowelRounded("LVR"),
    LastVowelUnrounded("LVuR"),

    LastLetterVoiceless("LLVless"),
    LastLetterNotVoiceless("LLNotVless"),

    LastLetterVoicelessStop("LLStop"),

    FirstLetterVowel("FLV"),
    FirstLetterConsonant("FLC"),

    HasNoVowel("NoVow");

    private final static StringEnumMap<PhoneticAttribute> shortFormToPosMap = StringEnumMap.get(PhoneticAttribute.class);

    private final String shortForm;

    private PhoneticAttribute(String shortForm) {
        this.shortForm = shortForm;
    }

    @Override
    public String getStringForm() {
        return shortForm;
    }

    public static StringEnumMap<PhoneticAttribute> converter() {
        return shortFormToPosMap;
    }
}
