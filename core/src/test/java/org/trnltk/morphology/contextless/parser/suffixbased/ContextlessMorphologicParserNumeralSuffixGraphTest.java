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
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserNumeralSuffixGraphTest;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.DictionaryLoader;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.model.lexicon.Lexeme;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.NumeralSuffixGraph;
import org.trnltk.morphology.morphotactics.PrecachingSuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.HashSet;
import java.util.List;

public class ContextlessMorphologicParserNumeralSuffixGraphTest extends BaseContextlessMorphologicParserNumeralSuffixGraphTest {

    private HashMultimap<String, ? extends Root> originalRootMap;
    private ContextlessMorphologicParser parser;

    public ContextlessMorphologicParserNumeralSuffixGraphTest() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultNumeralMasterDictionary();
        this.originalRootMap = RootMapFactory.buildWithLexemesConvertCircumflexes(lexemes);
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
        final NumeralSuffixGraph suffixGraph = new NumeralSuffixGraph(new BasicSuffixGraph());
        suffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PrecachingSuffixFormSequenceApplier precachingSuffixFormSequenceApplier = new PrecachingSuffixFormSequenceApplier(suffixGraph, suffixFormSequenceApplier);
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(precachingSuffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);
        final RootFinder numeralDictionaryRootFinder = new DictionaryRootFinder(this.createRootMap());
        final RootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final RootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final RootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator())
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(numeralDictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        this.parser = new ContextlessMorphologicParser(suffixGraph, null, rootFinderChain, suffixApplier);
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

}