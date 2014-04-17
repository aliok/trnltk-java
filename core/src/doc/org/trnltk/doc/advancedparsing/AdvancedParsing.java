package org.trnltk.doc.advancedparsing;

import com.google.common.collect.HashMultimap;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.*;
import org.trnltk.morphology.contextless.parser.cache.LRUMorphologicParserCache;
import org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache;
import org.trnltk.morphology.contextless.parser.cache.TwoLevelMorphologicParserCache;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.List;

public class AdvancedParsing {

    private static final int NUMBER_OF_THREADS = 8;
    private static final int L1_CACHE_INITIAL_SIZE = 20000;
    private static final long L1_CACHE_MAX_SIZE = 100000;
    private static final int L2_CACHE_MAX_SIZE = 1000;

    public static void main(String[] args) {
        MorphologicParser parser1 = buildParser1();
        MorphologicParser parser2 = buildParser2();

        compare("elma", parser1, parser2);
        compare("Ahmet", parser1, parser2);
        compare("elmadır", parser1, parser2);
        compare("Türkiye'ye", parser1, parser2);
        compare("kâtip", parser1, parser2);
        compare("katip", parser1, parser2);

    }

    private static void compare(String surface, MorphologicParser parser1, MorphologicParser parser2) {
        System.out.println("\nSurface " + surface + ":");

        System.out.println("Results from Parser1:");
        List<MorphemeContainer> parser1Results = parser1.parseStr(surface);
        if (parser1Results.isEmpty()) {
            System.out.println("\tNo results found");
        } else {
            for (MorphemeContainer result : parser1Results) {
                System.out.println("\t" + MorphemeContainerFormatter.formatMorphemeContainerWithForms(result));
            }
        }

        List<MorphemeContainer> parser2Results = parser2.parseStr(surface);
        System.out.println("Results from Parser2:");
        if (parser2Results.isEmpty()) {
            System.out.println("\tNo results found");
        } else {
            for (MorphemeContainer result : parser2Results) {
                System.out.println("\t" + MorphemeContainerFormatter.formatMorphemeContainerWithForms(result));
            }
        }
    }

    private static MorphologicParser buildParser1() {
        return ContextlessMorphologicParserBuilder.newBuilderWithoutCircumflexConversion()
                .includeBundledBasicSuffixGraph()
                .includeBundledNumeralSuffixGraph()
                .addAllBundledNoBruteForceRootFinders(false)
                .build(false);
    }

    private static MorphologicParser buildParser2() {
        // load bundled dictionaries of numbers and words
        HashMultimap<String, ? extends Root> dictionaryRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();

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

        // build cache
        final MorphologicParserCache l1Cache = new LRUMorphologicParserCache(NUMBER_OF_THREADS, L1_CACHE_INITIAL_SIZE, L1_CACHE_MAX_SIZE);
        final MorphologicParserCache twoLevelCache = new TwoLevelMorphologicParserCache(L2_CACHE_MAX_SIZE, l1Cache);

        // build a caching parser which delegates parsing to the one created above, if surface is not found in the cache
        return new CachingMorphologicParser(twoLevelCache, parser, true);
    }

}
