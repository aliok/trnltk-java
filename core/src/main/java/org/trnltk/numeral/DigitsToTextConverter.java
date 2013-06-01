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

package org.trnltk.numeral;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.math.BigInteger;
import java.util.regex.Pattern;

public class DigitsToTextConverter {
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TEN = BigInteger.TEN;
    private static final BigInteger ONE_HUNDRED = TEN.multiply(TEN);
    private static final BigInteger ONE_THOUSAND = ONE_HUNDRED.multiply(TEN);

    private static final Pattern TURKISH_NUMBER_PATTERN = Pattern.compile("^[-+]?\\d+(,\\d)?\\d*$");
    private static final int MAX_GROUP_BASE = 63;
    private static final BigInteger MAX_NATURAL_NUMBER_SUPPORTED = TEN.pow(MAX_GROUP_BASE + 3).add(ONE.negate());  // 10^^66 - 1

    private static final String FRACTION_SEPARATOR = ",";
    private static final String GROUPING_SEPARATOR_REGEX = "\\.";

    private static final String NEGATIVE_SIGN = "-";
    private static final String POSITIVE_SIGN = "+";

    private static final String COMMA_NAME = "virgül";
    private static final String MINUS_NAME = "eksi";

    private static final String ZERO_NAME = "sıfır";
    private static final String HUNDRED_NAME = "yüz";
    private static final String THOUSAND_NAME = "bin";

    private static final ImmutableMap<Integer, String> NUMERAL_SYMBOL_NAMES = new ImmutableMap.Builder<Integer, String>()
            .put(0, "sıfır")
            .put(1, "bir")
            .put(2, "iki")
            .put(3, "üç")
            .put(4, "dört")
            .put(5, "beş")
            .put(6, "altı")
            .put(7, "yedi")
            .put(8, "sekiz")
            .put(9, "dokuz")
            .build();

    private static final ImmutableMap<Integer, String> TENS_MULTIPLES_NAMES = new ImmutableMap.Builder<Integer, String>()
            .put(1, "on")
            .put(2, "yirmi")
            .put(3, "otuz")
            .put(4, "kırk")
            .put(5, "elli")
            .put(6, "altmış")
            .put(7, "yetmiş")
            .put(8, "seksen")
            .put(9, "doksan")
            .build();

    private static final ImmutableMap<Integer, String> THOUSAND_POWER_NAMES = new ImmutableMap.Builder<Integer, String>()
            .put(0, "")
            .put(1, "bin")
            .put(2, "milyon")
            .put(3, "milyar")
            .put(4, "trilyon")
            .put(5, "katrilyon")
            .put(6, "kentilyon")
            .put(7, "seksilyon")
            .put(8, "septilyon")
            .put(9, "oktilyon")
            .put(10, "nonilyon")
            .put(11, "desilyon")
            .put(12, "undesilyon")
            .put(13, "dodesilyon")
            .put(14, "tredesilyon")
            .put(15, "katordesilyon")
            .put(16, "kendesilyon")
            .put(17, "seksdesilyon")
            .put(18, "septendesilyon")
            .put(19, "oktodesilyon")
            .put(20, "novemdesilyon")
            .put(21, "vigintilyon")
            .build();

    public String convert(String digits) {
        if (StringUtils.isBlank(digits))
            return null;

        digits = digits.replaceAll(GROUPING_SEPARATOR_REGEX, StringUtils.EMPTY);

        if (!TURKISH_NUMBER_PATTERN.matcher(digits).matches())
            throw new IllegalArgumentException("'" + digits + "' is not a valid Turkish number. Allowed pattern is : " + TURKISH_NUMBER_PATTERN.pattern());

        String strIntegerPart, strFractionPart;

        if (digits.contains(FRACTION_SEPARATOR)) {
            strIntegerPart = digits.substring(0, digits.indexOf(FRACTION_SEPARATOR));
            strFractionPart = digits.substring(digits.indexOf(FRACTION_SEPARATOR) + 1);
        } else {
            strIntegerPart = digits;
            strFractionPart = null;
        }

        boolean isPositive = true;
        if (strIntegerPart.startsWith(POSITIVE_SIGN)) {
            isPositive = true;
            strIntegerPart = strIntegerPart.substring(1);
        }
        if (strIntegerPart.startsWith(NEGATIVE_SIGN)) {
            isPositive = false;
            strIntegerPart = strIntegerPart.substring(1);
        }

        BigInteger integerPart = new BigInteger(strIntegerPart);
        BigInteger fractionPart = StringUtils.isNotBlank(strFractionPart) ? new BigInteger(strFractionPart) : BigInteger.ZERO;

        if (!isPositive)
            integerPart = integerPart.negate();

        String wordIntegerPart = this.convertNaturalNumberToWords(integerPart.abs());
        String wordFractionPart = this.convertNaturalNumberToWords(fractionPart);

        wordIntegerPart = this.addTextForLeadingZeros(strIntegerPart, wordIntegerPart);
        wordFractionPart = StringUtils.isNotBlank(strFractionPart) ? this.addTextForLeadingZeros(strFractionPart, wordFractionPart) : wordFractionPart;

        if (integerPart.compareTo(ZERO) < 0)
            wordIntegerPart = MINUS_NAME + " " + wordIntegerPart;

        if (digits.contains(FRACTION_SEPARATOR))
            return wordIntegerPart + " " + COMMA_NAME + " " + wordFractionPart;
        else
            return wordIntegerPart;
    }

    private String convertNaturalNumberToWords(BigInteger naturalNumber) {
        Validate.isTrue(naturalNumber.compareTo(ZERO) >= 0);
        Validate.isTrue(naturalNumber.compareTo(MAX_NATURAL_NUMBER_SUPPORTED) <= 0,
                "Given number " + naturalNumber + " is larger than maximum supported natural number " + MAX_NATURAL_NUMBER_SUPPORTED);

        StringBuilder result = new StringBuilder();

        if (naturalNumber.compareTo(BigInteger.TEN) < 0) {
            result.append(NUMERAL_SYMBOL_NAMES.get(naturalNumber.intValue()));

        } else if (naturalNumber.compareTo(ONE_HUNDRED) < 0) {
            final BigInteger tensDigit = naturalNumber.divide(TEN);
            final BigInteger onesDigit = naturalNumber.mod(TEN);
            final String strTensDigit = TENS_MULTIPLES_NAMES.get(tensDigit.intValue());
            final String strOnesDigit = onesDigit.compareTo(ZERO) > 0 ? convertNaturalNumberToWords(onesDigit) : StringUtils.EMPTY;
            result.append(strTensDigit).append(" ").append(strOnesDigit);

        } else if (naturalNumber.compareTo(ONE_THOUSAND) < 0) {
            final BigInteger hundredsDigit = naturalNumber.divide(ONE_HUNDRED);
            final BigInteger rest = naturalNumber.mod(ONE_HUNDRED);
            final String strHundredsDigit;
            if (hundredsDigit.equals(ZERO)) {
                strHundredsDigit = StringUtils.EMPTY;
            } else if (hundredsDigit.equals(ONE)) {
                strHundredsDigit = StringUtils.EMPTY;
            } else {
                strHundredsDigit = convertNaturalNumberToWords(hundredsDigit);
            }

            final String restStr = rest.compareTo(ZERO) > 0 ? convertNaturalNumberToWords(rest) : StringUtils.EMPTY;

            result.append(strHundredsDigit).append(" ").append(HUNDRED_NAME).append(" ").append(restStr);

        } else {
            int mostSignificantGroupBase = this.findMostSignificantGroupBase(naturalNumber);
            for (int i = mostSignificantGroupBase / 3; i > 0; i--) {
                int groupNumber = this.getNthGroupNumber(naturalNumber, i);
                if (groupNumber == 0) {
                    // don't write 'sifir milyon'
                } else if (groupNumber == 1 && i == 1) {
                    // don't write 'bir bin', but write 'bir milyon'(below)
                    result.append(" ").append(THOUSAND_NAME);
                } else {
                    final String strGroupNumber = this.convertNaturalNumberToWords(BigInteger.valueOf(groupNumber));
                    result.append(" ").append(strGroupNumber).append(" ").append(THOUSAND_POWER_NAMES.get(i));
                }

                result = new StringBuilder(result.toString().trim());
            }

            final BigInteger lastGroupNumber = naturalNumber.mod(ONE_THOUSAND);
            if (lastGroupNumber.compareTo(ZERO) > 0)
                result.append(" ").append(convertNaturalNumberToWords(lastGroupNumber));
        }

        return result.toString().trim();
    }

    private int getNthGroupNumber(BigInteger naturalNumber, int n) {
        naturalNumber = naturalNumber.divide(ONE_THOUSAND.pow(n));
        naturalNumber = naturalNumber.mod(ONE_THOUSAND);
        return naturalNumber.intValue();
    }

    private String addTextForLeadingZeros(final String strInteger, final String word) {
        String newWord = word;

        final int numberOfLeadingZeros = this.getNumberOfLeadingZeros(strInteger);
        for (int i = 0; i < numberOfLeadingZeros; i++) {
            newWord = ZERO_NAME + " " + newWord;
        }

        return newWord;
    }

    private int getNumberOfLeadingZeros(String strInteger) {
        if (strInteger.startsWith(NEGATIVE_SIGN) || strInteger.startsWith(POSITIVE_SIGN))
            strInteger = strInteger.substring(1);

        int i = 0;
        for (; i < strInteger.toCharArray().length - 1; i++) {
            char c = strInteger.toCharArray()[i];
            if (c != '0')
                break;
        }
        return i;
    }

    private int findMostSignificantGroupBase(BigInteger naturalNumber) {
        int i = MAX_GROUP_BASE / 3;
        while (TEN.pow(i * 3).compareTo(naturalNumber) > 0)
            i--;

        return i * 3;
    }
}
