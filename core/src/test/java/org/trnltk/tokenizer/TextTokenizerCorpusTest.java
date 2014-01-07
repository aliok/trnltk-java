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
        createTokenizedFile(strictTokenizer, sentencesFile, tokenizedFile, false);

        shouldHaveNoDifferenceOtherThanWhiteSpace(sentencesFile, tokenizedFile);
    }

    protected static void createTokenizedFile(TextTokenizer tokenizer, File sentencesFile, File tokenizedFile, boolean silent) throws IOException {
        createTokenizedFile(tokenizer, sentencesFile, tokenizedFile, null, silent, null);
    }

    protected static void createTokenizedFile(TextTokenizer tokenizer, File sentencesFile, File tokenizedFile, File errorFile, boolean silent, TokenizationCommandCallback tokenizationCommandCallback) throws IOException {
        int N = 10000;

        final StopWatch tokenizationStopWatch = new StopWatch();
        tokenizationStopWatch.start();
        tokenizationStopWatch.suspend();

//        final BufferedReader lineReader = Files.newReader(sentencesFile, Charsets.UTF_8);       // don't read the file into the memory
//        final int lineCount = lineCount(sentencesFile);     // I want to know this in advance to make a ETA statement

        final List<String> sentences = Files.readLines(sentencesFile, Charsets.UTF_8);
        final int lineCount = sentences.size();

        if (!silent)
            System.out.println("Number of lines in the file : " + lineCount);

        final BufferedWriter tokensWriter = Files.newWriter(tokenizedFile, Charsets.UTF_8);
        final PrintWriter errorWriter = errorFile!=null ? new PrintWriter(Files.newWriter(errorFile, Charsets.UTF_8)) : new PrintWriter(System.out);


        int numberOfLinesInError = 0;
        int tokenCount = 0;
        try {
//            for (Iterator<String> iterator = sentences.iterator(); iterator.hasNext(); ) {
//              String sentence = iterator.next();
            int index;
            for (index = 0; index < sentences.size(); index++) {
                final String sentence = sentences.get(index);
                if (!silent && index % 10000 == 0) {
                    System.out.println("Tokenizing line #" + index);
                    final long totalTimeSoFar = tokenizationStopWatch.getTime();
                    final double avgTimeForALine = Long.valueOf(totalTimeSoFar).doubleValue() / index;
                    final double remainingTimeEstimate = avgTimeForALine * (lineCount - index);
                    System.out.println("For file --> ETA : " + DurationFormatUtils.formatDurationHMS((long) remainingTimeEstimate) + " So far : " + tokenizationStopWatch.toString());
                }
                if (tokenizationCommandCallback != null && index % N == 0) {
                    tokenizationCommandCallback.reportProgress(N);
                }
                tokenizationStopWatch.resume();
                final Iterable<Token> tokens;
                try{
                    tokens = tokenizer.tokenize(sentence);
                }catch (Exception e){
                    // skip the line
                    numberOfLinesInError++;
                    e.printStackTrace(errorWriter);
                    errorWriter.println();
                    tokenizationStopWatch.suspend();
                    continue;
                }
                tokenizationStopWatch.suspend();
                final Iterator<Token> tokensIterator = tokens.iterator();
                while (tokensIterator.hasNext()) {
                    final Token token = tokensIterator.next();
                    tokensWriter.write(token.getSurface());
                    tokenCount++;
                    if (tokensIterator.hasNext())
                        tokensWriter.write(" ");
                }
                tokensWriter.write("\n");
            }
            if (tokenizationCommandCallback != null) {
                //report the lines since last report
                tokenizationCommandCallback.reportProgress(index % N);
            }

        } finally {
            tokensWriter.close();
            errorWriter.close();
        }

        tokenizationStopWatch.stop();

        if (!silent) {
            System.out.println("Tokenized " + lineCount + " lines.");
            System.out.println("Found " + tokenCount + " tokens.");
            System.out.println("Avg time for tokenizing a line : " + Double.valueOf(tokenizationStopWatch.getTime()) / Double.valueOf(lineCount) + " ms");
            System.out.println("\tProcessed : " + Double.valueOf(lineCount) / Double.valueOf(tokenizationStopWatch.getTime()) * 1000d + " lines in a second");
            System.out.println("Avg time for generating a token : " + Double.valueOf(tokenizationStopWatch.getTime()) / Double.valueOf(tokenCount) + " ms");
            System.out.println("\tProcessed : " + Double.valueOf(tokenCount) / Double.valueOf(tokenizationStopWatch.getTime()) * 1000d + " tokens in a second");

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

        if (tokenizationCommandCallback != null) {
            tokenizationCommandCallback.reportFileFinished(tokenCount, numberOfLinesInError);
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

    protected class TokenizationCommand implements Runnable {

        private final TokenizationCommandCallback callback;
        private final TextTokenizer tokenizer;
        private final File sentencesFile;
        private final File tokenizedFile;
        private final File errorFile;

        protected TokenizationCommand(TokenizationCommandCallback callback, TextTokenizer tokenizer, File sentencesFile, File tokenizedFile, File errorFile) {
            this.callback = callback;
            this.tokenizer = tokenizer;
            this.sentencesFile = sentencesFile;
            this.tokenizedFile = tokenizedFile;
            this.errorFile = errorFile;
        }

        @Override
        public void run() {
            System.out.println("Tokenizing sourceFile " + sentencesFile + " to " + tokenizedFile);

            try {
                createTokenizedFile(tokenizer, sentencesFile, tokenizedFile, errorFile, true, callback);
            } catch (IOException e) {
                System.err.println("Error tokenizing file " + sentencesFile);
                e.printStackTrace();
            }
        }
    }

    protected class TokenizationCommandCallback {
        private final int lineCountOfAllFiles;
        private final StopWatch stopWatch;

        int numberOfTokenizedLines = 0;
        int numberOfTokens = 0;
        int numberOfLinesInError = 0;

        public TokenizationCommandCallback(int lineCountOfAllFiles, StopWatch stopWatch) {
            this.lineCountOfAllFiles = lineCountOfAllFiles;
            this.stopWatch = stopWatch;
        }

        public void reportProgress(int n) {
            synchronized (this) {
                numberOfTokenizedLines += n;
                final long timeSoFar = stopWatch.getTime();

                final double avgTimeForALine = Long.valueOf(timeSoFar).doubleValue() / Long.valueOf(numberOfTokenizedLines).doubleValue();
                final double avgTimeForAToken = Long.valueOf(timeSoFar).doubleValue() / Long.valueOf(numberOfTokens).doubleValue();
                final double remainingTimeEstimate = (lineCountOfAllFiles - numberOfTokenizedLines) * avgTimeForALine;
                System.out.printf("Speed: %.6f ms/line  ", avgTimeForALine);
                System.out.printf("TimeSoFar : %11s  ", DurationFormatUtils.formatDurationHMS(timeSoFar));
                System.out.printf("ETA: %11s ", DurationFormatUtils.formatDurationHMS((long) remainingTimeEstimate));
                System.out.printf("Speed: %.10f ms/token  ", avgTimeForAToken);
                System.out.printf("Lines: %8d  ", numberOfTokenizedLines);
                System.out.printf("Tokens: %10d  ", numberOfTokens);
                System.out.printf("ErrorsSoFar: %8d  ", numberOfLinesInError);
                System.out.printf("RemainingLines: %8d  ", this.lineCountOfAllFiles - numberOfTokenizedLines);
                System.out.println();
            }
        }

        public void reportFileFinished(int tokenCount, int numberOfLinesInError) {
            synchronized (this) {
                this.numberOfTokens += tokenCount;
                this.numberOfLinesInError += numberOfLinesInError;
            }
        }

        public int getNumberOfTokens() {
            return numberOfTokens;
        }
    }

}