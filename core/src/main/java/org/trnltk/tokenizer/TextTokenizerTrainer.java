package org.trnltk.tokenizer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.trnltk.tokenizer.data.TokenizerTrainingData;
import org.trnltk.tokenizer.data.TokenizerTrainingEntry;
import org.trnltk.util.DiffUtil;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TextTokenizerTrainer {

    static Logger logger = Logger.getLogger(TextTokenizerTrainer.class);

    private static final String SPACE = " ";
    private static final TextBlock SPACE_TEXT_BLOCK = new TextBlock(SPACE, TextBlockType.Space);

    private static final Pattern SPACES_IN_A_ROW = Pattern.compile(" {2,}");

    private final TextBlockSplitter textBlockSplitter;

    protected final int blockSize;
    protected final TokenizationGraph graph;

    public TextTokenizerTrainer(int blockSize, boolean recordTrainingExamples) {
        this.blockSize = blockSize;
        graph = new TokenizationGraph(recordTrainingExamples);
        textBlockSplitter = new TextBlockSplitter();
    }

    public TextTokenizerTrainer train(String text, String tokenizedText) {
        if (logger.isDebugEnabled())
            logger.debug("Training with lines\n\t" + text + "\n\t" + tokenizedText);

        Validate.isTrue(StringUtils.isNotEmpty(text), "Text is empty " + text);  //could be blank
        Validate.isTrue(StringUtils.isNotEmpty(tokenizedText), "Text is empty " + text);  //could be blank
        textShouldNotHaveMultipleSpacesInARow(text);
        textShouldNotHaveMultipleSpacesInARow(tokenizedText);
        textShouldNotEndWithSpace(text);
        textShouldNotEndWithSpace(tokenizedText);
        textsShouldHaveNoDifferenceOtherThanWhiteSpace(text, tokenizedText);

        final LinkedList<TextBlock> untokenizedTextBlocks = textBlockSplitter.splitToTextParts(text);
        final LinkedList<TextBlock> tokenizedTextBlocks = textBlockSplitter.splitToTextParts(tokenizedText);
        this.createRules(untokenizedTextBlocks, tokenizedTextBlocks);
        return this;
    }

    private void textShouldNotHaveMultipleSpacesInARow(String text) {
        if (SPACES_IN_A_ROW.matcher(text).matches())
            throw new IllegalArgumentException("Not allowed: text has multiple space chars in a row! " + text);
    }

    private void textShouldNotEndWithSpace(String text) {
        if (text.endsWith(SPACE))
            throw new IllegalArgumentException("Text ends with space char is not allowed! " + text);
    }

    private void textsShouldHaveNoDifferenceOtherThanWhiteSpace(final String text, final String tokenizedText) {
        final String[] diffLines = DiffUtil.diffLines(text, tokenizedText, true);
        if (diffLines != null) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Have difference other than white space in line ").append("\n")
                    .append("\t")
                    .append(Joiner.on("\n\t").join(Arrays.asList(diffLines)))
                    .append("\n\n");

            throw new IllegalArgumentException(messageBuilder.toString());
        }
    }

    protected void createRules(List<TextBlock> untokenizedTextBlocks, LinkedList<TextBlock> tokenizedTextBlocks) {
        this.textBlockSplitter.addTextStartsAndEnds(untokenizedTextBlocks, blockSize);
        this.textBlockSplitter.addTextStartsAndEnds(tokenizedTextBlocks, blockSize);

        int i = this.blockSize;      //untokenizedBlocksIndex
        int j = this.blockSize;      //tokenizedBlocksIndex

        while (true) {
            boolean addSpace;

            //we're out ot untokenizedBlocks
            if (i > untokenizedTextBlocks.size() - this.blockSize) {
                // assume we're out of tokenized blocks too
                // unless tokenized training str ends with space. but we don't allow it anyway
                Validate.isTrue(j > tokenizedTextBlocks.size() - this.blockSize);
                break;
            } else {
                if (untokenizedTextBlocks.get(i).equals(tokenizedTextBlocks.get(j))) {
                    //then there is no space!
                    addSpace = false;
                } else if (untokenizedTextBlocks.get(i).equals(tokenizedTextBlocks.get(j + 1))) {
                    Validate.isTrue(tokenizedTextBlocks.get(j).equals(SPACE_TEXT_BLOCK));
                    addSpace = true;
                } else {
                    throw new IllegalStateException("Found wrong aligned blocks. Possible causes : \n" +
                            "\t * There difference in two texts (tokenized and untokenized) other than space\n" +
                            "\t * There are more than one space in a row in one or both of the texts");
                }
            }

            //get left from untokenized blocks and right from untokenized blocks
            final TextBlockGroup leftTextBlockGroup = this.textBlockSplitter.getTextBlockGroup(untokenizedTextBlocks, this.blockSize, i - blockSize);
            final TextBlockGroup rightTextBlockGroup = this.textBlockSplitter.getTextBlockGroup(untokenizedTextBlocks, this.blockSize, i);

            this.addTokenizationRule(leftTextBlockGroup, rightTextBlockGroup, addSpace);

            if (addSpace)
                j++;    //align

            i++;
            j++;
        }
    }

    private void addTokenizationRule(TextBlockGroup leftTextBlockGroup, TextBlockGroup rightTextBlockGroup, boolean addSpace) {
        if (logger.isDebugEnabled())
            logger.debug("Adding tokenization rule:\n\tLeft: " + leftTextBlockGroup + "\n\tRight: " + rightTextBlockGroup + "\n\tAddSpace:" + addSpace);
        this.graph.addEdge(leftTextBlockGroup.getTextBlockTypeGroup(), rightTextBlockGroup.getTextBlockTypeGroup(), addSpace, false, rightTextBlockGroup.getTextBlocks());
    }

    public TokenizationGraph build() {
        return graph;
    }

    public static TokenizationGraph buildDefaultTokenizationGraph(boolean recordExamples) throws FileNotFoundException {
        final Splitter lineSplitter = Splitter.on(CharMatcher.anyOf("\n\r")).trimResults().omitEmptyStrings();

        final TokenizerTrainingData defaultTrainingData = TokenizerTrainingData.createDefaultTrainingData();
        final TextTokenizerTrainer trainer = new TextTokenizerTrainer(2, recordExamples);
        for (TokenizerTrainingEntry tokenizerTrainingEntry : defaultTrainingData.getEntries()) {
            try {
                // train with text block
                Validate.isTrue(StringUtils.isNotEmpty(tokenizerTrainingEntry.getText()), "text is empty " + tokenizerTrainingEntry.getText());  //could be blank
                Validate.isTrue(StringUtils.isNotEmpty(tokenizerTrainingEntry.getTknz()), "tknz is empty " + tokenizerTrainingEntry.getTknz());  //could be blank
                trainer.train(tokenizerTrainingEntry.getText(), tokenizerTrainingEntry.getTknz());

                // split text block to lines and train with them
                final Iterable<String> textLines = lineSplitter.split(tokenizerTrainingEntry.getText());
                final Iterable<String> tknzLines = lineSplitter.split(tokenizerTrainingEntry.getTknz());
                final Iterator<String> textLinesIterator = textLines.iterator();
                final Iterator<String> tknzLinesIterator = tknzLines.iterator();
                while (textLinesIterator.hasNext() && tknzLinesIterator.hasNext()) {
                    final String textLine = textLinesIterator.next();
                    final String tknzLine = tknzLinesIterator.next();
                    trainer.train(textLine, tknzLine);
                }
            } catch (RuntimeException e) {
                String msg = "Error training with entry: \n  - text: " + tokenizerTrainingEntry.getText() + "\n    tknz: " + tokenizerTrainingEntry.getTknz();
                throw new RuntimeException(msg, e);
            }
        }
        return trainer.build();
    }
}
