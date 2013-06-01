package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;

import java.util.Set;

public class SuffixFormGraphNodeKey {

    private final ImmutableSet<PhoneticAttribute> phonAttrSet;
    private final SuffixGraphState state;

    public SuffixFormGraphNodeKey(SuffixGraphState state, Set<PhoneticAttribute> phonAttrSet) {
        this.state = state;
        this.phonAttrSet = Sets.immutableEnumSet(phonAttrSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormGraphNodeKey suffixFormGraphNodeKey = (SuffixFormGraphNodeKey) o;

        if (!phonAttrSet.equals(suffixFormGraphNodeKey.phonAttrSet)) return false;
        if (!state.equals(suffixFormGraphNodeKey.state)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + phonAttrSet.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SuffixFormGraphNodeKey{" +
                "state='" + state + '\'' +
                ", phonAttrSet=" + phonAttrSet +
                '}';
    }

    public ImmutableSet<PhoneticAttribute> getPhonAttrSet() {
        return phonAttrSet;
    }

    public SuffixGraphState getState() {
        return state;
    }
}