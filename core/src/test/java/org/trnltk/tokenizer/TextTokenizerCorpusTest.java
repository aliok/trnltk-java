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

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.trnltk.util.DiffUtil;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

public class TextTokenizerCorpusTest {
    final TokenizationGraph graph;

    TextTokenizer strictTokenizer;

    public TextTokenizerCorpusTest() throws IOException {
        graph = TextTokenizerTrainer.buildDefaultTokenizationGraph(true);

        strictTokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .recordStats()
                .strict()
                .build();
    }

    @Test
    public void tokenizedFileForTbmmJournal_b0241h_shouldNotHaveDifferenceOtherThanWhiteSpace() throws IOException {
        // tokenize file every time
        // otherwise, we need to introduce test method ordering, which is not good
        final File sentencesFile = new File(Resources.getResource("tokenizer/tbmm_b0241h_lines.txt").getFile());
        final File tokenizedFile = new File(Resources.getResource("tokenizer/tbmm_b0241h_tokenized.txt").getFile());
        createTokenizedFile(strictTokenizer, sentencesFile, tokenizedFile);

        shouldHaveNoDifferenceOtherThanWhiteSpace(sentencesFile, tokenizedFile);
    }

    protected static void createTokenizedFile(TextTokenizer tokenizer, File sentencesFile, File tokenizedFile) throws IOException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        stopWatch.suspend();

        final BufferedReader lineReader = Files.newReader(sentencesFile, Charsets.UTF_8);       // don't read the file into the memory
        final int lineCount = lineCount(sentencesFile);     // I want to know this in advance to make a ETA statement
        System.out.println("Number of lines in the file : " + lineCount);

        final BufferedWriter tokensWriter = Files.newWriter(tokenizedFile, Charsets.UTF_8);

        int tokenCount = 0;
        try {
            int index = 0;
            while(lineReader.ready()) {
                final String sentence = lineReader.readLine();
                if (index % 10000 == 0) {
                    System.out.println("Tokenizing line #" + index);
                    final long totalTimeSoFar = stopWatch.getTime();
                    final double avgTimeForALine = Long.valueOf(totalTimeSoFar).doubleValue() / index;
                    final double remainingTimeEstimate = avgTimeForALine * (lineCount - index);
                    System.out.println("For file --> ETA : " + DurationFormatUtils.formatDurationHMS((long) remainingTimeEstimate) + " So far : " + stopWatch.toString());
                }
                stopWatch.resume();
                final Iterable<Token> tokens = tokenizer.tokenize(sentence);
                stopWatch.suspend();
                final Iterator<Token> tokensIterator = tokens.iterator();
                while (tokensIterator.hasNext()) {
                    final Token token = tokensIterator.next();
                    tokensWriter.write(token.getSurface());
                    tokenCount++;
                    if (tokensIterator.hasNext())
                        tokensWriter.write(" ");
                }
                tokensWriter.write("\n");
                index++;
            }

        } finally {
            tokensWriter.close();
        }

        stopWatch.stop();

        System.out.println("Tokenized " + lineCount + " lines.");
        System.out.println("Found " + tokenCount + " tokens.");
        System.out.println("Avg time for tokenizing a line : " + Double.valueOf(stopWatch.getTime()) / Double.valueOf(lineCount) + " ms");
        System.out.println("\tProcessed : " + Double.valueOf(lineCount) / Double.valueOf(stopWatch.getTime()) * 1000d + " lines in a second");
        System.out.println("Avg time for generating a token : " + Double.valueOf(stopWatch.getTime()) / Double.valueOf(tokenCount) + " ms");
        System.out.println("\tProcessed : " + Double.valueOf(tokenCount) / Double.valueOf(stopWatch.getTime()) * 1000d + " tokens in a second");

        final TextTokenizer.TextTokenizerStats stats = tokenizer.getStats();

        if (stats != null) {
            final LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Integer> successMap = stats.buildSortedSuccessMap();
            System.out.println("Used " + successMap.size() + " distinct rules");

            final LinkedHashMap<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Set<MissingTokenizationRuleException>> failMap = stats.buildSortedFailMap();
            System.out.println("Couldn't find a rule for " + failMap.size() + " distinct specs");
            System.out.println("Printing missing rules with occurrence count:");


            int countOfMissing = 0;
            for (Map.Entry<Pair<TextBlockTypeGroup, TextBlockTypeGroup>, Set<MissingTokenizationRuleException>> entry : failMap.entrySet()) {
                final Pair<TextBlockTypeGroup, TextBlockTypeGroup> theCase = entry.getKey();
                final Set<MissingTokenizationRuleException> exceptionsForCase = entry.getValue();
                countOfMissing += exceptionsForCase.size();
                System.out.println("\t" + theCase + "\t" + exceptionsForCase.size());
                int i = 0;
                for (MissingTokenizationRuleException ex : exceptionsForCase) {
                    final String message = ex.getMessage().replace("\t", "\t\t\t");
                    final String contextStr = "..." + ex.getContextBlockGroup().getText() + "...";

                    System.out.println("\t\t" + contextStr + "\n\t\t" + message);
                    if (i == 2)      //print only 3 messages for each case
                        break;
                    i++;
                }
            }

            System.out.println("Couldn't find a rule in a total of " + countOfMissing + " times");
        }
    }

    protected static void shouldHaveNoDifferenceOtherThanWhiteSpace(final File sentencesFile, final File tokenizedFile) throws IOException {
        final List<String> sentenceLines = Files.readLines(sentencesFile, Charsets.UTF_8);
        final List<String> tokenizedLines = Files.readLines(tokenizedFile, Charsets.UTF_8);

        assertThat(sentenceLines.size(), equalTo(tokenizedLines.size()));

        final StringBuilder messagesBuilder = new StringBuilder();
        for (int lineIndex = 0; lineIndex < tokenizedLines.size(); lineIndex++) {
            final String sentenceLine = sentenceLines.get(lineIndex);
            final String tokenizedLine = tokenizedLines.get(lineIndex);

            final String[] diffLines = DiffUtil.diffLines(sentenceLine, tokenizedLine, true);
            if (diffLines != null) {
                messagesBuilder.append("Have difference other than white space in line ").append(lineIndex).append("\n")
                        .append("\t")
                        .append(Joiner.on("\n\t").join(Arrays.asList(diffLines)))
                        .append("\n\n");
            }
        }

        if (messagesBuilder.length() != 0)
            fail(messagesBuilder.toString());
    }

    public static int lineCount(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

}