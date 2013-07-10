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
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.trnltk.app.App;
import org.trnltk.app.AppRunner;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.CachingMorphologicParser;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.suffixbased.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.suffixbased.SuffixApplier;
import org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.testutil.testmatchers.BaseParseResultsMatcher;
import org.trnltk.util.MorphemeContainerFormatter;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Doesn't produce real YAML. Needs escaping etc.
 */
@RunWith(AppRunner.class)
public class FolderContextlessMorphologicParsingApp {

    private static final int NUMBER_OF_THREADS = 8;


    private MorphologicParser contextlessMorphologicParser;
    private HashMultimap<String, ? extends Root> originalRootMap;

    public FolderContextlessMorphologicParsingApp() {
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

        final RootFinderChain rootFinderChain = new RootFinderChain();
        rootFinderChain
                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);


        final PredefinedPaths predefinedPaths = new PredefinedPaths(copulaSuffixGraph, rootMap, suffixApplier);
        predefinedPaths.initialize();

        this.contextlessMorphologicParser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPaths, rootFinderChain, suffixApplier);
    }


    @App
    public void parse8MWords_withOfflineAnalysis() throws Exception {
        final File folder = new File("D:\\devl\\data\\1MSentences");

        final List<File> files = new ArrayList<File>();

        for (File file : folder.listFiles()) {
            if (file.getName().endsWith("_tokenized.txt"))
                files.add(file);
        }

        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (File file : files) {
            final File targetFile = new File(file.getParent(), file.getName().substring(0, file.getName().length() - "_tokenized.txt".length()) + "_parsed.txt");
            final FileParseCommand fileParseCommand = new FileParseCommand(contextlessMorphologicParser, file, targetFile, false);
            pool.execute(fileParseCommand);
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        }

        stopWatch.stop();

        System.out.println("Total time :" + stopWatch.toString());
    }

    @App
    public void splitResultFiles() throws IOException {
        // ignore IOExceptions

        final File folder = new File("D:\\devl\\data\\1MSentences");

        final List<File> files = new ArrayList<File>();

        for (File file : folder.listFiles()) {
            if (file.getName().endsWith("_parsed.txt"))
                files.add(file);
        }

        int wordsForEachFile = 30000;

        for (File file : files) {
            int wordCount = 0;
            int fileCount = 0;
            final BufferedReader reader = Files.newReader(file, Charsets.UTF_8);
            BufferedWriter writer = null;
            do {
                final String line = reader.readLine();
                if (line.startsWith("- word:")) {
                    if (wordCount % wordsForEachFile == 0) {
                        if (writer != null)
                            writer.close();

                        final String srcFileName = file.getName();
                        final File targetFile = new File(file.getParent() + "\\split", srcFileName + "." + String.format("%03d", fileCount));
                        writer = new BufferedWriter(new FileWriter(targetFile));
                        fileCount++;
                    }
                    wordCount++;
                }

                writer.write(line + "\n");
            } while (reader.ready());

            if (writer != null)
                writer.close();
        }
    }

    private static class FileParseCommand implements Runnable {
        private final MorphologicParser parser;
        private final File sourceFile;
        private final File targetFile;
        private boolean printUnparseable;

        private FileParseCommand(final MorphologicParser parser, final File sourceFile, final File targetFile, boolean printUnparseable) {
            this.parser = parser;
            this.sourceFile = sourceFile;
            this.targetFile = targetFile;
            this.printUnparseable = printUnparseable;
        }

        @Override
        public void run() {
            BufferedWriter writer = null;

            try {
                final List<String> words = new ArrayList<String>();
                final HashSet<String> uniqueWords = new HashSet<String>();

                final List<String> lines = Files.readLines(sourceFile, Charsets.UTF_8);

                for (String line : lines) {
                    final ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
                    words.addAll(strings);
                    uniqueWords.addAll(strings);
                }

                System.out.println("Number of words : " + words.size());
                System.out.println("Number of unique words : " + uniqueWords.size());
                System.out.println("======================");

                final MorphologicParserCache staticCache = new MorphologicParserCache() {

                    private ImmutableMap<String, List<MorphemeContainer>> cacheMap;

                    {
                        final ImmutableMap.Builder<String, List<MorphemeContainer>> builder = new ImmutableMap.Builder<String, List<MorphemeContainer>>();
                        final List<String> wordsToUseInCache = findWordsToUseInCache(words);
                        for (String word : wordsToUseInCache) {
                            builder.put(word, parser.parseStr(word));
                        }
                        this.cacheMap = builder.build();
                    }

                    @Override
                    public List<MorphemeContainer> get(String input) {
                        return this.cacheMap.get(input);
                    }

                    @Override
                    public void put(String input, List<MorphemeContainer> morphemeContainers) {
                    }

                    @Override
                    public void putAll(Map<String, List<MorphemeContainer>> map) {
                        // do nothing
                    }
                };

                final CachingMorphologicParser cachingMorphologicParser = new CachingMorphologicParser(staticCache, parser, false);

                writer = new BufferedWriter(new FileWriter(targetFile));

                int i = 0;
                for (String word : words) {
                    final List<MorphemeContainer> results = cachingMorphologicParser.parseStr(word);
                    if (CollectionUtils.isEmpty(results) && printUnparseable) {
                        System.out.println("Word is not parsable " + word);
                    }

                    /*
                    - word: elma
                      results:
                        - elma+Noun+...
                        - elma+Noun+...

                     */

                    final List<String> resultStrs = Lists.newArrayList(Lists.transform(results, new Function<MorphemeContainer, String>() {
                        @Override
                        public String apply(MorphemeContainer input) {
                            return MorphemeContainerFormatter.formatMorphemeContainerDetailed(input);
                        }
                    }));

                    Collections.sort(resultStrs, BaseParseResultsMatcher.parseResultOrdering);

                    writer.append("- word: " + word + "\n");
                    writer.append("  results:\n");
                    for (String resultStr : resultStrs) {
                        writer.append("    - " + resultStr + "\n");
                    }

                    if (++i % 1000 == 0)
                        System.out.println("Finished\t" + i + "\t " + sourceFile);

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (writer != null)
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
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
    }

}
