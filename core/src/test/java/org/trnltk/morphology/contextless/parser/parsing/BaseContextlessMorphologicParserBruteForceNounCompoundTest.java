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

public abstract class BaseContextlessMorphologicParserBruteForceNounCompoundTest extends BaseContextlessMorphologicParserTest {


    @Test
    public void should_not_parse_some_cases_without_consontant_S_insertion() {
        // used imaginary compound "ateli" to keep it short
        assertNotParsable("a");
        assertNotParsable("an");
        assertNotParsable("anu");

        assertNotParsable("at");
        assertNotParsable("atn");
        assertNotParsable("atnu");

        assertNotParsable("ate");
        assertNotParsable("aten");
        assertNotParsable("ateni");

        assertNotParsable("atel");
        assertNotParsable("ateli");
        assertNotParsable("atelni");

        assertNotParsable("ateli");
        assertNotParsable("atelin");
        // following is parsable, which is correct!
        assertParseCorrect("atelini", "atel(ateli)+Noun+A3sg+P3sg(+sI[i])+Acc(nI[ni])");

        assertNotParsable("ateleni");
        assertNotParsable("atelani");
        assertNotParsable("ateluni");
        assertNotParsable("atelüni");
        assertNotParsable("ateloni");
        assertNotParsable("atelöni");

        assertNotParsable("atsdefani");
        assertNotParsable("atsdefoni");
        assertNotParsable("atsdefuni");
        assertNotParsable("atsdefüni");

        assertNotParsable("atsdefanu");
        assertNotParsable("atsdefonu");
        assertNotParsable("atsdefunu");
        assertNotParsable("atsdefünu");

    }

    @Test
    public void should_not_parse_some_cases_with_consontant_S_insertion() {
        // used imaginary compound "suağası" to keep it short
        assertNotParsable("s");
        assertNotParsable("sn");
        assertNotParsable("snu");

        assertNotParsable("su");
        assertNotParsable("sun");
        assertNotParsable("sunu");

        assertNotParsable("sua");
        assertNotParsable("suan");
        assertNotParsable("suanı");

        assertNotParsable("suağ");
        assertNotParsable("suağı");
        assertNotParsable("suağnı");

        assertNotParsable("suağa");
        assertNotParsable("suağan");
        assertNotParsable("suağanı");        // actually, this is also compound, but this case is not supported

        assertNotParsable("suağas");
        assertNotParsable("suağası");
        assertNotParsable("suağasnı");

        assertNotParsable("suağası");
        assertNotParsable("suağasın");
        // following is parsable, which is correct!
        assertParseCorrect("suağasını", "suağas(suağası)+Noun+A3sg+P3sg(+sI[ı])+Acc(nI[nı])", "suağa(suağası)+Noun+A3sg+P3sg(+sI[sı])+Acc(nI[nı])");

        assertNotParsable("suağassn");

        assertNotParsable("suağasanı");
        assertNotParsable("suağasenı");
        assertNotParsable("suağasunı");
        assertNotParsable("suağasünı");
        assertNotParsable("suağasonı");
        assertNotParsable("suağasönı");

        assertNotParsable("suağasanu");
        assertNotParsable("suağasenu");
        assertNotParsable("suağasunu");
        assertNotParsable("suağasünu");
        assertNotParsable("suağasonu");
        assertNotParsable("suağasönu");
    }

    @Test
    public void should_parse_simple_compounds() {
        assertParseCorrect("bacakkalemini", "bacakkalem(bacakkalemi)+Noun+A3sg+P3sg(+sI[i])+Acc(nI[ni])");
        assertParseCorrect("suborusuna", "suborus(suborusu)+Noun+A3sg+P3sg(+sI[u])+Dat(nA[na])", "suboru(suborusu)+Noun+A3sg+P3sg(+sI[su])+Dat(nA[na])");
    }

    @Test
    public void should_parse_with_possible_voicing() {
        assertParseCorrect("kuzukulağını", "kuzukulağ(kuzukulağı)+Noun+A3sg+P3sg(+sI[ı])+Acc(nI[nı])");
        assertParseCorrect("eczadolabında", "eczadolab(eczadolabı)+Noun+A3sg+P3sg(+sI[ı])+Loc(ndA[nda])");
        assertParseCorrect("kafakağıdından", "kafakağıd(kafakağıdı)+Noun+A3sg+P3sg(+sI[ı])+Abl(ndAn[ndan])");
    }

    @Test
    public void should_parse_with_explicit_no_voicing() {
        assertParseCorrect("adamotuna", "adamot(adamotu)+Noun+A3sg+P3sg(+sI[u])+Dat(nA[na])");
        assertParseCorrect("kaleiçinden", "kaleiç(kaleiçi)+Noun+A3sg+P3sg(+sI[i])+Abl(ndAn[nden])");
        assertParseCorrect("uykuhapını", "uykuhap(uykuhapı)+Noun+A3sg+P3sg(+sI[ı])+Acc(nI[nı])");
        assertParseCorrect("anaerkine", "anaerk(anaerki)+Noun+A3sg+P3sg(+sI[i])+Dat(nA[ne])");
    }

    @Test
    public void should_parse_with_explicit_inverse_harmony() {
        assertParseCorrect("dünyahaline", "dünyahal(dünyahali)+Noun+A3sg+P3sg(+sI[i])+Dat(nA[ne])");
        assertParseCorrect("doğuekolünü", "doğuekol(doğuekolü)+Noun+A3sg+P3sg(+sI[ü])+Acc(nI[nü])");
        assertParseCorrect("adamkatlini", "adamkatl(adamkatli)+Noun+A3sg+P3sg(+sI[i])+Acc(nI[ni])");
    }

    @Test
    public void should_parse_with_possible_voicing_and_explicit_inverse_harmony() {
        assertParseCorrect("saçmakelime_abine", "saçmakelime_ab(saçmakelime_abi)+Noun+A3sg+P3sg(+sI[i])+Dat(nA[ne])");
    }

    @Test
    public void should_parse_with_doubling() {
        assertParseCorrect("yaşhaddinden", "yaşhadd(yaşhaddi)+Noun+A3sg+P3sg(+sI[i])+Abl(ndAn[nden])");
    }

    @Override
    public void assertParseCorrect(String surfaceToParse, String... expectedParseResults) {
        final ArrayList<String> list = Lists.newArrayList(expectedParseResults);
        final HashSet<String> set = new HashSet<String>(list);
        Validate.isTrue(set.size() == list.size(), "There are duplicate items in expected parse results");
        super.assertParseCorrect(surfaceToParse, expectedParseResults);
    }

}
