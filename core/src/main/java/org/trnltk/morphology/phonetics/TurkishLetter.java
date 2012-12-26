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

import org.apache.commons.lang3.Validate;

public class TurkishLetter {
    private final char charValue;
    private final char upperCaseCharValue;
    private final int alphabeticIndex;
    private final boolean vowel;
    private final boolean frontal;
    private final boolean rounded;
    private final boolean voiceless;
    private final boolean continuant;
    private final boolean inAscii;
    private final boolean foreign;
    private final char asciiEquivalentChar;


    private TurkishLetter(char charValue, char upperCaseCharValue, int alphabeticIndex, boolean vowel, boolean frontal, boolean rounded, boolean voiceless,
                          boolean continuant, boolean inAscii, boolean foreign, char asciiEquivalentChar) {
        this.charValue = charValue;
        this.upperCaseCharValue = upperCaseCharValue;
        this.alphabeticIndex = alphabeticIndex;
        this.vowel = vowel;
        this.frontal = frontal;
        this.rounded = rounded;
        this.voiceless = voiceless;
        this.continuant = continuant;
        this.inAscii = inAscii;
        this.foreign = foreign;
        this.asciiEquivalentChar = asciiEquivalentChar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TurkishLetter that = (TurkishLetter) o;

        if (charValue != that.charValue) return false;
        if (upperCaseCharValue != that.upperCaseCharValue) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) charValue;
        result = 31 * result + (int) upperCaseCharValue;
        return result;
    }

    @Override
    public String toString() {
        return "TL{" + charValue + '}';
    }

    public char getCharValue() {
        return charValue;
    }

    public char getUpperCaseCharValue() {
        return upperCaseCharValue;
    }

    public int getAlphabeticIndex() {
        return alphabeticIndex;
    }

    public boolean isVowel() {
        return vowel;
    }

    public boolean isFrontal() {
        return frontal;
    }

    public boolean isRounded() {
        return rounded;
    }

    public boolean isVoiceless() {
        return voiceless;
    }

    public boolean isContinuant() {
        return continuant;
    }

    public boolean isInAscii() {
        return inAscii;
    }

    public boolean isForeign() {
        return foreign;
    }

    public char getAsciiEquivalentChar() {
        return asciiEquivalentChar;
    }

    static class TurkishLetterBuilder {
        private char charValue;
        private char upperCaseCharValue;
        private boolean vowel;
        private int alphabeticIndex;
        private boolean frontal = false;
        private boolean rounded = false;
        private boolean voiceless = false;
        private boolean continuant = false;
        private boolean inAscii = true;
        private boolean foreign = false;
        private Character asciiEquivalentChar;

        public TurkishLetterBuilder(char charValue, char upperCaseCharValue, int alphabeticIndex) {
            this.charValue = charValue;
            this.upperCaseCharValue = upperCaseCharValue;
            this.alphabeticIndex = alphabeticIndex;
        }

        TurkishLetter build() {
            Validate.isTrue(alphabeticIndex >= 0);
            if (vowel) {
                Validate.isTrue(!voiceless);
                Validate.isTrue(!continuant);
            } else {
                Validate.isTrue(!frontal);
                Validate.isTrue(!rounded);
            }
            Validate.isTrue(inAscii || asciiEquivalentChar != null);
            Validate.isTrue(inAscii || (asciiEquivalentChar.charValue() >= 'a' && asciiEquivalentChar.charValue() <= 'z'));


            return new TurkishLetter(charValue, upperCaseCharValue, alphabeticIndex, vowel,
                    frontal,
                    rounded,
                    voiceless,
                    continuant,
                    inAscii,
                    foreign,
                    asciiEquivalentChar == null ? charValue : asciiEquivalentChar.charValue());
        }

        public TurkishLetterBuilder vowel() {
            this.vowel = true;
            return this;
        }

        public TurkishLetterBuilder frontal() {
            Validate.isTrue(vowel);
            this.frontal = true;
            return this;
        }

        public TurkishLetterBuilder rounded() {
            Validate.isTrue(vowel);
            this.rounded = true;
            return this;
        }

        public TurkishLetterBuilder voiceless() {
            Validate.isTrue(!vowel);
            this.voiceless = true;
            return this;
        }

        public TurkishLetterBuilder continuant() {
            Validate.isTrue(!vowel);
            this.continuant = true;
            return this;
        }

        public TurkishLetterBuilder foreign() {
            this.foreign = true;
            return this;
        }

        public TurkishLetterBuilder asciiEquivalentChar(Character asciiEquivalentChar) {
            this.inAscii = true;
            this.asciiEquivalentChar = asciiEquivalentChar;
            return this;
        }
    }
}

