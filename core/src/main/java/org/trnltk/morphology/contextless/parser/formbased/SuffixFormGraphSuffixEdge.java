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
import org.trnltk.model.lexicon.PhoneticExpectation;
import org.trnltk.model.suffix.SuffixFormApplication;

/**
 * Defines a transition from a {@link SuffixFormGraphNode} to another one.
 * <p/>
 * This is pre computed when a {@link ContextlessMorphologicParser} is created.
 * <p/>
 * An edge defines source node in the {@link SuffixFormGraph}, a phonetic attributes combination that is applicable,
 * a target node in the {@link SuffixFormGraph} and what would be the new
 * phonetic attributes combination when the transition is applied.
 * <p/>
 * An edge and a suffix form application results in a transition in {@link SuffixFormGraph}.
 */
public class SuffixFormGraphSuffixEdge {

    private final SuffixFormGraphNode targetSuffixFormGraphNode;
    private final SuffixFormApplication suffixFormApplication;
    private final ImmutableSet<PhoneticExpectation> phoneticExpectations;

    public SuffixFormGraphSuffixEdge(SuffixFormGraphNode targetSuffixFormGraphNode, SuffixFormApplication suffixFormApplication, ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this.targetSuffixFormGraphNode = targetSuffixFormGraphNode;
        this.suffixFormApplication = suffixFormApplication;
        this.phoneticExpectations = phoneticExpectations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormGraphSuffixEdge that = (SuffixFormGraphSuffixEdge) o;

        if (suffixFormApplication != null ? !suffixFormApplication.equals(that.suffixFormApplication) : that.suffixFormApplication != null)
            return false;
        if (targetSuffixFormGraphNode != null ? !targetSuffixFormGraphNode.equals(that.targetSuffixFormGraphNode) : that.targetSuffixFormGraphNode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetSuffixFormGraphNode != null ? targetSuffixFormGraphNode.hashCode() : 0;
        result = 31 * result + (suffixFormApplication != null ? suffixFormApplication.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SuffixFormGraphSuffixEdge{" +
                "targetSuffixFormGraphNode=" + targetSuffixFormGraphNode +
                ", suffixFormApplication=" + suffixFormApplication +
                '}';
    }

    public SuffixFormGraphNode getTargetSuffixFormGraphNode() {
        return targetSuffixFormGraphNode;
    }

    public SuffixFormApplication getSuffixFormApplication() {
        return suffixFormApplication;
    }

    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations() {
        return phoneticExpectations;
    }
}