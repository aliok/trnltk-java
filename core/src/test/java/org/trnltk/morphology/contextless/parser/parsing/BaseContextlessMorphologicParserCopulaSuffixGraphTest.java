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

import org.junit.Test;
import org.trnltk.model.lexicon.PrimaryPos;

// TODO: divide
public abstract class BaseContextlessMorphologicParserCopulaSuffixGraphTest extends BaseContextlessMorphologicParserTest {

    @Test
    public void should_parse_other_categories_to_verbs_zero_transition() {
        //remove some roots for keeping the tests simple!
        removeRoots("elmas", "bent", "bend", "se", "oy");
        removeRootsExceptTheOneWithPrimaryPos("ben", PrimaryPos.Pronoun);

        assertParseCorrectForVerb("elmayım", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[yım])");
        assertParseCorrectForVerb("elmasın", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A2sg(sIn[sın])");
        assertParseCorrectForVerb("elma", "elma(elma)+Noun+A3sg+Pnon+Nom", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("elmayız", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A1pl(+yIz[yız])");
        assertParseCorrectForVerb("elmasınız", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A2pl(sInIz[sınız])");
        assertParseCorrectForVerb("elmalar", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3pl(lAr[lar])", "elma(elma)+Noun+A3pl(lAr[lar])+Pnon+Nom", "elma(elma)+Noun+A3pl(lAr[lar])+Pnon+Nom+Verb+Zero+Pres+A3sg");

        assertParseCorrectForVerb("elmaymışım", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A1sg(+yIm[ım])");
        assertParseCorrectForVerb("elmaymışsın", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A2sg(sIn[sın])");
        assertParseCorrectForVerb("elmaymış", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3sg");
        assertParseCorrectForVerb("elmaymışız", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A1pl(+yIz[ız])");
        assertParseCorrectForVerb("elmaymışsınız", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A2pl(sInIz[sınız])");
        assertParseCorrectForVerb("elmaymışlar", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])");

        assertParseCorrectForVerb("elmaydım", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydı])+A1sg(m[m])");
        assertParseCorrectForVerb("elmaydın", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydı])+A2sg(n[n])");
        assertParseCorrectForVerb("elmaydı", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydı])+A3sg");
        assertParseCorrectForVerb("elmaydık", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydı])+A1pl(!k[k])");
        assertParseCorrectForVerb("elmaydınız", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydı])+A2pl(nIz[nız])");
        assertParseCorrectForVerb("elmaydılar", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydı])+A3pl(lAr[lar])");

        assertParseCorrectForVerb("elmaysam", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[ysa])+A1sg(m[m])");
        assertParseCorrectForVerb("elmaysan", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[ysa])+A2sg(n[n])");
        assertParseCorrectForVerb("elmaysa", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[ysa])+A3sg");
        assertParseCorrectForVerb("elmaysak", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[ysa])+A1pl(!k[k])");
        assertParseCorrectForVerb("elmaysanız", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[ysa])+A2pl(nIz[nız])");
        assertParseCorrectForVerb("elmaysalar", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[ysa])+A3pl(lAr[lar])");

        assertParseCorrectForVerb("elmansam", "elma(elma)+Noun+A3sg+P2sg(+In[n])+Nom+Verb+Zero+Cond(+ysA[sa])+A1sg(m[m])");
        assertParseCorrectForVerb("elmamsa", "elma(elma)+Noun+A3sg+P1sg(+Im[m])+Nom+Verb+Zero+Cond(+ysA[sa])+A3sg");
        assertParseCorrectForVerb("elmamdın", "elma(elma)+Noun+A3sg+P1sg(+Im[m])+Nom+Verb+Zero+Past(+ydI[dı])+A2sg(n[n])");
        assertParseCorrectForVerb("elmanızdık", "elma(elma)+Noun+A3sg+P2pl(+InIz[nız])+Nom+Verb+Zero+Past(+ydI[dı])+A1pl(!k[k])");
        assertParseCorrectForVerb("elmamızmışsınız", "elma(elma)+Noun+A3sg+P1pl(+ImIz[mız])+Nom+Verb+Zero+Narr(+ymIş[mış])+A2pl(sInIz[sınız])");
        assertParseCorrectForVerb("elmalarınızsalar", "elma(elma)+Noun+A3pl(lAr[lar])+P2pl(+InIz[ınız])+Nom+Verb+Zero+Cond(+ysA[sa])+A3pl(lAr[lar])");

        assertParseCorrectForVerb("iyiyim", "iyi(iyi)+Adj+Verb+Zero+Pres+A1sg(+yIm[yim])", "iyi(iyi)+Adj+Adv+Zero+Verb+Zero+Pres+A1sg(+yIm[yim])", "iyi(iyi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[yim])");
        assertParseCorrectForVerb("küçüğümüzdeyseler", "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1pl(+ImIz[ümüz])+Loc(dA[de])+Verb+Zero+Cond(+ysA[yse])+A3pl(lAr[ler])");
        assertParseCorrectForVerb("küçüklerimizindiler", "küçük(küçük)+Adj+Noun+Zero+A3pl(lAr[ler])+P1pl(+ImIz[imiz])+Gen(+nIn[in])+Verb+Zero+Past(+ydI[di])+A3pl(lAr[ler])");
        assertParseCorrectForVerb("küçüğüm",
                "küçüğ(küçük)+Adj+Verb+Zero+Pres+A1sg(+yIm[üm])",                          // ben kucugum.
                "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1sg(+Im[üm])+Nom",                       // kucugum geldi.
                "küçüğ(küçük)+Adj+Adv+Zero+Verb+Zero+Pres+A1sg(+yIm[üm])",                        // TODO: sacma
                "küçüğ(küçük)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[üm])",  // ben kucugum.
                "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1sg(+Im[üm])+Nom+Verb+Zero+Pres+A3sg");   // -kim geldi? -kucugum
        assertParseCorrectForVerb("bendim", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Past(+ydI[di])+A1sg(m[m])");
        assertParseCorrectForVerb("benim",
                "ben(ben)+Pron+Pers+A1sg+Pnon+Gen(im[im])",                         // benim kitabim.
                "ben(ben)+Pron+Pers+A1sg+Pnon+Gen(im[im])+Verb+Zero+Pres+A3sg",     // -kimin o? -benim (benim kitabim).
                "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[im])"    // -kim o?   -benim (ben geldim).
        );
        assertParseCorrectForVerb("sensin", "sen(sen)+Pron+Pers+A2sg+Pnon+Nom+Verb+Zero+Pres+A2sg(sIn[sin])");
        assertParseCorrectForVerb("oydu", "o(o)+Pron+Pers+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydu])+A3sg", "o(o)+Pron+Demons+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydu])+A3sg");
        assertParseCorrectForVerb("hızlıcaymışlar",
                "hızlı(hızlı)+Adj+Adj+Equ(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Adj+Quite(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Adv+Ly(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Adj+Equ(cA[ca])+Adv+Zero+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Adj+Quite(cA[ca])+Adv+Zero+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Adj+Equ(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Adj+Quite(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Adv+Ly(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Adj+Equ(cA[ca])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Adj+Quite(cA[ca])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Adj+Equ(cA[ca])+Adv+Zero+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Adj+Quite(cA[ca])+Adv+Zero+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ca])+Adv+Zero+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ca])+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Adj+Equ(cA[ca])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Adj+Quite(cA[ca])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hızlı(hızlı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ca])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ca])+Adv+Zero+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "hız(hız)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lı])+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ca])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])"
        );

    }

    @Test
    public void should_parse_copula_derivations() {
        removeRoots("elmas", "on", "se");

        assertParseCorrectForVerb("elmayken", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("elmasıyken", "elma(elma)+Noun+A3sg+P3sg(+sI[sı])+Nom+Verb+Zero+Adv+While(+yken[yken])", "elma(elma)+Noun+A3sg+P3sg(+sI[sı])+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("kitapken", "kitap(kitap)+Noun+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])", "kitap(kitap)+Noun+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("kitaplarıyken", "kitap(kitap)+Noun+A3sg+P3pl(lAr!I[ları])+Nom+Verb+Zero+Adv+While(+yken[yken])", "kitap(kitap)+Noun+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])+Verb+Zero+Adv+While(+yken[yken])", "kitap(kitap)+Noun+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom+Verb+Zero+Adv+While(+yken[yken])", "kitap(kitap)+Noun+A3pl(lAr[lar])+P3pl(!I[ı])+Nom+Verb+Zero+Adv+While(+yken[yken])", "kitap(kitap)+Noun+A3sg+P3pl(lAr!I[ları])+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg", "kitap(kitap)+Noun+A3pl(lAr[lar])+Pnon+Acc(+yI[ı])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg", "kitap(kitap)+Noun+A3pl(lAr[lar])+P3sg(+sI[ı])+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg", "kitap(kitap)+Noun+A3pl(lAr[lar])+P3pl(!I[ı])+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("küçükken",
                "küçük(küçük)+Adj+Verb+Zero+Adv+While(+yken[ken])",
                "küçük(küçük)+Adj+Verb+Zero+Adv+While(+yken[ken])+Verb+Zero+Pres+A3sg",
                "küçük(küçük)+Adj+Adv+Zero+Verb+Zero+Adv+While(+yken[ken])",
                "küçük(küçük)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])",
                "küçük(küçük)+Adj+Adv+Zero+Verb+Zero+Adv+While(+yken[ken])+Verb+Zero+Pres+A3sg",   // TODO: sacma
                "küçük(küçük)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("küçüğümüzdeyken", "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1pl(+ImIz[ümüz])+Loc(dA[de])+Verb+Zero+Adv+While(+yken[yken])", "küçüğ(küçük)+Adj+Noun+Zero+A3sg+P1pl(+ImIz[ümüz])+Loc(dA[de])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("maviceyken",
                "mavi(mavi)+Adj+Adj+Equ(cA[ce])+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adj+Quite(cA[ce])+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adv+Ly(cA[ce])+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adj+Equ(cA[ce])+Adv+Zero+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adj+Quite(cA[ce])+Adv+Zero+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ce])+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ce])+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adj+Equ(cA[ce])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Adj+Quite(cA[ce])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Adv+Ly(cA[ce])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Adj+Equ(cA[ce])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adj+Quite(cA[ce])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Adv+Zero+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adj+Equ(cA[ce])+Adv+Zero+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Adj+Quite(cA[ce])+Adv+Zero+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ce])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ce])+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])",
                "mavi(mavi)+Adj+Adj+Equ(cA[ce])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Adj+Quite(cA[ce])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Adv+Zero+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg",
                "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ce])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Adv+While(+yken[yken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("seninken", "sen(sen)+Pron+Pers+A2sg+Pnon+Gen(in[in])+Verb+Zero+Adv+While(+yken[ken])", "sen(sen)+Pron+Pers+A2sg+Pnon+Gen(in[in])+Verb+Zero+Adv+While(+yken[ken])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("onlarken", "o(o)+Pron+Pers+A3pl(nlar[nlar])+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])", "o(o)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])", "o(o)+Pron+Pers+A3pl(nlar[nlar])+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])+Verb+Zero+Pres+A3sg", "o(o)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Nom+Verb+Zero+Adv+While(+yken[ken])+Verb+Zero+Pres+A3sg");

        assertParseCorrectForVerb("güneşçesine",
                "güneş(güneş)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+Adv+AsIf(cAs!InA[çesine])",
                "güneş(güneş)+Noun+A3sg+Pnon+Nom+Adj+Equ(cA[çe])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])",
                "güneş(güneş)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+Adv+AsIf(cAs!InA[çesine])+Verb+Zero+Pres+A3sg",
                "güneş(güneş)+Noun+A3sg+Pnon+Nom+Adj+Equ(cA[çe])+Noun+Zero+A3sg+P3sg(+sI[si])+Dat(nA[ne])+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("güneşiymişçesine",
                "güneş(güneş)+Noun+A3sg+P3sg(+sI[i])+Nom+Verb+Zero+Narr(+ymIş[ymiş])+Adv+AsIf(cAs!InA[çesine])",
                "güneş(güneş)+Noun+A3sg+Pnon+Acc(+yI[i])+Verb+Zero+Narr(+ymIş[ymiş])+Adv+AsIf(cAs!InA[çesine])",
                "güneş(güneş)+Noun+A3sg+P3sg(+sI[i])+Nom+Verb+Zero+Narr(+ymIş[ymiş])+Adv+AsIf(cAs!InA[çesine])+Verb+Zero+Pres+A3sg",
                "güneş(güneş)+Noun+A3sg+Pnon+Acc(+yI[i])+Verb+Zero+Narr(+ymIş[ymiş])+Adv+AsIf(cAs!InA[çesine])+Verb+Zero+Pres+A3sg");
    }

    @Test
    public void should_parse_verb_degil() {
        assertParseCorrectForVerb("değil", "de\u011fil(de\u011fil)+Conj", "değil(değil)+Verb+Pres+A3sg");
        assertParseCorrectForVerb("değilim", "değil(değil)+Verb+Pres+A1sg(+yIm[im])");
        assertParseCorrectForVerb("değilsin", "değil(değil)+Verb+Pres+A2sg(sIn[sin])");
        assertParseCorrectForVerb("değildik", "değil(değil)+Verb+Past(+ydI[di])+A1pl(!k[k])");
        assertParseCorrectForVerb("değilmişsiniz", "değil(değil)+Verb+Narr(+ymIş[miş])+A2pl(sInIz[siniz])");
        assertParseCorrectForVerb("değildiler", "değil(değil)+Verb+Past(+ydI[di])+A3pl(lAr[ler])");
        assertParseCorrectForVerb("değilseler", "değil(değil)+Verb+Cond(+ysA[se])+A3pl(lAr[ler])");
        //TODO: degillerdi, degillerse, degillermis

    }

    @Test
    public void should_parse_verbs_with_explicit_copula() {
        // remove some roots to keep tests simple
        removeRoots("on", "gelecek");
        removeRootsExceptTheOneWithPrimaryPos("ben", PrimaryPos.Pronoun);
        assertParseCorrectForVerb("elmadır", "elma(elma)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dır])");
        assertParseCorrectForVerb("müdürdür", "müdür(müdür)+Noun+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dür])");
        assertParseCorrectForVerb("zilidir", "zil(zil)+Noun+A3sg+Pnon+Acc(+yI[i])+Verb+Zero+Pres+A3sg+Cop(dIr[dir])", "zil(zil)+Noun+A3sg+P3sg(+sI[i])+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dir])");
        assertParseCorrectForVerb("mavidir", "mavi(mavi)+Adj+Verb+Zero+Pres+A3sg+Cop(dIr[dir])", "mavi(mavi)+Adj+Adv+Zero+Verb+Zero+Pres+A3sg+Cop(dIr[dir])", "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dir])");
        assertParseCorrectForVerb("mavisindir", "mavi(mavi)+Adj+Verb+Zero+Pres+A2sg(sIn[sin])+Cop(dIr[dir])", "mavi(mavi)+Adj+Adv+Zero+Verb+Zero+Pres+A2sg(sIn[sin])+Cop(dIr[dir])", "mavi(mavi)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A2sg(sIn[sin])+Cop(dIr[dir])");
        assertParseCorrectForVerb("benimdir", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[im])+Cop(dIr[dir])", "ben(ben)+Pron+Pers+A1sg+Pnon+Gen(im[im])+Verb+Zero+Pres+A3sg+Cop(dIr[dir])");
        assertParseCorrectForVerb("onlardır", "o(o)+Pron+Pers+A3pl(nlar[nlar])+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dır])", "o(o)+Pron+Demons+A3pl(nlar[nlar])+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dır])");
        assertParseCorrectForVerb("benimledir", "ben(ben)+Pron+Pers+A1sg+Pnon+Ins(imle[imle])+Verb+Zero+Pres+A3sg+Cop(dIr[dir])");
        assertParseCorrectForVerb("sıcakçayımdır",
                "sıcak(sıcak)+Adj+Adj+Equ(cA[ça])+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Adj+Quite(cA[ça])+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Adv+Ly(cA[ça])+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Adj+Equ(cA[ça])+Adv+Zero+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Adj+Quite(cA[ça])+Adv+Zero+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ça])+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+InTermsOf(cA[ça])+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adv+By(cA[ça])+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Adj+Equ(cA[ça])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Adj+Quite(cA[ça])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ça])+Adv+Zero+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])",
                "sıcak(sıcak)+Adj+Noun+Zero+A3sg+Pnon+Nom+Adj+Equ(cA[ça])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[yım])+Cop(dIr[dır])"
        );
        assertParseCorrectForVerb("gelmektedir", "gel(gelmek)+Verb+Pos+Prog(mAktA[mekte])+A3sg+Cop(dIr[dir])", "gel(gelmek)+Verb+Pos+Noun+Inf(mAk[mek])+A3sg+Pnon+Loc(dA[te])+Verb+Zero+Pres+A3sg+Cop(dIr[dir])");
        assertParseCorrectForVerb("geliyorlardır", "gel(gelmek)+Verb+Pos+Prog(Iyor[iyor])+A3pl(lAr[lar])+Cop(dIr[dır])");
        assertParseCorrectForVerb("gelmiştir", "gel(gelmek)+Verb+Pos+Narr(mIş[miş])+A3sg+Cop(dIr[tir])", "gel(gelmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Verb+Zero+Pres+A3sg+Cop(dIr[tir])", "gel(gelmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Adv+Zero+Verb+Zero+Pres+A3sg+Cop(dIr[tir])", "gel(gelmek)+Verb+Pos+Narr(mIş[miş])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[tir])");
        assertParseCorrectForVerb("geleceksinizdir", "gel(gelmek)+Verb+Pos+Fut(+yAcAk[ecek])+A2pl(sInIz[siniz])+Cop(dIr[dir])", "gel(gelmek)+Verb+Pos+Adj+FutPart(+yAcAk[ecek])+Pnon+Verb+Zero+Pres+A2pl(sInIz[siniz])+Cop(dIr[dir])", "gel(gelmek)+Verb+Pos+Fut(+yAcAk[ecek])+Adj+Zero+Verb+Zero+Pres+A2pl(sInIz[siniz])+Cop(dIr[dir])", "gel(gelmek)+Verb+Pos+Fut(+yAcAk[ecek])+Adj+Zero+Adv+Zero+Verb+Zero+Pres+A2pl(sInIz[siniz])+Cop(dIr[dir])", "gel(gelmek)+Verb+Pos+Noun+FutPart(+yAcAk[ecek])+A3sg+Pnon+Nom+Verb+Zero+Pres+A2pl(sInIz[siniz])+Cop(dIr[dir])", "gel(gelmek)+Verb+Pos+Fut(+yAcAk[ecek])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A2pl(sInIz[siniz])+Cop(dIr[dir])");
        assertParseCorrectForVerb("gelmelilerdir",
                "gel(gelmek)+Verb+Pos+Neces(mAl!I[meli])+A3pl(lAr[ler])+Cop(dIr[dir])",
                "gel(gelmek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom+Adj+With(lI[li])+Verb+Zero+Pres+A3pl(lAr[ler])+Cop(dIr[dir])",
                "gel(gelmek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom+Adj+With(lI[li])+Adv+Zero+Verb+Zero+Pres+A3pl(lAr[ler])+Cop(dIr[dir])",
                "gel(gelmek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom+Adj+With(lI[li])+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A3pl(lAr[ler])+Cop(dIr[dir])",
                "gel(gelmek)+Verb+Pos+Noun+Inf(mA[me])+A3sg+Pnon+Nom+Adj+With(lI[li])+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dir])"
        );
        assertParseCorrectForVerb("değildir", "değil(değil)+Verb+Pres+A3sg+Cop(dIr[dir])");
        assertParseCorrectForVerb("değillerdir", "değil(değil)+Verb+Pres+A3pl(lAr[ler])+Cop(dIr[dir])");
        assertParseCorrectForVerb("mıdır", "mı(mı)+Ques+Pres+A3sg+Cop(dIr[dır])");
        assertParseCorrectForVerb("mıyımdır", "mı(mı)+Ques+Pres+A1sg(yım[yım])+Cop(dIr[dır])");

    }

    @Test
    public void should_parse_adjectives_as_adverbs() {
        assertParseExists("mavi", "mavi(mavi)+Adj+Adv+Zero");
        assertParseExists("yapan", "yap(yapmak)+Verb+Pos+Adj+PresPart(+yAn[an])+Adv+Zero");
        assertParseExists("kesici", "kes(kesmek)+Verb+Pos+Adj+Agt(+yIcI[ici])+Adv+Zero");
        assertParseExists("pembemsi", "pembe(pembe)+Adj+Adj+JustLike(+ImsI[msi])+Adv+Zero");
        assertParseExists("delice", "deli(deli)+Adj+Adj+Equ(cA[ce])+Adv+Zero");
    }

    @Test
    public void shouldParseSomeProblematicWords() {
        assertNotParsable("kitapdı");
        assertParseCorrectForVerb("kitaptı", "kitap(kitap)+Noun+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[tı])+A3sg");

        assertNotParsable("yokdu");
        assertParseCorrectForVerb("yoktu", "yok(yok)+Adj+Verb+Zero+Past(+ydI[tu])+A3sg", "yok(yok)+Adj+Adv+Zero+Verb+Zero+Past(+ydI[tu])+A3sg", "yok(yok)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[tu])+A3sg");

        assertParseExists("alınmalıdır", "alın(alınmak)+Verb+Pos+Neces(mAl!I[malı])+A3sg+Cop(dIr[dır])");
        assertParseExists("alınmalılardır", "alın(alınmak)+Verb+Pos+Neces(mAl!I[malı])+A3pl(lAr[lar])+Cop(dIr[dır])");
        assertParseExists("alınmalıdırlar", "alın(alınmak)+Verb+Pos+Neces(mAl!I[malı])+Cop(dIr[dır])+A3pl(lAr[lar])");
    }

    @Test
    public void shouldParseCopA3plSwap_NonVerbs() {
        removeRoots("sar");

        assertParseCorrectForVerb("sarıydılar",
                "sarı(sarı)+Adj+Verb+Zero+Past(+ydI[ydı])+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Adv+Zero+Verb+Zero+Past(+ydI[ydı])+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[ydı])+A3pl(lAr[lar])");
        assertParseCorrectForVerb("sarılardı", "sarı(sarı)+Adj+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Verb+Zero+Past(+ydI[dı])+A3sg");

        assertParseCorrectForVerb("sarıymışlar",
                "sarı(sarı)+Adj+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Adv+Zero+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[ymış])+A3pl(lAr[lar])");
        assertParseCorrectForVerb("sarılarmış", "sarı(sarı)+Adj+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Verb+Zero+Narr(+ymIş[mış])+A3sg");

        assertParseCorrectForVerb("sarıysalar",
                "sarı(sarı)+Adj+Verb+Zero+Cond(+ysA[ysa])+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Adv+Zero+Verb+Zero+Cond(+ysA[ysa])+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[ysa])+A3pl(lAr[lar])");
        assertParseCorrectForVerb("sarılarsa", "sarı(sarı)+Adj+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Verb+Zero+Cond(+ysA[sa])+A3sg");

        assertParseCorrectForVerb("sarıdırlar",
                "sarı(sarı)+Adj+Verb+Zero+Cop(dIr[dır])+Verb+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Adv+Zero+Verb+Zero+Cop(dIr[dır])+Verb+A3pl(lAr[lar])",
                "sarı(sarı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Cop(dIr[dır])+Verb+A3pl(lAr[lar])");
        assertParseCorrectForVerb("sarılardır",
                "sarı(sarı)+Adj+Verb+Zero+Pres+A3pl(lAr[lar])+Cop(dIr[dır])",
                "sarı(sarı)+Adj+Adv+Zero+Verb+Zero+Pres+A3pl(lAr[lar])+Cop(dIr[dır])",
                "sarı(sarı)+Adj+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Verb+Zero+Pres+A3sg+Cop(dIr[dır])",
                "sarı(sarı)+Adj+Noun+Zero+A3sg+Pnon+Nom+Verb+Zero+Pres+A3pl(lAr[lar])+Cop(dIr[dır])");
    }

    @Test
    public void shouldParseVerbA3plSwap() {
        //Past+Copula NOT SUPPORTED
        //assertParseCorrect("bekledilerdir", "");
        //assertParseCorrect("beklediydirler", "");

        //Cond+Copula NOT APPLICABLE, becomes Desr
        //assertParseCorrect("bekleseydirler", "");
        //assertParseCorrect("bekleselerdir", "");

        //Neces+Copula
        assertParseExists("beklemelilerdir", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+A3pl(lAr[ler])+Cop(dIr[dir])");
        assertParseExists("beklemelidirler", "bekle(beklemek)+Verb+Pos+Neces(mAl!I[meli])+Cop(dIr[dir])+A3pl(lAr[ler])");

        //Opt+Copula NOT APPLICABLE
        //assertParseCorrect("bekleyelerdir", "bekle(beklemek)+Verb+Pos+Opt(yA[ye])+A3pl(lAr[ler])+Past(dI[di])");
        //assertParseCorrect("bekleyeydirler", "bekle(beklemek)+Verb+Pos+Opt(yA[ye])+Past(ydI[ydi])+A3pl(lAr[ler])");

        //Desr+Copula NOT APPLICABLE
        //assertParseCorrect("bekleselerdir", "bekle(beklemek)+Verb+Pos+Desr(sA[se])+A3pl(lAr[ler])+Past(dI[di])");
        //assertParseCorrect("beklesedirler", "bekle(beklemek)+Verb+Pos+Desr(sA[se])+Past(ydI[ydi])+A3pl(lAr[ler])");

        //Aor+Copula NOT APPLICABLE (actually, it is applicable through Adj Aorists)
        //assertParseCorrect("beklerlerdir", "bekle(beklemek)+Verb+Pos+Aor(+Ar[r])+A3pl(lAr[ler])+Cop(dIr[dir])", "bekle(beklemek)+Verb+Pos+Aor(+Ir[r])+A3pl(lAr[ler])+Cop(dIr[dir])");
        //assertParseCorrect("beklerdirler", "bekle(beklemek)+Verb+Pos+Aor(+Ar[r])+Cop(dIr[dir])+A3pl(lAr[ler])", "bekle(beklemek)+Verb+Pos+Aor(+Ir[r])+Cop(dIr[dir])+A3pl(lAr[ler])");

        //Prog+Copula
        assertParseCorrectForVerb("bekliyorlardır", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+A3pl(lAr[lar])+Cop(dIr[dır])");
        assertParseCorrectForVerb("bekliyordurlar", "bekl(beklemek)+Verb+Pos+Prog(Iyor[iyor])+Cop(dIr[dur])+A3pl(lAr[lar])");

        //Fut+Copula
        assertParseExists("bekleyeceklerdir", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+A3pl(lAr[ler])+Cop(dIr[dir])");
        assertParseExists("bekleyecektirler", "bekle(beklemek)+Verb+Pos+Fut(+yAcAk[yecek])+Cop(dIr[tir])+A3pl(lAr[ler])");

        //Narr+Copula
        assertParseExists("beklemişlerdir", "bekle(beklemek)+Verb+Pos+Narr(mIş[miş])+A3pl(lAr[ler])+Cop(dIr[dir])");
        assertParseExists("beklemiştirler", "bekle(beklemek)+Verb+Pos+Narr(mIş[miş])+Cop(dIr[tir])+A3pl(lAr[ler])");
    }

    @Test
    public void should_parse_pronoun_tenses() {
        // remove some roots to make the test simple
        removeRoots("bend", "kimi", "kimse");
        removeRootsExceptTheOneWithPrimaryPos("ben", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("ban", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("san", PrimaryPos.Pronoun);
        removeRootsExceptTheOneWithPrimaryPos("biz", PrimaryPos.Pronoun);

        assertParseExists("benim", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[im])");
        assertParseCorrectForVerb("bendim", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Past(+ydI[di])+A1sg(m[m])");
        assertParseCorrectForVerb("benmişim", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[miş])+A1sg(+yIm[im])");

        assertParseCorrectForVerb("bensem", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Cond(+ysA[se])+A1sg(m[m])");
        assertParseCorrectForVerb("bense", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Cond(+ysA[se])+A3sg");
        assertParseCorrectForVerb("bendiyse", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Past(+ydI[di])+Cond(+ysA[yse])+A3sg");
        //        assertParseCorrectForVerb("bendimse",           "xxxx")   TODO
        assertParseCorrectForVerb("bendiysem", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Past(+ydI[di])+Cond(+ysA[yse])+A1sg(m[m])");
        assertParseCorrectForVerb("benmişsem", "ben(ben)+Pron+Pers+A1sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[miş])+Cond(+ysA[se])+A1sg(m[m])");

        assertParseCorrectForVerb("beniyse", "ben(ben)+Pron+Pers+A1sg+Pnon+Acc(i[i])+Verb+Zero+Cond(+ysA[yse])+A3sg");
        assertParseCorrectForVerb("banaymışsa", "ban(ben)+Pron+Pers+A1sg+Pnon+Dat(a[a])+Verb+Zero+Narr(+ymIş[ymış])+Cond(+ysA[sa])+A3sg");
        assertParseCorrectForVerb("bendeymişseler", "ben(ben)+Pron+Pers+A1sg+Pnon+Loc(de[de])+Verb+Zero+Narr(+ymIş[ymiş])+Cond(+ysA[se])+A3pl(lAr[ler])");
        assertParseCorrectForVerb("bendendiyse", "ben(ben)+Pron+Pers+A1sg+Pnon+Abl(den[den])+Verb+Zero+Past(+ydI[di])+Cond(+ysA[yse])+A3sg");
        assertParseCorrectForVerb("benimleydiysen", "ben(ben)+Pron+Pers+A1sg+Pnon+Ins(imle[imle])+Verb+Zero+Past(+ydI[ydi])+Cond(+ysA[yse])+A2sg(n[n])");
        assertParseCorrectForVerb("benimleymişseler", "ben(ben)+Pron+Pers+A1sg+Pnon+Ins(imle[imle])+Verb+Zero+Narr(+ymIş[ymiş])+Cond(+ysA[se])+A3pl(lAr[ler])");
        //        assertParseCorrectForVerb("benimleymişlerse",   "xxxx")  TODO

        assertParseCorrectForVerb("kimim", "kim(kim)+Pron+Ques+A3sg+P1sg(+Im[im])+Nom", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Pres+A1sg(+yIm[im])", "kim(kim)+Pron+Ques+A3sg+P1sg(+Im[im])+Nom+Verb+Zero+Pres+A3sg");
        assertParseCorrectForVerb("kimdim", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[di])+A1sg(m[m])");
        assertParseCorrectForVerb("kimmişim", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[miş])+A1sg(+yIm[im])");

        assertParseCorrectForVerb("kimsem", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[se])+A1sg(m[m])");
        assertParseCorrectForVerb("kimse", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Cond(+ysA[se])+A3sg");
        assertParseCorrectForVerb("kimdiyse", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[di])+Cond(+ysA[yse])+A3sg");
        //        assertParseCorrectForVerb("kimdimse",           "xxxx") TODO
        assertParseCorrectForVerb("kimdiysem", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Past(+ydI[di])+Cond(+ysA[yse])+A1sg(m[m])");
        assertParseCorrectForVerb("kimmişsem", "kim(kim)+Pron+Ques+A3sg+Pnon+Nom+Verb+Zero+Narr(+ymIş[miş])+Cond(+ysA[se])+A1sg(m[m])");

        assertParseCorrectForVerb("kimiyse", "kim(kim)+Pron+Ques+A3sg+Pnon+Acc(+yI[i])+Verb+Zero+Cond(+ysA[yse])+A3sg", "kim(kim)+Pron+Ques+A3sg+P3sg(+sI[i])+Nom+Verb+Zero+Cond(+ysA[yse])+A3sg");
        assertParseCorrectForVerb("kimeymişse", "kim(kim)+Pron+Ques+A3sg+Pnon+Dat(+yA[e])+Verb+Zero+Narr(+ymIş[ymiş])+Cond(+ysA[se])+A3sg");
        assertParseCorrectForVerb("kimdeymişse", "kim(kim)+Pron+Ques+A3sg+Pnon+Loc(dA[de])+Verb+Zero+Narr(+ymIş[ymiş])+Cond(+ysA[se])+A3sg");
        assertParseCorrectForVerb("kimdendiyse", "kim(kim)+Pron+Ques+A3sg+Pnon+Abl(dAn[den])+Verb+Zero+Past(+ydI[di])+Cond(+ysA[yse])+A3sg");
        assertParseCorrectForVerb("kimlerdendiyse", "kim(kim)+Pron+Ques+A3pl(lAr[ler])+Pnon+Abl(dAn[den])+Verb+Zero+Past(+ydI[di])+Cond(+ysA[yse])+A3sg");
        assertParseCorrectForVerb("kimimleydiysen", "kim(kim)+Pron+Ques+A3sg+P1sg(+Im[im])+Ins(+ylA[le])+Verb+Zero+Past(+ydI[ydi])+Cond(+ysA[yse])+A2sg(n[n])");
        //        assertParseCorrectForVerb("kimimleymişlerse",   "xxxx") TODO
    }
}
