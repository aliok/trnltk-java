package org.trnltk.doc.advancedparsing;

import org.trnltk.morphology.contextless.parser.CachingMorphologicParser;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.formbased.PhoneticAttributeSets;
import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraph;
import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraphExtractor;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.NumeralSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

public class AdvancedParsing {

    public static void main(String[] args) {
        MorphologicParser parser1 = buildParser1();


    }

    private static MorphologicParser buildParser1() {
        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);

        final SuffixFormGraphExtractor suffixFormGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);

        final SuffixGraph suffixGraph = new NumeralSuffixGraph(new BasicSuffixGraph());
        final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, dictionaryRootMap);

        final RootFinderChain rootFinderChain;
        final SuffixFormGraph suffixFormGraph = suffixFormGraphExtractor.extract(suffixGraph);

        return new ContextlessMorphologicParser(suffixFormGraph, predefinedPaths, rootFinderChain, suffixApplier);
    }

}
