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

package org.trnltk.experiment.morphology.morphotactics;

import com.google.common.base.Predicate;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.trnltk.model.suffix.SuffixGroup;
import org.trnltk.morphology.contextless.parser.parsing.SampleSuffixGraph;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.morphotactics.reducedambiguity.BasicRASuffixGraph;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SuffixGraphDrawingTest {

    @Test
    public void shouldDumpBasicSuffixGraphInDotFormat() throws Exception {
        final BasicSuffixGraph graph = new BasicSuffixGraph();
        graph.initialize();


        this.dumpSuffixGraphInDotFormat(graph, null, null);
    }

    @Test
    public void shouldDumpBasicRASuffixGraphInDotFormat() throws Exception {
        final BasicRASuffixGraph graph = new BasicRASuffixGraph();
        graph.initialize();


        this.dumpSuffixGraphInDotFormat(new File("core/target/ra_full.dot"), graph, null, null);
    }

    @Test
    public void shouldDumpBasicRASuffixGraphInDotFormatForNounAndAdjRelatedNodes() throws Exception {
        final BasicRASuffixGraph graph = new BasicRASuffixGraph();
        graph.initialize();


        Predicate<SuffixGraphState> sourceNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NOUN") || input.getName().startsWith("ADJ");
            }
        };
        Predicate<SuffixGraphState> targetNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NOUN") || input.getName().startsWith("ADJ");
            }
        };
        this.dumpSuffixGraphInDotFormat(new File("core/target/ra_noun_adj.dot"), graph, sourceNodePredicate, targetNodePredicate);
    }

    @Test
    public void shouldDumpBasicRASuffixGraphInDotFormatForNounRelatedNodes() throws Exception {
        final BasicRASuffixGraph graph = new BasicRASuffixGraph();
        graph.initialize();


        Predicate<SuffixGraphState> sourceNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NOUN");
            }
        };
        Predicate<SuffixGraphState> targetNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NOUN");
            }
        };
        this.dumpSuffixGraphInDotFormat(new File("core/target/ra_noun.dot"), graph, sourceNodePredicate, targetNodePredicate);
    }

    @Test
    public void shouldDumpSampleGraphInDotFormat() throws Exception {
        final BaseSuffixGraph graph = new SampleSuffixGraph();
        graph.initialize();


        this.dumpSuffixGraphInDotFormat(graph, null, null);
    }

    @Test
    public void shouldDumpNumeralSuffixGraphInDotFormat() throws Exception {
        final NumeralSuffixGraph graph = new NumeralSuffixGraph(new BasicSuffixGraph());
        graph.initialize();


        this.dumpSuffixGraphInDotFormat(graph, null, null);
    }

    @Test
    public void shouldDumpBigSuffixGraphInDotFormat() throws Exception {
        final CopulaSuffixGraph graph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        graph.initialize();


        this.dumpSuffixGraphInDotFormat(graph, null, null);
    }

    @Test
    public void shouldDumpNumeralSuffixGraphInDotFormatForNumeralsAndAdj() throws Exception {
        final NumeralSuffixGraph graph = new NumeralSuffixGraph(new BasicSuffixGraph());
        graph.initialize();


        Predicate<SuffixGraphState> sourceNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NUMERAL") || input.getName().startsWith("ADJ");
            }
        };
        Predicate<SuffixGraphState> targetNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NUMERAL") || input.getName().startsWith("ADJ");
            }
        };
        this.dumpSuffixGraphInDotFormat(graph, sourceNodePredicate, targetNodePredicate);
    }

    @Test
    public void shouldDumpNumeralSuffixGraphInDotFormatForVerbs() throws Exception {
        final NumeralSuffixGraph graph = new NumeralSuffixGraph(new BasicSuffixGraph());
        graph.initialize();


        Predicate<SuffixGraphState> sourceNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("VERB");
            }
        };
        Predicate<SuffixGraphState> targetNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("VERB");
            }
        };
        this.dumpSuffixGraphInDotFormat(graph, sourceNodePredicate, targetNodePredicate);
    }

    @Test
    public void shouldDumpBigSuffixGraphInDotFormatForVerbAndAdjRelatedNodes() throws Exception {
        final CopulaSuffixGraph graph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        graph.initialize();


        Predicate<SuffixGraphState> sourceNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("VERB") || input.getName().startsWith("ADJ");
            }
        };
        Predicate<SuffixGraphState> targetNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("VERB") || input.getName().startsWith("ADJ");
            }
        };
        this.dumpSuffixGraphInDotFormat(graph, sourceNodePredicate, targetNodePredicate);
    }

    @Test
    public void shouldDumpBigSuffixGraphInDotFormatForVerbNounAndAdvRelatedNodes() throws Exception {
        final CopulaSuffixGraph graph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        graph.initialize();


        Predicate<SuffixGraphState> sourceNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("VERB") || input.getName().startsWith("ADV") || input.getName().startsWith("NOUN");
            }
        };
        Predicate<SuffixGraphState> targetNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("VERB") || input.getName().startsWith("ADV") || input.getName().startsWith("NOUN");
            }
        };
        this.dumpSuffixGraphInDotFormat(graph, sourceNodePredicate, targetNodePredicate);
    }

    @Test
    public void shouldDumpBigSuffixGraphInDotFormatForNounRelatedNodes() throws Exception {
        final CopulaSuffixGraph graph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        graph.initialize();


        Predicate<SuffixGraphState> sourceNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NOUN");
            }
        };
        Predicate<SuffixGraphState> targetNodePredicate = new Predicate<SuffixGraphState>() {
            @Override
            public boolean apply(SuffixGraphState input) {
                return input.getName().startsWith("NOUN");
            }
        };
        this.dumpSuffixGraphInDotFormat(graph, sourceNodePredicate, targetNodePredicate);
    }


    public void dumpSuffixGraphInDotFormat(BaseSuffixGraph theGraph, Predicate<SuffixGraphState> sourceNodePredicate, Predicate<SuffixGraphState> targetNodePredicate) {
        final String suffixGraphInDotFormat = createSuffixGraphInDotFormat(theGraph, sourceNodePredicate, targetNodePredicate);
        System.out.println(suffixGraphInDotFormat);
    }

    public void dumpSuffixGraphInDotFormat(File targetFile, BaseSuffixGraph theGraph, Predicate<SuffixGraphState> sourceNodePredicate, Predicate<SuffixGraphState> targetNodePredicate) throws IOException {
        final String suffixGraphInDotFormat = createSuffixGraphInDotFormat(theGraph, sourceNodePredicate, targetNodePredicate);
        System.out.println("Dumping suffix graph to file " + targetFile.getAbsolutePath());
        FileUtils.write(targetFile, suffixGraphInDotFormat);
    }

    public String createSuffixGraphInDotFormat(BaseSuffixGraph theGraph, Predicate<SuffixGraphState> sourceNodePredicate, Predicate<SuffixGraphState> targetNodePredicate) {
        final StringBuilder builder = new StringBuilder("digraph suffixGraph {").append("\n");

        final Set<String> nodeNames = new HashSet<String>();
        int edgeCount = 0;
        final HashMap<String, String> suffixGroupColorMap = new HashMap<String, String>();

        BaseSuffixGraph graph = theGraph;
        while (graph != null) {
            for (SuffixGraphState state : getStateMap(graph).values()) {
                if (sourceNodePredicate != null && !sourceNodePredicate.apply(state))
                    continue;

                final String sourceStateName = state.getName();
                final boolean sourceStateAdded = nodeNames.add(sourceStateName);
                if (sourceStateAdded)
                    builder.append(String.format("\t%s [shape=\"%s\"]", sourceStateName, getNodeShape(state))).append("\n");

                for (SuffixEdge suffixEdge : state.getOutEdges()) {
                    final SuffixGraphState targetState = suffixEdge.getTargetState();
                    if (targetNodePredicate != null && !targetNodePredicate.apply(targetState))
                        continue;

                    final String targetStateName = targetState.getName();
                    final boolean targetStateAdded = nodeNames.add(targetStateName);

                    if (targetStateAdded)
                        builder.append(String.format("\t%s [shape=\"%s\"]", targetStateName, getNodeShape(targetState))).append("\n");


                    String style = "solid";
                    String color = getEdgeColor(suffixEdge, suffixGroupColorMap);
                    String label = suffixEdge.getSuffix().getName();
                    String line = "\t%s -> %s [style=\"%s\" color=\"%s\" label=\"%s\"]";
                    builder.append(String.format(line, sourceStateName, targetStateName, style, color, label)).append("\n");

                    edgeCount++;
                }
            }

            if (getDecorated(graph) instanceof BaseSuffixGraph)
                graph = (BaseSuffixGraph) getDecorated(graph);
            else
                graph = null;
        }

        builder.append("// Number of nodes : ").append(nodeNames.size()).append("\n");
        builder.append("// Number of edges: ").append(edgeCount).append("\n");

        builder.append("}").append("\n");

        return builder.toString();
    }

    private String getEdgeColor(SuffixEdge suffixEdge, HashMap<String, String> suffixGroupColorMap) {
        //see http://www.graphviz.org/doc/info/attrs.html#k:colorList
        final SuffixGroup suffixGroup = suffixEdge.getSuffix().getSuffixGroup();
        if (suffixGroup == null)
            return "black";

        if (!suffixGroupColorMap.containsKey(suffixGroup.getName()))
            suffixGroupColorMap.put(suffixGroup.getName(), getRandomColor());

        return suffixGroupColorMap.get(suffixGroup.getName());
    }

    private String getNodeShape(SuffixGraphState state) {
        //see http://www.graphviz.org/doc/info/shapes.html
        if (state.getName().endsWith("_ROOT") || state.getName().endsWith("_ROOT_TERMINAL"))
            return "circle";
        else if (state.getType().equals(SuffixGraphStateType.DERIVATIONAL))
            return "house";
        else if (state.getType().equals(SuffixGraphStateType.TERMINAL))
            return "doubleoctagon";
        else
            return "ellipse";
    }

    private String getRandomColor() {
        int r = Double.valueOf(Math.random() * 0xd0).intValue() + 0x10;
        int g = Double.valueOf(Math.random() * 0xd0).intValue() + 0x10;
        int b = Double.valueOf(Math.random() * 0xd0).intValue() + 0x10;

        return "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);

    }

    private Map<String, SuffixGraphState> getStateMap(BaseSuffixGraph graph) {
        try {
            final Field field = BaseSuffixGraph.class.getDeclaredField("stateMap");
            field.setAccessible(true);
            return (Map<String, SuffixGraphState>) field.get(graph);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SuffixGraph getDecorated(BaseSuffixGraph graph) {
        try {
            final Field field = BaseSuffixGraph.class.getDeclaredField("decorated");
            field.setAccessible(true);
            return (SuffixGraph) field.get(graph);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
