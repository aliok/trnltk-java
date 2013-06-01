/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
