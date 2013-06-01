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

package org.trnltk.morphology.contextless.parser.parsing;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class BaseContextlessMorphologicParserBruteForceNounTest extends BaseContextlessMorphologicParserTest {

    @Test
    public void should_find_one_result_for_words_not_acceptable_by_suffix_graph() {
        assertParseCorrect("asdasmo", "asdasmo(asdasmo)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("asdassü", "asdassü(asdassü)+Noun+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_simple_nouns() {
        assertParseCorrect("o", "o(o)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("om", "om(om)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("b", "b(b)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("be", "be(be)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("bem", "be(be)+Noun+A3sg+P1sg(+Im[m])+Nom", "bem(bem)+Noun+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_with_possible_voicing() {
        assertParseCorrect("oda", "od(od)+Noun+A3sg+Pnon+Dat(+yA[a])", "od(ot)+Noun+A3sg+Pnon+Dat(+yA[a])", "oda(oda)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("kağa", "kağ(kağ)+Noun+A3sg+Pnon+Dat(+yA[a])", "kağ(kag)+Noun+A3sg+Pnon+Dat(+yA[a])", "kağ(kak)+Noun+A3sg+Pnon+Dat(+yA[a])", "kağa(kağa)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("zogu", "zog(zog)+Noun+A3sg+Pnon+Acc(+yI[u])", "zog(zog)+Noun+A3sg+P3sg(+sI[u])+Nom", "zogu(zogu)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("ıba", "ıb(ıb)+Noun+A3sg+Pnon+Dat(+yA[a])", "ıb(ıp)+Noun+A3sg+Pnon+Dat(+yA[a])", "ıba(ıba)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("acı", "ac(ac)+Noun+A3sg+Pnon+Acc(+yI[ı])", "ac(ac)+Noun+A3sg+P3sg(+sI[ı])+Nom", "ac(aç)+Noun+A3sg+Pnon+Acc(+yI[ı])", "ac(aç)+Noun+A3sg+P3sg(+sI[ı])+Nom", "acı(acı)+Noun+A3sg+Pnon+Nom");
        // skip nK -> nG voicing as in cenk->cengi
        assertParseCorrect("cengi", "ceng(ceng)+Noun+A3sg+Pnon+Acc(+yI[i])", "ceng(ceng)+Noun+A3sg+P3sg(+sI[i])+Nom", "cengi(cengi)+Noun+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_with_explicit_no_voicing() {
        assertParseCorrect("ota", "ot(ot)+Noun+A3sg+Pnon+Dat(+yA[a])", "ota(ota)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("kaka", "kak(kak)+Noun+A3sg+Pnon+Dat(+yA[a])", "kaka(kaka)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("zoku", "zok(zok)+Noun+A3sg+Pnon+Acc(+yI[u])", "zok(zok)+Noun+A3sg+P3sg(+sI[u])+Nom", "zoku(zoku)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("zogu", "zog(zog)+Noun+A3sg+Pnon+Acc(+yI[u])", "zog(zog)+Noun+A3sg+P3sg(+sI[u])+Nom", "zogu(zogu)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("ıpa", "ıp(ıp)+Noun+A3sg+Pnon+Dat(+yA[a])", "ıpa(ıpa)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("açı", "aç(aç)+Noun+A3sg+Pnon+Acc(+yI[ı])", "aç(aç)+Noun+A3sg+P3sg(+sI[ı])+Nom", "açı(açı)+Noun+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_with_explicit_inverse_harmony() {
        assertParseCorrect("ome", "om(om)+Noun+A3sg+Pnon+Dat(+yA[e])", "ome(ome)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("ani", "an(an)+Noun+A3sg+Pnon+Acc(+yI[i])", "an(an)+Noun+A3sg+P3sg(+sI[i])+Nom", "ani(ani)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("bema", "bem(bem)+Noun+A3sg+Pnon+Dat(+yA[a])", "bema(bema)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("bomü", "bom(bom)+Noun+A3sg+Pnon+Acc(+yI[ü])", "bom(bom)+Noun+A3sg+P3sg(+sI[ü])+Nom", "bomü(bomü)+Noun+A3sg+Pnon+Nom");
    }

    @Test
    public void should_parse_with_possible_voicing_and_explicit_inverse_harmony() {
        assertParseCorrect("ode", "od(od)+Noun+A3sg+Pnon+Dat(+yA[e])", "od(ot)+Noun+A3sg+Pnon+Dat(+yA[e])", "ode(ode)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("kağe", "kağ(kağ)+Noun+A3sg+Pnon+Dat(+yA[e])", "kağ(kag)+Noun+A3sg+Pnon+Dat(+yA[e])", "kağ(kak)+Noun+A3sg+Pnon+Dat(+yA[e])", "kağe(kağe)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("zogü", "zog(zog)+Noun+A3sg+Pnon+Acc(+yI[ü])", "zog(zog)+Noun+A3sg+P3sg(+sI[ü])+Nom", "zogü(zogü)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("ıbe", "ıb(ıb)+Noun+A3sg+Pnon+Dat(+yA[e])", "ıb(ıp)+Noun+A3sg+Pnon+Dat(+yA[e])", "ıbe(ıbe)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("aci", "ac(ac)+Noun+A3sg+Pnon+Acc(+yI[i])", "ac(ac)+Noun+A3sg+P3sg(+sI[i])+Nom", "ac(aç)+Noun+A3sg+Pnon+Acc(+yI[i])", "ac(aç)+Noun+A3sg+P3sg(+sI[i])+Nom", "aci(aci)+Noun+A3sg+Pnon+Nom");
        // skip nK -> nG voicing as in cenk->cengi
        assertParseCorrect("cengı", "ceng(ceng)+Noun+A3sg+Pnon+Acc(+yI[ı])", "ceng(ceng)+Noun+A3sg+P3sg(+sI[ı])+Nom", "cengı(cengı)+Noun+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_with_explicit_no_voicing_and_inverse_harmony() {
        assertParseCorrect("ote", "ot(ot)+Noun+A3sg+Pnon+Dat(+yA[e])", "ote(ote)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("kake", "kak(kak)+Noun+A3sg+Pnon+Dat(+yA[e])", "kake(kake)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("zokü", "zok(zok)+Noun+A3sg+Pnon+Acc(+yI[ü])", "zok(zok)+Noun+A3sg+P3sg(+sI[ü])+Nom", "zokü(zokü)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("zogü", "zog(zog)+Noun+A3sg+Pnon+Acc(+yI[ü])", "zog(zog)+Noun+A3sg+P3sg(+sI[ü])+Nom", "zogü(zogü)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("ıpe", "ıp(ıp)+Noun+A3sg+Pnon+Dat(+yA[e])", "ıpe(ıpe)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("açi", "aç(aç)+Noun+A3sg+Pnon+Acc(+yI[i])", "aç(aç)+Noun+A3sg+P3sg(+sI[i])+Nom", "açi(açi)+Noun+A3sg+Pnon+Nom");

    }

    @Test
    public void should_parse_with_doubling() {
        assertParseCorrect("assı", "ass(ass)+Noun+A3sg+Pnon+Acc(+yI[ı])", "ass(ass)+Noun+A3sg+P3sg(+sI[ı])+Nom", "ass(as)+Noun+A3sg+Pnon+Acc(+yI[ı])", "ass(as)+Noun+A3sg+P3sg(+sI[ı])+Nom", "assı(assı)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("tıbbı", "tıbb(tıbb)+Noun+A3sg+Pnon+Acc(+yI[ı])", "tıbb(tıbb)+Noun+A3sg+P3sg(+sI[ı])+Nom", "tıbb(tıb)+Noun+A3sg+Pnon+Acc(+yI[ı])", "tıbb(tıb)+Noun+A3sg+P3sg(+sI[ı])+Nom", "tıbb(tıp)+Noun+A3sg+Pnon+Acc(+yI[ı])", "tıbb(tıp)+Noun+A3sg+P3sg(+sI[ı])+Nom", "tıbbı(tıbbı)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("hakka", "hakk(hakk)+Noun+A3sg+Pnon+Dat(+yA[a])", "hakk(hak)+Noun+A3sg+Pnon+Dat(+yA[a])", "hakka(hakka)+Noun+A3sg+Pnon+Nom");
        assertParseCorrect("hallini", "hall(hall)+Noun+A3sg+P2sg(+In[in])+Acc(+yI[i])", "hall(hall)+Noun+A3sg+P3sg(+sI[i])+Acc(nI[ni])", "hall(hal)+Noun+A3sg+P2sg(+In[in])+Acc(+yI[i])", "hall(hal)+Noun+A3sg+P3sg(+sI[i])+Acc(nI[ni])", "halli(halli)+Noun+A3sg+P2sg(+In[n])+Acc(+yI[i])", "hallin(hallin)+Noun+A3sg+Pnon+Acc(+yI[i])", "hallin(hallin)+Noun+A3sg+P3sg(+sI[i])+Nom", "hallini(hallini)+Noun+A3sg+Pnon+Nom", "hal(hal)+Noun+A3sg+Pnon+Nom+Adj+With(lI[li])+Noun+Zero+A3sg+P2sg(+In[n])+Acc(+yI[i])");
        assertParseCorrect("serhaddime", "serhadd(serhadd)+Noun+A3sg+P1sg(+Im[im])+Dat(+yA[e])", "serhadd(serhad)+Noun+A3sg+P1sg(+Im[im])+Dat(+yA[e])", "serhadd(serhat)+Noun+A3sg+P1sg(+Im[im])+Dat(+yA[e])", "serhaddi(serhaddi)+Noun+A3sg+P1sg(+Im[m])+Dat(+yA[e])", "serhaddim(serhaddim)+Noun+A3sg+Pnon+Dat(+yA[e])", "serhaddime(serhaddime)+Noun+A3sg+Pnon+Nom");
    }

    @Override
    public void assertParseCorrect(String surfaceToParse, String... expectedParseResults) {
        final ArrayList<String> list = Lists.newArrayList(expectedParseResults);
        final HashSet<String> set = new HashSet<String>(list);
        Validate.isTrue(set.size() == list.size(), "There are duplicate items in expected parse results");
        super.assertParseCorrect(surfaceToParse, expectedParseResults);
    }
}
