package org.trnltk.morphology.model;

import org.trnltk.common.EnumLookupMap;
import org.trnltk.common.SupportsEnumLookup;

public enum SyntacticCategory implements SupportsEnumLookup<SyntacticCategory> {
    NOUN("Noun"),
    ADJECTIVE("Adj"),
    ADVERB("Adv"),
    VERB("Verb"),
    PRONOUN("Pron"),
    DETERMINER("Det"),
    INTERJECTION("Interj"),
    CONJUNCTION("Conj"),
    QUESTION("Ques"),
    PUNCTUATION("Punc"),
    NUMERAL("Num"),
    PARTICLE("Part");

    private final static EnumLookupMap<SyntacticCategory> strLookUpMap = new EnumLookupMap<SyntacticCategory>(SyntacticCategory.class);

    private final String name;

    private SyntacticCategory(String name) {
        this.name = name;
    }

    @Override
    public String getLookupKey() {
        return name;
    }

    public static SyntacticCategory lookup(String lookupKey) {
        return strLookUpMap.get(lookupKey);
    }

}
