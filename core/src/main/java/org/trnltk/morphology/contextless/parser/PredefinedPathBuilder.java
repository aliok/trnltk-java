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

package org.trnltk.morphology.contextless.parser;

import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.suffix.SuffixForm;
import org.trnltk.morphology.morphotactics.SuffixEdge;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;

/**
 * Helper class that makes easy and readable to build a predefined path for an irregular word.
 *
 * @see org.trnltk.morphology.morphotactics.PredefinedPathProviderImpl
 */
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

    /**
     * Finds the suffix for the given unique suffix name and adds it to current path with an empty suffix form application.
     */
    public PredefinedPathBuilder s(String suffixName) {
        return this.s(this.suffixGraph.getSuffix(suffixName));
    }

    /**
     * Adds given suffix to current path with an empty suffix form application.
     */
    public PredefinedPathBuilder s(Suffix suffix) {
        return this.s(suffix, StringUtils.EMPTY);
    }

    /**
     * Finds the suffix for the given unique suffix name and adds it to current path with the given suffix form application.
     */
    public PredefinedPathBuilder s(String suffixName, String suffixFormStr) {
        return this.s(this.suffixGraph.getSuffix(suffixName), suffixFormStr);
    }

    /**
     * Adds given suffix to current path with the given suffix form application.
     */
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
        // go through the SuffixGraph with the given suffix and the suffix form application

        // search for a transition from current state to target state of suffix form of suffix and apply it

        // if given suffix is not applicable to current state, then try to find the states:
        // --> that the system could go from current state
        // --> that the given suffix and the form is applicable
        // --> that is one level beyond
        // then current state is actually an intermediate state
        // this is helpful to skip defining epsilon transitions
        // e.g. say
        // * current state is A.
        // * we can go to B with an epsilon transition
        // * we can go to C from B with suffix "S" and form "f"
        // then, when a suffix is added like 'builder.s("S", "f")',
        // * first a transition of A->B with "S(f)" is searched
        // * since not found, an epsilon transition of A->B is searched
        // * it is found, and now a transition of B->C with "S(f)" is searched
        // * since it is found, both epsilon transition and the "S(f)" transition is added

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