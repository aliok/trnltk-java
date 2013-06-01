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

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class BaseContextlessMorphologicParserBruteForceVerbTest extends BaseContextlessMorphologicParserTest {

    @Test
    public void should_mark_unparsable() {
        assertNotParsable("d");
        assertNotParsable("dp");
        assertNotParsable("ayl");
        assertNotParsable("anf");
        assertNotParsable("azz");
        assertNotParsable("ddr");
        assertNotParsable("xxx");

    }

    @Test
    public void should_find_one_result_for_words_not_acceptable_by_suffix_graph() {
        assertParseCorrect("asdasmo", "asdasmo(asdasmomak)+Verb+Pos+Imp+A2sg");
        assertParseCorrect("balpaze", "balpaze(balpazemek)+Verb+Pos+Imp+A2sg");

    }

    @Test
    public void should_parse_simple_verbs() {
        assertParseCorrect("de", "de(demek)+Verb+Pos+Imp+A2sg");

        assertParseCorrect("git", "git(gitmek)+Verb+Pos+Imp+A2sg", "gi(gimek)+Verb+Verb+Caus(!t[t])+Pos+Imp+A2sg");

        assertParseCorrect("sok", "sok(sokmak)+Verb+Pos+Imp+A2sg");

        assertParseCorrect("deyip",
                "deyip(deyipmek)+Verb+Pos+Imp+A2sg",
                "de(demek)+Verb+Pos+Adv+AfterDoingSo(+yI!p[yip])",
                "dey(deymek)+Verb+Pos+Adv+AfterDoingSo(+yI!p[ip])");

        assertParseCorrect("sokacak",
                "sok(sokmak)+Verb+Pos+Fut(+yAcAk[acak])+A3sg",
                "sok(sokmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero",
                "sok(sokmak)+Verb+Pos+Adj+FutPart(+yAcAk[acak])+Pnon",
                "sok(sokmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom",
                "sok(sokmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "sokacak(sokacakmak)+Verb+Pos+Imp+A2sg");

        assertParseCorrect("saldı", "sal(salmak)+Verb+Pos+Past(dI[dı])+A3sg", "saldı(saldımak)+Verb+Pos+Imp+A2sg");

    }

    @Test
    public void should_parse_verbs_with_progressive_vowel_drop() {
        assertParseCorrect("başlıyor",
                "başl(başlamak)+Verb+Pos+Prog(Iyor[ıyor])+A3sg",
                "başl(başlımak)+Verb+Pos+Prog(Iyor[ıyor])+A3sg",
                "başlıyo(başlıyomak)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "başlıyor(başlıyormak)+Verb+Pos+Imp+A2sg",
                "başlıyo(başlıyomak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "başlıyo(başlıyomak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("elliyorduk",
                "ell(ellemek)+Verb+Pos+Prog(Iyor[iyor])+Past(dI[du])+A1pl(!k[k])",
                "ell(ellimek)+Verb+Pos+Prog(Iyor[iyor])+Past(dI[du])+A1pl(!k[k])",
                "elliyor(elliyormak)+Verb+Pos+Past(dI[du])+A1pl(!k[k])",
                "elliyorduk(elliyordukmak)+Verb+Pos+Imp+A2sg",
                "elliyo(elliyomak)+Verb+Pos+Aor(+Ar[r])+Past(dI[du])+A1pl(!k[k])",
                "elliyor(elliyormak)+Verb+Pos+Adj+PastPart(dIk[duk])+Pnon",
                "elliyor(elliyormak)+Verb+Pos+Noun+PastPart(dIk[duk])+A3sg+Pnon+Nom");

        assertParseCorrect("oynuyorlar",
                "oyn(oynamak)+Verb+Pos+Prog(Iyor[uyor])+A3pl(lAr[lar])",
                "oyn(oynumak)+Verb+Pos+Prog(Iyor[uyor])+A3pl(lAr[lar])",
                "oynuyo(oynuyomak)+Verb+Pos+Aor(+Ar[r])+A3pl(lAr[lar])",
                "oynuyorla(oynuyorlamak)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "oynuyorlar(oynuyorlarmak)+Verb+Pos+Imp+A2sg",
                "oynuyorla(oynuyorlamak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "oynuyo(oynuyomak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom",
                "oynuyorla(oynuyorlamak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("söylüyorsun",
                "söyl(söylemek)+Verb+Pos+Prog(Iyor[üyor])+A2sg(sIn[sun])",
                "söyl(söylümek)+Verb+Pos+Prog(Iyor[üyor])+A2sg(sIn[sun])",
                "söylüyo(söylüyomak)+Verb+Pos+Aor(+Ar[r])+A2sg(sIn[sun])",
                "söylüyor(söylüyormak)+Verb+Pos+Imp+A3sg(sIn[sun])",
                "söylüyorsun(söylüyorsunmak)+Verb+Pos+Imp+A2sg",
                "söylüyorsu(söylüyorsumak)+Verb+Verb+Pass(+In[n])+Pos+Imp+A2sg");

        assertParseCorrect("atlıyorsunuz",
                "atl(atlamak)+Verb+Pos+Prog(Iyor[ıyor])+A2pl(sInIz[sunuz])",
                "atl(atlımak)+Verb+Pos+Prog(Iyor[ıyor])+A2pl(sInIz[sunuz])",
                "atlıyo(atlıyomak)+Verb+Pos+Aor(+Ar[r])+A2pl(sInIz[sunuz])",
                "atlıyorsunuz(atlıyorsunuzmak)+Verb+Pos+Imp+A2sg");

        assertParseCorrect("kazıyor",
                "kaz(kazmak)+Verb+Pos+Prog(Iyor[ıyor])+A3sg",
                "kaz(kazamak)+Verb+Pos+Prog(Iyor[ıyor])+A3sg",
                "kaz(kazımak)+Verb+Pos+Prog(Iyor[ıyor])+A3sg",
                "kazıyo(kazıyomak)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "kazıyor(kazıyormak)+Verb+Pos+Imp+A2sg",
                "kazıyo(kazıyomak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "kazıyo(kazıyomak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("koruyor",
                "kor(kormak)+Verb+Pos+Prog(Iyor[uyor])+A3sg",
                "kor(koramak)+Verb+Pos+Prog(Iyor[uyor])+A3sg",
                "kor(korumak)+Verb+Pos+Prog(Iyor[uyor])+A3sg",
                "koruyo(koruyomak)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "koruyor(koruyormak)+Verb+Pos+Imp+A2sg",
                "koruyo(koruyomak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "koruyo(koruyomak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_verbs_with_aorist_A_and_causative_Ar() {
        assertParseCorrect("çıkar",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+A3sg",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+A3sg",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+A3sg",
                "çıka(çıkamak)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "çıkar(çıkarmak)+Verb+Pos+Imp+A2sg",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+Adj+Zero",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+Adj+Zero",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+Adj+Zero",
                "çıka(çıkamak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "çık(çıkmak)+Verb+Verb+Caus(Ar[ar])+Pos+Imp+A2sg",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "çık(çıkmak)+Verb+Pos+Aor(+Ar[ar])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "çıka(çıkamak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("ötercesine",
                "ötercesine(ötercesinemek)+Verb+Pos+Imp+A2sg",
                "ötercesin(ötercesinmek)+Verb+Pos+Opt(A[e])+A3sg",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adv+AsIf(cAs!InA[cesine])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adv+AsIf(cAs!InA[cesine])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adv+AsIf(cAs!InA[cesine])",
                "öte(ötemek)+Verb+Pos+Aor(+Ar[r])+Adv+AsIf(cAs!InA[cesine])",
                "öterces(ötercesmek)+Verb+Verb+Pass(+In[in])+Pos+Opt(A[e])+A3sg",
                "ötercesi(ötercesimek)+Verb+Verb+Pass(+In[n])+Pos+Opt(A[e])+A3sg",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öte(ötemek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Adj+Quite(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Adj+Quite(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Adj+Quite(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öte(ötemek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Adj+Quite(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öt(ötmek)+Verb+Pos+Aor(+Ar[er])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "öte(ötemek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])");


        assertParseCorrect("zebersin",
                "zeb(zebmek)+Verb+Pos+Aor(+Ar[er])+A2sg(sIn[sin])",
                "zeb(zebmek)+Verb+Pos+Aor(+Ar[er])+A2sg(sIn[sin])",
                "zeb(zebmek)+Verb+Pos+Aor(+Ar[er])+A2sg(sIn[sin])",
                "zebe(zebemek)+Verb+Pos+Aor(+Ar[r])+A2sg(sIn[sin])",
                "zeber(zebermek)+Verb+Pos+Imp+A3sg(sIn[sin])",
                "zebersin(zebersinmek)+Verb+Pos+Imp+A2sg",
                "zeb(zebmek)+Verb+Verb+Caus(Ar[er])+Pos+Imp+A3sg(sIn[sin])",
                "zebersi(zebersimek)+Verb+Verb+Pass(+In[n])+Pos+Imp+A2sg");

    }

    @Test
    public void should_parse_verbs_with_aorist_I() {
        assertParseCorrect("yatır",
                "ya(yamak)+Verb+Verb+Caus(!t[t])+Pos+Aor(+Ir[ır])+A3sg",
                "ya(yamak)+Verb+Verb+Caus(!t[t])+Verb+Caus(Ir[ır])+Pos+Imp+A2sg",
                "yat(yatmak)+Verb+Pos+Aor(+Ir[ır])+A3sg",
                "yat(yatmak)+Verb+Verb+Caus(Ir[ır])+Pos+Imp+A2sg",
                "yatı(yatımak)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "yatır(yatırmak)+Verb+Pos+Imp+A2sg",
                "yat(yatmak)+Verb+Pos+Aor(+Ir[ır])+Adj+Zero",
                "yatı(yatımak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "ya(yamak)+Verb+Verb+Caus(!t[t])+Pos+Aor(+Ir[ır])+Adj+Zero",
                "yat(yatmak)+Verb+Pos+Aor(+Ir[ır])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "yatı(yatımak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "ya(yamak)+Verb+Verb+Caus(!t[t])+Pos+Aor(+Ir[ır])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("gelir",
                "gel(gelmek)+Verb+Pos+Aor(+Ir[ir])+A3sg",
                "geli(gelimek)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "gelir(gelirmek)+Verb+Pos+Imp+A2sg",
                "gel(gelmek)+Verb+Pos+Aor(+Ir[ir])+Adj+Zero",
                "geli(gelimek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "gel(gelmek)+Verb+Verb+Caus(Ir[ir])+Pos+Imp+A2sg",
                "gel(gelmek)+Verb+Pos+Aor(+Ir[ir])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "geli(gelimek)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("zopuracak",
                "zopur(zopurmak)+Verb+Pos+Fut(+yAcAk[acak])+A3sg",
                "zopuracak(zopuracakmak)+Verb+Pos+Imp+A2sg",
                "zopur(zopurmak)+Verb+Pos+Adj+FutPart(+yAcAk[acak])+Pnon",
                "zopur(zopurmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero",
                "zop(zopmak)+Verb+Verb+Caus(Ir[ur])+Pos+Fut(+yAcAk[acak])+A3sg",
                "zop(zopmak)+Verb+Verb+Caus(Ir[ur])+Pos+Adj+FutPart(+yAcAk[acak])+Pnon",
                "zop(zopmak)+Verb+Verb+Caus(Ir[ur])+Pos+Fut(+yAcAk[acak])+Adj+Zero",
                "zopur(zopurmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom",
                "zop(zopmak)+Verb+Verb+Caus(Ir[ur])+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom",
                "zopur(zopurmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "zop(zopmak)+Verb+Verb+Caus(Ir[ur])+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("zoburacak",
                "zobur(zoburmak)+Verb+Pos+Fut(+yAcAk[acak])+A3sg",
                "zoburacak(zoburacakmak)+Verb+Pos+Imp+A2sg",
                "zobur(zoburmak)+Verb+Pos+Adj+FutPart(+yAcAk[acak])+Pnon",
                "zobur(zoburmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero",
                "zob(zobmak)+Verb+Verb+Caus(Ir[ur])+Pos+Fut(+yAcAk[acak])+A3sg",
                "zob(zobmak)+Verb+Verb+Caus(Ir[ur])+Pos+Adj+FutPart(+yAcAk[acak])+Pnon",
                "zob(zobmak)+Verb+Verb+Caus(Ir[ur])+Pos+Fut(+yAcAk[acak])+Adj+Zero",
                "zobur(zoburmak)+Verb+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom",
                "zob(zobmak)+Verb+Verb+Caus(Ir[ur])+Pos+Noun+FutPart(+yAcAk[acak])+A3sg+Pnon+Nom",
                "zobur(zoburmak)+Verb+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "zob(zobmak)+Verb+Verb+Caus(Ir[ur])+Pos+Fut(+yAcAk[acak])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_verbs_with_causative_t() {
        assertParseCorrect("kapattım",
                "kapat(kapatmak)+Verb+Pos+Past(dI[tı])+A1sg(+Im[m])",
                "kapattı(kapattımak)+Verb+Neg(m[m])+Imp+A2sg",
                "kapattım(kapattımmak)+Verb+Pos+Imp+A2sg",
                "kapa(kapamak)+Verb+Verb+Caus(!t[t])+Pos+Past(dI[tı])+A1sg(+Im[m])");

        assertParseCorrect("yürütecekmiş",
                "yürütecek(yürütecekmek)+Verb+Pos+Narr(mIş[miş])+A3sg",
                "yürütecekmiş(yürütecekmişmek)+Verb+Pos+Imp+A2sg",
                "yürüt(yürütmek)+Verb+Pos+Fut(+yAcAk[ecek])+Narr(mIş[miş])+A3sg",
                "yürütecek(yürütecekmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero",
                "yürütecekmi(yürütecekmimek)+Verb+Verb+Recip(+Iş[ş])+Pos+Imp+A2sg",
                "yür(yürmek)+Verb+Verb+Caus(I!t[üt])+Pos+Fut(+yAcAk[ecek])+Narr(mIş[miş])+A3sg",
                "yürü(yürümek)+Verb+Verb+Caus(!t[t])+Pos+Fut(+yAcAk[ecek])+Narr(mIş[miş])+A3sg",
                "yürütecek(yürütecekmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_verbs_with_causative_It() {
        assertParseCorrect("akıtmışlar",
                "ak(akmak)+Verb+Verb+Caus(I!t[ıt])+Pos+Narr(mIş[mış])+A3pl(lAr[lar])",
                "akı(akımak)+Verb+Verb+Caus(!t[t])+Pos+Narr(mIş[mış])+A3pl(lAr[lar])",
                "akıt(akıtmak)+Verb+Pos+Narr(mIş[mış])+A3pl(lAr[lar])",
                "akıtmışla(akıtmışlamak)+Verb+Pos+Aor(+Ar[r])+A3sg",
                "akıtmışlar(akıtmışlarmak)+Verb+Pos+Imp+A2sg",
                "akıtmışla(akıtmışlamak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero",
                "akıt(akıtmak)+Verb+Pos+Narr(mIş[mış])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom",
                "akıtmışla(akıtmışlamak)+Verb+Pos+Aor(+Ar[r])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom",
                "ak(akmak)+Verb+Verb+Caus(I!t[ıt])+Pos+Narr(mIş[mış])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom",
                "akı(akımak)+Verb+Verb+Caus(!t[t])+Pos+Narr(mIş[mış])+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom");

        assertParseCorrect("korkut",
                "korkut(korkutmak)+Verb+Pos+Imp+A2sg",
                "kork(korkmak)+Verb+Verb+Caus(I!t[ut])+Pos+Imp+A2sg",
                "korku(korkumak)+Verb+Verb+Caus(!t[t])+Pos+Imp+A2sg");

    }

    @Test
    public void should_parse_verbs_with_causative_dIr() {
        assertParseCorrect("aldırsın",
                "ald(altmak)+Verb+Pos+Aor(+Ir[ır])+A2sg(sIn[sın])",
                "ald(aldmak)+Verb+Pos+Aor(+Ir[ır])+A2sg(sIn[sın])",
                "aldı(aldımak)+Verb+Pos+Aor(+Ar[r])+A2sg(sIn[sın])",
                "aldır(aldırmak)+Verb+Pos+Imp+A3sg(sIn[sın])",
                "aldırsın(aldırsınmak)+Verb+Pos+Imp+A2sg",
                "al(almak)+Verb+Verb+Caus(dIr[dır])+Pos+Imp+A3sg(sIn[sın])",
                "ald(altmak)+Verb+Verb+Caus(Ir[ır])+Pos+Imp+A3sg(sIn[sın])",
                "ald(aldmak)+Verb+Verb+Caus(Ir[ır])+Pos+Imp+A3sg(sIn[sın])",
                "aldırsı(aldırsımak)+Verb+Verb+Pass(+In[n])+Pos+Imp+A2sg");

        assertParseCorrect("öldürelim",
                "öldür(öldürmek)+Verb+Pos+Opt(A[e])+A1pl(lIm[lim])",
                "öldüreli(öldürelimek)+Verb+Neg(m[m])+Imp+A2sg",
                "öldürelim(öldürelimmek)+Verb+Pos+Imp+A2sg",
                "öl(ölmek)+Verb+Verb+Caus(dIr[dür])+Pos+Opt(A[e])+A1pl(lIm[lim])",
                "öld(öldmek)+Verb+Verb+Caus(Ir[ür])+Pos+Opt(A[e])+A1pl(lIm[lim])",
                "öld(öltmek)+Verb+Verb+Caus(Ir[ür])+Pos+Opt(A[e])+A1pl(lIm[lim])");

        assertParseCorrect("öttürsek",
                "öttür(öttürmek)+Verb+Pos+Cond(+ysA[se])+A1pl(!k[k])",
                "öttür(öttürmek)+Verb+Pos+Desr(sA[se])+A1pl(!k[k])",
                "öttürsek(öttürsekmek)+Verb+Pos+Imp+A2sg",
                "öttü(öttümek)+Verb+Pos+Aor(+Ar[r])+Cond(+ysA[se])+A1pl(!k[k])",
                "öt(ötmek)+Verb+Verb+Caus(dIr[tür])+Pos+Cond(+ysA[se])+A1pl(!k[k])",
                "öt(ötmek)+Verb+Verb+Caus(dIr[tür])+Pos+Desr(sA[se])+A1pl(!k[k])");
    }

    @Override
    public void assertParseCorrect(String surfaceToParse, String... expectedParseResults) {
        final ArrayList<String> list = Lists.newArrayList(expectedParseResults);
        final HashSet<String> set = new HashSet<String>(list);
        if (set.size() == list.size())
            System.out.println("There are duplicate items in expected parse results.\n\t\t" + Thread.currentThread().getStackTrace()[2]);
        super.assertParseCorrect(surfaceToParse, expectedParseResults);
    }
}
