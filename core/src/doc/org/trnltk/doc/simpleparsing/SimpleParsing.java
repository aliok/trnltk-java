package org.trnltk.doc.simpleparsing;

import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParserFactory;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.List;

public class SimpleParsing {

    public static void main(String[] args) {
        ContextlessMorphologicParser parser = ContextlessMorphologicParserFactory.createSimple();

        List<MorphemeContainer> morphemeContainers = parser.parseStr("kediyi");

        for (MorphemeContainer morphemeContainer : morphemeContainers) {
            System.out.println(MorphemeContainerFormatter.formatMorphemeContainer(morphemeContainer));
        }
    }

}