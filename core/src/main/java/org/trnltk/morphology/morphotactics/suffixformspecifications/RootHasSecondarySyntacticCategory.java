package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.morphology.model.SecondarySyntacticCategory;
import org.trnltk.morphology.model.MorphemeContainer;

public class RootHasSecondarySyntacticCategory extends AbstractSpecification<MorphemeContainer> {
    private final SecondarySyntacticCategory secondarySyntacticCategory;

    public RootHasSecondarySyntacticCategory(SecondarySyntacticCategory secondarySyntacticCategory) {
        this.secondarySyntacticCategory = secondarySyntacticCategory;
    }

    @Override
    public String describe() {
        return String.format("root_has_secondary_syntactic_category(%s)", secondarySyntacticCategory);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        return this.secondarySyntacticCategory.equals(morphemeContainer.getRoot().getLexeme().getSecondarySyntacticCategory());
    }
}
