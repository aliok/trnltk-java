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

package org.trnltk.numeral;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DigitsToTextConverterTest {

    private DigitsToTextConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new DigitsToTextConverter();
    }


    @Test
    public void shouldCreateStringRepresentationsSimpleNumbers() {
        assertThat(cdtw("0"), equalTo("sıfır"));
        assertThat(cdtw("5"), equalTo("beş"));
        assertThat(cdtw("10"), equalTo("on"));
        assertThat(cdtw("12"), equalTo("on iki"));
        assertThat(cdtw("200"), equalTo("iki yüz"));
        assertThat(cdtw("1000"), equalTo("bin"));
        assertThat(cdtw("1001"), equalTo("bin bir"));
        assertThat(cdtw("1010"), equalTo("bin on"));
        assertThat(cdtw("1100"), equalTo("bin yüz"));
        assertThat(cdtw("1110"), equalTo("bin yüz on"));
        assertThat(cdtw("1111"), equalTo("bin yüz on bir"));
        assertThat(cdtw("5601"), equalTo("beş bin altı yüz bir"));
        assertThat(cdtw("999999"), equalTo("dokuz yüz doksan dokuz bin dokuz yüz doksan dokuz"));
        assertThat(cdtw("1000000"), equalTo("bir milyon"));
        assertThat(cdtw("999999999999999999999999999999999999999999999999999999999999999999"), equalTo(
                "dokuz yüz doksan dokuz vigintilyon dokuz yüz doksan dokuz novemdesilyon dokuz yüz doksan " +
                        "dokuz oktodesilyon dokuz yüz doksan dokuz septendesilyon dokuz yüz doksan dokuz " +
                        "seksdesilyon dokuz yüz doksan dokuz kendesilyon dokuz yüz doksan dokuz katordesilyon " +
                        "dokuz yüz doksan dokuz tredesilyon dokuz yüz doksan dokuz dodesilyon dokuz yüz doksan " +
                        "dokuz undesilyon dokuz yüz doksan dokuz desilyon dokuz yüz doksan dokuz nonilyon dokuz " +
                        "yüz doksan dokuz oktilyon dokuz yüz doksan dokuz septilyon dokuz yüz doksan dokuz seksilyon " +
                        "dokuz yüz doksan dokuz kentilyon dokuz yüz doksan dokuz katrilyon dokuz yüz doksan dokuz " +
                        "trilyon dokuz yüz doksan dokuz milyar dokuz yüz doksan dokuz milyon dokuz yüz doksan dokuz " +
                        "bin dokuz yüz doksan dokuz"));

    }

    @Test
    public void shouldCreateStringRepresentationsOfSimpleNegativeNumbers() {
        assertThat(cdtw("-0"), equalTo("sıfır"));
        assertThat(cdtw("-5"), equalTo("eksi beş"));
        assertThat(cdtw("-10"), equalTo("eksi on"));
        assertThat(cdtw("-12"), equalTo("eksi on iki"));
        assertThat(cdtw("-200"), equalTo("eksi iki yüz"));
        assertThat(cdtw("-1000"), equalTo("eksi bin"));
        assertThat(cdtw("-1001"), equalTo("eksi bin bir"));
        assertThat(cdtw("-1010"), equalTo("eksi bin on"));
        assertThat(cdtw("-1100"), equalTo("eksi bin yüz"));
        assertThat(cdtw("-1110"), equalTo("eksi bin yüz on"));
        assertThat(cdtw("-1111"), equalTo("eksi bin yüz on bir"));
        assertThat(cdtw("-5601"), equalTo("eksi beş bin altı yüz bir"));
        assertThat(cdtw("-999999"), equalTo("eksi dokuz yüz doksan dokuz bin dokuz yüz doksan dokuz"));
        assertThat(cdtw("-1000000"), equalTo("eksi bir milyon"));
        assertThat(cdtw("-999999999999999999999999999999999999999999999999999999999999999999"), equalTo(
                "eksi dokuz yüz doksan dokuz vigintilyon dokuz yüz doksan dokuz novemdesilyon dokuz yüz doksan " +
                        "dokuz oktodesilyon dokuz yüz doksan dokuz septendesilyon dokuz yüz doksan dokuz seksdesilyon " +
                        "dokuz yüz doksan dokuz kendesilyon dokuz yüz doksan dokuz katordesilyon dokuz yüz doksan " +
                        "dokuz tredesilyon dokuz yüz doksan dokuz dodesilyon dokuz yüz doksan dokuz undesilyon dokuz " +
                        "yüz doksan dokuz desilyon dokuz yüz doksan dokuz nonilyon dokuz yüz doksan dokuz oktilyon " +
                        "dokuz yüz doksan dokuz septilyon dokuz yüz doksan dokuz seksilyon dokuz yüz doksan dokuz " +
                        "kentilyon dokuz yüz doksan dokuz katrilyon dokuz yüz doksan dokuz trilyon dokuz yüz doksan " +
                        "dokuz milyar dokuz yüz doksan dokuz milyon dokuz yüz doksan dokuz bin dokuz yüz doksan dokuz"));
    }

    @Test
    public void shouldCreateStringRepresentationsOfSimpleExplicitlyPositiveNumbers() {
        assertThat(cdtw("+0"), equalTo("sıfır"));
        assertThat(cdtw("+5"), equalTo("beş"));
        assertThat(cdtw("+10"), equalTo("on"));
        assertThat(cdtw("+12"), equalTo("on iki"));
        assertThat(cdtw("+200"), equalTo("iki yüz"));
        assertThat(cdtw("+1000"), equalTo("bin"));
        assertThat(cdtw("+1001"), equalTo("bin bir"));
        assertThat(cdtw("+1010"), equalTo("bin on"));
        assertThat(cdtw("+1100"), equalTo("bin yüz"));
        assertThat(cdtw("+1110"), equalTo("bin yüz on"));
        assertThat(cdtw("+1111"), equalTo("bin yüz on bir"));
        assertThat(cdtw("+5601"), equalTo("beş bin altı yüz bir"));
        assertThat(cdtw("+999999"), equalTo("dokuz yüz doksan dokuz bin dokuz yüz doksan dokuz"));
        assertThat(cdtw("+1000000"), equalTo("bir milyon"));
        assertThat(cdtw("+999999999999999999999999999999999999999999999999999999999999999999"), equalTo(
                "dokuz yüz doksan dokuz vigintilyon dokuz yüz doksan dokuz novemdesilyon dokuz " +
                        "yüz doksan dokuz oktodesilyon dokuz yüz doksan dokuz septendesilyon dokuz " +
                        "yüz doksan dokuz seksdesilyon dokuz yüz doksan dokuz kendesilyon dokuz yüz " +
                        "doksan dokuz katordesilyon dokuz yüz doksan dokuz tredesilyon dokuz yüz " +
                        "doksan dokuz dodesilyon dokuz yüz doksan dokuz undesilyon dokuz yüz " +
                        "doksan dokuz desilyon dokuz yüz doksan dokuz nonilyon dokuz yüz doksan " +
                        "dokuz oktilyon dokuz yüz doksan dokuz septilyon dokuz yüz doksan dokuz " +
                        "seksilyon dokuz yüz doksan dokuz kentilyon dokuz yüz doksan dokuz katrilyon " +
                        "dokuz yüz doksan dokuz trilyon dokuz yüz doksan dokuz milyar dokuz yüz doksan " +
                        "dokuz milyon dokuz yüz doksan dokuz bin dokuz yüz doksan dokuz"));
    }

    @Test
    public void shouldCreateStringRepresentationsOfDecimalNumbers() {
        assertThat(cdtw("0,0"), equalTo("sıfır virgül sıfır"));
        assertThat(cdtw("0,000"), equalTo("sıfır virgül sıfır sıfır sıfır"));
        assertThat(cdtw("0,001"), equalTo("sıfır virgül sıfır sıfır bir"));
        assertThat(cdtw("-10,896"), equalTo("eksi on virgül sekiz yüz doksan altı"));
        assertThat(cdtw("+2567,01000"), equalTo("iki bin beş yüz altmış yedi virgül sıfır bin"));
        assertThat(
                cdtw("-999999999999999999999999999999999999999999999999999999999999999999,999999999999999999999999999999999999999999999999999999999999999999"),
                equalTo(
                        "eksi dokuz yüz doksan dokuz vigintilyon dokuz yüz doksan dokuz novemdesilyon " +
                                "dokuz yüz doksan dokuz oktodesilyon dokuz yüz doksan dokuz septendesilyon " +
                                "dokuz yüz doksan dokuz seksdesilyon dokuz yüz doksan dokuz kendesilyon " +
                                "dokuz yüz doksan dokuz katordesilyon dokuz yüz doksan dokuz tredesilyon " +
                                "dokuz yüz doksan dokuz dodesilyon dokuz yüz doksan dokuz undesilyon dokuz " +
                                "yüz doksan dokuz desilyon dokuz yüz doksan dokuz nonilyon dokuz yüz doksan " +
                                "dokuz oktilyon dokuz yüz doksan dokuz septilyon dokuz yüz doksan dokuz seksilyon " +
                                "dokuz yüz doksan dokuz kentilyon dokuz yüz doksan dokuz katrilyon dokuz yüz " +
                                "doksan dokuz trilyon dokuz yüz doksan dokuz milyar dokuz yüz doksan dokuz milyon " +
                                "dokuz yüz doksan dokuz bin dokuz yüz doksan dokuz virgül dokuz yüz doksan dokuz " +
                                "vigintilyon dokuz yüz doksan dokuz novemdesilyon dokuz yüz doksan dokuz oktodesilyon " +
                                "dokuz yüz doksan dokuz septendesilyon dokuz yüz doksan dokuz seksdesilyon dokuz yüz " +
                                "doksan dokuz kendesilyon dokuz yüz doksan dokuz katordesilyon dokuz yüz doksan dokuz " +
                                "tredesilyon dokuz yüz doksan dokuz dodesilyon dokuz yüz doksan dokuz undesilyon dokuz " +
                                "yüz doksan dokuz desilyon dokuz yüz doksan dokuz nonilyon dokuz yüz doksan dokuz " +
                                "oktilyon dokuz yüz doksan dokuz septilyon dokuz yüz doksan dokuz seksilyon dokuz " +
                                "yüz doksan dokuz kentilyon dokuz yüz doksan dokuz katrilyon dokuz yüz doksan dokuz " +
                                "trilyon dokuz yüz doksan dokuz milyar dokuz yüz doksan dokuz milyon dokuz yüz doksan " +
                                "dokuz bin dokuz yüz doksan dokuz"));

    }

    @Test
    public void shouldCreateStringRepresentationsOfNumbersWithLeadingZeros() {
        assertThat(cdtw("00"), equalTo("sıfır sıfır"));
        assertThat(cdtw("000"), equalTo("sıfır sıfır sıfır"));
        assertThat(cdtw("000,000"), equalTo("sıfır sıfır sıfır virgül sıfır sıfır sıfır"));
        assertThat(cdtw("000200"), equalTo("sıfır sıfır sıfır iki yüz"));
        assertThat(cdtw("-000200"), equalTo("eksi sıfır sıfır sıfır iki yüz"));
        assertThat(cdtw("+000200"), equalTo("sıfır sıfır sıfır iki yüz"));

        assertThat(cdtw("5,0"), equalTo("beş virgül sıfır"));
        assertThat(cdtw("-5,000"), equalTo("eksi beş virgül sıfır sıfır sıfır"));
        assertThat(cdtw("+5,000"), equalTo("beş virgül sıfır sıfır sıfır"));
    }

    @Test
    public void shouldCreateStringRepresentationsOfNumbersWithGrouping() {
        assertThat(cdtw("0.000"), equalTo("sıfır sıfır sıfır sıfır"));
        assertThat(cdtw("00.000"), equalTo("sıfır sıfır sıfır sıfır sıfır"));
        assertThat(cdtw("000.000"), equalTo("sıfır sıfır sıfır sıfır sıfır sıfır"));
        assertThat(cdtw("+5.000"), equalTo("beş bin"));
        assertThat(cdtw("-10.987"), equalTo("eksi on bin dokuz yüz seksen yedi"));
        assertThat(cdtw("12.000,0"), equalTo("on iki bin virgül sıfır"));
        assertThat(cdtw("+200.123,123"), equalTo("iki yüz bin yüz yirmi üç virgül yüz yirmi üç"));
        assertThat(cdtw("-1.000.999,0999"), equalTo("eksi bir milyon dokuz yüz doksan dokuz virgül sıfır dokuz yüz doksan dokuz"));
        assertThat(cdtw("+999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999.999"), equalTo(
                "dokuz yüz doksan dokuz vigintilyon dokuz yüz doksan dokuz novemdesilyon dokuz " +
                        "yüz doksan dokuz oktodesilyon dokuz yüz doksan dokuz septendesilyon dokuz " +
                        "yüz doksan dokuz seksdesilyon dokuz yüz doksan dokuz kendesilyon dokuz yüz " +
                        "doksan dokuz katordesilyon dokuz yüz doksan dokuz tredesilyon dokuz yüz " +
                        "doksan dokuz dodesilyon dokuz yüz doksan dokuz undesilyon dokuz yüz " +
                        "doksan dokuz desilyon dokuz yüz doksan dokuz nonilyon dokuz yüz doksan " +
                        "dokuz oktilyon dokuz yüz doksan dokuz septilyon dokuz yüz doksan dokuz " +
                        "seksilyon dokuz yüz doksan dokuz kentilyon dokuz yüz doksan dokuz katrilyon " +
                        "dokuz yüz doksan dokuz trilyon dokuz yüz doksan dokuz milyar dokuz yüz doksan " +
                        "dokuz milyon dokuz yüz doksan dokuz bin dokuz yüz doksan dokuz"));

        assertThat(cdtw("000.000,000"), equalTo("sıfır sıfır sıfır sıfır sıfır sıfır virgül sıfır sıfır sıfır"));
        assertThat(cdtw("000200.456"), equalTo("sıfır sıfır sıfır iki yüz bin dört yüz elli altı"));
        assertThat(cdtw("-000200.123"), equalTo("eksi sıfır sıfır sıfır iki yüz bin yüz yirmi üç"));
        assertThat(cdtw("+000200.900,12"), equalTo("sıfır sıfır sıfır iki yüz bin dokuz yüz virgül on iki"));

        assertThat(cdtw("5.123,0"), equalTo("beş bin yüz yirmi üç virgül sıfır"));
        assertThat(cdtw("-5.123,000"), equalTo("eksi beş bin yüz yirmi üç virgül sıfır sıfır sıfır"));
        assertThat(cdtw("+5.123,000"), equalTo("beş bin yüz yirmi üç virgül sıfır sıfır sıfır"));
    }

    private String cdtw(String digits) {
        return this.converter.convert(digits);
    }
}
