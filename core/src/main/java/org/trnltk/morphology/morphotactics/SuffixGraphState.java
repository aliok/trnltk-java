package org.trnltk.morphology.morphotactics;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.morphology.model.Suffix;
import org.trnltk.morphology.model.SyntacticCategory;

import java.util.HashSet;

public class SuffixGraphState {
    private final String name;
    private final SuffixGraphStateType type;
    private final SyntacticCategory syntacticCategory;
    private ImmutableSet<SuffixEdge> outEdges;

    public SuffixGraphState(String name, SuffixGraphStateType suffixGraphStateType, SyntacticCategory syntacticCategory) {
        this.name = name;
        this.type = suffixGraphStateType;
        this.syntacticCategory = syntacticCategory;
        this.outEdges = ImmutableSet.of();
    }

    public String getName() {
        return name;
    }

    public SyntacticCategory getSyntacticCategory() {
        return syntacticCategory;
    }

    public SuffixGraphStateType getType() {
        return type;
    }

    public ImmutableSet<SuffixEdge> getOutEdges() {
        return this.outEdges;
    }

    public void addOutSuffix(Suffix suffix, SuffixGraphState suffixGraphState) {
        final HashSet<SuffixEdge> tempSet = Sets.newHashSet(outEdges);
        tempSet.add(new SuffixEdge(suffix, suffixGraphState));
        this.outEdges = ImmutableSet.copyOf(tempSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixGraphState that = (SuffixGraphState) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "SuffixGraphState{" +
                "name='" + name + '\'' +
                '}';
    }
}
