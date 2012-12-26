package org.trnltk.morphology.model;

import org.trnltk.common.specification.Specification;

import java.util.LinkedHashSet;
import java.util.Set;

public class Suffix {
    private final String name;
    private final Set<SuffixForm> suffixForms = new LinkedHashSet<SuffixForm>();    //used LinkedHashSet to keep insertion order 
    private final SuffixGroup suffixGroup;
    private final String prettyName;
    private final boolean allowRepetition;

    public Suffix(String name, SuffixGroup suffixGroup, String prettyName) {
        this(name, suffixGroup, prettyName, false);
    }

    public Suffix(String name, SuffixGroup suffixGroup, String prettyName, boolean allowRepetition) {
        this.name = name;
        this.suffixGroup = suffixGroup;
        this.prettyName = prettyName;
        this.allowRepetition = allowRepetition;
    }

    public void addSuffixForm(String suffixFormStr) {
        this.addSuffixForm(suffixFormStr, null);
    }

    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition) {
        this.addSuffixForm(suffixFormStr, precondition, null);
    }

    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition, Specification<MorphemeContainer> postcondition) {
        this.addSuffixForm(suffixFormStr, precondition, postcondition, null);
    }

    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition,
                              Specification<MorphemeContainer> postCondition, Specification<MorphemeContainer> postDerivativeCondition) {
        final SuffixForm suffixForm = new SuffixForm(this, suffixFormStr, precondition, postCondition, postDerivativeCondition);
        this.suffixForms.add(suffixForm);
    }

    public SuffixForm getSuffixForm(String suffixFormStr) {
        SuffixForm result = null;

        for (SuffixForm suffixForm : suffixForms) {
            if (suffixFormStr.equals(suffixForm.getForm().getSuffixFormStr())) {
                if (result != null)
                    throw new IllegalStateException("Multiple suffix forms found for suffix " + this + " and form " + suffixFormStr);
                else
                    result = suffixForm;
            }
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public Set<SuffixForm> getSuffixForms() {
        return suffixForms;
    }

    public SuffixGroup getSuffixGroup() {
        return suffixGroup;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public boolean isAllowRepetition() {
        return allowRepetition;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suffix suffix = (Suffix) o;

        if (!name.equals(suffix.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Suffix{" +
                "name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                '}';
    }
}
