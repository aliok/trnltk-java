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

import com.google.common.base.Joiner;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.tokenizer.data.TokenizerTrainingData;
import org.trnltk.tokenizer.data.TokenizerTrainingEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TextTokenizerDefaultTrainingTest {

    @Before
    public void setUp() throws Exception {
        // set the appender every time!
        final Enumeration currentLoggers = Logger.getLogger("org.trnltk").getLoggerRepository().getCurrentLoggers();
        while (currentLoggers.hasMoreElements()) {
            final Logger logger = (Logger) currentLoggers.nextElement();
            logger.setLevel(Level.WARN);
        }
    }

    // useful while running tests individually
    protected void turnTokenizerLoggingOn() {
        Logger.getLogger(TextTokenizer.class).setLevel(Level.DEBUG);
    }

    // useful while running tests individually
    protected void turnTrainerLoggingOn() {
        // set the appender every time!
        Logger.getLogger(TextTokenizerTrainer.class).setLevel(Level.DEBUG);
        Logger.getLogger(TokenizationGraph.class).setLevel(Level.DEBUG);
        Logger.getLogger(TokenizationGraphNode.class).setLevel(Level.DEBUG);
    }

    @Test
    public void shouldValidateDefaultRuleEntries() throws IOException {
        final TokenizationGraph tokenizationGraph = TextTokenizerTrainer.buildDefaultTokenizationGraph(true);

        final TextTokenizer tokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .recordStats()
                .strict()
                .graph(tokenizationGraph).build();


        final TokenizerTrainingData defaultTrainingData = TokenizerTrainingData.createDefaultTrainingData();
        for (TokenizerTrainingEntry tokenizerTrainingEntry : defaultTrainingData.getEntries()) {
            final String text = tokenizerTrainingEntry.getText();
            final String tknz = tokenizerTrainingEntry.getTknz();

            final List<String> tokens = tokenizer.tokenize(text);
            final String join = Joiner.on(" ").join(tokens);
            assertThat(tknz.trim(), equalTo(join.trim()));
        }

        final TextTokenizer.TextTokenizerStats stats = tokenizer.getStats();
        final LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Set<MissingTokenizationRuleException>> failMap = stats.buildSortedFailMap();

        assertThat(failMap.isEmpty(), equalTo(true));
    }
}
