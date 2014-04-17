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

package org.trnltk.morphology.contextless.parser.parsing;


import com.google.common.collect.HashMultimap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.*;
import org.trnltk.morphology.contextless.parser.parsing.base.BaseContextlessMorphologicParserSimpleParseSetSpeedTest;
import org.trnltk.morphology.contextless.rootfinder.DictionaryRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.rootfinder.RootValidator;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.io.IOException;
import java.util.LinkedList;

public class ContextlessMorphologicParserSimpleParseSetSpeedTest extends BaseContextlessMorphologicParserSimpleParseSetSpeedTest {

    private HashMultimap<String, ? extends Root> originalRootMap;
    private ContextlessMorphologicParser parser;

    public ContextlessMorphologicParserSimpleParseSetSpeedTest() {
        this.originalRootMap = RootMapFactory.createSimple();
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
    protected void buildParser(final HashMultimap<String, Root> clonedRootMap) {
        final BasicSuffixGraph basicSuffixGraph = new BasicSuffixGraph();
        basicSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));
        final PredefinedPaths predefinedPaths = new PredefinedPaths(basicSuffixGraph, clonedRootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));

        predefinedPaths.initialize();

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator())
                .offer(new DictionaryRootFinder(clonedRootMap), RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, new PhoneticsAnalyzer(), phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(basicSuffixGraph);

        this.parser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPaths, rootFinderChain, suffixApplier);
    }

    @Override
    protected LinkedList<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

    @Test
    public void shouldParseParseSet001() throws IOException {
        this.shouldParseParseSetN("001", false);
    }

    @Test
    public void shouldParseParseSet003() throws IOException {
        this.shouldParseParseSetN("003", false);
    }

    @Test
    public void shouldParseParseSet005() throws IOException {
        this.shouldParseParseSetN("005", false);
    }

    @Test
    @Ignore
    public void shouldParseParseSet999() throws IOException {
        this.shouldParseParseSetN("999", false);
    }

    @Test
    @Ignore
    public void shouldParseParseSet9998() throws IOException {
        this.shouldParseParseSetN("9998", false);
    }

    public static void main(String[] args) throws Exception {
        final ContextlessMorphologicParserSimpleParseSetSpeedTest contextlessMorphologicParserSimpleParseSetTest = new ContextlessMorphologicParserSimpleParseSetSpeedTest();
        contextlessMorphologicParserSimpleParseSetTest.setUp();
        contextlessMorphologicParserSimpleParseSetTest.shouldParseParseSetN("003", true);
    }
}
