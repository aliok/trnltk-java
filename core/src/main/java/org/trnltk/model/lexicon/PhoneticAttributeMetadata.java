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

package org.trnltk.model.lexicon;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.common.specification.Specification;
import org.trnltk.common.specification.Specifications;

import java.util.*;

import static org.trnltk.model.lexicon.PhoneticAttribute.*;

/**
 * Specifications for {@link PhoneticAttribute}s.
 * <p/>
 * Defines what {@link PhoneticAttribute}s can co-exist or cannot co-exist or is required for each other.
 * <p/>
 * These rules are especially important when computing the possible valid {@link org.trnltk.morphology.contextless.parser.PhoneticAttributeSets}
 *
 * @see org.trnltk.morphology.contextless.parser.PhoneticAttributeSets
 */
public class PhoneticAttributeMetadata {

    private static final ImmutableMap<PhoneticAttribute, Specification<Collection<PhoneticAttribute>>> SPECIFICATION_MAP = buildSpecificationMap();

    private static ImmutableMap<PhoneticAttribute, Specification<Collection<PhoneticAttribute>>> buildSpecificationMap() {
        final Specification<Collection<PhoneticAttribute>> hasVowel = Specifications.and(
                cannotHave(HasNoVowel),
                mustHaveOneOf(LastVowelBack, LastVowelFrontal),
                mustHaveOneOf(LastVowelRounded, LastVowelUnrounded)
        );
        final Specification<Collection<PhoneticAttribute>> hasNoVowel = mustHave(HasNoVowel).and(hasVowel.not());

        final MustHaveOneOfSpecification mustHaveFirstLetterAttr = mustHaveOneOf(FirstLetterConsonant, FirstLetterVowel);
        final MustHaveOneOfSpecification mustHaveLastLetterAttr = mustHaveOneOf(LastLetterConsonant, LastLetterVowel);

        final HashMap<PhoneticAttribute, Specification<Collection<PhoneticAttribute>>> map = new HashMap<PhoneticAttribute, Specification<Collection<PhoneticAttribute>>>();

        // define specs for all attributes

        // following means:
        // when a sequence/root/lexeme/etc. has LastLetterVowel attribute, then the sequence/root/lexeme/etc.
        // -> cannot have any one of LastLetterConsonant, HasNoVowel, LastLetterVoiceless, LastLetterVoicelessStop attributes
        // -> must have LastLetterNotVoiceless attribute
        // -> ... so forth
        map.put(LastLetterVowel,
                Specifications.and(
                        cannotHave(LastLetterConsonant, HasNoVowel, LastLetterVoiceless, LastLetterVoicelessStop),
                        mustHave(LastLetterNotVoiceless),
                        mustHaveOneOf(LastVowelFrontal, LastVowelBack),
                        mustHaveOneOf(LastVowelRounded, LastVowelUnrounded)
                )
        );
        map.put(LastLetterConsonant,
                Specifications.and(
                        cannotHave(LastLetterVowel),
                        mustHaveOneOf(LastLetterVoiceless, LastLetterNotVoiceless),
                        Specifications.or(
                                hasNoVowel,
                                hasVowel
                        )
                )
        );
        map.put(LastVowelFrontal,
                Specifications.and(
                        cannotHave(LastVowelBack),
                        hasVowel,
                        mustHaveOneOf(LastLetterVowel, LastLetterConsonant)
                )
        );
        map.put(LastVowelBack,
                Specifications.and(
                        cannotHave(LastVowelFrontal),
                        hasVowel,
                        mustHaveOneOf(LastLetterVowel, LastLetterConsonant)
                )
        );
        map.put(LastVowelRounded,
                Specifications.and(
                        cannotHave(LastVowelUnrounded, HasNoVowel),
                        hasVowel,
                        mustHaveOneOf(LastLetterVowel, LastLetterConsonant)
                )
        );
        map.put(LastVowelUnrounded,
                Specifications.and(
                        cannotHave(LastVowelRounded, HasNoVowel),
                        hasVowel,
                        mustHaveOneOf(LastLetterVowel, LastLetterConsonant)
                )
        );
        map.put(LastLetterVoiceless,
                Specifications.and(
                        cannotHave(LastLetterVowel, LastLetterNotVoiceless),
                        mustHave(LastLetterConsonant)
                )
        );
        map.put(LastLetterNotVoiceless,
                Specifications.and(
                        cannotHave(LastLetterVoiceless, LastLetterVoicelessStop),
                        mustHaveOneOf(LastLetterConsonant, LastLetterVowel)
                )
        );
        map.put(LastLetterVoicelessStop,
                Specifications.and(
                        cannotHave(LastLetterVowel, LastLetterNotVoiceless),
                        mustHave(LastLetterConsonant, LastLetterVoiceless)
                )
        );
        map.put(FirstLetterVowel,
                Specifications.and(
                        cannotHave(FirstLetterConsonant, HasNoVowel),
                        mustHaveOneOf(LastVowelFrontal, LastVowelBack),
                        mustHaveOneOf(LastVowelRounded, LastVowelUnrounded),
                        mustHaveOneOf(LastLetterVowel, LastLetterConsonant)
                )
        );
        map.put(FirstLetterConsonant,
                Specifications.and(
                        cannotHave(FirstLetterVowel),
                        mustHaveOneOf(LastLetterVowel, LastLetterConsonant)
                )
        );
        //noinspection unchecked
        map.put(HasNoVowel,
                Specifications.and(
                        hasNoVowel,
                        cannotHave(FirstLetterVowel),
                        mustHave(FirstLetterConsonant, LastLetterConsonant),
                        mustHaveOneOf(LastLetterVoiceless, LastLetterNotVoiceless),
                        hasVowel.not()
                )
        );

        final Specification<Collection<PhoneticAttribute>> commonSpec = Specifications.and(
                Specifications.or(
                        hasVowel,
                        hasNoVowel
                ),
                mustHaveFirstLetterAttr,
                mustHaveLastLetterAttr
        );

        final ImmutableMap.Builder<PhoneticAttribute, Specification<Collection<PhoneticAttribute>>> builder = new ImmutableMap.Builder<PhoneticAttribute, Specification<Collection<PhoneticAttribute>>>();
        for (Map.Entry<PhoneticAttribute, Specification<Collection<PhoneticAttribute>>> phoneticAttributeSpecificationEntry : map.entrySet()) {
            final PhoneticAttribute key = phoneticAttributeSpecificationEntry.getKey();
            final Specification<Collection<PhoneticAttribute>> value = phoneticAttributeSpecificationEntry.getValue();

            // all attributes' specs must also include the common spec which is always valid

            builder.put(key, Specifications.and(commonSpec, value));
        }

        return builder.build();

    }

    /**
     * Checks a the given collections in terms of attributes' specs.
     *
     * @param phoneticAttributes A collection of attributes
     * @return true/false
     */
    public static boolean isValid(Collection<PhoneticAttribute> phoneticAttributes) {
        if (CollectionUtils.isEmpty(phoneticAttributes))
            return false;

        for (PhoneticAttribute phoneticAttribute : phoneticAttributes) {
            final Specification<Collection<PhoneticAttribute>> specification = SPECIFICATION_MAP.get(phoneticAttribute);
            if (!specification.isSatisfiedBy(phoneticAttributes))
                return false;
        }

        return true;
    }

    private static CannotHaveSpecification cannotHave(PhoneticAttribute... phoneticAttributes) {
        return new CannotHaveSpecification(phoneticAttributes);
    }

    private static MustHaveSpecification mustHave(PhoneticAttribute... phoneticAttributes) {
        return new MustHaveSpecification(phoneticAttributes);
    }

    private static MustHaveOneOfSpecification mustHaveOneOf(PhoneticAttribute... phoneticAttributes) {
        return new MustHaveOneOfSpecification(phoneticAttributes);
    }

    static abstract class PhoneticAttributeSpecification extends AbstractSpecification<Collection<PhoneticAttribute>> {
    }

    static class CannotHaveSpecification extends PhoneticAttributeSpecification {

        private final EnumSet<PhoneticAttribute> phoneticAttributes;

        CannotHaveSpecification(PhoneticAttribute... phoneticAttributes) {
            this.phoneticAttributes = EnumSet.copyOf(Arrays.asList(phoneticAttributes));
        }

        @Override
        public String describe() {
            return String.format("cannotHavePhoneticAttr(%s)", phoneticAttributes);
        }

        @Override
        public boolean isSatisfiedBy(Collection<PhoneticAttribute> object) {
            return CollectionUtils.isEmpty(object) || !CollectionUtils.containsAny(object, phoneticAttributes);
        }
    }

    static class MustHaveSpecification extends PhoneticAttributeSpecification {

        private final EnumSet<PhoneticAttribute> phoneticAttributes;

        MustHaveSpecification(PhoneticAttribute... phoneticAttributes) {
            this.phoneticAttributes = EnumSet.copyOf(Arrays.asList(phoneticAttributes));
        }

        @Override
        public String describe() {
            return String.format("mustHavePhoneticAttr(%s)", phoneticAttributes);
        }

        @Override
        public boolean isSatisfiedBy(Collection<PhoneticAttribute> object) {
            return CollectionUtils.isNotEmpty(object) && object.containsAll(this.phoneticAttributes);
        }
    }

    static class MustHaveOneOfSpecification extends PhoneticAttributeSpecification {

        private final EnumSet<PhoneticAttribute> phoneticAttributes;

        MustHaveOneOfSpecification(PhoneticAttribute... phoneticAttributes) {
            this.phoneticAttributes = EnumSet.copyOf(Arrays.asList(phoneticAttributes));
        }

        @Override
        public String describe() {
            return String.format("mustHaveOneOfPhoneticAttr(%s)", phoneticAttributes);
        }

        @Override
        public boolean isSatisfiedBy(Collection<PhoneticAttribute> object) {
            return CollectionUtils.isNotEmpty(object) && CollectionUtils.containsAny(object, this.phoneticAttributes);
        }
    }

}