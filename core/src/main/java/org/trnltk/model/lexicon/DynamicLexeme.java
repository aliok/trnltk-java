/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.trnltk.model.lexicon;

import org.apache.commons.collections.CollectionUtils;

import java.util.EnumSet;

/**
 * A mutable lexeme implementation. One example use case is brute force parsing where
 * the lexemes are guessed and created on the fly.
 *
 * @see Lexeme
 */
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
        else if (!lemmaRoot.equals(that.lemmaRoot)) return false;
        else if (!lexemeAttributes.equals(that.lexemeAttributes)) return false;
        else if (primaryPos != that.primaryPos) return false;
        else if (secondaryPos != that.secondaryPos) return false;

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

    @SuppressWarnings("UnusedDeclaration")
    public void setPrimaryPos(PrimaryPos primaryPos) {
        this.primaryPos = primaryPos;
    }

    @Override
    public SecondaryPos getSecondaryPos() {
        return secondaryPos;
    }

    @SuppressWarnings("UnusedDeclaration")
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
