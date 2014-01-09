package org.trnltk.apps.analysis;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class FrequentWordAnalysis {

    private static final Logger logger = Logger.getLogger(FrequentWordAnalysis.class);

    /**
     * @param words List of words
     * @param ratio e.g. 0.75 means finding finding most frequent words which represent 75% of all occurrences of all words
     */
    public FrequentWordAnalysisResult run(List<String> words, double ratio) {
        final FrequentWordAnalysisResult result = new FrequentWordAnalysisResult();

        final Multiset<String> wordSet = HashMultiset.create(words);
        final ImmutableMultiset<String> orderedWordSet = Multisets.copyHighestCountFirst(wordSet);
        final List<String> wordsWithMultipleOccurrence = new ArrayList<String>();
        long multipleOccurrenceCount = 0L;
        for (String word : orderedWordSet.elementSet()) {
            final int count = orderedWordSet.count(word);
            if (count < 2)
                break;
            wordsWithMultipleOccurrence.add(word);
            multipleOccurrenceCount += count;
        }

        result.countOfWordsWithMultipleOccurrences = wordsWithMultipleOccurrence.size();
        result.totalOccurrencesOfWordsWithMultipleOccurrences = multipleOccurrenceCount;
        result.percentageOfTotalOccurrencesOfWordsWithMultipleOccurrences = (Long.valueOf(multipleOccurrenceCount).doubleValue() / Integer.valueOf(words.size()).doubleValue() * 100.0);

        logger.info("Number of words that have multiple occurrences : " + result.getCountOfWordsWithMultipleOccurrences());
        logger.info("Total occurrence count of them : " + result.getTotalOccurrencesOfWordsWithMultipleOccurrences()
                + " which is " + result.getPercentageOfTotalOccurrencesOfWordsWithMultipleOccurrences() + " % of total");

        if (logger.isDebugEnabled()) {
            int N = 100;
            logger.debug("First " + N + "words with multiple occurrence:");
            for (int i = 0; i < N; i++) {
                String word = wordsWithMultipleOccurrence.get(i);
                final int count = orderedWordSet.count(word);
                logger.debug(word + "\t\t : " + count + " which is " + (Long.valueOf(count).doubleValue() / Integer.valueOf(words.size()).doubleValue() * 100.0) + " % of total");
            }
        }


        final List<String> wordsToUse = new LinkedList<String>();
        int occurrencesSoFar = 0;
        for (final String word : wordsWithMultipleOccurrence) {
            final int count = orderedWordSet.count(word);

            wordsToUse.add(word);

            occurrencesSoFar += count;
            if (occurrencesSoFar > (words.size() * ratio))
                break;
        }

        result.wordsWithEnoughOccurrences = wordsToUse;
        result.totalOccurrencesOfWordsWithEnoughOccurrences = occurrencesSoFar;
        result.percentageOfTotalOccurrencesOfWordsWithEnoughOccurrences = 100.0 * Integer.valueOf(occurrencesSoFar).doubleValue() / Integer.valueOf(words.size()).doubleValue();

        logger.info("Found " + result.getWordsWithEnoughOccurrences().size() + " words with enough occurrences");
        logger.info("Total occurrence count of them is " + result.getTotalOccurrencesOfWordsWithEnoughOccurrences()
                + " which is " + result.getPercentageOfTotalOccurrencesOfWordsWithEnoughOccurrences() + " % of all occurrences of all words");

        return result;
    }

    public static class FrequentWordAnalysisResult {
        private List<String> wordsWithEnoughOccurrences;
        private long countOfWordsWithMultipleOccurrences;
        private long totalOccurrencesOfWordsWithMultipleOccurrences;
        private double percentageOfTotalOccurrencesOfWordsWithMultipleOccurrences;
        private int totalOccurrencesOfWordsWithEnoughOccurrences;
        private double percentageOfTotalOccurrencesOfWordsWithEnoughOccurrences;

        public List<String> getWordsWithEnoughOccurrences() {
            return wordsWithEnoughOccurrences;
        }

        public long getCountOfWordsWithMultipleOccurrences() {
            return countOfWordsWithMultipleOccurrences;
        }

        public long getTotalOccurrencesOfWordsWithMultipleOccurrences() {
            return totalOccurrencesOfWordsWithMultipleOccurrences;
        }

        public double getPercentageOfTotalOccurrencesOfWordsWithMultipleOccurrences() {
            return percentageOfTotalOccurrencesOfWordsWithMultipleOccurrences;
        }

        public int getTotalOccurrencesOfWordsWithEnoughOccurrences() {
            return totalOccurrencesOfWordsWithEnoughOccurrences;
        }

        public double getPercentageOfTotalOccurrencesOfWordsWithEnoughOccurrences() {
            return percentageOfTotalOccurrencesOfWordsWithEnoughOccurrences;
        }
    }

}