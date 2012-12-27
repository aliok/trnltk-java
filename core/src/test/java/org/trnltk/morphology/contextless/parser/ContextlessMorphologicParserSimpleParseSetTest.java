/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.morphology.contextless.parser;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.Root;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ContextlessMorphologicParserSimpleParseSetTest extends BaseContextlessMorphologicParserTest {

    private HashMultimap<String, ? extends Root> originalRootMap;

    public ContextlessMorphologicParserSimpleParseSetTest() {
        this.originalRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected ContextlessMorphologicParser buildParser(HashMultimap<String, Root> clonedRootMap) {
        return ContextlessMorphologicParserFactory.createSimple();
    }

    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return HashMultimap.create(this.originalRootMap);
    }


    @Test
    public void shouldParseParseSet001() throws IOException {
        this.shouldParseParseSetN("001", false);
    }

    @Test
    public void shouldParseParseSet003() throws IOException {
        this.shouldParseParseSetN("003", false);
    }

    @Test
    public void shouldParseParseSet005() throws IOException {
        this.shouldParseParseSetN("005", false);
    }

    @Test
    @Ignore
    public void shouldParseParseSet999() throws IOException {
        this.shouldParseParseSetN("999", false);
    }

    @Test
    @Ignore
    public void shouldParseParseSet9998() throws IOException {
        this.shouldParseParseSetN("9998", false);
    }

    private void shouldParseParseSetN(String index, boolean profilingMode) throws IOException {
        final InputSupplier<InputStreamReader> supplier = Resources.newReaderSupplier(Resources.getResource("simpleparsesets/simpleparseset" + index + ".txt"),
                Charset.forName("utf-8"));

        //read all in advance
        final List<Pair<String, String>> lines = CharStreams.readLines(supplier, new LineProcessor<List<Pair<String, String>>>() {
            final ImmutableList.Builder<Pair<String, String>> builder = ImmutableList.builder();

            @Override
            public boolean processLine(final String line) throws IOException {
                if (!"#END#OF#SENTENCE#".equals(line)) {
                    final String[] split = line.split("=", 2);
                    Validate.isTrue(split.length == 2, line);
                    builder.add(Pair.of(split[0], split[1]));
                }
                return true;
            }

            @Override
            public List<Pair<String, String>> getResult() {
                return builder.build();
            }
        });

        System.out.println("Number of words to parse " + lines.size());
        final Stopwatch stopwatch = new Stopwatch();
        if (profilingMode) {
            System.out.println("Start profiling now and then press enter!");
            new Scanner(System.in).nextLine();
        }
        stopwatch.start();
        for (Pair<String, String> line : lines) {
            final String surfaceToParse = line.getLeft();
            this.parse(surfaceToParse);
        }
        stopwatch.stop();
        System.out.println("Parsed all of them in " + stopwatch);
        System.out.println("Avg time is " + stopwatch.elapsed(TimeUnit.MILLISECONDS) * 1.0 / (lines.size()) + " milliseconds");
        System.out.println("Avg time is " + stopwatch.elapsed(TimeUnit.MICROSECONDS) * 1.0 / (lines.size()) + " microseconds");
        if (profilingMode) {
            System.out.println("Stop profiling now and then press enter!");
            new Scanner(System.in).nextLine();
        }
    }

    public static void main(String[] args) throws Exception {
        final ContextlessMorphologicParserSimpleParseSetTest contextlessMorphologicParserSimpleParseSetTest = new ContextlessMorphologicParserSimpleParseSetTest();
        contextlessMorphologicParserSimpleParseSetTest.setUp();
        contextlessMorphologicParserSimpleParseSetTest.shouldParseParseSetN("003", true);
    }
}
