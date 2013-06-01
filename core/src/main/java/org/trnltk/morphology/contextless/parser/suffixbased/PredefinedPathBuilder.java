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

package org.trnltk.morphology.contextless.parser.suffixbased;

import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.morphology.model.suffixbased.SuffixForm;
import org.trnltk.morphology.morphotactics.SuffixEdge;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;

public class PredefinedPathBuilder {
    private final SuffixGraph suffixGraph;
    private final SuffixApplier suffixApplier;
    private final HashMultimap<Root, MorphemeContainer> morphemeContainerMap;

    private MorphemeContainer morphemeContainerInProgress;

    public PredefinedPathBuilder(final SuffixGraph suffixGraph, final SuffixApplier suffixApplier, HashMultimap<Root, MorphemeContainer> morphemeContainerMap) {
        this.suffixGraph = suffixGraph;
        this.suffixApplier = suffixApplier;
        this.morphemeContainerMap = morphemeContainerMap;
    }

    public PredefinedPathBuilder root(final Root root) {
        final SuffixGraphState defaultStateForRoot = this.suffixGraph.getDefaultStateForRoot(root);
        if (defaultStateForRoot == null)
            throw new IllegalStateException("No default state found for root " + root);

        this.morphemeContainerInProgress = new MorphemeContainer(root, defaultStateForRoot, StringUtils.EMPTY);
        return this;
    }

    public PredefinedPathBuilder s(String suffixName) {
        return this.s(this.suffixGraph.getSuffix(suffixName));
    }

    public PredefinedPathBuilder s(Suffix suffix) {
        return this.s(suffix, StringUtils.EMPTY);
    }

    public PredefinedPathBuilder s(String suffixName, String suffixFormStr) {
        return this.s(this.suffixGraph.getSuffix(suffixName), suffixFormStr);
    }

    public PredefinedPathBuilder s(Suffix suffix, String suffixFormStr) {
        Validate.notNull(suffix);
        if (StringUtils.isNotEmpty(suffixFormStr))
            Validate.isTrue(StringUtils.isAllLowerCase(suffixFormStr), "SuffixForm str should be lower case! See suffix :" + suffix.getName() + " and suffixFormStr : " + suffixFormStr);
        this.followPath(suffix, suffixFormStr);
        return this;
    }

    public void add() {
        this.morphemeContainerMap.put(this.morphemeContainerInProgress.getRoot(), this.morphemeContainerInProgress);
    }

    private void followPath(final Suffix suffix, final String strSuffixFormApplication) {
        final SuffixGraphState currentState = this.morphemeContainerInProgress.getLastState();

        final TurkishSequence surfaceSoFar = morphemeContainerInProgress.getSurfaceSoFar();
        final TurkishSequence pathResult = surfaceSoFar.append(strSuffixFormApplication);
        SuffixGraphState targetState = this.findTargetState(currentState, suffix);
        if (targetState == null) {
            final Pair<SuffixGraphState, Suffix> suffixGraphStateSuffixPair = this.discoverIntermediateStateAndSuffix(currentState, suffix);
            final SuffixGraphState intermediateState = suffixGraphStateSuffixPair.getLeft();
            final Suffix intermediateSuffix = suffixGraphStateSuffixPair.getRight();
            if (intermediateState == null)
                throw new IllegalStateException(String.format("Also tried to discover intermediate states, but unable to find output state for state %s %s", this.morphemeContainerInProgress.getLastState(), suffix));

            targetState = this.findTargetState(intermediateState, suffix);
            if (targetState == null)
                throw new IllegalStateException("Unable to find output state which has been suggested " +
                        "by intermediate state before, for " + currentState + " " + intermediateState + " " + suffix);


            this.addTransition(StringUtils.EMPTY, intermediateSuffix, intermediateState, surfaceSoFar);
            this.addTransition(strSuffixFormApplication, suffix, targetState, pathResult);
        } else {
            this.addTransition(strSuffixFormApplication, suffix, targetState, pathResult);
        }
    }

    private void addTransition(final String strSuffixFormApplication,
                               final Suffix suffix, final SuffixGraphState targetState, final TurkishSequence wholeSurface) {
        final SuffixForm suffixForm = new ForcedSuffixForm(suffix, strSuffixFormApplication);
        final MorphemeContainer newMorphemeContainer = suffixApplier.trySuffixForm(morphemeContainerInProgress, suffixForm, targetState, wholeSurface);
        if (newMorphemeContainer == null)
            throw new IllegalArgumentException("Unable to add transition " + strSuffixFormApplication + " " + morphemeContainerInProgress.toString());

        this.morphemeContainerInProgress = newMorphemeContainer;
    }

    private SuffixGraphState findTargetState(final SuffixGraphState sourceState, final Suffix suffix) {
        for (SuffixEdge suffixEdge : sourceState.getOutEdges()) {
            if (suffixEdge.getSuffix().equals(suffix))
                return suffixEdge.getTargetState();
        }

        return null;
    }

    private Pair<SuffixGraphState, Suffix> discoverIntermediateStateAndSuffix(final SuffixGraphState sourceState, final Suffix suffix) {
        // if suffix is not directly an edge from sourceState, go successor nodes of sourceState and see if suffix is an edge of them
        // go only one level
        SuffixGraphState foundState = null;
        Suffix foundSuffix = null;

        for (SuffixEdge outSuffixEdge : sourceState.getOutEdges()) {
            final SuffixGraphState outState = outSuffixEdge.getTargetState();
            final Suffix outSuffix = outSuffixEdge.getSuffix();
            for (SuffixEdge deepSuffixEdge : outState.getOutEdges()) {
                final SuffixGraphState deepOutState = deepSuffixEdge.getTargetState();
                final Suffix deepOutSuffix = deepSuffixEdge.getSuffix();

                if (deepOutSuffix.equals(suffix)) {
                    if (foundState != null) {
                        throw new IllegalStateException(String.format(
                                "Output state not found for %s %s. Tried states that are accessible, but found two states :%s, %s",
                                sourceState, suffix, foundState, deepOutState));
                    } else {
                        foundState = outState;
                        foundSuffix = outSuffix;
                    }
                }
            }
        }

        return Pair.of(foundState, foundSuffix);
    }


    private static class ForcedSuffixForm extends SuffixForm {
        public ForcedSuffixForm(Suffix suffix, String strSuffixFormApplication) {
            super(suffix, strSuffixFormApplication, null, null, null);
        }
    }
}