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

package org.trnltk.morphology.morphotactics;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.trnltk.model.lexicon.SecondaryPos;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.lexicon.PrimaryPos;

import java.util.HashSet;

public class SuffixGraphState {
    private final String name;
    private final SuffixGraphStateType type;
    private final PrimaryPos primaryPos;
    private final SecondaryPos secondaryPos;
    private ImmutableSet<SuffixEdge> outEdges;

    public SuffixGraphState(String name, SuffixGraphStateType suffixGraphStateType, PrimaryPos primaryPos, SecondaryPos secondaryPos) {
        this.name = name;
        this.type = suffixGraphStateType;
        this.primaryPos = primaryPos;
        this.secondaryPos = secondaryPos;
        this.outEdges = ImmutableSet.of();
    }

    public String getName() {
        return name;
    }

    public PrimaryPos getPrimaryPos() {
        return primaryPos;
    }

    public SecondaryPos getSecondaryPos() {
        return secondaryPos;
    }

    public SuffixGraphStateType getType() {
        return type;
    }

    public ImmutableSet<SuffixEdge> getOutEdges() {
        return this.outEdges;
    }

    public void addOutSuffix(Suffix suffix, SuffixGraphState suffixGraphState) {
        final HashSet<SuffixEdge> tempSet = Sets.newHashSet(outEdges);
        tempSet.add(new SuffixEdge(suffix, suffixGraphState));
        this.outEdges = ImmutableSet.copyOf(tempSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixGraphState that = (SuffixGraphState) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "SuffixGraphState{" +
                "name='" + name + '\'' +
                '}';
    }
}
