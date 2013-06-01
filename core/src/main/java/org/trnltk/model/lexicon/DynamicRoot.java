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

import org.trnltk.model.letter.TurkishSequence;

import java.util.EnumSet;

public class DynamicRoot implements Root {
    private TurkishSequence sequence;
    private DynamicLexeme lexeme;
    private EnumSet<PhoneticAttribute> phoneticAttributes;
    private EnumSet<PhoneticExpectation> phoneticExpectations;

    public DynamicRoot(TurkishSequence sequence, DynamicLexeme lexeme, EnumSet<PhoneticAttribute> phoneticAttributes, EnumSet<PhoneticExpectation> phoneticExpectations) {
        this.sequence = sequence;
        this.lexeme = lexeme;
        this.phoneticAttributes = phoneticAttributes;
        this.phoneticExpectations = phoneticExpectations;
    }

    public DynamicRoot(final DynamicRoot other) {
        this.sequence = other.sequence;
        this.lexeme = new DynamicLexeme(other.lexeme);
        this.phoneticAttributes = EnumSet.copyOf(other.phoneticAttributes);
        this.phoneticExpectations = EnumSet.copyOf(other.phoneticExpectations);
    }

    @Override
    public String toString() {
        return "DynamicRoot{" +
                "sequence=" + sequence +
                ", lexeme=" + lexeme +
                ", phoneticAttributes=" + phoneticAttributes +
                ", phoneticExpectations=" + phoneticExpectations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DynamicRoot that = (DynamicRoot) o;

        if (!lexeme.equals(that.lexeme)) return false;
        if (!phoneticAttributes.equals(that.phoneticAttributes)) return false;
        if (!phoneticExpectations.equals(that.phoneticExpectations)) return false;
        if (!sequence.equals(that.sequence)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sequence.hashCode();
        result = 31 * result + lexeme.hashCode();
        result = 31 * result + phoneticAttributes.hashCode();
        result = 31 * result + phoneticExpectations.hashCode();
        return result;
    }

    @Override
    public TurkishSequence getSequence() {
        return sequence;
    }

    public void setSequence(TurkishSequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public DynamicLexeme getLexeme() {
        return lexeme;
    }

    public void setLexeme(DynamicLexeme lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public EnumSet<PhoneticAttribute> getPhoneticAttributes() {
        return phoneticAttributes;
    }

    public void setPhoneticAttributes(EnumSet<PhoneticAttribute> phoneticAttributes) {
        this.phoneticAttributes = phoneticAttributes;
    }

    @Override
    public EnumSet<PhoneticExpectation> getPhoneticExpectations() {
        return phoneticExpectations;
    }

    public void setPhoneticExpectations(EnumSet<PhoneticExpectation> phoneticExpectations) {
        this.phoneticExpectations = phoneticExpectations;
    }
}
