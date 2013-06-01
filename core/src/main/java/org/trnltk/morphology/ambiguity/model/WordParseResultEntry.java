package org.trnltk.morphology.ambiguity.model;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordParseResultEntry {
    private final String word;
    private final List<ParseResult> parseResults = new ArrayList<ParseResult>();

    public WordParseResultEntry(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public List<ParseResult> getParseResults() {
        return Collections.unmodifiableList(parseResults);
    }

    public void addParseResult(ParseResult parseResult) throws JSONException {
        this.parseResults.add(parseResult);
    }

    @Override
    public String toString() {
        return "WordParseResultEntry{" +
                "word='" + word + '\'' +
                ", parseResults=" + parseResults +
                '}';
    }
}
