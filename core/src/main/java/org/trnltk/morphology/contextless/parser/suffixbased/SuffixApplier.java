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

import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.suffixbased.*;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.common.specification.Specification;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.lexicon.tr.PhoneticExpectation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SuffixApplier {
    private static Logger logger = Logger.getLogger(SuffixApplier.class);

    private final PhoneticsEngine phoneticsEngine;

    public SuffixApplier(PhoneticsEngine phoneticsEngine) {
        this.phoneticsEngine = phoneticsEngine;
    }

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

        if (this.phoneticsEngine.applicationMatches(input, appliedStr, !targetState.getName().equals("VERB_ROOT"))) {  //TODO: magic string
            final String actualSuffixForm = input.getUnderlyingString().substring(soFar.length(), appliedStr.length());
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Word '%s' starts with applied str '%s' (%s), adding to current morpheme container", input, appliedStr, actualSuffixForm));
            final MorphemeContainer cloneMorphemeContainer = new MorphemeContainer(morphemeContainer);
            cloneMorphemeContainer.addTransition(new SuffixFormApplication(suffixForm, actualSuffixForm, fittingSuffixForm), targetState);

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

    public MorphemeContainer trySuffixFormApplication(MorphemeContainer morphemeContainer, SuffixFormApplication suffixFormApplication, SuffixGraphState targetState, ImmutableSet<PhoneticExpectation> phoneticExpectations, TurkishSequence input) {
        final SuffixForm suffixForm = suffixFormApplication.getSuffixForm();
        final Suffix suffix = suffixForm.getSuffix();

        if (!this.transitionAllowedForSuffix(morphemeContainer, suffix))
            return null;

        if (!this.transitionAllowedForSuffixForm(morphemeContainer, suffixForm))
            return null;

        if (logger.isDebugEnabled())
            logger.debug(String.format("    Gonna try suffix form application: '%s'", suffixFormApplication));

        final SuffixGraphState stateBeforeSuffixFormApplication = morphemeContainer.getLastState();

        final TurkishSequence soFar = morphemeContainer.getSurfaceSoFar();

        final String actualSuffixForm = suffixFormApplication.getActualSuffixForm();
        final String fittingSuffixForm = suffixFormApplication.getFittingSuffixForm();
        final String appliedStr = soFar.getUnderlyingString() + actualSuffixForm;

        if (phoneticsEngine.applicationMatches(input, appliedStr, false)) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Word '%s' starts with applied str '%s' (%s), adding to current morpheme container", input, appliedStr, actualSuffixForm));

            final MorphemeContainer cloneMorphemeContainer = new MorphemeContainer(morphemeContainer);
            cloneMorphemeContainer.addTransition(new SuffixFormApplication(suffixForm, actualSuffixForm, fittingSuffixForm), targetState);

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

            //TODO:
            if (CollectionUtils.isNotEmpty(phoneticExpectations)) {
                cloneMorphemeContainer.setPhoneticExpectations(phoneticExpectations);
            }

            return cloneMorphemeContainer;

        } else {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Word '%s' does not start with applied str '%s', skipping", input, appliedStr));
            return null;
        }
    }

    private boolean transitionAllowedForSuffixForm(MorphemeContainer morphemeContainer, SuffixForm suffixForm) {
        if (suffixForm.getPrecondition() != null && !suffixForm.getPrecondition().isSatisfiedBy(morphemeContainer)) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Precondition '%s' of suffix form '%s' is not satisfied with transitions %s, skipping.", suffixForm.getForm(), suffixForm.getPrecondition(), morphemeContainer));
            return false;
        }

        if (suffixForm.getForm().isNotBlank() && !this.phoneticsEngine.expectationsSatisfied(morphemeContainer.getPhoneticExpectations(), suffixForm.getForm())) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Suffix form '%s' does not satisfy phonetic expectations %s, skipping.", suffixForm.getForm(), morphemeContainer.getPhoneticExpectations()));

            return false;
        }

        if (!phoneticsEngine.isSuffixFormApplicable(morphemeContainer.getPhoneticAttributes(), suffixForm.getForm())) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Suffix form '%s' is not phonetically applicable to '%s', skipping.", suffixForm.getForm(), morphemeContainer.getSurfaceSoFar()));

            return false;
        }

        return true;
    }
}