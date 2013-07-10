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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.parser.suffixbased.MandatoryTransitionApplier;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.suffixbased.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.suffixbased.SuffixApplier;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.util.MorphemeContainerFormatter;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.SuffixFormApplication;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;

import java.util.*;

/**
 * A form-based morphologic parser implementation which does not the context of the input.
 */
public class ContextlessMorphologicParser implements MorphologicParser {
    static Logger logger = Logger.getLogger(ContextlessMorphologicParser.class);    //could be used in other places too!

    private final MandatoryTransitionApplier mandatoryTransitionApplier;
    private ContextlessMorphologicParserListener listener;

    private final SuffixFormGraph suffixFormGraph;
    private final PredefinedPaths predefinedPaths;
    private final RootFinderChain rootFinderChain;
    private final SuffixApplier suffixApplier;

    public ContextlessMorphologicParser(final SuffixFormGraph suffixFormGraph, final PredefinedPaths predefinedPaths, final RootFinderChain rootFinderChain, final SuffixApplier suffixApplier) {
        this.suffixFormGraph = suffixFormGraph;
        this.predefinedPaths = predefinedPaths;
        this.rootFinderChain = rootFinderChain;
        this.suffixApplier = suffixApplier;
        this.mandatoryTransitionApplier = new MandatoryTransitionApplier(suffixFormGraph.getSuffixGraph(), suffixApplier);
    }

    @Override
    public List<List<MorphemeContainer>> parseAll(List<TurkishSequence> inputs) {
        return new ArrayList<List<MorphemeContainer>>(Lists.transform(inputs, new Function<TurkishSequence, List<MorphemeContainer>>() {
            @Override
            public List<MorphemeContainer> apply(TurkishSequence input) {
                return parse(input);
            }
        }));
    }

    @Override
    public List<List<MorphemeContainer>> parseAllStr(List<String> inputs) {
        return new ArrayList<List<MorphemeContainer>>(Lists.transform(inputs, new Function<String, List<MorphemeContainer>>() {
            @Override
            public List<MorphemeContainer> apply(String input) {
                return parseStr(input);
            }
        }));
    }

    @Override
    public List<MorphemeContainer> parseStr(String input) {
        return this.parse(new TurkishSequence(input));
    }

    @Override
    public LinkedList<MorphemeContainer> parse(final TurkishSequence input) {
        if (logger.isDebugEnabled())
            logger.debug("Parsing input " + input);

        final List<MorphemeContainer> candidateMorphemeContainers = this.findInitialMorphemeContainers(input);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Found %d candidate morpheme containers", candidateMorphemeContainers.size()));
            for (MorphemeContainer morphemeContainer : candidateMorphemeContainers) {
                logger.debug("\t " + morphemeContainer.toString());
            }
        }

        logger.debug("Applying mandatory transitions to candidates");

        final List<MorphemeContainer> candidateMorphemeContainersWithMandatoryTransitions = mandatoryTransitionApplier.applyMandatoryTransitionsToMorphemeContainers(candidateMorphemeContainers, input);

        final LinkedList<MorphemeContainer> results = new LinkedList<MorphemeContainer>();
        final LinkedList<MorphemeContainer> newCandidates = this.traverseCandidates(candidateMorphemeContainersWithMandatoryTransitions, results, input);

        if (CollectionUtils.isNotEmpty(newCandidates))
            throw new IllegalStateException("There are still parse morpheme containers to traverse, but traversing is finished : " + newCandidates.toString());

        return results;
    }

    private LinkedList<MorphemeContainer> traverseCandidates(final List<MorphemeContainer> candidates, final List<MorphemeContainer> results, final TurkishSequence input) {
        if (logger.isDebugEnabled()) {
            logger.debug("Gonna traverse " + candidates.size() + " candidates:");
            for (MorphemeContainer candidate : candidates) {
                logger.debug("\t " + candidate);
            }
        }

        LinkedList<MorphemeContainer> newCandidates = new LinkedList<MorphemeContainer>();
        for (MorphemeContainer candidateMorphemeContainer : candidates) {
            if (logger.isDebugEnabled())
                logger.debug(" Traversing candidate: %s" + candidateMorphemeContainer);
            final List<MorphemeContainer> morphemeContainersForCandidate = this.traverseCandidate(candidateMorphemeContainer, input);
            for (MorphemeContainer morphemeContainerForCandidate : morphemeContainersForCandidate) {
                if (SuffixGraphStateType.TERMINAL.equals(morphemeContainerForCandidate.getLastState().getType())) {
                    if (StringUtils.isBlank(morphemeContainerForCandidate.getRemainingSurface())) {
                        results.add(morphemeContainerForCandidate);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Found a terminal result --------------------->");
                            logger.debug(morphemeContainerForCandidate);
                            logger.debug(MorphemeContainerFormatter.formatMorphemeContainerWithForms(morphemeContainerForCandidate));
                        }
                    } else {
                        if (logger.isDebugEnabled())
                            logger.debug("Found a terminal result, but there is still remaining to parse : " + morphemeContainerForCandidate);
                    }
                } else {
                    newCandidates.add(morphemeContainerForCandidate);
                }
            }
        }

        // call recursively until nothing to traverse!
        if (CollectionUtils.isNotEmpty(newCandidates)) {
            List<MorphemeContainer> previousCandidates = newCandidates;
            newCandidates = this.traverseCandidates(previousCandidates, results, input);
            if (listener != null) {
                Sets.SetView<MorphemeContainer> invalidatedMorphemeContainers = Sets.difference(new HashSet<MorphemeContainer>(previousCandidates), new HashSet<MorphemeContainer>(newCandidates));
                for (MorphemeContainer invalidatedMorphemeContainer : invalidatedMorphemeContainers) {
                    if (SuffixGraphStateType.TERMINAL.equals(invalidatedMorphemeContainer.getLastState().getType())) {
                        if (StringUtils.isBlank(invalidatedMorphemeContainer.getRemainingSurface()))
                            continue;
                    }
                    this.listener.onMorphemeContainerInvalidated(invalidatedMorphemeContainer);
                }
            }
        }

        return newCandidates;
    }

    private LinkedList<MorphemeContainer> traverseCandidate(final MorphemeContainer initialContainer, final TurkishSequence input) {
        if (SuffixGraphStateType.TERMINAL.equals(initialContainer.getLastState().getType()))
            return Lists.newLinkedList(Arrays.asList(initialContainer));

        final SuffixFormGraphNodeKey currentSuffixFormGraphNodeKey = new SuffixFormGraphNodeKey(initialContainer.getLastState(), initialContainer.getPhoneticAttributes());
        final SuffixFormGraphNode currentNode = this.suffixFormGraph.getNode(currentSuffixFormGraphNodeKey);
        if (currentNode == null) {
            throw new IllegalStateException("Node not found for key : " + currentSuffixFormGraphNodeKey.getState() + " set: " + new PhoneticAttributeSets().getNumberForSet(currentSuffixFormGraphNodeKey.getPhonAttrSet()));
        }

        final LinkedList<MorphemeContainer> newCandidates = new LinkedList<MorphemeContainer>();

        final Set<SuffixFormGraphSuffixEdge> edges = this.getApplicableSuffixesOfNodeForMorphemeContainer(currentNode, initialContainer);
        if (logger.isDebugEnabled()) {
            if (CollectionUtils.isEmpty(edges))
                logger.debug(String.format("  No applicable transition edges found for morpheme_container from node %s", currentNode));
            else
                logger.debug(String.format("  Found applicable transition edges for morpheme_container from node %s: %s", currentNode, edges));
        }


        if (logger.isDebugEnabled())
            logger.debug(String.format("  Found applicable suffixes for morpheme_container from node %s: %s", currentNode, edges));

        for (SuffixFormGraphSuffixEdge transitionEdge : edges) {
            final SuffixFormApplication suffixFormApplication = transitionEdge.getSuffixFormApplication();

            if (logger.isDebugEnabled())
                logger.debug(String.format("   Going to try suffixFormApplication", suffixFormApplication));

            final SuffixGraphState targetState = transitionEdge.getTargetSuffixFormGraphNode().getSuffixFormGraphNodeKey().getState();
            final MorphemeContainer morphemeContainerForSuffixFormApplication = this.suffixApplier.trySuffixFormApplication(initialContainer, suffixFormApplication, targetState, transitionEdge.getPhoneticExpectations(), input);
            if (morphemeContainerForSuffixFormApplication != null)
                newCandidates.add(morphemeContainerForSuffixFormApplication);

            if (logger.isDebugEnabled())
                logger.debug(String.format("   Applied edge : %s . Applied morpheme container %s", transitionEdge, morphemeContainerForSuffixFormApplication));
        }

        return newCandidates;
    }

    private Set<SuffixFormGraphSuffixEdge> getApplicableSuffixesOfNodeForMorphemeContainer(final SuffixFormGraphNode node, final MorphemeContainer morphemeContainer) {
        if (logger.isDebugEnabled()) {
            logger.debug("  Finding applicable suffixes for morpheme_container from node " + node + " : " + morphemeContainer);
            logger.debug("   Found outputs " + node.getEdges());
        }

        Set<SuffixFormGraphSuffixEdge> edges = node.getEdges();

        edges = Sets.filter(edges, new Predicate<SuffixFormGraphSuffixEdge>() {
            @Override
            public boolean apply(SuffixFormGraphSuffixEdge input) {
                final String appliedSuffixForm = input.getSuffixFormApplication().getActualSuffixForm();
                return morphemeContainer.getRemainingSurface().startsWith(appliedSuffixForm);
            }
        });
        if (logger.isDebugEnabled())
            logger.debug("   Filtered out suffix forms which are not beginning of remaining surface " + morphemeContainer.getSuffixesSinceDerivationSuffix() + " : " + edges);


        edges = Sets.filter(edges, new Predicate<SuffixFormGraphSuffixEdge>() {
            @Override
            public boolean apply(SuffixFormGraphSuffixEdge input) {
                return !morphemeContainer.getSuffixesSinceDerivationSuffix().contains(input.getSuffixFormApplication().getSuffixForm().getSuffix());
            }
        });
        if (logger.isDebugEnabled())
            logger.debug("   Filtered out the applied suffixes since last derivation " + morphemeContainer.getSuffixesSinceDerivationSuffix() + " : " + edges);

        return edges;
    }

    private LinkedList<MorphemeContainer> findInitialMorphemeContainers(final TurkishSequence input) {
        final LinkedList<MorphemeContainer> candidates = new LinkedList<MorphemeContainer>();

        for (int i = 1; i < input.length() + 1; i++) {
            final TurkishSequence partialInput = input.subsequence(0, i);

            final List<Root> roots = this.rootFinderChain.findRootsForPartialInput(partialInput, input);


            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Found %d root candidates for partial input '%s':", roots.size(), partialInput));
                for (Root root : roots) {
                    logger.debug("\t " + root.toString());
                }
            }

            if (this.predefinedPaths == null) {
                for (Root root : roots) {
                    final String remainingInput = input.substring(root.getSequence().length());
                    final SuffixGraphState defaultStateForRoot = this.suffixFormGraph.getDefaultStateForRoot(root);
                    Validate.notNull(defaultStateForRoot, "No node found for root " + root);

                    final MorphemeContainer morphemeContainer = new MorphemeContainer(root, defaultStateForRoot, remainingInput);
                    candidates.add(morphemeContainer);
                }
            } else {
                for (Root root : roots) {
                    final String remainingInput = input.substring(root.getSequence().length());

                    final SuffixGraphState defaultStateForRoot = this.suffixFormGraph.getDefaultStateForRoot(root);
                    if (defaultStateForRoot == null)
                        throw new IllegalStateException("No default state found for root " + root);

                    if (this.predefinedPaths.hasPathsForRoot(root)) {
                        final Set<MorphemeContainer> predefinedMorphemeContainers = this.predefinedPaths.getPaths(root);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Found predefined morpheme containers for root candidate " + root + " : " + predefinedMorphemeContainers);
                        }
                        for (MorphemeContainer predefinedMorphemeContainer : predefinedMorphemeContainers) {
                            if (input.startsWith(predefinedMorphemeContainer.getSurfaceSoFar())) {
                                if (logger.isDebugEnabled())
                                    logger.debug("Predefined morpheme_container is applicable " + predefinedMorphemeContainer);
                                //entry is cloned and since the remaining surface can be different, it is set.
                                MorphemeContainer clone = new MorphemeContainer(predefinedMorphemeContainer, input);
                                candidates.add(clone);
                            } else {
                                if (logger.isDebugEnabled())
                                    logger.debug("Predefined morpheme container is not applicable, skipping " + predefinedMorphemeContainer);
                            }
                        }
                    } else {
                        candidates.add(new MorphemeContainer(root, defaultStateForRoot, remainingInput));
                    }
                }
            }
        }

        return candidates;
    }

    public void setListener(ContextlessMorphologicParserListener listener) {
        this.listener = listener;
    }
}
