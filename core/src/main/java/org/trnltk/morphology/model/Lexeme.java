package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import zemberek3.lexicon.PrimaryPos;

public class Lexeme {
    private final String lemma;
    private final String lemmaRoot;
    private final PrimaryPos primaryPos;
    private final SecondaryPos secondaryPos;
    private final ImmutableSet<LexemeAttribute> lexemeAttributes;

    public Lexeme(String lemma, String lemmaRoot, PrimaryPos primaryPos, SecondaryPos secondaryPos, ImmutableSet<LexemeAttribute> lexemeAttributes) {
        this.lemma = lemma;
        this.lemmaRoot = lemmaRoot;
        this.primaryPos = primaryPos;
        this.secondaryPos = secondaryPos;
        this.lexemeAttributes = CollectionUtils.isEmpty(lexemeAttributes) ? ImmutableSet.<LexemeAttribute>of() : lexemeAttributes;
    }

    public String getLemma() {
        return lemma;
    }

    public String getLemmaRoot() {
        return lemmaRoot;
    }

    public PrimaryPos getPrimaryPos() {
        return primaryPos;
    }

    public SecondaryPos getSecondaryPos() {
        return secondaryPos;
    }

    public ImmutableSet<LexemeAttribute> getAttributes() {
        return lexemeAttributes;
    }

    @Override
    public String toString() {
        return "Lexeme{" +
                "lemma='" + lemma + '\'' +
                ", lemmaRoot='" + lemmaRoot + '\'' +
                ", primaryPos=" + primaryPos +
                ", secondaryPos=" + secondaryPos +
                ", lexemeAttributes=" + lexemeAttributes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lexeme lexeme = (Lexeme) o;

        if (lemma != null ? !lemma.equals(lexeme.lemma) : lexeme.lemma != null) return false;
        if (lemmaRoot != null ? !lemmaRoot.equals(lexeme.lemmaRoot) : lexeme.lemmaRoot != null) return false;
        if (lexemeAttributes != null ? !lexemeAttributes.equals(lexeme.lexemeAttributes) : lexeme.lexemeAttributes != null)
            return false;
        if (secondaryPos != lexeme.secondaryPos) return false;
        if (primaryPos != lexeme.primaryPos) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lemma != null ? lemma.hashCode() : 0;
        result = 31 * result + (lemmaRoot != null ? lemmaRoot.hashCode() : 0);
        result = 31 * result + (primaryPos != null ? primaryPos.hashCode() : 0);
        result = 31 * result + (secondaryPos != null ? secondaryPos.hashCode() : 0);
        result = 31 * result + (lexemeAttributes != null ? lexemeAttributes.hashCode() : 0);
        return result;
    }
}
