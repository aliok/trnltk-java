///*
//* Copyright  2013  Ali Ok (aliokATapacheDOTorg)
//*
//*  Licensed under the Apache License, Version 2.0 (the "License");
//*  you may not use this file except in compliance with the License.
//*  You may obtain a copy of the License at
//*
//*     http://www.apache.org/licenses/LICENSE-2.0
//*
//*  Unless required by applicable law or agreed to in writing, software
//*  distributed under the License is distributed on an "AS IS" BASIS,
//*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//*  See the License for the specific language governing permissions and
//*  limitations under the License.
//*/
//
//package org.trnltk.morphology.contextless.parser.reducedambiguity;
//
//
//import com.google.common.base.Charsets;
//import com.google.common.base.Joiner;
//import com.google.common.base.Splitter;
//import com.google.common.collect.*;
//import com.google.common.io.Files;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.time.StopWatch;
//import org.junit.Before;
//import org.junit.Test;
//import org.trnltk.model.letter.TurkishChar;
//import org.trnltk.model.letter.TurkishSequence;
//import org.trnltk.model.lexicon.Root;
//import org.trnltk.model.morpheme.MorphemeContainer;
//import org.trnltk.morphology.contextless.parser.MorphologicParser;
//import org.trnltk.morphology.contextless.parser.PredefinedPaths;
//import org.trnltk.morphology.contextless.parser.SuffixApplier;
//import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParser;
//import org.trnltk.morphology.contextless.parser.formbased.PhoneticAttributeSets;
//import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraph;
//import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraphExtractor;
//import org.trnltk.morphology.contextless.rootfinder.*;
//import org.trnltk.morphology.lexicon.RootMapFactory;
//import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
//import org.trnltk.morphology.morphotactics.SuffixGraph;
//import org.trnltk.morphology.morphotactics.reducedambiguity.BasicRASuffixGraph;
//import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
//import org.trnltk.morphology.phonetics.PhoneticsEngine;
//import org.trnltk.util.MorphemeContainerFormatter;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
//* Requires a lot of memory!
//* Make sure you set properly.
//* <p/>
//* I used -Xms3512M -Xmx6072M and worked good with max L1 cache size of 200000
//*/
//// poor design of the test class....
//public class UniqueWordsGrowTest {
//
//    @Test
//    public void parse8MWords_withOfflineAnalysis() throws Exception {
//        final StopWatch completeProcessStopWatch = new StopWatch();
//        completeProcessStopWatch.start();
//
//        final List<String> words = getAllWords();
//        final ArrayList<String> distinctWords = getDistinctWords(words);
//        final ImmutableMultiset<String> wordCountSet = getWordCountSet(words);
//
//        final List<String> distinctWordsWithEnoughOccurrences = getDistinctWordsWithEnoughOccurrences(wordCountSet);
//
//        System.out.println("Number of words : " + words.size());
//        System.out.println("Number of distinct words : " + distinctWords.size());
//        System.out.println("Number of distinct words with enough occurrences : " + distinctWordsWithEnoughOccurrences.size());
//        System.out.println("======================");
//
//        final List<Map<String, List<String>>> resultMaps = buildResultMaps(distinctWordsWithEnoughOccurrences);
//
//        final StopWatch parseStopWatch = new StopWatch();
//        parseStopWatch.start();
//
//        final ThreadPoolExecutor pool = startThreads(distinctWordsWithEnoughOccurrences, resultMaps);
//        waitUntilThreadPoolToTerminate(pool);
//
//        parseStopWatch.stop();
//
//        System.out.println("Total time :" + parseStopWatch.toString());
//        System.out.println("Nr of tokens : " + words.size());
//        System.out.println("Nr of unique tokens : " + distinctWords.size());
//        System.out.println("Nr of unique tokens with enough occurrences: " + distinctWordsWithEnoughOccurrences.size());
//        System.out.println("Avg time : " + (parseStopWatch.getTime() * 1.0d) / (distinctWordsWithEnoughOccurrences.size() * 1.0d) + " ms");
//
//        final Map<String, List<String>> resultMap = mergeResultMap(resultMaps);
//        final Map<String, Integer> totalAmbiguityMap = buildTotalAmbiguityMap(wordCountSet, resultMap);
//        final SortedMap<String, Integer> sortedTotalAmbiguityMap = buildSortedTotalAmbiguityMap(totalAmbiguityMap);
//
//        final File outputFile = new File("F:\\vms\\virtualbox\\shared-folder\\ambiguity_data.txt");
//        BufferedWriter bufferedWriter = null;
//        try{
//            bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
//            dumpGraphData(bufferedWriter, wordCountSet, distinctWordsWithEnoughOccurrences, resultMap, sortedTotalAmbiguityMap);
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            if(bufferedWriter!=null)
//                bufferedWriter.close();
//        }
//
//        completeProcessStopWatch.stop();
//        System.out.println("It took " + completeProcessStopWatch.toString());
//    }
//
//    private List<Map<String, List<String>>> buildResultMaps(List<String> distinctWordsWithEnoughOccurrences) {
//        final List<Map<String, List<String>>> resultMaps = new ArrayList<Map<String, List<String>>>();
//        for (int i = 0; i < distinctWordsWithEnoughOccurrences.size() / BULK_SIZE + 1; i++) {
//            resultMaps.add(new HashMap<String, List<String>>());
//        }
//        return resultMaps;
//    }
//
//    private List<String> getDistinctWordsWithEnoughOccurrences(ImmutableMultiset<String> wordCountSet) {
//        final List<String> uniqueWordsWithEnoughOccurrences = new ArrayList<String>();
//        for (String surface : wordCountSet.elementSet()) {
//            int count = wordCountSet.count(surface);
//            if (count > 5)
//                uniqueWordsWithEnoughOccurrences.add(surface);
//        }
//        return uniqueWordsWithEnoughOccurrences;
//    }
//
//    private ImmutableMultiset<String> getWordCountSet(List<String> words) {
//        final Multiset<String> wordSet = HashMultiset.create(words);
//        return Multisets.copyHighestCountFirst(wordSet);
//    }
//
//    private ArrayList<String> getDistinctWords(List<String> words) {
//        final HashSet<String> uniqueWordsSet = new HashSet<String>();
//        for (String word : words) {
//            uniqueWordsSet.add(word);
//        }
//        return Lists.newArrayList(uniqueWordsSet);
//    }
//
//    private List<String> getAllWords() throws IOException {
//        final List<File> files = Arrays.asList(
////                new File("D:\\devl\\data\\1MSentences\\tbmm_tokenized_half.txt")
//                new File("D:\\devl\\data\\1MSentences\\tbmm_tokenized.txt"),
//                new File("D:\\devl\\data\\1MSentences\\ntvmsnbc_tokenized.txt"),
//                new File("D:\\devl\\data\\1MSentences\\radikal_tokenized.txt"),
//                new File("D:\\devl\\data\\1MSentences\\zaman_tokenized.txt"),
//                new File("D:\\devl\\data\\1MSentences\\milliyet-sondakika_tokenized.txt"),
//                new File("core/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt")
//        );
//
//        final List<String> words = new ArrayList<String>();
//        for (File tokenizedFile : files) {
//            final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
//            for (String line : lines) {
//                final ArrayList<String> strings = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line));
//                words.addAll(strings);
//            }
//        }
//        return words;
//    }
//
//}
