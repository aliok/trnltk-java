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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.trnltk.model.suffix.SuffixForm;
import org.trnltk.model.suffix.SuffixFormApplication;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.model.lexicon.PhoneticAttribute;
import org.trnltk.model.lexicon.PhoneticExpectation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A node in the {@link SuffixFormGraph} where states to go are stored with for each suffix and for each distinct
 * possible phonetic attribute combination.
 */
public class SuffixFormGraphNode {
    private final SuffixFormGraphNodeKey suffixFormGraphNodeKey;
    private final Set<SuffixFormGraphSuffixEdge> edges = new HashSet<SuffixFormGraphSuffixEdge>();
    private final ImmutableSet<PhoneticAttribute> currentPhonAttrSet;
    private final SuffixGraphStateType suffixGraphStateType;

    private boolean explored;

    SuffixFormGraphNode(SuffixFormGraphNodeKey suffixFormGraphNodeKey, SuffixGraphStateType suffixGraphStateType, ImmutableSet<PhoneticAttribute> currentPhonAttrSet) {
        this.suffixFormGraphNodeKey = suffixFormGraphNodeKey;
        this.currentPhonAttrSet = currentPhonAttrSet;   //TODO: intern?
        this.suffixGraphStateType = suffixGraphStateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormGraphNode suffixFormGraphNode = (SuffixFormGraphNode) o;

        if (!suffixFormGraphNodeKey.equals(suffixFormGraphNode.suffixFormGraphNodeKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return suffixFormGraphNodeKey.hashCode();
    }

    @Override
    public String toString() {
        return "SuffixFormGraphNode{" +
                "suffixFormGraphNodeKey=" + suffixFormGraphNodeKey +
                ", suffixGraphStateType=" + suffixGraphStateType +
                ", currentPhonAttrSet=" + currentPhonAttrSet +
                '}';
    }

    public SuffixFormGraphNodeKey getSuffixFormGraphNodeKey() {
        return suffixFormGraphNodeKey;
    }

    public ImmutableSet<PhoneticAttribute> getCurrentPhonAttrSet() {
        return currentPhonAttrSet;
    }

    public SuffixFormGraphSuffixEdge addSuffixFormEdge(SuffixFormGraphNode targetSuffixFormGraphNode, SuffixFormApplication suffixFormApplication, Collection<PhoneticExpectation> phoneticExpectations) {
        final SuffixFormGraphSuffixEdge edge = new SuffixFormGraphSuffixEdge(targetSuffixFormGraphNode, suffixFormApplication,
                CollectionUtils.isNotEmpty(phoneticExpectations) ? ImmutableSet.copyOf(phoneticExpectations) : ImmutableSet.<PhoneticExpectation>of());
        this.edges.add(edge);
        return edge;
    }

    public SuffixFormGraphSuffixEdge addEmptySuffixFormEdge(SuffixFormGraphNode targetSuffixFormGraphNode, SuffixForm suffixForm) {
        return this.addSuffixFormEdge(
                targetSuffixFormGraphNode,
                new SuffixFormApplication(suffixForm, StringUtils.EMPTY, StringUtils.EMPTY),
                null
        );
    }

    public Set<SuffixFormGraphSuffixEdge> getEdges() {
        return edges;
    }

    public SuffixGraphStateType getSuffixGraphStateType() {
        return suffixGraphStateType;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }
}