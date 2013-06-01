package org.trnltk.morphology.ambiguity.model;

import java.util.List;

public class ParseResultPart {
    private final String primaryPos;
    private final String secondaryPos;
    private final List<String> suffixes;

    public ParseResultPart(String primaryPos, String secondaryPos, List<String> suffixes) {
        this.primaryPos = primaryPos;
        this.secondaryPos = secondaryPos;
        this.suffixes = suffixes;
    }

    public String getPrimaryPos() {
        return primaryPos;
    }

    public String getSecondaryPos() {
        return secondaryPos;
    }

    public List<String> getSuffixes() {
        return suffixes;
    }

    @Override
    public String toString() {
        return "ParseResultPart{" +
                "primaryPos='" + primaryPos + '\'' +
                ", secondaryPos='" + secondaryPos + '\'' +
                ", suffixes=" + suffixes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResultPart that = (ParseResultPart) o;

        if (primaryPos != null ? !primaryPos.equals(that.primaryPos) : that.primaryPos != null) return false;
        if (secondaryPos != null ? !secondaryPos.equals(that.secondaryPos) : that.secondaryPos != null) return false;
        if (suffixes != null ? !suffixes.equals(that.suffixes) : that.suffixes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = primaryPos != null ? primaryPos.hashCode() : 0;
        result = 31 * result + (secondaryPos != null ? secondaryPos.hashCode() : 0);
        result = 31 * result + (suffixes != null ? suffixes.hashCode() : 0);
        return result;
    }
}