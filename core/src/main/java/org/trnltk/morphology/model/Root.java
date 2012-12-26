package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import org.trnltk.morphology.phonetics.PhoneticAttribute;
import org.trnltk.morphology.phonetics.PhoneticExpectation;

public interface Root {
    public TurkishSequence getSequence();

    public Lexeme getLexeme();

    public ImmutableSet<PhoneticAttribute> getPhoneticAttributes();

    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations();
}
