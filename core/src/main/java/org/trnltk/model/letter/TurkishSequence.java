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

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class TurkishSequence {
    private final String underlyingString;
    private final TurkishChar[] chars;
    private TurkishChar firstVowel;
    private TurkishChar lastVowel;
    private int count;

    public TurkishSequence(String underlyingString) {
        this.underlyingString = Strings.nullToEmpty(underlyingString);
        this.count = underlyingString.length();
        this.chars = new TurkishChar[this.count];
        for (int i = 0; i < underlyingString.length(); i++) {
            char c = underlyingString.charAt(i);
            TurkishChar turkishChar = TurkishAlphabet.getChar(c);
            this.chars[i] = turkishChar;
            if (turkishChar.getLetter().isVowel()) {
                if (this.firstVowel == null)
                    this.firstVowel = turkishChar;
                this.lastVowel = turkishChar;
            }
        }
    }

    private TurkishSequence(final TurkishSequence first, final String strSecond) {
        final String strFirst = first.underlyingString;
        final TurkishChar[] charsFirst = first.chars;
        this.underlyingString = strFirst + strSecond;
        this.count = this.underlyingString.length();
        this.firstVowel = first.firstVowel;
        this.lastVowel = first.lastVowel;
        this.chars = new TurkishChar[charsFirst.length + strSecond.length()];
        System.arraycopy(charsFirst, 0, this.chars, 0, charsFirst.length);
        for (int i = 0; i < strSecond.length(); i++) {
            char charOfSecondStr = strSecond.charAt(i);
            TurkishChar turkishCharOfSecondStr = TurkishAlphabet.getChar(charOfSecondStr);
            this.chars[i + charsFirst.length] = turkishCharOfSecondStr;
            if (turkishCharOfSecondStr.getLetter().isVowel()) {
                if (this.firstVowel == null)
                    this.firstVowel = turkishCharOfSecondStr;
                this.lastVowel = turkishCharOfSecondStr;
            }
        }
    }

    public TurkishSequence(final TurkishChar[] turkishChars) {
        StringBuilder underlyingStringBuilder = new StringBuilder();
        this.count = turkishChars.length;
        this.chars = new TurkishChar[this.count];
        for (int i = 0; i < this.count; i++) {
            TurkishChar turkishChar = turkishChars[i];
            this.chars[i] = turkishChar;
            if (turkishChar.getLetter().isVowel()) {
                if (this.firstVowel == null)
                    this.firstVowel = turkishChar;
                this.lastVowel = turkishChar;
            }

            underlyingStringBuilder.append(turkishChar.getCharValue());
        }

        this.underlyingString = underlyingStringBuilder.toString();
    }

    public TurkishSequence append(TurkishChar turkishChar) {
        return new TurkishSequence(this, String.valueOf(turkishChar.getCharValue()));
    }

    public TurkishSequence append(String str) {
        if (StringUtils.isEmpty(str))
            return this;
        else
            return new TurkishSequence(this, str);
    }

    public TurkishSequence subsequence(int beginIndex) {
        //TODO: create a version which works with negatives!
        return this.subsequence(beginIndex, this.count);
    }

    public TurkishSequence subsequence(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > this.count) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        if (beginIndex > endIndex) {
            throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
        }

        if (((beginIndex == 0) && (endIndex == this.count)))
            return this;

        return new TurkishSequence(Arrays.copyOfRange(this.chars, beginIndex, endIndex));
    }

    public TurkishSequence voiceLastLetterIfPossible() {
        final TurkishChar lastChar = this.getLastChar();
        final TurkicLetter letter = lastChar.getLetter();
        final TurkicLetter voicedLetter = TurkishAlphabet.voice(letter);
        if (voicedLetter != null)
            return this.subsequence(0, this.count - 1).append(voicedLetter.charValue() + "");
        else
            return this;
    }

    public TurkishChar[] getChars() {
        return chars;
    }

    public String getUnderlyingString() {
        return underlyingString;
    }

    public int length() {
        return this.count;
    }

    public boolean startsWith(TurkishSequence str) {
        return this.underlyingString.startsWith(str.getUnderlyingString());
    }

    public String substring(int beginIndex) {
        //TODO: create a version which works with negatives!
        return this.underlyingString.substring(beginIndex);
    }

    private String substring(int beginIndex, int endIndex) {
        return this.underlyingString.substring(beginIndex, endIndex);
    }

    public boolean isBlank() {
        return StringUtils.isBlank(this.underlyingString);
    }

    public TurkishChar getLastChar() {
        if (this.count == 0)
            return null;
        else
            return this.charAt(this.count - 1);
    }

    public TurkishChar charAt(int index) {
        return this.chars[index];
    }

    public TurkishChar getLastVowel() {
        return this.lastVowel;
    }

    public TurkishChar getFirstVowel() {
        return this.firstVowel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TurkishSequence that = (TurkishSequence) o;

        if (count != that.count) return false;
        if (!underlyingString.equals(that.underlyingString)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return underlyingString.hashCode();
    }

    @Override
    public String toString() {
        return "TurkishSequence{" +
                "underlyingString='" + underlyingString + '\'' +
                '}';
    }
}
