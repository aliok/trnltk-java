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

import org.junit.Ignore;
import org.junit.Test;

public abstract class BaseContextlessMorphologicParserProperNounSuffixGraphTest extends BaseContextlessMorphologicParserTest {

    @Test
    public void shouldParseProperNounsWithoutApostrophe() {
        assertParseCorrect("Ali", "Ali(Ali)+Noun+Prop+A3sg+Pnon+Nom");
        assertParseCorrect("Ahmet", "Ahmet(Ahmet)+Noun+Prop+A3sg+Pnon+Nom");
    }

    @Test
    public void shouldParseProperNounsWithApostrophe() {
        assertParseCorrect("Ali'ye", "Ali(Ali)+Noun+Prop+Apos+A3sg+Pnon+Dat(+yA[ye])");
    }

    @Test
    public void shouldParseAbbreviationsWithApostrophe() {
        assertParseCorrect("AB'ye", "AB(AB)+Noun+Abbr+Apos+A3sg+Pnon+Dat(+yA[ye])");
    }

    @Test
    public void shouldParseAbbreviationsWithoutApostrophe() {
        assertParseCorrect("AB", "AB(AB)+Noun+Abbr+A3sg+Pnon+Nom");
        assertParseCorrect("ABD", "ABD(ABD)+Noun+Abbr+A3sg+Pnon+Nom");
    }

    @Test
    @Ignore
    public void shouldParseProperNounsTDK() {
        // TODO: implementing these features is almost impossible without context and historical data

        // tests based on TDK
        // http://www.tdk.gov.tr/index.php?option=com_content&view=article&id=187:Noktalama-Isaretleri-Aciklamalar&catid=50:yazm-kurallar&Itemid=132

        // Özel adlara getirilen iyelik, durum ve bildirme ekleri kesme işaretiyle ayrılır
        assertParseCorrect("Kayseri'm", "Kayseri(Kayseri)+Noun+Prop+Apos+A3sg+P1sg(+Im[m])+Nom");
        assertParseCorrect("Türkiye'mizin", "Türkiye(Türkiye)+Noun+Prop+Apos+A3sg+P1sg(+ImIz[miz])+Gen(+nIn[nin])");
        assertParseCorrect("Muhibbi'nin", "Muhibbi(Muhibbi)+Noun+Prop+Apos+A3sg+Pnon+Gen(+nIn[in])");
        assertParseCorrect("Emre'yi");
        assertParseCorrect("Şinasi'yle");
        assertParseCorrect("Alman'sınız");
        assertParseCorrect("Kırgız'ım");
        assertParseCorrect("Karakeçili'nin");
        assertParseCorrect("Cebrail'den");
        assertParseCorrect("Samanyolu'nda");
        assertParseCorrect("Eminönü'nde");
        assertParseCorrect("Ahmet'miş");
        assertParseCorrect("Ahmet'ti");

        // Sonunda 3. teklik kişi iyelik eki olan özel ada, bu ek dışında başka bir iyelik eki getirildiğinde kesme işareti konmaz
        assertParseCorrect("Boğaz_Köprümüz");
        assertParseCorrect("Boğaz_Köprümüzün");
        assertParseCorrect("Kuşadamızda");

        // Kurum, kuruluş, kurul, birleşim, oturum ve iş yeri adlarına gelen ekler kesmeyle ayrılmaz:
        assertParseCorrect("Türk_Dil_Kurumundan");
        assertParseCorrect("Başbakanlığa");

        // Özel adlara getirilen yapım ekleri, çokluk eki ve bunlardan sonra gelen diğer ekler kesmeyle ayrılmaz
        assertParseCorrect("Türklük");
        assertParseCorrect("Türkleşmek");
        assertParseCorrect("Türkçü");
        assertParseCorrect("Türkçülük");
        assertParseCorrect("Türkçe");
        assertParseCorrect("Avrupalı");
        assertParseCorrect("Avrupalılaşmak");
        assertParseCorrect("Mehmetler");
        assertParseCorrect("Mehmetlere");
        assertParseCorrect("Mehmetgil");
        assertParseCorrect("Mehmetgile");
        assertParseCorrect("Türklerin");
        assertParseCorrect("Türkçenin");
        assertParseCorrect("Müslümanlıkta");

        // Kişi adlarından sonra gelen saygı ve unvan sözlerine getirilen ekleri ayırmak için konur:
        assertParseCorrect("Nihat_Bey'e");
        assertParseCorrect("Ayşe_Hanım'dan");
        assertParseCorrect("Enver_Paşa'ya");
    }
}
