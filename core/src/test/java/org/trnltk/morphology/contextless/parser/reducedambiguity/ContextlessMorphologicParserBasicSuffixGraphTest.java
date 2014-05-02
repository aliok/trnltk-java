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

package org.trnltk.morphology.contextless.parser.reducedambiguity;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.*;
import org.trnltk.morphology.contextless.parser.parsing.base.BaseContextlessMorphologicParserTest;
import org.trnltk.morphology.contextless.rootfinder.DictionaryRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.rootfinder.RootValidator;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.DisallowedPathProvider;
import org.trnltk.morphology.morphotactics.PredefinedPathProvider;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.reducedambiguity.BasicRASuffixGraph;
import org.trnltk.morphology.morphotactics.reducedambiguity.DisallowedPathProviderRAImpl;
import org.trnltk.morphology.morphotactics.reducedambiguity.PredefinedPathProviderRAImpl;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.Collection;
import java.util.List;

public class ContextlessMorphologicParserBasicSuffixGraphTest extends BaseContextlessMorphologicParserTest {

    private ContextlessMorphologicParser parser;
    private HashMultimap<String, ? extends Root> originalRootMap;

    public ContextlessMorphologicParserBasicSuffixGraphTest() {
        this.originalRootMap = RootMapFactory.createSimpleConvertCircumflexes(true);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return HashMultimap.create(this.originalRootMap);
    }

    @Override
    protected void buildParser(HashMultimap<String, Root> clonedRootMap) {
        final SuffixGraph suffixGraph = new BasicRASuffixGraph();
        suffixGraph.initialize();

        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();

        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, new PhoneticsAnalyzer(), phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(suffixGraph);

        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator());
        rootFinderChain.offer(new DictionaryRootFinder(clonedRootMap), RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        final PredefinedPathProvider predefinedPathProvider = new PredefinedPathProviderRAImpl(suffixGraph, clonedRootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPathProvider.initialize();

        final DisallowedPathProvider disallowedPathProvider = new DisallowedPathProviderRAImpl(suffixGraph);
        disallowedPathProvider.initialize();

        this.parser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPathProvider, disallowedPathProvider, rootFinderChain, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

    private final ImmutableSet<String> NOT_ALLOWED_CASES = new ImmutableSet.Builder<String>()
            // '>' char means parse result end

            .add("A3sg+P3pl")       // e.g. sokaklari
            .add("Adj+Zero+Noun+Zero+A3sg+Pnon+Nom>")        // kirmizi --> shouldn't be tagged as a noun in this stage!
            .add("Adj+With+Noun+Zero+A3sg+Pnon+Nom>")        // kirmizili --> shouldn't be tagged as a noun in this stage!
            .add("o(o)+Pron+Demons").add("o(o)+Pron+Pers")
            .build();

    @Override
    protected Collection<String> getFormattedParseResults(String surfaceToParse) {
        final List<MorphemeContainer> morphemeContainers = this.parse(surfaceToParse);

        final List<String> simpleFormattedParseResults = Lists.transform(morphemeContainers, new Function<MorphemeContainer, String>() {
            @Override
            public String apply(MorphemeContainer input) {
                return MorphemeContainerFormatter.formatMorphemeContainer(input);
            }
        });

        final List<String> withFormsFormattedParseResults = Lists.transform(morphemeContainers, new Function<MorphemeContainer, String>() {
            @Override
            public String apply(MorphemeContainer input) {
                return MorphemeContainerFormatter.formatMorphemeContainerWithForms(input);
            }
        });

        for (String formattedParseResult : simpleFormattedParseResults) {
            formattedParseResult = '<' + formattedParseResult + '>';        // add special chars to the start and end to allow matching by them. e.g. to have sth like "a parse result cannot end with XYZ"

            for (String notAllowedCase : NOT_ALLOWED_CASES) {
                if (formattedParseResult.contains(notAllowedCase)) {
                    StringBuilder failMessageBuilder = new StringBuilder("A not allowed case is found!")
                            .append("\n Surface : ").append(surfaceToParse)
                            .append("\n Not allowed case : ").append(notAllowedCase)
                            .append("\n Parse results : ");

                    for (String parseResult : withFormsFormattedParseResults) {
                        failMessageBuilder.append("\n - ").append(parseResult);
                    }

                    Assert.fail(failMessageBuilder.toString());
                }
            }
        }

        return withFormsFormattedParseResults;
    }

    @Override
    protected void assertParseCorrectForVerb(String surfaceToParse, String... expectedParseResults) {
        throw new IllegalStateException("Since this is reduced ambiguity tests, reducing ambiguity in the matcher is not appropriate!");
    }

    @Test
    public void shouldParseNounCases() {
        assertParseCorrect("sokak", "sokak(sokak)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("dikkatle", "dikkat(dikkat)+Noun+A3sg+Pnon+Ins(+ylA[le])");

        assertParseCorrect("kapıyı", "kapı(kapı)+Noun+A3sg+Pnon+Acc(+yI[yı])");
        assertParseCorrect("kapıya", "kapı(kapı)+Noun+A3sg+Pnon+Dat(+yA[ya])");
        assertParseCorrect("kapıda", "kapı(kapı)+Noun+A3sg+Pnon+Loc(dA[da])");
        assertParseCorrect("kapıdan", "kapı(kapı)+Noun+A3sg+Pnon+Abl(dAn[dan])");
        assertParseCorrect("dayının", "dayı(dayı)+Adj+Noun+Zero+A3sg+Pnon+Gen(+nIn[nın])", "dayı(dayı)+Adj+Noun+Zero+A3sg+P2sg(+In[n])+Gen(+nIn[ın])");
        assertParseCorrect("sokağın", "sokağ(sokak)+Noun+A3sg+Pnon+Gen(+nIn[ın])", "sokağ(sokak)+Noun+A3sg+P2sg(+In[ın])+Nom");
        assertParseCorrect("sokakla", "sokak(sokak)+Noun+A3sg+Pnon+Ins(+ylA[la])");

        assertParseCorrect("sokaklar", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Nom");
        assertParseCorrect("sokakları", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "sokak(sokak)+Noun+A3pl(lAr[lar])+P3sp(!I[ı])+Nom");      // P3sp means both P3sg and P3pl are possible.
        assertParseCorrect("sokaklara", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Dat(+yA[a])");
        assertParseCorrect("sokaklarda", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Loc(dA[da])");
        assertParseCorrect("sokaklardan", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Abl(dAn[dan])");
        assertParseCorrect("sokakların", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Gen(+nIn[ın])", "sokak(sokak)+Noun+A3pl(lAr[lar])+P2sg(+In[ın])+Nom");
        assertParseCorrect("sokaklarla", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Ins(+ylA[la])");
    }

    @Test
    public void shouldParseNounToNounDerivations() {
        assertParseCorrect("kitapçık", "kitap(kitap)+Noun+A3sg+Pnon+Nom+Noun+Dim(cIk[çık])+A3sg+Pnon+Nom");
        assertParseCorrect("kitapçığa", "kitap(kitap)+Noun+A3sg+Pnon+Nom+Noun+Dim(cIk[çığ])+A3sg+Pnon+Dat(+yA[a])");
        assertParseCorrect("parçacık", "parça(parça)+Noun+A3sg+Pnon+Nom+Noun+Dim(cIk[cık])+A3sg+Pnon+Nom");
        assertParseCorrect("parçacıklarla", "parça(parça)+Noun+A3sg+Pnon+Nom+Noun+Dim(cIk[cık])+A3pl(lAr[lar])+Pnon+Ins(+ylA[la])");
        assertNotParsable("parçacıkcık");
    }

    @Test
    public void shouldParseNounToAdjectiveDerivations() {
        removeRoots("kut");

        assertParseCorrect("kutulu", "kutu(kutu)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lu])");
        assertParseCorrect("kutulum", "kutu(kutu)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lu])+Noun+Zero+A3sg+P1sg(+Im[m])+Nom");    //derive back to noun
        assertParseCorrect("kutusuz", "kutu(kutu)+Noun+A3sg+Pnon+Nom+Adj+Without(sIz[suz])");
        assertParseCorrect("kutumsu", "kutu(kutu)+Noun+A3sg+Pnon+Nom+Adj+JustLike(+ImsI[msu])");
        assertParseCorrect("telefonumsu", "telefon(telefon)+Noun+A3sg+Pnon+Nom+Adj+JustLike(+ImsI[umsu])");
        assertParseCorrect("meleğimsi", "meleğ(melek)+Noun+A3sg+Pnon+Nom+Adj+JustLike(+ImsI[imsi])");

        assertParseCorrect("korucu", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])");
        assertParseCorrect("korucuyu", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])+Noun+Zero+A3sg+Pnon+Acc(+yI[yu])");
        assertParseCorrect("korucuya", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])+Noun+Zero+A3sg+Pnon+Dat(+yA[ya])");
        assertParseCorrect("korucuda", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])+Noun+Zero+A3sg+Pnon+Loc(dA[da])");
        assertParseCorrect("korucudan", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])+Noun+Zero+A3sg+Pnon+Abl(dAn[dan])");
        assertParseCorrect("korucunun", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])+Noun+Zero+A3sg+Pnon+Gen(+nIn[nun])", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])+Noun+Zero+A3sg+P2sg(+In[n])+Gen(+nIn[un])");
        assertParseCorrect("korucuyla", "koru(koru)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[cu])+Noun+Zero+A3sg+Pnon+Ins(+ylA[yla])");
    }

    @Test
    public void shouldParseNounToVerbDerivations() {
        //heyecanlan
        //TODO
    }

    @Test
    public void shouldParsePositiveVerbTenses() {
        assertParseCorrect("yaparım", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+A1sg(+Im[ım])");
        assertParseCorrect("yaparsın", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+A2sg(sIn[sın])");
        assertParseCorrect("yapar", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+A3sg", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Adj+Zero");
        assertParseCorrect("yaparız", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+A1pl(+Iz[ız])");

        assertParseCorrect("yapıyorum", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+A1sg(+Im[um])");
        assertParseCorrect("yapıyorsun", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+A2sg(sIn[sun])");
        assertParseCorrect("yapıyor", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+A3sg");
        assertParseCorrect("yapıyoruz", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+A1pl(+Iz[uz])");

        assertParseCorrect("yapmaktayım", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+A1sg(yIm[yım])");
        assertParseCorrect("yapmaktasın", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+A2sg(sIn[sın])");
        assertParseCorrect("yapmakta", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+A3sg", "yap(yapmak)+Verb+Pos+Noun+Inf(mAk[mak])+A3sg+Pnon+Loc(dA[ta])");
        assertParseCorrect("yapmaktayız", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+A1pl(yIz[yız])");

        assertParseCorrect("yapacağım",
                "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acağ])+A1sg(+Im[ım])",
                "yap(yapmak)+Verb+Pos+Adj+FutPart(+yAcAk[acağ])+P1sg(+Im[ım])",
                "yap(yapmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P1sg(+Im[ım])+Nom",
                "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[ım])+Nom"
        );
        assertParseCorrect("yapacaksın", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+A2sg(sIn[sın])");
        assertParseCorrect("yapacak",
                "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+A3sg",
                "yap(yapmak)+Verb+Pos+Adj+FutPart(+yAcAk[acak])+Pnon",
                "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero",
                "yap(yapmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom"
        );
        assertParseCorrect("yapacağız", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acağ])+A1pl(+Iz[ız])");

        assertParseCorrect("yaptım", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("yaptın", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A2sg(n[n])");
        assertParseCorrect("yaptı", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A3sg");
        assertParseCorrect("yaptık", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A1pl(!k[k])", "yap(yapmak)+Verb+Pos+Adj+PastPart(dIk[tık])+Pnon", "yap(yapmak)+Verb+Pos+Noun+PastPart(dIk[tık])+A3sg+Pnon+Nom");

        assertParseCorrect("yapmışım", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A1sg(+Im[ım])");
        assertParseCorrect("yapmışsın", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A2sg(sIn[sın])");
        assertParseCorrect("yapmış", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A3sg", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+Adj+Zero");
        assertParseCorrect("yapmışız", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A1pl(+Iz[ız])");

        assertParseCorrect("çeviririm", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+A1sg(+Im[im])");
        assertParseCorrect("çevirirsin", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+A2sg(sIn[sin])");
        assertParseCorrect("çevirir", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+A3sg", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+Adj+Zero");

        assertParseCorrect("çeviriyorum", "çevir(çevirmek)+Verb+Pos+Prog(Iyor[iyor])+A1sg(+Im[um])");
        assertParseCorrect("çeviriyorsun", "çevir(çevirmek)+Verb+Pos+Prog(Iyor[iyor])+A2sg(sIn[sun])");
        assertParseCorrect("çeviriyor", "çevir(çevirmek)+Verb+Pos+Prog(Iyor[iyor])+A3sg");

        assertParseCorrect("çevirmekteyim", "çevir(çevirmek)+Verb+Pos+Prog(mAktA[mekte])+A1sg(yIm[yim])");
        assertParseCorrect("çevirmektesin", "çevir(çevirmek)+Verb+Pos+Prog(mAktA[mekte])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmekte", "çevir(çevirmek)+Verb+Pos+Prog(mAktA[mekte])+A3sg", "çevir(çevirmek)+Verb+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Loc(dA[te])");

        assertParseCorrect("çevireceğim",
                "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[eceğ])+A1sg(+Im[im])",
                "çevir(çevirmek)+Verb+Pos+Adj+FutPart(+yAcAk[eceğ])+P1sg(+Im[im])",
                "çevir(çevirmek)+Verb+Pos+Noun+FutPart(+yAcAk[eceğ])+A3sg+P1sg(+Im[im])+Nom",
                "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[eceğ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom"
        );
        assertParseCorrect("çevireceksin", "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[ecek])+A2sg(sIn[sin])");
        assertParseCorrect("çevirecek",
                "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[ecek])+A3sg",
                "çevir(çevirmek)+Verb+Pos+Adj+FutPart(+yAcAk[ecek])+Pnon",
                "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[ecek])+Adj+Zero",
                "çevir(çevirmek)+Verb+Pos+Noun+FutPart(+yAcAk[ecek])+A3sg+Pnon+Nom"
        );

        assertParseCorrect("çevirdim", "çevir(çevirmek)+Verb+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("çevirdin", "çevir(çevirmek)+Verb+Pos+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("çevirdi", "çevir(çevirmek)+Verb+Pos+Past(dI[di])+A3sg");

        assertParseCorrect("çevirmişim", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+A1sg(+Im[im])", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("çevirmişsin", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmiş", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+A3sg", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero");


        removeRootsExceptTheOneWithPrimaryPos("el", PrimaryPos.Verb);

        assertParseCorrect("elerim", "ele(elemek)+Verb+Pos+Aor(r[r])+A1sg(+Im[im])");
        assertParseCorrect("elersin", "ele(elemek)+Verb+Pos+Aor(r[r])+A2sg(sIn[sin])");
        assertParseCorrect("eler", "ele(elemek)+Verb+Pos+Aor(r[r])+A3sg", "ele(elemek)+Verb+Pos+Aor(r[r])+Adj+Zero");

        assertParseCorrect("eliyorum", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+A1sg(+Im[um])");
        assertParseCorrect("eliyorsun", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+A2sg(sIn[sun])");
        assertParseCorrect("eliyor", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+A3sg");

        assertParseCorrect("elemekteyim", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+A1sg(yIm[yim])");
        assertParseCorrect("elemektesin", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+A2sg(sIn[sin])");
        assertParseCorrect("elemekte", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+A3sg", "ele(elemek)+Verb+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Loc(dA[te])");

        assertParseCorrect("eleyeceğim",
                "ele(elemek)+Verb+Pos+Fut(+yAcAk[yeceğ])+A1sg(+Im[im])",
                "ele(elemek)+Verb+Pos+Adj+FutPart(+yAcAk[yeceğ])+P1sg(+Im[im])",
                "ele(elemek)+Verb+Pos+Noun+FutPart(+yAcAk[yeceğ])+A3sg+P1sg(+Im[im])+Nom",
                "ele(elemek)+Verb+Pos+Fut(+yAcAk[yeceğ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom"
        );
        assertParseCorrect("eleyeceksin", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+A2sg(sIn[sin])");
        assertParseCorrect("eleyecek",
                "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+A3sg",
                "ele(elemek)+Verb+Pos+Adj+FutPart(+yAcAk[yecek])+Pnon",
                "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Adj+Zero",
                "ele(elemek)+Verb+Pos+Noun+FutPart(+yAcAk[yecek])+A3sg+Pnon+Nom"
        );

        assertParseCorrect("eledim", "ele(elemek)+Verb+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertNotParsable("eleydim");
        assertNotParsable("eteydim");
        assertParseCorrect("eledin", "ele(elemek)+Verb+Pos+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("eledi", "ele(elemek)+Verb+Pos+Past(dI[di])+A3sg");

        assertParseCorrect("elemişim", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("elemişsin", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("elemiş", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+A3sg", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
    }


    @Test
    public void shouldParseNegativeVerbTenses() {
        assertParseCorrect("yapmam", "yap(yapmak)+Verb+Neg(mA[ma])+Aor+A1sg(+Im[m])", "yap(yapmak)+Verb+Pos+Noun+Inf(mA[ma])+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("yapmazsın", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+A2sg(sIn[sın])");
        assertParseCorrect("yapmaz", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+A3sg", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Adj+Zero");
        assertParseCorrect("yapmayız", "yap(yapmak)+Verb+Neg(mA[ma])+Aor+A1pl(yIz[yız])");

        assertParseCorrect("yapmıyorum", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+A1sg(+Im[um])");
        assertParseCorrect("yapmıyorsun", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+A2sg(sIn[sun])");
        assertParseCorrect("yapmıyor", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+A3sg");

        assertParseCorrect("yapmamaktayım", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+A1sg(yIm[yım])");
        assertParseCorrect("yapmamaktasın", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+A2sg(sIn[sın])");
        assertParseCorrect("yapmamakta", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+A3sg", "yap(yapmak)+Verb+Neg(mA[ma])+Noun+Inf(mAk[mak])+A3sg+Pnon+Loc(dA[ta])");

        assertParseCorrect("yapmayacağım",
                "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+A1sg(+Im[ım])",
                "yap(yapmak)+Verb+Neg(mA[ma])+Adj+FutPart(+yAcAk[yacağ])+P1sg(+Im[ım])",
                "yap(yapmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P1sg(+Im[ım])+Nom",
                "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[ım])+Nom"
        );
        assertParseCorrect("yapmayacaksın", "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+A2sg(sIn[sın])");
        assertParseCorrect("yapmayacak",
                "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+A3sg",
                "yap(yapmak)+Verb+Neg(mA[ma])+Adj+FutPart(+yAcAk[yacak])+Pnon",
                "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero",
                "yap(yapmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3sg+Pnon+Nom"
        );

        assertParseCorrect("yapmadım", "yap(yapmak)+Verb+Neg(mA[ma])+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("yapmadın", "yap(yapmak)+Verb+Neg(mA[ma])+Past(dI[dı])+A2sg(n[n])");
        assertParseCorrect("yapmadı", "yap(yapmak)+Verb+Neg(mA[ma])+Past(dI[dı])+A3sg");

        assertParseCorrect("yapmamışım", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+A1sg(+Im[ım])");
        assertParseCorrect("yapmamışsın", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+A2sg(sIn[sın])");
        assertParseCorrect("yapmamış", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+A3sg", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+Adj+Zero");


        removeRoots("çevirme");
        assertParseCorrect("çevirmem", "çevir(çevirmek)+Verb+Neg(mA[me])+Aor+A1sg(+Im[m])", "çevir(çevirmek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("çevirmezsin", "çevir(çevirmek)+Verb+Neg(mA[me])+Aor(z[z])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmez", "çevir(çevirmek)+Verb+Neg(mA[me])+Aor(z[z])+A3sg", "çevir(çevirmek)+Verb+Neg(mA[me])+Aor(z[z])+Adj+Zero");

        assertParseCorrect("çevirmiyorum", "çevir(çevirmek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+A1sg(+Im[um])");
        assertParseCorrect("çevirmiyorsun", "çevir(çevirmek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+A2sg(sIn[sun])");
        assertParseCorrect("çevirmiyor", "çevir(çevirmek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+A3sg");

        assertParseCorrect("çevirmemekteyim", "çevir(çevirmek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+A1sg(yIm[yim])");
        assertParseCorrect("çevirmemektesin", "çevir(çevirmek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmemekte", "çevir(çevirmek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+A3sg", "çevir(çevirmek)+Verb+Neg(mA[me])+Noun+Inf(mAk[mek])+A3sg+Pnon+Loc(dA[te])");

        assertParseCorrect("çevirmeyeceğim",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Fut(+yAcAk[yeceğ])+A1sg(+Im[im])",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Adj+FutPart(+yAcAk[yeceğ])+P1sg(+Im[im])",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Noun+FutPart(+yAcAk[yeceğ])+A3sg+P1sg(+Im[im])+Nom",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Fut(+yAcAk[yeceğ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom"
        );
        assertParseCorrect("çevirmeyeceksin", "çevir(çevirmek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmeyecek",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+A3sg",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Adj+FutPart(+yAcAk[yecek])+Pnon",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Adj+Zero",
                "çevir(çevirmek)+Verb+Neg(mA[me])+Noun+FutPart(+yAcAk[yecek])+A3sg+Pnon+Nom"
        );

        assertParseCorrect("çevirmedim", "çevir(çevirmek)+Verb+Neg(mA[me])+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("çevirmedin", "çevir(çevirmek)+Verb+Neg(mA[me])+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("çevirmedi", "çevir(çevirmek)+Verb+Neg(mA[me])+Past(dI[di])+A3sg");

        assertParseCorrect("çevirmemişim", "çevir(çevirmek)+Verb+Neg(mA[me])+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("çevirmemişsin", "çevir(çevirmek)+Verb+Neg(mA[me])+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmemiş", "çevir(çevirmek)+Verb+Neg(mA[me])+Narr(mIş[miş])+A3sg", "çevir(çevirmek)+Verb+Neg(mA[me])+Narr(mIş[miş])+Adj+Zero");


        assertParseCorrect("elemem", "ele(elemek)+Verb+Neg(mA[me])+Aor+A1sg(+Im[m])", "ele(elemek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("elemezsin", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+A2sg(sIn[sin])");
        assertParseCorrect("elemez", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+A3sg", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+Adj+Zero");

        assertParseCorrect("elemiyorum", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+A1sg(+Im[um])");
        assertParseCorrect("elemiyorsun", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+A2sg(sIn[sun])");
        assertParseCorrect("elemiyor", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+A3sg");

        assertParseCorrect("elememekteyim", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+A1sg(yIm[yim])");
        assertParseCorrect("elememektesin", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+A2sg(sIn[sin])");
        assertParseCorrect("elememekte", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+A3sg", "ele(elemek)+Verb+Neg(mA[me])+Noun+Inf(mAk[mek])+A3sg+Pnon+Loc(dA[te])");

        assertParseCorrect("elemeyeceğim",
                "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yeceğ])+A1sg(+Im[im])",
                "ele(elemek)+Verb+Neg(mA[me])+Adj+FutPart(+yAcAk[yeceğ])+P1sg(+Im[im])",
                "ele(elemek)+Verb+Neg(mA[me])+Noun+FutPart(+yAcAk[yeceğ])+A3sg+P1sg(+Im[im])+Nom",
                "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yeceğ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom"
        );
        assertParseCorrect("elemeyeceksin", "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+A2sg(sIn[sin])");
        assertParseCorrect("elemeyecek",
                "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+A3sg",
                "ele(elemek)+Verb+Neg(mA[me])+Adj+FutPart(+yAcAk[yecek])+Pnon",
                "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Adj+Zero",
                "ele(elemek)+Verb+Neg(mA[me])+Noun+FutPart(+yAcAk[yecek])+A3sg+Pnon+Nom"
        );

        assertParseCorrect("elemedim", "ele(elemek)+Verb+Neg(mA[me])+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("elemedin", "ele(elemek)+Verb+Neg(mA[me])+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("elemedi", "ele(elemek)+Verb+Neg(mA[me])+Past(dI[di])+A3sg");

        assertParseCorrect("elememişim", "ele(elemek)+Verb+Neg(mA[me])+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("elememişsin", "ele(elemek)+Verb+Neg(mA[me])+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("elememiş", "ele(elemek)+Verb+Neg(mA[me])+Narr(mIş[miş])+A3sg", "ele(elemek)+Verb+Neg(mA[me])+Narr(mIş[miş])+Adj+Zero");
    }


    @Test
    public void shouldParsePositiveMultipleVerbTenses() {
        assertParseCorrect("yapardım", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("yapardın", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A2sg(n[n])");
        assertParseCorrect("yapardı", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A3sg");

        assertParseCorrect("yapıyordum", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+Past(dI[du])+A1sg(+Im[m])");
        assertParseCorrect("yapıyordun", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+Past(dI[du])+A2sg(n[n])");
        assertParseCorrect("yapıyordu", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+Past(dI[du])+A3sg");

        assertParseCorrect("yapmaktaydım", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+Past(ydI[ydı])+A1sg(+Im[m])");
        assertParseCorrect("yapmaktaydın", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+Past(ydI[ydı])+A2sg(n[n])");
        assertParseCorrect("yapmaktaydı", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+Past(ydI[ydı])+A3sg");

        assertParseCorrect("yapacaktım", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("yapacaktın", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Past(dI[tı])+A2sg(n[n])");
        assertParseCorrect("yapacaktı", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Past(dI[tı])+A3sg");

        assertParseCorrect("yapmıştım", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("yapmıştın", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+Past(dI[tı])+A2sg(n[n])");
        assertParseCorrect("yapmıştı", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+Past(dI[tı])+A3sg");


        assertParseCorrect("yaparmışım", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Narr(mIş[mış])+A1sg(+Im[ım])");
        assertParseCorrect("yaparmışsın", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Narr(mIş[mış])+A2sg(sIn[sın])");
        assertParseCorrect("yaparmış", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Narr(mIş[mış])+A3sg");

        assertParseCorrect("yapıyormuşum", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+Narr(mIş[muş])+A1sg(+Im[um])");
        assertParseCorrect("yapıyormuşsun", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+Narr(mIş[muş])+A2sg(sIn[sun])");
        assertParseCorrect("yapıyormuş", "yap(yapmak)+Verb+Pos+Prog(Iyor[ıyor])+Narr(mIş[muş])+A3sg");

        assertParseCorrect("yapmaktaymışım", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+Narr(ymIş[ymış])+A1sg(+Im[ım])");
        assertParseCorrect("yapmaktaymışsın", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+Narr(ymIş[ymış])+A2sg(sIn[sın])");
        assertParseCorrect("yapmaktaymış", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+Narr(ymIş[ymış])+A3sg");

        assertParseCorrect("yapacakmışım", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Narr(mIş[mış])+A1sg(+Im[ım])");
        assertParseCorrect("yapacakmışsın", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Narr(mIş[mış])+A2sg(sIn[sın])");
        assertParseCorrect("yapacakmış", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Narr(mIş[mış])+A3sg");


        assertParseCorrect("elerdim", "ele(elemek)+Verb+Pos+Aor(r[r])+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("elerdin", "ele(elemek)+Verb+Pos+Aor(r[r])+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("elerdi", "ele(elemek)+Verb+Pos+Aor(r[r])+Past(dI[di])+A3sg");

        assertParseCorrect("eliyordum", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+Past(dI[du])+A1sg(+Im[m])");
        assertParseCorrect("eliyordun", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+Past(dI[du])+A2sg(n[n])");
        assertParseCorrect("eliyordu", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+Past(dI[du])+A3sg");

        assertParseCorrect("elemekteydim", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+Past(ydI[ydi])+A1sg(+Im[m])");
        assertParseCorrect("elemekteydin", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+Past(ydI[ydi])+A2sg(n[n])");
        assertParseCorrect("elemekteydi", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+Past(ydI[ydi])+A3sg");

        assertParseCorrect("eleyecektim", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("eleyecektin", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Past(dI[ti])+A2sg(n[n])");
        assertParseCorrect("eleyecekti", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Past(dI[ti])+A3sg");

        assertParseCorrect("elemiştim", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("elemiştin", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Past(dI[ti])+A2sg(n[n])");
        assertParseCorrect("elemişti", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Past(dI[ti])+A3sg");


        assertParseCorrect("elermişim", "ele(elemek)+Verb+Pos+Aor(r[r])+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("elermişsin", "ele(elemek)+Verb+Pos+Aor(r[r])+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("elermiş", "ele(elemek)+Verb+Pos+Aor(r[r])+Narr(mIş[miş])+A3sg");

        assertParseCorrect("eliyormuşum", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+Narr(mIş[muş])+A1sg(+Im[um])");
        assertParseCorrect("eliyormuşsun", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+Narr(mIş[muş])+A2sg(sIn[sun])");
        assertParseCorrect("eliyormuş", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+Narr(mIş[muş])+A3sg");

        assertParseCorrect("elemekteymişim", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+Narr(ymIş[ymiş])+A1sg(+Im[im])");
        assertParseCorrect("elemekteymişsin", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+Narr(ymIş[ymiş])+A2sg(sIn[sin])");
        assertParseCorrect("elemekteymiş", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+Narr(ymIş[ymiş])+A3sg");

        assertParseCorrect("eleyecekmişim", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("eleyecekmişsin", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("eleyecekmiş", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Narr(mIş[miş])+A3sg");
    }

    @Test
    public void shouldParseNegativeMultipleVerbTenses() {
        assertParseCorrect("yapmazdım", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("yapmazdın", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Past(dI[dı])+A2sg(n[n])");
        assertParseCorrect("yapmazdı", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Past(dI[dı])+A3sg");

        assertParseCorrect("yapmıyordum", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+Past(dI[du])+A1sg(+Im[m])");
        assertParseCorrect("yapmıyordun", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+Past(dI[du])+A2sg(n[n])");
        assertParseCorrect("yapmıyordu", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+Past(dI[du])+A3sg");

        assertParseCorrect("yapmamaktaydım", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+Past(ydI[ydı])+A1sg(+Im[m])");
        assertParseCorrect("yapmamaktaydın", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+Past(ydI[ydı])+A2sg(n[n])");
        assertParseCorrect("yapmamaktaydı", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+Past(ydI[ydı])+A3sg");

        assertParseCorrect("yapmayacaktım", "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("yapmayacaktın", "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Past(dI[tı])+A2sg(n[n])");
        assertParseCorrect("yapmayacaktı", "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Past(dI[tı])+A3sg");

        assertParseCorrect("yapmamıştım", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("yapmamıştın", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+Past(dI[tı])+A2sg(n[n])");
        assertParseCorrect("yapmamıştı", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+Past(dI[tı])+A3sg");


        assertParseCorrect("yapmazmışım", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Narr(mIş[mış])+A1sg(+Im[ım])");
        assertParseCorrect("yapmazmışsın", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Narr(mIş[mış])+A2sg(sIn[sın])");
        assertParseCorrect("yapmazmış", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Narr(mIş[mış])+A3sg");

        assertParseCorrect("yapmıyormuşum", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+Narr(mIş[muş])+A1sg(+Im[um])");
        assertParseCorrect("yapmıyormuşsun", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+Narr(mIş[muş])+A2sg(sIn[sun])");
        assertParseCorrect("yapmıyormuş", "yap(yapmak)+Verb+Neg(m[m])+Prog(Iyor[ıyor])+Narr(mIş[muş])+A3sg");

        assertParseCorrect("yapmamaktaymışım", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+Narr(ymIş[ymış])+A1sg(+Im[ım])");
        assertParseCorrect("yapmamaktaymışsın", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+Narr(ymIş[ymış])+A2sg(sIn[sın])");
        assertParseCorrect("yapmamaktaymış", "yap(yapmak)+Verb+Neg(mA[ma])+Prog(mAktA[makta])+Narr(ymIş[ymış])+A3sg");

        assertParseCorrect("yapmayacakmışım", "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Narr(mIş[mış])+A1sg(+Im[ım])");
        assertParseCorrect("yapmayacakmışsın", "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Narr(mIş[mış])+A2sg(sIn[sın])");
        assertParseCorrect("yapmayacakmış", "yap(yapmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Narr(mIş[mış])+A3sg");


        assertParseCorrect("elemezdim", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("elemezdin", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("elemezdi", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+Past(dI[di])+A3sg");

        assertParseCorrect("elemiyordum", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+Past(dI[du])+A1sg(+Im[m])");
        assertParseCorrect("elemiyordun", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+Past(dI[du])+A2sg(n[n])");
        assertParseCorrect("elemiyordu", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+Past(dI[du])+A3sg");

        assertParseCorrect("elememekteydim", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+Past(ydI[ydi])+A1sg(+Im[m])");
        assertParseCorrect("elememekteydin", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+Past(ydI[ydi])+A2sg(n[n])");
        assertParseCorrect("elememekteydi", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+Past(ydI[ydi])+A3sg");

        assertParseCorrect("elemeyecektim", "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("elemeyecektin", "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Past(dI[ti])+A2sg(n[n])");
        assertParseCorrect("elemeyecekti", "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Past(dI[ti])+A3sg");

        assertParseCorrect("elememiştim", "ele(elemek)+Verb+Neg(mA[me])+Narr(mIş[miş])+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("elememiştin", "ele(elemek)+Verb+Neg(mA[me])+Narr(mIş[miş])+Past(dI[ti])+A2sg(n[n])");
        assertParseCorrect("elememişti", "ele(elemek)+Verb+Neg(mA[me])+Narr(mIş[miş])+Past(dI[ti])+A3sg");


        assertParseCorrect("elemezmişim", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("elemezmişsin", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("elemezmiş", "ele(elemek)+Verb+Neg(mA[me])+Aor(z[z])+Narr(mIş[miş])+A3sg");

        assertParseCorrect("elemiyormuşum", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+Narr(mIş[muş])+A1sg(+Im[um])");
        assertParseCorrect("elemiyormuşsun", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+Narr(mIş[muş])+A2sg(sIn[sun])");
        assertParseCorrect("elemiyormuş", "ele(elemek)+Verb+Neg(m[m])+Prog(Iyor[iyor])+Narr(mIş[muş])+A3sg");

        assertParseCorrect("elememekteymişim", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+Narr(ymIş[ymiş])+A1sg(+Im[im])");
        assertParseCorrect("elememekteymişsin", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+Narr(ymIş[ymiş])+A2sg(sIn[sin])");
        assertParseCorrect("elememekteymiş", "ele(elemek)+Verb+Neg(mA[me])+Prog(mAktA[mekte])+Narr(ymIş[ymiş])+A3sg");

        assertParseCorrect("elemeyecekmişim", "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("elemeyecekmişsin", "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("elemeyecekmiş", "ele(elemek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Narr(mIş[miş])+A3sg");
    }

    @Test
    public void shouldParseSomeVerbs() {
        assertParseCorrect("yapardık", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A1pl(!k[k])");
        assertParseCorrect("yapardınız", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A2pl(nIz[nız])");
        assertParseCorrect("yapardılar", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A3pl(lAr[lar])");
        assertParseCorrect("yaparlardı", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A3sg");

    }

    @Test
    public void shouldParseModals() {
        assertParseCorrect("eleyebilir", "ele(elemek)+Verb+Verb+Able(+yAbil[yebil])+Pos+Aor(+Ir[ir])+A3sg)", "ele(elemek)+Verb+Verb+Able(+yAbil[yebil])+Pos+Aor(+Ir[ir])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("eleyemez", "ele(elemek)+Verb+Verb+Able(+yA[ye])+Neg(mA[me])+Aor+A3sg(z[z])", "ele(elemek)+Verb+Verb+Able(+yA[ye])+Neg(mA[me])+Aor(+z[z])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("eleyebilirim", "ele(elemek)+Verb+Verb+Able(+yAbil[yebil])+Pos+Aor(+Ir[ir])+A1sg(+Im[im])");
        assertParseCorrect("eleyemem", "ele(elemek)+Verb+Verb+Able(+yA[ye])+Neg(mA[me])+Aor+A1sg(+Im[m])");
        assertParseCorrect("eleyemezsin", "ele(elemek)+Verb+Verb+Able(+yA[ye])+Neg(mA[me])+Aor(z[z])+A2sg(sIn[sin])");
        assertParseCorrect("yapamazdım", "yap(yapmak)+Verb+Verb+Able(+yA[a])+Neg(mA[ma])+Aor(z[z])+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("eleyemeyeceğim", "ele(elemek)+Verb+Verb+Able(+yA[ye])+Neg(mA[me])+Fut(+yAcAk[yeceğ])+A1sg(+Im[im])", "ele(elemek)+Verb+Verb+Able(+yA[ye])+Neg(mA[me])+Adj+FutPart(+yAcAk[yeceğ])+P1sg(+Im[im])");

        assertParseCorrect("yapabilirdim", "yap(yapmak)+Verb+Verb+Able(+yAbil[abil])+Pos+Aor(+Ir[ir])+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("yapabileceksin", "yap(yapmak)+Verb+Verb+Able(+yAbil[abil])+Pos+Fut(+yAcAk[ecek])+A2sg(sIn[sin])");

        assertParseCorrect("yapmalıyım", "yap(yapmak)+Verb+Pos+Neces(mAl!I[malı])+A1sg(yIm[yım])");
        assertParseCorrect("yapmalıydım", "yap(yapmak)+Verb+Pos+Neces(mAl!I[malı])+Past(ydI[ydı])+A1sg(+Im[m])");
        assertParseCorrect("yapmamalıyım", "yap(yapmak)+Verb+Neg(mA[ma])+Neces(mAl!I[malı])+A1sg(yIm[yım])");
        assertParseCorrect("yapmamalıydım", "yap(yapmak)+Verb+Neg(mA[ma])+Neces(mAl!I[malı])+Past(ydI[ydı])+A1sg(+Im[m])");

        assertParseCorrect("elemeliymiş", "ele(elemek)+Verb+Pos+Neces(mAl!I[meli])+Narr(ymIş[ymiş])+A3sg");
        assertParseCorrect("elememeliymiş", "ele(elemek)+Verb+Neg(mA[me])+Neces(mAl!I[meli])+Narr(ymIş[ymiş])+A3sg");

        assertParseCorrect("eleyeyim", "ele(elemek)+Verb+Pos+Opt(yA[ye])+A1sg(yIm[yim])");
        assertParseCorrect("eleyesin", "ele(elemek)+Verb+Pos+Opt(yA[ye])+A2sg(sIn[sin])");
        assertParseCorrect("elemeyeydim", "ele(elemek)+Verb+Neg(mA[me])+Opt(yA[ye])+Past(ydI[ydi])+A1sg(+Im[m])");

        assertParseCorrect("eleyebilmeliydim", "ele(elemek)+Verb+Verb+Able(+yAbil[yebil])+Pos+Neces(mAl!I[meli])+Past(ydI[ydi])+A1sg(+Im[m])");
        assertParseCorrect("eleyememeliydi", "ele(elemek)+Verb+Verb+Able(+yA[ye])+Neg(mA[me])+Neces(mAl!I[meli])+Past(ydI[ydi])+A3sg");
    }

    @Test
    public void shouldParsePossessives() {
        assertParseCorrect("kalemim", "kalem(kalem)+Noun+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("kalemimi", "kalem(kalem)+Noun+A3sg+P1sg(+Im[im])+Acc(+yI[i])");
        assertParseCorrect("kalemimden", "kalem(kalem)+Noun+A3sg+P1sg(+Im[im])+Abl(dAn[den])");
        assertParseCorrect("kalemimin", "kalem(kalem)+Noun+A3sg+P1sg(+Im[im])+Gen(+nIn[in])");

        assertParseCorrect("danam", "dana(dana)+Noun+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("danamı", "dana(dana)+Noun+A3sg+P1sg(+Im[m])+Acc(+yI[ı])");

        assertParseCorrect("kitabın", "kitab(kitap)+Noun+A3sg+Pnon+Gen(+nIn[ın])", "kitab(kitap)+Noun+A3sg+P2sg(+In[ın])+Nom");
        assertParseCorrect("kitabını", "kitab(kitap)+Noun+A3sg+P2sg(+In[ın])+Acc(+yI[ı])", "kitab(kitap)+Noun+A3sg+P3sg(+sI[ı])+Acc(nI[nı])");

        assertParseCorrect("danası", "dana(dana)+Noun+A3sg+P3sg(+sI[sı])+Nom");
        assertParseCorrect("danasında", "dana(dana)+Noun+A3sg+P3sg(+sI[sı])+Loc(ndA[nda])");

        assertParseCorrect("danamız", "dana(dana)+Noun+A3sg+P1pl(+ImIz[mız])+Nom");
        assertParseCorrect("danamızdan", "dana(dana)+Noun+A3sg+P1pl(+ImIz[mız])+Abl(dAn[dan])");

        assertParseCorrect("sandalyeniz", "sandalye(sandalye)+Noun+A3sg+P2pl(+InIz[niz])+Nom");
        assertParseCorrect("sandalyelerinizden", "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P2pl(+InIz[iniz])+Abl(dAn[den])");

        assertParseCorrect("sandalyeleri",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+Pnon+Acc(+yI[i])",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P3sp(!I[i])+Nom"
        );
        assertParseCorrect("sandalyelerini",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P2sg(+In[in])+Acc(+yI[i])",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P3sp(!I[i])+Acc(nI[ni])"
        );
        assertParseCorrect("sandalyelerine",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P2sg(+In[in])+Dat(+yA[e])",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P3sp(!I[i])+Dat(nA[ne])"
        );
        assertParseCorrect("sandalyelerinde",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P2sg(+In[in])+Loc(dA[de])",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P3sp(!I[i])+Loc(ndA[nde])"
        );
        assertParseCorrect("sandalyelerinin",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P2sg(+In[in])+Gen(+nIn[in])",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P3sp(!I[i])+Gen(+nIn[nin])"
        );
        assertParseCorrect("sandalyeleriyle",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P3sg(+sI[i])+Ins(+ylA[yle])",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P3sp(!I[i])+Ins(+ylA[yle])"
        );
        assertParseCorrect("sandalyelerinle",
                "sandalye(sandalye)+Noun+A3pl(lAr[ler])+P2sg(+In[in])+Ins(+ylA[le])"
        );

    }

    @Test
    public void shouldParseSomeAdverbs() {
        assertParseCorrect("aceleten", "aceleten(aceleten)+Adv");
    }


    @Test
    public void shouldParsePronouns() {
        // remove some roots to make the test simple
        removeRoots("on", "ona", "bend", "bun", "bizle", "se", "bur");
        removeRootsExceptTheOneWithPrimaryPos("ben", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("ban", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("san", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("biz", PrimaryPos.Pronoun);

        assertParseCorrect("ben", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom");
        assertParseCorrect("sen", "sen(sen)+Pron+Pers+A2sg+Pnon+Nom");
        assertParseCorrect("o", "o(o)+Det", "o(o)+Pron+A3sg+Pnon+Nom");
        assertParseCorrect("biz", "biz(biz)+Pron+Pers+A1pl+Pnon+Nom");
        assertParseCorrect("siz", "siz(siz)+Pron+Pers+A2pl+Pnon+Nom");
        assertParseCorrect("onlar", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Nom");
        assertParseCorrect("bizler", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Nom");
        assertParseCorrect("sizler", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+Nom");

        assertParseCorrect("beni", "ben(ben)+Pron+Pers+A1sg+Pnon+Acc(i[i])");
        assertParseCorrect("seni", "sen(sen)+Pron+Pers+A2sg+Pnon+Acc(i[i])");
        assertParseCorrect("onu", "o(o)+Pron+A3sg+Pnon+Acc(nu[nu])");
        assertParseCorrect("bizi", "biz(biz)+Pron+Pers+A1pl+Pnon+Acc(i[i])");
        assertParseCorrect("sizi", "siz(siz)+Pron+Pers+A2pl+Pnon+Acc(i[i])");
        assertParseCorrect("onları", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Acc(ı[ı])");
        assertParseCorrect("bizleri", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Acc(i[i])");
        assertParseCorrect("sizleri", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+Acc(i[i])");

        assertParseCorrect("bana", "ban(ben)+Pron+Pers+A1sg+Pnon+Dat(a[a])");
        assertParseCorrect("sana", "san(sen)+Pron+Pers+A2sg+Pnon+Dat(a[a])");
        assertParseCorrect("ona", "o(o)+Pron+A3sg+Pnon+Dat(na[na])");
        assertParseCorrect("bize", "biz(biz)+Pron+Pers+A1pl+Pnon+Dat(e[e])");
        assertParseCorrect("size", "siz(siz)+Pron+Pers+A2pl+Pnon+Dat(e[e])");
        assertParseCorrect("onlara", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Dat(a[a])");
        assertParseCorrect("bizlere", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Dat(e[e])");
        assertParseCorrect("sizlere", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+Dat(e[e])");

        assertParseCorrect("bende", "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])");
        assertParseCorrect("sende", "sen(sen)+Pron+Pers+A2sg+Pnon+Loc(de[de])");
        assertParseCorrect("onda", "o(o)+Pron+A3sg+Pnon+Loc(nda[nda])");
        assertParseCorrect("bizde", "biz(biz)+Pron+Pers+A1pl+Pnon+Loc(de[de])");
        assertParseCorrect("sizde", "siz(siz)+Pron+Pers+A2pl+Pnon+Loc(de[de])");
        assertParseCorrect("onlarda", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Loc(da[da])");
        assertParseCorrect("bizlerde", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Loc(de[de])");
        assertParseCorrect("sizlerde", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+Loc(de[de])");

        assertParseCorrect("benden", "ben(ben)+Pron+Pers+A1sg+Pnon+Abl(den[den])");
        assertParseCorrect("senden", "sen(sen)+Pron+Pers+A2sg+Pnon+Abl(den[den])");
        assertParseCorrect("ondan", "o(o)+Pron+A3sg+Pnon+Abl(ndan[ndan])");
        assertParseCorrect("bizden", "biz(biz)+Pron+Pers+A1pl+Pnon+Abl(den[den])");
        assertParseCorrect("sizden", "siz(siz)+Pron+Pers+A2pl+Pnon+Abl(den[den])");
        assertParseCorrect("onlardan", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Abl(dan[dan])");
        assertParseCorrect("bizlerden", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Abl(den[den])");
        assertParseCorrect("sizlerden", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+Abl(den[den])");

        assertParseCorrect("benim", "ben(ben)+Pron+Pers+A1sg+Pnon+Gen(im[im])");
        assertParseCorrect("senin", "sen(sen)+Pron+Pers+A2sg+Pnon+Gen(in[in])");
        assertParseCorrect("onun", "o(o)+Pron+A3sg+Pnon+Gen(nun[nun])");
        assertParseCorrect("bizim", "biz(biz)+Pron+Pers+A1pl+Pnon+Gen(im[im])");
        assertParseCorrect("sizin", "siz(siz)+Pron+Pers+A2pl+Pnon+Gen(in[in])");
        assertParseCorrect("onların", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Gen(ın[ın])");
        assertParseCorrect("bizlerin", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Gen(in[in])");
        assertParseCorrect("sizlerin", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+Gen(in[in])");

        assertParseCorrect("benimle", "ben(ben)+Pron+Pers+A1sg+Pnon+Ins(imle[imle])");
        assertParseCorrect("seninle", "sen(sen)+Pron+Pers+A2sg+Pnon+Ins(inle[inle])");
        assertParseCorrect("onunla", "o(o)+Pron+A3sg+Pnon+Ins(nunla[nunla])");
        assertParseCorrect("bizimle", "biz(biz)+Pron+Pers+A1pl+Pnon+Ins(imle[imle])");
        assertParseCorrect("sizinle", "siz(siz)+Pron+Pers+A2pl+Pnon+Ins(inle[inle])");
        assertParseCorrect("onlarla", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Ins(la[la])");
        assertParseCorrect("bizlerle", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Ins(le[le])");
        assertParseCorrect("sizlerle", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+Ins(le[le])");

        assertParseCorrect("benle", "ben(ben)+Pron+Pers+A1sg+Pnon+Ins(le[le])");
        assertParseCorrect("senle", "sen(sen)+Pron+Pers+A2sg+Pnon+Ins(le[le])");
        assertParseCorrect("onla", "o(o)+Pron+A3sg+Pnon+Ins(nla[nla])");
        assertParseCorrect("bizle", "biz(biz)+Pron+Pers+A1pl+Pnon+Ins(le[le])");
        assertParseCorrect("sizle", "siz(siz)+Pron+Pers+A2pl+Pnon+Ins(le[le])");

        assertParseCorrect("bu", "bu(bu)+Det", "bu(bu)+Pron+Demons+A3sg+Pnon+Nom");
        assertParseCorrect("şu", "şu(şu)+Pron+Demons+A3sg+Pnon+Nom", "şu(şu)+Det");
        assertParseCorrect("bunlar", "bu(bu)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Nom");
        assertParseCorrect("şunlar", "şu(şu)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Nom");

        assertParseCorrect("bunu", "bu(bu)+Pron+Demons+A3sg+Pnon+Acc(nu[nu])");
        assertParseCorrect("şunu", "şu(şu)+Pron+Demons+A3sg+Pnon+Acc(nu[nu])");
        assertParseCorrect("bunları", "bu(bu)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Acc(ı[ı])");
        assertParseCorrect("şunları", "şu(şu)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Acc(ı[ı])");

        assertParseCorrect("nere", "nere(nere)+Pron+Ques+A3sg+Pnon+Nom");
        assertParseCorrect("nereyi", "nere(nere)+Pron+Ques+A3sg+Pnon+Acc(+yI[yi])");
        assertParseCorrect("nereye", "nere(nere)+Pron+Ques+A3sg+Pnon+Dat(+yA[ye])");
        assertParseCorrect("nerede", "nere(nere)+Pron+Ques+A3sg+Pnon+Loc(dA[de])");
        assertParseCorrect("nereden", "nere(nere)+Pron+Ques+A3sg+Pnon+Abl(dAn[den])");
        assertParseCorrect("nerenin", "nere(nere)+Pron+Ques+A3sg+Pnon+Gen(+nIn[nin])", "nere(nere)+Pron+Ques+A3sg+P2sg(+In[n])+Gen(+nIn[in])");
        assertParseCorrect("nereyle", "nere(nere)+Pron+Ques+A3sg+Pnon+Ins(+ylA[yle])");

        assertParseCorrect("nerem", "nere(nere)+Pron+Ques+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("neren", "nere(nere)+Pron+Ques+A3sg+P2sg(+In[n])+Nom");
        assertParseCorrect("neresi", "nere(nere)+Pron+Ques+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrect("neremiz", "nere(nere)+Pron+Ques+A3sg+P1pl(+ImIz[miz])+Nom");
        assertParseCorrect("nereniz", "nere(nere)+Pron+Ques+A3sg+P2pl(+InIz[niz])+Nom");
        assertParseCorrect("nereleri", "nere(nere)+Pron+Ques+A3pl(lAr[ler])+P3sp(+sI[i])+Nom", "nereleri", "nere(nere)+Pron+Ques+A3pl(lAr[ler])+Pnon+Acc(+yI[i])");

        assertParseCorrect("nerenden", "nere(nere)+Pron+Ques+A3sg+P2sg(+In[n])+Abl(dAn[den])");
        assertParseCorrect("neresine", "nere(nere)+Pron+Ques+A3sg+P3sg(+sI[si])+Dat(nA[ne])");

        assertParseCorrect("kimse", "kimse(kimse)+Pron+A3sg+Pnon+Nom");
        assertParseCorrect("kimseyi", "kimse(kimse)+Pron+A3sg+Pnon+Acc(+yI[yi])");
        assertParseCorrect("kimseye", "kimse(kimse)+Pron+A3sg+Pnon+Dat(+yA[ye])");
        assertParseCorrect("kimsede", "kimse(kimse)+Pron+A3sg+Pnon+Loc(dA[de])");
        assertParseCorrect("kimseden", "kimse(kimse)+Pron+A3sg+Pnon+Abl(dAn[den])");
        assertParseCorrect("kimsenin", "kimse(kimse)+Pron+A3sg+Pnon+Gen(+nIn[nin])", "kimse(kimse)+Pron+A3sg+P2sg(+In[n])+Gen(+nIn[in])");
        assertParseCorrect("kimseyle", "kimse(kimse)+Pron+A3sg+Pnon+Ins(+ylA[yle])");

        assertParseCorrect("kimsem", "kimse(kimse)+Pron+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("kimsen", "kimse(kimse)+Pron+A3sg+P2sg(+In[n])+Nom");
        assertParseCorrect("kimsesi", "kimse(kimse)+Pron+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrect("kimsemiz", "kimse(kimse)+Pron+A3sg+P1pl(+ImIz[miz])+Nom");
        assertParseCorrect("kimseniz", "kimse(kimse)+Pron+A3sg+P2pl(+InIz[niz])+Nom");
        assertParseCorrect("kimseleri", "kimse(kimse)+Pron+A3pl(lAr[ler])+P3sp(+sI[i])+Nom", "kimse(kimse)+Pron+A3pl(lAr[ler])+Pnon+Acc(+yI[i])");
        assertParseCorrect("kimselerim", "kimse(kimse)+Pron+A3pl(lAr[ler])+P1sg(+Im[im])+Nom");
        assertParseCorrect("kimselerimizde", "kimse(kimse)+Pron+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Loc(dA[de])");
        assertParseCorrect("kimseler", "kimse(kimse)+Pron+A3pl(lAr[ler])+Pnon+Nom");

        assertParseCorrect("kimsecikler", "kimsecik(kimsecik)+Pron+A3pl(lAr[ler])+Pnon+Nom");
        assertParseCorrect("kimseciklerde", "kimsecik(kimsecik)+Pron+A3pl(lAr[ler])+Pnon+Loc(dA[de])");


        assertParseCorrect("nerenden", "nere(nere)+Pron+Ques+A3sg+P2sg(+In[n])+Abl(dAn[den])");

        assertParseCorrect("kimimiz", "kim(kim)+Pron+Ques+A3sg+P1pl(miz[imiz])+Nom");
        assertParseCorrect("kimimizle", "kim(kim)+Pron+Ques+A3sg+P1pl(miz[imiz])+Ins(+ylA[le])");
        assertParseCorrect("kimleri", "kim(kim)+Pron+Ques+A3pl(lAr[ler])+P3sp(+sI[i])+Nom", "kim(kim)+Pron+Ques+A3pl(lAr[ler])+Pnon+Acc(+yI[i])");
        assertParseCorrect("kimlerimiz", "kim(kim)+Pron+Ques+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Nom");
        assertParseCorrect("kimlerimize", "kim(kim)+Pron+Ques+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Dat(+yA[e])");
        assertParseCorrect("kimlerimizin", "kim(kim)+Pron+Ques+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Gen(+nIn[in])");
        assertParseCorrect("kimlerimiz", "kim(kim)+Pron+Ques+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Nom");
        assertParseCorrect("kimilerimize", "kimi(kimi)+Pron+A3pl(lAr[ler])+P1pl(+ImIz[miz])+Dat(+yA[e])");
        assertParseCorrect("kimilerimizin", "kim(kimi)+Pron+A3pl(lAr[ler])+P1pl(+ImIz[miz])+Gen(+nIn[in])");

        assertParseCorrect("kimim", "kim(kim)+Pron+Ques+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("kimin", "kim(kim)+Pron+Ques+A3sg+P2sg(+In[in])+Nom", "kim(kim)+Pron+Ques+A3sg+Pnon+Gen(+nIn[in])");

        assertParseCorrect("bura", "bura(bura)+Pron+A3sg+Pnon+Nom");
        assertParseCorrect("burayı", "bura(bura)+Pron+A3sg+Pnon+Acc(+yI[yı])");
        assertParseCorrect("buraya", "bura(bura)+Pron+A3sg+Pnon+Dat(+yA[ya])");
        assertParseCorrect("burada", "bura(bura)+Pron+A3sg+Pnon+Loc(dA[da])");
        assertParseCorrect("buradan", "bura(bura)+Pron+A3sg+Pnon+Abl(dAn[dan])");
        assertParseCorrect("buranın", "bura(bura)+Pron+A3sg+Pnon+Gen(+nIn[nın])", "bura(bura)+Pron+A3sg+P2sg(+In[n])+Gen(+nIn[ın])");
        assertParseCorrect("burayla", "bura(bura)+Pron+A3sg+Pnon+Ins(+ylA[yla])");

        assertParseCorrect("buram", "bura(bura)+Pron+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("buran", "bura(bura)+Pron+A3sg+P2sg(+In[n])+Nom");
        assertParseCorrect("burası", "bura(bura)+Pron+A3sg+P3sg(+sI[sı])+Nom");
        assertParseCorrect("buramız", "bura(bura)+Pron+A3sg+P1pl(+ImIz[mız])+Nom");
        assertParseCorrect("buranız", "bura(bura)+Pron+A3sg+P2pl(+InIz[nız])+Nom");
        assertParseCorrect("buraları", "bura(bura)+Pron+A3pl(lAr[lar])+P3sp(+sI[ı])+Nom", "bura(bura)+Pron+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])");

        assertParseCorrect("burandan", "bura(bura)+Pron+A3sg+P2sg(+In[n])+Abl(dAn[dan])");
        assertParseCorrect("burasına", "bura(bura)+Pron+A3sg+P3sg(+sI[sı])+Dat(nA[na])");

        assertParseCorrect("oradan", "ora(ora)+Pron+A3sg+Pnon+Abl(dAn[dan])");
        assertParseCorrect("şuranla", "şura(şura)+Pron+A3sg+P2sg(+In[n])+Ins(+ylA[la])", "şura(şûra)+Noun+A3sg+P2sg(+In[n])+Ins(+ylA[la])");
    }

    @Test
    public void shouldParsePointQualifiers() {
        removeRoots("masad", "bend", "on", "yar", "bizle");
        removeRootsExceptTheOneWithPrimaryPos("ben", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("biz", PrimaryPos.Pronoun);

        assertParseCorrect("masadaki", "masa(masa)+Noun+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])");
        assertParseCorrect("masamdaki", "masa(masa)+Noun+A3sg+P1sg(+Im[m])+Loc(dA[da])+Adj+PointQual(ki[ki])");
        assertParseCorrect("masalarındaki", "masa(masa)+Noun+A3pl(lAr[lar])+P2sg(+In[ın])+Loc(dA[da])+Adj+PointQual(ki[ki])", "masa(masa)+Noun+A3pl(lAr[lar])+P3sp(!I[ı])+Loc(ndA[nda])+Adj+PointQual(ki[ki])");
        assertParseCorrect("kısadaki", "kısa(kısa)+Adj+Noun+Zero+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])");
        assertParseCorrect("bendeki", "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])");
        assertParseCorrect("ondaki", "o(o)+Pron+A3sg+Pnon+Loc(nda[nda])+Adj+PointQual(ki[ki])");
        assertParseCorrect("bizlerdeki", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])");
        assertParseCorrect("küçüğümdekilere", "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1sg(+Im[üm])+Loc(dA[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3pl(lAr[ler])+Pnon+Dat(+yA[e])");
        assertParseCorrect("küçüğümdekilerde", "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1sg(+Im[üm])+Loc(dA[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3pl(lAr[ler])+Pnon+Loc(dA[de])");
        assertParseCorrect("küçüğümdekilerdeki", "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1sg(+Im[üm])+Loc(dA[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3pl(lAr[ler])+Pnon+Loc(dA[de])+Adj+PointQual(ki[ki])");
        assertParseCorrect("oradaki", "ora(ora)+Pron+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])");
        assertParseCorrect("oradakilerin", "ora(ora)+Pron+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])+Noun+Zero+A3pl(lAr[ler])+Pnon+Gen(+nIn[in])");
        assertParseCorrect("yarınki", "yarın(yarın)+Adv+Time+Adj+PointQual(ki[ki])");
        assertParseCorrect("bugünkünden", "bugün(bugün)+Adv+Time+Adj+PointQual(kü[kü])+Noun+Zero+A3sg+Pnon+Abl(ndAn[nden])");
        assertParseCorrect("bugünkülerden", "bugün(bugün)+Adv+Time+Adj+PointQual(kü[kü])+Noun+Zero+A3pl(lAr[ler])+Pnon+Abl(dAn[den])");
        assertParseCorrect("dünkünün", "dün(dün)+Adv+Adj+PointQual(kü[kü])+Noun+Zero+A3sg+Pnon+Gen(+nIn[nün])");
        assertParseCorrect("bendekileri", "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3pl(lAr[ler])+Pnon+Acc(+yI[i])");

        assertNotParsable("masadakim");
        assertNotParsable("masamdakin");
        assertNotParsable("masalarındakisi");
        assertNotParsable("sekizdekimiz");
        assertNotParsable("kısadakiniz");
        assertNotParsable("ondakisi");
        assertNotParsable("bizlerdekim");
        assertNotParsable("küçüğümdekilerdekiniz");
        assertNotParsable("oradakim");
        assertNotParsable("oradakilerisi");
        assertNotParsable("oradakilerinden");
        assertNotParsable("yarınkim");
        assertNotParsable("yarınkin");
        assertNotParsable("dünküsü");
        assertNotParsable("bendekimiz");

        assertParseCorrect("masadakini", "masa(masa)+Noun+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Acc(nI[ni])");
        assertParseCorrect("masamdakine", "masa(masa)+Noun+A3sg+P1sg(+Im[m])+Loc(dA[da])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Dat(nA[ne])");
        assertParseCorrect("masalarındakinde", "masa(masa)+Noun+A3pl(lAr[lar])+P2sg(+In[ın])+Loc(dA[da])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Loc(ndA[nde])", "masa(masa)+Noun+A3pl(lAr[lar])+P3sp(!I[ı])+Loc(ndA[nda])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Loc(ndA[nde])");
        assertParseCorrect("kısadakini", "kısa(kısa)+Adj+Noun+Zero+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Acc(nI[ni])");
        assertParseCorrect("bendekinden", "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Abl(ndAn[nden])");
        assertParseCorrect("ondakinde", "o(o)+Pron+A3sg+Pnon+Loc(nda[nda])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Loc(ndA[nde])");
        assertParseCorrect("bendekine", "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Dat(nA[ne])");
        assertParseCorrect("bizdekine", "biz(biz)+Pron+Pers+A1pl+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Dat(nA[ne])");
        assertParseCorrect("bizlerdekine", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Dat(nA[ne])");
        assertParseCorrect("küçüğümdekilerdekinden", "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1sg(+Im[üm])+Loc(dA[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3pl(lAr[ler])+Pnon+Loc(dA[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Abl(ndAn[nden])");
        assertParseCorrect("oradakine", "ora(ora)+Pron+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Dat(nA[ne])");
        assertParseCorrect("oradakilerden", "ora(ora)+Pron+A3sg+Pnon+Loc(dA[da])+Adj+PointQual(ki[ki])+Noun+Zero+A3pl(lAr[ler])+Pnon+Abl(dAn[den])");
        assertParseCorrect("bendekinin", "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Gen(+nIn[nin])");
        assertParseCorrect("ankini", "an(an)+Adv+Time+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Acc(nI[ni])");
        assertParseCorrect("günkünde", "gün(gün)+Adv+Time+Adj+PointQual(kü[kü])+Noun+Zero+A3sg+Pnon+Loc(ndA[nde])");
        assertParseCorrect("dünkünden", "dün(dün)+Adv+Adj+PointQual(kü[kü])+Noun+Zero+A3sg+Pnon+Abl(ndAn[nden])");
        assertParseCorrect("zamankini", "zaman(zaman)+Adv+Time+Adj+PointQual(ki[ki])+Noun+Zero+A3sg+Pnon+Acc(nI[ni])");
    }

    @Test
    public void shouldParsePointQualifiersForDerivedAdverbs() {
        assertParseCorrect("yaparkenki", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Adv+While(ken[ken])+Adj+PointQual(ki[ki])");
        assertParseCorrect("yapıncaki", "yap(yapmak)+Verb+Pos+Adv+When(+yIncA[ınca])+Adj+PointQual(ki[ki])");
        assertParseCorrect("yapacakkenki", "XXXX");
        assertParseCorrect("yapmışkenki", "XXXXX");

        // there are more forms of derived adverbs, but they don"t make sense with "ki" suffix
    }

    @Test
    public void shouldParsePronounDerivations() {
        removeRoots("on", "bun");
        removeRootsExceptTheOneWithPrimaryPos("biz", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("ben", PrimaryPos.Pronoun);


        assertParseCorrect("bensiz", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Adj+Without(sIz[siz])");
        assertParseCorrect("sensiz", "sen(sen)+Pron+Pers+A2sg+Pnon+Nom+Adj+Without(sIz[siz])");
        assertParseCorrect("onsuz", "o(o)+Pron+A3sg+Pnon+Nom+Adj+Without(nsuz[nsuz])");
        assertParseCorrect("bizsiz", "biz(biz)+Pron+Pers+A1pl+Pnon+Nom+Adj+Without(sIz[siz])");
        assertParseCorrect("sizsiz", "siz(siz)+Pron+Pers+A2pl+Pnon+Nom+Adj+Without(sIz[siz])");
        assertParseCorrect("onlarsız", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Nom+Adj+Without(sIz[sız])");
        assertParseCorrect("bunsuz", "bu(bu)+Pron+Demons+A3sg+Pnon+Nom+Adj+Without(nsuz[nsuz])");
        assertParseCorrect("şunsuz", "şu(şu)+Pron+Demons+A3sg+Pnon+Nom+Adj+Without(nsuz[nsuz])");
        assertParseCorrect("bunlarsız", "bu(bu)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Nom+Adj+Without(sIz[sız])");
        assertParseCorrect("şunlarsız", "şu(şu)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Nom+Adj+Without(sIz[sız])");

    }

    @Test
    public void shouldParseSomeImperatives() {
        removeRoots("gelin", "ge");

        assertParseCorrect("gel", "gel(gelmek)+Verb+Pos+Imp+A2sg");
        assertParseCorrect("gelsin", "gel(gelmek)+Verb+Pos+Imp+A3sg(sIn[sin])");
        assertParseCorrect("gelin", "gel(gelmek)+Verb+Pos+Imp+A2pl(+yIn[in])", "gel(gelmek)+Verb+Verb+Pass(+In[in])+Pos+Imp+A2sg");
        assertParseCorrect("geliniz", "gel(gelmek)+Verb+Pos+Imp+A2pl(+yInIz[iniz])");
        assertParseCorrect("gelsinler", "gel(gelmek)+Verb+Pos+Imp+A3pl(sInlAr[sinler])");

        assertParseCorrect("gelme", "gel(gelmek)+Verb+Neg(mA[me])+Imp+A2sg", "gel(gelmek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom");
        assertParseCorrect("gelmesin", "gel(gelmek)+Verb+Neg(mA[me])+Imp+A3sg(sIn[sin])");
        assertParseCorrect("gelmeyin", "gel(gelmek)+Verb+Neg(mA[me])+Imp+A2pl(+yIn[yin])");
        assertParseCorrect("gelmeyiniz", "gel(gelmek)+Verb+Neg(mA[me])+Imp+A2pl(+yInIz[yiniz])");
        assertParseCorrect("gelmesinler", "gel(gelmek)+Verb+Neg(mA[me])+Imp+A3pl(sInlAr[sinler])");

        assertParseCorrect("söyle", "söyle(söylemek)+Verb+Pos+Imp+A2sg");
        assertParseCorrect("söylesin", "söyle(söylemek)+Verb+Pos+Imp+A3sg(sIn[sin])");
        assertParseCorrect("söyleyin", "söyle(söylemek)+Verb+Pos+Imp+A2pl(+yIn[yin])");
        assertParseCorrect("söyleyiniz", "söyle(söylemek)+Verb+Pos+Imp+A2pl(+yInIz[yiniz])");
        assertParseCorrect("söylesinler", "söyle(söylemek)+Verb+Pos+Imp+A3pl(sInlAr[sinler])");

        assertParseCorrect("söyleme",
                "söyle(söylemek)+Verb+Neg(mA[me])+Imp+A2sg",
                "söylem(söylem)+Noun+A3sg+Pnon+Dat(+yA[e])",
                "söyle(söylemek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom"
        );
        assertParseCorrect("söylemesin", "söyle(söylemek)+Verb+Neg(mA[me])+Imp+A3sg(sIn[sin])");
        assertParseCorrect("söylemeyin", "söyle(söylemek)+Verb+Neg(mA[me])+Imp+A2pl(+yIn[yin])");
        assertParseCorrect("söylemeyiniz", "söyle(söylemek)+Verb+Neg(mA[me])+Imp+A2pl(+yInIz[yiniz])");
        assertParseCorrect("söylemesinler", "söyle(söylemek)+Verb+Neg(mA[me])+Imp+A3pl(sInlAr[sinler])");
    }

    @Test
    public void shouldParseVerbToNounDerivations() {
        assertParseCorrect("yapmak", "yap(yapmak)+Verb+Pos+Noun+Inf(mAk[mak])+A3sg+Pnon+Nom");
        assertParseCorrect("yapma", "yap(yapmak)+Verb+Neg(mA[ma])+Imp+A2sg", "yap(yapmak)+Verb+Pos+Noun+Inf(mA[ma])+A3sg+Pnon+Nom");
        assertParseCorrect("yapış", "yapış(yapışmak)+Verb+Pos+Imp+A2sg", "yap(yapmak)+Verb+Pos+Noun+Inf(+yIş[ış])+A3sg+Pnon+Nom");

        assertParseCorrect("gelmek", "gel(gelmek)+Verb+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom");
        assertParseCorrect("gelme", "gel(gelmek)+Verb+Neg(mA[me])+Imp+A2sg", "gel(gelmek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom");
        assertParseCorrect("geliş", "geliş(gelişmek)+Verb+Pos+Imp+A2sg", "gel(gelmek)+Verb+Pos+Noun+Inf(+yIş[iş])+A3sg+Pnon+Nom");

        assertParseCorrect("söylemek", "söyle(söylemek)+Verb+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom");
        assertParseCorrect("söyleme", "söyle(söylemek)+Verb+Neg(mA[me])+Imp+A2sg", "söylem(söylem)+Noun+A3sg+Pnon+Dat(+yA[e])", "söyle(söylemek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom");
        assertParseCorrect("söyleyiş", "söyle(söylemek)+Verb+Pos+Noun+Inf(+yIş[yiş])+A3sg+Pnon+Nom");

        assertParseCorrect("yapmamak", "yap(yapmak)+Verb+Neg(mA[ma])+Noun+Inf(mAk[mak])+A3sg+Pnon+Nom");
        assertParseCorrect("yapmama", "yap(yapmak)+Verb+Neg(mA[ma])+Noun+Inf(mA[ma])+A3sg+Pnon+Nom", "yap(yapmak)+Verb+Pos+Noun+Inf(mA[ma])+A3sg+P1sg(+Im[m])+Dat(+yA[a])");
        assertParseCorrect("yapmayış", "yap(yapmak)+Verb+Neg(mA[ma])+Noun+Inf(+yIş[yış])+A3sg+Pnon+Nom");

        assertParseCorrect("gelmemek", "gel(gelmek)+Verb+Neg(mA[me])+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom");
        assertParseCorrect("gelmeyiş", "gel(gelmek)+Verb+Neg(mA[me])+Noun+Inf(+yIş[yiş])+A3sg+Pnon+Nom");

        assertParseCorrect("söylememek", "söyle(söylemek)+Verb+Neg(mA[me])+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom");
        assertParseCorrect("söylememe", "söyle(söylemek)+Verb+Neg(mA[me])+Noun+Inf(mA[me])+A3sg+Pnon+Nom", "söyle(söylemek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+P1sg(+Im[m])+Dat(+yA[e])");
        assertParseCorrect("söylemeyiş", "söyle(söylemek)+Verb+Neg(mA[me])+Noun+Inf(+yIş[yiş])+A3sg+Pnon+Nom");
    }

    @Test
    public void shouldParsePassives() {
        assertParseCorrect("yazıldı", "yaz(yazmak)+Verb+Verb+Pass(+nIl[ıl])+Pos+Past(dI[dı])+A3sg");
        assertParseCorrect("yaptırıldı", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Pass(+nIl[ıl])+Pos+Past(dI[dı])+A3sg");
        assertParseCorrect("geliniyor", "gel(gelmek)+Verb+Verb+Pass(+In[in])+Pos+Prog(Iyor[iyor])+A3sg");
        assertParseCorrect("düşüldü", "düş(düşmek)+Verb+Verb+Pass(+nIl[ül])+Pos+Past(dI[dü])+A3sg");
        assertParseCorrect("düşünüldü", "düşün(düşünmek)+Verb+Verb+Pass(+nIl[ül])+Pos+Past(dI[dü])+A3sg");
        assertParseCorrect("silinecekti", "sil(silmek)+Verb+Verb+Pass(+In[in])+Pos+Fut(+yAcAk[ecek])+Past(dI[ti])+A3sg");
        assertParseCorrect("dendi", "de(demek)+Verb+Verb+Pass(+In[n])+Pos+Past(dI[di])+A3sg");
        assertParseCorrect("denildi", "de(demek)+Verb+Verb+Pass(+InIl[nil])+Pos+Past(dI[di])+A3sg");
        assertParseCorrect("yendi", "yen(yenmek)+Verb+Pos+Past(dI[di])+A3sg", "ye(yemek)+Verb+Verb+Pass(+In[n])+Pos+Past(dI[di])+A3sg");
        assertParseCorrect("yenildi", "yen(yenmek) + Verb + Verb + Pass(+nIl[il]) + Pos + Past(dI[di]) + A3sg");

        removeRoots("ye");
        assertParseCorrect("yerleştirilmiş", "yerleş(yerleşmek)+Verb+Verb+Caus(dIr[tir])+Verb+Pass(+nIl[il])+Pos+Narr(mIş[miş])+A3sg", "yerleş(yerleşmek)+Verb+Verb+Caus(dIr[tir])+Verb+Pass(+nIl[il])+Pos+Narr(mIş[miş])+Adj+Zero");
    }

    @Test
    public void shouldParseCausatives() {
        assertParseCorrect("düzelttim", "düzel(düzelmek)+Verb+Verb+Caus(!t[t])+Pos+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("çevirttim", "çevir(çevirmek)+Verb+Verb+Caus(!t[t])+Pos+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("kapattım", "kapa(kapamak)+Verb+Verb+Caus(!t[t])+Pos+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("bitirdim", "bit(bitmek)+Verb+Verb+Caus(Ir[ir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("yitirdim", "yit(yitmek)+Verb+Verb+Caus(Ir[ir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("ürküttüm", "ürk(ürkmek)+Verb+Verb+Caus(I!t[üt])+Pos+Past(dI[tü])+A1sg(+Im[m])");
        assertParseCorrect("çıkardım", "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+Past(dI[dı])+A1sg(+Im[m])", "çık(çıkmak)+Verb+Verb+Caus(Ar[ar])+Pos+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("ettirdim", "et(etmek)+Verb+Verb+Caus(dIr[tir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("yaptırdım", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Pos+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("doldurdum", "dol(dolmak)+Verb+Verb+Caus(dIr[dur])+Pos+Past(dI[du])+A1sg(+Im[m])");
        assertParseCorrect("azalttım", "azal(azalmak)+Verb+Verb+Caus(!t[t])+Pos+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("azaltıyordu", "azal(azalmak)+Verb+Verb+Caus(!t[t])+Pos+Prog(Iyor[ıyor])+Past(dI[du])+A3sg");
        assertParseCorrect("sürdürülen", "sür(sürmek)+Verb+Verb+Caus(dIr[dür])+Verb+Pass(+nIl[ül])+Pos+Adj+PresPart(+yAn[en])");
        assertParseCorrect("kapatılmış", "kapa(kapamak)+Verb+Verb+Caus(!t[t])+Verb+Pass(+nIl[ıl])+Pos+Narr(mIş[mış])+A3sg", "kapa(kapamak)+Verb+Verb+Caus(!t[t])+Verb+Pass(+nIl[ıl])+Pos+Narr(mIş[mış])+Adj+Zero");
        assertParseCorrect("düşündürtüyordu", "düşün(düşünmek)+Verb+Verb+Caus(dIr[dür])+Verb+Caus(!t[t])+Pos+Prog(Iyor[üyor])+Past(dI[du])+A3sg");
        assertParseCorrect("korkutmamalı", "kork(korkmak)+Verb+Verb+Caus(I!t[ut])+Neg(mA[ma])+Neces(mAl!I[malı])+A3sg");
        assertParseCorrect("sıkıştırıldığı", "sık(sıkmak)+Verb+Verb+Recip(+Iş[ış])+Verb+Caus(dIr[tır])+Verb+Pass(+nIl[ıl])+Pos+Adj+PastPart(dIk[dığ])+P3sg(+sI[ı])");
    }

    @Test
    public void shouldParseDoubleCausatives() {
        assertParseCorrect("düzelttirdim", "düzel(düzelmek)+Verb+Verb+Caus(!t[t])+Verb+Caus(dIr[tir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("çevirttirdim", "çevir(çevirmek)+Verb+Verb+Caus(!t[t])+Verb+Caus(dIr[tir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("kapattırdım", "kapa(kapamak)+Verb+Verb+Caus(!t[t])+Verb+Caus(dIr[tır])+Pos+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("bitirttim", "bit(bitmek)+Verb+Verb+Caus(Ir[ir])+Verb+Caus(!t[t])+Pos+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("yitirttim", "yit(yitmek)+Verb+Verb+Caus(Ir[ir])+Verb+Caus(!t[t])+Pos+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("ürküttürdüm", "ürk(ürkmek)+Verb+Verb+Caus(I!t[üt])+Verb+Caus(dIr[tür])+Pos+Past(dI[dü])+A1sg(+Im[m])");
        assertParseCorrect("çıkarttım", "çık(çıkmak)+Verb+Verb+Caus(Ar[ar])+Verb+Caus(!t[t])+Pos+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("ettirttim", "et(etmek)+Verb+Verb+Caus(dIr[tir])+Verb+Caus(!t[t])+Pos+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("yaptırttım", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Caus(!t[t])+Pos+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("doldurttum", "dol(dolmak)+Verb+Verb+Caus(dIr[dur])+Verb+Caus(!t[t])+Pos+Past(dI[tu])+A1sg(+Im[m])");
    }

    @Test
    public void shouldParseTripleCausatives() {
        assertParseCorrect("düzelttirttim", "düzel(düzelmek)+Verb+Verb+Caus(!t[t])+Verb+Caus(dIr[tir])+Verb+Caus(!t[t])+Pos+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("çevirttirttim", "çevir(çevirmek)+Verb+Verb+Caus(!t[t])+Verb+Caus(dIr[tir])+Verb+Caus(!t[t])+Pos+Past(dI[ti])+A1sg(+Im[m])");
        assertParseCorrect("kapattırttım", "kapa(kapamak)+Verb+Verb+Caus(!t[t])+Verb+Caus(dIr[tır])+Verb+Caus(!t[t])+Pos+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("bitirttirdim", "bit(bitmek)+Verb+Verb+Caus(Ir[ir])+Verb+Caus(!t[t])+Verb+Caus(dIr[tir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("yitirttirdim", "yit(yitmek)+Verb+Verb+Caus(Ir[ir])+Verb+Caus(!t[t])+Verb+Caus(dIr[tir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("ürküttürttüm", "ürk(ürkmek)+Verb+Verb+Caus(I!t[üt])+Verb+Caus(dIr[tür])+Verb+Caus(!t[t])+Pos+Past(dI[tü])+A1sg(+Im[m])");
        assertParseCorrect("çıkarttırdım", "çık(çıkmak)+Verb+Verb+Caus(Ar[ar])+Verb+Caus(!t[t])+Verb+Caus(dIr[tır])+Pos+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("ettirttirdim", "et(etmek)+Verb+Verb+Caus(dIr[tir])+Verb+Caus(!t[t])+Verb+Caus(dIr[tir])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("yaptırttırdım", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Caus(!t[t])+Verb+Caus(dIr[tır])+Pos+Past(dI[dı])+A1sg(+Im[m])");
        assertParseCorrect("doldurtturdum", "dol(dolmak)+Verb+Verb+Caus(dIr[dur])+Verb+Caus(!t[t])+Verb+Caus(dIr[tur])+Pos+Past(dI[du])+A1sg(+Im[m])");
    }

    @Test
    public void shouldParseFutParts() {
        assertParseCorrect("kalacak", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+A3sg", "kal(kalmak)+Verb+Pos+Adj+FutPart(+yAcAk[acak])+Pnon", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom");
        assertParseCorrect("kalmayacak", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+A3sg", "kal(kalmak)+Verb+Neg(mA[ma])+Adj+FutPart(+yAcAk[yacak])+Pnon", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3sg+Pnon+Nom");

        assertParseCorrect("bitecek", "bit(bitmek)+Verb+Pos+Fut(+yAcAk[ecek])+A3sg", "bit(bitmek)+Verb+Pos+Adj+FutPart(+yAcAk[ecek])+Pnon", "bit(bitmek)+Verb+Pos+Fut(+yAcAk[ecek])+Adj+Zero", "bit(bitmek)+Verb+Pos+Noun+FutPart(+yAcAk[ecek])+A3sg+Pnon+Nom");
        assertParseCorrect("bitmeyecek", "bit(bitmek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+A3sg", "bit(bitmek)+Verb+Neg(mA[me])+Adj+FutPart(+yAcAk[yecek])+Pnon", "bit(bitmek)+Verb+Neg(mA[me])+Fut(+yAcAk[yecek])+Adj+Zero", "bit(bitmek)+Verb+Neg(mA[me])+Noun+FutPart(+yAcAk[yecek])+A3sg+Pnon+Nom");

        assertParseCorrect("kalacağım", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+A1sg(+Im[ım])", "kal(kalmak)+Verb+Pos+Adj+FutPart(+yAcAk[acağ])+P1sg(+Im[ım])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P1sg(+Im[ım])+Nom");
        assertParseCorrect("kalmayacağım", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+A1sg(+Im[ım])", "kal(kalmak)+Verb+Neg(mA[ma])+Adj+FutPart(+yAcAk[yacağ])+P1sg(+Im[ım])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P1sg(+Im[ım])+Nom");

        assertParseCorrect("kalacağımı", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P1sg(+Im[ım])+Acc(+yI[ı])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[ım])+Acc(+yI[ı])");
        assertParseCorrect("kalmayacağımı", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P1sg(+Im[ım])+Acc(+yI[ı])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[ım])+Acc(+yI[ı])");

        assertParseCorrect("kalacağıma", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P1sg(+Im[ım])+Dat(+yA[a])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[ım])+Dat(+yA[a])");
        assertParseCorrect("kalmayacağıma", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P1sg(+Im[ım])+Dat(+yA[a])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[ım])+Dat(+yA[a])");

        assertParseCorrect("kalacağın", "kal(kalmak)+Verb+Pos+Adj+FutPart(+yAcAk[acağ])+P2sg(+In[ın])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+Pnon+Gen(+nIn[ın])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P2sg(+In[ın])+Nom", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+Pnon+Gen(+nIn[ın])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P2sg(+In[ın])+Nom");
        assertParseCorrect("kalmayacağın", "kal(kalmak)+Verb+Neg(mA[ma])+Adj+FutPart(+yAcAk[yacağ])+P2sg(+In[ın])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+Pnon+Gen(+nIn[ın])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P2sg(+In[ın])+Nom", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+Pnon+Gen(+nIn[ın])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P2sg(+In[ın])+Nom");

        assertParseCorrect("kalacağını", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P2sg(+In[ın])+Acc(+yI[ı])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P3sg(+sI[ı])+Acc(nI[nı])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P2sg(+In[ın])+Acc(+yI[ı])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Acc(nI[nı])");
        assertParseCorrect("kalmayacağını", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P2sg(+In[ın])+Acc(+yI[ı])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P3sg(+sI[ı])+Acc(nI[nı])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P2sg(+In[ın])+Acc(+yI[ı])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Acc(nI[nı])");

        assertParseCorrect("kalacağına", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P2sg(+In[ın])+Dat(+yA[a])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acağ])+A3sg+P3sg(+sI[ı])+Dat(nA[na])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P2sg(+In[ın])+Dat(+yA[a])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acağ])+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Dat(nA[na])");
        assertParseCorrect("kalmayacağına", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P2sg(+In[ın])+Dat(+yA[a])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacağ])+A3sg+P3sg(+sI[ı])+Dat(nA[na])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P2sg(+In[ın])+Dat(+yA[a])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacağ])+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Dat(nA[na])");

        assertParseCorrect("kalacaklar", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+A3pl(lAr[lar])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3pl(lAr[lar])+Pnon+Nom", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom");
        assertParseCorrect("kalmayacaklar", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+A3pl(lAr[lar])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3pl(lAr[lar])+Pnon+Nom", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom");

        assertParseCorrect("kalacakları", "kal(kalmak)+Verb+Pos+Adj+FutPart(+yAcAk[acak])+P3pl(lAr!I[ları])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+P3pl(lAr!I[ları])+Nom", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3pl(lAr[lar])+P3pl(!I[ı])+Nom", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3sg+P3pl(lAr!I[ları])+Nom", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+P3pl(!I[ı])+Nom");
        assertParseCorrect("kalmayacakları", "kal(kalmak)+Verb+Neg(mA[ma])+Adj+FutPart(+yAcAk[yacak])+P3pl(lAr!I[ları])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3sg+P3pl(lAr!I[ları])+Nom", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3pl(lAr[lar])+P3pl(!I[ı])+Nom", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero+Noun+Zero+A3sg+P3pl(lAr!I[ları])+Nom", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+P3pl(!I[ı])+Nom");

        assertParseCorrect("kalacaklarımı", "kal(kalmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3pl(lAr[lar])+P1sg(+Im[ım])+Acc(+yI[ı])", "kal(kalmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+P1sg(+Im[ım])+Acc(+yI[ı])");
        assertParseCorrect("kalmayacaklarımı", "kal(kalmak)+Verb+Neg(mA[ma])+Noun+FutPart(+yAcAk[yacak])+A3pl(lAr[lar])+P1sg(+Im[ım])+Acc(+yI[ı])", "kal(kalmak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+P1sg(+Im[ım])+Acc(+yI[ı])");

    }

    @Test
    public void shouldParsePastParts() {
        assertParseCorrect("ettiklerin",
                "et(etmek)+Verb+Pos+Noun+PastPart(dIk[tik])+A3pl(lAr[ler])+Pnon+Gen(+nIn[in])",
                "et(etmek)+Verb+Pos+Noun+PastPart(dIk[tik])+A3pl(lAr[ler])+P2sg(+In[in])+Nom"
        );
        assertParseCorrect("yediklerin",
                "ye(yemek)+Verb+Pos+Noun+PastPart(dIk[dik])+A3pl(lAr[ler])+Pnon+Gen(+nIn[in])",
                "ye(yemek)+Verb+Pos+Noun+PastPart(dIk[dik])+A3pl(lAr[ler])+P2sg(+In[in])+Nom"
        );

        assertParseCorrect("yediği",
                "ye(yemek)+Verb+Pos+Adj+PastPart(dIk[diğ])+P3sg(+sI[i])",
                "ye(yemek)+Verb+Pos+Noun+PastPart(dIk[diğ])+A3sg+Pnon+Acc(+yI[i])",
                "ye(yemek)+Verb+Pos+Noun+PastPart(dIk[diğ])+A3sg+P3sg(+sI[i])+Nom"
        );
        assertParseCorrect("yedik",
                "ye(yemek)+Verb+Pos+Past(dI[di])+A1pl(!k[k])",
                "ye(yemek)+Verb+Pos+Adj+PastPart(dIk[dik])+Pnon",
                "ye(yemek)+Verb+Pos+Noun+PastPart(dIk[dik])+A3sg+Pnon+Nom"
        );
    }

    @Test
    public void shouldParseRecipVerbs() {
        assertParseCorrect("öpüştünüz", "XXXXXX");
        assertParseCorrect("küfürleştik", "XXXXXX");
        assertParseCorrect("tutuştuk", "XXXXXX");
        assertParseCorrect("küfürleştik", "XXXXXX");
        // TODO: diger recip durumlarini bul
        assertParseCorrect("bakıştılar", "bak(bakmak)+Verb+Verb+Recip(+Iş[ış])+Pos+Past(dI[tı])+A3pl(lAr[lar])");
    }

    @Test
    public void shouldParseReflexivePronouns() {
        assertParseCorrect("kendim", "kendi(kendi)+Pron+Reflex+A1sg+P1sg(m[m])+Nom");
        assertParseCorrect("kendin", "kendi(kendi)+Pron+Reflex+A2sg+P2sg(n[n])+Nom");
        assertParseCorrect("kendi", "kendi(kendi)+Pron+Reflex+A3sg+P3sg+Nom");
        assertParseCorrect("kendimiz", "kendi(kendi)+Pron+Reflex+A1pl+P1pl(miz[miz])+Nom");
        assertParseCorrect("kendiniz", "kendi(kendi)+Pron+Reflex+A2pl+P2pl(niz[niz])+Nom");
        assertParseCorrect("kendileri", "kendi(kendi)+Pron+Reflex+A3pl(leri[leri])+P3pl+Nom");
        assertParseCorrect("kendisi", "kendi(kendi)+Pron+Reflex+A3sg+P3sg(si[si])+Nom");
        assertParseCorrect("kendilerimiz", "kendi(kendi)+Pron+Reflex+A1pl(ler[ler])+P1pl(imiz[imiz])+Nom");
        assertParseCorrect("kendileriniz", "kendi(kendi)+Pron+Reflex+A2pl(ler[ler])+P2pl(iniz[iniz])+Nom");

        assertParseCorrect("kendimi", "kendi(kendi)+Pron+Reflex+A1sg+P1sg(m[m])+Acc(i[i])");
        assertParseCorrect("kendini", "kendi(kendi)+Pron+Reflex+A2sg+P2sg(n[n])+Acc(i[i])", "kendi(kendi)+Pron+Reflex+A3sg+P3sg+Acc(ni[ni])");
        assertParseCorrect("kendimizi", "kendi(kendi)+Pron+Reflex+A1pl+P1pl(miz[miz])+Acc(i[i])");
        assertParseCorrect("kendinizi", "kendi(kendi)+Pron+Reflex+A2pl+P2pl(niz[niz])+Acc(i[i])");
        assertParseCorrect("kendilerini", "kendi(kendi)+Pron+Reflex+A3pl(leri[leri])+P3pl+Acc(ni[ni])");
        assertParseCorrect("kendisini", "kendi(kendi)+Pron+Reflex+A3sg+P3sg(si[si])+Acc(ni[ni])");
        assertParseCorrect("kendilerimizi", "kendi(kendi)+Pron+Reflex+A1pl(ler[ler])+P1pl(imiz[imiz])+Acc(i[i])");
        assertParseCorrect("kendilerinizi", "kendi(kendi)+Pron+Reflex+A2pl(ler[ler])+P2pl(iniz[iniz])+Acc(i[i])");

        assertParseCorrect("kendime", "kendi(kendi)+Pron+Reflex+A1sg+P1sg(m[m])+Dat(e[e])");
        assertParseCorrect("kendine", "kendi(kendi)+Pron+Reflex+A2sg+P2sg(n[n])+Dat(e[e])", "kendi(kendi)+Pron+Reflex+A3sg+P3sg+Dat(ne[ne])");
        assertParseCorrect("kendimize", "kendi(kendi)+Pron+Reflex+A1pl+P1pl(miz[miz])+Dat(e[e])");
        assertParseCorrect("kendinize", "kendi(kendi)+Pron+Reflex+A2pl+P2pl(niz[niz])+Dat(e[e])");
        assertParseCorrect("kendilerine", "kendi(kendi)+Pron+Reflex+A3pl(leri[leri])+P3pl+Dat(ne[ne])");
        assertParseCorrect("kendisine", "kendi(kendi)+Pron+Reflex+A3sg+P3sg(si[si])+Dat(ne[ne])");
        assertParseCorrect("kendilerimize", "kendi(kendi)+Pron+Reflex+A1pl(ler[ler])+P1pl(imiz[imiz])+Dat(e[e])");
        assertParseCorrect("kendilerinize", "kendi(kendi)+Pron+Reflex+A2pl(ler[ler])+P2pl(iniz[iniz])+Dat(e[e])");

        assertParseCorrect("kendimde", "kendi(kendi)+Pron+Reflex+A1sg+P1sg(m[m])+Loc(de[de])");
        assertParseCorrect("kendinde", "kendi(kendi)+Pron+Reflex+A2sg+P2sg(n[n])+Loc(de[de])", "kendi(kendi)+Pron+Reflex+A3sg+P3sg+Loc(nde[nde])");
        assertParseCorrect("kendimizde", "kendi(kendi)+Pron+Reflex+A1pl+P1pl(miz[miz])+Loc(de[de])");
        assertParseCorrect("kendinizde", "kendi(kendi)+Pron+Reflex+A2pl+P2pl(niz[niz])+Loc(de[de])");
        assertParseCorrect("kendilerinde", "kendi(kendi)+Pron+Reflex+A3pl(leri[leri])+P3pl+Loc(nde[nde])");
        assertParseCorrect("kendisinde", "kendi(kendi)+Pron+Reflex+A3sg+P3sg(si[si])+Loc(nde[nde])");
        assertParseCorrect("kendilerimizde", "kendi(kendi)+Pron+Reflex+A1pl(ler[ler])+P1pl(imiz[imiz])+Loc(de[de])");
        assertParseCorrect("kendilerinizde", "kendi(kendi)+Pron+Reflex+A2pl(ler[ler])+P2pl(iniz[iniz])+Loc(de[de])");

        assertParseCorrect("kendimden", "kendi(kendi)+Pron+Reflex+A1sg+P1sg(m[m])+Abl(den[den])");
        assertParseCorrect("kendinden", "kendi(kendi)+Pron+Reflex+A2sg+P2sg(n[n])+Abl(den[den])", "kendi(kendi)+Pron+Reflex+A3sg+P3sg+Abl(nden[nden])", "kendinden(kendinden)+Adv");
        assertParseCorrect("kendimizden", "kendi(kendi)+Pron+Reflex+A1pl+P1pl(miz[miz])+Abl(den[den])");
        assertParseCorrect("kendinizden", "kendi(kendi)+Pron+Reflex+A2pl+P2pl(niz[niz])+Abl(den[den])");
        assertParseCorrect("kendilerinden", "kendi(kendi)+Pron+Reflex+A3pl(leri[leri])+P3pl+Abl(nden[nden])");
        assertParseCorrect("kendisinden", "kendi(kendi)+Pron+Reflex+A3sg+P3sg(si[si])+Abl(nden[nden])");
        assertParseCorrect("kendilerimizden", "kendi(kendi)+Pron+Reflex+A1pl(ler[ler])+P1pl(imiz[imiz])+Abl(den[den])");
        assertParseCorrect("kendilerinizden", "kendi(kendi)+Pron+Reflex+A2pl(ler[ler])+P2pl(iniz[iniz])+Abl(den[den])");

        assertParseCorrect("kendimin", "kendi(kendi)+Pron+Reflex+A1sg+P1sg(m[m])+Gen(in[in])");
        assertParseCorrect("kendinin", "kendi(kendi)+Pron+Reflex+A2sg+P2sg(n[n])+Gen(in[in])", "kendi(kendi)+Pron+Reflex+A3sg+P3sg+Gen(nin[nin])");
        assertParseCorrect("kendimizin", "kendi(kendi)+Pron+Reflex+A1pl+P1pl(miz[miz])+Gen(in[in])");
        assertParseCorrect("kendinizin", "kendi(kendi)+Pron+Reflex+A2pl+P2pl(niz[niz])+Gen(in[in])");
        assertParseCorrect("kendilerinin", "kendi(kendi)+Pron+Reflex+A3pl(leri[leri])+P3pl+Gen(nin[nin])");
        assertParseCorrect("kendisinin", "kendi(kendi)+Pron+Reflex+A3sg+P3sg(si[si])+Gen(nin[nin])");
        assertParseCorrect("kendilerimizin", "kendi(kendi)+Pron+Reflex+A1pl(ler[ler])+P1pl(imiz[imiz])+Gen(in[in])");
        assertParseCorrect("kendilerinizin", "kendi(kendi)+Pron+Reflex+A2pl(ler[ler])+P2pl(iniz[iniz])+Gen(in[in])");

        assertParseCorrect("kendimle", "kendi(kendi)+Pron+Reflex+A1sg+P1sg(m[m])+Ins(le[le])");
        assertParseCorrect("kendinle", "kendi(kendi)+Pron+Reflex+A2sg+P2sg(n[n])+Ins(le[le])");
        assertParseCorrect("kendiyle", "kendi(kendi)+Pron+Reflex+A3sg+P3sg+Ins(yle[yle])");
        assertParseCorrect("kendimizle", "kendi(kendi)+Pron+Reflex+A1pl+P1pl(miz[miz])+Ins(le[le])");
        assertParseCorrect("kendinizle", "kendi(kendi)+Pron+Reflex+A2pl+P2pl(niz[niz])+Ins(le[le])");
        assertParseCorrect("kendileriyle", "kendi(kendi)+Pron+Reflex+A3pl(leri[leri])+P3pl+Ins(yle[yle])");
        assertParseCorrect("kendisiyle", "kendi(kendi)+Pron+Reflex+A3sg+P3sg(si[si])+Ins(yle[yle])");
        assertParseCorrect("kendilerimizle", "kendi(kendi)+Pron+Reflex+A1pl(ler[ler])+P1pl(imiz[imiz])+Ins(le[le])");
        assertParseCorrect("kendilerinizle", "kendi(kendi)+Pron+Reflex+A2pl(ler[ler])+P2pl(iniz[iniz])+Ins(le[le])");
    }

    @Test
    public void shouldParsePronounHepsi() {
        assertParseCorrect("hepsi", "hepsi(hepsi)+Pron+A3pl+P3pl+Nom");
        assertParseCorrect("hepsini", "hepsi(hepsi)+Pron+A3pl+P3pl+Acc(ni[ni])");
        assertParseCorrect("hepimize", "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Dat(e[e])");
        assertParseCorrect("hepinizle", "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Ins(le[le])");
    }

    @Test
    public void shouldParseAdjToNounZeroTransition() {
        removeRoots("gen");

        assertParseCorrect("maviye", "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Dat(+yA[ye])");
        assertParseCorrect("gencin", "genc(genç)+Adj+Noun+Zero+A3sg+Pnon+Gen(+nIn[in])", "genc(genç)+Adj+Noun+Zero+A3sg+P2sg(+In[in])+Nom");
    }

    @Test
    public void shouldParseVerbToAdvDerivations() {
        assertParseCorrect("yapınca", "yap(yapmak)+Verb+Pos+Adv+When(+yIncA[ınca])");
        assertParseCorrect("yapmayınca", "yap(yapmak)+Verb+Neg(mA[ma])+Adv+When(+yIncA[yınca])");
        assertParseCorrect("dönünce", "dön(dönmek)+Verb+Pos+Adv+When(+yIncA[ünce])");
        assertParseCorrect("dönmeyince", "dön(dönmek)+Verb+Neg(mA[me])+Adv+When(+yIncA[yince])");
        assertParseCorrect("yalayınca", "yala(yalamak)+Verb+Pos+Adv+When(+yIncA[yınca])");
        assertParseCorrect("yalamayınca", "yala(yalamak)+Verb+Neg(mA[ma])+Adv+When(+yIncA[yınca])");
        assertParseCorrect("çıkarttırabilince", "çık(çıkmak)+Verb+Verb+Caus(Ar[ar])+Verb+Caus(!t[t])+Verb+Caus(dIr[tır])+Verb+Able(+yAbil[abil])+Pos+Adv+When(+yIncA[ince])");
        assertParseCorrect("yaptıramayınca", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Able(+yA[a])+Neg(mA[ma])+Adv+When(+yIncA[yınca])");

        removeRoots("dönel");

        assertParseCorrect("yapalı", "yap(yapmak)+Verb+Pos+Adv+SinceDoingSo(+yAl!I[alı])");
        assertParseCorrect("yapmayalı", "yap(yapmak)+Verb+Neg(mA[ma])+Adv+SinceDoingSo(+yAl!I[yalı])");
        assertParseCorrect("döneli", "dön(dönmek)+Verb+Pos+Adv+SinceDoingSo(+yAl!I[eli])");
        assertParseCorrect("dönmeyeli", "dön(dönmek)+Verb+Neg(mA[me])+Adv+SinceDoingSo(+yAl!I[yeli])");
        assertParseCorrect("yalayalı", "yala(yalamak)+Verb+Pos+Adv+SinceDoingSo(+yAl!I[yalı])");
        assertParseCorrect("yalamayalı", "yala(yalamak)+Verb+Neg(mA[ma])+Adv+SinceDoingSo(+yAl!I[yalı])");
        assertParseCorrect("çıkarttırabileli", "çık(çıkmak)+Verb+Verb+Caus(Ar[ar])+Verb+Caus(!t[t])+Verb+Caus(dIr[tır])+Verb+Able(+yAbil[abil])+Pos+Adv+SinceDoingSo(+yAl!I[eli])");
        assertParseCorrect("yaptıramayalı", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Able(+yA[a])+Neg(mA[ma])+Adv+SinceDoingSo(+yAl!I[yalı])");

        assertParseCorrect("yaparken", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Adv+While(ken[ken])");
        assertParseCorrect("yapmazken", "yap(yapmak)+Verb+Neg(mA[ma])+Aor(z[z])+Adv+While(ken[ken])");
        assertParseCorrect("dönerlerken", "dön(dönmek)+Verb+Pos+Aor(+Ar[er])+A3pl(lAr[ler])+Adv+While(ken[ken])");
        assertParseCorrect("dönmezlerken", "dön(dönmek)+Verb+Neg(mA[me])+Aor(z[z])+A3pl(lAr[ler])+Adv+While(ken[ken])");
        assertParseCorrect("yalayacakken", "yala(yalamak)+Verb+Pos+Fut(+yAcAk[yacak])+Adv+While(ken[ken])");
        assertParseCorrect("yalamayacakken", "yala(yalamak)+Verb+Neg(mA[ma])+Fut(+yAcAk[yacak])+Adv+While(ken[ken])");
        assertParseCorrect("çıkarttırabilmişken", "çık(çıkmak)+Verb+Verb+Caus(Ar[ar])+Verb+Caus(!t[t])+Verb+Caus(dIr[tır])+Verb+Able(+yAbil[abil])+Pos+Narr(mIş[miş])+Adv+While(ken[ken])");
        assertParseCorrect("yaptıramıyorken", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Able(+yA[a])+Neg(m[m])+Prog(Iyor[ıyor])+Adv+While(ken[ken])");
        assertParseCorrect("yaptıramıyorlarken", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Able(+yA[a])+Neg(m[m])+Prog(Iyor[ıyor])+A3pl(lAr[lar])+Adv+While(ken[ken])");
        assertParseCorrect("yaptıramamışlarken", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Able(+yA[a])+Neg(mA[ma])+Narr(mIş[mış])+A3pl(lAr[lar])+Adv+While(ken[ken])");

        assertParseCorrect("yaparcasına", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Adv+AsIf(cAs!InA[casına])");
        assertParseCorrect("yaparlarcasına", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+A3pl(lAr[lar])+Adv+AsIf(cAs!InA[casına])");
        assertParseCorrect("yaparmışçasına", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Narr(mIş[mış])+Adv+AsIf(cAs!InA[çasına])");
        assertParseCorrect("yapmamışçasına", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+Adv+AsIf(cAs!InA[çasına])");
        assertParseCorrect("yapmamışlarcasına", "yap(yapmak)+Verb+Neg(mA[ma])+Narr(mIş[mış])+A3pl(lAr[lar])+Adv+AsIf(cAs!InA[casına])");
        assertParseCorrect("yapacakmışçasına", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Narr(mIş[mış])+Adv+AsIf(cAs!InA[çasına])");
        assertParseCorrect("yapacakmışlarcasına", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Narr(mIş[mış])+A3pl(lAr[lar])+Adv+AsIf(cAs!InA[casına])");
        assertParseCorrect("yaptıramazcasına", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Able(+yA[a])+Neg(mA[ma])+Aor(z[z])+Adv+AsIf(cAs!InA[casına])");
        assertParseCorrect("yaptıramazlarcasına", "yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Able(+yA[a])+Neg(mA[ma])+Aor(z[z])+A3pl(lAr[lar])+Adv+AsIf(cAs!InA[casına])");

    }

    @Test
    public void shouldParseAdjToAdjDerivations() {
        removeRoots("koy", "kırmız");

        assertParseCorrect("kırmızımsı", "kırmızı(kırmızı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+JustLike(+ImsI[msı])");
        assertParseCorrect("yeşilimsi", "yeşil(yeşil)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+JustLike(+ImsI[imsi])");
        assertParseCorrect("koyumsu", "koyu(koyu)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+JustLike(+ImsI[msu])");
    }

    @Test
    public void shouldParseWordsWithSuffixesCe() {
        removeRoots("babac", "babaca");

        assertParseCorrect("aptalca",
                "aptal(aptal)+Adj+Adj+Equ(cA[ca])",
                "aptal(aptal)+Adj+Adj+Quite(cA[ca])",
                "aptal(aptal)+Adj+Adv+Ly(cA[ca])",
                "aptal(aptal)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ca])",
                "aptal(aptal)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ca])",
                "aptal(aptal)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ca])"
        );

        assertParseCorrect("delice",
                "deli(deli)+Adj+Adj+Equ(cA[ce])",
                "deli(deli)+Adj+Adj+Quite(cA[ce])",
                "deli(deli)+Adj+Adv+Ly(cA[ce])",
                "delice(delice)+Noun+A3sg+Pnon+Nom",
                "deli(deli)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])",
                "deli(deli)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ce])",
                "deli(deli)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ce])"
        );

        assertParseCorrect("babaca",
                "baba(baba)+Noun+A3sg+Pnon+Nom+Adj+Equ(cA[ca])",
                "baba(baba)+Noun+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ca])",
                "baba(baba)+Noun+A3sg+Pnon+Nom+Adv+By(cA[ca])");

        assertParseCorrect("iyice",
                "iyi(iyi)+Adj+Adj+Equ(cA[ce])",
                "iyi(iyi)+Adj+Adj+Quite(cA[ce])",
                "iyi(iyi)+Adj+Adv+Ly(cA[ce])",
                "iyi(iyi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])",
                "iyi(iyi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ce])",
                "iyi(iyi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ce])");

        assertParseCorrect("öylece",
                "öyle(öyle)+Adj+Adj+Equ(cA[ce])",
                "öyle(öyle)+Adj+Adj+Quite(cA[ce])",
                "öyle(öyle)+Adj+Adv+Ly(cA[ce])",
                "öyle(öyle)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])",
                "öyle(öyle)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ce])",
                "öyle(öyle)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ce])");

        assertParseCorrect("sayıca",
                "sayı(sayı)+Noun+A3sg+Pnon+Nom+Adj+Equ(cA[ca])",
                "sayı(sayı)+Noun+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ca])",
                "sayı(sayı)+Noun+A3sg+Pnon+Nom+Adv+By(cA[ca])");

        assertParseCorrect("onlarca",
                "o(o)+Pron+Pers+A3pl(nlar[nlar])+Pnon+AccordingTo(ca[ca])");

        assertParseCorrect("saatlerce",
                "saat(saat)+Noun+A3sg+Pnon+Nom+Adv+ManyOf(lArcA[lerce])",
                "saat(saat)+Noun+A3pl(lAr[ler])+Pnon+Nom+Adj+Equ(cA[ce])",
                "saat(saat)+Noun+A3pl(lAr[ler])+Pnon+Nom+Adv+InTermsOf(cA[ce])",
                "saat(saat)+Noun+A3pl(lAr[ler])+Pnon+Nom+Adv+By(cA[ce])",
                "saat(saat)+Noun+Time+A3sg+Pnon+Nom+Adv+ManyOf(lArcA[lerce])",
                "saat(saat)+Noun+Time+A3sg+Pnon+Nom+Adv+ForALotOfTime(lArcA[lerce])",
                "saat(saat)+Noun+Time+A3pl(lAr[ler])+Pnon+Nom+Adj+Equ(cA[ce])",
                "saat(saat)+Noun+Time+A3pl(lAr[ler])+Pnon+Nom+Adv+InTermsOf(cA[ce])",
                "saat(saat)+Noun+Time+A3pl(lAr[ler])+Pnon+Nom+Adv+By(cA[ce])");

        assertParseCorrect("saliselerce",
                "salise(salise)+Noun+Time+A3sg+Pnon+Nom+Adv+ManyOf(lArcA[lerce])",
                "salise(salise)+Noun+Time+A3sg+Pnon+Nom+Adv+ForALotOfTime(lArcA[lerce])",
                "salise(salise)+Noun+Time+A3pl(lAr[ler])+Pnon+Nom+Adj+Equ(cA[ce])",
                "salise(salise)+Noun+Time+A3pl(lAr[ler])+Pnon+Nom+Adv+InTermsOf(cA[ce])",
                "salise(salise)+Noun+Time+A3pl(lAr[ler])+Pnon+Nom+Adv+By(cA[ce])");

        assertParseCorrect("makamlarca",
                "makam(makam)+Noun+A3sg+Pnon+Nom+Adv+ManyOf(lArcA[larca])",
                "makam(makam)+Noun+A3pl(lAr[lar])+Pnon+Nom+Adj+Equ(cA[ca])",
                "makam(makam)+Noun+A3pl(lAr[lar])+Pnon+Nom+Adv+InTermsOf(cA[ca])",
                "makam(makam)+Noun+A3pl(lAr[lar])+Pnon+Nom+Adv+By(cA[ca])");

        assertParseCorrect("organlarınca",      // as in "bu islem duyu organlarinca yapilir."
                "organ(organ)+Noun+A3sg+P3pl(lAr!I[ları])+Nom+Adv+By(ncA[nca])",
                "organ(organ)+Noun+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom+Adv+By(ncA[nca])",
                "organ(organ)+Noun+A3pl(lAr[lar])+P3pl(!I[ı])+Nom+Adv+By(ncA[nca])");

        removeRoots("on", "onca", "bizle");
        removeRootsExceptTheOneWithPrimaryPos("ben", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("biz", PrimaryPos.Pronoun);

        assertParseCorrect("bence", "ben(ben)+Pron+Pers+A1sg+Pnon+AccordingTo(ce[ce])");
        assertParseCorrect("sence", "sen(sen)+Pron+Pers+A2sg+Pnon+AccordingTo(ce[ce])");
        assertParseCorrect("onca", "o(o)+Pron+Pers+A3sg+Pnon+AccordingTo(nca[nca])");
        assertParseCorrect("bizce", "biz(biz)+Pron+Pers+A1pl+Pnon+AccordingTo(ce[ce])");
        assertParseCorrect("sizce", "siz(siz)+Pron+Pers+A2pl+Pnon+AccordingTo(ce[ce])");
        assertParseCorrect("onlarca", "o(o)+Pron+Pers+A3pl(nlar[nlar])+Pnon+AccordingTo(ca[ca])");
        assertParseCorrect("bizlerce", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+AccordingTo(ce[ce])");
        assertParseCorrect("sizlerce", "siz(siz)+Pron+Pers+A2pl(ler[ler])+Pnon+AccordingTo(ce[ce])");
        assertParseCorrect("hepimizce", "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+AccordingTo(ce[ce])");
        assertParseCorrect("hepinizce", "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+AccordingTo(ce[ce])");
        assertParseCorrect("hepsince", "hepsi(hepsi)+Pron+A3pl+P3pl+AccordingTo(nce[nce])");

        // kendince, kendimce, kendilerince vs...
    }

    @Test
    public void shouldParseVerbToAdjZeroTransition() {
        assertParseCorrect("pişmiş",
                "piş(pişmek)+Verb+Pos+Narr(mIş[miş])+A3sg",                                // yemek pismis.
                "piş(pişmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero"                             // pismis asa su katilmaz
        );

        assertParseCorrect("bilir",
                "bil(bilmek)+Verb+Pos+Aor(+Ir[ir])+A3sg",                                  // ali gelir.
                "bil(bilmek)+Verb+Pos+Aor(+Ir[ir])+Adj+Zero"                               // tartışılır konu
        );

        assertParseCorrect("pişmemiş",
                "piş(pişmek)+Verb+Neg(mA[me])+Narr(mIş[miş])+A3sg",
                "piş(pişmek)+Verb+Neg(mA[me])+Narr(mIş[miş])+Adj+Zero"
        );

        assertParseCorrect("bilmez",
                "bil(bilmek)+Verb+Neg(mA[me])+Aor(z[z])+A3sg",
                "bil(bilmek)+Verb+Neg(mA[me])+Aor(z[z])+Adj+Zero"
        );
    }

    @Test
    public void shouldParseDeYe() {
        removeRoots("der", "deri", "derle", "derd", "dirim", "diri", "deme");

        assertNotParsable("dirim");
        assertNotParsable("deyecek");
        assertNotParsable("deyor");
        assertNotParsable("deyen");

        assertParseCorrect("derim", "de(demek)+Verb+Pos+Aor(+Ar[r])+A1sg(+Im[im])");
        assertParseCorrect("derler", "de(demek)+Verb+Pos+Aor(+Ar[r])+A3pl(lAr[ler])");
        assertParseCorrect("dedim", "de(demek)+Verb+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("dediler", "de(demek)+Verb+Pos+Past(dI[di])+A3pl(lAr[ler])");
        assertParseCorrect("demişim", "de(demek)+Verb+Pos+Narr(mIş[miş])+A1sg(+Im[im])");
        assertParseCorrect("demişler", "de(demek)+Verb+Pos+Narr(mIş[miş])+A3pl(lAr[ler])");
        assertParseCorrect("diyeceğim", "di(demek)+Verb+Pos+Fut(yeceğ[yeceğ])+A1sg(+Im[im])", "di(demek)+Verb+Pos+Adj+FutPart(yeceğ[yeceğ])+P1sg(+Im[im])", "di(demek)+Verb+Pos+Noun+FutPart(yeceğ[yeceğ])+A3sg+P1sg(+Im[im])+Nom", "di(demek)+Verb+Pos+Fut(yeceğ[yeceğ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("diyecekler", "di(demek)+Verb+Pos+Fut(yecek[yecek])+A3pl(lAr[ler])", "di(demek)+Verb+Pos+Noun+FutPart(yecek[yecek])+A3pl(lAr[ler])+Pnon+Nom", "di(demek)+Verb+Pos+Fut(yecek[yecek])+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom");
        assertParseCorrect("diyorum", "di(demek)+Verb+Pos+Prog(yor[yor])+A1sg(+Im[um])");
        assertParseCorrect("diyorlar", "di(demek)+Verb+Pos+Prog(yor[yor])+A3pl(lAr[lar])");
        assertParseCorrect("demekteyim", "de(demek)+Verb+Pos+Prog(mAktA[mekte])+A1sg(yIm[yim])");
        assertParseCorrect("demekteler", "de(demek)+Verb+Pos+Prog(mAktA[mekte])+A3pl(lAr[ler])");
        assertParseCorrect("diyen", "di(demek)+Verb+Pos+Adj+PresPart(yen[yen])", "di(demek)+Verb+Pos+Adj+PresPart(yen[yen])+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("dediğim", "de(demek)+Verb+Pos+Adj+PastPart(dIk[diğ])+P1sg(+Im[im])", "de(demek)+Verb+Pos+Noun+PastPart(dIk[diğ])+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("dedikleri", "de(demek)+Verb+Pos+Adj+PastPart(dIk[dik])+P3pl(lAr!I[leri])", "de(demek)+Verb+Pos+Noun+PastPart(dIk[dik])+A3pl(lAr[ler])+Pnon+Acc(+yI[i])", "de(demek)+Verb+Pos+Noun+PastPart(dIk[dik])+A3pl(lAr[ler])+P3sp(+I[i])+Nom");
        assertParseCorrect("diyecekleri",
                "di(demek)+Verb+Pos+Adj+FutPart(yecek[yecek])+P3pl(lAr!I[leri])",
                "di(demek)+Verb+Pos+Noun+FutPart(yecek[yecek])+A3pl(lAr[ler])+Pnon+Acc(+yI[i])",
                "di(demek)+Verb+Pos+Noun+FutPart(yecek[yecek])+A3pl(lAr[ler])+P3sp(!I[i])+Nom",
                "di(demek)+Verb+Pos+Fut(yecek[yecek])+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Acc(+yI[i])",
                "di(demek)+Verb+Pos+Fut(yecek[yecek])+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+P3sp(!I[i])+Nom"
        );
        assertParseCorrect("diyebilirim", "di(demek)+Verb+Verb+Able(yebil[yebil])+Pos+Aor(+Ir[ir])+A1sg(+Im[im])");
        assertParseCorrect("diyebilirler", "di(demek)+Verb+Verb+Able(yebil[yebil])+Pos+Aor(+Ir[ir])+A3pl(lAr[ler])", "di(demek)+Verb+Verb+Able(yebil[yebil])+Pos+Aor(+Ir[ir])+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom");
        assertParseCorrect("dendim", "de(demek)+Verb+Verb+Pass(+In[n])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("dendiler", "de(demek)+Verb+Verb+Pass(+In[n])+Pos+Past(dI[di])+A3pl(lAr[ler])");
        assertParseCorrect("denildim", "de(demek)+Verb+Verb+Pass(+InIl[nil])+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("denildiler", "de(demek)+Verb+Verb+Pass(+InIl[nil])+Pos+Past(dI[di])+A3pl(lAr[ler])");
        assertParseCorrect("dedirir", "de(demek)+Verb+Verb+Caus(dIr[dir])+Pos+Aor(+Ir[ir])+A3sg", "de(demek)+Verb+Verb+Caus(dIr[dir])+Pos+Aor(+Ir[ir])+Adj+Zero", "de(demek)+Verb+Verb+Caus(dIr[dir])+Verb+Caus(Ir[ir])+Pos+Imp+A2sg");
        assertParseCorrect("dedirirler", "de(demek)+Verb+Verb+Caus(dIr[dir])+Pos+Aor(+Ir[ir])+A3pl(lAr[ler])", "de(demek)+Verb+Verb+Caus(dIr[dir])+Pos+Aor(+Ir[ir])+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom");
        assertParseCorrect("dediyse", "de(demek)+Verb+Pos+Past(dI[di])+Cond(+ysA[yse])+A3sg");
        assertParseCorrect("dediyseler", "de(demek)+Verb+Pos+Past(dI[di])+Cond(+ysA[yse])+A3pl(lAr[ler])");
        assertParseCorrect("demeliyim", "de(demek)+Verb+Pos+Neces(mAl!I[meli])+A1sg(yIm[yim])");
        assertParseCorrect("demeliler", "de(demek)+Verb+Pos+Neces(mAl!I[meli])+A3pl(lAr[ler])");
        assertParseCorrect("diye", "di(demek)+Verb+Pos+Opt(ye[ye])+A3sg", "diye(diye)+Adv");
        assertParseCorrect("dese", "de(demek)+Verb+Pos+Desr(sA[se])+A3sg");
        assertParseCorrect("deseler", "de(demek)+Verb+Pos+Desr(sA[se])+A3pl(lAr[ler])");
        assertParseCorrect("diyordular", "di(demek)+Verb+Pos+Prog(yor[yor])+Past(dI[du])+A3pl(lAr[lar])");
        assertParseCorrect("demekteydiler", "de(demek)+Verb+Pos+Prog(mAktA[mekte])+Past(ydI[ydi])+A3pl(lAr[ler])");
        assertParseCorrect("derdiniz", "de(demek)+Verb+Pos+Aor(+Ar[r])+Past(dI[di])+A2pl(nIz[niz])");
        assertParseCorrect("der", "de(demek)+Verb+Pos+Aor(+Ar[r])+A3sg", "de(demek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero");
        assertParseCorrect("denir", "de(demek)+Verb+Verb+Pass(+In[n])+Pos+Aor(+Ir[ir])+A3sg", "de(demek)+Verb+Verb+Pass(+In[n])+Pos+Aor(+Ir[ir])+Adj+Zero");
        assertParseCorrect("denmiş", "de(demek)+Verb+Verb+Pass(+In[n])+Pos+Narr(mIş[miş])+A3sg", "de(demek)+Verb+Verb+Pass(+In[n])+Pos+Narr(mIş[miş])+Adj+Zero");
        assertParseCorrect("denilmiş", "de(demek)+Verb+Verb+Pass(+InIl[nil])+Pos+Narr(mIş[miş])+A3sg", "de(demek)+Verb+Verb+Pass(+InIl[nil])+Pos+Narr(mIş[miş])+Adj+Zero");
        assertParseCorrect("diyen", "di(demek)+Verb+Pos+Adj+PresPart(yen[yen])");
        assertParseCorrect("diyenler", "di(demek)+Verb+Pos+Adj+PresPart(yen[yen])+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom");
        assertParseCorrect("de", "de(de)+Conj", "de(demek)+Verb+Pos+Imp+A2sg");
        assertParseCorrect("desinler", "de(demek)+Verb+Pos+Imp+A3pl(sInlAr[sinler])");
        assertParseCorrect("diyerek", "di(demek)+Verb+Pos+Adv+ByDoingSo(yerek[yerek])");
    }

    @Test
    public void shouldParseSomeProblematicWords() {
        removeRoots("deyi");

        assertParseCorrect("bitirelim", "bit(bitmek)+Verb+Verb+Caus(Ir[ir])+Pos+Opt(A[e])+A1pl(lIm[lim])");
        assertParseCorrect("bulmalıyım", "bul(bulmak)+Verb+Pos+Neces(mAl!I[malı])+A1sg(yIm[yım])");
        assertParseCorrect("diyordunuz", "di(demek)+Verb+Pos+Prog(yor[yor])+Past(dI[du])+A2pl(nIz[nuz])");
        assertParseCorrect("yiyoruz", "yi(yemek)+Verb+Pos+Prog(yor[yor])+A1pl(+Iz[uz])");
        assertParseCorrect("deyin", "de(demek)+Verb+Pos+Imp+A2pl(+yIn[yin])");
        assertParseCorrect("yiyin", "yi(yemek)+Verb+Pos+Imp+A2pl(yin[yin])");
        assertParseCorrect("baksana", "bak(bakmak)+Verb+Pos+Imp(sAnA[sana])+A2sg");
        assertParseCorrect("gelsenize", "gel(gelmek)+Verb+Pos+Imp(sAnIzA[senize])+A2pl");
        assertParseCorrect("yapan", "yap(yapmak)+Verb+Pos+Adj+PresPart(+yAn[an])");
        assertParseCorrect("diyen", "di(demek)+Verb+Pos+Adj+PresPart(yen[yen])");
        assertParseCorrect("duyumsatınca", "duyumsa(duyumsamak)+Verb+Verb+Caus(!t[t])+Pos+Adv+When(+yIncA[ınca])");
        assertParseCorrect("duyumsatıncaya", "xxxxxxxxx");
        assertNotParsable("yaparkene");
        assertNotParsable("yapıba");
        assertNotParsable("yaparcasınaya");
        assertParseCorrect("evsahibi", "evsahib(evsahibi)+Noun+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrect("serpiştirilmiş",
                "serp(serpmek)+Verb+Verb+Recip(+Iş[iş])+Verb+Caus(dIr[tir])+Verb+Pass(+nIl[il])+Pos+Narr(mIş[miş])+A3sg",
                "serp(serpmek)+Verb+Verb+Recip(+Iş[iş])+Verb+Caus(dIr[tir])+Verb+Pass(+nIl[il])+Pos+Narr(mIş[miş])+Adj+Zero"
        );
        assertParseCorrect("reddine", "redd(ret)+Noun+A3sg+P2sg(+In[in])+Dat(+yA[e])", "redd(ret)+Noun+A3sg+P3sg(+sI[i])+Dat(nA[ne])");
        assertParseCorrect("yanlışlanabilirlik", "yanlışla(yanlışlamak)+Verb+Verb+Pass(+In[n])+Verb+Able(+yAbil[abil])+Pos+Aor(+Ir[ir])+Adj+Zero+Noun+Ness(lIk[lik])+A3sg+Pnon+Nom");

        assertParseCorrect("içerde", "içer(içeri)+Noun+A3sg+Pnon+Loc(de[de])", "iç(içmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Noun+Zero+A3sg+Pnon+Loc(dA[de])");
        assertParseCorrect("içerden", "içer(içeri)+Noun+A3sg+Pnon+Abl(den[den])", "iç(içmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Noun+Zero+A3sg+Pnon+Abl(dAn[den])");
        assertParseCorrect("dışarda", "dışar(dışarı)+Noun+A3sg+Pnon+Loc(da[da])");
        assertParseCorrect("dışardan", "dışar(dışarı)+Noun+A3sg+Pnon+Abl(dan[dan])");
        assertParseCorrect("inandırıcı", "inan(inanmak)+Verb+Verb+Caus(dIr[dır])+Pos+Adj+Agt(+yIcI[ıcı])");
        assertParseCorrect("hangisini", "hangi(hangi)+Adj+Noun+Zero+A3sg+P3sg(+sI[si])+Acc(nI[ni])");
        assertParseCorrect("kaynakçılığını", "kaynak(kaynak)+Noun+A3sg+Pnon+Nom+Adj+Agt(cI[çı])+Noun+Zero+A3sg+Pnon+Nom+Noun+Title(lIk[lığ])+A3sg+P2sg(+In[ın])+Acc(+yI[ı])");
        assertParseCorrect("yediremiyordu", "ye(yemek)+Verb+Verb+Caus(dIr[dir])+Verb+Able(+yA[e])+Neg(m[m])+Prog(Iyor[iyor])+Past(dI[du])+A3sg");
        assertParseCorrect("saatlerce", "saat(saat)+Noun+Time+A3sg+Pnon+Nom+Adv+ForALotOfTime(lArcA[lerce])");
        assertParseCorrect("defalarca", "defa(defa)+Noun+A3sg+Pnon+Nom+Adv+ManyOf(lArcA[larca])");
        assertParseCorrect("deyip", "de(demek)+Verb+Pos+Adv+AfterDoingSo(+yI!p[yip])");
        assertParseCorrect("yiyip", "yi(yemek)+Verb+Pos+Adv+AfterDoingSo(yip[yip])");
        assertParseCorrect("tıkıştırmak", "tık(tıkmak)+Verb+Verb+Recip(+Iş[ış])+Verb+Caus(dIr[tır])+Pos+Noun+Inf(mAk[mak])+A3sg+Pnon+Nom");
        assertParseCorrect("büyütülüyor", "büyü(büyümek)+Verb+Verb+Caus(!t[t])+Verb+Pass(+nIl[ül])+Pos+Prog(Iyor[üyor])+A3sg");
        assertParseCorrect("dayatabilir", "daya(dayamak)+Verb+Verb+Caus(!t[t])+Verb+Able(+yAbil[abil])+Pos+Aor(+Ir[ir])+A3sg", "daya(dayamak)+Verb+Verb+Caus(!t[t])+Verb+Able(+yAbil[abil])+Pos+Aor(+Ir[ir])+Adj+Zero");
        assertParseCorrect("çağrılıyor", "çağr(çağırmak)+Verb+Verb+Pass(+nIl[ıl])+Pos+Prog(Iyor[ıyor])+A3sg");
        assertParseCorrect("tutturmuştuk", "tut(tutmak)+Verb+Verb+Caus(dIr[tur])+Pos+Narr(mIş[muş])+Past(dI[tu])+A1pl(!k[k])");
        assertParseCorrect("yaparcasına", "yap(yapmak)+Verb+Pos+Aor(+Ar[ar])+Adv+AsIf(cAs!InA[casına])");
        assertParseCorrect("okur", "oku(okumak)+Verb+Pos+Aor(+Ar[r])+A3sg");
        assertParseCorrect("okurcasına", "oku(okumak)+Verb+Pos+Aor(+Ar[r])+Adv+AsIf(cAs!InA[casına])");
        assertParseCorrect("okumuşçasına", "oku(okumak)+Verb+Pos+Narr(mIş[muş])+Adv+AsIf(cAs!InA[çasına])");
        assertParseCorrect("yeşillikleri", "yeşil(yeşil)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Y(lIk[lik])+Noun+Zero+A3sg+P3pl(lAr!I[leri])+Nom");
        assertParseCorrect("sulandı", "sula(sulamak)+Verb+Verb+Pass(+In[n])+Pos+Past(dI[dı])+A3sg");
        assertParseCorrect("sulaştı", "sula(sulamak)+Verb+Verb+Recip(+Iş[ş])+Pos+Past(dI[tı])+A3sg");

        assertParseCorrect("yapmadan", "yap(yapmak)+Verb+Pos+Adv+WithoutHavingDoneSo(mAdAn[madan])", "yap(yapmak)+Verb+Pos+Noun+Inf(mA[ma])+A3sg+Pnon+Abl(dAn[dan])");
        assertParseCorrect("yapamadan", "yap(yapmak)+Verb+Verb+Able(+yA[a])+Neg+Adv+WithoutHavingDoneSo(mAdAn[madan])");
        assertParseCorrect("diyemeden", "di(demek)+Verb+Verb+Able(ye[ye])+Neg+Adv+WithoutHavingDoneSo(meden[meden])");

        assertParseCorrect("yaparları", "XXXXXXXX");
        assertParseCorrect("yapacakları", "XXXXXXXX");
        assertParseCorrect("yapmışları", "XXXXXXXX");
        assertParseCorrect("yaptıkları", "XXXXXXXX");
    }

    @Test
    public void shouldParseWordsWith_su_ne() {
        removeRoots("ney", "sula");

        assertParseCorrect("neyi", "ne(ne)+Pron+Ques+A3sg+P3sg(yi[yi])+Nom", "ne(ne)+Pron+Ques+A3sg+Pnon+Acc(+yI[yi])");
        assertParseCorrect("neyin", "ne(ne)+Pron+Ques+A3sg+Pnon+Gen(yin[yin])", "ne(ne)+Pron+Ques+A3sg+P2sg(yin[yin])+Nom");
        assertParseCorrect("nesi", "ne(ne)+Pron+Ques+A3sg+P3sg(si[si])+Nom");
        assertParseCorrect("neyim", "ne(ne)+Pron+Ques+A3sg+P1sg(yim[yim])+Nom");
        assertParseCorrect("nen", "ne(ne)+Pron+Ques+A3sg+P2sg(n[n])+Nom");
        assertParseCorrect("neyine", "ne(ne)+Pron+Ques+A3sg+P2sg(yin[yin])+Dat(+yA[e])", "ne(ne)+Pron+Ques+A3sg+P3sg(yi[yi])+Dat(nA[ne])");

        assertParseCorrect("suyu", "su(su)+Noun+A3sg+P3sg(yu[yu])+Nom", "su(su)+Noun+A3sg+Pnon+Acc(+yI[yu])");
        assertParseCorrect("suyum", "su(su)+Noun+A3sg+P1sg(yum[yum])+Nom");
        assertParseCorrect("suları", "su(su)+Noun+A3pl(lAr[lar])+P3sp(!I[ı])+Nom", "su(su)+Noun+A3pl(lar[lar])+Pnon+Acc(+yI[ı])");
        assertParseCorrect("suyuna", "su(su)+Noun+A3sg+P3sg(yu[yu])+Dat(nA[na])", "su(su)+Noun+A3sg+P2sg(yun[yun])+Dat(+yA[a])");
    }

    @Test
    public void shouldParseQuestionParticles() {
        assertParseCorrect("mı", "mı(mı)+Ques+Pres+A3sg");
        assertParseCorrect("mü", "mü(mü)+Ques+Pres+A3sg");
        assertParseCorrect("müydük", "mü(mü)+Ques+Past(ydü[ydü])+A1pl(k[k])");
        assertParseCorrect("mıydılar", "mı(mı)+Ques+Past(ydı[ydı])+A3pl(lar[lar])");
        assertParseCorrect("mıyız", "mı(mı)+Ques+Pres+A1pl(yız[yız])");
        assertParseCorrect("miymiş", "mi(mi)+Ques+Narr(ymiş[ymiş])+A3sg");
        assertParseCorrect("miymişsiniz", "mi(mi)+Ques+Narr(ymiş[ymiş])+A2pl(siniz[siniz])");
    }

    @Test
    public void shouldParseNounCompounds() {
        assertParseCorrect("zeytinyağı", "zeytinyağ(zeytinyağı)+Noun+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrect("zeytinyağına", "zeytinyağ(zeytinyağı)+Noun+A3sg+P3sg(+sI[ı])+Dat(nA[na])");
        assertParseCorrect("zeytinyağlı", "zeytinyağ(zeytinyağı)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])");
        assertParseCorrect("zeytinyağlıya", "zeytinyağ(zeytinyağı)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Noun+Zero+A3sg+Pnon+Dat(+yA[ya])");
        assertParseCorrect("zeytinyağları", "zeytinyağ(zeytinyağı)+Noun+A3sg+P3pl(lAr!I[ları])+Nom");
        assertParseCorrect("zeytinyağlarını", "zeytinyağ(zeytinyağı)+Noun+A3sg+P3pl(lAr!I[ları])+Acc(nI[nı])");

        removeRoots("a", "ak", "akşam");
        removeRootsExceptTheOneWithPrimaryPos("akşamüstü", PrimaryPos.Noun);

        assertParseCorrect("akşamüstü", "akşamüst(akşamüstü)+Noun+Time+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrect("akşamüstleri", "akşamüst(akşamüstü)+Noun+Time+A3sg+P3pl(lAr!I[leri])+Nom");
        assertParseCorrect("akşamüstüne", "akşamüst(akşamüstü)+Noun+Time+A3sg+P3sg(+sI[ü])+Dat(nA[ne])");
        assertParseCorrect("akşamüstlü", "akşamüst(akşamüstü)+Noun+Time+A3sg+Pnon+Nom+Adj+With(lI[lü])");

        assertParseCorrect("zeytinyağım", "xxxx");
        assertParseCorrect("zeytinyağın", "xxxx");
        assertParseCorrect("zeytinyağı", "xxxx");
        assertParseCorrect("zeytinyağlarım", "xxxx");
        assertParseCorrect("zeytinyağların", "xxxx");
        assertParseCorrect("zeytinyağları", "xxxx");
    }

    @Test
    public void shouldParseSomeWordsWithVowelDrops() {
        assertParseCorrect("vaktimi", "vakt(vakit)+Noun+A3sg+P1sg(+Im[im])+Acc(+yI[i])");
        assertParseCorrect("havliyle", "havl(havil)+Noun+A3sg+P3sg(+sI[i])+Ins(+ylA[yle])");
        assertParseCorrect("savruldu", "savr(savurmak)+Verb+Verb+Pass(+nIl[ul])+Pos+Past(dI[du])+A3sg");
        assertParseCorrect("kavruldu", "kavr(kavurmak)+Verb+Verb+Pass(+nIl[ul])+Pos+Past(dI[du])+A3sg");
        assertParseCorrect("sıyrılıyor", "sıyr(sıyırmak)+Verb+Verb+Pass(+nIl[ıl])+Pos+Prog(Iyor[ıyor])+A3sg");
    }

    @Test
    public void shouldParseCoguBircogu() {
        assertParseCorrect("çoğu", "çoğu(çoğu)+Adj", "çoğu(çoğu)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("çoğumuz", "çoğu(çoğu)+Pron+A3sg+P1pl(muz[muz])+Nom");
        assertParseCorrect("çoğunun", "çoğu(çoğu)+Pron+A3sg+P3sg+Gen(+nIn[nun])");
        assertParseCorrect("çoğumuzdan", "çoğu(çoğu)+Pron+A3sg+P1pl(muz[muz])+Abl(dAn[dan])");
        assertParseCorrect("birçoğu", "birçoğu(birçoğu)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("birçoğumuz", "birçoğu(birçoğu)+Pron+A3sg+P1pl(muz[muz])+Nom");
        assertParseCorrect("birçoğunun", "birçoğu(birçoğu)+Pron+A3sg+P3sg+Gen(+nIn[nun])");
        assertParseCorrect("birçoğumuzdan", "birçoğu(birçoğu)+Pron+A3sg+P1pl(muz[muz])+Abl(dAn[dan])");
        assertNotParsable("çoğum");
        assertNotParsable("çoğun");
        assertNotParsable("birçoğum");
        assertNotParsable("birçoğun");
    }

    @Test
    public void shouldParsePronounsWithImplicitPossession() {
        assertParseCorrect("bazıları", "bazıları(bazıları)+Pron+A3sg+P3sg+Nom");
        assertParseDoesntExist("bazıları", "bazıları(bazıları)+Pron+A3sg+Pnon+Nom");
        assertParseCorrect("bazılarına", "bazıları(bazıları)+Pron+A3sg+P3sg+Dat(nA[na])");
        assertParseCorrect("bazılarımız", "bazıları(bazıları)+Pron+A3sg+P1pl(mız[mız])+Nom");
        assertParseCorrect("bazılarının", "bazıları(bazıları)+Pron+A3sg+P3sg+Gen(+nIn[nın])");
        assertParseCorrect("bazısı", "bazısı(bazısı)+Pron+A3sg+P3sg+Nom");

        assertParseCorrect("kimileri", "kimileri(kimileri)+Pron+A3sg+P3sg+Nom");
        assertParseDoesntExist("kimileri", "kimileri(kimileri)+Pron+A3sg+Pnon+Nom");
        assertParseCorrect("kimilerimiz", "kimileri(kimileri)+Pron+A3sg+P1pl(miz[miz])+Nom");
        assertParseCorrect("kimileriniz", "kimileri(kimileri)+Pron+A3sg+P2pl(niz[niz])+Nom");
        assertParseCorrect("kimilerinin", "kimileri(kimileri)+Pron+A3sg+P3sg+Gen(+nIn[nin])");
        assertParseCorrect("kimisi", "kimisi(kimisi)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("kimisini", "kimisi(kimisi)+Pron+A3sg+P3sg+Acc(nI[ni])");
        assertParseCorrect("kimisinin", "kimisi(kimisi)+Pron+A3sg+P3sg+Gen(+nIn[nin])");
        assertParseCorrect("kimi", "kimi(kimi)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("kimimiz", "kimi(kimi)+Pron+A3sg+P1pl(miz[miz])+Nom", "kim(kim)+Pron+Ques+A3sg+P1pl(+ImIz[imiz])+Nom");
        assertParseCorrect("kiminiz", "kimi(kimi)+Pron+A3sg+P2pl(niz[niz])+Nom", "kim(kim)+Pron+Ques+A3sg+P2pl(+InIz[iniz])+Nom");

        assertParseCorrect("birileri", "birileri(birileri)+Pron+A3sg+P3sg+Nom");
        assertParseDoesntExist("birileri", "birileri(birileri)+Pron+A3sg+Pnon+Nom");
        assertParseCorrect("birilerimiz", "birileri(birileri)+Pron+A3sg+P1pl(miz[miz])+Nom");
        assertParseCorrect("birileriniz", "birileri(birileri)+Pron+A3sg+P2pl(niz[niz])+Nom");
        assertParseCorrect("birilerinin", "birileri(birileri)+Pron+A3sg+P3sg+Gen(+nIn[nin])");
        assertParseCorrect("birisi", "birisi(birisi)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("birisinde", "birisi(birisi)+Pron+A3sg+P3sg+Loc(ndA[nde])");
        assertParseCorrect("birisinin", "birisi(birisi)+Pron+A3sg+P3sg+Gen(+nIn[nin])");
        assertParseCorrect("biri", "biri(biri)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("birimiz", "biri(biri)+Pron+A3sg+P1pl(miz[miz])+Nom");
        assertParseCorrect("biriniz", "biri(biri)+Pron+A3sg+P2pl(niz[niz])+Nom");

        assertParseCorrect("hiçbirisi", "hiçbirisi(hiçbirisi)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("hiçbirisinde", "hiçbirisi(hiçbirisi)+Pron+A3sg+P3sg+Loc(ndA[nde])");
        assertParseCorrect("hiçbirisinin", "hiçbirisi(hiçbirisi)+Pron+A3sg+P3sg+Gen(+nIn[nin])");
        assertParseCorrect("hiçbiri", "hiçbiri(hiçbiri)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("hiçbirimiz", "hiçbiri(hiçbiri)+Pron+A3sg+P1pl(miz[miz])+Nom");
        assertParseCorrect("hiçbiriniz", "hiçbiri(hiçbiri)+Pron+A3sg+P2pl(niz[niz])+Nom");

        assertParseCorrect("birbiri", "birbiri(birbiri)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("birbirine", "birbiri(birbiri)+Pron+A3sg+P3sg+Dat(nA[ne])");
        assertParseCorrect("birbirinden", "birbiri(birbiri)+Pron+A3sg+P3sg+Abl(ndAn[nden])");
        assertParseCorrect("birbirimiz", "birbiri(birbiri)+Pron+A1pl+P1pl(miz[miz])+Nom");
        assertParseCorrect("birbiriniz", "birbiri(birbiri)+Pron+A2pl+P2pl(niz[niz])+Nom");
        assertParseCorrect("birbirinize", "birbiri(birbiri)+Pron+A2pl+P2pl(niz[niz])+Dat(+yA[e])");
        assertParseCorrect("birbirleri", "birbir(birbiri)+Pron+A3pl+P3pl(leri[leri])+Nom");
        assertParseCorrect("birbirlerine", "birbir(birbiri)+Pron+A3pl+P3pl(leri[leri])+Dat(nA[ne])");

        assertParseCorrect("çoğu", "çoğu(çoğu)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("çoğumuz", "çoğu(çoğu)+Pron+A3sg+P1pl(muz[muz])+Nom");
        assertParseCorrect("çoğunuz", "çoğu(çoğu)+Pron+A3sg+P2pl(nuz[nuz])+Nom");
        assertParseCorrect("birçoğu", "birçoğu(birçoğu)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("birçoğumuz", "birçoğu(birçoğu)+Pron+A3sg+P1pl(muz[muz])+Nom");
        assertParseCorrect("birçoğunuz", "birçoğu(birçoğu)+Pron+A3sg+P2pl(nuz[nuz])+Nom");
        assertParseCorrect("çokları", "çokları(çokları)+Pron+A3sg+P3pl+Nom");
        assertParseCorrect("birçokları", "birçokları(birçokları)+Pron+A3sg+P3pl+Nom");

        assertParseCorrect("birkaçı", "birkaçı(birkaçı)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("birkaçımız", "birkaçı(birkaçı)+Pron+A3sg+P1pl(mız[mız])+Nom");
        assertParseCorrect("birkaçınız", "birkaçı(birkaçı)+Pron+A3sg+P2pl(nız[nız])+Nom");

        assertParseCorrect("cümlesi", "cümlesi(cümlesi)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("cümlesine", "cümlesi(cümlesi)+Pron+A3sg+P3sg+Dat(nA[ne])");

        assertParseCorrect("diğeri", "diğeri(diğeri)+Pron+A3sg+P3sg+Nom");
        assertParseCorrect("diğerinde", "diğeri(diğeri)+Pron+A3sg+P3sg+Loc(ndA[nde])");
        assertParseCorrect("diğerimize", "diğeri(diğeri)+Pron+A3sg+P1pl(miz[miz])+Dat(+yA[e])");
        assertParseCorrect("diğerinizin", "diğeri(diğeri)+Pron+A3sg+P2pl(niz[niz])+Gen(+nIn[in])");
        assertParseCorrect("diğerleri", "diğerleri(diğerleri)+Pron+A3sg+P3pl+Nom");
        assertParseCorrect("diğerlerini", "diğerleri(diğerleri)+Pron+A3sg+P3pl+Acc(nI[ni])");
        assertParseCorrect("diğerlerimizle", "diğerleri(diğerleri)+Pron+A3sg+P1pl(miz[miz])+Ins(+ylA[le])");
        assertParseCorrect("diğerlerinize", "diğerleri(diğerleri)+Pron+A3sg+P2pl(niz[niz])+Dat(+yA[e])");
    }

    @Test
    public void shouldParseIrregularPronouns() {
        assertParseCorrect("herkes", "herkes(herkes)+Pron+A3sg+Pnon+Nom");
        assertParseCorrect("herkese", "herkes(herkes)+Pron+A3sg+Pnon+Dat(+yA[e])");
        assertParseCorrect("herkesin", "herkes(herkes)+Pron+A3sg+Pnon+Gen(+nIn[in])");
        assertParseCorrect("herkeste", "herkes(herkes)+Pron+A3sg+Pnon+Loc(dA[te])");
        assertParseCorrect("herkesle", "herkes(herkes)+Pron+A3sg+Pnon+Ins(+ylA[le])");
        assertNotParsable("herkesim");
    }

    @Test
    public void shouldParseLIKSuffixes() {
        assertParseCorrect("güzellik", "güzel(güzel)+Adj+Noun+Ness(lIk[lik])+A3sg+Pnon+Nom");
        assertParseCorrect("ustalık", "usta(usta)+Noun+A3sg+Pnon+Nom+Noun+Prof(lIk[lık])+A3sg+Pnon+Nom");
        assertParseCorrect("kitaplık", "kitap(kitap)+Noun+A3sg+Pnon+Nom+Noun+FitFor(lIk[lık])+A3sg+Pnon+Nom");
        assertParseCorrect("çamlık", "çam(çam)+Noun+A3sg+Pnon+Nom+Adj+Y(lIk[lık])");
        assertParseCorrect("kiralık", "kira(kira)+Noun+A3sg+Pnon+Nom+Adj+For(lIk[lık])");
        assertParseCorrect("savcılık", "savcı(savcı)+Noun+A3sg+Pnon+Nom+Noun+Title(lIk[lık])+A3sg+Pnon+Nom");
        assertParseCorrect("yıllık", "yıl(yıl)+Noun+Time+A3sg+Pnon+Nom+Adj+DurationOf(lIk[lık])");
        assertParseCorrect("dolarlık", "dolar(dolar)+Noun+A3sg+Pnon+Nom+Adj+OfUnit(lIk[lık])");
    }

    @Test
    public void shouldParseRelativePronouns() {
        removeRoots("on", "se", "bizle");
        removeRootsExceptTheOneWithPrimaryPos("biz", PrimaryPos.Pronoun);

        assertParseCorrect("masamınki", "masa(masa)+Noun+A3sg+P1sg(+Im[m])+Gen(+nIn[ın])+Pron+A3sg(ki[ki])+Pnon+Nom");
        assertParseCorrect("masanınki", "masa(masa)+Noun+A3sg+Pnon+Gen(+nIn[nın])+Pron+A3sg(ki[ki])+Pnon+Nom", "masa(masa)+Noun+A3sg+P2sg(+In[n])+Gen(+nIn[ın])+Pron+A3sg(ki[ki])+Pnon+Nom");
        assertParseCorrect("masamınkiler", "masa(masa)+Noun+A3sg+P1sg(+Im[m])+Gen(+nIn[ın])+Pron+A3pl(kiler[kiler])+Pnon+Nom");
        assertParseCorrect("masalarınınkiler",
                "masa(masa)+Noun+A3pl(lAr[lar])+P2sg(+In[ın])+Gen(+nIn[ın])+Pron+A3pl(kiler[kiler])+Pnon+Nom",
                "masa(masa)+Noun+A3pl(lAr[lar])+P3sp(!I[ı])+Gen(+nIn[nın])+Pron+A3pl(kiler[kiler])+Pnon+Nom"
        );

        assertParseCorrect("masamınkine", "masa(masa)+Noun+A3sg+P1sg(+Im[m])+Gen(+nIn[ın])+Pron+A3sg(ki[ki])+Pnon+Dat(nA[ne])");
        assertParseCorrect("masanınkini", "masa(masa)+Noun+A3sg+Pnon+Gen(+nIn[nın])+Pron+A3sg(ki[ki])+Pnon+Acc(nI[ni])", "masa(masa)+Noun+A3sg+P2sg(+In[n])+Gen(+nIn[ın])+Pron+A3sg(ki[ki])+Pnon+Acc(nI[ni])");
        assertParseCorrect("masamınkinde", "masa(masa)+Noun+A3sg+P1sg(+Im[m])+Gen(+nIn[ın])+Pron+A3sg(ki[ki])+Pnon+Loc(ndA[nde])");
        assertParseCorrect("masalarınınkilere",
                "masa(masa)+Noun+A3pl(lAr[lar])+P2sg(+In[ın])+Gen(+nIn[ın])+Pron+A3pl(kiler[kiler])+Pnon+Dat(+yA[e])",
                "masa(masa)+Noun+A3pl(lAr[lar])+P3sp(!I[ı])+Gen(+nIn[nın])+Pron+A3pl(kiler[kiler])+Pnon+Dat(+yA[e])"
        );

        assertParseCorrect("benimki", "ben(ben)+Pron+Pers+A1sg+Pnon+Gen(im[im])+Pron+A3sg(ki[ki])+Pnon+Nom");
        assertParseCorrect("seninki", "sen(sen)+Pron+Pers+A2sg+Pnon+Gen(in[in])+Pron+A3sg(ki[ki])+Pnon+Nom");
        assertParseCorrect("onunki", "o(o)+Pron+A3sg+Pnon+Gen(nun[nun])+Pron+A3sg(ki[ki])+Pnon+Nom");
        assertParseCorrect("onlarınkiler", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Gen(ın[ın])+Pron+A3pl(kiler[kiler])+Pnon+Nom");
        assertParseCorrect("bizlerinkilerin", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Gen(in[in])+Pron+A3pl(kiler[kiler])+Pnon+Gen(+nIn[in])");
        assertParseCorrect("bizlerinkilerinki", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Gen(in[in])+Pron+A3pl(kiler[kiler])+Pnon+Gen(+nIn[in])+Pron+A3sg(ki[ki])+Pnon+Nom");

        assertParseCorrect("benimkine", "ben(ben)+Pron+Pers+A1sg+Pnon+Gen(im[im])+Pron+A3sg(ki[ki])+Pnon+Dat(nA[ne])");
        assertParseCorrect("seninkini", "sen(sen)+Pron+Pers+A2sg+Pnon+Gen(in[in])+Pron+A3sg(ki[ki])+Pnon+Acc(nI[ni])");
        assertParseCorrect("onunkinden", "o(o)+Pron+A3sg+Pnon+Gen(nun[nun])+Pron+A3sg(ki[ki])+Pnon+Abl(ndAn[nden])");
        assertParseCorrect("onlarınkilerinkini", "o(o)+Pron+A3pl(nlar[nlar])+Pnon+Gen(ın[ın])+Pron+A3pl(kiler[kiler])+Pnon+Gen(+nIn[in])+Pron+A3sg(ki[ki])+Pnon+Acc(nI[ni])");
        assertParseCorrect("bizlerinkilerde", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Gen(in[in])+Pron+A3pl(kiler[kiler])+Pnon+Loc(dA[de])");
        assertParseCorrect("bizlerinkilere", "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Gen(in[in])+Pron+A3pl(kiler[kiler])+Pnon+Dat(+yA[e])");

        assertParseCorrect("nereninki", "nere(nere)+Pron+Ques+A3sg+Pnon+Gen(+nIn[nin])+Pron+A3sg(ki[ki])+Pnon+Nom", "nere(nere)+Pron+Ques+A3sg+P2sg(+In[n])+Gen(+nIn[in])+Pron+A3sg(ki[ki])+Pnon+Nom");
        assertParseCorrect("buramınki", "bura(bura)+Pron+A3sg+P1sg(+Im[m])+Gen(+nIn[ın])+Pron+A3sg(ki[ki])+Pnon+Nom");
        assertParseCorrect("nerelerimizinkiler", "nere(nere)+Pron+Ques+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Gen(+nIn[in])+Pron+A3pl(kiler[kiler])+Pnon+Nom");
        assertParseCorrect("nerelerimizinkilerin", "nere(nere)+Pron+Ques+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Gen(+nIn[in])+Pron+A3pl(kiler[kiler])+Pnon+Gen(+nIn[in])");
        assertParseCorrect("nerelerimizinkilerde", "nere(nere)+Pron+Ques+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Gen(+nIn[in])+Pron+A3pl(kiler[kiler])+Pnon+Loc(dA[de])");

        assertNotParsable("masamınkin");
        assertNotParsable("benimkilerim");
        assertNotParsable("nereninkisi");

        assertParseCorrect("berikini", "xxx");      // "berikini getir"
        assertParseCorrect("berikisi", "xxx");      // "berikisi bizim ev"
        assertParseCorrect("berikiyi", "xxx");      // "berikiyi getir"
        assertParseCorrect("ötekin", "xxx");        // "senin ötekin nereder"
        assertParseCorrect("ötekisi", "xxx");
        assertParseCorrect("öbürkü", "xxx");
        assertParseCorrect("öbürkün", "xxx");
        assertParseCorrect("öbürküsü", "xxx");
    }

    @Test
    public void shouldParseConjunctions() {
        assertParseCorrect("ama", "ama(ama)+Conj");
        assertParseCorrect("Ama", "ama(ama)+Conj");
    }

    @Test
    public void shouldParsePostpositives() {
        assertParseCorrect("kadar", "kadar(kadar)+Postp");
        assertParseCorrect("Kadar", "kadar(kadar)+Postp");
    }

    @Test
    public void shouldParseSomeWordsWithCircumflexes() {
        assertParseCorrect("rüzgâr", "rüzgâr(rüzgâr)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("rüzgârımı", "rüzgâr(rüzgâr)+Noun+A3sg+P1sg(+Im[ım])+Acc(+yI[ı])");
        // TODO: corpus'a bak Become eki icin. hic isim+les var mi? Mesela kapilasmak ? Sadece isim mi yoksa?
        assertParseCorrect("alenîleşmek", "alenî(alenî)+Adj+Verb+Become(lAş[leş])+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom", "alenî(alenî)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Become(lAş[leş])+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom");
        assertParseCorrect("cülûsten", "cülûs(cülûs)+Noun+A3sg+Pnon+Abl(dAn[ten])");
        assertParseCorrect("hâlâ", "hâlâ(hâlâ)+Adv+Time");
    }

    @Test
    public void shouldParseSomeWordsWithCircumflexesWithNoCircumflexForms() {
        assertParseCorrect("rüzgar", "rüzgar(rüzgâr)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("rüzgarımı", "rüzgar(rüzgâr)+Noun+A3sg+P1sg(+Im[ım])+Acc(+yI[ı])");
        assertParseCorrect("alenileşmek", "aleni(alenî)+Adj+Verb+Become(lAş[leş])+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom", "aleni(alenî)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Become(lAş[leş])+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Nom");
        assertParseCorrect("cülusten", "cülus(cülûs)+Noun+A3sg+Pnon+Abl(dAn[ten])");
        assertParseCorrect("hala", "hala(hâlâ)+Adv+Time", "hala(hala)+Noun+A3sg+Pnon+Nom", "hal(hal)+Noun+A3sg+Pnon+Dat(+yA[a])");
    }

    @Test
    public void shouldParseVerbsWithA3PlSwap() {
        //Past+Past NOT SUPPORTED
        //assertParseCorrect("bekledilerdi", "");
        //assertParseCorrect("beklediydiler", "");

        //Cond+Past NOT APPLICABLE, becomes Desr
        //assertParseCorrect("bekleseydiler", "");
        //assertParseCorrect("bekleselerdi", "");

        //Neces+Past
        assertParseCorrect("beklemelilerdi", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+A3pl(lAr[ler])+Past(dI[di])");
        assertParseCorrect("beklemeliydiler", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+Past(ydI[ydi])+A3pl(lAr[ler])");

        //Opt+Past
        assertParseCorrect("bekleyelerdi", "bekle(beklemek)+Verb+Pos+Opt(yA[ye])+A3pl(lAr[ler])+Past(dI[di])");
        assertParseCorrect("bekleyeydiler", "bekle(beklemek)+Verb+Pos+Opt(yA[ye])+Past(ydI[ydi])+A3pl(lAr[ler])");

        //Desr+Past
        assertParseCorrect("bekleselerdi", "bekle(beklemek)+Verb+Pos+Desr(sA[se])+A3pl(lAr[ler])+Past(dI[di])");
        assertParseCorrect("bekleseydiler", "bekle(beklemek)+Verb+Pos+Desr(sA[se])+Past(ydI[ydi])+A3pl(lAr[ler])");

        //Aor+Past
        assertParseCorrect("beklerlerdi", "bekle(beklemek)+Verb+Pos+Aor(r[r])+A3pl(lAr[ler])+Past(dI[di])");
        assertParseCorrect("beklerdiler", "bekle(beklemek)+Verb+Pos+Aor(r[r])+Past(dI[di])+A3pl(lAr[ler])");

        //Prog+Past
        assertParseCorrect("bekliyorlardı", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+A3pl(lAr[lar])+Past(dI[dı])");
        assertParseCorrect("bekliyordular", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+Past(dI[du])+A3pl(lAr[lar])");

        //Fut+Past
        assertParseCorrect("bekleyeceklerdi", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+A3pl(lAr[ler])+Past(dI[di])");
        assertParseCorrect("bekleyecektiler", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+Past(dI[ti])+A3pl(lAr[ler])");

        //Narr+Past
        assertParseCorrect("beklemişlerdi", "bekle(beklemek)+Verb+Pos+Narr(mIş[miş])+A3pl(lAr[ler])+Past(dI[di])");
        assertParseCorrect("beklemiştiler", "bekle(beklemek)+Verb+Pos+Narr(mIş[miş])+Past(dI[ti])+A3pl(lAr[ler])");


        //Past+Narr NOT APPLICABLE
        //assertParseCorrect("bekledilermiş", "");
        //assertParseCorrect("beklediymişler", "");

        //Cond+Narr NOT APPLICABLE, becomes Desr
        //assertParseCorrect("bekleseymişler", "");
        //assertParseCorrect("bekleselermiş", "");

        //Neces+Narr
        assertParseCorrect("beklemelilermiş", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+A3pl(lAr[ler])+Narr(mIş[miş])");
        assertParseCorrect("beklemeliymişler", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+Narr(ymIş[ymiş])+A3pl(lAr[ler])");

        //Opt+Narr
        assertParseCorrect("bekleyelermiş", "bekle(beklemek)+Verb+Pos+Opt(yA[ye])+A3pl(lAr[ler])+Narr(mIş[miş])");
        assertParseCorrect("bekleyeymişler", "bekle(beklemek)+Verb+Pos+Opt(yA[ye])+Narr(ymIş[ymiş])+A3pl(lAr[ler])");

        //Desr+Narr
        assertParseCorrect("bekleselermiş", "bekle(beklemek)+Verb+Pos+Desr(sA[se])+A3pl(lAr[ler])+Narr(mIş[miş])");
        assertParseCorrect("bekleseymişler", "bekle(beklemek)+Verb+Pos+Desr(sA[se])+Narr(ymIş[ymiş])+A3pl(lAr[ler])");

        //Aor+Narr
        assertParseCorrect("beklerlermiş", "bekle(beklemek)+Verb+Pos+Aor(r[r])+A3pl(lAr[ler])+Narr(mIş[miş])");
        assertParseCorrect("beklermişler", "bekle(beklemek)+Verb+Pos+Aor(r[r])+Narr(mIş[miş])+A3pl(lAr[ler])");

        //Prog+Narr
        assertParseCorrect("bekliyorlarmış", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+A3pl(lAr[lar])+Narr(mIş[mış])");
        assertParseCorrect("bekliyormuşlar", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+Narr(mIş[muş])+A3pl(lAr[lar])");

        //Fut+Narr
        assertParseCorrect("bekleyeceklermiş", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+A3pl(lAr[ler])+Narr(mIş[miş])");
        assertParseCorrect("bekleyecekmişler", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+Narr(mIş[miş])+A3pl(lAr[ler])");

        //Narr+Narr  NOT SUPPORTED
        //assertParseCorrect("beklemişlermiş", "");
        //assertParseCorrect("beklemişmişler", "");


        //Past+Cond
        assertParseCorrect("bekledilerse", "bekle(beklemek)+Verb+Pos+Past(dI[di])+A3pl(lAr[ler])+Cond(+ysA[se])");
        assertParseCorrect("beklediyseler", "bekle(beklemek)+Verb+Pos+Past(dI[di])+Cond(+ysA[yse])+A3pl(lAr[ler])");

        //Cond+Cond NOT APPLICABLE
        //assertParseCorrect("bekleseyseler", "");
        //assertParseCorrect("bekleselerse", "");

        //Neces+Cond
        assertParseCorrect("beklemelilerse", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+A3pl(lAr[ler])+Cond(+ysA[se])");
        assertParseCorrect("beklemeliyseler", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+Cond(+ysA[yse])+A3pl(lAr[ler])");

        //Opt+Cond NOT APPLICABLE
        //assertParseCorrect("bekleyelerse", "");
        //assertParseCorrect("bekleyeyseler", "");

        //Desr+Cond NOT APPLICABLE
        //assertParseCorrect("bekleselermiş", "");
        //assertParseCorrect("bekleseymişler", "");

        //Aor+Cond
        assertParseCorrect("beklerlerse", "bekle(beklemek)+Verb+Pos+Aor(r[r])+A3pl(lAr[ler])+Cond(+ysA[se])");
        assertParseCorrect("beklerseler", "bekle(beklemek)+Verb+Pos+Aor(r[r])+Cond(+ysA[se])+A3pl(lAr[ler])");

        //Prog+Cond
        assertParseCorrect("bekliyorlarsa", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+A3pl(lAr[lar])+Cond(+ysA[sa])");
        assertParseCorrect("bekliyorsalar", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+Cond(+ysA[sa])+A3pl(lAr[lar])");

        //Fut+Cond
        assertParseCorrect("bekleyeceklerse", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+A3pl(lAr[ler])+Cond(+ysA[se])");
        assertParseCorrect("bekleyecekseler", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+Cond(+ysA[se])+A3pl(lAr[ler])");

        //Narr+Cond
        assertParseCorrect("beklemişlerse", "bekle(beklemek)+Verb+Pos+Narr(mIş[miş])+A3pl(lAr[ler])+Cond(+ysA[se])");
        assertParseCorrect("beklemişseler", "bekle(beklemek)+Verb+Pos+Narr(mIş[miş])+Cond(+ysA[se])+A3pl(lAr[ler])");
    }

    @Test
    public void shouldParseSwappedCondPast() {
        assertParseCorrect("geldiysen", "gel(gelmek)+Verb+Pos+Past(dI[di])+Cond(+ysA[yse])+A2sg(n[n])");

        assertParseCorrect("geldimse", "gel(gelmek)+Verb+Pos+Past(dI[di])+A1sg(m[m])+Cond(+ysA[se])");
        assertParseCorrect("geldinse", "gel(gelmek)+Verb+Pos+Past(dI[di])+A2sg(n[n])+Cond(+ysA[se])");
        assertParseCorrect("geldikse", "gel(gelmek)+Verb+Pos+Past(dI[di])+A1pl(!k[k])+Cond(+ysA[se])");
        assertParseCorrect("geldinizse", "gel(gelmek)+Verb+Pos+Past(dI[di])+A2pl(nIz[niz])+Cond(+ysA[se])");
        assertParseCorrect("geldilerse", "gel(gelmek)+Verb+Pos+Past(dI[di])+A3pl(lAr[ler])+Cond(+ysA[se])");
    }

    @Test
    public void shouldParseSurfacesWithRootsEndingWithArabicAyn() {
        removeRootsExceptTheOneWithPrimaryPos("cami", PrimaryPos.Noun);

        assertParseCorrect("camisi", "cami(cami)+Noun+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrect("camii", "cami(cami)+Noun+A3sg+P3sg(+sI[i])+Nom", "cami(cami)+Noun+A3sg+Pnon+Acc(+yI[i])");

        assertParseCorrect("sanayisi", "sanayi(sanayi)+Noun+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrect("sanayii", "sanayi(sanayi)+Noun+A3sg+P3sg(+sI[i])+Nom", "sanayi(sanayi)+Noun+A3sg+Pnon+Acc(+yI[i])");

        assertParseCorrect("mevzusu", "mevzu(mevzu)+Noun+A3sg+P3sg(+sI[su])+Nom");
        assertParseCorrect("mevzuu", "mevzu(mevzu)+Noun+A3sg+P3sg(+sI[u])+Nom", "mevzu(mevzu)+Noun+A3sg+Pnon+Acc(+yI[u])");
    }

    @Test
    public void shouldParseWordsWithSuffix_related() {
        assertParseCorrect("tarihsel", "tarih(tarih)+Noun+A3sg+Pnon+Nom+Adj+Related(sAl[sel])");
        assertParseCorrect("duygusal", "duygu(duygu)+Noun+A3sg+Pnon+Nom+Adj+Related(sAl[sal])");
    }

    @Test
    public void shouldParseAuxVerbs() {
        removeRoots("çal");

        assertParseCorrect("geliverdi", "gel(gelmek)+Verb+Pos+Verb+Hastily(+yIver[iver])+Pos+Past(dI[di])+A3sg");
        assertParseCorrect("gelivermedi", "gel(gelmek)+Verb+Pos+Verb+Hastily(+yIver[iver])+Neg(mA[me])+Past(dI[di])+A3sg");
        assertParseCorrect("gelmeyiverdi", "gel(gelmek)+Verb+Neg(mA[me])+Verb+Hastily(+yIver[yiver])+Pos+Past(dI[di])+A3sg");
        assertParseCorrect("gelmeyivermedi", "gel(gelmek)+Verb+Neg(mA[me])+Verb+Hastily(+yIver[yiver])+Neg(mA[me])+Past(dI[di])+A3sg");

        assertParseCorrect("olageldi", "ol(olmak)+Verb+Pos+Verb+EverSince(+yAgel[agel])+Pos+Past(dI[di])+A3sg");
        assertParseCorrect("olagelmedi", "ol(olmak)+Verb+Pos+Verb+EverSince(+yAgel[agel])+Neg(mA[me])+Past(dI[di])+A3sg");
        assertParseCorrect("olmayageldi", "ol(olmak)+Verb+Neg(mA[ma])+Verb+EverSince(+yAgel[yagel])+Pos+Past(dI[di])+A3sg");
        assertParseCorrect("olmayagelmedi", "ol(olmak)+Verb+Neg(mA[ma])+Verb+EverSince(+yAgel[yagel])+Neg(mA[me])+Past(dI[di])+A3sg");

        assertParseCorrect("bakakalır", "bak(bakmak)+Verb+Pos+Verb+Stay(+yAkal[akal])+Pos+Aor(+Ir[ır])+A3sg", "bak(bakmak)+Verb+Pos+Verb+Stay(+yAkal[akal])+Pos+Aor(+Ir[ır])+Adj+Zero");
        assertParseDoesntExist("bakakalır", "bak(bakmak)+Verb+Pos+Verb+Stay(+yAkal[akal])+Verb+Caus(Ir[ır])+Pos+Imp+A2sg");      // correct causative form is bakakaldir
        assertParseCorrect("bakakalmaz", "bak(bakmak)+Verb+Pos+Verb+Stay(+yAkal[akal])+Neg(mA[ma])+Aor(z[z])+A3sg", "bak(bakmak)+Verb+Pos+Verb+Stay(+yAkal[akal])+Neg(mA[ma])+Aor(z[z])+Adj+Zero");
        assertParseCorrect("bakmayakalır", "bak(bakmak)+Verb+Neg(mA[ma])+Verb+Stay(+yAkal[yakal])+Pos+Aor(+Ir[ır])+A3sg", "bak(bakmak)+Verb+Neg(mA[ma])+Verb+Stay(+yAkal[yakal])+Pos+Aor(+Ir[ır])+Adj+Zero");
        assertParseCorrect("bakmayakalmaz", "bak(bakmak)+Verb+Neg(mA[ma])+Verb+Stay(+yAkal[yakal])+Neg(mA[ma])+Aor(z[z])+A3sg", "bak(bakmak)+Verb+Neg(mA[ma])+Verb+Stay(+yAkal[yakal])+Neg(mA[ma])+Aor(z[z])+Adj+Zero");

        assertParseCorrect("düşeyazdı", "düş(düşmek)+Verb+Pos+Verb+Almost(+yAyaz[eyaz])+Pos+Past(dI[dı])+A3sg");
        assertParseCorrect("düşeyazmadı", "düş(düşmek)+Verb+Pos+Verb+Almost(+yAyaz[eyaz])+Neg(mA[ma])+Past(dI[dı])+A3sg");
        assertParseCorrect("düşmeyeyazdı", "düş(düşmek)+Verb+Neg(mA[me])+Verb+Almost(+yAyaz[yeyaz])+Pos+Past(dI[dı])+A3sg");
        assertParseCorrect("düşmeyeyazmadı", "düş(düşmek)+Verb+Neg(mA[me])+Verb+Almost(+yAyaz[yeyaz])+Neg(mA[ma])+Past(dI[dı])+A3sg");

        assertParseCorrect("yapagörsün", "yap(yapmak)+Verb+Pos+Verb+Once(+yAgör[agör])+Pos+Imp+A3sg(sIn[sün])");
        assertParseCorrect("yapagörmesin", "yap(yapmak)+Verb+Pos+Verb+Once(+yAgör[agör])+Neg(mA[me])+Imp+A3sg(sIn[sin])");
        assertParseCorrect("yapmayagörsün", "yap(yapmak)+Verb+Neg(mA[ma])+Verb+Once(+yAgör[yagör])+Pos+Imp+A3sg(sIn[sün])");
        assertParseCorrect("yapmayagörmesin", "yap(yapmak)+Verb+Neg(mA[ma])+Verb+Once(+yAgör[yagör])+Neg(mA[me])+Imp+A3sg(sIn[sin])");

        assertParseCorrect("yapılagidecekti", "yap(yapmak)+Verb+Verb+Pass(+nIl[ıl])+Pos+Verb+Gone(+yAgid[agid])+Pos+Fut(+yAcAk[ecek])+Past(dI[ti])+A3sg");
        assertParseCorrect("yapılagitmeyecekti", "yap(yapmak)+Verb+Verb+Pass(+nIl[ıl])+Pos+Verb+Gone(+yAgi!t[agit])+Neg(mA[me])+Fut(+yAcAk[yecek])+Past(dI[ti])+A3sg");
        assertParseCorrect("yapılmayagidecekti", "yap(yapmak)+Verb+Verb+Pass(+nIl[ıl])+Neg(mA[ma])+Verb+Gone(+yAgid[yagid])+Pos+Fut(+yAcAk[ecek])+Past(dI[ti])+A3sg");
        assertParseCorrect("yapılmayagitmeyecekti", "yap(yapmak)+Verb+Verb+Pass(+nIl[ıl])+Neg(mA[ma])+Verb+Gone(+yAgi!t[yagit])+Neg(mA[me])+Fut(+yAcAk[yecek])+Past(dI[ti])+A3sg");

        assertParseCorrect("çalışakoy", "çalış(çalışmak)+Verb+Pos+Verb+Start(+yAkoy[akoy])+Pos+Imp+A2sg");
        assertParseCorrect("çalışakoyma", "çalış(çalışmak)+Verb+Pos+Verb+Start(+yAkoy[akoy])+Neg(mA[ma])+Imp+A2sg", "çalış(çalışmak)+Verb+Pos+Verb+Start(+yAkoy[akoy])+Pos+Noun+Inf(mA[ma])+A3sg+Pnon+Nom");
        assertParseCorrect("çalışmayakoy", "çalış(çalışmak)+Verb+Neg(mA[ma])+Verb+Start(+yAkoy[yakoy])+Pos+Imp+A2sg");
        assertParseCorrect("çalışmayakoyma", "çalış(çalışmak)+Verb+Neg(mA[ma])+Verb+Start(+yAkoy[yakoy])+Neg(mA[ma])+Imp+A2sg", "çalış(çalışmak)+Verb+Neg(mA[ma])+Verb+Start(+yAkoy[yakoy])+Pos+Noun+Inf(mA[ma])+A3sg+Pnon+Nom");

    }

    @Test
    public void shouldParseSomeWordsWithCapitalStart() {
        assertParseCorrect("Sokak", "sokak(sokak)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("Is", "ıs(ıs)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("İs", "is(is)+Noun+A3sg+Pnon+Nom");
    }
}
