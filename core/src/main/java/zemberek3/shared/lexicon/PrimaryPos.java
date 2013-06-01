package zemberek3.shared.lexicon;

import zemberek3.shared.structure.StringEnum;
import zemberek3.shared.structure.StringEnumMap;

public enum PrimaryPos implements StringEnum<PrimaryPos> {
    Noun("Noun"),
    Adjective("Adj"),
    Adverb("Adv"),
    Conjunction("Conj"),
    Interjection("Interj"),
    Verb("Verb"),
    Pronoun("Pron"),
    Numeral("Num"),
    Determiner("Det"),
    PostPositive("Postp"),
    Question("Ques"),
    Duplicator("Dup"),
    Punctuation("Punc"),
    Unknown("Unk");

    public String shortForm;

    PrimaryPos(String shortForm) {
        this.shortForm = shortForm;
    }

    private final static StringEnumMap<PrimaryPos> shortFormToPosMap = StringEnumMap.get(PrimaryPos.class);

    public static StringEnumMap<PrimaryPos> converter() {
        return shortFormToPosMap;
    }

    public String getStringForm() {
        return shortForm;
    }
}
