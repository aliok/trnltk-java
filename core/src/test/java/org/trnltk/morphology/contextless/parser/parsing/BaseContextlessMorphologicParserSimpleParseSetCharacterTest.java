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

package org.trnltk.morphology.contextless.parser.parsing;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
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
import org.trnltk.morphology.contextless.parser.suffixbased.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.suffixbased.ContextlessMorphologicParserFactory;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.suffixbased.Formatter;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class BaseContextlessMorphologicParserSimpleParseSetCharacterTest extends BaseContextlessMorphologicParserTest {

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
            .put("Postp+PCNom", "Postp")
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
            .add("Aaa").add("ham").add("aga").add("Eee").add("daa").add("çoook")
            .add("Börtü")
            .add("eşşek")
            .add("vb.").add("vb")

            .add("on'da").add("onaltıotuz'da").add("dokuz'a").add("dokuz.").add("atmışbeş'e").add("doksanaltı'dan")
            .add("doksandokuz.").add("dokuzkırkbeş'te").add("onikinci.").add("oniki.").add("onbirbindokuzyüzdoksansekiz").add("ondokuz.sekiz")
            .add("otuzbeşer").add("yedi.").add("yetmişaltı.").add("yirmibeş'lik").add("yirmisekizer").add("yirmiüç'üncü").add("onsekiz.")
            .add("19.8'lik")
            .add("i").add("ii").add("ci").add("yı").add("na")
            .add("ikiyüzyirmiüç.yedi")
            .add("dortyuz-besyuz")
            .add("emmioğlu").add("vizyonuuum")
            .add("diyip").add("diyin")
            .add("cigara").add("for").add("garni").add("hard").add("ilkyardım").add("kafatasımı").add("memişler")
            .add("the").add("thermal").add("volatilite").add("yankee").add("control")
            .add(",bir")
            .add("eşgüdüm").add("garnisine").add("adaları'na").add("anfide").add("düşkırıklığına").add("habire")
            .add("krizmalarıyla").add("kuruyemiş").add("mastıralar").add("mersi").add("önyüzünü").add("krizma")
            .add("metodoljik")
            .add("selahiyet").add("selahiyeti").add("mevlüt").add("usül")

                    // "beyin meyin kalmamisti"
            .add("meyin").add("melektronik").add("mekonomi").add("mişletme").add("miçki").add("mumar").add("mefahat").add("moşku")
            .add("mırık").add("meker")


            .add("yüzeysel").add("çıtırlarla").add("vahlara").add("epeyce").add("ekononomik")

            .build();

    private static final ImmutableSet<String> EXPECTED_PARSE_RESULTS_TO_SKIP = new ImmutableSet.Builder<String>()
            .add("1+Num+Card")
            .add("70+Num+Card")
            .add("Num+Distrib")

            .add("Postp")
                    // sacmalik!
            .add("+Num+Card")       //skip all numbers because of the commented crap below
//            .add("ikibin+Num").add("sekizonikibindokuzyuzdoksansekiz").add("onsekiz").add("onyedi")
//            .add("doksandokuz").add("bindokuzyüzseksendokuz").add("onbirbindokuzyüzdoksansekiz")
//            .add("binyediyüzotuzdört").add("onbir")
            .add("onyedi+Num+Ord").add("kırkyedi")
            .add("ağbi+Noun")
            .add("birbuçuk+Num+Real").add("ikibuçuk+Num+Real").add("binüçyüz+Num+Real")
            .add("birbuçuk+Noun")
            .add("case+Noun")
            .add("flaster+Noun")
            .add("toplusözleşme+Noun").add("anayol+Noun").add("ağabeyi+Noun")
            .add("system+Noun")
            .add("planjon+Noun").add("papiş+Noun").add("ortakokul+Noun").add("praznik+Noun").add("gözbebek+Noun+")
            .add("hoşkal+Noun").add("lordlar+Noun").add("işyeri+Noun").add("yanıbaş+Noun").add("karayol+Noun")
            .add("sözet+Verb").add("terket+Verb").add("varetme+Noun").add("yeral+Verb").add("yolaç+Verb").add("elatma+Noun")
            .add("ayırdet+Verb")
            .add("bastırıvemiş")

            .add("_").add("+Prop").add("+Abbr+")

                    // -sel
            .add("dinsel+Adj").add("(1,\"toplumsal+Adj\")").add("kişisel+Adj").add("tarihsel").add("içgüdüsel")
            .add("matematiksel").add("mantıksal").add("deneysel").add("gözlemsel").add("kimyasal")
            .add("ereksel").add("nedensel").add("fiziksel").add("bütünsel").add("duygusal").add("ruhsal")
            .add("kavramsal").add("nesnel+Adj").add("algısal").add("içsel").add("geleneksel").add("madensel")
            .add("hukuksal").add("parasal")

            .build();

    protected void shouldParseParseSetN(String index, boolean printSurfaces) throws IOException {
        final InputSupplier<InputStreamReader> supplier = Resources.newReaderSupplier(Resources.getResource("simpleparsesets/simpleparseset" + index + ".txt"),
                Charset.forName("utf-8"));


        //read all in advance
        final List<Pair<String, String>> lines = CharStreams.readLines(supplier, new SimpleParseSetValidationLineProcessor());

        final int numberOfSurfaces = lines.size();
        System.out.println("Number of words to parse " + numberOfSurfaces);

        int unparsable = 0;
        int incorrectParses = 0;
        int skippedSurfaces = 0;
        int skippedExpectedParseResults = 0;

        final TreeMultiset<String> unparsableSurfaces = TreeMultiset.create();
        final TreeMultiset<String> incorrectParsedSurfaces = TreeMultiset.create();

        for (Pair<String, String> line : lines) {
            final String surfaceToParse = line.getLeft();
            if (SURFACES_TO_SKIP.contains(surfaceToParse)) {
                if (printSurfaces)
                    System.out.println("Surface '" + surfaceToParse + "' is a skippedSurface");
                skippedSurfaces++;
                continue;
            }
            final String expectedResult = line.getRight();
            if (isSkippedExpectedParseResult(expectedResult)) {
                if (printSurfaces)
                    System.out.println("Surface with expected parse result '" + expectedResult + "' is a skippedExpectedParseResult");
                skippedExpectedParseResults++;
                continue;
            }

            final List<MorphemeContainer> retrievedResults = this.parse(surfaceToParse);
            if (Character.isUpperCase(surfaceToParse.charAt(0))) {
                retrievedResults.addAll(this.parse(Character.toLowerCase(surfaceToParse.charAt(0)) + surfaceToParse.substring(1)));
            }

            if (CollectionUtils.isEmpty(retrievedResults)) {
                if (printSurfaces)
                    System.out.println("Surface '" + surfaceToParse + "' is not parseable");
                unparsableSurfaces.add(surfaceToParse + "\t" + expectedResult);
                unparsable++;
            } else {
                final Collection<String> formattedRetrievedResults = Formatter.formatMorphemeContainersWithDerivationGrouping(retrievedResults);
                if (!formattedRetrievedResults.contains(expectedResult)) {
                    if (printSurfaces) {
                        System.out.println("Surface '" + surfaceToParse + "' is parseable, but expected result '" + expectedResult + "' is not found!");
                        System.out.println("\t" + Joiner.on("\n\t").join(formattedRetrievedResults));
                    }
                    incorrectParsedSurfaces.add(expectedResult);
                    incorrectParses++;
                }
            }
        }

        final int correctParses = numberOfSurfaces - unparsable - incorrectParses - skippedSurfaces - skippedExpectedParseResults;

        System.out.println("========SUMMARY===========");
        System.out.println("Surface count             :\t\t" + numberOfSurfaces);
        System.out.println("Unparsable                :\t\t" + unparsable);
        System.out.println("Incorrect parses          :\t\t" + incorrectParses);
        System.out.println("Skipped surfaces          :\t\t" + skippedSurfaces);
        System.out.println("Skipped parse results     :\t\t" + skippedExpectedParseResults);
        System.out.println("Correct parses            :\t\t" + correctParses);
        System.out.println("Correct parse %           :\t\t" + (correctParses) * 1.0 / numberOfSurfaces * 100);

        final ImmutableMultiset<String> unparsableSurfacesByFrequencies = Multisets.copyHighestCountFirst(unparsableSurfaces);
        final ImmutableMultiset<String> incorrectParsedSurfacesByFrequencies = Multisets.copyHighestCountFirst(incorrectParsedSurfaces);

        System.out.println("=====Unparsable surfaces");
        for (Multiset.Entry<String> entry : unparsableSurfacesByFrequencies.entrySet()) {
            System.out.println(entry.getElement() + "\t\t\t" + entry.getCount());
        }

        System.out.println("=====Incorrect parsed surfaces with occurrence count > 1=====");
        for (Multiset.Entry<String> entry : incorrectParsedSurfacesByFrequencies.entrySet()) {
            if (entry.getCount() > 1)
                System.out.println(entry.getElement() + "\t\t\t" + entry.getCount());
        }
    }

    private boolean isSkippedExpectedParseResult(String expectedResult) {
        if (EXPECTED_PARSE_RESULTS_TO_SKIP.contains(expectedResult))
            return true;
        for (String s : EXPECTED_PARSE_RESULTS_TO_SKIP) {
            if (expectedResult.contains(s))
                return true;
        }

        return false;
    }


    public static class SimpleParseSetValidationLineProcessor implements LineProcessor<List<Pair<String, String>>> {
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
