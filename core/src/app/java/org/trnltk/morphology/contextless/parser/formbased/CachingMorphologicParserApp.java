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


import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.Files;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.trnltk.app.App;
import org.trnltk.app.AppProperties;
import org.trnltk.app.AppRunner;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.CachingMorphologicParser;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.cache.LRUMorphologicParserCache;
import org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache;
import org.trnltk.morphology.contextless.parser.cache.TwoLevelMorphologicParserCache;
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
            final List<String> subWordList = words.subList(start, end);
            final int wordIndex = i;
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
        final List<File> files = Arrays.asList(
                new File(AppProperties.oneMillionSentencesFolder() + "/tbmm_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/ntvmsnbc_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/radikal_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/zaman_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/milliyet-sondakika_tokenized.txt")
        );

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

        if (1 == 1)
            return;

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
    public void parse8MWords_withOfflineAnalysis() throws Exception {
        final List<File> files = Arrays.asList(
                new File(AppProperties.oneMillionSentencesFolder() + "/tbmm_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/ntvmsnbc_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/radikal_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/zaman_tokenized.txt"),
                new File(AppProperties.oneMillionSentencesFolder() + "/milliyet-sondakika_tokenized.txt")
        );

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
                final List<String> wordsToUseInCache = findWordsToUseInCache(words);
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

    private List<String> findWordsToUseInCache(List<String> words) {
        final Multiset<String> wordSet = HashMultiset.create(words);
        final ImmutableMultiset<String> orderedWordSet = Multisets.copyHighestCountFirst(wordSet);
        final List<String> wordsWithMultipleOccurrence = new ArrayList<String>();
        long multipleOccurrenceCount = 0L;
        for (String word : orderedWordSet.elementSet()) {
            final int count = orderedWordSet.count(word);
            if (count < 2)
                break;
            wordsWithMultipleOccurrence.add(word);
            multipleOccurrenceCount += count;
        }

        System.out.println("Number of words that have multiple occurrence : " + wordsWithMultipleOccurrence.size());
        System.out.println("Total occurrence count of them : " + multipleOccurrenceCount + " which is " + (Long.valueOf(multipleOccurrenceCount).doubleValue() / Integer.valueOf(words.size()).doubleValue() * 100.0) + " % of total");

//        int N = 100;
//        System.out.println("First " + N + "words with multiple occurrence:");
//        for (int i = 0; i < N; i++) {
//            String word = wordsWithMultipleOccurrence.get(i);
//            final int count = orderedWordSet.count(word);
//            System.out.println(word + "\t\t : " + count + " which is " + (Long.valueOf(count).doubleValue() / Integer.valueOf(words.size()).doubleValue() * 100.0) + " % of total");
//        }

        double ratio = 0.75;

        final List<String> wordsToUse = new LinkedList<String>();
        int occurrencesSoFar = 0;
        for (int i = 0; i < wordsWithMultipleOccurrence.size(); i++) {
            final String word = wordsWithMultipleOccurrence.get(i);
            final int count = orderedWordSet.count(word);

            wordsToUse.add(word);

            occurrencesSoFar += count;
            if (occurrencesSoFar > (words.size() * ratio))
                break;
        }

        System.out.println("Found " + wordsToUse.size() + " words which are " + 100.0 * Integer.valueOf(occurrencesSoFar).doubleValue() / Integer.valueOf(words.size()).doubleValue() + " % of all words");

        return wordsToUse;

    }

    private static class BulkParseCommand implements Runnable {
        private final MorphologicParser parser;
        private final List<String> subWordList;
        private final int wordIndex;
        private boolean printUnparseable;

        private BulkParseCommand(final MorphologicParser parser, final List<String> subWordList, final int wordIndex, boolean printUnparseable) {
            this.parser = parser;
            this.subWordList = subWordList;
            this.wordIndex = wordIndex;
            this.printUnparseable = printUnparseable;
        }

        @Override
        public void run() {
            final List<List<MorphemeContainer>> results = parser.parseAllStr(subWordList);

            System.out.println("Finished " + wordIndex);

            if (printUnparseable) {
                for (int i = 0; i < results.size(); i++) {
                    List<MorphemeContainer> result = results.get(i);
                    if (result.isEmpty())
                        System.out.println("Word is not parsable " + subWordList.get(i));
                }
            }
        }
    }

    private static class SingleParseCommand implements Runnable {
        private final MorphologicParser parser;
        private final String word;
        private final int wordIndex;
        private boolean printUnparseable;

        private SingleParseCommand(final MorphologicParser parser, final String word, final int wordIndex, boolean printUnparseable) {
            this.parser = parser;
            this.word = word;
            this.wordIndex = wordIndex;
            this.printUnparseable = printUnparseable;
        }

        @Override
        public void run() {
            final List<MorphemeContainer> morphemeContainers = parser.parseStr(word);
            if (printUnparseable) {
                if (morphemeContainers.isEmpty())
                    System.out.println("Word is not parsable " + word);
            }
            if (wordIndex % 500 == 0)
                System.out.println("Finished " + wordIndex);
        }
    }

}
