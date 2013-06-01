package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.morphology.contextless.parser.rootfinders.DictionaryRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.RootFinderChain;
import org.trnltk.morphology.contextless.parser.suffixbased.SuffixApplier;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.suffixbased.Formatter;
import org.trnltk.morphology.model.suffixbased.*;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import java.util.*;

public class CharSuffixGraphExtractorDrawingTest {

    private final PhoneticAttributeSets phoneticAttributeSets;
    private final SuffixFormSequenceApplier suffixFormSequenceApplier;

    private SuffixFormGraphExtractor charSuffixGraphExtractor;

    public CharSuffixGraphExtractorDrawingTest() {
        this.phoneticAttributeSets = new MockPhoneticAttributeSets();
        this.suffixFormSequenceApplier = new SuffixFormSequenceApplier();
    }

    @Before
    public void setUp() throws Exception {
        final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();
        this.charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, phoneticsAnalyzer, phoneticAttributeSets);
    }

    @Ignore
    @Test
    public void dumpCharSuffixGraphForBigSuffixGraphInDotFormat() throws Exception {
        final CopulaSuffixGraph suffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        suffixGraph.initialize();

        final PhoneticAttributeSets override_phoneticAttributeSets = new PhoneticAttributeSets();

        this.charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, new PhoneticsAnalyzer(), override_phoneticAttributeSets);

        final SuffixFormGraph charSuffixGraph = this.charSuffixGraphExtractor.extract(suffixGraph);


        final CharSuffixGraphExtractorPlotter plotter = new CharSuffixGraphExtractorPlotter(charSuffixGraph, override_phoneticAttributeSets);
        plotter.dumpCharSuffixGraphInDotFormat(false);
    }

    @Test
    public void dumpCharSuffixGraphForSimplifiedSuffixGraphInDotFormat() throws Exception {
        final SimplifiedSampleSuffixGraph suffixGraph = new SimplifiedSampleSuffixGraph();
        suffixGraph.initialize();

        final SuffixFormGraph charSuffixGraph = this.charSuffixGraphExtractor.extract(suffixGraph);

        final CharSuffixGraphExtractorPlotter plotter = new CharSuffixGraphExtractorPlotter(charSuffixGraph, phoneticAttributeSets);
        plotter.dumpCharSuffixGraphInDotFormat(false);
    }


    @Test
    public void dumpCharSuffixGraphForSampleSuffixGraphInDotFormat_forSurface_gazelcigim() {
        final HashMultimap<String, ? extends Root> rootMap = RootMapFactory.createSimpleConvertCircumflexes();
        final List<String> rootsToRemove = Arrays.asList("gaz", "gazell", "gazelle", "ke", "kel", "kele");

        for (String s : rootsToRemove) {
            rootMap.removeAll(s);
        }

        this.dumpCharSuffixGraphForSampleSuffixGraphInDotFormat_forSurface(rootMap, "gazelciğim", true);
    }

    @Test
    public void dumpCharSuffixGraphForSampleSuffixGraphInDotFormat_forSurface_gazellercik() {
        final HashMultimap<String, ? extends Root> rootMap = RootMapFactory.createSimpleConvertCircumflexes();
        final List<String> rootsToRemove = Arrays.asList("gaz", "gazell", "gazelle", "ke", "kel", "kele");

        for (String s : rootsToRemove) {
            rootMap.removeAll(s);
        }

        this.dumpCharSuffixGraphForSampleSuffixGraphInDotFormat_forSurface(rootMap, "gazellerciğim", false);
    }

    @Test
    public void dumpCharSuffixGraphForSampleSuffixGraphInDotFormat_forSurface_geliyor() {
        final HashMultimap<String, ? extends Root> rootMap = RootMapFactory.createSimpleConvertCircumflexes();
        final List<String> rootsToRemove = Arrays.asList("ge", "gelir");

        for (String s : rootsToRemove) {
            rootMap.removeAll(s);
        }

        this.dumpCharSuffixGraphForSampleSuffixGraphInDotFormat_forSurface(rootMap, "geliyor", false);
    }

    public void dumpCharSuffixGraphForSampleSuffixGraphInDotFormat_forSurface(HashMultimap<String, ? extends Root> rootMap, String surface, boolean drawOnlySuccessfulParses) {
        final SampleSuffixGraph sampleSuffixGraph = new SampleSuffixGraph();
        sampleSuffixGraph.initialize();

        final SuffixFormGraph charSuffixGraph = this.charSuffixGraphExtractor.extract(sampleSuffixGraph);

        final CharSuffixGraphExtractorPlotter plotter = new CharSuffixGraphExtractorPlotter(charSuffixGraph, phoneticAttributeSets);

        final SuffixGraph suffixGraph = new SampleSuffixGraph();
        suffixGraph.initialize();

        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(new SuffixFormSequenceApplier(), new PhoneticsAnalyzer(), new MockPhoneticAttributeSets());
        charSuffixGraphExtractor.extract(suffixGraph);

        final RootFinderChain rootFinderChain = new RootFinderChain();
        rootFinderChain.offer(new DictionaryRootFinder(rootMap), RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        final ContextlessMorphologicParser contextlessMorphologicParser = new ContextlessMorphologicParser(charSuffixGraph, null, rootFinderChain, new SuffixApplier(new PhoneticsEngine(new SuffixFormSequenceApplier())));

        final Set<MorphemeContainer> invalidatedPaths = new HashSet<MorphemeContainer>();

        if (!drawOnlySuccessfulParses) {
            final Set<String> strPaths = new HashSet<String>();

            contextlessMorphologicParser.setListener(new ContextlessMorphologicParserListener() {
                @Override
                public void onMorphemeContainerInvalidated(MorphemeContainer morphemeContainer) {
                    final boolean added = strPaths.add(Formatter.formatMorphemeContainerWithForms(morphemeContainer));
                    if (added)
                        invalidatedPaths.add(morphemeContainer);
                }
            });

        }

        final LinkedList<MorphemeContainer> results = contextlessMorphologicParser.parse(new TurkishSequence(surface));

        for (MorphemeContainer path : invalidatedPaths) {
            plotter.processPath(path, false);
        }

        for (MorphemeContainer path : results) {
            plotter.processPath(path, true);
        }

        plotter.dumpCharSuffixGraphInDotFormat(true);
    }

    class CharSuffixGraphExtractorPlotter {

        SuffixFormGraph charSuffixGraph;
        PhoneticAttributeSets phoneticAttributeSets;

        Predicate<SuffixFormGraphNode> sourceNodePredicate = Predicates.alwaysTrue();
        Predicate<SuffixFormGraphNode> targetNodePredicate = Predicates.alwaysTrue();

        Multimap<String, Pair<String, String>> edgeColorMap = HashMultimap.create();
        PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();


        CharSuffixGraphExtractorPlotter(SuffixFormGraph charSuffixGraph, PhoneticAttributeSets phoneticAttributeSets) {
            this.charSuffixGraph = charSuffixGraph;
            this.phoneticAttributeSets = phoneticAttributeSets;
        }

        public void processPath(MorphemeContainer morphemeContainer, boolean emphasize) {
            final String color = getRandomColor();


            final Root root = morphemeContainer.getRoot();
            final String rootStr = root.getSequence().toString();

            String surfaceSoFar = rootStr;

            Collection<LexemeAttribute> lexemeAttributes = root.getLexeme().getAttributes();

            final List<SuffixTransition> suffixTransitions = morphemeContainer.getSuffixTransitions();
            for (SuffixTransition suffixTransition : suffixTransitions) {
                final SuffixFormApplication suffixFormApplication = suffixTransition.getSuffixFormApplication();
                final String actualSuffixForm = suffixFormApplication.getActualSuffixForm();

                final SuffixGraphState sourceState = suffixTransition.getSourceState();
                final SuffixGraphState targetState = suffixTransition.getTargetState();
                final EnumSet<PhoneticAttribute> phonAttrSetOfSurfaceSoFar = phoneticsAnalyzer.calculatePhoneticAttributes(surfaceSoFar, lexemeAttributes);
                final EnumSet<PhoneticAttribute> phonAttrSetOfSurfaceAfter = StringUtils.isBlank(actualSuffixForm) ? phonAttrSetOfSurfaceSoFar : phoneticsAnalyzer.calculatePhoneticAttributes(surfaceSoFar + actualSuffixForm, lexemeAttributes);
                final SuffixFormGraphNodeKey sourceNodeKey = new SuffixFormGraphNodeKey(sourceState, phonAttrSetOfSurfaceSoFar);
                final SuffixFormGraphNodeKey targetNodeKey = new SuffixFormGraphNodeKey(targetState, phonAttrSetOfSurfaceAfter);

                final SuffixFormGraphNode sourceNode = charSuffixGraph.getNode(sourceNodeKey);
                final SuffixFormGraphNode targetNode = charSuffixGraph.getNode(targetNodeKey);

                Validate.notNull(sourceNode, sourceNodeKey.toString());
                Validate.notNull(targetNode, targetNodeKey.toString());

                SuffixFormGraphSuffixEdge appliedEdge = null;

                for (SuffixFormGraphSuffixEdge edge : sourceNode.getEdges()) {
                    if (edge.getTargetSuffixFormGraphNode().equals(targetNode) && edge.getSuffixFormApplication().equals(suffixFormApplication)) {
                        appliedEdge = edge;
                        break;
                    }
                }

                if (appliedEdge == null) {
                    StringBuilder builder = new StringBuilder("Couldn't find the edge:");
                    builder.append("\n\t source: ").append(sourceNode.getSuffixFormGraphNodeKey())
                            .append("\n\t target: ").append(targetNode.getSuffixFormGraphNodeKey())
                            .append("\n\t application: ").append(suffixFormApplication)
                            .append("\n\t existing edges: \n[")
                            .append(Joiner.on("\n\t").join(Iterables.transform(sourceNode.getEdges(), new Function<SuffixFormGraphSuffixEdge, String>() {
                                @Override
                                public String apply(SuffixFormGraphSuffixEdge input) {
                                    return input.getSuffixFormApplication().toString();
                                }
                            })))
                            .append("\n]");

                    throw new IllegalStateException(builder.toString());
                }


                if (emphasize)
                    this.edgeColorMap.put(getEdgeKey(sourceNode, appliedEdge), Pair.of(color, "bold"));
                else
                    this.edgeColorMap.put(getEdgeKey(sourceNode, appliedEdge), Pair.of(color, "dashed"));

                surfaceSoFar = surfaceSoFar + actualSuffixForm;

                if (StringUtils.isNotBlank(actualSuffixForm))
                    lexemeAttributes = null;        // set to null after first transition
            }
        }

        private String getEdgeKey(SuffixFormGraphNode sourceNode, SuffixFormGraphSuffixEdge appliedEdge) {
            return getKeyName(sourceNode.getSuffixFormGraphNodeKey()) + "#" + getEdgeLabel(appliedEdge) + "#" + getKeyName(appliedEdge.getTargetSuffixFormGraphNode().getSuffixFormGraphNodeKey());
        }

        public void dumpCharSuffixGraphInDotFormat(boolean drawOnlyProcessedPaths) {
            System.out.println("digraph charSuffixGraph {");

            Set<String> printedNodeNames = new HashSet<String>();

            for (Map.Entry<SuffixFormGraphNodeKey, SuffixFormGraphNode> entry : charSuffixGraph.getMap().entrySet()) {
                final SuffixFormGraphNodeKey sourceCharSuffixGraphNodeKey = entry.getKey();
                final SuffixFormGraphNode sourceCharSuffixGraphNode = entry.getValue();

                if (!sourceNodePredicate.apply(sourceCharSuffixGraphNode))
                    continue;

                final String sourceStateName = getKeyName(sourceCharSuffixGraphNodeKey);

                final StringBuilder builder = new StringBuilder();

                final boolean added = printedNodeNames.add(sourceStateName);
                if (!added)
                    throw new IllegalStateException("State " + sourceStateName + " was already added!");

                boolean hasColoredEdges = false;

                for (SuffixFormGraphSuffixEdge edge : sourceCharSuffixGraphNode.getEdges()) {
                    final SuffixFormGraphNode targetCharSuffixGraphNode = edge.getTargetSuffixFormGraphNode();

                    if (!targetNodePredicate.apply(targetCharSuffixGraphNode))
                        continue;

                    final String targetStateName = getKeyName(targetCharSuffixGraphNode.getSuffixFormGraphNodeKey());

                    final String line = "\t%s -> %s [style=\"%s\" color=\"%s\" label=\"%s\"]";
                    final String edgeLabel = getEdgeLabel(edge);

                    final String edgeKey = getEdgeKey(sourceCharSuffixGraphNode, edge);
                    if (edgeColorMap.containsKey(edgeKey)) {
                        final Collection<Pair<String, String>> colors = edgeColorMap.get(edgeKey);
                        if (CollectionUtils.isNotEmpty(colors)) {
                            hasColoredEdges = true;
                        }
                        for (Pair<String, String> pair : colors) {
                            builder.append(String.format(line, sourceStateName, targetStateName, pair.getRight(), pair.getLeft(), edgeLabel)).append("\n");
                        }
                    } else {
                        if (!drawOnlyProcessedPaths)
                            builder.append(String.format(line, sourceStateName, targetStateName, "solid", "black", edgeLabel)).append("\n");
                    }

                }

                if (!drawOnlyProcessedPaths || hasColoredEdges) {
                    System.out.println(String.format("\t%s [shape=\"%s\"]", sourceStateName, getNodeShape(sourceCharSuffixGraphNode)));
                    System.out.println(builder);
                }
            }
            System.out.println("}");
        }

        public void setSourceNodePredicate(Predicate<SuffixFormGraphNode> sourceNodePredicate) {
            this.sourceNodePredicate = sourceNodePredicate;
        }

        public void setTargetNodePredicate(Predicate<SuffixFormGraphNode> targetNodePredicate) {
            this.targetNodePredicate = targetNodePredicate;
        }

        private String[] colorValues = new String[]{            // colors easy to notice
                "FF0000", "00FF00", "0000FF", "FFFF00", "FF00FF", "00FFFF", "000000",
                "800000", "008000", "000080", "808000", "800080", "008080", "808080",
                "C00000", "00C000", "0000C0", "C0C000", "C000C0", "00C0C0", "C0C0C0",
                "400000", "004000", "000040", "404000", "400040", "004040", "404040",
                "200000", "002000", "000020", "202000", "200020", "002020", "202020",
                "600000", "006000", "000060", "606000", "600060", "006060", "606060",
                "A00000", "00A000", "0000A0", "A0A000", "A000A0", "00A0A0", "A0A0A0",
                "E00000", "00E000", "0000E0", "E0E000", "E000E0", "00E0E0", "E0E0E0",
        };
        private int lastUsedColorIndex = -1;

        private String getRandomColor() {
//            int r = Double.valueOf(Math.random() * 0xd0).intValue() + 0x10;
//            int g = Double.valueOf(Math.random() * 0xd0).intValue() + 0x10;
//            int b = Double.valueOf(Math.random() * 0xd0).intValue() + 0x10;
//
//            return "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
            lastUsedColorIndex++;
            return "#" + colorValues[lastUsedColorIndex];
        }

        private String getNodeShape(SuffixFormGraphNode state) {
            //see http://www.graphviz.org/doc/info/shapes.html
            final String stateName = state.getSuffixFormGraphNodeKey().getState().getName();
            if (state.getSuffixGraphStateType().equals(SuffixGraphStateType.DERIVATIONAL))
                return "house";
            else if (state.getSuffixGraphStateType().equals(SuffixGraphStateType.TERMINAL))
                return "doubleoctagon";
            else if (stateName.endsWith("_ROOT") || stateName.endsWith("_ROOT_TERMINAL"))
                return "circle";
            else
                return "ellipse";
        }

        private String getEdgeLabel(SuffixFormGraphSuffixEdge charSuffixGraphSuffixEdge) {
            final Suffix suffix = charSuffixGraphSuffixEdge.getSuffixFormApplication().getSuffixForm().getSuffix();
            String appliedSuffixForm = charSuffixGraphSuffixEdge.getSuffixFormApplication().getActualSuffixForm();
            if (StringUtils.isBlank(appliedSuffixForm))
                appliedSuffixForm = "¬";
            return appliedSuffixForm + "(" + suffix.getName() + "[" + appliedSuffixForm + "]" + ")";

        }

        private String getKeyName(SuffixFormGraphNodeKey key) {
            return key.getState().getName() + "_" + this.phoneticAttributeSets.getNumberForSet(key.getPhonAttrSet());
        }
    }

}