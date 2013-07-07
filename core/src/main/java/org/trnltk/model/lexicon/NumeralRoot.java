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

package org.trnltk.model.lexicon;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.letter.TurkishSequence;

/**
 * A {@link Root} implementation special for numerals which holds the text presentation for a numeral.
 * <p/>
 * The text representation is not computed by this class. It is passed.
 *
 * @see {@link org.trnltk.numeral.DigitsToTextConverter}
 */
public class NumeralRoot implements Root {

    private final ImmutableRoot immutableRoot;
    private final String underlyingNumeralText;

    public NumeralRoot(final TurkishSequence partialInput, final String underlyingNumeralText, final SecondaryPos secondaryPos,
                       final ImmutableSet<PhoneticAttribute> phoneticAttributes) {
        Validate.notNull(partialInput);

        final String partialInputUnderlyingString = partialInput.getUnderlyingString();

        Validate.notEmpty(partialInputUnderlyingString);
        Validate.notEmpty(underlyingNumeralText);
        Validate.notNull(secondaryPos);
        Validate.isTrue(SecondaryPos.NUMERAL_APPLICABLE.contains(secondaryPos));

        final PrimaryPos primaryPos = PrimaryPos.Numeral;
        final ImmutableSet<LexemeAttribute> lexemeAttributes = ImmutableSet.of();

        final Lexeme lexeme = new ImmutableLexeme(partialInputUnderlyingString, partialInputUnderlyingString, primaryPos,
                secondaryPos, lexemeAttributes);

        final ImmutableSet<PhoneticExpectation> phoneticExpectations = null;
        this.immutableRoot = new ImmutableRoot(partialInput, lexeme, phoneticAttributes, phoneticExpectations);

        this.underlyingNumeralText = underlyingNumeralText;
    }

    @Override
    public TurkishSequence getSequence() {
        return immutableRoot.getSequence();
    }

    @Override
    public Lexeme getLexeme() {
        return immutableRoot.getLexeme();
    }

    @Override
    public ImmutableSet<PhoneticAttribute> getPhoneticAttributes() {
        return immutableRoot.getPhoneticAttributes();
    }

    @Override
    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations() {
        return immutableRoot.getPhoneticExpectations();
    }

    /**
     * @return Underlying text which is i.e. "yirmisekiz" for numeral "28"
     */
    public String getUnderlyingNumeralText() {
        return underlyingNumeralText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumeralRoot that = (NumeralRoot) o;

        if (!immutableRoot.equals(that.immutableRoot)) return false;
        if (!underlyingNumeralText.equals(that.underlyingNumeralText)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = immutableRoot.hashCode();
        result = 31 * result + underlyingNumeralText.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NumeralRoot{" +
                "immutableRoot=" + immutableRoot +
                ", underlyingNumeralText='" + underlyingNumeralText + '\'' +
                '}';
    }
}
