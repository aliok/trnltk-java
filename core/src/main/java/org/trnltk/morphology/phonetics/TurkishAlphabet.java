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
import zemberek3.structure.TurkicLetter;

public class TurkishAlphabet extends zemberek3.structure.TurkishAlphabet {

    private static final zemberek3.structure.TurkishAlphabet ZEMBEREK_ALPHABET_INSTANCE = new zemberek3.structure.TurkishAlphabet();

    public static final ImmutableSet<TurkicLetter> Devoicable_Letters = ImmutableSet.copyOf(zemberek3.structure.TurkishAlphabet.devoicingMap.keySet());

    public static TurkicLetter getLetterForChar(char c) {
        c = Character.toLowerCase(c);
        try {
            return ZEMBEREK_ALPHABET_INSTANCE.getLetter(c);
        }
        catch (IllegalArgumentException e){     //TODO-INTEGRATION: this is ugly!
            return TurkicLetter.builder(c, 9999).build();
        }
    }

    public static TurkicLetter voiceLetter(TurkicLetter letter) {
        return ZEMBEREK_ALPHABET_INSTANCE.voice(letter);
    }

    public static TurkicLetter devoiceLetter(TurkicLetter letter) {
        return ZEMBEREK_ALPHABET_INSTANCE.devoice(letter);
    }

    public static TurkishChar getChar(char c) {
        final TurkicLetter letterForChar = getLetterForChar(c);
        return new TurkishChar(c, letterForChar);
    }
}
