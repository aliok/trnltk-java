package org.trnltk.tokenizer.experiment;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ali Ok
 */
class TokenizationGraphNode {
    static Logger logger = Logger.getLogger(TokenizationGraphNode.class);

    protected final TextBlockTypeGroup data;
    protected final Map<TextBlockTypeGroup, TokenizationGraphEdge> edges;

    public TokenizationGraphNode(TextBlockTypeGroup data) {
        this.data = data;
        this.edges = new HashMap<TextBlockTypeGroup, TokenizationGraphEdge>();
    }

    public boolean addEdge(TokenizationGraphNode targetNode, boolean addSpace, boolean inferred, List<TextBlock> exampleTextBlocks) {
        if (logger.isDebugEnabled())
            logger.debug("Adding a new edge:\n\tSource:" + this + "\n\tTarget:" + targetNode + "\n\taddSpace:" + addSpace + "\n\tinferred:" + inferred);

        final TokenizationGraphEdge existingEdge = this.edges.get(targetNode.getData());
        if (existingEdge == null) {
            final TokenizationGraphEdge newEdge = addNewEdge(targetNode, addSpace, inferred, exampleTextBlocks);
            if (logger.isDebugEnabled())
                logger.debug("No edge found, created one : " + newEdge);
            return true;
        } else {
            // tree is deterministic. thus check existing edge
            // if it was inferred, overwrite
            // if it was not inferred, make sure addSpace values are same

            if (logger.isDebugEnabled())
                logger.debug("Found existing edge : " + existingEdge);

            if (existingEdge.isInferred()) {
                if (inferred) {
                    //assert that existing inferred node and new inferred node has same "addSpace" rule
                    Validate.isTrue(existingEdge.isAddSpace() == addSpace,
                            "'addSpace' rule of existing inferred node is not consistent with new inferred node.\n" +
                                    "Existing edge:\n" +
                                    "\tSource: " + this + "\n" +
                                    "\tTarget" + targetNode + "\n" +
                                    "\tEdge:" + existingEdge + "\n" +
                                    "\tTextBlocks: " + exampleTextBlocks + "\n" +
                                    "\tCurrent example:" + exampleTextBlocks + "\n" +
                                    "\tPrevious examples for edge:" + existingEdge.getExamples());

                    if (CollectionUtils.isNotEmpty(exampleTextBlocks))
                        existingEdge.addExample(exampleTextBlocks);

                    if (logger.isDebugEnabled())
                        logger.debug("Since edge wanted to add is also an inferred one, not overwriting");

                    return false;
                } else {
                    final TokenizationGraphEdge newEdge = this.addNewEdge(targetNode, addSpace, inferred, exampleTextBlocks);

                    if (logger.isDebugEnabled())
                        logger.debug("Since edge wanted to add is NOT an inferred one, it is overwritten with this one: " + newEdge);

                    return false;
                }
            } else {
                if (logger.isDebugEnabled())
                    logger.debug("Since existing edge is not inferred one, doing nothing but rule consistency check");

                Validate.isTrue(existingEdge.isAddSpace() == addSpace, "For node, 'addSpace' rule is not consistent. \n\tSource: " + this +
                        "\n\tTarget" + targetNode + "\n\tEdge:" + existingEdge + "\n\tTextBlocks: " + exampleTextBlocks + "\n\tPrevious examples for edge:" + existingEdge.getExamples());

                if (CollectionUtils.isNotEmpty(exampleTextBlocks))
                    existingEdge.addExample(exampleTextBlocks);

                return false;
            }
        }
    }

    private TokenizationGraphEdge addNewEdge(TokenizationGraphNode targetNode, boolean addSpace, boolean inferred, List<TextBlock> exampleTextBlocks) {
        final TokenizationGraphEdge newEdge = new TokenizationGraphEdge(inferred, targetNode, addSpace);
        this.edges.put(targetNode.getData(), newEdge);

        if (CollectionUtils.isNotEmpty(exampleTextBlocks))
            newEdge.addExample(exampleTextBlocks);

        return newEdge;
    }

    public boolean addEdge(TokenizationGraphNode targetNode, boolean addSpace, boolean inferred) {
        return this.addEdge(targetNode, addSpace, inferred, null);
    }

    public TokenizationGraphEdge getEdge(TextBlockTypeGroup targetNode) {
        return this.edges.get(targetNode);
    }

    public TextBlockTypeGroup getData() {
        return data;
    }

    @Override
    public String toString() {
        return "TokenizationGraphNode{" +
                "data=" + data +
                ", edges=" + edges +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenizationGraphNode that = (TokenizationGraphNode) o;

        if (!data.equals(that.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
