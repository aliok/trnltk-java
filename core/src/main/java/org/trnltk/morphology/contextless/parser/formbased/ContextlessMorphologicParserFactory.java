package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.HashMultimap;
import org.trnltk.model.lexicon.Root;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.rootfinder.DictionaryRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.rootfinder.RootValidator;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

public class ContextlessMorphologicParserFactory {
    public static ContextlessMorphologicParser createSimple() {
        // create common phonetic and morphotactic parts
        PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);

        // create the suffix graph. the simplest one for now
        BasicSuffixGraph suffixGraph = new BasicSuffixGraph();
        suffixGraph.initialize();

        // following is to extract a form-based graph from a suffix-based graph
        SuffixFormGraphExtractor graphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);
        // extract the formBasedGraph
        SuffixFormGraph formBasedGraph = graphExtractor.extract(suffixGraph);

        // create root entries from bundled dictionary
        HashMultimap<String, ? extends Root> rootMap = RootMapFactory.createSimple();

        // create chained root finders
        RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());
        rootFinderChain.offer(new DictionaryRootFinder(rootMap), RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED);

        // create predefined paths
        PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, rootMap, suffixApplier);
        predefinedPaths.initialize();

        return new ContextlessMorphologicParser(formBasedGraph, predefinedPaths, rootFinderChain, suffixApplier);
    }
}
