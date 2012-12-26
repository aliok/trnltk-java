package org.trnltk.morphology.morphotactics;

import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.Suffix;
import org.trnltk.morphology.model.SuffixForm;

import java.util.Collection;

public interface SuffixGraph {

    public SuffixGraphState getDefaultStateForRoot(Root root);

    void initialize();

    Suffix getSuffix(String name);

    SuffixGraphState getSuffixGraphState(String stateName);

    Collection<Suffix> getAllSuffixes();

    SuffixForm getSuffixForm(String suffixName, String suffixFormStr);
}
