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

import org.junit.Before;
import org.junit.Test;
import org.trnltk.tokenizer.TextBlock;
import org.trnltk.tokenizer.TextBlockSplitter;
import org.trnltk.tokenizer.TextBlockType;

import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class TextBlockSplitterTest {
    TextBlockSplitter splitter;

    @Before
    public void before() {
        splitter = new TextBlockSplitter();
    }

    @Test
    public void shouldSplitSimpleText() throws Exception {
        {
            final LinkedList<TextBlock> textBlocks = splitter.splitToTextParts("a b");
            assertThat(textBlocks, hasSize(3));
            assertThat(textBlocks.get(0), equalTo(new TextBlock("a", TextBlockType.Word)));
            assertThat(textBlocks.get(1), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(2), equalTo(new TextBlock("b", TextBlockType.Word)));
        }
        {
            final LinkedList<TextBlock> textBlocks = splitter.splitToTextParts("a\nb12");
            assertThat(textBlocks, hasSize(4));
            assertThat(textBlocks.get(0), equalTo(new TextBlock("a", TextBlockType.Word)));
            assertThat(textBlocks.get(1), equalTo(new TextBlock("\n", TextBlockType.Other_WhiteSpace)));
            assertThat(textBlocks.get(2), equalTo(new TextBlock("b", TextBlockType.Word)));
            assertThat(textBlocks.get(3), equalTo(new TextBlock("12", TextBlockType.Digits)));
        }
        {
            final LinkedList<TextBlock> textBlocks = splitter.splitToTextParts("WORD Word word");
            assertThat(textBlocks, hasSize(5));
            assertThat(textBlocks.get(0), equalTo(new TextBlock("WORD", TextBlockType.AllCaps_Word)));
            assertThat(textBlocks.get(1), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(2), equalTo(new TextBlock("Word", TextBlockType.Capitalized_Word)));
            assertThat(textBlocks.get(3), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(4), equalTo(new TextBlock("word", TextBlockType.Word)));
        }
    }

    @Test
    public void shouldSplitWithAbbreviations() throws Exception {
        {
            final LinkedList<TextBlock> textBlocks = splitter.splitToTextParts("bk. elma, armut vb.");
            assertThat(textBlocks, hasSize(8));
            assertThat(textBlocks.get(0), equalTo(new TextBlock("bk.", TextBlockType.Abbreviation)));
            assertThat(textBlocks.get(1), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(2), equalTo(new TextBlock("elma", TextBlockType.Word)));
            assertThat(textBlocks.get(3), equalTo(new TextBlock(",", TextBlockType.Comma)));
            assertThat(textBlocks.get(4), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(5), equalTo(new TextBlock("armut", TextBlockType.Word)));
            assertThat(textBlocks.get(6), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(7), equalTo(new TextBlock("vb.", TextBlockType.Abbreviation)));
        }
        {
            final LinkedList<TextBlock> textBlocks = splitter.splitToTextParts("Kur. Alb. Ahmet");
            assertThat(textBlocks, hasSize(5));
            assertThat(textBlocks.get(0), equalTo(new TextBlock("Kur.", TextBlockType.Abbreviation)));
            assertThat(textBlocks.get(1), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(2), equalTo(new TextBlock("Alb.", TextBlockType.Abbreviation)));
            assertThat(textBlocks.get(3), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(4), equalTo(new TextBlock("Ahmet", TextBlockType.Capitalized_Word)));
        }
        {
            final LinkedList<TextBlock> textBlocks = splitter.splitToTextParts("Gnkur. Bşk. Mehmet");
            assertThat(textBlocks, hasSize(5));
            assertThat(textBlocks.get(0), equalTo(new TextBlock("Gnkur.", TextBlockType.Abbreviation)));
            assertThat(textBlocks.get(1), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(2), equalTo(new TextBlock("Bşk.", TextBlockType.Abbreviation)));
            assertThat(textBlocks.get(3), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(4), equalTo(new TextBlock("Mehmet", TextBlockType.Capitalized_Word)));
        }
    }

    @Test
    public void shouldSplitWithRomanNumerals() throws Exception {
        {
            final LinkedList<TextBlock> textBlocks = splitter.splitToTextParts("VIII VIIII MMMMM MMMM");
            assertThat(textBlocks, hasSize(7));
            assertThat(textBlocks.get(0), equalTo(new TextBlock("VIII", TextBlockType.Roman_Numeral)));
            assertThat(textBlocks.get(1), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(2), equalTo(new TextBlock("VIIII", TextBlockType.AllCaps_Word))); // not a valid roman nr
            assertThat(textBlocks.get(3), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(4), equalTo(new TextBlock("MMMMM", TextBlockType.AllCaps_Word))); // not a valid roman nr
            assertThat(textBlocks.get(5), equalTo(new TextBlock(" ", TextBlockType.Space)));
            assertThat(textBlocks.get(6), equalTo(new TextBlock("MMMM", TextBlockType.Roman_Numeral)));
        }
    }
}
