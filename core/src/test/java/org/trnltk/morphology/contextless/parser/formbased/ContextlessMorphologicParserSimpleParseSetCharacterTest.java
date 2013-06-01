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
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserSimpleParseSetCharacterTest;
import org.trnltk.morphology.contextless.parser.rootfinders.*;
import org.trnltk.morphology.contextless.parser.suffixbased.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.suffixbased.SuffixApplier;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.io.IOException;
import java.util.List;

public class ContextlessMorphologicParserSimpleParseSetCharacterTest extends BaseContextlessMorphologicParserSimpleParseSetCharacterTest {

    private HashMultimap<String, ? extends Root> originalRootMap;
    private ContextlessMorphologicParser parser;

    public ContextlessMorphologicParserSimpleParseSetCharacterTest() {
        this.originalRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
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
        final CopulaSuffixGraph copulaSuffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        copulaSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));
        final PredefinedPaths predefinedPaths = new PredefinedPaths(copulaSuffixGraph, clonedRootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPaths.initialize();

        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(clonedRootMap);
        final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
        final PuncRootFinder puncRootFinder = new PuncRootFinder();


        final RootFinderChain rootFinderChain = new RootFinderChain()
                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, new PhoneticsAnalyzer(), phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(copulaSuffixGraph);

        this.parser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPaths, rootFinderChain, suffixApplier);
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

    @Test
    public void shouldParseParseSet001() throws IOException {
        this.shouldParseParseSetN("001", true);
    }

    @Ignore
    @Test
    public void shouldParseParseSet003() throws IOException {
        this.shouldParseParseSetN("003", false);
    }

    @Ignore
    @Test
    public void shouldParseParseSet005() throws IOException {
        this.shouldParseParseSetN("005", false);
    }

    @Test
    @Ignore
    public void shouldParseParseSet999() throws IOException {
        this.shouldParseParseSetN("999", false);
    }

}
