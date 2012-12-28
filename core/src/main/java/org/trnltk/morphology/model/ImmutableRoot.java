package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import zemberek3.lexicon.tr.PhonAttr;
import org.trnltk.morphology.phonetics.PhoneticExpectation;

public final class ImmutableRoot implements Root {
    private final TurkishSequence sequence;
    private final Lexeme lexeme;
    private final ImmutableSet<PhonAttr> phonAttrs;       //immutable to prevent change of underlying set
    private final ImmutableSet<PhoneticExpectation> phoneticExpectations;   //immutable to prevent change of underlying set

    public ImmutableRoot(TurkishSequence sequence, Lexeme lexeme, ImmutableSet<PhonAttr> phonAttrs, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this.sequence = sequence;
        this.lexeme = lexeme;
        this.phonAttrs = phonAttrs == null ? ImmutableSet.<PhonAttr>of() : phonAttrs;
        this.phoneticExpectations = phoneticExpectations == null ? ImmutableSet.<PhoneticExpectation>of() : phoneticExpectations;
    }

    public ImmutableRoot(String str, Lexeme lexeme, ImmutableSet<PhonAttr> phonAttrs, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this(new TurkishSequence(str), lexeme, phonAttrs, phoneticExpectations);
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
    public ImmutableSet<PhonAttr> getPhonAttrs() {
        return phonAttrs;
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
        if (phonAttrs != null ? !phonAttrs.equals(that.phonAttrs) : that.phonAttrs != null)
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
        result = 31 * result + (phonAttrs != null ? phonAttrs.hashCode() : 0);
        result = 31 * result + (phoneticExpectations != null ? phoneticExpectations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImmutableRoot{" +
                "sequence=" + sequence +
                ", lexeme=" + lexeme +
                ", phonAttrs=" + phonAttrs +
                ", phoneticExpectations=" + phoneticExpectations +
                '}';
    }
}
