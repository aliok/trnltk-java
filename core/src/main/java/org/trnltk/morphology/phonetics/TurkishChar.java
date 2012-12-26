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

public class TurkishChar {

    private final char charValue;
    private final TurkishLetter letter;

    public TurkishChar(char charValue, TurkishLetter letter) {
        this.charValue = charValue;
        this.letter = letter;
    }

    public TurkishLetter getLetter() {
        return letter;
    }

    public char getCharValue() {
        return charValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TurkishChar that = (TurkishChar) o;

        if (charValue != that.charValue) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) charValue;
    }

    @Override
    public String toString() {
        return "TurkishChar{" +
                "charValue=" + charValue +
                '}';
    }
}
