package org.trnltk.morphology.ambiguity.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ParseResultPartDifference {
    private final Pair<List<ParseResultPart>, List<ParseResultPart>> parts;

    public ParseResultPartDifference(Pair<List<ParseResultPart>, List<ParseResultPart>> parts) {
        this.parts = parts;
    }

    public Pair<List<ParseResultPart>, List<ParseResultPart>> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "ParseResultPartDifference{\n\t\t\t" +
                "parts=\n\t\t\t\t" +
                parts.getLeft() + "\n\t\t\t\t" +
                parts.getRight() +
                "\n\t\t}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResultPartDifference that = (ParseResultPartDifference) o;

        if (parts != null ? !parts.equals(that.parts) : that.parts != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return parts != null ? parts.hashCode() : 0;
    }
}
