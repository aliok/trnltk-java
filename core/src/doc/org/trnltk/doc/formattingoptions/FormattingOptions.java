package org.trnltk.doc.formattingoptions;

import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.formbased.ContextlessMorphologicParserBuilder;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.List;

public class FormattingOptions {
    public static void main(String[] args) {
        final MorphologicParser parser = ContextlessMorphologicParserBuilder.createSimple();
        final List<MorphemeContainer> morphemeContainers = parser.parseStr("kitaba");

        // there should be only one, get it
        final MorphemeContainer result = morphemeContainers.get(0);

        // Oflazer format : kitap+Noun+A3sg+Pnon+Dat
        System.out.println(MorphemeContainerFormatter.formatMorphemeContainer(result));
        // TRNLTK detailed format : {"Parts":[{"POS":"Noun","Suffixes":["A3sg","Pnon","Dat"]}],"LemmaRoot":"kitap","RootPos":"Noun","Root":"kitab"}
        System.out.println(MorphemeContainerFormatter.formatMorphemeContainerDetailed(result));
        // Metu-Sabanci corpus format : (1,"kitap+Noun+A3sg+Pnon+Dat")
        System.out.println(MorphemeContainerFormatter.formatMorphemeContainerWithDerivationGrouping(result));
        // TRNLTK format : kitab(kitap)+Noun+A3sg+Pnon+Dat(+yA[a])
        System.out.println(MorphemeContainerFormatter.formatMorphemeContainerWithForms(result));

    }
}
