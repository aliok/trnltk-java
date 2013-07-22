package org.trnltk.doc.simpleparsing;

import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParserBuilder;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.List;

public class SimpleParsing {

    public static void main(String[] args) {
        // create a morphologic parser with simplest suffix graph and numeral suffix graph, roots from bundled dictionary
        MorphologicParser parser = ContextlessMorphologicParserBuilder.createSimple();

        // parse surface
        List<MorphemeContainer> morphemeContainers = parser.parseStr("eti");

        // print results
        for (MorphemeContainer morphemeContainer : morphemeContainers) {
            // printing format is the simplest one : no suffix form applications, no grouping
            System.out.println(MorphemeContainerFormatter.formatMorphemeContainer(morphemeContainer));
        }
    }

}