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

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.suffixbased.cache.LRUMorphologicParserCache;
import org.trnltk.morphology.contextless.parser.suffixbased.cache.MorphologicParserCache;
import org.trnltk.morphology.contextless.parser.suffixbased.cache.TwoLevelMorphologicParserCache;
import org.trnltk.morphology.lexicon.RootMapFactory;

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
public class CachingMorphologicParserApp {

    private static final int BULK_SIZE = 500;
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
        this.contextlessMorphologicParser = ContextlessMorphologicParserFactory.createWithBigGraphForRootMap(rootMap);
    }

    @Test
    public void parseTbmmJournal_b0241h_noBulkParse() throws Exception {
        final File tokenizedFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
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

    @Test
    public void parseTbmmJournal_b0241h_withBulkParse() throws Exception {
        final File tokenizedFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
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

    @Test
    public void parse8MWords() throws Exception {
        final List<File> files = Arrays.asList(
                new File("D:\\devl\\data\\1MSentences\\tbmm_tokenized.txt"),
                new File("D:\\devl\\data\\1MSentences\\ntvmsnbc_tokenized.txt"),
                new File("D:\\devl\\data\\1MSentences\\radikal_tokenized.txt"),
                new File("D:\\devl\\data\\1MSentences\\zaman_tokenized.txt"),
                new File("D:\\devl\\data\\1MSentences\\milliyet-sondakika_tokenized.txt")
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
            final List<List<MorphemeContainer>> results = parser.parseAll(Lists.transform(subWordList, new Function<String, TurkishSequence>() {
                @Override
                public TurkishSequence apply(String input) {
                    return new TurkishSequence(input);
                }
            }));

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
            final List<MorphemeContainer> morphemeContainers = parser.parse(new TurkishSequence(word));
            if (printUnparseable) {
                if (morphemeContainers.isEmpty())
                    System.out.println("Word is not parsable " + word);
            }
            if (wordIndex % 500 == 0)
                System.out.println("Finished " + wordIndex);
        }
    }

}
