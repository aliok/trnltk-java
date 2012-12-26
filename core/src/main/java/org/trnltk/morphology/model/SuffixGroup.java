package org.trnltk.morphology.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class SuffixGroup {
    private final String name;
    private final Set<Suffix> suffixes = new LinkedHashSet<Suffix>();

    public SuffixGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Suffix> getSuffixes() {
        return suffixes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixGroup that = (SuffixGroup) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "SuffixGroup{" +
                "name='" + name + '\'' +
                '}';
    }
}
