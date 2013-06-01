package org.trnltk.morphology.ambiguity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.trnltk.morphology.ambiguity.model.ParseResult;
import org.trnltk.morphology.ambiguity.model.ParseResultPart;
import org.trnltk.morphology.ambiguity.model.WordParseResultEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ParseResultReader {

    public List<WordParseResultEntry> getParseResultEntries(Reader reader) throws IOException, JSONException {

        final BufferedReader bufferedReader = new BufferedReader(reader);

        List<WordParseResultEntry> entries = new ArrayList<WordParseResultEntry>();
        WordParseResultEntry currentEntry = null;
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            if (line.startsWith("- word: ")) {
                final String word = line.substring("- word: ".length());
                currentEntry = new WordParseResultEntry(word);
                entries.add(currentEntry);
            } else if (line.startsWith("  results:")) {
                // ok, go
            } else if (line.startsWith("    - ")) {
                final String result = line.substring("    - ".length());
                final ParseResult objParseResult = this.createParseResultObject(result);
                currentEntry.addParseResult(objParseResult);
            }
        }

        bufferedReader.close();
        return entries;
    }

    public ParseResult createParseResultObject(String strResult) throws JSONException {
        final JSONObject jsonObject = new JSONObject(strResult);
        List<ParseResultPart> parts = null;
        if (jsonObject.has("Parts")) {
            final JSONArray jsonParts = jsonObject.getJSONArray("Parts");
            parts = new ArrayList<ParseResultPart>(jsonParts.length());
            for (int i = 0; i < jsonParts.length(); i++) {
                final JSONObject currentPart = jsonParts.getJSONObject(i);
                final String pos = currentPart.getString("POS");
                final String spos = currentPart.has("SPOS") ? currentPart.getString("SPOS") : null;

                final List<String> suffixes = new ArrayList<String>(2);       // in avg, we have 2 suffixes
                if (currentPart.has("Suffixes")) {
                    final JSONArray jsonSuffixes = currentPart.getJSONArray("Suffixes");

                    for (int j = 0; j < jsonSuffixes.length(); j++) {
                        final String suffix = jsonSuffixes.getString(j);
                        suffixes.add(suffix);
                    }
                }

                parts.add(new ParseResultPart(pos, spos, suffixes));
            }
        }

        final String root = jsonObject.getString("Root");
        final String lemmaRoot = jsonObject.getString("LemmaRoot");
        final String rootPos = jsonObject.getString("RootPos");
        final String rootSpos = jsonObject.has("RootSpos") ? jsonObject.getString("RootSpos") : null;

        return new ParseResult(strResult, root, lemmaRoot, rootPos, rootSpos, parts);
    }

}
