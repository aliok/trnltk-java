package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.suffixbased.SuffixTransition;
import zemberek3.shared.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;

public class LastSuffixGoesToStateWithType extends AbstractSpecification<MorphemeContainer> {
    private final SuffixGraphStateType suffixGraphStateType;

    public LastSuffixGoesToStateWithType(SuffixGraphStateType suffixGraphStateType) {
        this.suffixGraphStateType = suffixGraphStateType;
    }

    @Override
    public String describe() {
        return String.format("suffix_goes_to_state_type(%s)", this.suffixGraphStateType);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        SuffixTransition lastSuffixTransition = morphemeContainer.getLastSuffixTransition();
        if (lastSuffixTransition == null)
            return false;

        return lastSuffixTransition.getTargetState().getType().equals(this.suffixGraphStateType);
    }
}
