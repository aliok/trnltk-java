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

import com.google.common.collect.ImmutableSet;
import org.trnltk.model.letter.TurkishSequence;

/**
 * An immutable root implementation. One big use case is creating immutable roots from a
 * dictionary, since dictionary items are not going to change.
 *
 * @see Root
 */
public final class ImmutableRoot implements Root {
    private final TurkishSequence sequence;
    private final Lexeme lexeme;
    private final ImmutableSet<PhoneticAttribute> phoneticAttributes;       //immutable to prevent change of underlying set
    private final ImmutableSet<PhoneticExpectation> phoneticExpectations;   //immutable to prevent change of underlying set

    public ImmutableRoot(TurkishSequence sequence, Lexeme lexeme, ImmutableSet<PhoneticAttribute> phoneticAttributes, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this.sequence = sequence;
        this.lexeme = lexeme;
        this.phoneticAttributes = phoneticAttributes == null ? ImmutableSet.<PhoneticAttribute>of() : phoneticAttributes;
        this.phoneticExpectations = phoneticExpectations == null ? ImmutableSet.<PhoneticExpectation>of() : phoneticExpectations;
    }

    public ImmutableRoot(String str, Lexeme lexeme, ImmutableSet<PhoneticAttribute> phoneticAttributes, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this(new TurkishSequence(str), lexeme, phoneticAttributes, phoneticExpectations);
    }

    @Override
    public TurkishSequence getSequence() {
        return this.sequence;
    }

    @Override
    public Lexeme getLexeme() {
        return this.lexeme;
    }

    @Override
    public ImmutableSet<PhoneticAttribute> getPhoneticAttributes() {
        return phoneticAttributes;
    }

    @Override
    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations() {
        return phoneticExpectations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableRoot that = (ImmutableRoot) o;

        if (!lexeme.equals(that.lexeme)) return false;
        if (phoneticAttributes != null ? !phoneticAttributes.equals(that.phoneticAttributes) : that.phoneticAttributes != null)
            return false;
        if (phoneticExpectations != null ? !phoneticExpectations.equals(that.phoneticExpectations) : that.phoneticExpectations != null)
            return false;
        if (!sequence.equals(that.sequence)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sequence.hashCode();
        result = 31 * result + lexeme.hashCode();
        result = 31 * result + (phoneticAttributes != null ? phoneticAttributes.hashCode() : 0);
        result = 31 * result + (phoneticExpectations != null ? phoneticExpectations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImmutableRoot{" +
                "sequence=" + sequence +
                ", lexeme=" + lexeme +
                ", phoneticAttributes=" + phoneticAttributes +
                ", phoneticExpectations=" + phoneticExpectations +
                '}';
    }
}
