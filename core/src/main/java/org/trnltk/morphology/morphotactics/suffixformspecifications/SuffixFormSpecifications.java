package org.trnltk.morphology.morphotactics.suffixformspecifications;

import com.google.common.collect.Sets;
import org.trnltk.common.specification.Specification;
import org.trnltk.common.specification.TrueSpecification;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import zemberek3.lexicon.PrimaryPos;

import java.util.Arrays;

public class SuffixFormSpecifications {

    public static final Specification<MorphemeContainer> doesnt(final Specification<MorphemeContainer> specification) {
        return specification.not();
    }

    ////// preconditions
    public static final Specification<MorphemeContainer> comesAfter(Suffix suffix) {
        return comesAfter(suffix, null);
    }

    public static final Specification<MorphemeContainer> comesAfter(Suffix suffix, String suffixFormStr) {
        return new HasSuffixFormSinceLastDerivation(suffix, suffixFormStr);
    }

    public static final Specification<MorphemeContainer> doesntComeAfter(Suffix suffix) {
        return comesAfter(suffix).not();
    }

    public static final Specification<MorphemeContainer> comesAfterDerivation(Suffix suffix) {
        return comesAfterDerivation(suffix, null);
    }

    public static final Specification<MorphemeContainer> comesAfterDerivation(Suffix suffix, String suffixFormStr) {
        return new HasSuffixFormAsLastDerivation(suffix, suffixFormStr);
    }

    public static final Specification<MorphemeContainer> doesntComeAfterDerivation(Suffix suffix) {
        return doesntComeAfterDerivation(suffix, null);
    }

    public static final Specification<MorphemeContainer> doesntComeAfterDerivation(Suffix suffix, String suffixFormStr) {
        return comesAfterDerivation(suffix, suffixFormStr).not();
    }

    public static final Specification<MorphemeContainer> appliesToRoot(String rootStr) {
        return new AppliesToRoot(rootStr);
    }

    public static final Specification<MorphemeContainer> rootHasSecondarySyntacticCategory(SecondarySyntacticCategory secondarySyntacticCategory) {
        return new RootHasSecondarySyntacticCategory(secondarySyntacticCategory);
    }

    public static final Specification<MorphemeContainer> hasLexemeAttributes(LexemeAttribute... lexemeAttributes) {
        return new HasLexemeAttributes(Sets.immutableEnumSet(Arrays.asList(lexemeAttributes)));
    }

    public static final Specification<MorphemeContainer> doesntHaveLexemeAttributes(LexemeAttribute... lexemeAttributes) {
        return new DoesntHaveLexemeAttributes(Sets.immutableEnumSet(Arrays.asList(lexemeAttributes)));
    }

    public static final Specification<MorphemeContainer> comesAfterLastNonBlankDerivation(Suffix suffix) {
        return comesAfterLastNonBlankDerivation(suffix, null);
    }

    public static final Specification<MorphemeContainer> comesAfterLastNonBlankDerivation(Suffix suffix, String suffixFormStr) {
        return new HasLastNonBlankDerivation(suffix, suffixFormStr);
    }

    public static final Specification<MorphemeContainer> rootHasSyntacticCategory(PrimaryPos primaryPos) {
        return new RootHasSyntacticCategory(primaryPos);
    }

    public static final Specification<MorphemeContainer> rootHasProgressiveVowelDrop() {
        return new RootHasVowelDrop();
    }

    ////////// postconditions
    public static final Specification<MorphemeContainer> followedBy(Suffix suffix) {
        return followedBy(suffix, null);
    }

    public static final Specification<MorphemeContainer> followedBy(Suffix suffix, String suffixFormStr) {
        return new HasSuffixFormSinceLastDerivation(suffix, suffixFormStr);
    }

    public static final Specification<MorphemeContainer> followedByOneFromGroup(SuffixGroup suffixGroup) {
        @SuppressWarnings("unchecked")
        Specification<MorphemeContainer> spec = (Specification<MorphemeContainer>) TrueSpecification.INSTANCE;
        for (Suffix suffix : suffixGroup.getSuffixes()) {
            spec = spec.or(followedBy(suffix));
        }

        return spec;
    }

    public static final Specification<MorphemeContainer> followedByDerivation(Suffix suffix) {
        return followedByDerivation(suffix, null);
    }

    public static final Specification<MorphemeContainer> followedByDerivation(Suffix suffix, String suffixFormStr) {
        return new HasSuffixFormAsLastDerivation(suffix, suffixFormStr);
    }

    public static final Specification<MorphemeContainer> followedBySuffixGoesTo(SuffixGraphStateType followedBySuffixGoesTo) {
        return new LastSuffixGoesToStateWithType(followedBySuffixGoesTo);
    }

}
