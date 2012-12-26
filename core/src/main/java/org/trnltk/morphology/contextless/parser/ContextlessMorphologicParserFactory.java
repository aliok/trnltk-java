/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.morphology.contextless.parser;

import com.google.common.collect.Multimap;
import org.trnltk.morphology.contextless.parser.rootfinders.DictionaryRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.NumeralRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.ProperNounFromApostropheRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.ProperNounWithoutApostropheRootFinder;
import org.trnltk.morphology.lexicon.CircumflexConvertingRootGenerator;
import org.trnltk.morphology.lexicon.DictionaryLoader;
import org.trnltk.morphology.lexicon.RootMapGenerator;
import org.trnltk.morphology.model.Lexeme;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ContextlessMorphologicParserFactory {

    private SuffixGraph suffixGraph;
    private Set<RootFinder> rootFinders = new HashSet<RootFinder>();
    private PredefinedPaths predefinedPaths;
    private SuffixApplier suffixApplier;

    public ContextlessMorphologicParserFactory suffixGraph(SuffixGraph suffixGraph) {
        this.suffixGraph = suffixGraph;
        return this;
    }

    public ContextlessMorphologicParserFactory rootFinder(RootFinder... rootFinders) {
        for (RootFinder rootFinder : rootFinders) {
            this.rootFinders.add(rootFinder);
        }
        return this;
    }

    public ContextlessMorphologicParserFactory suffixApplier(SuffixApplier suffixApplier) {
        this.suffixApplier = suffixApplier;
        return this;
    }

    public ContextlessMorphologicParserFactory predefinedPaths(PredefinedPaths predefinedPaths) {
        this.predefinedPaths = predefinedPaths;
        return this;
    }

    public ContextlessMorphologicParser build() {
        return new ContextlessMorphologicParser(this.suffixGraph, this.predefinedPaths, this.rootFinders, this.suffixApplier);
    }

    public static ContextlessMorphologicParser createSimple() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        final CircumflexConvertingRootGenerator rootGenerator = new CircumflexConvertingRootGenerator();
        Collection<? extends Root> roots = rootGenerator.generateAll(lexemes);
        final Multimap<String, ? extends Root> rootMap = new RootMapGenerator().generate(roots);

        final BasicSuffixGraph basicSuffixGraph = new BasicSuffixGraph();
        basicSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));
        final PredefinedPaths predefinedPaths = new PredefinedPaths(basicSuffixGraph, rootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));

        predefinedPaths.initialize();

        return new ContextlessMorphologicParserFactory()
                .suffixGraph(basicSuffixGraph)
                .rootFinder(new DictionaryRootFinder(rootMap))
                .predefinedPaths(predefinedPaths)
                .suffixApplier(suffixApplier)
                .build();
    }

    public static ContextlessMorphologicParser createSimpleWithRootMap(Multimap<String, ? extends Root> rootMap) {
        final BasicSuffixGraph basicSuffixGraph = new BasicSuffixGraph();
        basicSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));
        final PredefinedPaths predefinedPaths = new PredefinedPaths(basicSuffixGraph, rootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPaths.initialize();

        return new ContextlessMorphologicParserFactory()
                .suffixGraph(basicSuffixGraph)
                .rootFinder(new DictionaryRootFinder(rootMap))
                .predefinedPaths(predefinedPaths)
                .suffixApplier(suffixApplier)
                .build();
    }

    public static ContextlessMorphologicParser createWithBigGraphForRootMap(Multimap<String, ? extends Root> rootMap) {
        final CopulaSuffixGraph copulaSuffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        copulaSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));
        final PredefinedPaths predefinedPaths = new PredefinedPaths(copulaSuffixGraph, rootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPaths.initialize();

        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(rootMap);
        final NumeralRootFinder numeralRootFinder = new NumeralRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();

        final RootFinder[] rootFinders = {dictionaryRootFinder, numeralRootFinder, properNounFromApostropheRootFinder, properNounWithoutApostropheRootFinder};

        return new ContextlessMorphologicParserFactory()
                .suffixGraph(copulaSuffixGraph)
                .rootFinder(rootFinders)
                .predefinedPaths(predefinedPaths)
                .suffixApplier(suffixApplier)
                .build();
    }

    public static ContextlessMorphologicParser createWithBigGraphAndSuffixFormApplicationCachingForRootMap(Multimap<String, ? extends Root> rootMap) {
        final CopulaSuffixGraph copulaSuffixGraph = new CopulaSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph()));
        copulaSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final PrecachingSuffixFormSequenceApplier precachingSuffixFormSequenceApplier = new PrecachingSuffixFormSequenceApplier(copulaSuffixGraph, suffixFormSequenceApplier);
        final SuffixApplier suffixApplierWithoutCaching = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));
        final SuffixApplier suffixApplierWithCaching = new SuffixApplier(new PhoneticsEngine(precachingSuffixFormSequenceApplier));
        final PredefinedPaths predefinedPaths = new PredefinedPaths(copulaSuffixGraph, rootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPaths.initialize();

        return new ContextlessMorphologicParserFactory()
                .suffixGraph(copulaSuffixGraph)
                .rootFinder(new DictionaryRootFinder(rootMap))
                .predefinedPaths(predefinedPaths)
                .suffixApplier(suffixApplierWithCaching)
                .build();
    }
}
