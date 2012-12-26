package org.trnltk.morphology.contextless.parser;

import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.Collection;

public interface RootFinder {
    public Collection<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input);
}
