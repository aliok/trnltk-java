package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;

public class AppliesToRoot extends AbstractSpecification<MorphemeContainer> {
    private final String rootStr;

    public AppliesToRoot(String rootStr) {
        this.rootStr = rootStr;
    }

    @Override
    public String describe() {
        return String.format("applies_to_root(%s)", this.rootStr);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        return morphemeContainer.getRoot().getSequence().getUnderlyingString().equals(this.rootStr);
    }
}
