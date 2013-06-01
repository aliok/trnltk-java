package org.trnltk.morphology.model;

import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticExpectation;

import java.util.Set;

public interface Root {
    public TurkishSequence getSequence();

    public Lexeme getLexeme();

    public Set<PhoneticAttribute> getPhoneticAttributes();

    public Set<PhoneticExpectation> getPhoneticExpectations();
}
