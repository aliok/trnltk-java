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

import com.google.common.collect.*;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Ali Ok
 */
public class TextTokenizer {

    static Logger logger = Logger.getLogger(TextTokenizer.class);

    private static final String SPACE = " ";

    protected final int blockSize;
    protected final TokenizationGraph graph;
    protected final boolean strict;

    protected final TextBlockSplitter textBlockSplitter;

    protected final TextTokenizerStats stats;

    private TextTokenizer(TextTokenizerBuilder builder) {
        this.blockSize = builder.blockSize;
        this.graph = builder.graph;
        this.strict = builder.strict;
        this.stats = builder.recordStats ? new TextTokenizerStats() : null;

        this.textBlockSplitter = new TextBlockSplitter();
    }

    public LinkedList<Token> tokenize(String text) {
        if (logger.isDebugEnabled())
            logger.debug("Tokenizing text: '" + text + "'");

        text = text.replaceAll("  +", " "); // remove multiple consequent space chars
        text = text.trim();

        final LinkedList<TextBlock> textBlocks = textBlockSplitter.splitToTextParts(text);
        this.textBlockSplitter.addTextStartsAndEnds(textBlocks, this.blockSize);

        final LinkedList<Token> tokens = new LinkedList<Token>();

        StringBuilder currentTokenBuilder = new StringBuilder();
        List<TextBlockType> currentBlockTypes = new LinkedList<TextBlockType>();


        for (int i = this.blockSize; i <= textBlocks.size() - this.blockSize; i++) {
            final TextBlockGroup leftTextBlockGroup = this.textBlockSplitter.getTextBlockGroup(textBlocks, this.blockSize, i - this.blockSize);
            final TextBlockGroup rightTextBlockGroup = this.textBlockSplitter.getTextBlockGroup(textBlocks, this.blockSize, i);

            if (logger.isDebugEnabled())
                logger.debug("Applying rule for left : " + leftTextBlockGroup.getTextBlockTypeGroup() + " right :" + rightTextBlockGroup.getTextBlockTypeGroup());


            boolean addSpace;
            try {
                addSpace = this.graph.isAddSpace(leftTextBlockGroup, rightTextBlockGroup, textBlocks, i);
                if (this.stats != null)
                    this.stats.addSuccess(leftTextBlockGroup, rightTextBlockGroup);
            } catch (MissingTokenizationRuleException ex) {
                if (strict) {
                    throw ex;
                } else {
                    addSpace = false;
                    if (this.stats != null)
                        this.stats.addFail(ex);
                }
            }

            final TextBlock firstTextBlock = rightTextBlockGroup.getFirstTextBlock();
            final String textToAdd = firstTextBlock.getText();
            final TextBlockType textBlockType = firstTextBlock.getTextBlockType();
            if (addSpace || SPACE.equals(textToAdd)) {
                if (currentTokenBuilder.length() > 0){
                    tokens.add(new Token(currentTokenBuilder.toString(), currentBlockTypes));
                }

                if (SPACE.equals(textToAdd)){
                    currentTokenBuilder = new StringBuilder();
                    currentBlockTypes = new LinkedList<TextBlockType>();
                }
                else{
                    currentTokenBuilder = new StringBuilder(textToAdd);
                    currentBlockTypes.add(textBlockType);
                }
            } else {
                currentTokenBuilder.append(textToAdd);
                currentBlockTypes.add(textBlockType);
            }
        }

        if (currentTokenBuilder.length() > 0)
            tokens.add(new Token(currentTokenBuilder.toString(), currentBlockTypes));

        return tokens;
    }

    public TextTokenizerStats getStats() {
        return stats;
    }

    /**
     * Creates a default text tokenizer : block size of 2, non-strict mode,
     * without recording stats, trained with default training data, without tracking training data
     *
     * @return the built and trained tokenizer
     * @see TextTokenizer#createDefaultTextTokenizer(boolean)
     */
    public static TextTokenizer createDefaultTextTokenizer() {
        return createDefaultTextTokenizer(false);
    }

    /**
     * Creates a default text tokenizer : block size of 2, non-strict mode,
     * without recording stats, trained with default training data
     *
     * @param recordExamples Shall the trainer record stats and keep examples?
     * @return the built and trained tokenizer
     */
    public static TextTokenizer createDefaultTextTokenizer(boolean recordExamples) {
        try {
            final TokenizationGraph graph = TextTokenizerTrainer.buildDefaultTokenizationGraph(recordExamples);
            return TextTokenizer.newBuilder()
                    .blockSize(2)
                    .graph(graph)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TextTokenizerBuilder newBuilder() {
        return new TextTokenizerBuilder();
    }

    public static class TextTokenizerBuilder {
        private boolean strict = false;
        private Integer blockSize;
        private boolean recordStats = false;
        private TokenizationGraph graph;

        public TextTokenizerBuilder blockSize(Integer blockSize) {
            this.blockSize = blockSize;
            return this;
        }

        public TextTokenizerBuilder strict() {
            this.strict = true;
            return this;
        }

        public TextTokenizerBuilder recordStats() {
            this.recordStats = true;
            return this;
        }

        public TextTokenizerBuilder graph(TokenizationGraph graph) {
            this.graph = graph;
            return this;
        }

        public TextTokenizer build() {
            Validate.notNull(this.blockSize, "blockSize not provided!");
            Validate.notNull(this.graph, "graph not provided!");

            return new TextTokenizer(this);
        }
    }

    public static class TextTokenizerStats {

        private final HashMultiset<Pair<TextBlockTypeGroup, TextBlockTypeGroup>> successSet = HashMultiset.create();
        private final HashMultimap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, MissingTokenizationRuleException> failMap = HashMultimap.create();

        public void addSuccess(TextBlockGroup leftTextBlockGroup, TextBlockGroup rightTextBlockGroup) {
            successSet.add(Pair.of(leftTextBlockGroup.getTextBlockTypeGroup(), rightTextBlockGroup.getTextBlockTypeGroup()));
        }

        public void addFail(MissingTokenizationRuleException ex) {
            final TextBlockGroup leftTextBlockGroup = ex.getLeftTextBlockGroup();
            final TextBlockGroup rightTextBlockGroup = ex.getRightTextBlockGroup();

            failMap.put(Pair.of(leftTextBlockGroup.getTextBlockTypeGroup(), rightTextBlockGroup.getTextBlockTypeGroup()), ex);
        }


        /**
         * Build a map sorted by number of success.
         *
         * @return the map
         */
        public LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Integer> buildSortedSuccessMap() {
            final ImmutableMultiset<Pair<TextBlockTypeGroup, TextBlockTypeGroup>> sortedSet = Multisets.copyHighestCountFirst(successSet);

            // use LinkedHashMap to preserve insertion order
            final LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Integer> map = new LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Integer>();

            for (Pair<TextBlockTypeGroup, TextBlockTypeGroup> pair : sortedSet) {
                map.put(pair, successSet.count(pair));
            }

            return map;
        }

        /**
         * Build a map sorted by number of fails.
         *
         * @return the map
         */
        public LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Set<MissingTokenizationRuleException>> buildSortedFailMap() {

            Multiset<Pair<TextBlockTypeGroup, TextBlockTypeGroup>> failedPairSet = HashMultiset.create();
            for (Pair<TextBlockTypeGroup, TextBlockTypeGroup> pair : failMap.keys()) {
                failedPairSet.add(pair);
            }

            failedPairSet = Multisets.copyHighestCountFirst(failedPairSet);

            // use LinkedHashMap to preserve insertion order
            final LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Set<MissingTokenizationRuleException>> map
                    = new LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Set<MissingTokenizationRuleException>>();
            for (Pair<TextBlockTypeGroup, TextBlockTypeGroup> pair : failedPairSet) {
                map.put(pair, failMap.get(pair));
            }

            return map;
        }
    }
}

