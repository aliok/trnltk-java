package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

public class ImmutableLexeme implements Lexeme {
    private final String lemma;
    private final String lemmaRoot;
    private final PrimaryPos primaryPos;
    private final SecondaryPos secondaryPos;
    private final ImmutableSet<LexemeAttribute> lexemeAttributes;

    public ImmutableLexeme(String lemma, String lemmaRoot, PrimaryPos primaryPos, SecondaryPos secondaryPos, ImmutableSet<LexemeAttribute> lexemeAttributes) {
        this.lemma = lemma;
        this.lemmaRoot = lemmaRoot;
        this.primaryPos = primaryPos;
        this.secondaryPos = secondaryPos;
        this.lexemeAttributes = CollectionUtils.isEmpty(lexemeAttributes) ? ImmutableSet.<LexemeAttribute>of() : lexemeAttributes;
    }

    @Override
    public String getLemma() {
        return lemma;
    }

    @Override
    public String getLemmaRoot() {
        return lemmaRoot;
    }

    @Override
    public PrimaryPos getPrimaryPos() {
        return primaryPos;
    }

    @Override
    public SecondaryPos getSecondaryPos() {
        return secondaryPos;
    }

    @Override
    public ImmutableSet<LexemeAttribute> getAttributes() {
        return lexemeAttributes;
    }

    @Override
    public String toString() {
        return "ImmutableLexeme{" +
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

        ImmutableLexeme that = (ImmutableLexeme) o;

        if (!lemma.equals(that.lemma)) return false;
        if (!lemmaRoot.equals(that.lemmaRoot)) return false;
        if (!lexemeAttributes.equals(that.lexemeAttributes)) return false;
        if (primaryPos != that.primaryPos) return false;
        if (secondaryPos != that.secondaryPos) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lemma.hashCode();
        result = 31 * result + lemmaRoot.hashCode();
        result = 31 * result + primaryPos.hashCode();
        result = 31 * result + (secondaryPos != null ? secondaryPos.hashCode() : 0);
        result = 31 * result + lexemeAttributes.hashCode();
        return result;
    }
}
