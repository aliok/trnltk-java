package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticExpectation;

public final class ImmutableRoot implements Root {
    private final TurkishSequence sequence;
    private final Lexeme lexeme;
    private final ImmutableSet<PhoneticAttribute> phoneticAttributes;       //immutable to prevent change of underlying set
    private final ImmutableSet<PhoneticExpectation> phoneticExpectations;   //immutable to prevent change of underlying set

    public ImmutableRoot(TurkishSequence sequence, Lexeme lexeme, ImmutableSet<PhoneticAttribute> phoneticAttributes, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this.sequence = sequence;
        this.lexeme = lexeme;
        this.phoneticAttributes = phoneticAttributes == null ? ImmutableSet.<PhoneticAttribute>of() : phoneticAttributes;
        this.phoneticExpectations = phoneticExpectations == null ? ImmutableSet.<PhoneticExpectation>of() : phoneticExpectations;
    }

    public ImmutableRoot(String str, Lexeme lexeme, ImmutableSet<PhoneticAttribute> phoneticAttributes, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this(new TurkishSequence(str), lexeme, phoneticAttributes, phoneticExpectations);
    }

    @Override
    public TurkishSequence getSequence() {
        return this.sequence;
    }

    @Override
    public Lexeme getLexeme() {
        return this.lexeme;
    }

    @Override
    public ImmutableSet<PhoneticAttribute> getPhoneticAttributes() {
        return phoneticAttributes;
    }

    @Override
    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations() {
        return phoneticExpectations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableRoot that = (ImmutableRoot) o;

        if (!lexeme.equals(that.lexeme)) return false;
        if (phoneticAttributes != null ? !phoneticAttributes.equals(that.phoneticAttributes) : that.phoneticAttributes != null)
            return false;
        if (phoneticExpectations != null ? !phoneticExpectations.equals(that.phoneticExpectations) : that.phoneticExpectations != null)
            return false;
        if (!sequence.equals(that.sequence)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sequence.hashCode();
        result = 31 * result + lexeme.hashCode();
        result = 31 * result + (phoneticAttributes != null ? phoneticAttributes.hashCode() : 0);
        result = 31 * result + (phoneticExpectations != null ? phoneticExpectations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImmutableRoot{" +
                "sequence=" + sequence +
                ", lexeme=" + lexeme +
                ", phoneticAttributes=" + phoneticAttributes +
                ", phoneticExpectations=" + phoneticExpectations +
                '}';
    }
}
