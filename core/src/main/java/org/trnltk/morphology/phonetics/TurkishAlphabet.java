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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import org.trnltk.morphology.model.structure.TurkicLetter;

public class TurkishAlphabet extends org.trnltk.morphology.model.structure.TurkishAlphabet {

    //TODO-INTEGRATION: following lines are ugly and temporary
    private static final org.trnltk.morphology.model.structure.TurkishAlphabet OTHER_ALPHABET_INSTANCE = new org.trnltk.morphology.model.structure.TurkishAlphabet();

    public static final ImmutableSet<TurkicLetter> Devoicable_Letters = ImmutableSet.copyOf(org.trnltk.morphology.model.structure.TurkishAlphabet.devoicingMap.keySet());
    public static final ImmutableSet<TurkicLetter> Voicable_Letters = ImmutableSet.copyOf(org.trnltk.morphology.model.structure.TurkishAlphabet.voicingMap.keySet());
    public static final ImmutableSetMultimap<TurkicLetter, TurkicLetter> Inverse_Voicing_Map = new ImmutableSetMultimap.Builder<TurkicLetter, TurkicLetter>()
            .put(TurkishAlphabet.L_b, TurkishAlphabet.L_p)
            .put(TurkishAlphabet.L_c, TurkishAlphabet.L_cc)
            .put(TurkishAlphabet.L_d, TurkishAlphabet.L_t)
            .put(TurkishAlphabet.L_g, TurkishAlphabet.L_k)
            .put(TurkishAlphabet.L_gg, TurkishAlphabet.L_g)
            .put(TurkishAlphabet.L_gg, TurkishAlphabet.L_k)
            .build();

    public static TurkicLetter getLetterForChar(char c) {
        c = Character.toLowerCase(c);
        try {
            return OTHER_ALPHABET_INSTANCE.getLetter(c);
        } catch (IllegalArgumentException e) {     //TODO-INTEGRATION: this is ugly!
            return TurkicLetter.builder(c, 9999).build();
        }
    }

    public static TurkicLetter voiceLetter(TurkicLetter letter) {
        return OTHER_ALPHABET_INSTANCE.voice(letter);
    }

    public static TurkicLetter devoiceLetter(TurkicLetter letter) {
        return OTHER_ALPHABET_INSTANCE.devoice(letter);
    }

    public static TurkishChar getChar(char c) {
        final TurkicLetter letterForChar = getLetterForChar(c);
        return new TurkishChar(c, letterForChar);
    }
}
