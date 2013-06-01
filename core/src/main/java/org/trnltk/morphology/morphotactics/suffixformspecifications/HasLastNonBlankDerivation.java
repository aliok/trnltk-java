package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.model.suffixbased.SuffixForm;
import org.trnltk.morphology.model.suffixbased.SuffixTransition;

public class HasLastNonBlankDerivation extends AbstractSpecification<MorphemeContainer> {
    private final Suffix suffix;
    private final String suffixFormStr;

    public HasLastNonBlankDerivation(Suffix suffix, String suffixFormStr) {
        this.suffix = suffix;
        this.suffixFormStr = suffixFormStr;
    }

    @Override
    public String describe() {
        if (this.suffixFormStr != null)    // can be blank
            return String.format("has_last_non_blank_derivation(%s[%s])", this.suffix, this.suffixFormStr);
        else
            return String.format("has_last_non_blank_derivation(%s)", this.suffix);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        SuffixTransition lastNonBlankDerivation = morphemeContainer.getLastNonBlankDerivation();

        if (lastNonBlankDerivation == null)
            return false;

        final SuffixForm lastNonBlankDerivationSuffixForm = lastNonBlankDerivation.getSuffixFormApplication().getSuffixForm();
        if (this.suffixFormStr != null) {       //can be blank
            return this.suffix.equals(lastNonBlankDerivationSuffixForm.getSuffix()) &&
                    this.suffixFormStr.equals(lastNonBlankDerivationSuffixForm.getForm().getSuffixFormStr());
        } else {
            return this.suffix.equals(lastNonBlankDerivationSuffixForm.getSuffix());
        }
    }
}