package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import zemberek3.lexicon.tr.PhonAttr;
import org.trnltk.morphology.phonetics.PhoneticExpectation;

public interface Root {
    public TurkishSequence getSequence();

    public Lexeme getLexeme();

    public ImmutableSet<PhonAttr> getPhonAttrs();

    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations();
}
