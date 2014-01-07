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

package org.trnltk.apps.tokenizer;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.runner.RunWith;
import org.trnltk.apps.commons.App;
import org.trnltk.apps.commons.AppRunner;
import org.trnltk.tokenizer.TextTokenizer;
import org.trnltk.tokenizer.TextTokenizerCorpusTest;
import org.trnltk.tokenizer.TokenizationUtils;
import org.trnltk.util.Utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(AppRunner.class)
public class TextTokenizerCorpusApp extends TextTokenizerCorpusTest {
    TextTokenizer relaxedTokenizer;
    TextTokenizer fastRelaxedTokenizer;

    public TextTokenizerCorpusApp() throws IOException {
        super();

        relaxedTokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .recordStats()
                .build();

        fastRelaxedTokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .build();
    }

    @App("Creates tokenized file, so you can check that the difference of files manually with your IDE")
    public void tokenizeTbmmJournal_b0241h_onSource() throws IOException {
        final File sentencesFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_lines.txt");
        final File tokenizedFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile, false);
    }

    @App("Creates tokenized file, so you can check that the difference of files manually with your IDE")
    public void tokenizeTbmm_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\tbmm.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\tbmm_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile, false);
    }

    @App("Creates tokenized files")
    public void tokenize1M_files_onSource() throws IOException {
        final File folder = new File("D:\\devl\\data\\1MSentences");
        final File[] files = folder.listFiles();
        Validate.notNull(files);

        final List<File> filesToTokenize = new ArrayList<File>();
        for (File file : files) {
            if (!file.getName().endsWith(".txt"))
                continue;
            if (file.getName().endsWith("_tokenized.txt"))
                continue;

            filesToTokenize.add(file);
        }

        for (File file : filesToTokenize) {
            final File targetFile = new File(file.getParent(), file.getName().substring(0, file.getName().length() - ".txt".length()) + "_tokenized.txt");
            System.out.println("Tokenizing file " + file + " to " + targetFile);
            createTokenizedFile(fastRelaxedTokenizer, file, targetFile, false);
        }
    }

    @App("Creates tokenized file, so you can check that the difference of files manually with your IDE")
    public void tokenizeNtvmsnbc_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\ntvmsnbc.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\ntvmsnbc_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile, false);
    }

    @App("Creates tokenized file, so you can check that the difference of files manually with your IDE")
    public void tokenizeKadinlarKulubu_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\kadinlar-klubu.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\kadinlar-klubu_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile, false);
    }

    @App("Creates tokenized file for TBMM corpus and checks if only difference between tokenized and plain corpus is the whitespace")
    public void tokenizedFileForTbmm_1M_file_shouldNotHaveDifferenceOtherThanWhiteSpace() throws IOException {
        // tokenize file every time
        // otherwise, we need to introduce test method ordering, which is not good
        final File sentencesFile = new File("F:\\data\\1MSentences\\tbmm.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\tbmm_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile, false);

        shouldHaveNoDifferenceOtherThanWhiteSpace(sentencesFile, tokenizedFile);
    }

    @App("One time task to create sentences file")
    public void createSentencesFile() throws IOException {
        // stupid impl!
        final File sampleFile = new File(Resources.getResource("tokenizer/tbmm_b0241h.txt").getFile());
        final File outputFile = new File(Resources.getResource("tokenizer/tbmm_b0241h_lines.txt").getFile());
        List<String> lines = Files.readLines(sampleFile, Charsets.UTF_8);

        lines = Lists.transform(lines, new Function<String, String>() {
            @Override
            public String apply(String input) {
                input = TokenizationUtils.normalizeQuotesHyphens(input);
                return input.trim().replaceAll("\\s", " ").replaceAll(" {2,}", " ").trim();
            }
        });

        final Collection<String> strings = Collections2.filter(lines, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input != null && !input.trim().isEmpty();
            }
        });

        final BufferedWriter bufferedWriter = Files.newWriter(outputFile, Charsets.UTF_8);
        try {
            for (String string : strings) {
                bufferedWriter.write(string);
                bufferedWriter.write("\n");
            }
        } finally {
            bufferedWriter.close();
        }
    }

    @App
    public void splitCorpusFiles() throws IOException {
        // ignore IOExceptions

        final File folder = new File("D:\\devl\\data\\aakindan");

        final List<File> files = new ArrayList<File>();

        for (File file : folder.listFiles()) {
            if (file.isDirectory())
                continue;
            if (file.getName().endsWith(".txt"))
                files.add(file);
        }

        int linesForEachFile = 100000;

        for (File file : files) {
            System.out.println("Processing file " + file);
            int lineCount = 0;
            int fileCount = 0;
            final BufferedReader reader = Files.newReader(file, Charsets.UTF_8);
            BufferedWriter writer = null;
            do {
                final String line = reader.readLine();
                if (lineCount % linesForEachFile == 0) {
                    if (writer != null)
                        writer.close();

                    final String srcFileName = file.getName();
                    final File targetFile = new File(file.getParent() + "\\src_split", srcFileName + "." + String.format("%04d", fileCount));
                    writer = new BufferedWriter(new FileWriter(targetFile));
                    fileCount++;
                    System.out.println("Using new target file " + targetFile);
                }
                lineCount++;

                writer.write(line + "\n");
            } while (reader.ready());

            if (writer != null)
                writer.close();
        }
    }

    @App("Creates tokenized files")
    public void tokenizeBig_files_onSource() throws IOException, InterruptedException {
        final StopWatch taskStopWatch = new StopWatch();
        taskStopWatch.start();

        final File parentFolder = new File("D:\\devl\\data\\aakindan");
        final File sourceFolder = new File(parentFolder, "src_split");
        final File targetFolder = new File(parentFolder, "src_split_tokenized");
        final File errorFolder = new File(parentFolder, "src_split_tokenization_error");
        final File[] files = sourceFolder.listFiles();
        Validate.notNull(files);

        final List<File> filesToTokenize = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory())
                continue;

            filesToTokenize.add(file);
        }

        int lineCountOfAllFiles = 0;
        for (File file : filesToTokenize) {
            lineCountOfAllFiles += Utilities.lineCount(file);
        }

        System.out.println("Total lines in all files " + lineCountOfAllFiles);

        final StopWatch callbackStopWatch = new StopWatch();
        final TokenizationCommandCallback callback = new TokenizationCommandCallback(lineCountOfAllFiles, callbackStopWatch);

        int NUMBER_OF_THREADS = 8;
        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        callbackStopWatch.start();
        for (File sourceFile : filesToTokenize) {
            final String fileBaseName = sourceFile.getName().substring(0, sourceFile.getName().length() - ".txt.0000".length());
            final String index = FilenameUtils.getExtension(sourceFile.getName());
            final File targetFile = new File(targetFolder, fileBaseName + "_tokenized.txt." + index);
            final File errorFile = new File(errorFolder, fileBaseName + "_tokenization_error.txt." + index);

            pool.execute(new TokenizationCommand(callback, fastRelaxedTokenizer, sourceFile, targetFile, errorFile));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
//            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(3000, TimeUnit.MILLISECONDS);
        }

        callbackStopWatch.stop();
        taskStopWatch.stop();
        System.out.println("Total time :" + taskStopWatch.toString());
        System.out.println("Nr of tokens : " + callback.getNumberOfTokens());
        System.out.println("Avg time : " + (taskStopWatch.getTime() * 1.0d) / (callback.getNumberOfTokens() * 1.0d) + " ms");
    }

    @App("Creates tokenized files")
    public void convertTokensToLines_Big_files_onSource() throws IOException, InterruptedException {
        final StopWatch taskStopWatch = new StopWatch();
        taskStopWatch.start();

        final File parentFolder = new File("D:\\devl\\data\\aakindan");
        final File sourceFolder = new File(parentFolder, "src_split_tokenized");
        final File targetFolder = new File(parentFolder, "src_split_tokenized_lines");
        final File[] files = sourceFolder.listFiles();
        Validate.notNull(files);

        final List<File> filesToTokenize = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory())
                continue;

            filesToTokenize.add(file);
        }

        final StopWatch callbackStopWatch = new StopWatch();

        int NUMBER_OF_THREADS = 8;
        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        callbackStopWatch.start();
        for (final File sourceFile : filesToTokenize) {
            final File targetFile = new File(targetFolder, sourceFile.getName());
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Processing file " + sourceFile);
                    BufferedWriter writer = null;
                    try {
                        final List<String> lines = Files.readLines(sourceFile, Charsets.UTF_8);
                        writer = Files.newWriter(targetFile, Charsets.UTF_8);
                        for (String line : lines) {
                            final Iterable<String> tokens = Splitter.on(' ').omitEmptyStrings().trimResults().split(line);
                            for (String token : tokens) {
                                writer.write(token);
                                writer.write("\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (writer != null)
                            try {
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }
            });
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            //            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(3000, TimeUnit.MILLISECONDS);
        }

        callbackStopWatch.stop();
        taskStopWatch.stop();
        System.out.println("Total time :" + taskStopWatch.toString());
    }

    @App("Creates tokenized files")
    public void findUniqueChars_Big_files_onSource() throws IOException, InterruptedException {
        final StopWatch taskStopWatch = new StopWatch();
        taskStopWatch.start();

        final File parentFolder = new File("D:\\devl\\data\\aakindan");
        final File targetFile = new File(parentFolder, "chars_with_occurrence.txt");
        final File sourceFolder = new File(parentFolder, "src_split_tokenized_lines");
        final File[] files = sourceFolder.listFiles();
        Validate.notNull(files);

        final List<File> filesToInvestigate = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory())
                continue;

            filesToInvestigate.add(file);
        }

        final StopWatch callbackStopWatch = new StopWatch();

        int NUMBER_OF_THREADS = 8;
        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        final boolean[] b = new boolean[65536 * 5];

        callbackStopWatch.start();
        for (final File sourceFile : filesToInvestigate) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Processing file " + sourceFile);
                    try {
                        final List<String> lines = Files.readLines(sourceFile, Charsets.UTF_8);
                        for (String token : lines) {
                            for (int i = 0; i < token.length(); i++) {
                                char aChar = token.charAt(i);
                                b[aChar] = true;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            //            System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(3000, TimeUnit.MILLISECONDS);
        }

        final BufferedWriter writer = Files.newWriter(targetFile, Charsets.UTF_8);
        for (int i = 0; i < b.length; i++) {
            boolean occurs = b[i];
            if (occurs) {
                writer.write((char) i);
                writer.write("\n");
            }
        }
        writer.close();

        callbackStopWatch.stop();
        taskStopWatch.stop();
        System.out.println("Total time :" + taskStopWatch.toString());
    }

}
