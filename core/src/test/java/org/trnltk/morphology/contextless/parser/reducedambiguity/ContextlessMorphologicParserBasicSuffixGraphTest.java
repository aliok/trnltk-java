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
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.formbased.PhoneticAttributeSets;
import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraph;
import org.trnltk.morphology.contextless.parser.formbased.SuffixFormGraphExtractor;
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserTest;
import org.trnltk.morphology.contextless.rootfinder.DictionaryRootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.rootfinder.RootValidator;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.reducedambiguity.BasicRASuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

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


    @Test
    public void shouldParseNounCases() {
        assertParseCorrect("sokak", "sokak(sokak)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("dikkatle", "dikkat(dikkat)+Noun+A3sg+Pnon+Ins(+ylA[le])");

        assertParseCorrect("kapıyı", "kapı(kapı)+Noun+A3sg+Pnon+Acc(+yI[yı])");
        assertParseCorrect("kapıya", "kapı(kapı)+Noun+A3sg+Pnon+Dat(+yA[ya])");
        assertParseCorrect("kapıda", "kapı(kapı)+Noun+A3sg+Pnon+Loc(dA[da])");
        assertParseCorrect("kapıdan", "kapı(kapı)+Noun+A3sg+Pnon+Abl(dAn[dan])");
        assertParseCorrect("dayının", "dayı(dayı)+Noun+A3sg+Pnon+Gen(+nIn[nın])", "dayı(dayı)+Noun+A3sg+P2sg(+In[n])+Gen(+nIn[ın])", "dayı(dayı)+Adj+Noun+Zero+A3sg+Pnon+Gen(+nIn[nın])", "dayı(dayı)+Adj+Noun+Zero+A3sg+P2sg(+In[n])+Gen(+nIn[ın])");
        assertParseCorrect("sokağın", "sokağ(sokak)+Noun+A3sg+Pnon+Gen(+nIn[ın])", "sokağ(sokak)+Noun+A3sg+P2sg(+In[ın])+Nom");
        assertParseCorrect("sokakla", "sokak(sokak)+Noun+A3sg+Pnon+Ins(+ylA[la])");

        assertParseCorrect("sokaklar", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Nom");
        assertParseCorrect("sokakları", "sokak(sokak)+Noun+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])", "sokak(sokak)+Noun+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom", "sokak(sokak)+Noun+A3pl(lAr[lar])+P3pl(!I[ı])+Nom", "sokak(sokak)+Noun+A3sg+P3pl(lAr!I[ları])+Nom");
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
}
