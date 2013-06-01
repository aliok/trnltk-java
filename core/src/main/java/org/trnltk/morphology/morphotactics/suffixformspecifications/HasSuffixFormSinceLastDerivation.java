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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.morphology.model.suffixbased.SuffixTransition;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;

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
            Set<SuffixTransition> transitionsSinceDerivationSuffix = morphemeContainer.getTransitionsSinceDerivationSuffix();
            return Iterables.any(transitionsSinceDerivationSuffix, new Predicate<SuffixTransition>() {
                @Override
                public boolean apply(SuffixTransition suffixTransition) {
                    return suffixTransition.getSuffixFormApplication().getSuffixForm().getSuffix().equals(suffix) &&
                            suffixTransition.getSuffixFormApplication().getSuffixForm().getForm().getSuffixFormStr().equals(suffixFormStr);
                }
            });
        } else {
            return suffixesSinceDerivationSuffix.contains(this.suffix);
        }
    }
}
