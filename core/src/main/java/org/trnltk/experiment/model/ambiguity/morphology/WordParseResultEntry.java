/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.trnltk.experiment.model.ambiguity.morphology;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds a word (surface) and parse results for it.
 */
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
