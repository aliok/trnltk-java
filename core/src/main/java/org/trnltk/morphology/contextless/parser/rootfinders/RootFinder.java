package org.trnltk.morphology.contextless.parser.rootfinders;

import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.Collection;

public interface RootFinder {

    /**
     * A quick check if any roots could be created for the given input. A RootFinder impl doesn't have to
     * check everything, but it can use a regex to check a pattern quickly.
     * <p/>
     * A RootFinder implementation doesn't have to return some roots for an input which is marked as "could be handled" or vice-versa.
     * <p/>
     * The method {@link RootFinder#findRootsForPartialInput(org.trnltk.morphology.model.TurkishSequence, org.trnltk.morphology.model.TurkishSequence)}
     * can still return nothing, even if this method returns true.
     *
     * @param partialInput Partial surface
     * @param wholeSurface Whole surface
     * @return true if partial input could be handled
     */
    public boolean handles(TurkishSequence partialInput, TurkishSequence wholeSurface);

    public Collection<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence wholeSurface);
}
