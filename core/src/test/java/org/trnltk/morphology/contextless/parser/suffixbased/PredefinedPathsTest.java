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

package org.trnltk.morphology.contextless.parser.suffixbased;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.lexicon.Lexeme;
import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.lexicon.SecondaryPos;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.testutil.testmatchers.ParseResultsEqualMatcher;
import org.trnltk.morphology.lexicon.DictionaryLoader;
import org.trnltk.morphology.lexicon.ImmutableRootGenerator;
import org.trnltk.morphology.lexicon.RootMapGenerator;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.trnltk.model.lexicon.PrimaryPos.Noun;
import static org.trnltk.model.lexicon.PrimaryPos.Pronoun;
import static org.trnltk.model.lexicon.SecondaryPos.Personal;

public class PredefinedPathsTest {

    private final HashMultimap<String, ? extends Root> rootMap;
    private final BasicSuffixGraph basicSuffixGraph;
    private final SuffixApplier suffixApplier;

    private PredefinedPaths predefinedPaths;

    public PredefinedPathsTest() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        final ImmutableRootGenerator immutableRootGenerator = new ImmutableRootGenerator();
        Collection<? extends Root> roots = immutableRootGenerator.generateAll(lexemes);
        this.rootMap = new RootMapGenerator().generate(roots);

        this.basicSuffixGraph = new BasicSuffixGraph();
        this.basicSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        // for predefined paths, we shouldn't use precached ones, since dynamic suffixForms are not conforming
        final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
        this.suffixApplier = new SuffixApplier(phoneticsEngine);
    }

    @Before
    public void setUp() throws Exception {
        this.predefinedPaths = new PredefinedPaths(basicSuffixGraph, rootMap, suffixApplier);
    }

    @Test
    public void shouldHavePathsForPersonalPronouns() {
        predefinedPaths.createPredefinedPathOf_ben();
        predefinedPaths.createPredefinedPathOf_sen();
        predefinedPaths.createPredefinedPathOf_biz();

        // last one ends with transition to derivation state
        assertDefinedPath("ben", Pronoun, Personal,
                "ben(ben)+Pron+Pers+A1sg+Pnon+Nom",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Acc(i[i])",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Abl(den[den])",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Ins(le[le])",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Ins(imle[imle])",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Gen(im[im])",
                "ben(ben)+Pron+Pers+A1sg+Pnon+AccordingTo(ce[ce])",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Nom");

        assertDefinedPath("ban", Pronoun, Personal,
                "ban(ben)+Pron+Pers+A1sg+Pnon+Dat(a[a])");

        // last one ends with transition to derivation state
        assertDefinedPath("sen", Pronoun, Personal,
                "sen(sen)+Pron+Pers+A2sg+Pnon+Nom",
                "sen(sen)+Pron+Pers+A2sg+Pnon+Acc(i[i])",
                "sen(sen)+Pron+Pers+A2sg+Pnon+Loc(de[de])",
                "sen(sen)+Pron+Pers+A2sg+Pnon+Abl(den[den])",
                "sen(sen)+Pron+Pers+A2sg+Pnon+Ins(le[le])",
                "sen(sen)+Pron+Pers+A2sg+Pnon+Ins(inle[inle])",
                "sen(sen)+Pron+Pers+A2sg+Pnon+Gen(in[in])",
                "sen(sen)+Pron+Pers+A2sg+Pnon+AccordingTo(ce[ce])",
                "sen(sen)+Pron+Pers+A2sg+Pnon+Nom");

        assertDefinedPath("san", Pronoun, Personal,
                "san(sen)+Pron+Pers+A2sg+Pnon+Dat(a[a])");

        assertDefinedPath("biz", Pronoun, Personal,
                "biz(biz)+Pron+Pers+A1pl+Pnon+Nom",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Nom",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Acc(i[i])",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Dat(e[e])",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Gen(im[im])",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Ins(le[le])",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Loc(de[de])",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Abl(den[den])",
                "biz(biz)+Pron+Pers+A1pl+Pnon+Ins(imle[imle])",
                "biz(biz)+Pron+Pers+A1pl+Pnon+AccordingTo(ce[ce])",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Nom",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Nom",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Acc(i[i])",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Dat(e[e])",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Gen(in[in])",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Ins(le[le])",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Loc(de[de])",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+Abl(den[den])",
                "biz(biz)+Pron+Pers+A1pl(ler[ler])+Pnon+AccordingTo(ce[ce])");
    }

    @Test
    public void test_should_have_paths_for_hepsi() {
        predefinedPaths.createPredefinedPathOf_hepsi();

        // last one ends with transition to derivation state
        assertDefinedPath("hepsi", Pronoun, null,
                "hepsi(hepsi)+Pron+A3pl+P3pl+Nom",
                "hepsi(hepsi)+Pron+A3pl+P3pl+Acc(ni[ni])",
                "hepsi(hepsi)+Pron+A3pl+P3pl+Dat(ne[ne])",
                "hepsi(hepsi)+Pron+A3pl+P3pl+Loc(nde[nde])",
                "hepsi(hepsi)+Pron+A3pl+P3pl+Abl(nden[nden])",
                "hepsi(hepsi)+Pron+A3pl+P3pl+Ins(yle[yle])",
                "hepsi(hepsi)+Pron+A3pl+P3pl+Gen(nin[nin])",
                "hepsi(hepsi)+Pron+A3pl+P3pl+AccordingTo(nce[nce])",
                "hepsi(hepsi)+Pron+A3pl+P3pl+Nom");

        // last one ends with transition to derivation state
        assertDefinedPath("hep", Pronoun, null,
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Nom",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Acc(i[i])",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Dat(e[e])",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Loc(de[de])",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Abl(den[den])",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Ins(le[le])",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Gen(in[in])",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+AccordingTo(ce[ce])",
                "hep(hepsi)+Pron+A1pl+P1pl(imiz[imiz])+Nom",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Nom",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Acc(i[i])",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Dat(e[e])",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Loc(de[de])",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Abl(den[den])",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Ins(le[le])",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Gen(in[in])",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+AccordingTo(ce[ce])",
                "hep(hepsi)+Pron+A2pl+P2pl(iniz[iniz])+Nom");
    }

    @Test
    public void test_should_have_paths_for_ques() {
        predefinedPaths.createPredefinedPathOf_question_particles();

        // last one ends with transition to derivation state
        assertDefinedPath("mı", PrimaryPos.Question, null,
                "mı(mı)+Ques+Pres+A1sg(yım[yım])",
                "mı(mı)+Ques+Pres+A2sg(sın[sın])",
                "mı(mı)+Ques+Pres+A3sg",
                "mı(mı)+Ques+Pres+A1pl(yız[yız])",
                "mı(mı)+Ques+Pres+A2pl(sınız[sınız])",
                "mı(mı)+Ques+Pres+A3pl(lar[lar])",
                "mı(mı)+Ques+Past(ydı[ydı])+A1sg(m[m])",
                "mı(mı)+Ques+Past(ydı[ydı])+A2sg(n[n])",
                "mı(mı)+Ques+Past(ydı[ydı])+A3sg",
                "mı(mı)+Ques+Past(ydı[ydı])+A1pl(k[k])",
                "mı(mı)+Ques+Past(ydı[ydı])+A2pl(nız[nız])",
                "mı(mı)+Ques+Past(ydı[ydı])+A3pl(lar[lar])",
                "mı(mı)+Ques+Narr(ymış[ymış])+A1sg(ım[ım])",
                "mı(mı)+Ques+Narr(ymış[ymış])+A2sg(sın[sın])",
                "mı(mı)+Ques+Narr(ymış[ymış])+A3sg",
                "mı(mı)+Ques+Narr(ymış[ymış])+A1pl(ız[ız])",
                "mı(mı)+Ques+Narr(ymış[ymış])+A2pl(sınız[sınız])",
                "mı(mı)+Ques+Narr(ymış[ymış])+A3pl(lar[lar])");
    }

    @Test
    public void test_should_have_paths_for_pronouns_with_implicit_possession() {
        predefinedPaths.createPredefinedPathOf_bazilari_bazisi();
        predefinedPaths.createPredefinedPathOf_kimileri_kimisi_kimi();
        predefinedPaths.createPredefinedPathOf_birileri_birisi_biri();
        predefinedPaths.createPredefinedPathOf_hicbirisi_hicbiri();
        predefinedPaths.createPredefinedPathOf_birbiri();
        predefinedPaths.createPredefinedPathOf_cogu_bircogu_coklari_bircoklari();
        predefinedPaths.createPredefinedPathOf_birkaci();
        predefinedPaths.createPredefinedPathOf_cumlesi();
        predefinedPaths.createPredefinedPathOf_digeri_digerleri();

        assertDefinedPath("bazıları", Pronoun, null, "bazıları(bazıları)+Pron+A3sg+P3sg", "bazıları(bazıları)+Pron+A3sg+P1pl(mız[mız])", "bazıları(bazıları)+Pron+A3sg+P2pl(nız[nız])");
        assertDefinedPath("bazısı", Pronoun, null, "bazısı(bazısı)+Pron+A3sg+P3sg");

        assertDefinedPath("kimileri", Pronoun, null, "kimileri(kimileri)+Pron+A3sg+P3sg", "kimileri(kimileri)+Pron+A3sg+P1pl(miz[miz])", "kimileri(kimileri)+Pron+A3sg+P2pl(niz[niz])");
        assertDefinedPath("kimisi", Pronoun, null, "kimisi(kimisi)+Pron+A3sg+P3sg");
        assertDefinedPath("kimi", Pronoun, null, "kimi(kimi)+Pron+A3sg+P3sg", "kimi(kimi)+Pron+A3sg+P1pl(miz[miz])", "kimi(kimi)+Pron+A3sg+P2pl(niz[niz])");

        assertDefinedPath("birileri", Pronoun, null, "birileri(birileri)+Pron+A3sg+P3sg", "birileri(birileri)+Pron+A3sg+P1pl(miz[miz])", "birileri(birileri)+Pron+A3sg+P2pl(niz[niz])");
        assertDefinedPath("birisi", Pronoun, null, "birisi(birisi)+Pron+A3sg+P3sg");
        assertDefinedPath("biri", Pronoun, null, "biri(biri)+Pron+A3sg+P3sg", "biri(biri)+Pron+A3sg+P1pl(miz[miz])", "biri(biri)+Pron+A3sg+P2pl(niz[niz])");

        assertDefinedPath("hiçbirisi", Pronoun, null, "hiçbirisi(hiçbirisi)+Pron+A3sg+P3sg");
        assertDefinedPath("hiçbiri", Pronoun, null, "hiçbiri(hiçbiri)+Pron+A3sg+P3sg", "hiçbiri(hiçbiri)+Pron+A3sg+P1pl(miz[miz])", "hiçbiri(hiçbiri)+Pron+A3sg+P2pl(niz[niz])");

        assertDefinedPath("birbiri", Pronoun, null, "birbiri(birbiri)+Pron+A3sg+P3sg", "birbiri(birbiri)+Pron+A1pl+P1pl(miz[miz])", "birbiri(birbiri)+Pron+A2pl+P2pl(niz[niz])");
        assertDefinedPath("birbir", Pronoun, null, "birbir(birbiri)+Pron+A3pl+P3pl(leri[leri])");

        assertDefinedPath("çoğu", Pronoun, null, "çoğu(çoğu)+Pron+A3sg+P3sg", "çoğu(çoğu)+Pron+A3sg+P1pl(muz[muz])", "çoğu(çoğu)+Pron+A3sg+P2pl(nuz[nuz])");
        assertDefinedPath("birçoğu", Pronoun, null, "birçoğu(birçoğu)+Pron+A3sg+P3sg", "birçoğu(birçoğu)+Pron+A3sg+P1pl(muz[muz])", "birçoğu(birçoğu)+Pron+A3sg+P2pl(nuz[nuz])");
        assertDefinedPath("çokları", Pronoun, null, "çokları(çokları)+Pron+A3sg+P3pl");
        assertDefinedPath("birçokları", Pronoun, null, "birçokları(birçokları)+Pron+A3sg+P3pl");

        assertDefinedPath("birkaçı", Pronoun, null, "birkaçı(birkaçı)+Pron+A3sg+P3sg", "birkaçı(birkaçı)+Pron+A3sg+P1pl(mız[mız])", "birkaçı(birkaçı)+Pron+A3sg+P2pl(nız[nız])");

        assertDefinedPath("cümlesi", Pronoun, null, "cümlesi(cümlesi)+Pron+A3sg+P3sg");

        assertDefinedPath("diğeri", Pronoun, null, "diğeri(diğeri)+Pron+A3sg+P3sg", "diğeri(diğeri)+Pron+A3sg+P1pl(miz[miz])", "diğeri(diğeri)+Pron+A3sg+P2pl(niz[niz])");
        assertDefinedPath("diğerleri", Pronoun, null, "diğerleri(diğerleri)+Pron+A3sg+P3pl", "diğerleri(diğerleri)+Pron+A3sg+P1pl(miz[miz])", "diğerleri(diğerleri)+Pron+A3sg+P2pl(niz[niz])");
    }

    @Test
    public void test_should_have_paths_for_irregular_pronouns() {
        predefinedPaths.createPredefinedPathOf_herkes();

        assertDefinedPath("herkes", Pronoun, null, "herkes(herkes)+Pron+A3sg+Pnon");
    }

    @Test
    public void test_should_have_paths_for_pronouns_bura_sura_ora() {
        predefinedPaths.createPredefinedPathOf_ora_bura_sura_nere();

        assertDefinedPath("or", Pronoun, null, "or(ora)+Pron+A3sg+Pnon+Loc(da[da])", "or(ora)+Pron+A3sg+Pnon+Abl(dan[dan])");
        assertDefinedPath("bur", Pronoun, null, "bur(bura)+Pron+A3sg+Pnon+Loc(da[da])", "bur(bura)+Pron+A3sg+Pnon+Abl(dan[dan])");
        assertDefinedPath("şur", Pronoun, null, "şur(şura)+Pron+A3sg+Pnon+Loc(da[da])", "şur(şura)+Pron+A3sg+Pnon+Abl(dan[dan])");
        assertDefinedPath("ner", Pronoun, SecondaryPos.Question, "ner(nere)+Pron+Ques+A3sg+Pnon+Loc(de[de])", "ner(nere)+Pron+Ques+A3sg+Pnon+Abl(den[den])");
    }

    @Test
    public void test_should_have_paths_for_iceri_disari() {
        predefinedPaths.createPredefinedPathOf_iceri_disari();

        assertDefinedPath("içer", Noun, null, "içer(içeri)+Noun+A3sg+Pnon+Loc(de[de])", "içer(içeri)+Noun+A3sg+Pnon+Abl(den[den])", "içer(içeri)+Noun+A3sg+P3sg(si[si])");
        assertDefinedPath("dışar", Noun, null, "dışar(dışarı)+Noun+A3sg+Pnon+Loc(da[da])", "dışar(dışarı)+Noun+A3sg+Pnon+Abl(dan[dan])", "dışar(dışarı)+Noun+A3sg+P3sg(sı[sı])");
    }

    @Test
    public void shouldBuildAllPathsWithoutAnError() {
        predefinedPaths.initialize();
    }

    private void assertDefinedPath(String rootStr, PrimaryPos primaryPos, SecondaryPos secondaryPos, String... expectedResults) {
        assertThat(this.getFormattedPredefinedMorhpemeContainers(rootStr, primaryPos, secondaryPos), new ParseResultsEqualMatcher(false, expectedResults));
    }

    private Collection<String> getFormattedPredefinedMorhpemeContainers(String rootStr, PrimaryPos
            primaryPos, SecondaryPos secondaryPos) {
        final Set<? extends Root> roots = this.rootMap.get(rootStr);
        for (Root root : roots) {
            if (root.getLexeme().getPrimaryPos().equals(primaryPos) && Objects.equal(root.getLexeme().getSecondaryPos(), secondaryPos))
                return getFormattedPredefinedMorhpemeContainersForRoot(root);
        }

        fail("No root found in root map for " + rootStr + " " + primaryPos + " " + secondaryPos);
        return null;
    }

    private Collection<String> getFormattedPredefinedMorhpemeContainersForRoot(final Root root) {
        final Set<MorphemeContainer> morphemeContainers = this.predefinedPaths.getPaths(root);
        return Collections2.transform(morphemeContainers, new Function<MorphemeContainer, String>() {
            @Override
            public String apply(MorphemeContainer input) {
                return MorphemeContainerFormatter.formatMorphemeContainerWithForms(input);
            }
        });
    }
}
