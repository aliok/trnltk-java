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

import org.apache.commons.lang3.Validate;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.SuffixForm;
import org.trnltk.model.suffix.SuffixTransition;

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