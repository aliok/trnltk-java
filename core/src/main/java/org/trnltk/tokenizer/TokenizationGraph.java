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

package org.trnltk.tokenizer;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Ali Ok
 */
public class TokenizationGraph {
    static Logger logger = Logger.getLogger(TokenizationGraph.class);

    protected final Map<TextBlockTypeGroup, TokenizationGraphNode> nodeMap = new HashMap<TextBlockTypeGroup, TokenizationGraphNode>();
    protected final boolean recordExamples;
    private static final int CONTEXT_LENGTH = 10;

    public TokenizationGraph(boolean recordExamples) {
        this.recordExamples = recordExamples;
    }

    public void addEdge(TextBlockTypeGroup leftTextBlockTypeGroup, TextBlockTypeGroup rightTextBlockTypeGroup, boolean addSpace, boolean inferred, ImmutableList<TextBlock> exampleTextBlocks) {
        TokenizationGraphNode sourceNode = nodeMap.get(leftTextBlockTypeGroup);
        TokenizationGraphNode targetNode = nodeMap.get(rightTextBlockTypeGroup);

        if (sourceNode == null) {
            sourceNode = new TokenizationGraphNode(leftTextBlockTypeGroup);
            if (logger.isDebugEnabled())
                logger.debug("SourceNode not found, created one : " + sourceNode);
            nodeMap.put(leftTextBlockTypeGroup, sourceNode);
        } else {
            if (logger.isDebugEnabled())
                logger.debug("SourceNode found : " + sourceNode);
        }

        if (targetNode == null) {
            targetNode = new TokenizationGraphNode(rightTextBlockTypeGroup);
            if (logger.isDebugEnabled())
                logger.debug("Target not found, created one : " + targetNode);
            nodeMap.put(rightTextBlockTypeGroup, targetNode);
        } else {
            if (logger.isDebugEnabled())
                logger.debug("SourceNode found : " + targetNode);
        }

        final boolean addedNewEdge = this.addSingleEdge(sourceNode, targetNode, addSpace, inferred, exampleTextBlocks);

        if (addedNewEdge) {
            if (logger.isDebugEnabled())
                logger.debug("An edge is added, going to infer new edges");
            this.inferEdges(sourceNode, targetNode, addSpace, exampleTextBlocks);
        } else {
            if (logger.isDebugEnabled())
                logger.debug("No new edge is added, nothing to infer");
        }
    }

    private void inferEdges(TokenizationGraphNode sourceNode, TokenizationGraphNode targetNode, boolean addSpace, ImmutableList<TextBlock> exampleTextBlocks) {
        // assume we have sourceTypes <A,B> and targetTypes <C,D>
        // and K infers from A, L infers from B, M infers from C, N infers from D
        // target is to have same rule for following:
        // <A,B> - <C,D>        //not this! this is already the "premise"
        // <A,B> - <C,N>
        // <A,B> - <M,D>
        // <A,B> - <M,N>
        // <A,L> - <C,D>
        // <A,L> - <C,N>
        // <A,L> - <M,D>
        // <A,L> - <M,N>
        // <K,B> - <C,D>
        // <K,B> - <C,N>
        // <K,B> - <M,D>
        // <K,B> - <M,N>
        // <K,L> - <C,D>
        // <K,L> - <C,N>
        // <K,L> - <M,D>
        // <K,L> - <M,N>


        // [A,B]
        final ImmutableList<TextBlockType> sourceTypes = sourceNode.getData().getTextBlockTypes();
        final ImmutableList<TextBlockType> targetTypes = targetNode.getData().getTextBlockTypes();

        final Function<TextBlockType, Set<TextBlockType>> inferringFunction = new Function<TextBlockType, Set<TextBlockType>>() {
            @Override
            public Set<TextBlockType> apply(TextBlockType input) {
                Set<TextBlockType> types = Sets.newHashSet(input);

                final ImmutableCollection<TextBlockType> textBlockTypes = TextBlockType.INFERENCE_MAP.get(input);
                if (textBlockTypes != null)
                    types.addAll(textBlockTypes);

                return types;
            }
        };

        // [{A,K},{B,L}]
        final List<Set<TextBlockType>> sourceInferenceTypes = Lists.transform(sourceTypes, inferringFunction);
        final List<Set<TextBlockType>> targetInferenceTypes = Lists.transform(targetTypes, inferringFunction);

        // [[A,B],[A,L],[K,B],[K,L]]
        final Set<List<TextBlockType>> sourceInferencePossibilities = Sets.cartesianProduct(sourceInferenceTypes);
        final Set<List<TextBlockType>> targetInferencePossibilities = Sets.cartesianProduct(targetInferenceTypes);

        final Set<Pair<TextBlockTypeGroup, TextBlockTypeGroup>> nodesToAddRules = new HashSet<Pair<TextBlockTypeGroup, TextBlockTypeGroup>>();

        for (List<TextBlockType> sourceInferencePossibility : sourceInferencePossibilities) {
            for (List<TextBlockType> targetInferencePossibility : targetInferencePossibilities) {
                nodesToAddRules.add(Pair.of(new TextBlockTypeGroup(sourceInferencePossibility), new TextBlockTypeGroup(targetInferencePossibility)));
            }
        }

        nodesToAddRules.remove(Pair.of(sourceNode.getData(), targetNode.getData()));    //don't add premise again

        if (logger.isDebugEnabled()) {
            logger.debug("Gonna try inferring rules for these source and target nodes:\t");
            for (Pair<TextBlockTypeGroup, TextBlockTypeGroup> pair : nodesToAddRules) {
                logger.debug(pair);
            }
        }


        this.addInferredEdges(nodesToAddRules, addSpace, exampleTextBlocks);
    }

    public boolean addSingleEdge(TokenizationGraphNode sourceNode, TokenizationGraphNode targetNode, boolean addSpace, boolean inferred, ImmutableList<TextBlock> exampleTextBlocks) {
        if (recordExamples) {
            return sourceNode.addEdge(targetNode, addSpace, inferred, exampleTextBlocks);
        } else {
            return sourceNode.addEdge(targetNode, addSpace, inferred);
        }
    }

    private void addInferredEdges(Set<Pair<TextBlockTypeGroup, TextBlockTypeGroup>> nodesToAddRules, boolean addSpace, ImmutableList<TextBlock> exampleTextBlocks) {
        for (Pair<TextBlockTypeGroup, TextBlockTypeGroup> nodeToAddRule : nodesToAddRules) {
            final TextBlockTypeGroup sourceNodeType = nodeToAddRule.getLeft();
            final TextBlockTypeGroup targetNodeType = nodeToAddRule.getRight();

            this.addEdge(sourceNodeType, targetNodeType, addSpace, true, exampleTextBlocks);
        }
    }

    public boolean isAddSpace(TextBlockGroup leftTextBlockGroup, TextBlockGroup rightTextBlockGroup, LinkedList<TextBlock> textBlocks, int currentBlockIndex) throws MissingTokenizationRuleException {
        final TokenizationGraphEdge edge = getRule(leftTextBlockGroup, rightTextBlockGroup, textBlocks, currentBlockIndex);
        if (edge == null) {
            int startIndex = Math.max(0, currentBlockIndex - CONTEXT_LENGTH);
            int endIndex = Math.min(textBlocks.size(), currentBlockIndex + CONTEXT_LENGTH);
            final TextBlockGroup contextBlockGroup = new TextBlockGroup(textBlocks.subList(startIndex, endIndex));
            final String leftTextBlockGroupStr = leftTextBlockGroup.toString().replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            final String rightTextBlockGroupStr = rightTextBlockGroup.toString().replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            throw new MissingTokenizationRuleException(leftTextBlockGroup, rightTextBlockGroup, "No rule found for \n\tleft : " + leftTextBlockGroupStr + "\n\tright " + rightTextBlockGroupStr, contextBlockGroup);
        }

        return edge.isAddSpace();
    }

    private TokenizationGraphEdge getRule(TextBlockGroup leftTextBlockGroup, TextBlockGroup rightTextBlockGroup, LinkedList<TextBlock> textBlocks, int currentBlockIndex) {
        final TextBlockTypeGroup leftTextBlockTypeGroup = leftTextBlockGroup.getTextBlockTypeGroup();
        final TextBlockTypeGroup rightTextBlockTypeGroup = rightTextBlockGroup.getTextBlockTypeGroup();

        final TokenizationGraphNode sourceNode = this.nodeMap.get(leftTextBlockTypeGroup);
        if (sourceNode == null) {
            int startIndex = Math.max(0, currentBlockIndex - CONTEXT_LENGTH);
            int endIndex = Math.min(textBlocks.size(), currentBlockIndex + CONTEXT_LENGTH);
            final TextBlockGroup contextBlockGroup = new TextBlockGroup(textBlocks.subList(startIndex, endIndex));
            final String leftTextBlockGroupStr = leftTextBlockGroup.toString().replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            final String rightTextBlockGroupStr = rightTextBlockGroup.toString().replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            throw new MissingTokenizationRuleException(leftTextBlockGroup, rightTextBlockGroup, "No source node found \n\tleft : " + leftTextBlockGroupStr + "\n\tright " + rightTextBlockGroupStr, contextBlockGroup);
        }


        final TokenizationGraphNode targetNode = this.nodeMap.get(rightTextBlockTypeGroup);
        if (targetNode == null) {
            int startIndex = Math.max(0, currentBlockIndex - CONTEXT_LENGTH);
            int endIndex = Math.min(textBlocks.size(), currentBlockIndex + CONTEXT_LENGTH);
            final TextBlockGroup contextBlockGroup = new TextBlockGroup(textBlocks.subList(startIndex, endIndex));
            final String leftTextBlockGroupStr = leftTextBlockGroup.toString().replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            final String rightTextBlockGroupStr = rightTextBlockGroup.toString().replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            throw new MissingTokenizationRuleException(leftTextBlockGroup, rightTextBlockGroup, "No target node found \n\tleft : " + leftTextBlockGroupStr + "\n\tright " + rightTextBlockGroupStr, contextBlockGroup);
        }

        final TokenizationGraphEdge edge = sourceNode.getEdge(rightTextBlockTypeGroup);

        return edge;
    }

    @Override
    public String toString() {
        return "TokenizationGraph{" +
                "recordExamples=" + recordExamples +
                ", nodeMap=" + nodeMap +
                '}';
    }
}
