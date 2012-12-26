package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.common.EnumLookupMap;
import org.trnltk.common.SupportsEnumLookup;


public enum SecondarySyntacticCategory implements SupportsEnumLookup<SecondarySyntacticCategory> {
    DUPLICATOR("Dup"),
    POST_POSITIVE("Postp"),
    QUESTION("Ques"),
    DEMONSTRATIVE("Demons"),
    REFLEXIVE("Reflex"),
    PERSONAL("Pers"),
    TIME("Time"),
    PROPER_NOUN("Prop"),
    ABBREVIATION("Abbr"),

    CARD("Card"),
    ORD("Ord"),
    DIGITS("Digits");

    public static final ImmutableSet<SecondarySyntacticCategory> NUMERAL_APPLICABLE = Sets.immutableEnumSet(CARD, ORD, DIGITS);

    private final static EnumLookupMap<SecondarySyntacticCategory> strLookUpMap = new EnumLookupMap<SecondarySyntacticCategory>(SecondarySyntacticCategory.class);
    private final String name;

    private SecondarySyntacticCategory(String name) {
        this.name = name;
    }

    @Override
    public String getLookupKey() {
        return name;
    }

    public static SecondarySyntacticCategory lookup(String lookupKey) {
        return strLookUpMap.get(lookupKey);
    }
}
