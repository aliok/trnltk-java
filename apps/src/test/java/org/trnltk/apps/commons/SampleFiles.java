package org.trnltk.apps.commons;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.Validate;

import java.io.File;

import static org.apache.commons.io.FilenameUtils.concat;
import static org.trnltk.apps.commons.AppProperties.oneMillionSentencesFolder;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class SampleFiles {

    private final static File SMALL_TOKENIZED_FILE = new File(concat(oneMillionSentencesFolder(), "tbmm_b0241h_tokenized_tokenized.txt"));

    private final static ImmutableList<File> ONE_MILLION_SENTENCES_TOKENIZED_FILES = ImmutableList.of(
            new File(concat(oneMillionSentencesFolder(), "cnn-turk_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "dunya_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "hukuki-net_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "milliyet-sondakika_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "ntvmsnbc_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "kadinlar-klubu_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "radikal_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "star-gazete_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "tbmm_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "yargitay-karar_tokenized.txt")),
            new File(concat(oneMillionSentencesFolder(), "zaman_tokenized.txt"))
    );

    public static File getSmallTokenizedFile() {
        checkFileExistsAndReadable(SMALL_TOKENIZED_FILE);
        return SMALL_TOKENIZED_FILE;
    }

    /**
     * Number of words : 18362187 (18.3 M)
     * Number of distinct words : 664329
     * Number of distinct words with enough occurrences (>5) : 142212
     *
     * @return
     */
    public static ImmutableList<File> oneMillionSentencesTokenizedFiles() {
        for (File file : ONE_MILLION_SENTENCES_TOKENIZED_FILES) {
            checkFileExistsAndReadable(file);
        }

        return ONE_MILLION_SENTENCES_TOKENIZED_FILES;
    }

    private static void checkFileExistsAndReadable(File file) {
        Validate.isTrue(file.exists(), "File does not exist : " + file + " Please configure trnltk.apps.properties file.");
        Validate.isTrue(file.canRead(), "File is not readable : " + file + " Do you have the correct permissions for the file?");
    }
}


