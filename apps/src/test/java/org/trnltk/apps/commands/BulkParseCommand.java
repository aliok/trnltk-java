package org.trnltk.apps.commands;

import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.List;
import java.util.Map;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class BulkParseCommand implements Runnable {
    private final MorphologicParser parser;
    private final List<String> subWordList;
    private final int wordIndex;
    private boolean printUnparseable;
    final Map<String, List<String>> resultMap;

    public BulkParseCommand(final MorphologicParser parser, final List<String> subWordList, final int wordIndex, boolean printUnparseable, Map<String, List<String>> resultMap) {
        this.parser = parser;
        this.subWordList = subWordList;
        this.wordIndex = wordIndex;
        this.printUnparseable = printUnparseable;
        this.resultMap = resultMap;
    }

    public BulkParseCommand(final MorphologicParser parser, final List<String> subWordList, final int wordIndex, boolean printUnparseable) {
        this(parser, subWordList, wordIndex, printUnparseable, null);
    }

    @Override
    public void run() {
        final List<List<MorphemeContainer>> results = parser.parseAllStr(subWordList);

        System.out.println("Finished " + wordIndex);

        if (resultMap != null) {
            for (int i = 0; i < results.size(); i++) {
                String surface = subWordList.get(i);
                List<MorphemeContainer> result = results.get(i);
                if (result.size() > 1)
                    resultMap.put(surface, MorphemeContainerFormatter.formatMorphemeContainers(result));
            }
        }

        if (printUnparseable) {
            for (int i = 0; i < results.size(); i++) {
                String surface = subWordList.get(i);
                List<MorphemeContainer> result = results.get(i);

                if (result.isEmpty())
                    System.out.println("Word is not parsable " + surface);
            }
        }

    }
}
