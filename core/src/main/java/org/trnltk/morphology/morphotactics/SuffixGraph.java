package org.trnltk.morphology.morphotactics;

import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.morphology.model.suffixbased.SuffixForm;

import java.util.Collection;

public interface SuffixGraph {

    SuffixGraphState getDefaultStateForRoot(Root root);

    Collection<SuffixGraphState> getRootSuffixGraphStates();

    void initialize();

    Suffix getSuffix(String name);

    SuffixGraphState getSuffixGraphState(String stateName);

    Collection<Suffix> getAllSuffixes();

    SuffixForm getSuffixForm(String suffixName, String suffixFormStr);
}
