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
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.suffix.FreeTransitionSuffix;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.SuffixTransition;
import org.trnltk.model.suffix.ZeroTransitionSuffix;
import org.trnltk.common.specification.AbstractSpecification;

import java.util.Collection;
import java.util.Set;

public class HasLexemeAttributes extends AbstractSpecification<MorphemeContainer> {
    private final ImmutableSet<LexemeAttribute> lexemeAttributes;

    public HasLexemeAttributes(ImmutableSet<LexemeAttribute> lexemeAttributes) {
        this.lexemeAttributes = lexemeAttributes;
    }

    @Override
    public String describe() {
        return String.format("has_lexeme_attributes(%s)", this.lexemeAttributes);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        Collection<SuffixTransition> suffixTransitions = morphemeContainer.getSuffixTransitions();

        // filter out free suffixTransitions, zero suffixTransitions and suffixTransitions with empty suffix forms
        // since all those three don't change the phonetic attributes of a string
        suffixTransitions = Collections2.filter(suffixTransitions, new Predicate<SuffixTransition>() {
            @Override
            public boolean apply(SuffixTransition input) {
                return !(input.getSuffixFormApplication().getSuffixForm().getSuffix() instanceof FreeTransitionSuffix) &&
                        !(input.getSuffixFormApplication().getSuffixForm().getSuffix() instanceof ZeroTransitionSuffix) &&
                        StringUtils.isNotEmpty(input.getSuffixFormApplication().getActualSuffixForm()); // The str " " would change the phonetics!
            }
        });

        if (CollectionUtils.isNotEmpty(suffixTransitions))
            return true;

        final Set<LexemeAttribute> morphemeContainerLexemeAttributes = morphemeContainer.getRoot().getLexeme().getAttributes();
        if (CollectionUtils.isEmpty(morphemeContainerLexemeAttributes))
            return false;

        return morphemeContainerLexemeAttributes.containsAll(this.lexemeAttributes);
    }
}
