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

package org.trnltk.morphology.ambiguity;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.trnltk.model.ambiguity.morphology.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//DOCME
public class ParseResultDiffTool {

    public static final Ordering<String> byLengthOrdering = new Ordering<String>() {
        public int compare(String left, String right) {
            return Ints.compare(left.length(), right.length());
        }
    };

    public static final Ordering<String> parseResultOrdering = Ordering.compound(Arrays.asList(byLengthOrdering, Ordering.<String>natural()));

    public ParseResultDifference findDifference(ParseResult _parseResultA, ParseResult _parseResultB) {
        final ParseResult firstParseResult;
        final ParseResult secondParseResult;

        if (parseResultOrdering.compare(_parseResultA.getStr(), _parseResultB.getStr()) <= 0) {
            firstParseResult = _parseResultA;
            secondParseResult = _parseResultB;
        } else {
            firstParseResult = _parseResultB;
            secondParseResult = _parseResultA;
        }

        final RootDifference rootDifference = buildRootDifference(firstParseResult, secondParseResult);

        final DataDiffUtil<ParseResultPart> diffUtil = new DataDiffUtil<ParseResultPart>();

        final List<ParseResultPart> firstParseResultParts = CollectionUtils.isEmpty(firstParseResult.getParts()) ? ListUtils.EMPTY_LIST : firstParseResult.getParts();
        final List<ParseResultPart> secondParseResultParts = CollectionUtils.isEmpty(secondParseResult.getParts()) ? ListUtils.EMPTY_LIST : secondParseResult.getParts();

        final LinkedList<DataDiffUtil.Diff<ParseResultPart>> diffs = diffUtil.diff_main(firstParseResultParts, secondParseResultParts);
        diffUtil.diff_cleanupSemantic(diffs);

        final ParseResultDifference parseResultDifference = new ParseResultDifference(rootDifference);

        final int diffsLength = diffs.size();
        int i = 0;
        while (i < diffsLength) {
            final DataDiffUtil.Diff<ParseResultPart> currentDiff = diffs.get(i);

            final DataDiffUtil.Diff<ParseResultPart> nextDiff = i + 1 >= diffsLength ? null : diffs.get(i + 1);

            //note: delete always comes before insert
            switch (currentDiff.operation) {
                case EQUAL: {
                    i++;
                    break;
                }
                case INSERT: {
                    final Pair<List<ParseResultPart>, List<ParseResultPart>> pair = Pair.of(null, currentDiff.text);
                    final ParseResultPartDifference partDifference = new ParseResultPartDifference(pair);
                    parseResultDifference.addParseResultPartDifference(partDifference);
                    i++;
                    break;
                }
                case DELETE: {
                    //
                    List<ParseResultPart> secondPart;
                    if (nextDiff != null && nextDiff.operation.equals(DataDiffUtil.Operation.INSERT))
                        secondPart = nextDiff.text;
                    else
                        secondPart = null;

                    final Pair<List<ParseResultPart>, List<ParseResultPart>> pair = Pair.of(currentDiff.text, secondPart);
                    final ParseResultPartDifference partDifference = new ParseResultPartDifference(pair);
                    parseResultDifference.addParseResultPartDifference(partDifference);
                    i = i + 2;
                    break;
                }
            }
        }

        return parseResultDifference;
    }

    private RootDifference buildRootDifference(ParseResult firstParseResult, ParseResult secondParseResult) {
        final Pair<String, String> rootStrPair = Pair.of(firstParseResult.getRoot(), secondParseResult.getRoot());
        final Pair<String, String> lemmaRootStrPair = Pair.of(firstParseResult.getLemmaRoot(), secondParseResult.getLemmaRoot());
        final Pair<String, String> posPair = Pair.of(firstParseResult.getRootPos(), secondParseResult.getRootPos());
        final Pair<String, String> sposPair = Pair.of(firstParseResult.getRootSpos(), secondParseResult.getRootSpos());

        final RootDifference rootDifference = new RootDifference(rootStrPair, lemmaRootStrPair, posPair, sposPair);

        if (rootDifference.differs())
            return rootDifference;
        else
            return null;
    }

}
