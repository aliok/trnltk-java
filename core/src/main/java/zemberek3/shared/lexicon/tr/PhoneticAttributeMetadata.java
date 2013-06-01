package zemberek3.shared.lexicon.tr;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import zemberek3.shared.common.specification.AbstractSpecification;
import zemberek3.shared.common.specification.Specification;
import zemberek3.shared.common.specification.Specifications;

import java.util.*;

import static zemberek3.shared.lexicon.tr.PhoneticAttribute.*;

public class PhoneticAttributeMetadata {

    private static final ImmutableMap<PhoneticAttribute, Specification> SPECIFICATION_MAP = buildSpecificationMap();

    private static ImmutableMap<PhoneticAttribute, Specification> buildSpecificationMap() {
        final Specification<Collection<PhoneticAttribute>> hasVowel = Specifications.and(
                cannotHave(HasNoVowel),
                mustHaveOneOf(LastVowelBack, LastVowelFrontal),
                mustHaveOneOf(LastVowelRounded, LastVowelUnrounded)
        );
        final Specification<Collection<PhoneticAttribute>> hasNoVowel = mustHave(HasNoVowel).and(hasVowel.not());

        final MustHaveOneOfSpecification mustHaveFirstLetterAttr = mustHaveOneOf(FirstLetterConsonant, FirstLetterVowel);
        final MustHaveOneOfSpecification mustHaveLastLetterAttr = mustHaveOneOf(LastLetterConsonant, LastLetterVowel);

        final HashMap<PhoneticAttribute, Specification> map = new HashMap<>();
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

        final ImmutableMap.Builder<PhoneticAttribute, Specification> builder = new ImmutableMap.Builder<>();
        for (Map.Entry<PhoneticAttribute, Specification> phoneticAttributeSpecificationEntry : map.entrySet()) {
            final PhoneticAttribute key = phoneticAttributeSpecificationEntry.getKey();
            final Specification value = phoneticAttributeSpecificationEntry.getValue();

            builder.put(key, Specifications.and(commonSpec, value));
        }

        return builder.build();

    }

    public static boolean isValid(Collection<PhoneticAttribute> phoneticAttributes) {
        if (CollectionUtils.isEmpty(phoneticAttributes))
            return false;

        for (PhoneticAttribute phoneticAttribute : phoneticAttributes) {
            final Specification specification = SPECIFICATION_MAP.get(phoneticAttribute);
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