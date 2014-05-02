package org.trnltk.morphology.morphotactics;

import org.trnltk.model.suffix.SuffixTransition;
import org.trnltk.morphology.contextless.parser.SuffixFormGraphSuffixEdge;

import java.util.List;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public interface DisallowedPathProvider {

    void initialize();

    boolean isPathDisallowed(SuffixFormGraphSuffixEdge suffixFormGraphSuffixEdge, List<SuffixTransition> suffixTransitionsOfMorphemeContainer);
}
