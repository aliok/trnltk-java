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
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multisets;
import com.google.common.io.Files;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.runner.RunWith;
import org.trnltk.app.App;
import org.trnltk.app.AppRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(AppRunner.class)
public class UniqueWordFileHistogramApp {

    @App("Goes thru tokenized files and builds word histogram")
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
//            if(filesToRead.size()==8)
//                break;
        }

        int NUMBER_OF_THREADS = 8;
        final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        HashMultiset[] multisets = new HashMultiset[NUMBER_OF_THREADS];
        for (int i = 0; i < multisets.length; i++) {
            multisets[i] = HashMultiset.create(1000000);
        }

        for (int i = 0; i < filesToRead.size(); i++) {
            File file = filesToRead.get(i);
            //noinspection unchecked
            pool.execute(new HistogramCommand(multisets[i % NUMBER_OF_THREADS], file));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            //System.out.println("Waiting pool to be terminated!");
            pool.awaitTermination(3000, TimeUnit.MILLISECONDS);
        }

        System.out.println("Merging sets");
        final HashMultiset<String> mergeSet = HashMultiset.create(multisets[0].elementSet().size() * NUMBER_OF_THREADS);        //approx
        for (HashMultiset<String> hashMultiset : multisets) {
            mergeSet.addAll(hashMultiset);
        }

        final ImmutableMultiset<String> sortedMergeSet = Multisets.copyHighestCountFirst(mergeSet);

        final File outputFile = new File(parentFolder, "wordHistogram.txt");
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = Files.newWriter(outputFile, Charsets.UTF_8);
            for (String word : sortedMergeSet.elementSet()) {
                final int count = sortedMergeSet.count(word);
                bufferedWriter.write(word + " " + count + "\n");
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
        System.out.println("Nr of tokens : " + sortedMergeSet.size());
        System.out.println("Nr of unique tokens : " + sortedMergeSet.elementSet().size());
    }

    private static class HistogramCommand implements Runnable {
        private final HashMultiset<String> multiset;
        private final File sourceFile;

        private HistogramCommand(HashMultiset<String> multiset, File sourceFile) {
            this.multiset = multiset;
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
                        multiset.add(word);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file " + sourceFile);
                e.printStackTrace();
            }
        }
    }

}
