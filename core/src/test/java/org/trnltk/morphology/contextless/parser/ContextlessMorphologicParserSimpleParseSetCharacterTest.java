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

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.Formatter;
import org.trnltk.morphology.model.MorphemeContainer;
import org.trnltk.morphology.model.Root;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContextlessMorphologicParserSimpleParseSetCharacterTest extends BaseContextlessMorphologicParserTest {

    private static final ImmutableMap<String, String> PARSE_RESULT_REPLACE_HACK_MAP = new ImmutableMap.Builder<String, String>()
            // for example, in simple parse set, there is a suffix Prog1 for "iyor", and Prog2 for "makta"
            // but, we don't differentiate them and use "Prog" for both
            // thus, we need a small hack for validating simple parse sets
            .put("Prog1", "Prog")
            .put("Prog2", "Prog")
            .put("Inf1", "Inf")
            .put("Inf2", "Inf")
            .put("Inf3", "Inf")
            .put("WithoutHavingDoneSo1", "WithoutHavingDoneSo")
            .put("WithoutHavingDoneSo2", "WithoutHavingDoneSo")

                    //TODO: Hastily suffix is without polarity in simple parse sets, but in TRNLTK, they need polarity
            .put("Hastily", "Hastily+Pos")

                    //TODO: BIG TODO! not supported yet!
            .put("Postp+PCNom", "Part")
            .put("Postp+PCDat", "Postp")
            .put("Postp+PCAcc", "Postp")
            .put("Postp+PCLoc", "Postp")
            .put("Postp+PCAbl", "Postp")
            .put("Postp+PCIns", "Postp")
            .put("Postp+PCGen", "Postp")

            .build();

    private static final ImmutableSet<String> SURFACES_TO_SKIP = new ImmutableSet.Builder<String>()
            .add("yapıyon").add("korkuyo").add("yakak")
            .add("Hiiç").add("Giir").add("hii").add("Geeç").add("yo").add("Yoo").add("ööö")     // mark as "Arbitrary Interjection"
            .add("Aaa").add("ham").add("aga").add("Eee")
            .add("Börtü")
            .add("eşşek")
            .add("vb.").add("vb")

                    // "beyin meyin kalmamisti"
            .add("meyin").add("melektronik").add("mekonomi").add("mişletme").add("miçki").add("mumar").add("mefahat").add("moşku")
            .add("mırık").add("meker")

            .add("Dördü")
            .add("çocuksu")

                    // words ending with arabic "Ayn"
            .add("mevzuu").add("camii").add("sanayii")

                    // "until" suffix
            .add("duyumsatıncaya").add("kızarıncaya").add("deyinceye")

            .add("psikolog").add("antropolog")

            .build();

    private HashMultimap<String, ? extends Root> originalRootMap;

    public ContextlessMorphologicParserSimpleParseSetCharacterTest() {
        this.originalRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected ContextlessMorphologicParser buildParser(HashMultimap<String, Root> clonedRootMap) {
        return ContextlessMorphologicParserFactory.createWithBigGraphForRootMap(clonedRootMap);
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

    private void shouldParseParseSetN(String index, boolean printSurfaces) throws IOException {
        final InputSupplier<InputStreamReader> supplier = Resources.newReaderSupplier(Resources.getResource("simpleparsesets/simpleparseset" + index + ".txt"),
                Charset.forName("utf-8"));


        //read all in advance
        final List<Pair<String, String>> lines = CharStreams.readLines(supplier, new SimpleParseSetValidationLineProcessor());

        final int numberOfSurfaces = lines.size();
        System.out.println("Number of words to parse " + numberOfSurfaces);

        int unparsable = 0;
        int incorrectParses = 0;
        int skipped = 0;

        for (Pair<String, String> line : lines) {
            final String surfaceToParse = line.getLeft();
            if (SURFACES_TO_SKIP.contains(surfaceToParse)) {
                if (printSurfaces)
                    System.out.println("Surface '" + surfaceToParse + "' is not skipped");
                skipped++;
            }
            final String expectedResult = line.getRight();
            final LinkedList<MorphemeContainer> retrievedResults = this.parse(surfaceToParse);
            if (CollectionUtils.isEmpty(retrievedResults)) {
                if (printSurfaces)
                    System.out.println("Surface '" + surfaceToParse + "' is not parseable");
                unparsable++;
            } else {
                final Collection<String> formattedRetrievedResults = Formatter.formatMorphemeContainersWithDerivationGrouping(retrievedResults);
                if (!formattedRetrievedResults.contains(expectedResult)) {
                    if (printSurfaces) {
                        System.out.println("Surface '" + surfaceToParse + "' is parseable, but expected result '" + expectedResult + "' is not found!");
                        System.out.println("\t" + Joiner.on("\n\t").join(formattedRetrievedResults));
                    }
                    incorrectParses++;
                }
            }
        }

        final int correctParses = numberOfSurfaces - unparsable - incorrectParses - skipped;

        System.out.println("========SUMMARY===========");
        System.out.println("Surface count   :\t\t" + numberOfSurfaces);
        System.out.println("Unparsable      :\t\t" + unparsable);
        System.out.println("Incorrect parses:\t\t" + incorrectParses);
        System.out.println("Skipped surfaces:\t\t" + skipped);
        System.out.println("Correct parses  :\t\t" + correctParses);
        System.out.println("Correct parse % :\t\t" + (correctParses) * 1.0 / numberOfSurfaces * 100);
    }


    private static class SimpleParseSetValidationLineProcessor implements LineProcessor<List<Pair<String, String>>> {
        final ImmutableList.Builder<Pair<String, String>> builder = ImmutableList.builder();

        @Override
        public boolean processLine(final String line) throws IOException {
            if (!"#END#OF#SENTENCE#".equals(line)) {
                final String[] split = line.split("=", 2);
                Validate.isTrue(split.length == 2, line);
                final String surface = split[0];
                final String expectedParseResultStr = applyParseResultReplaceHack(split[1]);
                builder.add(Pair.of(surface, expectedParseResultStr));
            }
            return true;
        }

        private String applyParseResultReplaceHack(String expectedParseResultStr) {
            for (Map.Entry<String, String> parseResultReplaceHackEntry : PARSE_RESULT_REPLACE_HACK_MAP.entrySet()) {
                expectedParseResultStr = expectedParseResultStr.replace(parseResultReplaceHackEntry.getKey(), parseResultReplaceHackEntry.getValue());
            }
            return expectedParseResultStr;
        }

        @Override
        public List<Pair<String, String>> getResult() {
            return builder.build();
        }
    }
}
