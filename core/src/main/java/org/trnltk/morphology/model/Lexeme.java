package org.trnltk.morphology.model;

import zemberek3.shared.lexicon.PrimaryPos;

import java.util.Set;

public interface Lexeme {
    String getLemma();

    String getLemmaRoot();

    PrimaryPos getPrimaryPos();

    SecondaryPos getSecondaryPos();

    Set<LexemeAttribute> getAttributes();
}
