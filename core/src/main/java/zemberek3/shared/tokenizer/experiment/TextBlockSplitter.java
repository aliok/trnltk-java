package zemberek3.shared.tokenizer.experiment;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ali Ok
 */
public class TextBlockSplitter {

    private static final TextBlock SENTENCE_START_TEXT_BLOCK = new TextBlock(StringUtils.EMPTY, TextBlockType.Sentence_Start);
    private static final TextBlock SENTENCE_END_TEXT_BLOCK = new TextBlock(StringUtils.EMPTY, TextBlockType.Sentence_End);

    protected LinkedList<TextBlock> splitToTextParts(String text) {
        final LinkedList<TextBlock> textBlocks = new LinkedList<TextBlock>();
        while (StringUtils.isNotBlank(text)) {
            boolean foundOneClass = false;
            for (TextBlockType textBlockType : TextBlockType.PHYSICAL_TYPES) {
                final String matchedStr = textBlockType.findMatchFromBeginning(text);
                if (matchedStr != null) {
                    textBlocks.add(new TextBlock(matchedStr, textBlockType));
                    text = text.substring(matchedStr.length());
                    foundOneClass = true;
                    break;
                } else {
                    continue;
                }
            }
            if (!foundOneClass) {
                throw new IllegalArgumentException("Text is not matched with any of the classes: \"" + text + "\"");
            }
        }

        return textBlocks;
    }

    public TextBlockGroup getTextBlockGroup(List<TextBlock> textBlocks, int blockSize, int startIndex) {
        return new TextBlockGroup(textBlocks.subList(startIndex, startIndex + blockSize));
    }

    public void addTextStartsAndEnds(List<TextBlock> textBlocks, int blockSize) {
        for (int i = 0; i < blockSize; i++) {
            textBlocks.add(0, SENTENCE_START_TEXT_BLOCK);
        }

        for (int i = 0; i < blockSize; i++) {
            textBlocks.add(SENTENCE_END_TEXT_BLOCK);
        }
    }
}