package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SuffixFormGraph {

    private final Map<SuffixFormGraphNodeKey, SuffixFormGraphNode> nodeMap = new HashMap<>();
    private final SuffixGraph suffixGraph;

    public SuffixFormGraph(SuffixGraph suffixGraph) {
        this.suffixGraph = suffixGraph;
    }

    public SuffixFormGraphNode addNode(SuffixFormGraphNodeKey suffixFormGraphNodeKey, SuffixGraphStateType suffixGraphStateType, Set<PhoneticAttribute> phonAttrSet) {
        final SuffixFormGraphNode suffixFormGraphNode = new SuffixFormGraphNode(suffixFormGraphNodeKey, suffixGraphStateType, Sets.immutableEnumSet(phonAttrSet));
        final SuffixFormGraphNode existingSuffixFormGraphNode = this.nodeMap.put(suffixFormGraphNodeKey, suffixFormGraphNode);
        Validate.isTrue(existingSuffixFormGraphNode == null);

        return suffixFormGraphNode;
    }

    public void addNode(SuffixFormGraphNodeKey suffixFormGraphNodeKey, SuffixFormGraphNode suffixFormGraphNode) {
        final SuffixFormGraphNode existingNode = nodeMap.put(suffixFormGraphNodeKey, suffixFormGraphNode);
        Validate.isTrue(existingNode == null);
    }

    public SuffixFormGraphNode getNode(SuffixFormGraphNodeKey suffixFormGraphNodeKey) {
        return nodeMap.get(suffixFormGraphNodeKey);
    }

    public SuffixGraphState getDefaultStateForRoot(Root root) {
        final SuffixGraphState defaultStateForRoot = this.suffixGraph.getDefaultStateForRoot(root);
        if (defaultStateForRoot == null)
            throw new IllegalStateException("No default state found for root " + root);

        return defaultStateForRoot;
    }

    public SuffixGraph getSuffixGraph() {
        return suffixGraph;
    }

    // for tests
    Map<SuffixFormGraphNodeKey, SuffixFormGraphNode> getMap() {
        return nodeMap;
    }
}
