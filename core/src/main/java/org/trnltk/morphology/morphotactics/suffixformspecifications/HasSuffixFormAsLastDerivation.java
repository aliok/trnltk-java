package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import zemberek3.shared.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.morphology.model.suffixbased.SuffixTransition;

public class HasSuffixFormAsLastDerivation extends AbstractSpecification<MorphemeContainer> {
    private final Suffix suffix;
    private final String suffixFormStr;

    HasSuffixFormAsLastDerivation(Suffix suffix, String suffixFormStr) {
        this.suffix = suffix;
        this.suffixFormStr = suffixFormStr;
    }

    @Override
    public String describe() {
        if (this.suffixFormStr != null)    // can be blank
            return String.format("has_suffix_form_as_last_deriv(%s[%s])", this.suffix, this.suffixFormStr);
        else
            return String.format("has_suffix_form_as_last_deriv(%s)", this.suffix);
    }


    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        SuffixTransition lastDerivationSuffixTransition = morphemeContainer.getLastDerivationSuffixTransition();
        if (lastDerivationSuffixTransition == null)
            return false;

        if (this.suffixFormStr != null) {       //can be blank
            return lastDerivationSuffixTransition.getSuffixFormApplication().getSuffixForm().getSuffix().equals(this.suffix)
                    && lastDerivationSuffixTransition.getSuffixFormApplication().getSuffixForm().getForm().getSuffixFormStr().equals(this.suffixFormStr);
        } else {
            return lastDerivationSuffixTransition.getSuffixFormApplication().getSuffixForm().getSuffix().equals(this.suffix);
        }
    }
}
