package org.trnltk.web.criticalsurface;

import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class ParseResultWithSentencesContainer implements Serializable {
    private final String parseResultStr;

    // list of Pair<Sentence, SurfaceIndexInSentence>
    // Sentence : list of surfaces in a sentence
    // SurfaceIndexInSentence : index of the surface in the sentence. that means index of the surface that the parse result belongs to
    private final List<Pair<SentenceContainer, Integer>> sentencesAndIndices;

    public ParseResultWithSentencesContainer(String parseResultStr, List<Pair<SentenceContainer, Integer>> sentencesAndIndices) {
        this.parseResultStr = parseResultStr;
        this.sentencesAndIndices = sentencesAndIndices;
    }

    public String getParseResultStr() {
        return parseResultStr;
    }

    public List<Pair<SentenceContainer, Integer>> getSentencesAndIndices() {
        return sentencesAndIndices;
    }

}
