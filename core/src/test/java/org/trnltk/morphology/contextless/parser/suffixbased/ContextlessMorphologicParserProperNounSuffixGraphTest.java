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
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserProperNounSuffixGraphTest;
import org.trnltk.morphology.contextless.rootfinder.ProperNounFromApostropheRootFinder;
import org.trnltk.morphology.contextless.rootfinder.ProperNounWithoutApostropheRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.ProperNounSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.List;

public class ContextlessMorphologicParserProperNounSuffixGraphTest extends BaseContextlessMorphologicParserProperNounSuffixGraphTest {

    private ContextlessMorphologicParser parser;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return HashMultimap.create();
    }

    @Override
    protected void buildParser(final HashMultimap<String, Root> _unused) {
        final SuffixGraph suffixGraph = new ProperNounSuffixGraph(new BasicSuffixGraph());
        suffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);
        final RootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();

        final RootFinderChain rootFinderChain = new RootFinderChain()
                .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        this.parser = new ContextlessMorphologicParser(suffixGraph, null, rootFinderChain, suffixApplier);
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }
}
