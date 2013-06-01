package org.trnltk.morphology.model;

import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticExpectation;

import java.util.EnumSet;

public class DynamicRoot implements Root {
    private TurkishSequence sequence;
    private DynamicLexeme lexeme;
    private EnumSet<PhoneticAttribute> phoneticAttributes;
    private EnumSet<PhoneticExpectation> phoneticExpectations;

    public DynamicRoot(TurkishSequence sequence, DynamicLexeme lexeme, EnumSet<PhoneticAttribute> phoneticAttributes, EnumSet<PhoneticExpectation> phoneticExpectations) {
        this.sequence = sequence;
        this.lexeme = lexeme;
        this.phoneticAttributes = phoneticAttributes;
        this.phoneticExpectations = phoneticExpectations;
    }

    public DynamicRoot(final DynamicRoot other) {
        this.sequence = other.sequence;
        this.lexeme = new DynamicLexeme(other.lexeme);
        this.phoneticAttributes = EnumSet.copyOf(other.phoneticAttributes);
        this.phoneticExpectations = EnumSet.copyOf(other.phoneticExpectations);
    }

    @Override
    public String toString() {
        return "DynamicRoot{" +
                "sequence=" + sequence +
                ", lexeme=" + lexeme +
                ", phoneticAttributes=" + phoneticAttributes +
                ", phoneticExpectations=" + phoneticExpectations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DynamicRoot that = (DynamicRoot) o;

        if (!lexeme.equals(that.lexeme)) return false;
        if (!phoneticAttributes.equals(that.phoneticAttributes)) return false;
        if (!phoneticExpectations.equals(that.phoneticExpectations)) return false;
        if (!sequence.equals(that.sequence)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sequence.hashCode();
        result = 31 * result + lexeme.hashCode();
        result = 31 * result + phoneticAttributes.hashCode();
        result = 31 * result + phoneticExpectations.hashCode();
        return result;
    }

    @Override
    public TurkishSequence getSequence() {
        return sequence;
    }

    public void setSequence(TurkishSequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public DynamicLexeme getLexeme() {
        return lexeme;
    }

    public void setLexeme(DynamicLexeme lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public EnumSet<PhoneticAttribute> getPhoneticAttributes() {
        return phoneticAttributes;
    }

    public void setPhoneticAttributes(EnumSet<PhoneticAttribute> phoneticAttributes) {
        this.phoneticAttributes = phoneticAttributes;
    }

    @Override
    public EnumSet<PhoneticExpectation> getPhoneticExpectations() {
        return phoneticExpectations;
    }

    public void setPhoneticExpectations(EnumSet<PhoneticExpectation> phoneticExpectations) {
        this.phoneticExpectations = phoneticExpectations;
    }
}
