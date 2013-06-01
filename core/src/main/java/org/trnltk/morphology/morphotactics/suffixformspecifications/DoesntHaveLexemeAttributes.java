package org.trnltk.morphology.morphotactics.suffixformspecifications;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.suffixbased.FreeTransitionSuffix;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.model.suffixbased.SuffixTransition;
import org.trnltk.morphology.model.suffixbased.ZeroTransitionSuffix;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.*;

import java.util.Collection;
import java.util.Set;

public class DoesntHaveLexemeAttributes extends AbstractSpecification<MorphemeContainer> {
    private final ImmutableSet<LexemeAttribute> lexemeAttributes;

    public DoesntHaveLexemeAttributes(ImmutableSet<LexemeAttribute> lexemeAttributes) {
        this.lexemeAttributes = lexemeAttributes;
    }

    @Override
    public String describe() {
        return String.format("doesnt_have_lexeme_attributes(%s)", this.lexemeAttributes);
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

        return !CollectionUtils.containsAny(morphemeContainerLexemeAttributes, this.lexemeAttributes);  // cannot have even one
    }
}
