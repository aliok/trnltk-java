package org.trnltk.morphology.morphotactics;

import org.trnltk.model.suffix.SuffixTransition;
import org.trnltk.morphology.contextless.parser.SuffixFormGraphSuffixEdge;

import java.util.List;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class AllowEverythingDisallowedPathProviderImpl implements DisallowedPathProvider {
    @Override
    public void initialize() {
        // do nothing
    }

    @Override
    public boolean isPathDisallowed(SuffixFormGraphSuffixEdge suffixFormGraphSuffixEdge, List<SuffixTransition> suffixTransitionsOfMorphemeContainer) {
        // allow everything
        return false;
    }
}
