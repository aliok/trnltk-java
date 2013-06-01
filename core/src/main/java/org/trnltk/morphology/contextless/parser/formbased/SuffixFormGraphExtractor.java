package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.structure.TurkishAlphabet;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.morphology.model.suffixbased.SuffixForm;
import org.trnltk.morphology.model.suffixbased.SuffixFormApplication;
import org.trnltk.morphology.model.suffixbased.SuffixFormSequence;
import org.trnltk.morphology.morphotactics.SuffixEdge;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.lexicon.tr.PhoneticExpectation;
import org.trnltk.morphology.model.structure.TurkicLetter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class SuffixFormGraphExtractor {

    private final PhoneticAttributeSets phoneticAttributeSets;
    private final PhoneticsAnalyzer phoneticsAnalyzer;
    private final SuffixFormSequenceApplier suffixFormSequenceApplier;

    public SuffixFormGraphExtractor(SuffixFormSequenceApplier suffixFormSequenceApplier, PhoneticsAnalyzer phoneticsAnalyzer, PhoneticAttributeSets phoneticAttributeSets) {
        this.phoneticsAnalyzer = phoneticsAnalyzer;
        this.phoneticAttributeSets = phoneticAttributeSets;
        this.suffixFormSequenceApplier = suffixFormSequenceApplier;
    }


    public SuffixFormGraph extract(SuffixGraph suffixGraph) {
        final Collection<SuffixGraphState> rootSuffixGraphStates = suffixGraph.getRootSuffixGraphStates();

        final SuffixFormGraph suffixFormGraph = new SuffixFormGraph(suffixGraph);

        for (SuffixGraphState rootSuffixGraphState : rootSuffixGraphStates) {
            exploreStateBasedSuffixGraph(rootSuffixGraphState, suffixFormGraph);
        }

        return suffixFormGraph;
    }

    private void exploreStateBasedSuffixGraph(SuffixGraphState suffixGraphState, SuffixFormGraph suffixFormGraph) {
        for (Set<PhoneticAttribute> validPhonAttrSet : this.phoneticAttributeSets.getValidPhoneticAttributeSets()) {
            final SuffixFormGraphNodeKey suffixFormGraphNodeKey = new SuffixFormGraphNodeKey(suffixGraphState, validPhonAttrSet);

            SuffixFormGraphNode suffixFormGraphNode = suffixFormGraph.getNode(suffixFormGraphNodeKey);
            if (suffixFormGraphNode == null) {
                suffixFormGraphNode = suffixFormGraph.addNode(suffixFormGraphNodeKey, suffixGraphState.getType(), validPhonAttrSet);
            } else {
                if (suffixFormGraphNode.isExplored())
                    continue;
            }

            suffixFormGraphNode.setExplored(true);

            final ImmutableSet<SuffixEdge> outEdges = suffixGraphState.getOutEdges();
            for (SuffixEdge outEdge : outEdges) {
                final SuffixGraphState targetState = outEdge.getTargetState();
                final Suffix suffix = outEdge.getSuffix();
                addTransitionNodes(suffixFormGraph, suffixFormGraphNode, suffix, targetState);
                exploreStateBasedSuffixGraph(targetState, suffixFormGraph);
            }
        }
    }

    private void addTransitionNodes(SuffixFormGraph suffixFormGraph, SuffixFormGraphNode sourceSuffixFormGraphNode, Suffix suffix, SuffixGraphState targetState) {
        for (SuffixForm suffixForm : suffix.getSuffixForms()) {
            final SuffixFormSequence suffixFormSequence = suffixForm.getForm();
            final boolean suffixFormSequenceApplicable = suffixFormSequenceApplier.isApplicable(suffixFormSequence, sourceSuffixFormGraphNode.getCurrentPhonAttrSet());
            if (!suffixFormSequenceApplicable)
                continue;

            final String appliedSuffixForm = suffixFormSequenceApplier.apply(suffixFormSequence, sourceSuffixFormGraphNode.getCurrentPhonAttrSet());
            addSuffixFormEdge(suffixFormGraph, sourceSuffixFormGraphNode, targetState, suffixForm, appliedSuffixForm, appliedSuffixForm);

            if (StringUtils.isNotBlank(appliedSuffixForm) && suffixFormSequence.lastLetterCanBeVoiced()) {
                final TurkicLetter lastLetter = TurkishAlphabet.getLetter(appliedSuffixForm.charAt(appliedSuffixForm.length() - 1));
                Validate.notNull(lastLetter);
                final TurkicLetter voicedLastLetter = TurkishAlphabet.voice(lastLetter);
                if (voicedLastLetter != null) {
                    final String voicedSuffixForm = appliedSuffixForm.substring(0, appliedSuffixForm.length() - 1) + voicedLastLetter.charValue;
                    addSuffixFormEdge(suffixFormGraph, sourceSuffixFormGraphNode, targetState, suffixForm, voicedSuffixForm, appliedSuffixForm, Arrays.asList(PhoneticExpectation.VowelStart));
                }
            }
        }
    }

    private void addSuffixFormEdge(SuffixFormGraph suffixFormGraph, SuffixFormGraphNode sourceSuffixFormGraphNode, SuffixGraphState targetState, SuffixForm suffixForm, String appliedSuffixForm, String fittingSuffixForm) {
        this.addSuffixFormEdge(suffixFormGraph, sourceSuffixFormGraphNode, targetState, suffixForm, appliedSuffixForm, fittingSuffixForm, null);
    }

    private SuffixFormGraphSuffixEdge addSuffixFormEdge(SuffixFormGraph charSuffixGraph, SuffixFormGraphNode sourceCharSuffixGraphNode,
                                                        SuffixGraphState targetState, SuffixForm suffixForm, String appliedSuffixForm,
                                                        String fittingSuffixForm, Collection<PhoneticExpectation> phoneticExpectations) {
        final ImmutableSet<PhoneticAttribute> newNodePhoneticAttributes;

        final boolean appliedSuffixFormIsEmpty = StringUtils.isBlank(appliedSuffixForm);
        // if true, then this is one of the following:
        // free transition
        // empty transition
        // zero transition


        if (appliedSuffixFormIsEmpty) {
            newNodePhoneticAttributes = sourceCharSuffixGraphNode.getCurrentPhonAttrSet();
        } else {
            newNodePhoneticAttributes = phoneticsAnalyzer.calculateNewPhoneticAttributes(sourceCharSuffixGraphNode.getCurrentPhonAttrSet(), appliedSuffixForm);
        }

        final SuffixFormGraphNodeKey newCharSuffixGraphNodeKey = new SuffixFormGraphNodeKey(targetState, newNodePhoneticAttributes);
        SuffixFormGraphNode newCharSuffixGraphNode = charSuffixGraph.getNode(newCharSuffixGraphNodeKey);
        if (newCharSuffixGraphNode == null) {
            newCharSuffixGraphNode = new SuffixFormGraphNode(newCharSuffixGraphNodeKey, targetState.getType(), newNodePhoneticAttributes);
            charSuffixGraph.addNode(newCharSuffixGraphNodeKey, newCharSuffixGraphNode);
        }

        if (appliedSuffixFormIsEmpty)
            return sourceCharSuffixGraphNode.addEmptySuffixFormEdge(newCharSuffixGraphNode, suffixForm);
        else
            return sourceCharSuffixGraphNode.addSuffixFormEdge(newCharSuffixGraphNode, new SuffixFormApplication(suffixForm, appliedSuffixForm, fittingSuffixForm), phoneticExpectations);

    }
}
