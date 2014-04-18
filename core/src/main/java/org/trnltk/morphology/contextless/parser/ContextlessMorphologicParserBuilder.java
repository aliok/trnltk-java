package org.trnltk.morphology.contextless.parser;

import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.lexicon.Root;
import org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache;
import org.trnltk.morphology.contextless.parser.cache.SimpleOfflineCache;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

/**
 * A helper class to build morphologic parsers.
 */
@SuppressWarnings({"WeakerAccess", "UnusedDeclaration"})
public class ContextlessMorphologicParserBuilder {

    private SuffixGraph suffixGraph;
    private RootFinderChain rootFinderChain;
    private MorphologicParserCache cache;
    private boolean useLocalCache;

    private final HashMultimap<String, ? extends Root> _dictionaryRootMap;

    private ContextlessMorphologicParserBuilder(boolean convertCircumflexes) {
        this.rootFinderChain = new RootFinderChain(new RootValidator());

        // create root entries from bundled dictionary
        if (convertCircumflexes)
            _dictionaryRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
        else
            _dictionaryRootMap = RootMapFactory.createSimpleWithNumbers();

    }

    /**
     * Creates a new builder with converting circumflexed items in the bundled dictionaries to no circumflexed ones.
     * <p/>
     * That means, when a surface which is not circumflexed correctly is given, it is parsed with the no circumflexed root.
     */
    public static ContextlessMorphologicParserBuilder newBuilder() {
        return new ContextlessMorphologicParserBuilder(true);
    }

    /**
     * Creates a new builder with strictness about circumflexes.
     * <p/>
     * That means, when a surface which is not circumflexed correctly is given, it won't be parsed.
     */
    public static ContextlessMorphologicParserBuilder newBuilderWithoutCircumflexConversion() {
        return new ContextlessMorphologicParserBuilder(false);
    }

    /**
     * Build the parser with already given criteria.
     * <p/>
     * If <code>defaults</code> is true, for values not set, default values for the parser will be set.
     * If no suffix graph is set, all bundled suffix graphs will be included. If no root finders are added, all no brute
     * force root finders will be added.
     * <p/>
     * If <code>defaults</code> is false, builder will be strict about set values and will throw an exception if one of the
     * required parts is missing.
     */
    public MorphologicParser build(boolean defaults) {
        if (defaults) {
            if (this.suffixGraph == null)
                this.includeAllBundledSuffixGraphs();
            if (!this.rootFinderChain.hasRootFinders())
                this.addAllBundledNoBruteForceRootFinders(true);
        } else {
            Validate.notNull(suffixGraph, "No suffix graph included!");
            Validate.notNull(rootFinderChain.hasRootFinders(), "No root finders added!");
        }


        // create common phonetic and morphotactic parts
        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);

        // following is to extract a form-based graph from a suffix-based graph
        final SuffixFormGraphExtractor suffixFormGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);

        // need to initialize suffix graph first
        suffixGraph.initialize();

        // extract the formBasedGraph
        final SuffixFormGraph suffixFormGraph = suffixFormGraphExtractor.extract(suffixGraph);


        // create predefined paths
        final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, _dictionaryRootMap, suffixApplier);
        predefinedPaths.initialize();

        final MorphologicParser parser = new ContextlessMorphologicParser(suffixFormGraph, predefinedPaths, rootFinderChain, suffixApplier);

        if (cache != null)
            return new CachingMorphologicParser(cache, parser, useLocalCache);
        else
            return parser;
    }

    public ContextlessMorphologicParserBuilder addAllBundledNoBruteForceRootFinders(boolean includeProperNounRootFinders) {
        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(_dictionaryRootMap);
        final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
        final PuncRootFinder puncRootFinder = new PuncRootFinder();


        rootFinderChain
                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED);
        if (includeProperNounRootFinders)
            rootFinderChain
                    .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                    .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        rootFinderChain.offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        return this;
    }

    public ContextlessMorphologicParserBuilder addAllBundledBruteForceRootFinders() {
        final BruteForceCompoundNounRootFinder bruteForceCompoundNounRootFinder = new BruteForceCompoundNounRootFinder();
        final BruteForceNounRootFinder bruteForceNounRootFinder = new BruteForceNounRootFinder();
        final BruteForceVerbRootFinder bruteForceVerbRootFinder = new BruteForceVerbRootFinder();

        rootFinderChain
                .offer(bruteForceCompoundNounRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(bruteForceNounRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(bruteForceVerbRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        return this;
    }

    /**
     * Adds all bundled root finders : brute force and no brute force
     */
    public ContextlessMorphologicParserBuilder addAllBundledRootFinders() {
        this.addAllBundledNoBruteForceRootFinders(true);
        this.addAllBundledBruteForceRootFinders();

        return this;
    }

    /**
     * Add a {@link RootFinder} manually in the {@link RootFinderChain}. Please note that order is important.
     * <p/>
     * If you have a custom root finder, then you must add it into the chain using this method.
     * <p/>
     * Please note that, unless you use one of the <code>addAllBundledXXXRootFinders</code> methods, system will not
     * add any bundled root finders.
     */
    public ContextlessMorphologicParserBuilder offerRootFinder(RootFinder rootFinder, RootFinderChain.RootFinderPolicy rootFinderPolicy) {
        this.rootFinderChain.offer(rootFinder, rootFinderPolicy);
        return this;
    }

    /**
     * Set {@link SuffixGraph} to use manually.
     */
    public ContextlessMorphologicParserBuilder suffixGraph(SuffixGraph suffixGraph) {
        this.suffixGraph = suffixGraph;
        return this;
    }

    public ContextlessMorphologicParserBuilder includeBundledBasicSuffixGraph() {
        Validate.isTrue(this.suffixGraph == null, "Basic suffix graph must be included as the first suffix graph!");
        this.suffixGraph = new BasicSuffixGraph();
        return this;
    }

    public ContextlessMorphologicParserBuilder includeBundledNumeralSuffixGraph() {
        this.suffixGraph = new NumeralSuffixGraph(this.suffixGraph);
        return this;
    }

    public ContextlessMorphologicParserBuilder includeBundledProperNounSuffixGraph() {
        this.suffixGraph = new ProperNounSuffixGraph(this.suffixGraph);
        return this;
    }

    public ContextlessMorphologicParserBuilder includeBundledCopulaSuffixGraph() {
        this.suffixGraph = new CopulaSuffixGraph(this.suffixGraph);
        return this;
    }

    public ContextlessMorphologicParserBuilder includeAllBundledSuffixGraphs() {
        return this.includeBundledBasicSuffixGraph().includeBundledNumeralSuffixGraph().includeBundledProperNounSuffixGraph().includeBundledCopulaSuffixGraph();
    }

    /**
     * If true, resulting parser will use a {@link SimpleOfflineCache} with most frequent Turkish words.
     * Parser will also use local caching.
     *
     * @see org.trnltk.morphology.contextless.parser.cache.SimpleOfflineCache#forTop20kWords()
     * @see ContextlessMorphologicParserBuilder#cache(org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache)
     */
    public ContextlessMorphologicParserBuilder bundledSimpleOfflineCache() {
        return this.cache(SimpleOfflineCache.forTop20kWords());
    }

    /**
     * Manually set a cache to use.
     *
     * @param cache         Cache to use
     * @param useLocalCache If true, parser will also use local caching. That means, it will use the locality of the inputs.
     * @see MorphologicParserCache
     */
    public ContextlessMorphologicParserBuilder cache(MorphologicParserCache cache, boolean useLocalCache) {
        this.cache = cache;
        this.useLocalCache = useLocalCache;
        return this;
    }

    /**
     * Manually set a cache to use and use local caching.
     *
     * @param cache Cache to use
     * @see MorphologicParserCache
     * @see ContextlessMorphologicParserBuilder#cache(org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache, boolean)
     */
    public ContextlessMorphologicParserBuilder cache(MorphologicParserCache cache) {
        return this.cache(cache, true);
    }

    /**
     * Creates a morphologic parser with simplest suffix graph and numeral suffix graph, roots from bundled dictionary.
     */
    public static MorphologicParser createSimple() {
        return newBuilderWithoutCircumflexConversion()
                .includeBundledBasicSuffixGraph()
                .addAllBundledNoBruteForceRootFinders(false)
                .build(true);
    }


}
