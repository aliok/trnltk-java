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

package org.trnltk.morphology.contextless.parser.formbased;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserBruteForceVerbTest;
import org.trnltk.morphology.contextless.rootfinder.BruteForceVerbRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ContextlessMorphologicParserBruteForceVerbTest extends BaseContextlessMorphologicParserBruteForceVerbTest {

    private ContextlessMorphologicParser parser;

    public ContextlessMorphologicParserBruteForceVerbTest() {
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

        final BruteForceVerbRootFinder bruteForceVerbRootFinder = new BruteForceVerbRootFinder();

        final BasicSuffixGraph basicSuffixGraph = new BasicSuffixGraph();

        basicSuffixGraph.initialize();

        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(basicSuffixGraph);

        final RootFinderChain rootFinderChain = new RootFinderChain()
                .offer(bruteForceVerbRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        this.parser = new ContextlessMorphologicParser(charSuffixGraph, null, rootFinderChain, suffixApplier);
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

    @Override
    public void assertParseCorrect(String surfaceToParse, String... expectedParseResults) {
        final ArrayList<String> list = Lists.newArrayList(expectedParseResults);
        final HashSet<String> set = new HashSet<String>(list);
        if (set.size() == list.size())
            System.out.println("There are duplicate items in expected parse results.\n\t\t" + Thread.currentThread().getStackTrace()[2]);
        super.assertParseCorrect(surfaceToParse, expectedParseResults);
    }
}
