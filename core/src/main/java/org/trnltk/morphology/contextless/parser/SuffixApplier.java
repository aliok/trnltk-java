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

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.trnltk.common.specification.Specification;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.PhoneticAttribute;
import org.trnltk.model.lexicon.PhoneticExpectation;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.suffix.SuffixForm;
import org.trnltk.model.suffix.SuffixFormApplication;
import org.trnltk.model.suffix.SuffixTransition;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Morphotactics engine.
 * <p/>
 * Checks if a {@link Suffix}, a {@link SuffixForm} or a {@link SuffixFormApplication} is applicable for a given {@link MorphemeContainer} and applies these.
 */
public class SuffixApplier {
    protected final Logger logger = Logger.getLogger(SuffixApplier.class);

    private final PhoneticsEngine phoneticsEngine;

    public SuffixApplier(PhoneticsEngine phoneticsEngine) {
        this.phoneticsEngine = phoneticsEngine;
    }

    /**
     * Checks if the suffix is applicable and applies it.
     * <p/>
     * Tries all {@link SuffixForm}s of the given suffix.
     *
     * @return Morpheme containers where the transitions for the suffix are applied. Passed container is immutable thus untouched.
     * @see SuffixApplier#transitionAllowedForSuffix(org.trnltk.model.morpheme.MorphemeContainer, org.trnltk.model.suffix.Suffix)
     */
    public List<MorphemeContainer> trySuffix(MorphemeContainer morphemeContainer, Suffix suffix, SuffixGraphState targetState, TurkishSequence input) {
        if (!this.transitionAllowedForSuffix(morphemeContainer, suffix))
            return new ArrayList<MorphemeContainer>();

        final List<MorphemeContainer> newMorphemeContainers = new LinkedList<MorphemeContainer>();

        if (logger.isDebugEnabled())
            logger.debug(String.format("    Gonna try %d suffix forms : '%s'", suffix.getSuffixForms().size(), suffix.getSuffixForms()));

        for (SuffixForm suffixForm : suffix.getSuffixForms()) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("    Gonna try suffix form : '%s'", suffix));

            MorphemeContainer newMorphemeContainer = this.trySuffixForm(morphemeContainer, suffixForm, targetState, input);
            if (newMorphemeContainer != null)
                newMorphemeContainers.add(newMorphemeContainer);
        }
        return newMorphemeContainers;
    }

    /**
     * Checks if the suffix is applicable to given container.
     * <p/>
     * Given suffix is applicable <i>iff</i>
     * <ul>
     * <li>{@link org.trnltk.model.suffix.SuffixGroup}s of suffixes in last inflection group (suffixes since last derivation)
     * do not contain the group of given {@link Suffix} --> A {@link Suffix} from one {@link org.trnltk.model.suffix.SuffixGroup}
     * cannot exist in an inflection group.</li>
     * <li>Suffixes in last inflection group (suffixes since last derivation)
     * do not contain the given {@link Suffix} and suffix does not allow repetition -->
     * Given {@link Suffix} does not allow repetition, then it cannot exist twice in an inflection group.</li>
     * </ul>
     */
    public boolean transitionAllowedForSuffix(MorphemeContainer morphemeContainer, Suffix suffix) {
        if (suffix.getSuffixGroup() != null && morphemeContainer.getSuffixGroupsSinceLastDerivationSuffix().contains(suffix.getSuffixGroup())) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("    Another suffix is already added on the same group(%s) since last derivation, skipping suffix.", suffix.getSuffixGroup()));
                logger.debug(String.format("    Groups since last derivation are : %s", morphemeContainer.getSuffixGroupsSinceLastDerivationSuffix()));
            }
            return false;
        }

        if (!suffix.isAllowRepetition() && suffix.equals(morphemeContainer.getLastDerivationSuffix())) {
            logger.debug("    The last derivation suffix is same with the suffix, skipping.");
            return false;
        }
        return true;
    }


    /**
     * Checks if the given suffix form is applicable and applies it. Checks done are (in order):
     * <ul>
     * <li>Is precondition of the suffix form satisfied with the given container?</li>
     * <li>If suffixForm is not blank, is the phonetic expectations of container is satisfied with the suffix form?</li>
     * <li>If suffix form is phonetically applicable to container? see {@link PhoneticsEngine#isSuffixFormApplicable(java.util.Set, org.trnltk.model.suffix.SuffixFormSequence)}</li>
     * <li>Does computed suffix form application based on phonetic attributes match the remaining part of the surface?</li>
     * <li>Are the post conditions of the suffix forms in the last inflection group satisfied when suffix form is applied?</li>
     * <li>If the current state of the container is derivational, is the post derivation condition of the container satisfied with suffix form?</li>
     * </ul>
     *
     * @return Morpheme container where the transition for the given suffix form is applied. Passed container is immutable thus untouched.
     * @see SuffixApplier#transitionAllowedForSuffixForm(org.trnltk.model.morpheme.MorphemeContainer, org.trnltk.model.suffix.SuffixForm)
     */
    public MorphemeContainer trySuffixForm(MorphemeContainer morphemeContainer, SuffixForm suffixForm, SuffixGraphState targetState, TurkishSequence input) {
        if (!this.transitionAllowedForSuffixForm(morphemeContainer, suffixForm))
            return null;

        final SuffixGraphState stateBeforeSuffixFormApplication = morphemeContainer.getLastState();

        final TurkishSequence soFar = morphemeContainer.getSurfaceSoFar();
        final ImmutableSet<LexemeAttribute> morphemeContainerLexemeAttributes = morphemeContainer.getLexemeAttributes();
        final ImmutableSet<PhoneticAttribute> morphemeContainerPhoneticAttributes = morphemeContainer.getPhoneticAttributes();

        final Pair<TurkishSequence, String> appliedPhonetics = this.phoneticsEngine.apply(soFar, morphemeContainerPhoneticAttributes, suffixForm.getForm(), morphemeContainerLexemeAttributes);
        final TurkishSequence modifiedWord = appliedPhonetics.getLeft();
        final String fittingSuffixForm = appliedPhonetics.getRight();
        final String appliedStr = modifiedWord.getUnderlyingString() + fittingSuffixForm;


        // Does computed suffix form application based on phonetic attributes match the remaining part of the surface?
        if (this.phoneticsEngine.applicationMatches(input, appliedStr, !targetState.getName().equals("VERB_ROOT"))) {  //TODO: magic string
            final String actualSuffixForm = input.getUnderlyingString().substring(soFar.length(), appliedStr.length());
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Word '%s' starts with applied str '%s' (%s), adding to current morpheme container", input, appliedStr, actualSuffixForm));
            final MorphemeContainer cloneMorphemeContainer = new MorphemeContainer(morphemeContainer);
            cloneMorphemeContainer.addTransition(new SuffixFormApplication(suffixForm, actualSuffixForm, fittingSuffixForm), targetState);

            // Are the post conditions of the suffix forms in the last inflection group satisfied when suffix form is applied?
            if (morphemeContainer.hasTransitions()) {
                final Specification<MorphemeContainer> postCondition = morphemeContainer.getLastSuffixTransition().getSuffixFormApplication().getSuffixForm().getPostCondition();
                if (postCondition != null) {
                    if (postCondition.isSatisfiedBy(cloneMorphemeContainer)) {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("      Suffix satisfies the postcondition '%s' of last transition suffix form '%s'", postCondition, cloneMorphemeContainer.getLastSuffixTransition()));
                    } else {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("      Suffix does not satisfy the postcondition '%s' of last transition suffix form '%s', skipping.", postCondition, cloneMorphemeContainer.getLastSuffixTransition()));
                        return null;
                    }

                }

                // If the current state of the container is derivational, is the post derivation condition of the container satisfied with suffix form?
                if (SuffixGraphStateType.DERIVATIONAL.equals(stateBeforeSuffixFormApplication.getType())) {
                    logger.debug("      Suffix is derivative, checking the post derivation conditions of suffixes from previous derivation.");
                    for (SuffixTransition suffixTransition : morphemeContainer.getTransitionsFromDerivationSuffix()) {
                        final SuffixForm applicationSuffixForm = suffixTransition.getSuffixFormApplication().getSuffixForm();
                        final Specification<MorphemeContainer> postDerivativeCondition = applicationSuffixForm.getPostDerivativeCondition();
                        if (postDerivativeCondition != null && !postDerivativeCondition.isSatisfiedBy(cloneMorphemeContainer)) {
                            if (logger.isDebugEnabled())
                                logger.debug(String.format("      Post derivation condition '%s' of suffix '%s' is not satisfied, skipping.", applicationSuffixForm.getPostDerivativeCondition(), applicationSuffixForm.getSuffix()));
                            return null;
                        }
                    }
                }
            }
            return cloneMorphemeContainer;
        }

        if (logger.isDebugEnabled())
            logger.debug(String.format("      Word '%s' does not start with applied str '%s', skipping", input, appliedStr));
        return null;
    }

    /**
     * Checks if the given suffix form application is applicable and applies it.
     * <p/>
     * Difference with trying a suffix form ({@link SuffixApplier#trySuffixForm(org.trnltk.model.morpheme.MorphemeContainer, org.trnltk.model.suffix.SuffixForm, org.trnltk.morphology.morphotactics.SuffixGraphState, org.trnltk.model.letter.TurkishSequence)})
     * is, the application already given. It is not computed.
     * <p/>
     * Checks done are (in order):
     * <ul>
     * <li>Is suffix of the application's form allowed? see {@link SuffixApplier#transitionAllowedForSuffix(org.trnltk.model.morpheme.MorphemeContainer, org.trnltk.model.suffix.Suffix)}</li>
     * <li>Is suffix form of the application allowed? see {@link SuffixApplier#transitionAllowedForSuffixForm(org.trnltk.model.morpheme.MorphemeContainer, org.trnltk.model.suffix.SuffixForm)}</li>
     * <li>Does given suffix form application match the remaining part of the surface?</li>
     * <li>Are the post conditions of the suffix forms in the last inflection group satisfied when suffix form application is applied?</li>
     * <li>If the current state of the container is derivational, is the post derivation condition of the container satisfied with suffix form application?</li>
     * </ul>
     *
     * @return Morpheme container where the transition for the given suffix form application is applied. Passed container is immutable thus untouched.
     * @see SuffixApplier#transitionAllowedForSuffix(org.trnltk.model.morpheme.MorphemeContainer, org.trnltk.model.suffix.Suffix)
     * @see SuffixApplier#transitionAllowedForSuffixForm(org.trnltk.model.morpheme.MorphemeContainer, org.trnltk.model.suffix.SuffixForm)
     */
    public MorphemeContainer trySuffixFormApplication(MorphemeContainer morphemeContainer, SuffixFormApplication suffixFormApplication, SuffixGraphState targetState, ImmutableSet<PhoneticExpectation> phoneticExpectations, TurkishSequence input) {
        final SuffixForm suffixForm = suffixFormApplication.getSuffixForm();
        final Suffix suffix = suffixForm.getSuffix();

        // Is suffix of the application's form allowed?
        if (!this.transitionAllowedForSuffix(morphemeContainer, suffix))
            return null;

        // Is suffix form of the application allowed?
        if (!this.transitionAllowedForSuffixForm(morphemeContainer, suffixForm))
            return null;

        if (logger.isDebugEnabled())
            logger.debug(String.format("    Gonna try suffix form application: '%s'", suffixFormApplication));

        final SuffixGraphState stateBeforeSuffixFormApplication = morphemeContainer.getLastState();

        final TurkishSequence soFar = morphemeContainer.getSurfaceSoFar();

        final String actualSuffixForm = suffixFormApplication.getActualSuffixForm();
        final String fittingSuffixForm = suffixFormApplication.getFittingSuffixForm();
        final String appliedStr = soFar.getUnderlyingString() + actualSuffixForm;

        // Does given suffix form application match the remaining part of the surface?
        if (phoneticsEngine.applicationMatches(input, appliedStr, false)) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Word '%s' starts with applied str '%s' (%s), adding to current morpheme container", input, appliedStr, actualSuffixForm));

            final MorphemeContainer cloneMorphemeContainer = new MorphemeContainer(morphemeContainer);
            cloneMorphemeContainer.addTransition(new SuffixFormApplication(suffixForm, actualSuffixForm, fittingSuffixForm), targetState);

            // Are the post conditions of the suffix forms in the last inflection group satisfied when suffix form application is applied?
            if (morphemeContainer.hasTransitions()) {
                final Specification<MorphemeContainer> postCondition = morphemeContainer.getLastSuffixTransition().getSuffixFormApplication().getSuffixForm().getPostCondition();
                if (postCondition != null) {
                    if (postCondition.isSatisfiedBy(cloneMorphemeContainer)) {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("      Suffix satisfies the postcondition '%s' of last transition suffix form '%s'", postCondition, cloneMorphemeContainer.getLastSuffixTransition()));
                    } else {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("      Suffix does not satisfy the postcondition '%s' of last transition suffix form '%s', skipping.", postCondition, cloneMorphemeContainer.getLastSuffixTransition()));
                        return null;
                    }

                }

                // If the current state of the container is derivational, is the post derivation condition of the container satisfied with suffix form application?
                if (SuffixGraphStateType.DERIVATIONAL.equals(stateBeforeSuffixFormApplication.getType())) {
                    logger.debug("      Suffix is derivative, checking the post derivation conditions of suffixes from previous derivation.");
                    for (SuffixTransition suffixTransition : morphemeContainer.getTransitionsFromDerivationSuffix()) {
                        final SuffixForm applicationSuffixForm = suffixTransition.getSuffixFormApplication().getSuffixForm();
                        final Specification<MorphemeContainer> postDerivativeCondition = applicationSuffixForm.getPostDerivativeCondition();
                        if (postDerivativeCondition != null && !postDerivativeCondition.isSatisfiedBy(cloneMorphemeContainer)) {
                            if (logger.isDebugEnabled())
                                logger.debug(String.format("      Post derivation condition '%s' of suffix '%s' is not satisfied, skipping.", applicationSuffixForm.getPostDerivativeCondition(), applicationSuffixForm.getSuffix()));
                            return null;
                        }
                    }
                }
            }

            // since transition's target could have some phoneticExpectations, set them
            if (CollectionUtils.isNotEmpty(phoneticExpectations)) {
                cloneMorphemeContainer.overwritePhoneticExpectations(phoneticExpectations);
            }

            return cloneMorphemeContainer;

        } else {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Word '%s' does not start with applied str '%s', skipping", input, appliedStr));
            return null;
        }
    }

    private boolean transitionAllowedForSuffixForm(MorphemeContainer morphemeContainer, SuffixForm suffixForm) {
        // Is precondition of the suffix form satisfied with the given container?
        if (suffixForm.getPrecondition() != null && !suffixForm.getPrecondition().isSatisfiedBy(morphemeContainer)) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Precondition '%s' of suffix form '%s' is not satisfied with transitions %s, skipping.", suffixForm.getForm(), suffixForm.getPrecondition(), morphemeContainer));
            return false;
        }

        // If suffixForm is not blank, is the phonetic expectations of container is satisfied with the suffix form?
        if (suffixForm.getForm().isNotBlank() && !this.phoneticsEngine.expectationsSatisfied(morphemeContainer.getPhoneticExpectations(), suffixForm.getForm())) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Suffix form '%s' does not satisfy phonetic expectations %s, skipping.", suffixForm.getForm(), morphemeContainer.getPhoneticExpectations()));

            return false;
        }

        // Does computed suffix form application based on phonetic attributes match the remaining part of the surface?
        if (!phoneticsEngine.isSuffixFormApplicable(morphemeContainer.getPhoneticAttributes(), suffixForm.getForm())) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Suffix form '%s' is not phonetically applicable to '%s', skipping.", suffixForm.getForm(), morphemeContainer.getSurfaceSoFar()));

            return false;
        }

        return true;
    }
}