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

package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import java.util.Set;

public class SuffixFormGraphNodeKey {

    private final ImmutableSet<PhoneticAttribute> phonAttrSet;
    private final SuffixGraphState state;

    public SuffixFormGraphNodeKey(SuffixGraphState state, Set<PhoneticAttribute> phonAttrSet) {
        this.state = state;
        this.phonAttrSet = Sets.immutableEnumSet(phonAttrSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormGraphNodeKey suffixFormGraphNodeKey = (SuffixFormGraphNodeKey) o;

        if (!phonAttrSet.equals(suffixFormGraphNodeKey.phonAttrSet)) return false;
        if (!state.equals(suffixFormGraphNodeKey.state)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + phonAttrSet.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SuffixFormGraphNodeKey{" +
                "state='" + state + '\'' +
                ", phonAttrSet=" + phonAttrSet +
                '}';
    }

    public ImmutableSet<PhoneticAttribute> getPhonAttrSet() {
        return phonAttrSet;
    }

    public SuffixGraphState getState() {
        return state;
    }
}