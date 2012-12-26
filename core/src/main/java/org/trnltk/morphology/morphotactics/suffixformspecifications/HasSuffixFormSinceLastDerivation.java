package org.trnltk.morphology.morphotactics.suffixformspecifications;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.MorphemeContainer;
import org.trnltk.morphology.model.Suffix;
import org.trnltk.morphology.model.Transition;

import java.util.Collection;
import java.util.Set;

class HasSuffixFormSinceLastDerivation extends AbstractSpecification<MorphemeContainer> {

    private final Suffix suffix;
    private final String suffixFormStr;

    HasSuffixFormSinceLastDerivation(Suffix suffix, String suffixFormStr) {
        this.suffix = suffix;
        this.suffixFormStr = suffixFormStr;
    }

    @Override
    public String describe() {
        if (this.suffixFormStr != null)    // can be blank
            return String.format("has_suffix_form_since_last_deriv(%s[%s])", this.suffix, this.suffixFormStr);
        else
            return String.format("has_suffix_form_since_last_deriv(%s)", this.suffix);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        final Collection<Suffix> suffixesSinceDerivationSuffix = morphemeContainer.getSuffixesSinceDerivationSuffix();
        if (CollectionUtils.isEmpty(suffixesSinceDerivationSuffix))
            return false;

        if (suffixFormStr != null) {    // can be blank
            Set<Transition> transitionsSinceDerivationSuffix = morphemeContainer.getTransitionsSinceDerivationSuffix();
            return Iterables.any(transitionsSinceDerivationSuffix, new Predicate<Transition>() {
                @Override
                public boolean apply(Transition transition) {
                    return transition.getSuffixFormApplication().getSuffixForm().getSuffix().equals(suffix) &&
                            transition.getSuffixFormApplication().getSuffixForm().getForm().getSuffixFormStr().equals(suffixFormStr);
                }
            });
        } else {
            return suffixesSinceDerivationSuffix.contains(this.suffix);
        }
    }
}
