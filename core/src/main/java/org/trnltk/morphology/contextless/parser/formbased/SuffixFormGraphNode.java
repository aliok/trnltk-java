package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.trnltk.morphology.model.suffixbased.SuffixForm;
import org.trnltk.morphology.model.suffixbased.SuffixFormApplication;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.lexicon.tr.PhoneticExpectation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SuffixFormGraphNode {
    private final SuffixFormGraphNodeKey suffixFormGraphNodeKey;
    private final Set<SuffixFormGraphSuffixEdge> edges = new HashSet<>();
    private final ImmutableSet<PhoneticAttribute> currentPhonAttrSet;
    private final SuffixGraphStateType suffixGraphStateType;

    private boolean explored;

    SuffixFormGraphNode(SuffixFormGraphNodeKey suffixFormGraphNodeKey, SuffixGraphStateType suffixGraphStateType, ImmutableSet<PhoneticAttribute> currentPhonAttrSet) {
        this.suffixFormGraphNodeKey = suffixFormGraphNodeKey;
        this.currentPhonAttrSet = currentPhonAttrSet;   //TODO: intern?
        this.suffixGraphStateType = suffixGraphStateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormGraphNode suffixFormGraphNode = (SuffixFormGraphNode) o;

        if (!suffixFormGraphNodeKey.equals(suffixFormGraphNode.suffixFormGraphNodeKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return suffixFormGraphNodeKey.hashCode();
    }

    @Override
    public String toString() {
        return "SuffixFormGraphNode{" +
                "suffixFormGraphNodeKey=" + suffixFormGraphNodeKey +
                ", suffixGraphStateType=" + suffixGraphStateType +
                ", currentPhonAttrSet=" + currentPhonAttrSet +
                '}';
    }

    public SuffixFormGraphNodeKey getSuffixFormGraphNodeKey() {
        return suffixFormGraphNodeKey;
    }

    public ImmutableSet<PhoneticAttribute> getCurrentPhonAttrSet() {
        return currentPhonAttrSet;
    }

    public SuffixFormGraphSuffixEdge addSuffixFormEdge(SuffixFormGraphNode targetSuffixFormGraphNode, SuffixFormApplication suffixFormApplication, Collection<PhoneticExpectation> phoneticExpectations) {
        final SuffixFormGraphSuffixEdge edge = new SuffixFormGraphSuffixEdge(targetSuffixFormGraphNode, suffixFormApplication,
                CollectionUtils.isNotEmpty(phoneticExpectations) ? ImmutableSet.copyOf(phoneticExpectations) : ImmutableSet.<PhoneticExpectation>of());
        this.edges.add(edge);
        return edge;
    }

    public SuffixFormGraphSuffixEdge addEmptySuffixFormEdge(SuffixFormGraphNode targetSuffixFormGraphNode, SuffixForm suffixForm) {
        return this.addSuffixFormEdge(
                targetSuffixFormGraphNode,
                new SuffixFormApplication(suffixForm, StringUtils.EMPTY, StringUtils.EMPTY),
                null
        );
    }

    public Set<SuffixFormGraphSuffixEdge> getEdges() {
        return edges;
    }

    public SuffixGraphStateType getSuffixGraphStateType() {
        return suffixGraphStateType;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }
}