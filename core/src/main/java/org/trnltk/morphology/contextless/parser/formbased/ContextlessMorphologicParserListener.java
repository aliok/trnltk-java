package org.trnltk.morphology.contextless.parser.formbased;

import org.trnltk.morphology.model.suffixbased.MorphemeContainer;

public interface ContextlessMorphologicParserListener {

    public void onMorphemeContainerInvalidated(MorphemeContainer morphemeContainer);

}
