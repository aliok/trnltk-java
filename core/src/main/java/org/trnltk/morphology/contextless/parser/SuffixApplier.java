package org.trnltk.morphology.contextless.parser;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.trnltk.common.specification.Specification;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import zemberek3.lexicon.tr.PhonAttr;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

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
        final ImmutableSet<PhonAttr> morphemeContainerPhonAttrs = morphemeContainer.getPhonAttrs();

        final Pair<TurkishSequence, String> appliedPhonetics = this.phoneticsEngine.apply(soFar, morphemeContainerPhonAttrs, suffixForm.getForm(), morphemeContainerLexemeAttributes);
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
                final Specification<MorphemeContainer> postCondition = morphemeContainer.getLastTransition().getSuffixFormApplication().getSuffixForm().getPostCondition();
                if (postCondition != null) {
                    if (postCondition.isSatisfiedBy(cloneMorphemeContainer)) {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("      Suffix satisfies the postcondition '%s' of last transition suffix form '%s'", postCondition, cloneMorphemeContainer.getLastTransition()));
                    } else {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("      Suffix does not satisfy the postcondition '%s' of last transition suffix form '%s', skipping.", postCondition, cloneMorphemeContainer.getLastTransition()));
                        return null;
                    }

                }

                if (SuffixGraphStateType.DERIVATIONAL.equals(stateBeforeSuffixFormApplication.getType())) {
                    logger.debug("      Suffix is derivative, checking the post derivation conditions of suffixes from previous derivation.");
                    for (Transition transition : morphemeContainer.getTransitionsFromDerivationSuffix()) {
                        final SuffixForm applicationSuffixForm = transition.getSuffixFormApplication().getSuffixForm();
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

        if (!phoneticsEngine.isSuffixFormApplicable(morphemeContainer.getPhonAttrs(), suffixForm.getForm())) {
            if (logger.isDebugEnabled())
                logger.debug(String.format("      Suffix form '%s' is not phonetically applicable to '%s', skipping.", suffixForm.getForm(), morphemeContainer.getSurfaceSoFar()));

            return false;
        }

        return true;
    }
}