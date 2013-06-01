package org.trnltk.morphology.model;

import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.lexicon.tr.PhoneticExpectation;

import java.util.Set;

public interface Root {
    public TurkishSequence getSequence();

    public Lexeme getLexeme();

    public Set<PhoneticAttribute> getPhoneticAttributes();

    public Set<PhoneticExpectation> getPhoneticExpectations();
}
