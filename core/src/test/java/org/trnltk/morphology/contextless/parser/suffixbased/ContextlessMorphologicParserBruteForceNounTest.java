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

package org.trnltk.morphology.contextless.parser.suffixbased;

import com.google.common.collect.HashMultimap;
import org.junit.Before;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserBruteForceNounTest;
import org.trnltk.morphology.contextless.rootfinder.BruteForceNounRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.List;

public class ContextlessMorphologicParserBruteForceNounTest extends BaseContextlessMorphologicParserBruteForceNounTest {

    private ContextlessMorphologicParser parser;

    public ContextlessMorphologicParserBruteForceNounTest() {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return null;
    }

    @Override
    protected void buildParser(HashMultimap<String, Root> clonedRootMap) {
        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);

        final BruteForceNounRootFinder bruteForceNounRootFinder = new BruteForceNounRootFinder();

        final BasicSuffixGraph basicSuffixGraph = new BasicSuffixGraph();

        basicSuffixGraph.initialize();

        this.parser = new ContextlessMorphologicParserFactory()
                .suffixGraph(basicSuffixGraph)
                .suffixApplier(suffixApplier)
                .rootFinder(bruteForceNounRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .build();
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

}
