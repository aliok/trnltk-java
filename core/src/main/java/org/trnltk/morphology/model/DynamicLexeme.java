package org.trnltk.morphology.model;

import org.apache.commons.collections.CollectionUtils;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.EnumSet;

public class DynamicLexeme implements Lexeme {
    private String lemma;
    private String lemmaRoot;
    private PrimaryPos primaryPos;
    private SecondaryPos secondaryPos;
    private EnumSet<LexemeAttribute> lexemeAttributes;

    public DynamicLexeme(String lemma, String lemmaRoot, PrimaryPos primaryPos, SecondaryPos secondaryPos, EnumSet<LexemeAttribute> lexemeAttributes) {
        this.lemma = lemma;
        this.lemmaRoot = lemmaRoot;
        this.primaryPos = primaryPos;
        this.secondaryPos = secondaryPos;
        this.lexemeAttributes = CollectionUtils.isEmpty(lexemeAttributes) ? EnumSet.noneOf(LexemeAttribute.class) : lexemeAttributes;
    }

    public DynamicLexeme(final DynamicLexeme other) {
        this.lemma = other.lemma;
        this.lemmaRoot = other.lemmaRoot;
        this.primaryPos = other.primaryPos;
        this.secondaryPos = other.secondaryPos;
        this.lexemeAttributes = EnumSet.copyOf(other.lexemeAttributes);
    }

    @Override
    public String toString() {
        return "DynamicLexeme{" +
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

        DynamicLexeme that = (DynamicLexeme) o;

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

    @Override
    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    @Override
    public String getLemmaRoot() {
        return lemmaRoot;
    }

    public void setLemmaRoot(String lemmaRoot) {
        this.lemmaRoot = lemmaRoot;
    }

    @Override
    public PrimaryPos getPrimaryPos() {
        return primaryPos;
    }

    public void setPrimaryPos(PrimaryPos primaryPos) {
        this.primaryPos = primaryPos;
    }

    @Override
    public SecondaryPos getSecondaryPos() {
        return secondaryPos;
    }

    public void setSecondaryPos(SecondaryPos secondaryPos) {
        this.secondaryPos = secondaryPos;
    }

    @Override
    public EnumSet<LexemeAttribute> getAttributes() {
        return lexemeAttributes;
    }

    public void setAttributes(EnumSet<LexemeAttribute> lexemeAttributes) {
        this.lexemeAttributes = lexemeAttributes;
    }
}
