package org.trnltk.morphology.model;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.morphology.phonetics.PhoneticAttribute;
import org.trnltk.morphology.phonetics.PhoneticExpectation;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import zemberek3.lexicon.PrimaryPos;

import java.util.*;

public class MorphemeContainer {

    final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    // final values
    private final Root root;
    private final SuffixGraphState rootState;

    // below are changed with transitions, but do have a value set in constructor
    private TurkishSequence surfaceSoFar;
    private String remainingSurface;
    private LinkedList<Transition> transitions;
    private SuffixGraphState lastState;
    private String wholeSurface;
    private ImmutableSet<PhoneticExpectation> phoneticExpectations;
    private ImmutableSet<LexemeAttribute> lexemeAttributes;
    private ImmutableSet<PhoneticAttribute> phoneticAttributes;

    // below are changed with transitions, but do have a value set in constructor
    private Transition lastTransition = null;
    private Transition lastDerivationTransition = null;
    private Transition lastNonBlankTransition = null;
    private Suffix lastDerivationSuffix = null;
    private Transition lastNonBlankDerivation = null;
    private LinkedHashSet<Transition> transitionsSinceDerivationSuffix = new LinkedHashSet<Transition>();
    private LinkedHashSet<Transition> transitionsFromDerivationSuffix = new LinkedHashSet<Transition>();
    private LinkedHashSet<Suffix> suffixesSinceDerivationSuffix = new LinkedHashSet<Suffix>();
    private LinkedHashSet<SuffixGroup> suffixGroupsSinceLastDerivationSuffix = new LinkedHashSet<SuffixGroup>();

    public MorphemeContainer(Root root, SuffixGraphState rootState, String remainingSurface) {
        this.root = root;
        this.rootState = rootState;

        this.surfaceSoFar = root.getSequence();
        this.remainingSurface = remainingSurface;
        this.transitions = new LinkedList<Transition>();
        this.lastState = this.rootState;
        this.wholeSurface = Strings.nullToEmpty(this.surfaceSoFar.getUnderlyingString()) + Strings.nullToEmpty(this.remainingSurface);
        this.phoneticExpectations = Sets.immutableEnumSet(root.getPhoneticExpectations());
        this.lexemeAttributes = this.root.getLexeme().getAttributes();
        this.phoneticAttributes = this.root.getPhoneticAttributes();
    }

    public MorphemeContainer(final MorphemeContainer toCopy) {
        this.root = toCopy.root;
        this.rootState = toCopy.rootState;
        this.surfaceSoFar = toCopy.surfaceSoFar;
        this.remainingSurface = toCopy.remainingSurface;
        //noinspection unchecked
        this.transitions = (LinkedList<Transition>) toCopy.transitions.clone();
        this.lastState = toCopy.lastState;
        this.wholeSurface = toCopy.wholeSurface;
        this.phoneticExpectations = toCopy.phoneticExpectations;
        this.lexemeAttributes = toCopy.lexemeAttributes;
        this.phoneticAttributes = toCopy.phoneticAttributes;

        this.lastTransition = toCopy.lastTransition;
        this.lastDerivationTransition = toCopy.lastDerivationTransition;
        this.lastNonBlankTransition = toCopy.lastNonBlankTransition;
        this.lastDerivationSuffix = toCopy.lastDerivationSuffix;
        this.lastNonBlankDerivation = toCopy.lastNonBlankDerivation;
        //noinspection unchecked
        this.transitionsSinceDerivationSuffix = (LinkedHashSet<Transition>) toCopy.transitionsSinceDerivationSuffix.clone();
        //noinspection unchecked
        this.transitionsFromDerivationSuffix = (LinkedHashSet<Transition>) toCopy.transitionsFromDerivationSuffix.clone();
        //noinspection unchecked
        this.suffixesSinceDerivationSuffix = (LinkedHashSet<Suffix>) toCopy.suffixesSinceDerivationSuffix.clone();
        //noinspection unchecked
        this.suffixGroupsSinceLastDerivationSuffix = (LinkedHashSet<SuffixGroup>) toCopy.suffixGroupsSinceLastDerivationSuffix.clone();
    }

    public void addTransition(SuffixFormApplication suffixFormApplication, SuffixGraphState targetState) {
        final Transition newTransition = new Transition(this.lastState, suffixFormApplication, targetState);
        this.transitions.add(newTransition);

        this.reinitialize(newTransition);
    }

    private void reinitialize(final Transition newTransition) {
        Validate.notNull(newTransition);

        final SuffixFormApplication suffixFormApplication = newTransition.getSuffixFormApplication();
        final SuffixForm suffixForm = suffixFormApplication.getSuffixForm();

        this.surfaceSoFar = this.surfaceSoFar.append(suffixFormApplication.getActualSuffixForm());
        this.remainingSurface = StringUtils.isBlank(this.remainingSurface) ?
                StringUtils.EMPTY :
                this.remainingSurface.substring(suffixFormApplication.getActualSuffixForm().length());

        if (suffixFormApplication.getSuffixForm().getForm().isNotBlank())
            this.phoneticExpectations = ImmutableSet.of();

        this.lastState = newTransition.getTargetState();
        this.lastTransition = newTransition;
        this.wholeSurface = Strings.nullToEmpty(this.surfaceSoFar.getUnderlyingString()) + Strings.nullToEmpty(this.remainingSurface);

        if (newTransition.isDerivational()) {
            this.lastDerivationTransition = newTransition;
            this.lastDerivationSuffix = suffixForm.getSuffix();
            this.transitionsSinceDerivationSuffix = new LinkedHashSet<Transition>();
            //noinspection unchecked
            this.transitionsFromDerivationSuffix = new LinkedHashSet(Arrays.asList(newTransition));
            this.suffixesSinceDerivationSuffix = new LinkedHashSet<Suffix>();
            this.suffixGroupsSinceLastDerivationSuffix = new LinkedHashSet<SuffixGroup>();

            if (suffixFormApplication.getSuffixForm().getForm().isNotBlank())
                this.lastNonBlankDerivation = newTransition;

        } else {
            this.transitionsSinceDerivationSuffix.add(newTransition);
            this.transitionsFromDerivationSuffix.add(newTransition);
            this.suffixesSinceDerivationSuffix.add(suffixForm.getSuffix());
            if (suffixForm.getSuffix().getSuffixGroup() != null)
                this.suffixGroupsSinceLastDerivationSuffix.add(suffixForm.getSuffix().getSuffixGroup());
        }

        if (suffixFormApplication.getSuffixForm().getForm().isNotBlank())
            this.lastNonBlankTransition = newTransition;

        this.phoneticAttributes = this.findPhoneticAttributes();
        this.lexemeAttributes = this.findLexemeAttributes();
    }

    public void setRemainingSurface(String remainingSurface) {
        this.remainingSurface = remainingSurface;
    }

    public SuffixGraphState getLastState() {
        return this.lastState;
    }

    public TurkishSequence getSurfaceSoFar() {
        return surfaceSoFar;
    }

    public String getRemainingSurface() {
        return remainingSurface;
    }

    public Set<Transition> getTransitionsSinceDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.transitionsSinceDerivationSuffix);
    }

    public Set<Transition> getTransitionsFromDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.transitionsFromDerivationSuffix);
    }

    public Set<Suffix> getSuffixesSinceDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.suffixesSinceDerivationSuffix);
    }

    public Set<SuffixGroup> getSuffixGroupsSinceLastDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.suffixGroupsSinceLastDerivationSuffix);
    }

    public Transition getLastDerivationTransition() {
        return this.lastDerivationTransition;
    }

    public Root getRoot() {
        return this.root;
    }

    public SuffixGraphState getRootState() {
        return rootState;
    }

    public ImmutableSet<PhoneticAttribute> getPhoneticAttributes() {
        return this.phoneticAttributes;
    }

    public ImmutableSet<PhoneticExpectation> getPhoneticExpectations() {
        return this.phoneticExpectations;
    }

    public Transition getLastNonBlankTransition() {
        return this.lastNonBlankTransition;
    }

    public ImmutableSet<LexemeAttribute> getLexemeAttributes() {
        return this.lexemeAttributes;
    }

    public boolean hasTransitions() {
        return CollectionUtils.isNotEmpty(this.transitions);
    }

    public Suffix getLastDerivationSuffix() {
        return this.lastDerivationSuffix;
    }

    public Transition getLastNonBlankDerivation() {
        return this.lastNonBlankDerivation;
    }

    public Transition getLastTransition() {
        return this.lastTransition;
    }

    public List<Transition> getTransitions() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableList(this.transitions);
    }

    private ImmutableSet<LexemeAttribute> findLexemeAttributes() {
        if (CollectionUtils.isEmpty(this.transitions))
            return this.root.getLexeme().getAttributes();

        final Transition lastNonBlankTransition = this.getLastNonBlankTransition();
        if (lastNonBlankTransition == null) {
            return this.root.getLexeme().getAttributes();
        } else {
            //TODO:!!!!  necessary for the case yurutemeyecekmisim !-> yurudemeyecekmisim
            final SuffixGraphState lastState = this.getLastState();
            final boolean lastStateIsVerb = PrimaryPos.Verb.equals(lastState.getPrimaryPos());

            if (!lastStateIsVerb) {
                return ImmutableSet.of();
            } else {
                final boolean lastStateIsDerivational = SuffixGraphStateType.DERIVATIONAL.equals(lastState.getType());
                final boolean lastSuffixIsBlank = StringUtils.isBlank(this.getLastTransition().getSuffixFormApplication().getActualSuffixForm());

                if (lastStateIsDerivational || lastSuffixIsBlank)
                    return ImmutableSet.of(LexemeAttribute.NoVoicing);
                else
                    return ImmutableSet.of();
            }
        }


    }

    private ImmutableSet<PhoneticAttribute> findPhoneticAttributes() {
        if (this.hasTransitions()) {
            final String suffixSoFar = this.surfaceSoFar.substring(this.root.getSequence().length());
            if (StringUtils.isBlank(suffixSoFar) || !StringUtils.isAlphanumeric(suffixSoFar))
                return this.root.getPhoneticAttributes();
            else
                return Sets.immutableEnumSet(phoneticsAnalyzer.calculatePhoneticAttributes(this.getSurfaceSoFar(), this.getLexemeAttributes()));
        } else {
            return root.getPhoneticAttributes();
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
        if (transitions != null ? !transitions.equals(that.transitions) : that.transitions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = root.hashCode();
        result = 31 * result + rootState.hashCode();
        result = 31 * result + surfaceSoFar.hashCode();
        result = 31 * result + remainingSurface.hashCode();
        result = 31 * result + (transitions != null ? transitions.hashCode() : 0);
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
                ", transitions=" + transitions +
                ", phoneticExpectations=" + phoneticExpectations +
                '}';
    }
}
