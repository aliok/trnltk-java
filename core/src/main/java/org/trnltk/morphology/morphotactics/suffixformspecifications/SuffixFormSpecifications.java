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

package org.trnltk.morphology.morphotactics.suffixformspecifications;

import com.google.common.collect.Sets;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.SecondaryPos;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.suffix.SuffixGroup;
import org.trnltk.common.specification.Specification;
import org.trnltk.common.specification.TrueSpecification;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.model.lexicon.PrimaryPos;

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

    public static final Specification<MorphemeContainer> rootHasSecondaryPos(SecondaryPos secondaryPos) {
        return new RootHasSecondaryPos(secondaryPos);
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

    public static final Specification<MorphemeContainer> rootHasPrimaryPos(PrimaryPos primaryPos) {
        return new RootHasPrimaryPos(primaryPos);
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
