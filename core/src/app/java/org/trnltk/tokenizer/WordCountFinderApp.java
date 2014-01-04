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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.runner.RunWith;
import org.trnltk.app.App;
import org.trnltk.app.AppProperties;
import org.trnltk.app.AppRunner;
import org.trnltk.model.letter.TurkicLetter;
import org.trnltk.model.letter.TurkishAlphabet;
import org.trnltk.util.Constants;
import org.trnltk.util.Utilities;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Goes thru files and finds occurrences of each word. Results are stored in separate files.
 */
@RunWith(AppRunner.class)
public class WordCountFinderApp {
    private static final int NUMBER_OF_THREADS = 8;

    @App("Goes thru histogram files created with the apps below and merges them")
    public void mergeHistogramFiles() throws IOException {
        final File histogramsFolder = new File(AppProperties.largeFilesFolder(), "histograms");
        final File targetFile = new File(AppProperties.largeFilesFolder(), "wordCounts.txt");

        final File[] histogramFiles = histogramsFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                //if (!name.startsWith("wordHistogram-aa") && !name.startsWith("wordHistogram-ai"))
                //    return false;
                return name.endsWith(".txt");
            }
        });

        int totalLines = 0;
        for (File histogramFile : histogramFiles) {
            totalLines += Utilities.lineCount(histogramFile);
        }

        System.out.println("Total lines : " + totalLines);

        final BufferedWriter targetWriter = Files.newWriter(targetFile, Charsets.UTF_8);

        final List<HistogramSource> sources = Lists.newArrayList(Iterables.transform(Arrays.asList(histogramFiles), new Function<File, HistogramSource>() {
            @Override
            public HistogramSource apply(File input) {
                try {
                    return new HistogramSource(input);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to open file " + input, e);
                }
            }
        }));

        for (HistogramSource source : sources) {
            System.out.println("Initializing source " + source);
            source.readNext();
        }

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int lineIndex = 0;
        while (!sources.isEmpty()) {
            HistogramSource highestSource = null;
            for (Iterator<HistogramSource> iterator = sources.iterator(); iterator.hasNext(); ) {
                HistogramSource source = iterator.next();
                if (!source.hasNext()) {
                    iterator.remove();
                } else {
                    if (highestSource == null || source.isHigherThan(highestSource))
                        highestSource = source;
                }
            }
            if (highestSource != null) {
                //noinspection ConstantConditions
                targetWriter.write(highestSource.getCurrentWord() + " " + highestSource.getCurrentCount());
                targetWriter.newLine();
                highestSource.readNext();
                lineIndex++;
                if (lineIndex % 10000 == 0) {
                    final long timeSoFar = stopWatch.getTime();
                    double estimatedTime = new Long(timeSoFar).doubleValue() / new Integer(lineIndex).doubleValue() * new Integer(totalLines - lineIndex).doubleValue();
                    System.out.println("Found " + lineIndex + "th word of " + totalLines + " So far: " + stopWatch + "\t Remaining: " + DurationFormatUtils.formatDurationHMS((long) estimatedTime));
                }
            }
        }

        for (HistogramSource source : sources) {
            source.closeSource();
        }

        targetWriter.flush();
        targetWriter.close();

    }

    private static class HistogramSource {
        private final String name;
        private BufferedReader bufferedReader;
        private String currentWord;
        private int currentCount;

        public HistogramSource(File file) throws IOException {
            this.name = FilenameUtils.getName(file.getName());
            final CharSource charSource = Files.asCharSource(file, Charsets.UTF_8);
            bufferedReader = charSource.openBufferedStream();
        }

        public boolean hasNext() throws IOException {
            return bufferedReader.ready();
        }

        public void readNext() throws IOException {
            if (!hasNext()) {
                this.currentWord = null;
                this.currentCount = 0;
            }
            final String line = bufferedReader.readLine();
            if (StringUtils.isEmpty(line)) {

            } else {
                final int endIndexOfWord = line.lastIndexOf(' ');
                this.currentWord = line.substring(0, endIndexOfWord);
                this.currentCount = Integer.parseInt(line.substring(endIndexOfWord + 1));
            }
        }

        public void closeSource() {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                    bufferedReader = null;
                } catch (IOException e) {
                    System.err.println("Unable to close file!");
                    e.printStackTrace();
                }
        }

        public boolean isHigherThan(HistogramSource other) {
            if (this.currentCount > other.currentCount)
                return true;
            else if (this.currentCount < other.currentCount)
                return false;
            else
                return this.currentWord.compareTo(other.currentWord) >= 0;

        }

        public boolean isClosed() {
            return bufferedReader == null;
        }

        public String getCurrentWord() {
            return currentWord;
        }

        public int getCurrentCount() {
            return currentCount;
        }

        @Override
        public String toString() {
            return "HistogramSource{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @App("Goes thru tokenized files and builds word histogram for words starting with char ranges. e.g. in range of 'aa'-'ad'")
    public void findWordCount_forPredicates_charRanges() throws InterruptedException {
        final List<CharRangePredicate2> charRangePredicates = Lists.newArrayList(Iterables.filter(CharRangePredicate2.buildPredicatesForAlphaCharRanges(),
                new Predicate<CharRangePredicate2>() {
                    @Override
                    public boolean apply(WordCountFinderApp.CharRangePredicate2 input) {
//                        return input.buildName().startsWith("w") ||
//                                input.buildName().startsWith("x") ||
//                                input.buildName().startsWith("y") ||
//                                input.buildName().startsWith("z");
                        return true;

                    }
                }));

        findWordCount_forPredicates(charRangePredicates);
    }

    @App("Goes thru tokenized files and builds word histogram for words NOT starting in range of e.g 'aa'-'dz' but with char 'a'")
    public void findWordCount_forPredicates_singleChars() throws InterruptedException {
        final ArrayList<OneCharPredicate> oneCharPredicates = OneCharPredicate.buildPredicatesForSingleLetters();

        findWordCount_forPredicates(oneCharPredicates);
    }

    @App("Goes thru tokenized files and builds word histogram for words which starts with some punc characters (not all)")
    public void findWordCount_forPredicates_somePuncChars() throws InterruptedException {
        final ArrayList<StartsWithCharPredicate> startsWithCharPredicates = StartsWithCharPredicate.buildPredicatesForSomePuncChars();

        findWordCount_forPredicates(startsWithCharPredicates);
    }

    @App("Goes thru tokenized files and builds word histogram for words which aren't matched by other predicates")
    public void findWordCount_forPredicates_theRest() throws InterruptedException {
        final List<PassPredicate> allPredicates = new ArrayList<PassPredicate>();
        final List<CharRangePredicate2> charRangePredicates = CharRangePredicate2.buildPredicatesForAlphaCharRanges();
        final ArrayList<OneCharPredicate> oneCharPredicates = OneCharPredicate.buildPredicatesForSingleLetters();
        final ArrayList<StartsWithCharPredicate> startsWithCharPredicates = StartsWithCharPredicate.buildPredicatesForSomePuncChars();
        allPredicates.addAll(charRangePredicates);
        allPredicates.addAll(oneCharPredicates);
        allPredicates.addAll(startsWithCharPredicates);

        findWordCount_forPredicates(Arrays.asList(new RestPredicate(new ArrayList<PassPredicate>(allPredicates))));
    }

    public void findWordCount_forPredicates(final List<? extends PassPredicate> allPredicates) throws InterruptedException {
        final StopWatch taskStopWatch = new StopWatch();
        taskStopWatch.start();

        final File parentFolder = new File(AppProperties.largeFilesFolder());
        final File sourceFolder = new File(parentFolder, "src_split_tokenized_lines");
        final File histogramsFolder = new File(parentFolder, "histograms");
        final File[] files = sourceFolder.listFiles();
        Validate.notNull(files);

        final List<File> filesToRead = getFilesToRead(files);
        int numberOfTokens = 0;

        for (PassPredicate passPredicate : allPredicates) {
            final StopWatch passStopWatch = new StopWatch();
            passStopWatch.start();
            final File targetFile = new File(histogramsFolder + "\\wordHistogram-" + passPredicate.buildName() + ".txt");
            final OnePass onePass = new OnePass(targetFile, filesToRead, passPredicate).invoke();
            final int numberOfTokensForPass = onePass.getNumberOfTokens();
            numberOfTokens += numberOfTokensForPass;
            Map<String, Integer> sortedMergeMapForPass = onePass.getSortedMergeMap();
            passStopWatch.stop();

            System.out.println("Total time for pass :" + passStopWatch.toString());
            System.out.println("Nr of tokens for pass: " + numberOfTokensForPass);
            System.out.println("Nr of unique tokens for pass : " + sortedMergeMapForPass.size());

            sortedMergeMapForPass.clear();  // I am not very hopeful, but maybe it helps GC
        }

        taskStopWatch.stop();

        System.out.println("Total time :" + taskStopWatch.toString());
        System.out.println("Nr of tokens : " + numberOfTokens);
    }

    private List<File> getFilesToRead(File[] files) {
        final List<File> filesToRead = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory())
                continue;

            filesToRead.add(file);
        }
        return filesToRead;
    }


    private static interface PassPredicate extends Predicate<String> {
        String buildName();
    }

    private static class StartsWithCharPredicate implements PassPredicate {
        private final char startChar;

        private StartsWithCharPredicate(char startChar) {
            this.startChar = startChar;
        }

        public static ArrayList<StartsWithCharPredicate> buildPredicatesForSomePuncChars() {
            final char[] chars = ",.?!-'".toCharArray();
            final ArrayList<StartsWithCharPredicate> predicates = new ArrayList<StartsWithCharPredicate>();
            for (char aChar : chars) {
                final StartsWithCharPredicate startsWithCharPredicate = new StartsWithCharPredicate(aChar);
                predicates.add(startsWithCharPredicate);
            }
            return predicates;
        }

        @Override
        public boolean apply(String input) {
            return input.charAt(0) == startChar;
        }

        @Override
        public String buildName() {
            return "" + startChar;
        }
    }

    private static class OneCharPredicate implements PassPredicate {
        private final char startChar;

        private OneCharPredicate(char startChar) {
            this.startChar = startChar;
        }

        public static ArrayList<OneCharPredicate> buildPredicatesForSingleLetters() {
            final ArrayList<OneCharPredicate> predicates = new ArrayList<OneCharPredicate>();
            for (int i = 0; i < TurkishAlphabet.TURKISH_ALPHA_LETTERS.length; i++) {
                final TurkicLetter letter = TurkishAlphabet.TURKISH_ALPHA_LETTERS[i];
                final char theChar = letter.charValue();
                final OneCharPredicate oneCharPredicate = new OneCharPredicate(theChar);
                predicates.add(oneCharPredicate);
            }
            return predicates;
        }

        @Override
        public boolean apply(java.lang.String input) {
            if (input.length() < 2)             // accept 'a'
                return input.charAt(0) == startChar;
            if (input.charAt(0) == startChar) {         // accept 'a,' or 'a.'
                return !StringUtils.isAlpha(input.charAt(1) + "");
            }
            return false;
        }

        @Override
        public String buildName() {
            return "" + startChar;
        }
    }

    private static class CharRangePredicate2 implements PassPredicate {
        private final int rangeStartFirstCharLowerCasePosition;
        private final int rangeStartSecondCharLowerCasePosition;
        private final int rangeEndFirstCharLowerCasePosition;
        private final int rangeEndSecondCharLowerCasePosition;
        private final int rangeStartFirstCharUpperCasePosition;
        private final int rangeStartSecondCharUpperCasePosition;
        private final int rangeEndFirstCharUpperCasePosition;
        private final int rangeEndSecondCharUpperCasePosition;


        private final String rangeStart;
        private final String rangeEnd;

        private static final int[] LETTER_POSITIONS = new int[65536 * 5];
        private static final boolean[] IS_ALPHA = new boolean[65536 * 5];

        static {
            Arrays.fill(LETTER_POSITIONS, -1);
            Arrays.fill(IS_ALPHA, false);

            for (int i = 0; i < TurkishAlphabet.TURKISH_ALPHA_LETTERS.length; i++) {
                final TurkicLetter letter = TurkishAlphabet.TURKISH_ALPHA_LETTERS[i];
                char lowerCaseChar = letter.charValue;
                final char upperCaseChar = String.valueOf(lowerCaseChar).toUpperCase(Constants.TURKISH_LOCALE).charAt(0);
                IS_ALPHA[lowerCaseChar] = true;
                IS_ALPHA[upperCaseChar] = true;
                LETTER_POSITIONS[lowerCaseChar] = i;
                LETTER_POSITIONS[upperCaseChar] = i;
            }
        }

        public static List<CharRangePredicate2> buildPredicatesForAlphaCharRanges() {
            final int N = 4;
            final List<CharRangePredicate2> predicates = new ArrayList<CharRangePredicate2>();

            for (int i = 0; i < TurkishAlphabet.TURKISH_ALPHA_LETTERS.length; i++) {
                for (int j = 0; j < TurkishAlphabet.TURKISH_ALPHA_LETTERS.length; j = j + N) {
                    int secondCharEndIndex = j + N;
                    if (secondCharEndIndex > TurkishAlphabet.TURKISH_ALPHA_LETTERS.length)
                        secondCharEndIndex = TurkishAlphabet.TURKISH_ALPHA_LETTERS.length - 1;
                    String rangeStart = "" + TurkishAlphabet.TURKISH_ALPHA_LETTERS[i].charValue + TurkishAlphabet.TURKISH_ALPHA_LETTERS[j].charValue;
                    String rangeEnd = "" + TurkishAlphabet.TURKISH_ALPHA_LETTERS[i].charValue + TurkishAlphabet.TURKISH_ALPHA_LETTERS[secondCharEndIndex].charValue;
                    predicates.add(new CharRangePredicate2(rangeStart, rangeEnd));
                }
            }

            return predicates;
        }

        private CharRangePredicate2(String rangeStart, String rangeEnd) {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
            Validate.isTrue(rangeStart.length() == 2);
            Validate.isTrue(rangeEnd.length() == 2);
            Validate.isTrue(StringUtils.isAllLowerCase(rangeStart));
            Validate.isTrue(StringUtils.isAllLowerCase(rangeEnd));
            Validate.isTrue(isAlpha(rangeStart.charAt(0)));
            Validate.isTrue(isAlpha(rangeStart.charAt(1)));
            Validate.isTrue(isAlpha(rangeEnd.charAt(0)));
            Validate.isTrue(isAlpha(rangeEnd.charAt(1)));
            this.rangeStartFirstCharLowerCasePosition = getPosition(this.rangeStart.charAt(0));
            this.rangeStartSecondCharLowerCasePosition = getPosition(this.rangeStart.charAt(1));
            this.rangeEndFirstCharLowerCasePosition = getPosition(this.rangeEnd.charAt(0));
            this.rangeEndSecondCharLowerCasePosition = getPosition(this.rangeEnd.charAt(1));

            this.rangeStartFirstCharUpperCasePosition = getPosition(this.rangeStart.toUpperCase(Constants.TURKISH_LOCALE).charAt(0));
            this.rangeStartSecondCharUpperCasePosition = getPosition(this.rangeStart.toUpperCase(Constants.TURKISH_LOCALE).charAt(1));
            this.rangeEndFirstCharUpperCasePosition = getPosition(this.rangeEnd.toUpperCase(Constants.TURKISH_LOCALE).charAt(0));
            this.rangeEndSecondCharUpperCasePosition = getPosition(this.rangeEnd.toUpperCase(Constants.TURKISH_LOCALE).charAt(1));
        }

        private boolean isAlpha(char c) {
            return IS_ALPHA[c];
        }

        private int getPosition(char c) {
            return LETTER_POSITIONS[c];
        }

        @Override
        public boolean apply(java.lang.String input) {
            if (input.length() < 2)
                return false;

            final char firstCharOfInput = input.charAt(0);
            if (!isAlpha(firstCharOfInput))
                return false;

            final char secondCharOfInput = input.charAt(1);
            if (!isAlpha(secondCharOfInput))
                return false;

            final int positionOfFirstCharOfInput = getPosition(firstCharOfInput);
            if (positionOfFirstCharOfInput < rangeStartFirstCharLowerCasePosition && positionOfFirstCharOfInput < rangeStartFirstCharUpperCasePosition)
                return false;

            if (positionOfFirstCharOfInput > rangeEndFirstCharLowerCasePosition && positionOfFirstCharOfInput > rangeEndFirstCharUpperCasePosition)
                return false;

            final int positionOfSecondCharOfInput = getPosition(secondCharOfInput);
            if (positionOfSecondCharOfInput < rangeStartSecondCharLowerCasePosition && positionOfSecondCharOfInput < rangeStartSecondCharUpperCasePosition)
                return false;

            if (positionOfSecondCharOfInput > rangeEndSecondCharLowerCasePosition && positionOfSecondCharOfInput > rangeEndSecondCharUpperCasePosition)
                return false;

            return true;
        }

        @Override
        public String buildName() {
            return rangeStart + "_TO_" + rangeEnd;
        }
    }

    private static class RestPredicate implements PassPredicate {
        private final List<? extends PassPredicate> otherPredicates;

        private RestPredicate(List<? extends PassPredicate> otherPredicates) {
            this.otherPredicates = otherPredicates;
        }

        @Override
        public boolean apply(java.lang.String input) {
            for (PassPredicate otherPredicate : otherPredicates) {
                if (otherPredicate.apply(input))
                    return false;
            }
            return true;
        }

        @Override
        public String buildName() {
            return "ZZZ_THE_REST";
        }
    }

    private static class HistogramCommand implements Runnable {
        private final Map<String, Integer> countMap;
        private final File sourceFile;
        private final PassPredicate passPredicate;

        private HistogramCommand(Map<String, Integer> countMap, File sourceFile, PassPredicate passPredicate) {
            this.countMap = countMap;
            this.sourceFile = sourceFile;
            this.passPredicate = passPredicate;
        }

        @Override
        public void run() {
            System.out.println("Predicate : " + passPredicate.buildName() + " Reading sourceFile " + sourceFile);
            try {
                final List<String> lines = Files.readLines(sourceFile, Charsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    final String word = lines.get(i);
                    if (!passPredicate.apply(word))
                        continue;

                    final Integer count = countMap.get(word);
                    if (count == null)
                        countMap.put(word, 1);
                    else
                        countMap.put(word, count + 1);
                }
            } catch (IOException e) {
                System.err.println("Error reading file " + sourceFile);
                e.printStackTrace();
            }
        }
    }

    private static class OnePass {
        private final File targetFile;
        private final PassPredicate passPredicate;
        private final List<File> filesToRead;

        private Map<String, Integer> sortedMergeMap;
        private int numberOfTokens;

        public OnePass(File targetFile, List<File> filesToRead, PassPredicate passPredicate) {
            this.targetFile = targetFile;
            this.filesToRead = filesToRead;
            this.passPredicate = passPredicate;
        }

        public Map<String, Integer> getSortedMergeMap() {
            return sortedMergeMap;
        }

        public int getNumberOfTokens() {
            return numberOfTokens;
        }

        public OnePass invoke() throws InterruptedException {
            final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);
            final Map[] countMaps = buildCountMaps();

            for (int i = 0; i < filesToRead.size(); i++) {
                File file = filesToRead.get(i);
                //noinspection unchecked
                pool.execute(new HistogramCommand(countMaps[i % NUMBER_OF_THREADS], file, passPredicate));
            }

            pool.shutdown();
            while (!pool.isTerminated()) {
                //System.out.println("Waiting pool to be terminated!");
                pool.awaitTermination(3000, TimeUnit.MILLISECONDS);
            }

            System.out.println("Merging countMaps");
            final HashMap<String, Integer> mergeMap = mergeMaps(countMaps);

            System.out.println("Sorting mergeMaps");
            sortMergeMap(mergeMap);

            System.out.println("Writing to file");
            writeToFile(targetFile, sortedMergeMap);
            return this;
        }

        private Map[] buildCountMaps() {
            Map[] countMaps = new Map[NUMBER_OF_THREADS];
            for (int i = 0; i < countMaps.length; i++) {
                countMaps[i] = new HashMap(1000000);
            }
            return countMaps;
        }

        private HashMap<String, Integer> mergeMaps(Map[] countMaps) {
            final HashMap<String, Integer> mergeMap = new HashMap<String, Integer>(countMaps[0].size() * NUMBER_OF_THREADS);        //approx
            for (Map<String, Integer> countMap : countMaps) {
                for (Map.Entry<String, Integer> stringIntegerEntry : countMap.entrySet()) {
                    final String surface = stringIntegerEntry.getKey();
                    final Integer newCount = stringIntegerEntry.getValue();
                    final Integer existingCount = mergeMap.get(surface);
                    if (existingCount == null)
                        mergeMap.put(surface, newCount);
                    else
                        mergeMap.put(surface, existingCount + newCount);
                }
            }
            return mergeMap;
        }

        private void sortMergeMap(final HashMap<String, Integer> mergeMap) {
            this.sortedMergeMap = new TreeMap<String, Integer>(new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    Integer x = mergeMap.get(a);
                    Integer y = mergeMap.get(b);
                    if (x.equals(y)) {
                        return a.compareTo(b);
                    }
                    return y.compareTo(x);
                }
            });

            sortedMergeMap.putAll(mergeMap);
        }

        private void writeToFile(File targetFile, Map<String, Integer> sortedMergeMap) {
            this.numberOfTokens = 0;
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = Files.newWriter(targetFile, Charsets.UTF_8);
                for (Map.Entry<String, Integer> entry : sortedMergeMap.entrySet()) {
                    this.numberOfTokens += entry.getValue();
                    bufferedWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedWriter != null)
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        System.err.println("Unable to close file ");
                        e.printStackTrace();
                    }
            }
        }
    }
}
