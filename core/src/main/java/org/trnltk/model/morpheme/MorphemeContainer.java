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

package org.trnltk.model.morpheme;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.*;
import org.trnltk.model.suffix.*;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.*;

/**
 * A container to hold <i>morpheme</i>s. This class not only holds the morhpemes, also computes the information that are used
 * common. It is heavy because processes some phonetic/orthographic rules and it computes the information mentioned.
 * <p/>
 * A morpheme is a part of a surface(word), which can be root or a suffix.
 * For example, for surface <code>gözlükçülükten</code>, the morphemes are <code>göz</code>, <code>lük</code>,
 * <code>çü</code>, <code>lük</code>, <code>ten</code>.
 * <p/>
 * Morphemes are not just hold in a plain way. Transitions for suffixes, beginning point in the
 * {@link org.trnltk.morphology.morphotactics.SuffixGraph} etc. is kept as well.
 * <p/>
 * A {@code MorphemeContainer} is basically used to represent a parse result for a surface or an intermediate state
 * of a parse result of a surface. That means {@code MorphemeContainer} can be the parse result itself, or the
 * transition state of it. In case of an intermediate state, remaining surface is also kept.
 * <p/>
 * This class is optimized to save the state of most commonly used information e.g. last suffix or suffixes since last derivation;
 * thus it is heavy.
 */
public class MorphemeContainer {

    final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    // final values
    private final Root root;
    private final SuffixGraphState rootState;

    // things below are changed with suffixTransitions, but do have a value set in constructor
    private TurkishSequence surfaceSoFar;
    private String remainingSurface;
    private LinkedList<SuffixTransition> suffixTransitions;
    private SuffixGraphState lastState;
    private String wholeSurface;
    private ImmutableSet<PhoneticExpectation> phoneticExpectations;
    private ImmutableSet<LexemeAttribute> lexemeAttributes;
    private ImmutableSet<PhoneticAttribute> phoneticAttributes;

    // things below are changed with suffixTransitions, but do not have a value set in constructor
    private SuffixTransition lastSuffixTransition = null;
    private SuffixTransition lastDerivationSuffixTransition = null;
    private SuffixTransition lastNonBlankSuffixTransition = null;
    private Suffix lastDerivationSuffix = null;
    private SuffixTransition lastNonBlankDerivation = null;
    private LinkedHashSet<SuffixTransition> transitionsSinceDerivationSuffix = new LinkedHashSet<SuffixTransition>();
    private LinkedHashSet<SuffixTransition> transitionsFromDerivationSuffix = new LinkedHashSet<SuffixTransition>();
    private LinkedHashSet<Suffix> suffixesSinceDerivationSuffix = new LinkedHashSet<Suffix>();
    private LinkedHashSet<SuffixGroup> suffixGroupsSinceLastDerivationSuffix = new LinkedHashSet<SuffixGroup>();

    public MorphemeContainer(Root root, SuffixGraphState rootState, String remainingSurface) {
        this.root = root;
        this.rootState = rootState;

        this.surfaceSoFar = root.getSequence();
        this.remainingSurface = remainingSurface;
        this.suffixTransitions = new LinkedList<SuffixTransition>();
        this.lastState = this.rootState;
        this.wholeSurface = Strings.nullToEmpty(this.surfaceSoFar.getUnderlyingString()) + Strings.nullToEmpty(this.remainingSurface);
        this.phoneticExpectations = Sets.immutableEnumSet(root.getPhoneticExpectations());
        this.lexemeAttributes = Sets.immutableEnumSet(this.root.getLexeme().getAttributes());
        this.phoneticAttributes = Sets.immutableEnumSet(this.root.getPhoneticAttributes());
    }

    public MorphemeContainer(final MorphemeContainer toCopy) {
        this.root = toCopy.root;
        this.rootState = toCopy.rootState;
        this.surfaceSoFar = toCopy.surfaceSoFar;
        this.remainingSurface = toCopy.remainingSurface;
        //noinspection unchecked
        this.suffixTransitions = (LinkedList<SuffixTransition>) toCopy.suffixTransitions.clone();
        this.lastState = toCopy.lastState;
        this.wholeSurface = toCopy.wholeSurface;
        this.phoneticExpectations = toCopy.phoneticExpectations;
        this.lexemeAttributes = toCopy.lexemeAttributes;
        this.phoneticAttributes = toCopy.phoneticAttributes;

        this.lastSuffixTransition = toCopy.lastSuffixTransition;
        this.lastDerivationSuffixTransition = toCopy.lastDerivationSuffixTransition;
        this.lastNonBlankSuffixTransition = toCopy.lastNonBlankSuffixTransition;
        this.lastDerivationSuffix = toCopy.lastDerivationSuffix;
        this.lastNonBlankDerivation = toCopy.lastNonBlankDerivation;
        //noinspection unchecked
        this.transitionsSinceDerivationSuffix = (LinkedHashSet<SuffixTransition>) toCopy.transitionsSinceDerivationSuffix.clone();
        //noinspection unchecked
        this.transitionsFromDerivationSuffix = (LinkedHashSet<SuffixTransition>) toCopy.transitionsFromDerivationSuffix.clone();
        //noinspection unchecked
        this.suffixesSinceDerivationSuffix = (LinkedHashSet<Suffix>) toCopy.suffixesSinceDerivationSuffix.clone();
        //noinspection unchecked
        this.suffixGroupsSinceLastDerivationSuffix = (LinkedHashSet<SuffixGroup>) toCopy.suffixGroupsSinceLastDerivationSuffix.clone();
    }

    /**
     * Deep clone the given {@code MorphemeContainer} and set remaining surface of the container according to the given whole surface.
     *
     * @param toCopy       source container
     * @param wholeSurface Whole surface to compute remaining surface
     */
    public MorphemeContainer(MorphemeContainer toCopy, TurkishSequence wholeSurface) {
        this(toCopy);
        this.remainingSurface = wholeSurface.subsequence(toCopy.getSurfaceSoFar().getUnderlyingString().length()).getUnderlyingString();
    }

    /**
     * Add a suffix transition and incrementally re-compute the states.
     *
     * @param suffixFormApplication SuffixFormApplication for the transition
     * @param targetState           target suffix graph state to go with the transition
     */
    public void addTransition(SuffixFormApplication suffixFormApplication, SuffixGraphState targetState) {
        final SuffixTransition newSuffixTransition = new SuffixTransition(this.lastState, suffixFormApplication, targetState);
        this.suffixTransitions.add(newSuffixTransition);

        this.reinitialize(newSuffixTransition);
    }

    private void reinitialize(final SuffixTransition newSuffixTransition) {
        Validate.notNull(newSuffixTransition);

        final SuffixFormApplication suffixFormApplication = newSuffixTransition.getSuffixFormApplication();
        final SuffixForm suffixForm = suffixFormApplication.getSuffixForm();

        // recompute the things incrementally

        // update surface so far and remaining surface
        this.surfaceSoFar = this.surfaceSoFar.append(suffixFormApplication.getActualSuffixForm());
        this.remainingSurface = StringUtils.isBlank(this.remainingSurface) ?
                StringUtils.EMPTY :
                this.remainingSurface.substring(suffixFormApplication.getActualSuffixForm().length());

        // when there is a non-blank suffix form, then clear phoneticExpectations, since parser checked
        // them and decided that they're satisfied
        if (suffixFormApplication.getSuffixForm().getForm().isNotBlank())
            this.phoneticExpectations = ImmutableSet.of();

        // update easy stuff
        this.lastState = newSuffixTransition.getTargetState();
        this.lastSuffixTransition = newSuffixTransition;
        this.wholeSurface = Strings.nullToEmpty(this.surfaceSoFar.getUnderlyingString()) + Strings.nullToEmpty(this.remainingSurface);

        if (newSuffixTransition.isDerivational()) {
            // update the things when new suffix is a derivational one

            this.lastDerivationSuffixTransition = newSuffixTransition;
            this.lastDerivationSuffix = suffixForm.getSuffix();
            this.transitionsSinceDerivationSuffix = new LinkedHashSet<SuffixTransition>();
            //noinspection unchecked
            this.transitionsFromDerivationSuffix = new LinkedHashSet(Arrays.asList(newSuffixTransition));
            this.suffixesSinceDerivationSuffix = new LinkedHashSet<Suffix>();
            this.suffixGroupsSinceLastDerivationSuffix = new LinkedHashSet<SuffixGroup>();

            if (suffixFormApplication.getSuffixForm().getForm().isNotBlank())
                this.lastNonBlankDerivation = newSuffixTransition;

        } else {
            this.transitionsSinceDerivationSuffix.add(newSuffixTransition);
            this.transitionsFromDerivationSuffix.add(newSuffixTransition);
            this.suffixesSinceDerivationSuffix.add(suffixForm.getSuffix());
            if (suffixForm.getSuffix().getSuffixGroup() != null)
                this.suffixGroupsSinceLastDerivationSuffix.add(suffixForm.getSuffix().getSuffixGroup());
        }

        if (suffixFormApplication.getSuffixForm().getForm().isNotBlank())
            this.lastNonBlankSuffixTransition = newSuffixTransition;

        // cannot do the following 2 incrementally
        this.lexemeAttributes = this.findLexemeAttributes();
        this.phoneticAttributes = this.findPhoneticAttributes();
    }

    /**
     * Get last suffix graph state which the container transitioned by the last suffix.
     *
     * @return lastState
     */
    public SuffixGraphState getLastState() {
        return this.lastState;
    }

    /**
     * Get surface included by the container.
     *
     * @return surfaceSoFar
     */
    public TurkishSequence getSurfaceSoFar() {
        return surfaceSoFar;
    }

    /**
     * Get surface not included by the container.
     *
     * @return remainingSurface
     */
    public String getRemainingSurface() {
        return remainingSurface;
    }

    /**
     * Get suffix transitions that are added since derivation suffix. Result <b>does not</b> include the last derivation suffix.
     * <p/>
     * Returned set is unmodifiable (JDK).
     *
     * @return set
     */
    public Set<SuffixTransition> getTransitionsSinceDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.transitionsSinceDerivationSuffix);
    }

    /**
     * Get suffix transitions that are added from derivation suffix. Result <b>includes</b> the last derivation suffix.
     * <p/>
     * Returned set is unmodifiable (JDK).
     *
     * @return set
     */
    public Set<SuffixTransition> getTransitionsFromDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.transitionsFromDerivationSuffix);
    }

    /**
     * Get suffixes since derivation suffix. Result <b>does not</b> include the last derivation suffix.
     * <p/>
     * This could also be computed by using {@link org.trnltk.model.morpheme.MorphemeContainer#getTransitionsSinceDerivationSuffix()}
     * but that is slow.
     * <p/>
     * Returned set is unmodifiable (JDK).
     *
     * @return set
     */
    public Set<Suffix> getSuffixesSinceDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.suffixesSinceDerivationSuffix);
    }

    /**
     * Get suffix groups since derivation suffix. Result <b>does not</b> include the group of last derivation suffix.
     * <p/>
     * This could also be computed by using {@link org.trnltk.model.morpheme.MorphemeContainer#getTransitionsSinceDerivationSuffix()}
     * but that is slow.
     * <p/>
     * Returned set is unmodifiable (JDK).
     *
     * @return set
     */
    public Set<SuffixGroup> getSuffixGroupsSinceLastDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.suffixGroupsSinceLastDerivationSuffix);
    }

    /**
     * Get last transition of a derivation suffix.
     *
     * @return transition
     */
    public SuffixTransition getLastDerivationSuffixTransition() {
        return this.lastDerivationSuffixTransition;
    }

    /**
     * @return Root of the container
     */
    public Root getRoot() {
        return this.root;
    }

    /**
     * @return Starting point in suffix graph
     */
    public SuffixGraphState getRootState() {
        return rootState;
    }

    /**
     * Get phonetic attributes for the container.
     * <p/>
     * These are recomputed with each suffix transition added.
     *
     * @return set
     */
    public ImmutableSet<PhoneticAttribute> getPhoneticAttributes() {
        return this.phoneticAttributes;
    }

    /**
     * Get phonetic expectations of the container. Phonetic expectations are cleared once a non-blank suffix is applied.
     *
     * @return Phonetic expectations of the container
     */
    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations() {
        return this.phoneticExpectations;
    }

    /**
     * @return Last transition of non-blank suffix
     */
    public SuffixTransition getLastNonBlankSuffixTransition() {
        return this.lastNonBlankSuffixTransition;
    }

    /**
     * Get lexeme attributes which are computed for current state of the container.
     * <p/>
     * If no suffix is added or all added suffixes are blank, result is lexeme attributes of the lexeme.
     * Otherwise compute lexeme attributes of the container in respect to suffix transitions and states.
     *
     * @return immutable set
     */
    public ImmutableSet<LexemeAttribute> getLexemeAttributes() {
        return this.lexemeAttributes;
    }

    /**
     * @return true if container has a suffix transition added
     */
    public boolean hasTransitions() {
        return CollectionUtils.isNotEmpty(this.suffixTransitions);
    }

    /**
     * @return last derivation suffix
     */
    public Suffix getLastDerivationSuffix() {
        return this.lastDerivationSuffix;
    }

    /**
     * @return Last non blank derivation suffix
     */
    public SuffixTransition getLastNonBlankDerivation() {
        return this.lastNonBlankDerivation;
    }

    /**
     * @return last suffix transition
     */
    public SuffixTransition getLastSuffixTransition() {
        return this.lastSuffixTransition;
    }

    /**
     * Get all suffix transitions applied to the container
     *
     * @return Unmodifiable (JDK) list
     */
    public List<SuffixTransition> getSuffixTransitions() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableList(this.suffixTransitions);
    }

    private ImmutableSet<LexemeAttribute> findLexemeAttributes() {
        // return lexeme attributes to consider while parsing
        // -> if there is no transition or if there are only blank transitions, then return the attributes of the lexeme
        // ...... since nothing changed since lexeme in terms of phonetics
        // -> otherwise, return no lexeme attributes since all the attributes are invalid because of added suffix transitions
        // -> except when last state is Verb and last transition is derivational.
        // ...... then voicing is not applicable. (e.g. yurut+uyor != yuruduyor)
        // ...... then return NoVoicing

        if (CollectionUtils.isEmpty(this.suffixTransitions))
            return Sets.immutableEnumSet(this.root.getLexeme().getAttributes());

        final SuffixTransition lastNonBlankSuffixTransition = this.getLastNonBlankSuffixTransition();
        if (lastNonBlankSuffixTransition == null) {
            return Sets.immutableEnumSet(this.root.getLexeme().getAttributes());
        } else {
            //TODO:!!!!  necessary for the case yurutemeyecekmisim !-> yurudemeyecekmisim
            final SuffixGraphState lastState = this.getLastState();
            final boolean lastStateIsVerb = PrimaryPos.Verb.equals(lastState.getPrimaryPos());

            if (!lastStateIsVerb) {
                return ImmutableSet.of();
            } else {
                final boolean lastStateIsDerivational = SuffixGraphStateType.DERIVATIONAL.equals(lastState.getType());
                final boolean lastSuffixIsBlank = StringUtils.isBlank(this.getLastSuffixTransition().getSuffixFormApplication().getActualSuffixForm());

                if (lastStateIsDerivational || lastSuffixIsBlank)
                    return ImmutableSet.of(LexemeAttribute.NoVoicing);
                else
                    return ImmutableSet.of();
            }
        }


    }

    private ImmutableSet<PhoneticAttribute> findPhoneticAttributes() {
        // if there are no transitions or no non-blank transitions or only non-alphanumeric transitions
        // ...then use the phonetic attributes of the root (no need to calculate them using LexemeAttributes and root sequence)
        // otherwise, calculate the phonetic attributes from the sequence built so far and the lexeme attributes of the container
        if (this.hasTransitions()) {
            final String suffixSoFar = this.surfaceSoFar.substring(this.root.getSequence().length());
            if (StringUtils.isBlank(suffixSoFar) || !StringUtils.isAlphanumeric(suffixSoFar))
                return Sets.immutableEnumSet(this.root.getPhoneticAttributes());
            else
                return Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(this.getSurfaceSoFar(), this.getLexemeAttributes()));
        } else {
            return Sets.immutableEnumSet(root.getPhoneticAttributes());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MorphemeContainer that = (MorphemeContainer) o;

        if (!rootState.equals(that.rootState)) return false;
        if (phoneticExpectations != null ? !phoneticExpectations.equals(that.phoneticExpectations) : that.phoneticExpectations != null)
            return false;
        if (!remainingSurface.equals(that.remainingSurface)) return false;
        if (!root.equals(that.root)) return false;
        if (!surfaceSoFar.equals(that.surfaceSoFar)) return false;
        if (suffixTransitions != null ? !suffixTransitions.equals(that.suffixTransitions) : that.suffixTransitions != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = root.hashCode();
        result = 31 * result + rootState.hashCode();
        result = 31 * result + surfaceSoFar.hashCode();
        result = 31 * result + remainingSurface.hashCode();
        result = 31 * result + (suffixTransitions != null ? suffixTransitions.hashCode() : 0);
        result = 31 * result + (phoneticExpectations != null ? phoneticExpectations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MorphemeContainer{" +
                "root=" + root +
                ", rootState=" + rootState +
                ", surfaceSoFar='" + surfaceSoFar + '\'' +
                ", remainingSurface='" + remainingSurface + '\'' +
                ", suffixTransitions=" + suffixTransitions +
                ", phoneticExpectations=" + phoneticExpectations +
                '}';
    }


    // unfortunately this is required
    public void overwritePhoneticExpectations(ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this.phoneticExpectations = phoneticExpectations;
    }
}
