package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.Validate;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticExpectation;
import zemberek3.shared.lexicon.PrimaryPos;

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
