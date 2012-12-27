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

package org.trnltk.morphology.phonetics;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

import java.util.Arrays;

public class TurkishAlphabet {

    public static final TurkishLetter L_a = new TurkishLetter.TurkishLetterBuilder('a', 1).vowel().build();
    public static final TurkishLetter L_b = new TurkishLetter.TurkishLetterBuilder('b', 2).build();
    public static final TurkishLetter L_c = new TurkishLetter.TurkishLetterBuilder('c', 3).build();
    public static final TurkishLetter L_cc = new TurkishLetter.TurkishLetterBuilder('ç', 4).voiceless().asciiEquivalentChar('c').build();
    public static final TurkishLetter L_d = new TurkishLetter.TurkishLetterBuilder('d', 5).build();
    public static final TurkishLetter L_e = new TurkishLetter.TurkishLetterBuilder('e', 6).vowel().frontal().build();
    public static final TurkishLetter L_f = new TurkishLetter.TurkishLetterBuilder('f', 7).continuant().voiceless().build();
    public static final TurkishLetter L_g = new TurkishLetter.TurkishLetterBuilder('g', 8).build();
    public static final TurkishLetter L_gg = new TurkishLetter.TurkishLetterBuilder('ğ', 9).continuant().asciiEquivalentChar('g').build();
    public static final TurkishLetter L_h = new TurkishLetter.TurkishLetterBuilder('h', 10).continuant().voiceless().build();
    public static final TurkishLetter L_ii = new TurkishLetter.TurkishLetterBuilder('ı', 11).vowel().asciiEquivalentChar('i').build();
    public static final TurkishLetter L_i = new TurkishLetter.TurkishLetterBuilder('i', 12).vowel().frontal().build();
    public static final TurkishLetter L_j = new TurkishLetter.TurkishLetterBuilder('j', 13).continuant().build();
    public static final TurkishLetter L_k = new TurkishLetter.TurkishLetterBuilder('k', 14).voiceless().build();
    public static final TurkishLetter L_l = new TurkishLetter.TurkishLetterBuilder('l', 15).continuant().build();
    public static final TurkishLetter L_m = new TurkishLetter.TurkishLetterBuilder('m', 16).continuant().build();
    public static final TurkishLetter L_n = new TurkishLetter.TurkishLetterBuilder('n', 17).continuant().build();
    public static final TurkishLetter L_o = new TurkishLetter.TurkishLetterBuilder('o', 18).vowel().rounded().build();
    public static final TurkishLetter L_oo = new TurkishLetter.TurkishLetterBuilder('ö', 19).vowel().frontal().rounded().asciiEquivalentChar('o').build();
    public static final TurkishLetter L_p = new TurkishLetter.TurkishLetterBuilder('p', 20).voiceless().build();
    public static final TurkishLetter L_r = new TurkishLetter.TurkishLetterBuilder('r', 21).continuant().build();
    public static final TurkishLetter L_s = new TurkishLetter.TurkishLetterBuilder('s', 22).continuant().voiceless().build();
    public static final TurkishLetter L_ss = new TurkishLetter.TurkishLetterBuilder('ş', 23).continuant().voiceless().asciiEquivalentChar('s').build();
    public static final TurkishLetter L_t = new TurkishLetter.TurkishLetterBuilder('t', 24).voiceless().build();
    public static final TurkishLetter L_u = new TurkishLetter.TurkishLetterBuilder('u', 25).vowel().rounded().build();
    public static final TurkishLetter L_uu = new TurkishLetter.TurkishLetterBuilder('ü', 26).vowel().frontal().rounded().asciiEquivalentChar('o').build();
    public static final TurkishLetter L_v = new TurkishLetter.TurkishLetterBuilder('v', 27).continuant().build();
    public static final TurkishLetter L_y = new TurkishLetter.TurkishLetterBuilder('y', 28).continuant().build();
    public static final TurkishLetter L_z = new TurkishLetter.TurkishLetterBuilder('z', 29).continuant().build();

    public static final TurkishLetter L_q = new TurkishLetter.TurkishLetterBuilder('q', 30).foreign().build();
    public static final TurkishLetter L_w = new TurkishLetter.TurkishLetterBuilder('w', 31).foreign().build();
    public static final TurkishLetter L_x = new TurkishLetter.TurkishLetterBuilder('x', 32).foreign().build();

    public static final TurkishLetter L_ac = new TurkishLetter.TurkishLetterBuilder('â', 33).vowel().asciiEquivalentChar('a').build();
    public static final TurkishLetter L_ic = new TurkishLetter.TurkishLetterBuilder('î', 34).vowel().frontal().asciiEquivalentChar('i').build();
    public static final TurkishLetter L_uc = new TurkishLetter.TurkishLetterBuilder('û', 35).vowel().rounded().asciiEquivalentChar('u').build();

    public static ImmutableSet<TurkishLetter> Turkish_Letters = ImmutableSet.copyOf(Arrays.asList(
            L_a, L_b, L_c, L_cc, L_d, L_e, L_f, L_g,
            L_gg, L_h, L_ii, L_i, L_j, L_k, L_l, L_m,
            L_n, L_o, L_oo, L_p, L_r, L_s, L_ss, L_t,
            L_u, L_uu, L_v, L_y, L_z, L_q, L_w, L_x,
            L_ac, L_ic, L_uc));

    public static final ImmutableSet<TurkishLetter> Consonants = ImmutableSet.copyOf(Collections2.filter(Turkish_Letters, new Predicate<TurkishLetter>() {
        @Override
        public boolean apply(TurkishLetter input) {
            return !input.isVowel();
        }
    }));

    public static final ImmutableSet<TurkishLetter> Vowels = ImmutableSet.copyOf(Collections2.filter(Turkish_Letters, new Predicate<TurkishLetter>() {
        @Override
        public boolean apply(TurkishLetter input) {
            return input.isVowel();
        }
    }));

    public static final ImmutableMap<TurkishLetter, TurkishLetter> Devoicing_Map = new ImmutableMap.Builder<TurkishLetter, TurkishLetter>()
            .put(L_b, L_p)
            .put(L_c, L_cc)
            .put(L_d, L_t)
            .put(L_g, L_k)
            .put(L_gg, L_k)
            .build();

    public static final ImmutableSet<TurkishLetter> Devoicable_Letters = Devoicing_Map.keySet();

    public static final ImmutableMap<TurkishLetter, TurkishLetter> Voicing_Map = new ImmutableMap.Builder<TurkishLetter, TurkishLetter>()
            .put(L_p, L_b)
            .put(L_cc, L_c)
            .put(L_t, L_d)
            .put(L_g, L_gg)
            .put(L_k, L_gg)
            .build();

    public static final ImmutableSetMultimap<TurkishLetter, TurkishLetter> Inverse_Voicing_Map = new ImmutableSetMultimap.Builder<TurkishLetter, TurkishLetter>()
            .put(L_b, L_p)
            .put(L_c, L_cc)
            .put(L_d, L_t)
            .put(L_g, L_k)
            .put(L_gg, L_g)
            .put(L_gg, L_k)
            .build();

    public static final ImmutableMap<Character, TurkishLetter> Letter_Map = ImmutableMap.copyOf(
            HashBiMap.create(
                    Maps.toMap(Turkish_Letters, new Function<TurkishLetter, Character>() {
                        @Override
                        public Character apply(TurkishLetter input) {
                            return input.getCharValue();
                        }
                    })
            ).inverse()
    );

    public static TurkishLetter getLetterForChar(char c) {
        c = Character.toLowerCase(c);
        final TurkishLetter lowerLetter = Letter_Map.get(c);
        if (lowerLetter != null)
            return lowerLetter;

        return new TurkishLetter.TurkishLetterBuilder(c, 99).build();
    }

    public static TurkishLetter voice(TurkishLetter letter) {
        return Voicing_Map.get(letter);
    }

    public static TurkishLetter devoice(TurkishLetter letter) {
        return Devoicing_Map.get(letter);
    }

    public static TurkishChar getChar(char c) {
        final TurkishLetter letterForChar = getLetterForChar(c);
        return new TurkishChar(c, letterForChar);
    }
}
