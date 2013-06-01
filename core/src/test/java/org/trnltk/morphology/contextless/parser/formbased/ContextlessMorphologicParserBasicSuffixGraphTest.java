/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.morphology.contextless.parser.formbased;


import com.google.common.collect.HashMultimap;
import org.junit.Before;
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserBasicSuffixGraphTest;
import org.trnltk.morphology.contextless.parser.rootfinders.DictionaryRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.RootFinderChain;
import org.trnltk.morphology.contextless.parser.suffixbased.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.suffixbased.SuffixApplier;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.List;

public class ContextlessMorphologicParserBasicSuffixGraphTest extends BaseContextlessMorphologicParserBasicSuffixGraphTest {

    private ContextlessMorphologicParser parser;
    private HashMultimap<String, ? extends Root> originalRootMap;

    public ContextlessMorphologicParserBasicSuffixGraphTest() {
        this.originalRootMap = RootMapFactory.createSimpleConvertCircumflexes();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return HashMultimap.create(this.originalRootMap);
    }

    @Override
    protected void buildParser(HashMultimap<String, Root> clonedRootMap) {
        final SuffixGraph suffixGraph = new BasicSuffixGraph();
        suffixGraph.initialize();

        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();

        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, new PhoneticsAnalyzer(), phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(suffixGraph);

        final RootFinderChain rootFinderChain = new RootFinderChain();
        rootFinderChain.offer(new DictionaryRootFinder(clonedRootMap), RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, clonedRootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPaths.initialize();

        this.parser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPaths, rootFinderChain, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }
}
