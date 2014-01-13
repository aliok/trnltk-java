package org.trnltk.apps.commons;

import com.google.common.base.Charsets;
import com.google.common.collect.*;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.io.FilenameUtils.concat;
import static org.trnltk.apps.commons.AppProperties.oneMillionSentencesFolder;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class SampleFiles {

    private final static File SMALL_TOKENIZED_FILE = new File(concat(oneMillionSentencesFolder(), "tbmm_b0241h_tokenized_tokenized.txt"));

    private final static ImmutableSortedMap<String, File> ONE_MILLION_SENTENCES_TOKENIZED_FILES_WITH_IDS = new ImmutableSortedMap.Builder<String, File>(Ordering.natural())
            .put("1M-cnn-turk", new File(concat(oneMillionSentencesFolder(), "cnn-turk_tokenized.txt")))
            .put("1M-dunya", new File(concat(oneMillionSentencesFolder(), "dunya_tokenized.txt")))
            .put("1M-hukuki", new File(concat(oneMillionSentencesFolder(), "hukuki-net_tokenized.txt")))
            .put("1M-milliyet", new File(concat(oneMillionSentencesFolder(), "milliyet-sondakika_tokenized.txt")))
            .put("1M-ntvmsnbc", new File(concat(oneMillionSentencesFolder(), "ntvmsnbc_tokenized.txt")))
            .put("1M-kadinlar", new File(concat(oneMillionSentencesFolder(), "kadinlar-klubu_tokenized.txt")))
            .put("1M-radikal", new File(concat(oneMillionSentencesFolder(), "radikal_tokenized.txt")))
            .put("1M-star", new File(concat(oneMillionSentencesFolder(), "star-gazete_tokenized.txt")))
            .put("1M-tbmm", new File(concat(oneMillionSentencesFolder(), "tbmm_tokenized.txt")))
            .put("1M-yargitay", new File(concat(oneMillionSentencesFolder(), "yargitay-karar_tokenized.txt")))
            .put("1M-zaman", new File(concat(oneMillionSentencesFolder(), "zaman_tokenized.txt")))
            .build();


    private final static ImmutableSortedSet<File> ONE_MILLION_SENTENCES_TOKENIZED_FILES = ImmutableSortedSet.copyOf(
            ONE_MILLION_SENTENCES_TOKENIZED_FILES_WITH_IDS.values()
    );

    public static File getSmallTokenizedFile() {
        checkFileExistsAndReadable(SMALL_TOKENIZED_FILE);
        return SMALL_TOKENIZED_FILE;
    }

    /**
     * Number of words : 18362187 (18.3 M)
     * Number of distinct words : 664329
     * Number of distinct words with enough occurrences (>5) : 142212
     */
    public static ImmutableSortedSet<File> oneMillionSentencesTokenizedFiles() {
        for (File file : ONE_MILLION_SENTENCES_TOKENIZED_FILES) {
            checkFileExistsAndReadable(file);
        }

        return ONE_MILLION_SENTENCES_TOKENIZED_FILES;
    }

    /**
     * @see SampleFiles#oneMillionSentencesTokenizedFiles()
     */
    public static ImmutableSortedMap<String, File> oneMillionSentencesTokenizedFilesMap() {
        return ONE_MILLION_SENTENCES_TOKENIZED_FILES_WITH_IDS;
    }

    private static void checkFileExistsAndReadable(File file) {
        Validate.isTrue(file.exists(), "File does not exist : " + file + " Please configure trnltk.apps.properties file.");
        Validate.isTrue(file.canRead(), "File is not readable : " + file + " Do you have the correct permissions for the file?");
    }

    /**
     * @return Map of FileId-Lines where each line is a sentence whose surfaces are seperated with a WhiteSpace character
     */
    public static Map<String, ArrayList<String>> getLinesOfOneMillionSentences() {
        final Map<String, ArrayList<String>> tokenizedFiles = new HashMap<String, ArrayList<String>>();
        final ImmutableMap<String, File> filesMap = SampleFiles.oneMillionSentencesTokenizedFilesMap();
        for (Map.Entry<String, File> entry : filesMap.entrySet()) {
            final CharSource charSource = Files.asCharSource(entry.getValue(), Charsets.UTF_8);
            final ImmutableList<String> lines;
            try {
                lines = charSource.readLines();
            } catch (IOException e) {
                throw new RuntimeException("Cannot read file " + entry.getValue(), e);
            }
            tokenizedFiles.put(entry.getKey(), Lists.newArrayList(lines));
        }

        return tokenizedFiles;
    }
}


