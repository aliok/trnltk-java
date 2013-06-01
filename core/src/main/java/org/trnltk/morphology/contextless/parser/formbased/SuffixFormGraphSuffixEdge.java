package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableSet;
import org.trnltk.morphology.model.suffixbased.SuffixFormApplication;
import org.trnltk.morphology.model.lexicon.tr.PhoneticExpectation;

public class SuffixFormGraphSuffixEdge {

    private final SuffixFormGraphNode targetSuffixFormGraphNode;
    private final SuffixFormApplication suffixFormApplication;
    private final ImmutableSet<PhoneticExpectation> phoneticExpectations;

    public SuffixFormGraphSuffixEdge(SuffixFormGraphNode targetSuffixFormGraphNode, SuffixFormApplication suffixFormApplication, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this.targetSuffixFormGraphNode = targetSuffixFormGraphNode;
        this.suffixFormApplication = suffixFormApplication;
        this.phoneticExpectations = phoneticExpectations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormGraphSuffixEdge that = (SuffixFormGraphSuffixEdge) o;

        if (suffixFormApplication != null ? !suffixFormApplication.equals(that.suffixFormApplication) : that.suffixFormApplication != null)
            return false;
        if (targetSuffixFormGraphNode != null ? !targetSuffixFormGraphNode.equals(that.targetSuffixFormGraphNode) : that.targetSuffixFormGraphNode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetSuffixFormGraphNode != null ? targetSuffixFormGraphNode.hashCode() : 0;
        result = 31 * result + (suffixFormApplication != null ? suffixFormApplication.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SuffixFormGraphSuffixEdge{" +
                "targetSuffixFormGraphNode=" + targetSuffixFormGraphNode +
                ", suffixFormApplication=" + suffixFormApplication +
                '}';
    }

    public SuffixFormGraphNode getTargetSuffixFormGraphNode() {
        return targetSuffixFormGraphNode;
    }

    public SuffixFormApplication getSuffixFormApplication() {
        return suffixFormApplication;
    }

    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations() {
        return phoneticExpectations;
    }
}