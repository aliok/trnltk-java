package org.trnltk.tokenizer;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.tokenizer.*;
import org.trnltk.util.DiffUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

public class TextTokenizerCorpusTest {
    TextTokenizer relaxedTokenizer;
    TextTokenizer fastRelaxedTokenizer;
    TextTokenizer strictTokenizer;

    public TextTokenizerCorpusTest() throws FileNotFoundException {
        final TokenizationGraph graph = TextTokenizerTrainer.buildDefaultTokenizationGraph(true);

        relaxedTokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .recordStats()
                .build();

        fastRelaxedTokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .build();

        strictTokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .recordStats()
                .strict()
                .build();
    }

    @Ignore("Not a unit test. Creates tokenized file, so you can check that the difference of files manually with your IDE")
    @Test
    public void shouldTokenizeTbmmJournal_b0241h_onSource() throws IOException {
        final File sentencesFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_lines.txt");
        final File tokenizedFile = new File("shared/src/test/resources/tokenizer/tbmm_b0241h_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
    }

    @Ignore("Not a unit test. Creates tokenized file, so you can check that the difference of files manually with your IDE")
    @Test
    public void shouldTokenizeTbmm_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\tbmm.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\tbmm_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
    }

    @Ignore("Not a unit test. Creates tokenized files")
    @Test
    public void shouldTokenize1M_files_onSource() throws IOException {
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

    @Ignore("Not a unit test. Creates tokenized file, so you can check that the difference of files manually with your IDE")
    @Test
    public void shouldTokenizeNtvmsnbc_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\ntvmsnbc.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\ntvmsnbc_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
    }

    @Ignore("Not a unit test. Creates tokenized file, so you can check that the difference of files manually with your IDE")
    @Test
    public void shouldTokenizeKadinlarKulubu_1M_file_onSource() throws IOException {
        final File sentencesFile = new File("F:\\data\\1MSentences\\kadinlar-klubu.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\kadinlar-klubu_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);
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

    @Ignore("References resources which are not in the project sources")
    @Test
    public void tokenizedFileForTbmm_1M_file_shouldNotHaveDifferenceOtherThanWhiteSpace() throws IOException {
        // tokenize file every time
        // otherwise, we need to introduce test method ordering, which is not good
        final File sentencesFile = new File("F:\\data\\1MSentences\\tbmm.txt");
        final File tokenizedFile = new File("F:\\data\\1MSentences\\tbmm_tokenized.txt");
        createTokenizedFile(relaxedTokenizer, sentencesFile, tokenizedFile);

        shouldHaveNoDifferenceOtherThanWhiteSpace(sentencesFile, tokenizedFile);
    }

    private static void createTokenizedFile(TextTokenizer tokenizer, File sentencesFile, File tokenizedFile) throws IOException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        stopWatch.suspend();

        final List<String> sentences = Files.readLines(sentencesFile, Charsets.UTF_8);

        final BufferedWriter tokensWriter = Files.newWriter(tokenizedFile, Charsets.UTF_8);

        int tokenCount = 0;
        try {
            for (int index = 0; index < sentences.size(); index++) {
                final String sentence = sentences.get(index);
                if (index % 10000 == 0)
                    System.out.println("Tokenizing sentence #" + index);
                stopWatch.resume();
                final Iterable<String> tokens = tokenizer.tokenize(sentence);
                stopWatch.suspend();
                final Iterator<String> tokensIterator = tokens.iterator();
                while (tokensIterator.hasNext()) {
                    final String token = tokensIterator.next();
                    tokensWriter.write(token);
                    tokenCount++;
                    if (tokensIterator.hasNext())
                        tokensWriter.write(" ");
                }
                tokensWriter.write("\n");
            }

        } finally {
            tokensWriter.close();
        }

        stopWatch.stop();

        final TextTokenizer.TextTokenizerStats stats = tokenizer.getStats();

        System.out.println("Tokenized " + sentences.size() + " sentences.");
        System.out.println("Found " + tokenCount + " tokens.");
        System.out.println("Avg time for tokenizing a sentence : " + Double.valueOf(stopWatch.getTime()) / Double.valueOf(sentences.size()) + " ms");
        System.out.println("\tProcessed : " + Double.valueOf(sentences.size()) / Double.valueOf(stopWatch.getTime()) * 1000d + " sentences in a second");
        System.out.println("Avg time for generating a token : " + Double.valueOf(stopWatch.getTime()) / Double.valueOf(tokenCount) + " ms");
        System.out.println("\tProcessed : " + Double.valueOf(tokenCount) / Double.valueOf(stopWatch.getTime()) * 1000d + " tokens in a second");

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

    private static void shouldHaveNoDifferenceOtherThanWhiteSpace(final File sentencesFile, final File tokenizedFile) throws IOException {
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

    @Ignore("One time task!")
    @Test
    public void shouldCreateSentencesFile() throws IOException {
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