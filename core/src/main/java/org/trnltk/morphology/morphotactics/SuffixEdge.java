package org.trnltk.morphology.morphotactics;

import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.Suffix;

public class SuffixEdge {
    private final Suffix suffix;
    private final SuffixGraphState targetState;

    public SuffixEdge(Suffix suffix, SuffixGraphState targetState) {
        Validate.notNull(suffix);
        Validate.notNull(targetState);

        this.suffix = suffix;
        this.targetState = targetState;
    }

    public Suffix getSuffix() {
        return suffix;
    }

    public SuffixGraphState getTargetState() {
        return targetState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixEdge that = (SuffixEdge) o;

        if (!suffix.equals(that.suffix)) return false;
        if (!targetState.equals(that.targetState)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = suffix.hashCode();
        result = 31 * result + targetState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SuffixEdge{" +
                "suffix=" + suffix +
                ", targetState=" + targetState +
                '}';
    }
}
