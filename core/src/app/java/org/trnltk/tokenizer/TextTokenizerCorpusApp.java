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
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.lang3.Validate;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TextTokenizerCorpusApp extends TextTokenizerCorpusTest {
    TextTokenizer relaxedTokenizer;
    TextTokenizer fastRelaxedTokenizer;

    public TextTokenizerCorpusApp() throws FileNotFoundException {
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

    //Creates tokenized file, so you can check that the difference of files manually with your IDE
    @Test
    public void tokenizeTbmmJournal_b0241h_onSource() throws IOException {
        final File sentencesFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_lines.txt");
        final File tokenizedFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
    }

    //Creates tokenized file, so you can check that the difference of files manually with your IDE
    @Test
    public void tokenizeTbmm_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\tbmm.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\tbmm_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
    }

    //Creates tokenized files
    @Test
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
            createTokenizedFile(fastRelaxedTokenizer, file, targetFile);
        }
    }

    //Creates tokenized file, so you can check that the difference of files manually with your IDE
    @Test
    public void tokenizeNtvmsnbc_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\ntvmsnbc.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\ntvmsnbc_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
    }

    //Creates tokenized file, so you can check that the difference of files manually with your IDE
    @Test
    public void tokenizeKadinlarKulubu_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\kadinlar-klubu.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\kadinlar-klubu_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
    }

    @Test
    public void tokenizedFileForTbmm_1M_file_shouldNotHaveDifferenceOtherThanWhiteSpace() throws IOException {
        // tokenize file every time
        // otherwise, we need to introduce test method ordering, which is not good
        final File sentencesFile = new File("F:\\data\\1MSentences\\tbmm.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\tbmm_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);

        shouldHaveNoDifferenceOtherThanWhiteSpace(sentencesFile, tokenizedFile);
    }

    //One time task!
    @Test
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

}