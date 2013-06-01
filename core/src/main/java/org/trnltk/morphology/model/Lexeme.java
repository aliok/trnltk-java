package org.trnltk.morphology.model;

import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.Set;

public interface Lexeme {
    String getLemma();

    String getLemmaRoot();

    PrimaryPos getPrimaryPos();

    SecondaryPos getSecondaryPos();

    Set<LexemeAttribute> getAttributes();
}
