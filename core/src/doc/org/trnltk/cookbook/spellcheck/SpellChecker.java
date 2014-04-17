package org.trnltk.cookbook.spellcheck;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.trnltk.model.lexicon.Root;
import org.trnltk.morphology.contextless.parser.CachingMorphologicParser;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.cache.SimpleOfflineCache;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.PhoneticAttributeSets;
import org.trnltk.morphology.contextless.parser.SuffixFormGraph;
import org.trnltk.morphology.contextless.parser.SuffixFormGraphExtractor;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SpellChecker {

    private final MorphologicParser morphologicParser;

    public SpellChecker() {
        this(null);
    }

    public SpellChecker(Collection<Tolerance> toleranceValues) {
        if (toleranceValues == null)
            this.morphologicParser = this.buildCachingParser(CollectionUtils.EMPTY_COLLECTION);
        else
            this.morphologicParser = this.buildCachingParser(toleranceValues);
    }

    public boolean spellCheck(String word) {
        return !morphologicParser.parseStr(word).isEmpty();
    }

    private MorphologicParser buildCachingParser(Collection<Tolerance> toleranceValues) {
        final SimpleOfflineCache cache = SimpleOfflineCache.forTop2kWords();
        final MorphologicParser delegate = buildBaseParser(toleranceValues);
        return new CachingMorphologicParser(cache, delegate, true);
    }

    private MorphologicParser buildBaseParser(Collection<Tolerance> toleranceValues) {
        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);
        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();

        final SuffixGraph suffixGraph = buildSuffixGraph(toleranceValues);

        final HashMultimap<String, ? extends Root> dictionaryRootMap = buildDictionaryRootMap(toleranceValues);

        final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, dictionaryRootMap, suffixApplier);
        predefinedPaths.initialize();

        final RootFinderChain rootFinderChain = buildRootFinderChain(toleranceValues, dictionaryRootMap);

        final SuffixFormGraphExtractor suffixFormGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);
        final SuffixFormGraph suffixFormGraph = suffixFormGraphExtractor.extract(suffixGraph);

        final MorphologicParser parser =
                new ContextlessMorphologicParser(suffixFormGraph, predefinedPaths, rootFinderChain, suffixApplier);

        return parser;
    }

    private HashMultimap<String, ? extends Root> buildDictionaryRootMap(Collection<Tolerance> toleranceValues) {
        final HashMultimap<String, ? extends Root> dictionaryRootMap;
        if (toleranceValues.contains(Tolerance.ALLOW_CONVERSION_OF_CIRCUMFLEXES))
            dictionaryRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
        else
            dictionaryRootMap = RootMapFactory.createSimpleWithNumbers();
        return dictionaryRootMap;
    }

    private SuffixGraph buildSuffixGraph(Collection<Tolerance> toleranceValues) {
        SuffixGraph suffixGraph = new NumeralSuffixGraph(new BasicSuffixGraph());

        if (toleranceValues.contains(Tolerance.ALLOW_PROPER_NOUNS))
            suffixGraph = new ProperNounSuffixGraph(suffixGraph);

        suffixGraph = new CopulaSuffixGraph(suffixGraph);

        suffixGraph.initialize();

        return suffixGraph;
    }

    private RootFinderChain buildRootFinderChain(Collection<Tolerance> toleranceValues, HashMultimap<String, ? extends Root> dictionaryRootMap) {
        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(dictionaryRootMap);
        final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
        final PuncRootFinder puncRootFinder = new PuncRootFinder();

        final BruteForceCompoundNounRootFinder bruteForceCompoundNounRootFinder = new BruteForceCompoundNounRootFinder();
        final BruteForceNounRootFinder bruteForceNounRootFinder = new BruteForceNounRootFinder();
        final BruteForceVerbRootFinder bruteForceVerbRootFinder = new BruteForceVerbRootFinder();

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());

        rootFinderChain
                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED);
        if (toleranceValues.contains(Tolerance.ALLOW_PROPER_NOUNS))
            rootFinderChain.offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                    .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        rootFinderChain.offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        if (toleranceValues.contains(Tolerance.ALLOW_NON_DICTIONARY_NOUN_COMPOUNDS))
            rootFinderChain.offer(bruteForceCompoundNounRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);
        if (toleranceValues.contains(Tolerance.ALLOW_NON_DICTIONARY_NOUNS))
            rootFinderChain.offer(bruteForceNounRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);
        if (toleranceValues.contains(Tolerance.ALLOW_NON_DICTIONARY_VERBS))
            rootFinderChain.offer(bruteForceVerbRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);
        return rootFinderChain;
    }

    public static void main(String[] args) {
        final SpellChecker[] spellCheckers = new SpellChecker[]{
                new SpellChecker(null),      // no tolerance
                new SpellChecker(Arrays.asList(Tolerance.ALLOW_PROPER_NOUNS)),
                new SpellChecker(Arrays.asList(Tolerance.ALLOW_CONVERSION_OF_CIRCUMFLEXES)),
                new SpellChecker(Arrays.asList(Tolerance.ALLOW_NON_DICTIONARY_VERBS, Tolerance.ALLOW_NON_DICTIONARY_NOUNS)),
        };

        final ArrayList<String> words = Lists.newArrayList(
                "elma",
                "elmalar",
                "elmaler",
                "ahmet",
                "Ahmet'in",
                "ahmetin",          // to demonstrate Tolerance.ALLOW_PROPER_NOUNS
                "itaatkârlık",
                "itaatkarlık",      // to demonstrate Tolerance.ALLOW_CONVERSION_OF_CIRCUMFLEXES
                "zaptım",           // made-up word to demonstrate Tolerance.ALLOW_NON_DICTIONARY_VERBS
                "dejlmiş"           // made-up word to demonstrate Tolerance.ALLOW_NON_DICTIONARY_NOUNS
        );

        for (int i = 0; i < spellCheckers.length; i++) {
            SpellChecker spellChecker = spellCheckers[i];
            System.out.println("Checking with spellChecker #" + i);
            for (String word : words) {
                System.out.printf("%13s\t%5s\n", word, String.valueOf(spellChecker.spellCheck(word)));
            }
            System.out.println("\n\n");
        }

    }

}
