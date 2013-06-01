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