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

package org.trnltk.apps.experiments;


import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.Files;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.apps.commons.AppProperties;
import org.trnltk.model.letter.TurkishChar;
import org.trnltk.model.letter.TurkishSequence;
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
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.reducedambiguity.BasicRASuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.util.MorphemeContainerFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Requires a lot of memory!
 * Make sure you set properly.
 * <p/>
 * I used -Xms3512M -Xmx6072M and worked good with max L1 cache size of 200000
 */
// poor design of the test class....
public class AmbigutyGraphTest {

    private static final int BULK_SIZE = 1500;
    private static final int NUMBER_OF_THREADS = 8;

    private MorphologicParser contextlessMorphologicParser;
    private HashMultimap<String, ? extends Root> originalRootMap;


    public AmbigutyGraphTest() {
        this.originalRootMap = RootMapFactory.createSimpleConvertCircumflexes();
    }

    @Before
    public void setUp() {
        final HashMultimap<String, Root> rootMap = HashMultimap.create(this.originalRootMap);

        final SuffixGraph suffixGraph = new BasicRASuffixGraph();
        suffixGraph.initialize();

        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();

        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, new PhoneticsAnalyzer(), phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(suffixGraph);

        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(rootMap);
        final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
        final PuncRootFinder puncRootFinder = new PuncRootFinder();

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());
        rootFinderChain
//                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
//                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
//                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
//                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
//                .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
//                .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, rootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPaths.initialize();

        this.contextlessMorphologicParser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPaths, rootFinderChain, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
    }

    @Test
    public void parse8MWords_withOfflineAnalysis() throws Exception {
        final StopWatch completeProcessStopWatch = new StopWatch();
        completeProcessStopWatch.start();

        final List<String> words = getAllWords();
        final ArrayList<String> distinctWords = getDistinctWords(words);
        final ImmutableMultiset<String> wordCountSet = getWordCountSet(words);

        final List<String> distinctWordsWithEnoughOccurrences = getDistinctWordsWithEnoughOccurrences(wordCountSet);

        System.out.println("Number of words : " + words.size());
        System.out.println("Number of distinct words : " + distinctWords.size());
        System.out.println("Number of distinct words with enough occurrences : " + distinctWordsWithEnoughOccurrences.size());
        System.out.println("======================");

        final List<Map<String, List<String>>> resultMaps = buildResultMaps(distinctWordsWithEnoughOccurrences);

        final StopWatch parseStopWatch = new StopWatch();
        parseStopWatch.start();

        final ThreadPoolExecutor pool = startThreads(distinctWordsWithEnoughOccurrences, resultMaps);
        waitUntilThreadPoolToTerminate(pool);

        parseStopWatch.stop();

        System.out.println("Total time :" + parseStopWatch.toString());
        System.out.println("Nr of tokens : " + words.size());
        System.out.println("Nr of unique tokens : " + distinctWords.size());
        System.out.println("Nr of unique tokens with enough occurrences: " + distinctWordsWithEnoughOccurrences.size());
        System.out.println("Avg time : " + (parseStopWatch.getTime() * 1.0d) / (distinctWordsWithEnoughOccurrences.size() * 1.0d) + " ms");

        final Map<String, List<String>> resultMap = mergeResultMap(resultMaps);
        final Map<String, Integer> totalAmbiguityMap = buildTotalAmbiguityMap(wordCountSet, resultMap);
        final SortedMap<String, Integer> sortedTotalAmbiguityMap = buildSortedTotalAmbiguityMap(totalAmbiguityMap);

        final File outputFile = new File(AppProperties.generalFolder() + "/ambiguity_data.txt");
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
            dumpGraphData(bufferedWriter, wordCountSet, distinctWordsWithEnoughOccurrences, resultMap, sortedTotalAmbiguityMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null)
                bufferedWriter.close();
        }

        completeProcessStopWatch.stop();
        System.out.println("It took " + completeProcessStopWatch.toString());
    }

    private void dumpGraphData(BufferedWriter bufferedWriter, ImmutableMultiset<String> wordCountSet, List<String> distinctWordsWithEnoughOccurrences, Map<String, List<String>> resultMap, SortedMap<String, Integer> sortedTotalAmbiguityMap) throws IOException {
        final String separator = " ";

//        System.out.println(Joiner.on(separator).join(Iterables.transform(Lists.newArrayList(Feature.values()), new Function<Feature, String>() {
//            @Override
//            public String apply(org.trnltk.apps.experiments.AmbigutyGraphTest.Feature input) {
//                return input.name();
//            }
//        })));
        int[][] data = getGraphData(wordCountSet, distinctWordsWithEnoughOccurrences, resultMap, sortedTotalAmbiguityMap);
        for (int[] row : data) {
            bufferedWriter.write(Joiner.on(separator).join(ArrayUtils.toObject(row)));
            bufferedWriter.newLine();
        }
    }

    private int[][] getGraphData(ImmutableMultiset<String> wordCountSet, List<String> distinctWordsWithEnoughOccurrences, Map<String, List<String>> resultMap, SortedMap<String, Integer> sortedTotalAmbiguityMap) {
        int data[][] = new int[distinctWordsWithEnoughOccurrences.size()][Feature.values().length + 1];     // +1 for Y = totalAmbiguity
        int i = 0;
        for (Map.Entry<String, Integer> ambiguityEntry : sortedTotalAmbiguityMap.entrySet()) {
            final int[] dataRow = data[i];
            dataRow[Feature.values().length] = ambiguityEntry.getValue();           // last column is the Y
            for (int j = 0; j < Feature.values().length; j++) {
                Feature feature = Feature.values()[j];
                final String surface = ambiguityEntry.getKey();
                dataRow[j] = feature.compute(surface, resultMap.get(surface), wordCountSet);
            }
            i++;
        }
        return data;
    }

    private SortedMap<String, Integer> buildSortedTotalAmbiguityMap(final Map<String, Integer> totalAmbiguityMap) {
        final TreeMap<String, Integer> sortedTotalAmbiguityMap = new TreeMap<String, Integer>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Ordering.natural().reverse().compare(totalAmbiguityMap.get(o1), totalAmbiguityMap.get(o2));
            }
        });

        sortedTotalAmbiguityMap.putAll(totalAmbiguityMap);
        return sortedTotalAmbiguityMap;
    }

    private void waitUntilThreadPoolToTerminate(ThreadPoolExecutor pool) throws InterruptedException {
        pool.shutdown();
        while (!pool.isTerminated()) {
            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        }
    }

    private ThreadPoolExecutor startThreads(List<String> distinctWordsWithEnoughOccurrences, List<Map<String, List<String>>> resultMaps) {
        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        for (int i = 0; i < distinctWordsWithEnoughOccurrences.size(); i = i + BULK_SIZE) {
            int start = i;
            int end = i + BULK_SIZE < distinctWordsWithEnoughOccurrences.size() ? i + BULK_SIZE : distinctWordsWithEnoughOccurrences.size();
            final List<String> subWordList = distinctWordsWithEnoughOccurrences.subList(start, end);
            final int wordIndex = i;
            pool.execute(new BulkParseCommand(contextlessMorphologicParser, subWordList, wordIndex, false, resultMaps.get(i / BULK_SIZE)));
        }
        return pool;
    }

    private List<Map<String, List<String>>> buildResultMaps(List<String> distinctWordsWithEnoughOccurrences) {
        final List<Map<String, List<String>>> resultMaps = new ArrayList<Map<String, List<String>>>();
        for (int i = 0; i < distinctWordsWithEnoughOccurrences.size() / BULK_SIZE + 1; i++) {
            resultMaps.add(new HashMap<String, List<String>>());
        }
        return resultMaps;
    }

    private List<String> getDistinctWordsWithEnoughOccurrences(ImmutableMultiset<String> wordCountSet) {
        final List<String> uniqueWordsWithEnoughOccurrences = new ArrayList<String>();
        for (String surface : wordCountSet.elementSet()) {
            int count = wordCountSet.count(surface);
            if (count > 5)
                uniqueWordsWithEnoughOccurrences.add(surface);
        }
        return uniqueWordsWithEnoughOccurrences;
    }

    private ImmutableMultiset<String> getWordCountSet(List<String> words) {
        final Multiset<String> wordSet = HashMultiset.create(words);
        return Multisets.copyHighestCountFirst(wordSet);
    }

    private ArrayList<String> getDistinctWords(List<String> words) {
        final HashSet<String> uniqueWordsSet = new HashSet<String>();
        for (String word : words) {
            uniqueWordsSet.add(word);
        }
        return Lists.newArrayList(uniqueWordsSet);
    }

    private List<String> getAllWords() throws IOException {
        final List<File> files = Arrays.asList(
//                new File(AppProperties.oneMillionSentencesFolder() + "/tbmm_tokenized_half.txt")
                new File(AppProperties.oneMillionSentencesFolder() + "/tbmm_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/ntvmsnbc_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/radikal_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/zaman_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/milliyet-sondakika_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/tbmm_b0241h_tokenized.txt")
        );

        final List<String> words = new ArrayList<String>();
        for (File tokenizedFile : files) {
            final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
            for (String line : lines) {
                final ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
                words.addAll(strings);
            }
        }
        return words;
    }

    private Map<String, Integer> buildTotalAmbiguityMap(ImmutableMultiset<String> wordCountSet, Map<String, List<String>> resultMap) {
        HashMap<String, Integer> totalAmbiguityMap = new HashMap<String, Integer>();

        for (String word : wordCountSet) {
            int occurrenceCount = wordCountSet.count(word);
            List<String> results = resultMap.get(word);
            if (results != null)
                totalAmbiguityMap.put(word, occurrenceCount * results.size());
        }

        return totalAmbiguityMap;
    }

    private Map<String, List<String>> mergeResultMap(List<Map<String, List<String>>> resultMaps) {
        HashMap<String, List<String>> mergedMap = new HashMap<String, List<String>>();
        for (Map<String, List<String>> resultMap : resultMaps) {
            for (Map.Entry<String, List<String>> stringListEntry : resultMap.entrySet()) {
                mergedMap.put(stringListEntry.getKey(), stringListEntry.getValue());
            }
        }
        return mergedMap;
    }

    private static class BulkParseCommand implements Runnable {
        private final MorphologicParser parser;
        private final List<String> subWordList;
        private final int wordIndex;
        private boolean printUnparseable;
        final Map<String, List<String>> resultMap;

        private BulkParseCommand(final MorphologicParser parser, final List<String> subWordList, final int wordIndex, boolean printUnparseable, Map<String, List<String>> resultMap) {
            this.parser = parser;
            this.subWordList = subWordList;
            this.wordIndex = wordIndex;
            this.printUnparseable = printUnparseable;
            this.resultMap = resultMap;
        }

        @Override
        public void run() {
            final List<List<MorphemeContainer>> results = parser.parseAllStr(subWordList);

            System.out.println("Finished " + wordIndex);


            for (int i = 0; i < results.size(); i++) {
                String surface = subWordList.get(i);
                List<MorphemeContainer> result = results.get(i);
                if (result.size() > 1)
                    resultMap.put(surface, MorphemeContainerFormatter.formatMorphemeContainers(result));

                if (printUnparseable && result.isEmpty()) {
                    System.out.println("Word is not parsable " + surface);
                }

            }
        }
    }

    private static enum Feature {
        LENGTH {
            @Override
            public int compute(String surface, List<String> results, ImmutableMultiset<String> wordCountSet) {
                return surface.length();
            }
        },
        VOWEL_COUNT {
            @Override
            public int compute(String surface, List<String> results, ImmutableMultiset<String> wordCountSet) {
                int c = 0;
                final TurkishSequence sequence = new TurkishSequence(surface);
                for (TurkishChar turkishChar : sequence.getChars()) {
                    if (turkishChar.getLetter().isVowel())
                        c++;
                }
                return c;
            }
        },
        PARSE_RESULT_COUNT {
            @Override
            public int compute(String surface, List<String> results, ImmutableMultiset<String> wordCountSet) {
                return results.size();
            }
        },
        OCCURRENCE_COUNT {
            @Override
            public int compute(String surface, List<String> results, ImmutableMultiset<String> wordCountSet) {
                return wordCountSet.count(surface);
            }
        },;

        public abstract int compute(String surface, List<String> results, ImmutableMultiset<String> wordCountSet);
    }
}
