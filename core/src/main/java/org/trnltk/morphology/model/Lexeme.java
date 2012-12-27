package org.trnltk.morphology.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import zemberek3.lexicon.PrimaryPos;

public class Lexeme {
    private final String lemma;
    private final String lemmaRoot;
    private final PrimaryPos syntacticCategory;
    private final SecondarySyntacticCategory secondarySyntacticCategory;
    private final ImmutableSet<LexemeAttribute> lexemeAttributes;

    public Lexeme(String lemma, String lemmaRoot, PrimaryPos syntacticCategory, SecondarySyntacticCategory secondarySyntacticCategory, ImmutableSet<LexemeAttribute> lexemeAttributes) {
        this.lemma = lemma;
        this.lemmaRoot = lemmaRoot;
        this.syntacticCategory = syntacticCategory;
        this.secondarySyntacticCategory = secondarySyntacticCategory;
        this.lexemeAttributes = CollectionUtils.isEmpty(lexemeAttributes) ? ImmutableSet.<LexemeAttribute>of() : lexemeAttributes;
    }

    public String getLemma() {
        return lemma;
    }

    public String getLemmaRoot() {
        return lemmaRoot;
    }

    public PrimaryPos getPrimaryPos() {
        return syntacticCategory;
    }

    public SecondarySyntacticCategory getSecondarySyntacticCategory() {
        return secondarySyntacticCategory;
    }

    public ImmutableSet<LexemeAttribute> getAttributes() {
        return lexemeAttributes;
    }

    @Override
    public String toString() {
        return "Lexeme{" +
                "lemma='" + lemma + '\'' +
                ", lemmaRoot='" + lemmaRoot + '\'' +
                ", syntacticCategory=" + syntacticCategory +
                ", secondarySyntacticCategory=" + secondarySyntacticCategory +
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
        if (secondarySyntacticCategory != lexeme.secondarySyntacticCategory) return false;
        if (syntacticCategory != lexeme.syntacticCategory) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lemma != null ? lemma.hashCode() : 0;
        result = 31 * result + (lemmaRoot != null ? lemmaRoot.hashCode() : 0);
        result = 31 * result + (syntacticCategory != null ? syntacticCategory.hashCode() : 0);
        result = 31 * result + (secondarySyntacticCategory != null ? secondarySyntacticCategory.hashCode() : 0);
        result = 31 * result + (lexemeAttributes != null ? lexemeAttributes.hashCode() : 0);
        return result;
    }
}
