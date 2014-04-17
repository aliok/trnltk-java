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

package org.trnltk.apps.morphology.contextless.parser;


import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.trnltk.apps.analysis.FrequentWordAnalysis;
import org.trnltk.apps.commands.BulkParseCommand;
import org.trnltk.apps.commands.SingleParseCommand;
import org.trnltk.apps.commons.App;
import org.trnltk.apps.commons.AppRunner;
import org.trnltk.apps.commons.LoggingSettings;
import org.trnltk.apps.commons.SampleFiles;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.CachingMorphologicParser;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.cache.LRUMorphologicParserCache;
import org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache;
import org.trnltk.morphology.contextless.parser.cache.TwoLevelMorphologicParserCache;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.PhoneticAttributeSets;
import org.trnltk.morphology.contextless.parser.SuffixFormGraph;
import org.trnltk.morphology.contextless.parser.SuffixFormGraphExtractor;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Requires a lot of memory!
 * Make sure you set properly.
 * <p/>
 * I used -Xms3512M -Xmx6072M and worked good with max L1 cache size of 200000
 */
@RunWith(AppRunner.class)
public class CachingMorphologicParserApp {

    private static final int BULK_SIZE = 1500;
    private static final int NUMBER_OF_THREADS = 8;
    private static final int INITIAL_L1_CACHE_SIZE = 1000 * 200;
    private static final long MAX_L1_CACHE_SIZE = 1000 * 200;


    private MorphologicParser contextlessMorphologicParser;
    private HashMultimap<String, ? extends Root> originalRootMap;

    public CachingMorphologicParserApp() {
        this.originalRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
    }

    @Before
    public void setUp() throws Exception {
        final HashMultimap<String, Root> rootMap = HashMultimap.create(this.originalRootMap);
        final CopulaSuffixGraph copulaSuffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        copulaSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));

        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(rootMap);
        final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
        final PuncRootFinder puncRootFinder = new PuncRootFinder();


        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();

        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(copulaSuffixGraph);

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());
        rootFinderChain
                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);


        final PredefinedPaths predefinedPaths = new PredefinedPaths(copulaSuffixGraph, rootMap, suffixApplier);
        predefinedPaths.initialize();

        this.contextlessMorphologicParser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPaths, rootFinderChain, suffixApplier);
    }

    @App("Parse sample TBMM Journal w/o bulk parse")
    public void parseTbmmJournal_b0241h_noBulkParse() throws Exception {
        final File tokenizedFile = new File("core/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
        final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
        final LinkedList<String> words = new LinkedList<String>();
        final HashSet<String> uniqueWords = new HashSet<String>();
        for (String line : lines) {
            final ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
            words.addAll(strings);
            uniqueWords.addAll(strings);
        }

        final int initialL1CacheSize = uniqueWords.size();
        final int maxL1CacheSize = initialL1CacheSize;

        final MorphologicParserCache l1Cache = new LRUMorphologicParserCache(NUMBER_OF_THREADS, initialL1CacheSize, maxL1CacheSize);

        final ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        final MorphologicParser[] parsers = new MorphologicParser[NUMBER_OF_THREADS];
        for (int i = 0; i < parsers.length; i++) {
            parsers[i] = new CachingMorphologicParser(
                    new TwoLevelMorphologicParserCache(BULK_SIZE, l1Cache),
                    contextlessMorphologicParser, true);
        }


        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < words.size(); i++) {
            final MorphologicParser parser = parsers[i % NUMBER_OF_THREADS];
            final String word = words.get(i);
            final int wordIndex = i;
            pool.execute(new SingleParseCommand(parser, word, wordIndex, false));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(500, TimeUnit.MILLISECONDS);
        }

        stopWatch.stop();

        System.out.println("Total time :" + stopWatch.toString());
        System.out.println("Nr of tokens : " + words.size());
        System.out.println("Avg time : " + (stopWatch.getTime() * 1.0d) / (words.size() * 1.0d) + " ms");
    }

    @App("Parse sample TBMM Journal with bulk parse")
    public void parseTbmmJournal_b0241h_withBulkParse() throws Exception {
        final File tokenizedFile = new File("core/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
        final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
        final LinkedList<String> words = new LinkedList<String>();
        final HashSet<String> uniqueWords = new HashSet<String>();
        for (String line : lines) {
            final ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
            words.addAll(strings);
            uniqueWords.addAll(strings);
        }

        final int initialL1CacheSize = uniqueWords.size();
        final int maxL1CacheSize = initialL1CacheSize;

        final MorphologicParserCache l1Cache = new LRUMorphologicParserCache(NUMBER_OF_THREADS, initialL1CacheSize, maxL1CacheSize);

        final ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        final MorphologicParser[] parsers = new MorphologicParser[NUMBER_OF_THREADS];
        for (int i = 0; i < parsers.length; i++) {
            parsers[i] = new CachingMorphologicParser(
                    new TwoLevelMorphologicParserCache(BULK_SIZE, l1Cache),
                    contextlessMorphologicParser, true);
        }


        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < words.size(); i = i + BULK_SIZE) {
            final MorphologicParser parser = parsers[(i / BULK_SIZE) % NUMBER_OF_THREADS];
            int start = i;
            int end = i + BULK_SIZE < words.size() ? i + BULK_SIZE : words.size();
            final int wordIndex = i;

            final List<String> subWordList = words.subList(start, end);
            pool.execute(new BulkParseCommand(parser, subWordList, wordIndex, false));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(500, TimeUnit.MILLISECONDS);
        }

        stopWatch.stop();

        System.out.println("Total time :" + stopWatch.toString());
        System.out.println("Nr of tokens : " + words.size());
        System.out.println("Avg time : " + (stopWatch.getTime() * 1.0d) / (words.size() * 1.0d) + " ms");
    }

    @App("Parse all sample corpus. Does not do an offline analysis to add most frequent words to cache in advance.")
    public void parse8MWords() throws Exception {
        /*
         Total time :0:07:29.799
         Nr of tokens : 18362187
         Avg time : 0.024495938310616267 ms
        */
        final Set<File> files = SampleFiles.oneMillionSentencesTokenizedFiles();

        final LinkedList<String> words = new LinkedList<String>();
        final HashSet<String> uniqueWords = new HashSet<String>();

        for (File tokenizedFile : files) {
            final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
            for (String line : lines) {
                final ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
                words.addAll(strings);
                uniqueWords.addAll(strings);
            }
        }

        System.out.println("Number of words : " + words.size());
        System.out.println("Number of unique words : " + uniqueWords.size());
        System.out.println("======================");

        final MorphologicParserCache l1Cache = new LRUMorphologicParserCache(NUMBER_OF_THREADS, INITIAL_L1_CACHE_SIZE, MAX_L1_CACHE_SIZE);

        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        final MorphologicParser[] parsers = new MorphologicParser[NUMBER_OF_THREADS];
        for (int i = 0; i < parsers.length; i++) {
            parsers[i] = new CachingMorphologicParser(
                    new TwoLevelMorphologicParserCache(BULK_SIZE, l1Cache),
                    contextlessMorphologicParser, true);
        }


        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < words.size(); i = i + BULK_SIZE) {
            final MorphologicParser parser = parsers[(i / BULK_SIZE) % NUMBER_OF_THREADS];
            int start = i;
            int end = i + BULK_SIZE < words.size() ? i + BULK_SIZE : words.size();
            final List<String> subWordList = words.subList(start, end);
            final int wordIndex = i;
            pool.execute(new BulkParseCommand(parser, subWordList, wordIndex, false));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        }

        stopWatch.stop();

        System.out.println("Total time :" + stopWatch.toString());
        System.out.println("Nr of tokens : " + words.size());
        System.out.println("Avg time : " + (stopWatch.getTime() * 1.0d) / (words.size() * 1.0d) + " ms");
    }

    @App("Parse all sample corpus. Does an offline analysis to add most frequent words to cache in advance.")
    public void parseWordsOfOneMillionSentences_withOfflineAnalysis() throws Exception {
        /*
        Total time :0:05:27.806
        Nr of tokens : 18362187
        Avg time : 0.01785223078274935 ms
        */
        LoggingSettings.turnOnLogger(LoggingSettings.Piece.FrequentWordAnalysis);

        final Set<File> files = SampleFiles.oneMillionSentencesTokenizedFiles();

        final List<String> words = new ArrayList<String>();
        final HashSet<String> uniqueWords = new HashSet<String>();

        for (File tokenizedFile : files) {
            final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
            for (String line : lines) {
                final ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
                words.addAll(strings);
                uniqueWords.addAll(strings);
            }
        }

        System.out.println("Number of words : " + words.size());
        System.out.println("Number of unique words : " + uniqueWords.size());
        System.out.println("======================");

        final MorphologicParserCache staticCache = new MorphologicParserCache() {

            private ImmutableMap<String, List<MorphemeContainer>> cacheMap;
            private boolean built;

            @Override
            public List<MorphemeContainer> get(String input) {
                return this.cacheMap.get(input);
            }

            @Override
            public void put(String input, List<MorphemeContainer> morphemeContainers) {
                // do nothing
            }

            @Override
            public void putAll(Map<String, List<MorphemeContainer>> map) {
                // do nothing
            }

            @Override
            public void build(MorphologicParser parser) {
                final ImmutableMap.Builder<String, List<MorphemeContainer>> builder = new ImmutableMap.Builder<String, List<MorphemeContainer>>();
                final FrequentWordAnalysis.FrequentWordAnalysisResult result = new FrequentWordAnalysis().run(words, 0.75);

                final List<String> wordsToUseInCache = result.getWordsWithEnoughOccurrences();
                for (String word : wordsToUseInCache) {
                    builder.put(word, contextlessMorphologicParser.parseStr(word));
                }
                this.cacheMap = builder.build();
                this.built = true;
            }

            @Override
            public boolean isBuilt() {
                return this.built;
            }
        };


        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        final MorphologicParser[] parsers = new MorphologicParser[NUMBER_OF_THREADS];
        for (int i = 0; i < parsers.length; i++) {
            parsers[i] = new CachingMorphologicParser(
                    staticCache,
                    contextlessMorphologicParser, true);
        }


        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < words.size(); i = i + BULK_SIZE) {
            final MorphologicParser parser = parsers[(i / BULK_SIZE) % NUMBER_OF_THREADS];
            int start = i;
            int end = i + BULK_SIZE < words.size() ? i + BULK_SIZE : words.size();
            final List<String> subWordList = words.subList(start, end);
            final int wordIndex = i;
            pool.execute(new BulkParseCommand(parser, subWordList, wordIndex, false));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        }

        stopWatch.stop();

        System.out.println("Total time :" + stopWatch.toString());
        System.out.println("Nr of tokens : " + words.size());
        System.out.println("Avg time : " + (stopWatch.getTime() * 1.0d) / (words.size() * 1.0d) + " ms");
    }

}
