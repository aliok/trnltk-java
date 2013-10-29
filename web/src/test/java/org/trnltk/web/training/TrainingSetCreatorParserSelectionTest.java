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

package org.trnltk.web.training;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.formbased.PhoneticAttributeSets;
import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraph;
import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraphExtractor;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.util.MorphemeContainerFormatter;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.fail;

public class TrainingSetCreatorParserSelectionTest {

    static final Ordering<String> byLengthOrdering = new Ordering<String>() {
        public int compare(String left, String right) {
            return Ints.compare(left.length(), right.length());
        }
    };

    @SuppressWarnings("unchecked")
    static final Ordering<String> parseResultOrdering = Ordering.compound(Arrays.asList(byLengthOrdering, Ordering.<String>natural()));

    @Test
//    @Ignore
    public void shouldMatchExpectations() throws IOException {
        boolean hasError = false;

        final List<Pair<String, List<String>>> entries = getEntries();

        final MorphologicParser morphologicParser = createParser();

        for (Pair<String, List<String>> entry : entries) {
            final String surface = entry.getLeft();
            final List<String> expectedParseResult = entry.getRight();
            final List<MorphemeContainer> morphemeContainers = morphologicParser.parseStr(surface);
            final List<String> retrieved = new ArrayList<String>(MorphemeContainerFormatter.formatMorphemeContainers(morphemeContainers));
            filterOutPossibleAmbiguities(morphemeContainers, retrieved);
            Collections.sort(retrieved, parseResultOrdering);
            if (!expectedParseResult.equals(retrieved)) {
                System.out.println("W " + surface);
                System.out.println("Expected");
                for (String s : expectedParseResult) {
                    System.out.println("- " + s);
                }
                System.out.println("Retrieved");
                for (String s : retrieved) {
                    System.out.println("- " + s);
                }
                hasError = true;
            }
        }

        if (hasError)
            fail();
    }

    private static final ImmutableList<String> UNNECESSARY_AMBIGUITIES = new ImmutableList.Builder<String>()
            .add("P3sg+Adj+asd")
            .build();

    private void filterOutPossibleAmbiguities(List<MorphemeContainer> morphemeContainers, List<String> formattedMorphemeContainers){
        Validate.isTrue(morphemeContainers.size() == formattedMorphemeContainers.size());
        Iterator<MorphemeContainer> morphemeContainerIterator = morphemeContainers.iterator();
        Iterator<String> formattedMorphemeContainerIterator = formattedMorphemeContainers.iterator();
        while(morphemeContainerIterator.hasNext()){
            MorphemeContainer morphemeContainer = morphemeContainerIterator.next();
            String formattedMorphemeContainer = formattedMorphemeContainerIterator.next();
            for (String unnecessaryAmbiguity : UNNECESSARY_AMBIGUITIES) {
                if(formattedMorphemeContainer.contains(unnecessaryAmbiguity)){
                    morphemeContainerIterator.remove();
                    formattedMorphemeContainerIterator.remove();
                }
            }
        }
    }

    private void validateEntries(List<Pair<String, List<String>>> entries) {
        for (Pair<String, List<String>> entry : entries) {
            final List<String> parseResults = entry.getRight();
            final boolean ordered = parseResultOrdering.isOrdered(parseResults);
            if (!ordered) {
                throw new RuntimeException("Parse results are not sorted! " + entry.getLeft());
            }
        }
    }

    private List<Pair<String, List<String>>> getEntries() throws IOException {
        final File expectationFile = new File(Resources.getResource("trainingSetParserExpectation.txt").getFile());
        final List<String> lines = Files.readLines(expectationFile, Charsets.UTF_8);

        final List<Pair<String, List<String>>> entries = new LinkedList<Pair<String, List<String>>>();

        String currentSurface = null;
        List<String> currentExpectedParseResults = new LinkedList<String>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("W ")) {
                if (currentSurface != null) {
                    entries.add(Pair.of(currentSurface, currentExpectedParseResults));
                }
                currentSurface = line.substring(2);
                currentExpectedParseResults = new LinkedList<String>();
            } else if (line.startsWith("- ")) {
                currentExpectedParseResults.add(line.substring(2));
            } else {
                throw new RuntimeException("Illegal line : " + i);
            }
        }

        if (currentSurface != null) {
            entries.add(Pair.of(currentSurface, currentExpectedParseResults));
        }

        validateEntries(entries);

        return entries;
    }

    private MorphologicParser createParser() {
        // load bundled dictionaries of numbers and words
        HashMultimap<String, ? extends Root> dictionaryRootMap = RootMapFactory.createSimpleWithNumbers();

        // build common parts
        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);

        // build extractor which is used while converting a suffix graph to a suffix form graph
        final SuffixFormGraphExtractor suffixFormGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);

        // build suffix graphs
        final SuffixGraph suffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        suffixGraph.initialize();

        // build predefined paths with suffix graphs and dictionary
        final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, dictionaryRootMap, suffixApplier);
        predefinedPaths.initialize();

        // build root finders and add them into the chain
        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(dictionaryRootMap);
        final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
        final PuncRootFinder puncRootFinder = new PuncRootFinder();

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());

        rootFinderChain
                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        // extract suffix form graph from suffix graph
        final SuffixFormGraph suffixFormGraph = suffixFormGraphExtractor.extract(suffixGraph);

        // finally, build parser
        final ContextlessMorphologicParser parser = new ContextlessMorphologicParser(suffixFormGraph, predefinedPaths, rootFinderChain, suffixApplier);

        return parser;
    }
}
