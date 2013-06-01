package org.trnltk.morphology.model.suffixbased;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticExpectation;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import zemberek3.shared.lexicon.PrimaryPos;

import java.util.*;

public class MorphemeContainer {

    final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    // final values
    private final Root root;
    private final SuffixGraphState rootState;

    // below are changed with suffixTransitions, but do have a value set in constructor
    private TurkishSequence surfaceSoFar;
    private String remainingSurface;
    private LinkedList<SuffixTransition> suffixTransitions;
    private SuffixGraphState lastState;
    private String wholeSurface;
    private ImmutableSet<PhoneticExpectation> phoneticExpectations;
    private ImmutableSet<LexemeAttribute> lexemeAttributes;
    private ImmutableSet<PhoneticAttribute> phoneticAttributes;

    // below are changed with suffixTransitions, but do have a value set in constructor
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

    public void addTransition(SuffixFormApplication suffixFormApplication, SuffixGraphState targetState) {
        final SuffixTransition newSuffixTransition = new SuffixTransition(this.lastState, suffixFormApplication, targetState);
        this.suffixTransitions.add(newSuffixTransition);

        this.reinitialize(newSuffixTransition);
    }

    private void reinitialize(final SuffixTransition newSuffixTransition) {
        Validate.notNull(newSuffixTransition);

        final SuffixFormApplication suffixFormApplication = newSuffixTransition.getSuffixFormApplication();
        final SuffixForm suffixForm = suffixFormApplication.getSuffixForm();

        this.surfaceSoFar = this.surfaceSoFar.append(suffixFormApplication.getActualSuffixForm());
        this.remainingSurface = StringUtils.isBlank(this.remainingSurface) ?
                StringUtils.EMPTY :
                this.remainingSurface.substring(suffixFormApplication.getActualSuffixForm().length());

        if (suffixFormApplication.getSuffixForm().getForm().isNotBlank())
            this.phoneticExpectations = ImmutableSet.of();

        this.lastState = newSuffixTransition.getTargetState();
        this.lastSuffixTransition = newSuffixTransition;
        this.wholeSurface = Strings.nullToEmpty(this.surfaceSoFar.getUnderlyingString()) + Strings.nullToEmpty(this.remainingSurface);

        if (newSuffixTransition.isDerivational()) {
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

        this.lexemeAttributes = this.findLexemeAttributes();
        this.phoneticAttributes = this.findPhoneticAttributes();
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

    public Set<SuffixTransition> getTransitionsSinceDerivationSuffix() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableSet(this.transitionsSinceDerivationSuffix);
    }

    public Set<SuffixTransition> getTransitionsFromDerivationSuffix() {
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

    public SuffixTransition getLastDerivationSuffixTransition() {
        return this.lastDerivationSuffixTransition;
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

    public SuffixTransition getLastNonBlankSuffixTransition() {
        return this.lastNonBlankSuffixTransition;
    }

    public ImmutableSet<LexemeAttribute> getLexemeAttributes() {
        return this.lexemeAttributes;
    }

    public boolean hasTransitions() {
        return CollectionUtils.isNotEmpty(this.suffixTransitions);
    }

    public Suffix getLastDerivationSuffix() {
        return this.lastDerivationSuffix;
    }

    public SuffixTransition getLastNonBlankDerivation() {
        return this.lastNonBlankDerivation;
    }

    public SuffixTransition getLastSuffixTransition() {
        return this.lastSuffixTransition;
    }

    public List<SuffixTransition> getSuffixTransitions() {
        // since Guava immutable collections are copying the items, using JDK unmodifiable collections for a better performance
        return Collections.unmodifiableList(this.suffixTransitions);
    }

    private ImmutableSet<LexemeAttribute> findLexemeAttributes() {
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
        if (suffixTransitions != null ? !suffixTransitions.equals(that.suffixTransitions) : that.suffixTransitions != null) return false;

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

    //TODO:
    public void setPhoneticExpectations(ImmutableSet<PhoneticExpectation> phoneticExpectations) {
        this.phoneticExpectations = phoneticExpectations;
    }
}
