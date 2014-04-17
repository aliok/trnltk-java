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

package bruteforce;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.Files;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.PhoneticAttributeSets;
import org.trnltk.morphology.contextless.parser.SuffixFormGraph;
import org.trnltk.morphology.contextless.parser.SuffixFormGraphExtractor;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.lexicon.CircumflexConvertingRootGenerator;
import org.trnltk.morphology.lexicon.DictionaryLoader;
import org.trnltk.morphology.lexicon.RootMapGenerator;
import org.trnltk.model.lexicon.Lexeme;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BruteForceExperiments {
    private ContextlessMorphologicParser parser;

    @Before
    public void before() {
        final CopulaSuffixGraph copulaSuffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));

        // dictionary is only read for predefined paths! dictionary words are not used while root finding
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        final CircumflexConvertingRootGenerator rootGenerator = new CircumflexConvertingRootGenerator();
        final Collection<? extends Root> roots = rootGenerator.generateAll(lexemes);
        final Multimap<String, ? extends Root> rootMap = new RootMapGenerator().generate(roots);

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);
        final PredefinedPaths predefinedPaths = new PredefinedPaths(copulaSuffixGraph, rootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));

        final BruteForceCompoundNounRootFinder bruteForceCompoundNounRootFinder = new BruteForceCompoundNounRootFinder();
        final BruteForceNounRootFinder bruteForceNounRootFinder = new BruteForceNounRootFinder();
        final BruteForceVerbRootFinder bruteForceVerbRootFinder = new BruteForceVerbRootFinder();

        copulaSuffixGraph.initialize();
        predefinedPaths.initialize();

        // create common phonetic and morphotactic parts
        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();

        // following is to extract a form-based graph from a suffix-based graph
        final SuffixFormGraphExtractor suffixFormGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);

        // extract the formBasedGraph
        final SuffixFormGraph suffixFormGraph = suffixFormGraphExtractor.extract(copulaSuffixGraph);

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());
        rootFinderChain
                .offer(bruteForceCompoundNounRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(bruteForceNounRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(bruteForceVerbRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        parser = new ContextlessMorphologicParser(suffixFormGraph, predefinedPaths, rootFinderChain, suffixApplier);
    }

    @Test
    public void shouldParseTbmmJournal_b0241h() throws IOException {
        final File tokenizedFile = new File("core/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
        final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
        final LinkedList<String> words = new LinkedList<String>();
        for (String line : lines) {
            words.addAll(Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line)));
        }

        final StopWatch stopWatch = new StopWatch();
        int parseResultCount = 0;
        final int MAX_WORD_LENGTH = 100;

        int[] wordCountsByLength = new int[MAX_WORD_LENGTH];
        int[] parseResultCountTotalsByTokenLength = new int[MAX_WORD_LENGTH];

        stopWatch.start();
        stopWatch.suspend();

        for (String word : words) {
            stopWatch.resume();
            final LinkedList<MorphemeContainer> morphemeContainers = parser.parse(new TurkishSequence(word));
            stopWatch.suspend();
            if (morphemeContainers.isEmpty())
                System.out.println("Word is not parsable " + word);
            parseResultCount += morphemeContainers.size();
            parseResultCountTotalsByTokenLength[word.length()] += morphemeContainers.size();
            wordCountsByLength[word.length()]++;
        }

        stopWatch.stop();

        final double[] parseResultCountAvgsByLength = new double[MAX_WORD_LENGTH];
        for (int i = 0; i < parseResultCountTotalsByTokenLength.length; i++) {
            int totalParseResultCount = parseResultCountTotalsByTokenLength[i];
            final int wordCount = wordCountsByLength[i];
            parseResultCountAvgsByLength[i] = Double.valueOf(totalParseResultCount) / Double.valueOf(wordCount);
        }

        System.out.println("Total time :" + stopWatch.toString());
        System.out.println("Nr of tokens : " + words.size());
        System.out.println("Nr of parse results : " + parseResultCount);
        System.out.println("Avg time : " + (stopWatch.getTime() * 1.0d) / (words.size() * 1.0d) + " ms");
        System.out.println("Avg parse result count : " + (parseResultCount * 1.0) / (words.size() * 1.0));
        System.out.println("Word counts by token length " + "\n\t" + Arrays.toString(wordCountsByLength));
        System.out.println("Parse result count totals by token length " + "\n\t" + Arrays.toString(parseResultCountTotalsByTokenLength));
        System.out.println("Parse result count avgs by token length " + "\n\t" + Arrays.toString(parseResultCountAvgsByLength));
    }

    @Test
    public void shouldParseTbmmJournal_b0241h_andCreateRootHistogram() throws IOException {
        final File tokenizedFile = new File("core/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
        final List<String> lines = Files.readLines(tokenizedFile, Charsets.UTF_8);
        final LinkedList<String> words = new LinkedList<String>();
        for (String line : lines) {
            words.addAll(Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(line)));
        }

        final HashMultiset<Root> roots = HashMultiset.create(words.size() * 4);
        final HashMultiset<String> lemmas = HashMultiset.create(words.size() * 4);

        for (String word : words) {
            final LinkedList<MorphemeContainer> morphemeContainers = parser.parse(new TurkishSequence(word));
            if (morphemeContainers.isEmpty())
                System.out.println("Word is not parsable " + word);

            final List<Root> rootsForWord = Lists.transform(morphemeContainers, new Function<MorphemeContainer, Root>() {
                @Override
                public Root apply(MorphemeContainer input) {
                    return input.getRoot();
                }
            });

            final List<String> lemmasForWord = Lists.transform(morphemeContainers, new Function<MorphemeContainer, String>() {
                @Override
                public String apply(MorphemeContainer input) {
                    return input.getRoot().getLexeme().getLemma();
                }
            });

            roots.addAll(rootsForWord);
            lemmas.addAll(lemmasForWord);

            if (roots.size() > 50000)
                break;
        }

        System.out.println("Found lemma count " + lemmas.size());
        final ImmutableMultiset<String> lemmasSortedByCount = Multisets.copyHighestCountFirst(lemmas);
        for (Multiset.Entry<String> stringEntry : lemmasSortedByCount.entrySet()) {
            final String lemma = stringEntry.getElement();
            final int count = stringEntry.getCount();
            System.out.println(String.format("%5d ", count) + lemma);
        }

        System.out.println("Found root count " + roots.size());
//        final ImmutableMultiset<Root> rootsSortedByCount = Multisets.copyHighestCountFirst(roots);
//        for (Multiset.Entry<Root> rootEntry : rootsSortedByCount.entrySet()) {
//            final Root root = rootEntry.getElement();
//            final int count = rootEntry.getCount();
//            System.out.println(String.format("%5d ", count) + root.toString());
//        }
    }
}