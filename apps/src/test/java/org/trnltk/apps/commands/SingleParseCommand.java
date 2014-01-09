package org.trnltk.apps.commands;

import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;

import java.util.List;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class SingleParseCommand implements Runnable {
    private final MorphologicParser parser;
    private final String word;
    private final int wordIndex;
    private boolean printUnparseable;

    public SingleParseCommand(final MorphologicParser parser, final String word, final int wordIndex, boolean printUnparseable) {
        this.parser = parser;
        this.word = word;
        this.wordIndex = wordIndex;
        this.printUnparseable = printUnparseable;
    }

    @Override
    public void run() {
        final List<MorphemeContainer> morphemeContainers = parser.parseStr(word);
        if (printUnparseable) {
            if (morphemeContainers.isEmpty())
                System.out.println("Word is not parsable " + word);
        }
        if (wordIndex % 500 == 0)
            System.out.println("Finished " + wordIndex);
    }
}
