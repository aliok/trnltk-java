package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.common.EnumLookupMap;
import org.trnltk.common.SupportsEnumLookup;

import java.util.Collection;
import java.util.Set;

public enum LexemeAttribute implements SupportsEnumLookup<LexemeAttribute> {

    // verb related
    Aorist_I, Aorist_A,
    ProgressiveVowelDrop,
    Passive_Il, Passive_In, Passive_InIl,
    Causative_t, Causative_Ir, Causative_It, Causative_Ar, Causative_dIr,

    // phonetic
    NoVoicing, Voicing, VoicingOpt,
    InverseHarmony, Doubling,

    // noun related
    CompoundP3sg,
    LastVowelDrop,

    // other
    RootChange,
    NoSuffix,
    Plural;

    public static final ImmutableSet<LexemeAttribute> CAUSATIVES = Sets.immutableEnumSet(Causative_t, Causative_dIr, Causative_Ar, Causative_Ir, Causative_It);

    private final static EnumLookupMap<LexemeAttribute> strLookUpMap = new EnumLookupMap<LexemeAttribute>(LexemeAttribute.class);

    @Override
    public String getStringForm() {
        return this.name();
    }

    public static LexemeAttribute lookup(String lookupKey) {
        return strLookUpMap.get(lookupKey);
    }

    public static Set<LexemeAttribute> lookupMultiple(Collection<String> lookupKeys) {
        return strLookUpMap.getMultiple(lookupKeys);
    }
}
