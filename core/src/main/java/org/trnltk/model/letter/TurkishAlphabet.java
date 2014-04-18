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

package org.trnltk.model.letter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import org.apache.commons.lang3.StringUtils;
import org.trnltk.util.Constants;

import java.util.Arrays;

import static org.trnltk.model.letter.TurkicLetter.builder;

/**
 * Contains Turkish Letters, Turkish Letter equivalent chars, several helper methods.
 * TurkishAlphabet only contains small case letters.
 */
@SuppressWarnings({"UnusedDeclaration","WeakerAccess"})
public class TurkishAlphabet {

    // Turkish specific characters.
    public static final char C_CC = '\u00c7'; // Ç
    public static final char C_cc = '\u00e7'; // ç
    public static final char C_GG = '\u011e'; // Ğ
    public static final char C_gg = '\u011f'; // ğ
    public static final char C_ii = '\u0131'; // ı
    public static final char C_II = '\u0130'; // İ
    public static final char C_OO = '\u00d6'; // Ö
    public static final char C_oo = '\u00f6'; // ö
    public static final char C_SS = '\u015e'; // Ş
    public static final char C_ss = '\u015f'; // ş
    public static final char C_UU = '\u00dc'; // Ü
    public static final char C_uu = '\u00fc'; // ü

    // letters used in turkish text having circumflex.
    public static final char A_CIRC = '\u00c2'; // Â
    public static final char a_CIRC = '\u00e2'; // â
    public static final char I_CIRC = '\u00ce'; // Î
    public static final char i_CIRC = '\u00ee'; // î
    public static final char U_CIRC = '\u00db'; // Û
    public static final char u_CIRC = '\u00fb'; // û

    /**
     * Turkish Letters. q,x,w is also added for foreign proper nouns. They are marked as 'foreign'
     */
    public static final TurkicLetter L_a = builder('a', 1).vowel().build();
    public static final TurkicLetter L_b = builder('b', 2).build();
    public static final TurkicLetter L_c = builder('c', 3).build();
    public static final TurkicLetter L_cc = builder(C_cc, 4).notInAscii().voiceless().similarAscii('c').build();
    public static final TurkicLetter L_d = builder('d', 5).build();
    public static final TurkicLetter L_e = builder('e', 6).vowel().frontalVowel().build();
    public static final TurkicLetter L_f = builder('f', 7).continuant().voiceless().build();
    public static final TurkicLetter L_g = builder('g', 8).build();
    public static final TurkicLetter L_gg = builder(C_gg, 9).continuant().notInAscii().similarAscii('g').build();
    public static final TurkicLetter L_h = builder('h', 10).continuant().voiceless().build();
    public static final TurkicLetter L_ii = builder(C_ii, 11).vowel().notInAscii().similarAscii('i').build();
    public static final TurkicLetter L_i = builder('i', 12).vowel().frontalVowel().build();
    public static final TurkicLetter L_j = builder('j', 13).continuant().build();
    public static final TurkicLetter L_k = builder('k', 14).voiceless().build();
    public static final TurkicLetter L_l = builder('l', 15).continuant().build();
    public static final TurkicLetter L_m = builder('m', 16).continuant().build();
    public static final TurkicLetter L_n = builder('n', 17).continuant().build();
    public static final TurkicLetter L_o = builder('o', 18).vowel().roundedVowel().build();
    public static final TurkicLetter L_oo = builder(C_oo, 19).vowel().frontalVowel().roundedVowel().notInAscii().similarAscii('o').build();
    public static final TurkicLetter L_p = builder('p', 20).voiceless().build();
    public static final TurkicLetter L_r = builder('r', 21).continuant().build();
    public static final TurkicLetter L_s = builder('s', 22).continuant().voiceless().build();
    public static final TurkicLetter L_ss = builder(C_ss, 23).continuant().notInAscii().voiceless().similarAscii('s').build();
    public static final TurkicLetter L_t = builder('t', 24).voiceless().build();
    public static final TurkicLetter L_u = builder('u', 25).vowel().roundedVowel().build();
    public static final TurkicLetter L_uu = builder(C_uu, 26).vowel().roundedVowel().frontalVowel().similarAscii('u').notInAscii().build();
    public static final TurkicLetter L_v = builder('v', 27).continuant().build();
    public static final TurkicLetter L_y = builder('y', 28).continuant().build();
    public static final TurkicLetter L_z = builder('z', 29).continuant().build();
    // Not Turkish but sometimes appears in geographical names etc.
    public static final TurkicLetter L_q = builder('q', 30).foreign().build();
    public static final TurkicLetter L_w = builder('w', 31).foreign().build();
    public static final TurkicLetter L_x = builder('x', 32).foreign().build();
    // Circumflexed letters
    public static final TurkicLetter L_ac = builder(a_CIRC, 33).vowel().similarAscii('a').notInAscii().build();
    public static final TurkicLetter L_ic = builder(i_CIRC, 34).vowel().frontalVowel().similarAscii('i').notInAscii().build();
    public static final TurkicLetter L_uc = builder(u_CIRC, 35).vowel().frontalVowel().similarAscii('u').roundedVowel().notInAscii().build();

    // Punctuations
    public static final TurkicLetter P_Dot = builder('.', 33).build();
    public static final TurkicLetter P_Comma = builder(',', 34).build();
    public static final TurkicLetter P_Hyphen = builder('-', 35).build();
    public static final TurkicLetter P_Colon = builder(':', 36).build();
    public static final TurkicLetter P_Semicolon = builder(';', 37).build();
    public static final TurkicLetter P_Plus = builder('+', 38).build();
    public static final TurkicLetter P_Popen = builder('(', 39).build();
    public static final TurkicLetter P_Pclose = builder(')', 40).build();
    public static final TurkicLetter P_Bopen = builder('[', 41).build();
    public static final TurkicLetter P_Bclose = builder(']', 42).build();
    public static final TurkicLetter P_CBopen = builder('{', 43).build();
    public static final TurkicLetter P_CBclose = builder('}', 44).build();
    public static final TurkicLetter P_QuestionMark = builder('?', 45).build();
    public static final TurkicLetter P_ExcMark = builder('!', 46).build();
    public static final TurkicLetter P_SQuote = builder('\'', 47).build();
    public static final TurkicLetter P_DQuote = builder('\"', 48).build();
    public static final TurkicLetter P_Slash = builder('/', 49).build();
    public static final TurkicLetter P_Percent = builder('%', 50).build();
    public static final TurkicLetter P_Number = builder('#', 51).build();
    public static final TurkicLetter P_Dollar = builder('$', 52).build();
    public static final TurkicLetter P_Yen = builder('¥', 53).build();
    public static final TurkicLetter P_Pound = builder('£', 54).build();
    public static final TurkicLetter P_Euro = builder('€', 55).build();

    // numbers
    public static final TurkicLetter N_0 = builder('0', 100).build();
    public static final TurkicLetter N_1 = builder('1', 101).build();
    public static final TurkicLetter N_2 = builder('2', 102).build();
    public static final TurkicLetter N_3 = builder('3', 103).build();
    public static final TurkicLetter N_4 = builder('4', 104).build();
    public static final TurkicLetter N_5 = builder('5', 105).build();
    public static final TurkicLetter N_6 = builder('6', 106).build();
    public static final TurkicLetter N_7 = builder('7', 107).build();
    public static final TurkicLetter N_8 = builder('8', 108).build();
    public static final TurkicLetter N_9 = builder('9', 109).build();


    public static final TurkicLetter[] TURKISH_LETTERS = {
            L_a, L_b, L_c, L_cc, L_d, L_e, L_f, L_g,
            L_gg, L_h, L_ii, L_i, L_j, L_k, L_l, L_m,
            L_n, L_o, L_oo, L_p, L_r, L_s, L_ss, L_t,
            L_u, L_uu, L_v, L_y, L_z, L_q, L_w, L_x,
            L_ac, L_ic, L_uc,
            P_Dot, P_Comma, P_Hyphen, P_Colon, P_Semicolon,
            P_Plus, P_Popen, P_Pclose, P_Bopen, P_Bclose, P_CBopen, P_CBclose,
            P_QuestionMark, P_ExcMark, P_SQuote, P_DQuote, P_Slash, P_Percent, P_Number,
            P_Dollar, P_Yen, P_Pound, P_Euro,
            N_0, N_1, N_2, N_3, N_4, N_5, N_6, N_7, N_8, N_9
    };

    public static final TurkicLetter[] TURKISH_ALPHA_LETTERS = {
            L_a, L_ac, L_b, L_c, L_cc, L_d, L_e, L_f, L_g,
            L_gg, L_h, L_ii, L_i, L_ic, L_j, L_k, L_l, L_m,
            L_n, L_o, L_oo, L_p, L_q, L_r, L_s, L_ss, L_t,
            L_u, L_uu, L_uc, L_v, L_w, L_x, L_y, L_z
    };

    public static final TurkicLetter[] TURKISH_PUNC_LETTERS = {
            P_Dot, P_Comma, P_Hyphen, P_Colon, P_Semicolon,
            P_Plus, P_Popen, P_Pclose, P_Bopen, P_Bclose, P_CBopen, P_CBclose,
            P_QuestionMark, P_ExcMark, P_SQuote, P_DQuote, P_Slash, P_Percent, P_Number,
            P_Dollar, P_Yen, P_Pound, P_Euro
    };

    public static final TurkicLetter[] TURKISH_NUMERIC_LETTERS = {
            N_0, N_1, N_2, N_3, N_4, N_5, N_6, N_7, N_8, N_9
    };

    // 0x15f is the maximum char value in turkish specific characters. It is the size
    // of our lookup tables. This could be done better, but for now it works.
    private static final int MAX_CHAR_VALUE = 0x20ac + 1;
    private static final TurkicLetter[] CHAR_TO_LETTER_LOOKUP = new TurkicLetter[MAX_CHAR_VALUE];
    private static final boolean[] VALID_CHAR_TABLE = new boolean[MAX_CHAR_VALUE];

    static {
        Arrays.fill(CHAR_TO_LETTER_LOOKUP, TurkicLetter.UNDEFINED);
        Arrays.fill(VALID_CHAR_TABLE, false);
        for (TurkicLetter turkicLetter : TURKISH_LETTERS) {
            final char c = turkicLetter.charValue();
            CHAR_TO_LETTER_LOOKUP[c] = turkicLetter;
            VALID_CHAR_TABLE[c] = true;

            char upperCase = StringUtils.upperCase(String.valueOf(c), Constants.TURKISH_LOCALE).charAt(0);
            if (upperCase != c) {
                CHAR_TO_LETTER_LOOKUP[upperCase] = turkicLetter;
                VALID_CHAR_TABLE[upperCase] = true;
            }

        }
    }

    protected static final ImmutableMap<TurkicLetter, TurkicLetter> devoicingMap = new ImmutableMap.Builder<TurkicLetter, TurkicLetter>()
            .put(L_b, L_p)
            .put(L_c, L_cc)
            .put(L_d, L_t)
            .put(L_g, L_k)
            .put(L_gg, L_k)
            .build();

    protected static final ImmutableMap<TurkicLetter, TurkicLetter> voicingMap = new ImmutableMap.Builder<TurkicLetter, TurkicLetter>().
            put(L_p, L_b).
            put(L_k, L_gg).
            put(L_cc, L_c).
            put(L_t, L_d).
            put(L_g, L_gg).
            build();

    public static final ImmutableSetMultimap<TurkicLetter, TurkicLetter> Inverse_Voicing_Map = new ImmutableSetMultimap.Builder<TurkicLetter, TurkicLetter>()
            .put(TurkishAlphabet.L_b, TurkishAlphabet.L_p)
            .put(TurkishAlphabet.L_c, TurkishAlphabet.L_cc)
            .put(TurkishAlphabet.L_d, TurkishAlphabet.L_t)
            .put(TurkishAlphabet.L_g, TurkishAlphabet.L_k)
            .put(TurkishAlphabet.L_gg, TurkishAlphabet.L_g)
            .put(TurkishAlphabet.L_gg, TurkishAlphabet.L_k)
            .build();
    public static final ImmutableSet<TurkicLetter> Devoicable_Letters = ImmutableSet.copyOf(org.trnltk.model.letter.TurkishAlphabet.devoicingMap.keySet());
    public static final ImmutableSet<TurkicLetter> Voicable_Letters = ImmutableSet.copyOf(org.trnltk.model.letter.TurkishAlphabet.voicingMap.keySet());

    private TurkishAlphabet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Devoices a turkish letter.
     * <ul>
     * <li>b -> p (*)</li>
     * <li>c -> ç</li>
     * <li>d -> t</li>
     * <li>g -> k (*)</li>
     * <li>ğ -> k (*)</li>
     * <li>Otherwise -> null</li>
     * </ul>
     * <p/>
     * * = not really applicable, since there is no suffix starting with b, g or ğ
     *
     * @param l Letter to devoice
     * @return Devoiced letter or null if letter is not devoicable
     */
    public static TurkicLetter devoice(TurkicLetter l) {
        return devoicingMap.get(l);
    }

    /**
     * Voices a turkish letter.
     * <ul>
     * <li>p -> b</li>
     * <li>k -> ğ</li>
     * <li>ç -> c</li>
     * <li>t -> d</li>
     * <li>g -> ğ</li>
     * <li>Otherwise -> null</li>
     * </ul>
     *
     * @param l Letter to voice
     * @return Voiced letter or null if letter is not voicable
     */
    public static TurkicLetter voice(TurkicLetter l) {
        return voicingMap.get(l);
    }

    /**
     * Returns the TurkicLetter equivalent of character c.
     *
     * @param c input character
     * @return TurkishLetter equivalent.
     * @throws IllegalArgumentException if input character is out of alphabet.
     */
    public static TurkicLetter getLetter(char c) {
        if (c >= MAX_CHAR_VALUE || !VALID_CHAR_TABLE[c])
            return TurkicLetter.builder(c, 9999).build();
        else
            return CHAR_TO_LETTER_LOOKUP[c];
    }

    public static TurkishChar getChar(char c) {
        final TurkicLetter letterForChar = getLetter(c);
        return new TurkishChar(c, letterForChar);
    }

    /**
     * Checks if a character is part of TurkishAlphabet.
     *
     * @param c character to check
     * @return true if it is part of the Turkish alphabet. false otherwise
     */
    public static boolean isValid(char c) {
        return c < MAX_CHAR_VALUE && VALID_CHAR_TABLE[c];
    }

}