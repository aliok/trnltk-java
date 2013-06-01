package org.trnltk.morphology.model.suffixbased;

import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;

public class SuffixTransition {
    private final SuffixGraphState sourceState;
    private final SuffixFormApplication suffixFormApplication;
    private final SuffixGraphState targetState;

    public SuffixTransition(SuffixGraphState sourceState, SuffixFormApplication suffixFormApplication, SuffixGraphState targetState) {
        this.sourceState = sourceState;
        this.suffixFormApplication = suffixFormApplication;
        this.targetState = targetState;
    }

    public boolean isDerivational() {
        return SuffixGraphStateType.DERIVATIONAL.equals(this.sourceState.getType());
    }

    public SuffixGraphState getSourceState() {
        return sourceState;
    }

    public SuffixFormApplication getSuffixFormApplication() {
        return suffixFormApplication;
    }

    public SuffixGraphState getTargetState() {
        return targetState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixTransition that = (SuffixTransition) o;

        if (!sourceState.equals(that.sourceState)) return false;
        if (!suffixFormApplication.equals(that.suffixFormApplication)) return false;
        if (!targetState.equals(that.targetState)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourceState.hashCode();
        result = 31 * result + suffixFormApplication.hashCode();
        result = 31 * result + targetState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SuffixTransition{" +
                "sourceState=" + sourceState +
                ", targetState=" + targetState +
                ", suffixFormApplication=" + suffixFormApplication +
                '}';
    }
}
