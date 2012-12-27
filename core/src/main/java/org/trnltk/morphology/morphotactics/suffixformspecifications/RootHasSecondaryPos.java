package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.MorphemeContainer;
import org.trnltk.morphology.model.SecondaryPos;

public class RootHasSecondaryPos extends AbstractSpecification<MorphemeContainer> {
    private final SecondaryPos secondaryPos;

    public RootHasSecondaryPos(SecondaryPos secondaryPos) {
        this.secondaryPos = secondaryPos;
    }

    @Override
    public String describe() {
        return String.format("root_has_secondary_pos(%s)", secondaryPos);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        return this.secondaryPos.equals(morphemeContainer.getRoot().getLexeme().getSecondaryPos());
    }
}
