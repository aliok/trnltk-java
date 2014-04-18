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

package org.trnltk.morphology.contextless.parser.parsing.base;

import org.junit.Test;

public abstract class BaseContextlessMorphologicParserNumeralSuffixGraphTest extends BaseContextlessMorphologicParserTest {

    @Test
    public void shouldParseNumeralsToAdjectiveDerivations() {
        assertParseCorrect("onlarca",
                "on(on)+Num+Card+Adj+NumbersOf(lArcA[larca])",
                "on(on)+Num+Card+Adj+NumbersOf(lArcA[larca])+Noun+Zero+A3sg+Pnon+Nom",
                "on(on)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Adv+ManyOf(lArcA[larca])",
                "on(on)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Adj+Equ(cA[ca])",
                "on(on)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Adv+InTermsOf(cA[ca])",
                "on(on)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Adv+By(cA[ca])",
                "on(on)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[lar])+Pnon+Nom+Adj+Equ(cA[ca])+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("binlerce",
                "bin(bin)+Num+Card+Adj+NumbersOf(lArcA[lerce])",
                "bin(bin)+Num+Card+Adj+NumbersOf(lArcA[lerce])+Noun+Zero+A3sg+Pnon+Nom",
                "bin(bin)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom+Adv+ManyOf(lArcA[lerce])",
                "bin(bin)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom+Adj+Equ(cA[ce])",
                "bin(bin)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom+Adv+InTermsOf(cA[ce])",
                "bin(bin)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom+Adv+By(cA[ce])",
                "bin(bin)+Num+Card+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom+Adj+Equ(cA[ce])+Noun+Zero+A3sg+Pnon+Nom");

        assertParseExists("milyarlık", "milyar(milyar)+Num+Card+Adj+OfUnit(lIk[lık])");
        assertParseExists("ellilik", "elli(elli)+Num+Card+Adj+OfUnit(lIk[lik])");
    }

    @Test
    public void shouldParseCardinalNumerals() {
        assertParseCorrect("sıfır", "sıfır(sıfır)+Num+Card+Adj+Zero", "sıfır(sıfır)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("bir", "bir(bir)+Num+Card+Adj+Zero", "bir(bir)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("iki", "iki(iki)+Num+Card+Adj+Zero", "iki(iki)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("üç", "üç(üç)+Num+Card+Adj+Zero", "üç(üç)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("dört", "dört(dört)+Num+Card+Adj+Zero", "dört(dört)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("beş", "beş(beş)+Num+Card+Adj+Zero", "beş(beş)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("altı", "altı(altı)+Num+Card+Adj+Zero", "altı(altı)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yedi", "yedi(yedi)+Num+Card+Adj+Zero", "yedi(yedi)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("sekiz", "sekiz(sekiz)+Num+Card+Adj+Zero", "sekiz(sekiz)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("dokuz", "dokuz(dokuz)+Num+Card+Adj+Zero", "dokuz(dokuz)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("on", "on(on)+Num+Card+Adj+Zero", "on(on)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yirmi", "yirmi(yirmi)+Num+Card+Adj+Zero", "yirmi(yirmi)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("otuz", "otuz(otuz)+Num+Card+Adj+Zero", "otuz(otuz)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("kırk", "kırk(kırk)+Num+Card+Adj+Zero", "kırk(kırk)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("elli", "elli(elli)+Num+Card+Adj+Zero", "elli(elli)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("altmış", "altmış(altmış)+Num+Card+Adj+Zero", "altmış(altmış)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yetmiş", "yetmiş(yetmiş)+Num+Card+Adj+Zero", "yetmiş(yetmiş)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("seksen", "seksen(seksen)+Num+Card+Adj+Zero", "seksen(seksen)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("doksan", "doksan(doksan)+Num+Card+Adj+Zero", "doksan(doksan)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yüz", "yüz(yüz)+Num+Card+Adj+Zero", "yüz(yüz)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("bin", "bin(bin)+Num+Card+Adj+Zero", "bin(bin)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("milyon", "milyon(milyon)+Num+Card+Adj+Zero", "milyon(milyon)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("milyar", "milyar(milyar)+Num+Card+Adj+Zero", "milyar(milyar)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("trilyon", "trilyon(trilyon)+Num+Card+Adj+Zero", "trilyon(trilyon)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("katrilyon", "katrilyon(katrilyon)+Num+Card+Adj+Zero", "katrilyon(katrilyon)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("kentilyon", "kentilyon(kentilyon)+Num+Card+Adj+Zero", "kentilyon(kentilyon)+Num+Card+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

    }

    @Test
    public void shouldParseOrdinalNumerals() {
        assertParseCorrect("sıfırıncı", "sıfırıncı(sıfırıncı)+Num+Ord+Adj+Zero", "sıfırıncı(sıfırıncı)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("birinci", "birinci(birinci)+Num+Ord+Adj+Zero", "birinci(birinci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("ikinci", "ikinci(ikinci)+Num+Ord+Adj+Zero", "ikinci(ikinci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("üçüncü", "üçüncü(üçüncü)+Num+Ord+Adj+Zero", "üçüncü(üçüncü)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("dördüncü", "dördüncü(dördüncü)+Num+Ord+Adj+Zero", "dördüncü(dördüncü)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("beşinci", "beşinci(beşinci)+Num+Ord+Adj+Zero", "beşinci(beşinci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("altıncı", "altıncı(altıncı)+Num+Ord+Adj+Zero", "altıncı(altıncı)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yedinci", "yedinci(yedinci)+Num+Ord+Adj+Zero", "yedinci(yedinci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("sekizinci", "sekizinci(sekizinci)+Num+Ord+Adj+Zero", "sekizinci(sekizinci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("dokuzuncu", "dokuzuncu(dokuzuncu)+Num+Ord+Adj+Zero", "dokuzuncu(dokuzuncu)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("onuncu", "onuncu(onuncu)+Num+Ord+Adj+Zero", "onuncu(onuncu)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yirminci", "yirminci(yirminci)+Num+Ord+Adj+Zero", "yirminci(yirminci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("otuzuncu", "otuzuncu(otuzuncu)+Num+Ord+Adj+Zero", "otuzuncu(otuzuncu)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("kırkıncı", "kırkıncı(kırkıncı)+Num+Ord+Adj+Zero", "kırkıncı(kırkıncı)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("ellinci", "ellinci(ellinci)+Num+Ord+Adj+Zero", "ellinci(ellinci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("altmışıncı", "altmışıncı(altmışıncı)+Num+Ord+Adj+Zero", "altmışıncı(altmışıncı)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yetmişinci", "yetmişinci(yetmişinci)+Num+Ord+Adj+Zero", "yetmişinci(yetmişinci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("sekseninci", "sekseninci(sekseninci)+Num+Ord+Adj+Zero", "sekseninci(sekseninci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("doksanıncı", "doksanıncı(doksanıncı)+Num+Ord+Adj+Zero", "doksanıncı(doksanıncı)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("yüzüncü", "yüzüncü(yüzüncü)+Num+Ord+Adj+Zero", "yüzüncü(yüzüncü)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("bininci", "bininci(bininci)+Num+Ord+Adj+Zero", "bininci(bininci)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("milyonuncu", "milyonuncu(milyonuncu)+Num+Ord+Adj+Zero", "milyonuncu(milyonuncu)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("milyarıncı", "milyarıncı(milyarıncı)+Num+Ord+Adj+Zero", "milyarıncı(milyarıncı)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("trilyonuncu", "trilyonuncu(trilyonuncu)+Num+Ord+Adj+Zero", "trilyonuncu(trilyonuncu)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("katrilyonuncu", "katrilyonuncu(katrilyonuncu)+Num+Ord+Adj+Zero", "katrilyonuncu(katrilyonuncu)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("kentilyonuncu", "kentilyonuncu(kentilyonuncu)+Num+Ord+Adj+Zero", "kentilyonuncu(kentilyonuncu)+Num+Ord+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
    }

    @Test
    public void shouldParseDigits() {
        assertParseCorrectForVerb("0", "0(0)+Num+DigitsC+Adj+Zero", "0(0)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("1", "1(1)+Num+DigitsC+Adj+Zero", "1(1)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-1", "-1(-1)+Num+DigitsC+Adj+Zero", "-1(-1)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("9999999999", "9999999999(9999999999)+Num+DigitsC+Adj+Zero", "9999999999(9999999999)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-9999999999", "-9999999999(-9999999999)+Num+DigitsC+Adj+Zero", "-9999999999(-9999999999)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        // In Turkish, comma is the fraction separator
        assertParseCorrectForVerb("0,0", "0,0(0,0)+Num+DigitsC+Adj+Zero", "0,0(0,0)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("0,1", "0,1(0,1)+Num+DigitsC+Adj+Zero", "0,1(0,1)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-0,0", "-0,0(-0,0)+Num+DigitsC+Adj+Zero", "-0,0(-0,0)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-0,1", "-0,1(-0,1)+Num+DigitsC+Adj+Zero", "-0,1(-0,1)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("0,000000001", "0,000000001(0,000000001)+Num+DigitsC+Adj+Zero", "0,000000001(0,000000001)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-0,000000001", "-0,000000001(-0,000000001)+Num+DigitsC+Adj+Zero", "-0,000000001(-0,000000001)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        // In Turkish, full stop is the grouping separator
        assertParseCorrectForVerb("1.000", "1.000(1.000)+Num+DigitsC+Adj+Zero", "1.000(1.000)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("9.999.999.999.999", "9.999.999.999.999(9.999.999.999.999)+Num+DigitsC+Adj+Zero", "9.999.999.999.999(9.999.999.999.999)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-1.000", "-1.000(-1.000)+Num+DigitsC+Adj+Zero", "-1.000(-1.000)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-9.999.999.999.999", "-9.999.999.999.999(-9.999.999.999.999)+Num+DigitsC+Adj+Zero", "-9.999.999.999.999(-9.999.999.999.999)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrectForVerb("1.000,0001212", "1.000,0001212(1.000,0001212)+Num+DigitsC+Adj+Zero", "1.000,0001212(1.000,0001212)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("9.999.999.999.999,01", "9.999.999.999.999,01(9.999.999.999.999,01)+Num+DigitsC+Adj+Zero", "9.999.999.999.999,01(9.999.999.999.999,01)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-1.000,0001212", "-1.000,0001212(-1.000,0001212)+Num+DigitsC+Adj+Zero", "-1.000,0001212(-1.000,0001212)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrectForVerb("-9.999.999.999.999,01", "-9.999.999.999.999,01(-9.999.999.999.999,01)+Num+DigitsC+Adj+Zero", "-9.999.999.999.999,01(-9.999.999.999.999,01)+Num+DigitsC+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

    }

    @Test
    public void shouldParseDigitsWithSuffixes() {
        assertParseCorrectForVerb("0'ı", "0(0)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "0(0)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1'i", "1(1)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1(1)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("2'si", "2(2)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrectForVerb("3'ü", "3(3)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "3(3)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("4'ü", "4(4)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "4(4)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("5'i", "5(5)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "5(5)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("6'sı", "6(6)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[sı])+Nom");
        assertParseCorrectForVerb("7'si", "7(7)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrectForVerb("8'i", "8(8)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "8(8)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("9'u", "9(9)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "9(9)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 10-99
        assertParseCorrectForVerb("10'u", "10(10)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "10(10)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11'i", "11(11)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11(11)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("20'si", "20(20)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrectForVerb("30'u", "30(30)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "30(30)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("40'ı", "40(40)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "40(40)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("50'si", "50(50)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[si])+Nom");
        assertParseCorrectForVerb("60'ı", "60(60)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "60(60)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("70'i", "70(70)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "70(70)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("80'i", "80(80)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "80(80)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("90'ı", "90(90)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "90(90)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");

        // 100-999
        assertParseCorrectForVerb("100'ü", "100(100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "100(100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("110'u", "110(110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "110(110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111'i", "111(111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111(111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("200'ü", "200(200)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "200(200)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");

        // 1000-9999 (bin)
        assertParseCorrectForVerb("1000'i", "1000(1000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1000(1000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1100'ü", "1100(1100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "1100(1100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("1110'u", "1110(1110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1110(1110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111'i", "1111(1111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111(1111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("2000'i", "2000(2000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "2000(2000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");

        // 10000-99999 (on bin)
        assertParseCorrectForVerb("10000'i", "10000(10000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "10000(10000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11000'i", "11000(11000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11000(11000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11100'ü", "11100(11100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "11100(11100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("11110'u", "11110(11110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11110(11110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111'i", "11111(11111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111(11111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("20000'i", "20000(20000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "20000(20000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");

        // 100000-999999 (yüz bin)
        assertParseCorrectForVerb("100000'i", "100000(100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "100000(100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("110000'i", "110000(110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "110000(110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111000'i", "111000(111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111000(111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111100'ü", "111100(111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "111100(111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("111110'u", "111110(111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111110(111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111'i", "111111(111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111(111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("200000'i", "200000(200000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "200000(200000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");

        // 1000000-9999999 (milyon)
        assertParseCorrectForVerb("1000000'u", "1000000(1000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1000000(1000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1100000'i", "1100000(1100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1100000(1100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1110000'i", "1110000(1110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1110000(1110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111000'i", "1111000(1111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111000(1111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111100'ü", "1111100(1111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "1111100(1111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("1111110'u", "1111110(1111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111110(1111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111'i", "1111111(1111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111(1111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("2000000'u", "2000000(2000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "2000000(2000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 10000000-99999999 (on milyon)
        assertParseCorrectForVerb("10000000'u", "10000000(10000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "10000000(10000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11000000'u", "11000000(11000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11000000(11000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11100000'i", "11100000(11100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11100000(11100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11110000'i", "11110000(11110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11110000(11110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111000'i", "11111000(11111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111000(11111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111100'ü", "11111100(11111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "11111100(11111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("11111110'u", "11111110(11111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111110(11111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111'i", "11111111(11111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111(11111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("20000000'u", "20000000(20000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "20000000(20000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 100000000-999999999 (yüz milyon)
        assertParseCorrectForVerb("100000000'u", "100000000(100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "100000000(100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("110000000'u", "110000000(110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "110000000(110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111000000'u", "111000000(111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111000000(111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111100000'i", "111100000(111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111100000(111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111110000'i", "111110000(111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111110000(111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111000'i", "111111000(111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111000(111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111100'ü", "111111100(111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "111111100(111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("111111110'u", "111111110(111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111110(111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111'i", "111111111(111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111(111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("200000000'u", "200000000(200000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "200000000(200000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 1000000000-9999999999 (milyar)
        assertParseCorrectForVerb("1000000000'ı", "1000000000(1000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "1000000000(1000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1100000000'u", "1100000000(1100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1100000000(1100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1110000000'u", "1110000000(1110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1110000000(1110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111000000'u", "1111000000(1111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111000000(1111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111100000'i", "1111100000(1111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111100000(1111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111110000'i", "1111110000(1111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111110000(1111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111000'i", "1111111000(1111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111000(1111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111100'ü", "1111111100(1111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "1111111100(1111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("1111111110'u", "1111111110(1111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111111110(1111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111111'i", "1111111111(1111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111111(1111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("2000000000'ı", "2000000000(2000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "2000000000(2000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");

        // 10000000000-99999999999 (on milyar)
        assertParseCorrectForVerb("10000000000'ı", "10000000000(10000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "10000000000(10000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11000000000'ı", "11000000000(11000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "11000000000(11000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11100000000'u", "11100000000(11100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11100000000(11100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11110000000'u", "11110000000(11110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11110000000(11110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111000000'u", "11111000000(11111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111000000(11111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111100000'i", "11111100000(11111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111100000(11111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111110000'i", "11111110000(11111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111110000(11111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111000'i", "11111111000(11111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111000(11111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111100'ü", "11111111100(11111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "11111111100(11111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("11111111110'u", "11111111110(11111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111111110(11111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111111'i", "11111111111(11111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111111(11111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("20000000000'ı", "20000000000(20000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "20000000000(20000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");

        // 10000000000-99999999999 (yüz milyar)
        assertParseCorrectForVerb("100000000000'ı", "100000000000(100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "100000000000(100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("110000000000'ı", "110000000000(110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "110000000000(110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111000000000'ı", "111000000000(111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "111000000000(111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111100000000'u", "111100000000(111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111100000000(111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111110000000'u", "111110000000(111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111110000000(111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111000000'u", "111111000000(111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111000000(111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111100000'i", "111111100000(111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111100000(111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111110000'i", "111111110000(111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111110000(111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111000'i", "111111111000(111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111000(111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111100'ü", "111111111100(111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "111111111100(111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("111111111110'u", "111111111110(111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111111110(111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111111'i", "111111111111(111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111111(111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("200000000000'ı", "200000000000(200000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "200000000000(200000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");

        // 100000000000-999999999999 (trilyon)
        assertParseCorrectForVerb("1000000000000'u", "1000000000000(1000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1000000000000(1000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1100000000000'ı", "1100000000000(1100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "1100000000000(1100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1110000000000'ı", "1110000000000(1110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "1110000000000(1110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1111000000000'ı", "1111000000000(1111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "1111000000000(1111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1111100000000'u", "1111100000000(1111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111100000000(1111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111110000000'u", "1111110000000(1111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111110000000(1111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111000000'u", "1111111000000(1111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111111000000(1111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111100000'i", "1111111100000(1111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111100000(1111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111110000'i", "1111111110000(1111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111110000(1111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111111000'i", "1111111111000(1111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111111000(1111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111111100'ü", "1111111111100(1111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "1111111111100(1111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("1111111111110'u", "1111111111110(1111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111111111110(1111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111111111'i", "1111111111111(1111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111111111(1111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("2000000000000'u", "2000000000000(2000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "2000000000000(2000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 1000000000000-9999999999999 (on trilyon)
        assertParseCorrectForVerb("10000000000000'u", "10000000000000(10000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "10000000000000(10000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11000000000000'u", "11000000000000(11000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11000000000000(11000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11100000000000'ı", "11100000000000(11100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "11100000000000(11100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11110000000000'ı", "11110000000000(11110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "11110000000000(11110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11111000000000'ı", "11111000000000(11111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "11111000000000(11111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11111100000000'u", "11111100000000(11111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111100000000(11111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111110000000'u", "11111110000000(11111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111110000000(11111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111000000'u", "11111111000000(11111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111111000000(11111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111100000'i", "11111111100000(11111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111100000(11111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111110000'i", "11111111110000(11111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111110000(11111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111111000'i", "11111111111000(11111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111111000(11111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111111100'ü", "11111111111100(11111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "11111111111100(11111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("11111111111110'u", "11111111111110(11111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111111111110(11111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111111111'i", "11111111111111(11111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111111111(11111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("20000000000000'u", "20000000000000(20000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "20000000000000(20000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 10000000000000-99999999999999 (yüz trilyon)
        assertParseCorrectForVerb("100000000000000'u", "100000000000000(100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "100000000000000(100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("110000000000000'u", "110000000000000(110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "110000000000000(110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111000000000000'u", "111000000000000(111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111000000000000(111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111100000000000'ı", "111100000000000(111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "111100000000000(111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111110000000000'ı", "111110000000000(111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "111110000000000(111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111111000000000'ı", "111111000000000(111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "111111000000000(111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111111100000000'u", "111111100000000(111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111100000000(111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111110000000'u", "111111110000000(111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111110000000(111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111000000'u", "111111111000000(111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111111000000(111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111100000'i", "111111111100000(111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111100000(111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111110000'i", "111111111110000(111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111110000(111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111111000'i", "111111111111000(111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111111000(111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111111100'ü", "111111111111100(111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "111111111111100(111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("111111111111110'u", "111111111111110(111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111111111110(111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111111111'i", "111111111111111(111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111111111(111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("200000000000000'u", "200000000000000(200000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "200000000000000(200000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 100000000000000-999999999999999 (katrilyon)
        assertParseCorrectForVerb("1000000000000000'u", "1000000000000000(1000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1000000000000000(1000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1100000000000000'u", "1100000000000000(1100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1100000000000000(1100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1110000000000000'u", "1110000000000000(1110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1110000000000000(1110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111000000000000'u", "1111000000000000(1111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111000000000000(1111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111100000000000'ı", "1111100000000000(1111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "1111100000000000(1111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1111110000000000'ı", "1111110000000000(1111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "1111110000000000(1111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1111111000000000'ı", "1111111000000000(1111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "1111111000000000(1111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("1111111100000000'u", "1111111100000000(1111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111111100000000(1111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111110000000'u", "1111111110000000(1111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111111110000000(1111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111111000000'u", "1111111111000000(1111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111111111000000(1111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111111100000'i", "1111111111100000(1111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111111100000(1111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111111110000'i", "1111111111110000(1111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111111110000(1111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111111111000'i", "1111111111111000(1111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111111111000(1111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("1111111111111100'ü", "1111111111111100(1111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "1111111111111100(1111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("1111111111111110'u", "1111111111111110(1111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "1111111111111110(1111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("1111111111111111'i", "1111111111111111(1111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "1111111111111111(1111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("2000000000000000'u", "2000000000000000(2000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "2000000000000000(2000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 1000000000000000-9999999999999999 (on katrilyon)
        assertParseCorrectForVerb("10000000000000000'u", "10000000000000000(10000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "10000000000000000(10000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11000000000000000'u", "11000000000000000(11000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11000000000000000(11000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11100000000000000'u", "11100000000000000(11100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11100000000000000(11100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11110000000000000'u", "11110000000000000(11110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11110000000000000(11110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111000000000000'u", "11111000000000000(11111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111000000000000(11111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111100000000000'ı", "11111100000000000(11111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "11111100000000000(11111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11111110000000000'ı", "11111110000000000(11111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "11111110000000000(11111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11111111000000000'ı", "11111111000000000(11111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "11111111000000000(11111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("11111111100000000'u", "11111111100000000(11111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111111100000000(11111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111110000000'u", "11111111110000000(11111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111111110000000(11111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111111000000'u", "11111111111000000(11111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111111111000000(11111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111111100000'i", "11111111111100000(11111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111111100000(11111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111111110000'i", "11111111111110000(11111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111111110000(11111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111111111000'i", "11111111111111000(11111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111111111000(11111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("11111111111111100'ü", "11111111111111100(11111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "11111111111111100(11111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("11111111111111110'u", "11111111111111110(11111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "11111111111111110(11111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("11111111111111111'i", "11111111111111111(11111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "11111111111111111(11111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("20000000000000000'u", "20000000000000000(20000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "20000000000000000(20000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");

        // 10000000000000000-99999999999999999 (yüz katrilyon)
        assertParseCorrectForVerb("100000000000000000'u", "100000000000000000(100000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "100000000000000000(100000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("110000000000000000'u", "110000000000000000(110000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "110000000000000000(110000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111000000000000000'u", "111000000000000000(111000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111000000000000000(111000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111100000000000000'u", "111100000000000000(111100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111100000000000000(111100000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111110000000000000'u", "111110000000000000(111110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111110000000000000(111110000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111000000000000'u", "111111000000000000(111111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111000000000000(111111000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111100000000000'ı", "111111100000000000(111111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "111111100000000000(111111100000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111111110000000000'ı", "111111110000000000(111111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "111111110000000000(111111110000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111111111000000000'ı", "111111111000000000(111111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ı])", "111111111000000000(111111111000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ı])+Nom");
        assertParseCorrectForVerb("111111111100000000'u", "111111111100000000(111111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111111100000000(111111111100000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111110000000'u", "111111111110000000(111111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111111110000000(111111111110000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111111000000'u", "111111111111000000(111111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111111111000000(111111111111000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111111100000'i", "111111111111100000(111111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111111100000(111111111111100000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111111110000'i", "111111111111110000(111111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111111110000(111111111111110000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111111111000'i", "111111111111111000(111111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111111111000(111111111111111000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("111111111111111100'ü", "111111111111111100(111111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[ü])", "111111111111111100(111111111111111100)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[ü])+Nom");
        assertParseCorrectForVerb("111111111111111110'u", "111111111111111110(111111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "111111111111111110(111111111111111110)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
        assertParseCorrectForVerb("111111111111111111'i", "111111111111111111(111111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[i])", "111111111111111111(111111111111111111)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[i])+Nom");
        assertParseCorrectForVerb("200000000000000000'u", "200000000000000000(200000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[u])", "200000000000000000(200000000000000000)+Num+DigitsC+Apos+Adj+Zero+Noun+Zero+A3sg+P3sg(+sI[u])+Nom");
    }

    @Test
    public void shouldParseOrdinalDigits() {
        assertParseCorrect("0.", "0.(0.)+Num+DigitsO+Adj+Zero", "0.(0.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1.", "1.(1.)+Num+DigitsO+Adj+Zero", "1.(1.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1234.", "1234.(1234.)+Num+DigitsO+Adj+Zero", "1234.(1234.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1000000.", "1000000.(1000000.)+Num+DigitsO+Adj+Zero", "1000000.(1000000.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1.000.000.", "1.000.000.(1.000.000.)+Num+DigitsO+Adj+Zero", "1.000.000.(1.000.000.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("-34.", "-34.(-34.)+Num+DigitsO+Adj+Zero", "-34.(-34.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("-1000000.", "-1000000.(-1000000.)+Num+DigitsO+Adj+Zero", "-1000000.(-1000000.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("-1.000.000.", "-1.000.000.(-1.000.000.)+Num+DigitsO+Adj+Zero", "-1.000.000.(-1.000.000.)+Num+DigitsO+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("1'inci", "1(1)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[inci])+Adj+Zero", "1(1)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[inci])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("2'nci", "2(2)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[nci])+Adj+Zero", "2(2)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[nci])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1234'üncü", "1234(1234)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[üncü])+Adj+Zero", "1234(1234)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[üncü])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("-1'inci", "-1(-1)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[inci])+Adj+Zero", "-1(-1)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[inci])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("-1000000'uncu", "-1000000(-1000000)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[uncu])+Adj+Zero", "-1000000(-1000000)+Num+DigitsC+Apos+Num+Ord+OrdT(+IncI[uncu])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("6.'ya", "6.(6.)+Num+DigitsO+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Dat(+yA[ya])");
        assertParseCorrect("1.'yi", "1.(1.)+Num+DigitsO+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[yi])");
        assertParseCorrect("1000000.'nun", "1000000.(1000000.)+Num+DigitsO+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Gen(+nIn[nun])");
        assertParseCorrect("1.000.000.'nun", "1.000.000.(1.000.000.)+Num+DigitsO+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Gen(+nIn[nun])");
        assertParseCorrect("-123.'yü", "-123.(-123.)+Num+DigitsO+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[yü])");
        assertParseCorrect("-1000000.'da", "-1000000.(-1000000.)+Num+DigitsO+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Loc(dA[da])");
        assertParseCorrect("-1.000.000.'dan", "-1.000.000.(-1.000.000.)+Num+DigitsO+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Abl(dAn[dan])");
    }

    @Test
    public void shouldParseRangeDigits() {
        assertParseCorrect("0-1", "0-1(0-1)+Num+Range+Adj+Zero", "0-1(0-1)+Num+Range+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("0-1-2", "0-1-2(0-1-2)+Num+Range+Adj+Zero", "0-1-2(0-1-2)+Num+Range+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("10-1000-100000-1231233", "10-1000-100000-1231233(10-1000-100000-1231233)+Num+Range+Adj+Zero", "10-1000-100000-1231233(10-1000-100000-1231233)+Num+Range+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1.000-2.000", "1.000-2.000(1.000-2.000)+Num+Range+Adj+Zero", "1.000-2.000(1.000-2.000)+Num+Range+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1000-2.000", "1000-2.000(1000-2.000)+Num+Range+Adj+Zero", "1000-2.000(1000-2.000)+Num+Range+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1.000-2000", "1.000-2000(1.000-2000)+Num+Range+Adj+Zero", "1.000-2000(1.000-2000)+Num+Range+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("0-1-2-3-4-5-6", "0-1-2-3-4-5-6(0-1-2-3-4-5-6)+Num+Range+Adj+Zero", "0-1-2-3-4-5-6(0-1-2-3-4-5-6)+Num+Range+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("0-1'e", "0-1(0-1)+Num+Range+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Dat(+yA[e])");
        assertParseCorrect("0-1-2'yi", "0-1-2(0-1-2)+Num+Range+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[yi])");
        assertParseCorrect("10-1000-100000-1231233'te", "10-1000-100000-1231233(10-1000-100000-1231233)+Num+Range+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Loc(dA[te])");
        assertParseCorrect("1.000-2.000'ler", "1.000-2.000(1.000-2.000)+Num+Range+Apos+Adj+Zero+Noun+Zero+A3pl(lAr[ler])+Pnon+Nom");
        assertParseCorrect("1.000-2000'le", "1.000-2000(1.000-2000)+Num+Range+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Ins(+ylA[le])");
        assertParseCorrect("4-4-4-4'ten", "4-4-4-4(4-4-4-4)+Num+Range+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Abl(dAn[ten])");

        assertParseCorrect("1-2'nci", "1-2(1-2)+Num+Range+Apos+Num+Ord+OrdT(+IncI[nci])+Adj+Zero", "1-2(1-2)+Num+Range+Apos+Num+Ord+OrdT(+IncI[nci])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1.000.000-2.000.000'uncu", "1.000.000-2.000.000(1.000.000-2.000.000)+Num+Range+Apos+Num+Ord+OrdT(+IncI[uncu])+Adj+Zero", "1.000.000-2.000.000(1.000.000-2.000.000)+Num+Range+Apos+Num+Ord+OrdT(+IncI[uncu])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("0-1234'üncü", "0-1234(0-1234)+Num+Range+Apos+Num+Ord+OrdT(+IncI[üncü])+Adj+Zero", "0-1234(0-1234)+Num+Range+Apos+Num+Ord+OrdT(+IncI[üncü])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("0-1-2-3-4'üncü", "0-1-2-3-4(0-1-2-3-4)+Num+Range+Apos+Num+Ord+OrdT(+IncI[üncü])+Adj+Zero", "0-1-2-3-4(0-1-2-3-4)+Num+Range+Apos+Num+Ord+OrdT(+IncI[üncü])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");
        assertParseCorrect("1.000-2000'inci", "1.000-2000(1.000-2000)+Num+Range+Apos+Num+Ord+OrdT(+IncI[inci])+Adj+Zero", "1.000-2000(1.000-2000)+Num+Range+Apos+Num+Ord+OrdT(+IncI[inci])+Adj+Zero+Noun+Zero+A3sg+Pnon+Nom");

        assertParseCorrect("4-6.'ya", "4-6(4-6)+Num+Range+Num+DigitsO+OrdDot+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Dat(+yA[ya])");
        assertParseCorrect("1-2.'yi", "1-2(1-2)+Num+Range+Num+DigitsO+OrdDot+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Acc(+yI[yi])");
        assertParseCorrect("9.999-1000000.'dan", "9.999-1000000(9.999-1000000)+Num+Range+Num+DigitsO+OrdDot+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Abl(dAn[dan])");
        //TODO: following should actually be 1000-1.000.000'_y_la
        assertParseCorrect("0-1000-1.000.000.'la", "0-1000-1.000.000(0-1000-1.000.000)+Num+Range+Num+DigitsO+OrdDot+Apos+Adj+Zero+Noun+Zero+A3sg+Pnon+Ins(+ylA[la])");
    }

}