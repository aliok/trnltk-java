package org.trnltk.morphology.model.suffixbased;

public class ZeroTransitionSuffix extends Suffix {
    private static final String PRETTY_NAME = "Zero";

    public ZeroTransitionSuffix(String name) {
        super(name, null, PRETTY_NAME, false);
        this.addSuffixForm("");
    }
}
