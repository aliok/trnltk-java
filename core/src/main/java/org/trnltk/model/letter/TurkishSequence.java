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

/**
 * An immutable sequence of {@link TurkishChar}s.
 */
public class TurkishSequence {
    private final String underlyingString;
    private final TurkishChar[] chars;
    private TurkishChar firstVowel;
    private TurkishChar lastVowel;
    private int count;

    /**
     * Create a {@link TurkishSequence} instance from a string.
     * <p/>
     * Chars are one by one converted to {@link TurkishChar}s, thus if you are going to clone a {@link TurkishSequence}
     * it is advised to use the method {@link TurkishSequence#TurkishSequence(TurkishSequence)}
     *
     * @param underlyingString String to convert to {@link TurkishSequence}
     */
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

    /**
     * Deep-copy constructor. Copies the properties from argument and creates a new {@link TurkishSequence}.
     *
     * @param toClone Sequence to copy
     */
    @SuppressWarnings("UnusedDeclaration")
    public TurkishSequence(final TurkishSequence toClone) {
        this(toClone, "");
    }

    /**
     * Creates a new {@link TurkishSequence} from the given chars.
     *
     * @param turkishChars Char array to convert to a {@link TurkishSequence}
     */
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

    private TurkishSequence(final TurkishSequence first, final String strSecond) {
        // some notes here:
        // - TurkishChar instances are immutable. Thus it is safe to reference the chars of param 'first'.

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

    /**
     * Appends the given char to current sequence and returns a new sequence. Current sequence will not be modified.
     *
     * @param turkishChar Char to append
     * @return New sequence
     */
    public TurkishSequence append(TurkishChar turkishChar) {
        return new TurkishSequence(this, String.valueOf(turkishChar.getCharValue()));
    }

    /**
     * Appends the given string to current sequence and returns a new sequence. Current sequence will not be modified.
     *
     * @param str String to append
     * @return New sequence
     */
    public TurkishSequence append(String str) {
        if (StringUtils.isEmpty(str))
            return this;
        else
            return new TurkishSequence(this, str);
    }

    /**
     * Creates a subsequence starting at index {@code beginIndex} and ending at last character.
     *
     * @param beginIndex The begin index
     * @return A new {@link TurkishSequence} instance as subsequence
     * @see TurkishSequence#subsequence(int, int)
     */
    public TurkishSequence subsequence(int beginIndex) {
        //TODO: create a version which works with negatives! like the Python slicing
        return this.subsequence(beginIndex, this.count);
    }

    /**
     * Creates a subsequence starting at index {@code beginIndex} and ending at index {@code endIndex}.
     * <p/>
     * Returned subsequence is not a view, but a full-copy.
     *
     * @param beginIndex The begin index
     * @param endIndex   The end index
     * @return A new {@link TurkishSequence} instance as subsequence
     */
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


    /**
     * If last letter of the sequence is voicable, then returns a new sequence with last letter voiced.
     * If not, returns the {@link TurkishSequence} instance method triggered on.
     *
     * @return The sequence with last letter voiced if last letter is voicable
     * @see TurkishAlphabet#voice(TurkicLetter)
     */
    public TurkishSequence voiceLastLetterIfPossible() {
        final TurkishChar lastChar = this.getLastChar();
        final TurkicLetter letter = lastChar.getLetter();
        final TurkicLetter voicedLetter = TurkishAlphabet.voice(letter);
        if (voicedLetter != null)
            return this.subsequence(0, this.count - 1).append(voicedLetter.charValue() + "");
        else
            return this;
    }

    /**
     * Returns shallow clone of the underlying {@link TurkishChar}s.
     *
     * @return Shallow clone of underlying chars
     */
    public TurkishChar[] getChars() {
        return chars.clone();
    }

    /**
     * @return Underlying string of the sequence
     */
    public String getUnderlyingString() {
        return underlyingString;
    }

    /**
     * @return length of the sequence
     */
    public int length() {
        return this.count;
    }

    /**
     * Checks if given sequence is beginning of the sequence.
     *
     * @param str Sequence to check if starts with
     * @return true/false
     */
    public boolean startsWith(TurkishSequence str) {
        return this.underlyingString.startsWith(str.getUnderlyingString());
    }

    /**
     * Returns the substring starting at index {@code beginIndex} and ends at end of the sequence. Mimics the behavior of
     * {@link String#substring(int)}
     * <p/>
     * If you need subsequence instead of substring, use {@link TurkishSequence#subsequence(int)} as it is more efficient.
     *
     * @param beginIndex The begin index
     * @return Substring
     */
    public String substring(int beginIndex) {
        //TODO: create a version which works with negatives!
        return this.underlyingString.substring(beginIndex);
    }

    /**
     * Returns the substring starting at index {@code beginIndex} and ends at index {@code endIndex}. Mimics the behavior of
     * {@link String#substring(int, int)}
     * <p/>
     * If you need subsequence instead of substring, use {@link TurkishSequence#subsequence(int, int)} as it is more efficient.
     *
     * @param beginIndex The begin index
     * @param endIndex The end index
     * @return Substring
     */
    @SuppressWarnings("UnusedDeclaration")
    private String substring(int beginIndex, int endIndex) {
        return this.underlyingString.substring(beginIndex, endIndex);
    }

    /**
     * Convenience method to check if the sequence is blank (blank != empty. empty ⊂ blank).
     *
     * @see org.apache.commons.lang3.StringUtils#isBlank(CharSequence)
     *
     * @return true/false
     */
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

        return count == that.count && underlyingString.equals(that.underlyingString);
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
