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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.PhoneticAttributeSets;
import org.trnltk.morphology.contextless.parser.SuffixFormGraph;
import org.trnltk.morphology.contextless.parser.SuffixFormGraphExtractor;
import org.trnltk.morphology.contextless.parser.parsing.base.BaseContextlessMorphologicParserTest;
import org.trnltk.morphology.contextless.rootfinder.DictionaryRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.rootfinder.RootValidator;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.reducedambiguity.BasicRASuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.Collection;
import java.util.List;

public class ContextlessMorphologicParserBasicSuffixGraphTest extends BaseContextlessMorphologicParserTest {

    private ContextlessMorphologicParser parser;
    private HashMultimap<String, ? extends Root> originalRootMap;

    public ContextlessMorphologicParserBasicSuffixGraphTest() {
        this.originalRootMap = RootMapFactory.createSimpleConvertCircumflexes();
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

        final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, clonedRootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPaths.initialize();

        this.parser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPaths, rootFinderChain, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

    private final ImmutableSet<String> NOT_ALLOWED_CASES = new ImmutableSet.Builder<String>()
            .add("A3sg+P3pl")       // e.g. sokaklari
            .build();

    @Override
    protected Collection<String> getFormattedParseResults(String surfaceToParse) {
        Collection<String> formattedParseResults = super.getFormattedParseResults(surfaceToParse);
        for (String formattedParseResult : formattedParseResults) {
            for (String notAllowedCase : NOT_ALLOWED_CASES) {
                if(formattedParseResult.contains(notAllowedCase)){
                    StringBuilder failMessageBuilder = new StringBuilder("A not allowed case is found!")
                            .append("\n Surface : ").append(surfaceToParse)
                            .append("\n Parse results : ");

                    for (String parseResult : formattedParseResults) {
                        failMessageBuilder.append("\n - ").append(parseResult);
                    }

                    Assert.fail(failMessageBuilder.toString());
                }
            }
        }

        return formattedParseResults;
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
        assertParseCorrect("sokakları", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "sokak(sokak)+Noun+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom", "sokak(sokak)+Noun+A3pl(lAr[lar])+P3pl(!I[ı])+Nom");
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
        assertParseCorrect("yapmakta", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+A3sg");
        assertParseCorrect("yapmaktayız", "yap(yapmak)+Verb+Pos+Prog(mAktA[makta])+A1pl(yIz[yız])");

        assertParseCorrect("yapacağım", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acağ])+A1sg(+Im[ım])", "yap(yapmak)+Verb+Pos+Adj+FutPart(+yAcAk[acağ])+P1sg(+Im[ım])");
        assertParseCorrect("yapacaksın", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+A2sg(sIn[sın])");
        assertParseCorrect("yapacak", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+A3sg", "yap(yapmak)+Verb+Pos+Adj+FutPart(+yAcAk[acak])+Pnon", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero", "yap(yapmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yapacağız", "yap(yapmak)+Verb+Pos+Fut(+yAcAk[acağ])+A1pl(+Iz[ız])");

        assertParseCorrect("yaptım", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A1sg(+Im[m])");
        assertParseCorrect("yaptın", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A2sg(n[n])");
        assertParseCorrect("yaptı", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A3sg");
        assertParseCorrect("yaptık", "yap(yapmak)+Verb+Pos+Past(dI[tı])+A1pl(!k[k])", "yap(yapmak)+Verb+Pos+Adj+PastPart(dIk[tık])+Pnon", "yap(yapmak)+Verb+Pos+Noun+PastPart(dIk[tık])+A3sg+Pnon+Nom");

        assertParseCorrect("yapmışım", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A1sg(+Im[ım])", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[ım])+Nom");
        assertParseCorrect("yapmışsın", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A2sg(sIn[sın])");
        assertParseCorrect("yapmış", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A3sg", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+Adj+Zero", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yapmışız", "yap(yapmak)+Verb+Pos+Narr(mIş[mış])+A1pl(+Iz[ız])");

        assertParseCorrect("çeviririm", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+A1sg(+Im[im])", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("çevirirsin", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+A2sg(sIn[sin])");
        assertParseCorrect("çevirir", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+A3sg", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+Adj+Zero", "çevir(çevirmek)+Verb+Pos+Aor(+Ir[ir])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("çeviriyorum", "çevir(çevirmek)+Verb+Pos+Prog(Iyor[iyor])+A1sg(+Im[um])");
        assertParseCorrect("çeviriyorsun", "çevir(çevirmek)+Verb+Pos+Prog(Iyor[iyor])+A2sg(sIn[sun])");
        assertParseCorrect("çeviriyor", "çevir(çevirmek)+Verb+Pos+Prog(Iyor[iyor])+A3sg");

        assertParseCorrect("çevirmekteyim", "çevir(çevirmek)+Verb+Pos+Prog(mAktA[mekte])+A1sg(yIm[yim])");
        assertParseCorrect("çevirmektesin", "çevir(çevirmek)+Verb+Pos+Prog(mAktA[mekte])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmekte", "çevir(çevirmek)+Verb+Pos+Prog(mAktA[mekte])+A3sg", "çevir(çevirmek)+Verb+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Loc(dA[te])");

        assertParseCorrect("çevireceğim", "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[eceğ])+A1sg(+Im[im])", "çevir(çevirmek)+Verb+Pos+Adj+FutPart(+yAcAk[eceğ])+P1sg(+Im[im])", "çevir(çevirmek)+Verb+Pos+Noun+FutPart(+yAcAk[eceğ])+A3sg+P1sg(+Im[im])+Nom", "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[eceğ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("çevireceksin", "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[ecek])+A2sg(sIn[sin])");
        assertParseCorrect("çevirecek", "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[ecek])+A3sg", "çevir(çevirmek)+Verb+Pos+Adj+FutPart(+yAcAk[ecek])+Pnon", "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[ecek])+Adj+Zero", "çevir(çevirmek)+Verb+Pos+Noun+FutPart(+yAcAk[ecek])+A3sg+Pnon+Nom", "çevir(çevirmek)+Verb+Pos+Fut(+yAcAk[ecek])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("çevirdim", "çevir(çevirmek)+Verb+Pos+Past(dI[di])+A1sg(+Im[m])");
        assertParseCorrect("çevirdin", "çevir(çevirmek)+Verb+Pos+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("çevirdi", "çevir(çevirmek)+Verb+Pos+Past(dI[di])+A3sg");

        assertParseCorrect("çevirmişim", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+A1sg(+Im[im])", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("çevirmişsin", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("çevirmiş", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+A3sg", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero", "çevir(çevirmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");


        removeRootsExceptTheOneWithPrimaryPos("el", PrimaryPos.Verb);

        assertParseCorrect("elerim", "ele(elemek)+Verb+Pos+Aor(+Ir[r])+A1sg(+Im[im])", "ele(elemek)+Verb+Pos+Aor(+Ar[r])+A1sg(+Im[im])", "ele(elemek)+Verb+Pos+Aor(+Ir[r])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom", "ele(elemek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("elersin", "ele(elemek)+Verb+Pos+Aor(+Ir[r])+A2sg(sIn[sin])", "ele(elemek)+Verb+Pos+Aor(+Ar[r])+A2sg(sIn[sin])");
        assertParseCorrect("eler", "ele(elemek)+Verb+Pos+Aor(+Ir[r])+A3sg", "ele(elemek)+Verb+Pos+Aor(+Ar[r])+A3sg", "ele(elemek)+Verb+Pos+Aor(+Ir[r])+Adj+Zero", "ele(elemek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero", "ele(elemek)+Verb+Pos+Aor(+Ir[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom", "ele(elemek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("eliyorum", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+A1sg(+Im[um])");
        assertParseCorrect("eliyorsun", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+A2sg(sIn[sun])");
        assertParseCorrect("eliyor", "el(elemek)+Verb+Pos+Prog(Iyor[iyor])+A3sg");

        assertParseCorrect("elemekteyim", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+A1sg(yIm[yim])");
        assertParseCorrect("elemektesin", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+A2sg(sIn[sin])");
        assertParseCorrect("elemekte", "ele(elemek)+Verb+Pos+Prog(mAktA[mekte])+A3sg", "ele(elemek)+Verb+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Loc(dA[te])");

        assertParseCorrect("eleyeceğim", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yeceğ])+A1sg(+Im[im])", "ele(elemek)+Verb+Pos+Adj+FutPart(+yAcAk[yeceğ])+P1sg(+Im[im])", "ele(elemek)+Verb+Pos+Noun+FutPart(+yAcAk[yeceğ])+A3sg+P1sg(+Im[im])+Nom", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yeceğ])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("eleyeceksin", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+A2sg(sIn[sin])");
        assertParseCorrect("eleyecek", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+A3sg", "ele(elemek)+Verb+Pos+Adj+FutPart(+yAcAk[yecek])+Pnon", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Adj+Zero", "ele(elemek)+Verb+Pos+Noun+FutPart(+yAcAk[yecek])+A3sg+Pnon+Nom", "ele(elemek)+Verb+Pos+Fut(+yAcAk[yecek])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("eledim", "ele(elemek)+Verb+Pos+Past(dI[di])+A1sg(+Im[m])"); // TODO: Wrong! "el-e-ydim" is also parsed with "el-e-dim" if dictionary item "el" wasn't removed
        assertParseCorrect("eledin", "ele(elemek)+Verb+Pos+Past(dI[di])+A2sg(n[n])");
        assertParseCorrect("eledi", "ele(elemek)+Verb+Pos+Past(dI[di])+A3sg");

        assertParseCorrect("elemişim", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+A1sg(+Im[im])", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+P1sg(+Im[im])+Nom");
        assertParseCorrect("elemişsin", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+A2sg(sIn[sin])");
        assertParseCorrect("elemiş", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+A3sg", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero", "ele(elemek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
    }
}
