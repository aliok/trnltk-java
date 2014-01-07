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
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.runner.RunWith;
import org.trnltk.app.App;
import org.trnltk.app.AppRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(AppRunner.class)
@Deprecated
/**
 * Finds unique words in files in a folder.
 *
 * Is not very efficient and required very huge memory if files are big.
 *
 * Use WordCountFinderApp instead as it is doing multi-pass and then merging.
 * @deprecated Use WordCountFinderApp
 */
public class UniqueWordFinderApp {

    @App("Goes thru tokenized files, finds unique words")
    public void findWordHistogram() throws InterruptedException {
        final StopWatch taskStopWatch = new StopWatch();
        taskStopWatch.start();

        final File parentFolder = new File("D:\\devl\\data\\aakindan");
        final File sourceFolder = new File(parentFolder, "src_split_tokenized");
        final File[] files = sourceFolder.listFiles();
        Validate.notNull(files);

        final List<File> filesToRead = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory())
                continue;

            filesToRead.add(file);
        }

        int NUMBER_OF_THREADS = 8;
        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        Map[] countMaps = new Map[NUMBER_OF_THREADS];
        for (int i = 0; i < countMaps.length; i++) {
            countMaps[i] = new HashMap(1000000);
        }

        for (int i = 0; i < filesToRead.size(); i++) {
            File file = filesToRead.get(i);
            //noinspection unchecked
            pool.execute(new HistogramCommand(countMaps[i % NUMBER_OF_THREADS], file));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            //System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(3000, TimeUnit.MILLISECONDS);
        }

        System.out.println("Merging countMaps");
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

        System.out.println("Sorting mergeMaps");
        final Map<String, Integer> sortedMergeMap = new TreeMap<String, Integer>(new Comparator<String>() {
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


        System.out.println("Writing to file");
        int numberOfTokens = 0;
        final File outputFile = new File(parentFolder, "wordHistogram.txt");
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = Files.newWriter(outputFile, Charsets.UTF_8);
            for (Map.Entry<String, Integer> entry : sortedMergeMap.entrySet()) {
                numberOfTokens += entry.getValue();
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


        taskStopWatch.stop();

        System.out.println("Total time :" + taskStopWatch.toString());
        System.out.println("Nr of tokens : " + numberOfTokens);
        System.out.println("Nr of unique tokens : " + sortedMergeMap.size());
    }

    private static class HistogramCommand implements Runnable {
        private final Map<String, Integer> countMap;
        private final File sourceFile;

        private HistogramCommand(Map<String, Integer> countMap, File sourceFile) {
            this.countMap = countMap;
            this.sourceFile = sourceFile;
        }

        @Override
        public void run() {
            System.out.println("Reading sourceFile " + sourceFile);
            final Splitter splitter = Splitter.on(' ').omitEmptyStrings().trimResults();

            try {
                final List<String> lines = Files.readLines(sourceFile, Charsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    if (i % 10000 == 0)
                        System.out.println("Source file " + sourceFile + "  line " + i);
                    final String line = lines.get(i);
                    final Iterable<String> words = splitter.split(line);
                    for (String word : words) {
                        final Integer count = countMap.get(word);
                        if (count == null)
                            countMap.put(word, 1);
                        else
                            countMap.put(word, count + 1);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file " + sourceFile);
                e.printStackTrace();
            }
        }
    }

}
