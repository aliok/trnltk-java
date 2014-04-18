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

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.trnltk.common.specification.Specification;
import org.trnltk.common.specification.Specifications;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.suffix.SuffixForm;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;

import java.util.LinkedList;
import java.util.List;

import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.rootHasPrimaryPos;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.rootHasProgressiveVowelDrop;

/**
 * Applies mandatory transitions for a {@link MorphemeContainer}.
 * <p/>
 * Mandatory transitions are transitions which need to be applied when a root for a word is found and it is a valid root
 * if following suffix and suffix form are specified ones.
 * <p/>
 * One example is progressive suffix for some verbs. For example, root "at" of dictionary entry "atamak" is only valid
 * if suffixForm "Iyor" of suffix "Progressive" follows. That results in a partial surface of "atıyor".
 * Root "at" is not valid for other suffixes (e.g. "atacak" -> not valid, "atayacak" -> valid).
 */
public class MandatoryTransitionApplier {

    private final Logger logger = Logger.getLogger(MandatoryTransitionApplier.class);

    private final SuffixGraph suffixGraph;
    private final SuffixApplier suffixApplier;

    private final LinkedList<MandatoryTransitionRule> mandatoryTransitionRules = new LinkedList<MandatoryTransitionRule>();

    public MandatoryTransitionApplier(final SuffixGraph suffixGraph, final SuffixApplier suffixApplier) {
        this.suffixGraph = suffixGraph;
        this.suffixApplier = suffixApplier;

        this.createRules();
    }

    public List<MorphemeContainer> applyMandatoryTransitionsToMorphemeContainers(final List<MorphemeContainer> morphemeContainers, final TurkishSequence input) {
        final List<MorphemeContainer> newMorphemeContainers = new LinkedList<MorphemeContainer>();
        for (MorphemeContainer morphemeContainer : morphemeContainers) {
            MorphemeContainer newMorhpemeContainer = morphemeContainer;

            for (MandatoryTransitionRule mandatoryTransitionRule : this.mandatoryTransitionRules) {
                if (!mandatoryTransitionRule.getCondition().isSatisfiedBy(newMorhpemeContainer))
                    continue;
                if (!mandatoryTransitionRule.getSourceState().equals(newMorhpemeContainer.getLastState()))
                    continue;

                for (MandatoryTransitionRuleStep mandatoryTransitionRuleStep : mandatoryTransitionRule.getMandatoryTransitionRuleSteps()) {
                    newMorhpemeContainer = applyRequiredTransitionRuleStepToMorphemeContainer(newMorhpemeContainer, mandatoryTransitionRuleStep, input);
                    if (newMorhpemeContainer == null)
                        break;
                }

                if (newMorhpemeContainer == null)
                    break;
            }

            if (newMorhpemeContainer != null)
                newMorphemeContainers.add(newMorhpemeContainer);
        }
        return newMorphemeContainers;
    }

    private void createRules() {
        // English translation to following code fragment:
        // if root has PrimaryPos Verb and root has progressive vowel drop
        // and its state is VERB_ROOT
        // add positive suffix, and then progressive suffix

        final MandatoryTransitionRule progressiveVowelDropRule = new RequiredTransitionRuleBuilder(this.suffixGraph)
                .condition(
                        Specifications.and(
                                rootHasPrimaryPos(PrimaryPos.Verb),
                                rootHasProgressiveVowelDrop()
                        )
                )
                .sourceState("VERB_ROOT")
                .step("Pos", "", "VERB_WITH_POLARITY")
                .step("Prog", "Iyor", "VERB_WITH_TENSE")
                .build();

        // more rules can be added

        this.mandatoryTransitionRules.add(progressiveVowelDropRule);
    }

    private MorphemeContainer applyRequiredTransitionRuleStepToMorphemeContainer(final MorphemeContainer morphemeContainer, MandatoryTransitionRuleStep mandatoryTransitionRuleStep, TurkishSequence input) {
        final SuffixForm suffixForm = mandatoryTransitionRuleStep.getSuffixForm();
        final Suffix suffix = suffixForm.getSuffix();
        if (!this.suffixApplier.transitionAllowedForSuffix(morphemeContainer, suffix))
            throw new IllegalStateException(String.format("There is a matching mandatory transition rule, but suffix \"%s\" cannot be applied to %s", suffix, morphemeContainer));

        final MorphemeContainer newMorphemeContainer = this.suffixApplier.trySuffixForm(morphemeContainer, suffixForm, mandatoryTransitionRuleStep.getTargetState(), input);
        if (newMorphemeContainer == null) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("There is a matching mandatory transition rule, but suffix form \"%s\" cannot be applied to %s", suffixForm, morphemeContainer));
            return null;
        } else {
            return newMorphemeContainer;
        }
    }

    private static class RequiredTransitionRuleBuilder {
        private final SuffixGraph suffixGraph;

        private Specification<MorphemeContainer> condition;
        private SuffixGraphState sourceState;
        private LinkedList<MandatoryTransitionRuleStep> steps = new LinkedList<MandatoryTransitionRuleStep>();

        private RequiredTransitionRuleBuilder(final SuffixGraph suffixGraph) {
            this.suffixGraph = suffixGraph;
        }

        public RequiredTransitionRuleBuilder condition(Specification<MorphemeContainer> condition) {
            this.condition = condition;
            return this;
        }

        public RequiredTransitionRuleBuilder sourceState(String sourceStateName) {
            this.sourceState = this.suffixGraph.getSuffixGraphState(sourceStateName);
            Validate.notNull(this.sourceState);
            return this;
        }

        public RequiredTransitionRuleBuilder step(String suffixName, String suffixFormStr, String targetStateName) {
            final SuffixForm suffixForm = this.suffixGraph.getSuffixForm(suffixName, suffixFormStr);
            final SuffixGraphState targetState = this.suffixGraph.getSuffixGraphState(targetStateName);

            Validate.notNull(suffixForm);
            Validate.notNull(targetState);

            this.steps.add(new MandatoryTransitionRuleStep(suffixForm, targetState));
            return this;
        }

        public MandatoryTransitionRule build() {
            Validate.notNull(this.sourceState);
            Validate.notNull(this.condition);
            Validate.notEmpty(this.steps);

            return new MandatoryTransitionRule(condition, sourceState, steps);
        }
    }

    private static class MandatoryTransitionRule {
        private Specification<MorphemeContainer> condition;
        private SuffixGraphState sourceState;
        private List<MandatoryTransitionRuleStep> mandatoryTransitionRuleSteps;

        private MandatoryTransitionRule(Specification<MorphemeContainer> condition, SuffixGraphState sourceState,
                                        List<MandatoryTransitionRuleStep> mandatoryTransitionRuleSteps) {
            Validate.notEmpty(mandatoryTransitionRuleSteps);
            this.condition = condition;
            this.sourceState = sourceState;
            this.mandatoryTransitionRuleSteps = mandatoryTransitionRuleSteps;
        }

        public SuffixGraphState getSourceState() {
            return sourceState;
        }

        public Specification<MorphemeContainer> getCondition() {
            return condition;
        }

        public List<MandatoryTransitionRuleStep> getMandatoryTransitionRuleSteps() {
            return mandatoryTransitionRuleSteps;
        }
    }

    private static class MandatoryTransitionRuleStep {
        private final SuffixForm suffixForm;
        private final SuffixGraphState targetState;

        private MandatoryTransitionRuleStep(SuffixForm suffixForm, SuffixGraphState targetState) {
            this.suffixForm = suffixForm;
            this.targetState = targetState;
        }

        public SuffixForm getSuffixForm() {
            return suffixForm;

        }

        public SuffixGraphState getTargetState() {
            return targetState;
        }
    }
}