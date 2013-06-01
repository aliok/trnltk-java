package org.trnltk.morphology.contextless.parser.rootfinders;

import org.junit.Before;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BaseRootFinderTest<R extends Root> {

    private RootFinder rootFinder;

    @Before
    public void setUp() throws Exception {
        this.rootFinder = this.createRootFinder();
    }

    protected abstract RootFinder createRootFinder();

    protected List<R> findRootsForPartialInput(String partialInput, String wholeSurface) {
        final TurkishSequence partialInputSeq = partialInput != null ? new TurkishSequence(partialInput) : null;
        final TurkishSequence inputSeq = wholeSurface != null ? new TurkishSequence(wholeSurface) : null;
        if (!rootFinder.handles(partialInputSeq, inputSeq))
            return Collections.EMPTY_LIST;
        else
            return new ArrayList<R>((Collection<? extends R>) rootFinder.findRootsForPartialInput(partialInputSeq, inputSeq));
    }

}
