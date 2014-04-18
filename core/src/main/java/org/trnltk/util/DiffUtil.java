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

package org.trnltk.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import name.fraser.neil.plaintext.diff_match_patch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ali Ok
 *         not the most efficient algorithm to create formatted lines (method diffToFormattedLines), but... never mind...
 */
public class DiffUtil {

    public static String[] diffLines(final String line1, final String line2, final boolean ignoreWhiteSpace) {
        final diff_match_patch dmp = new diff_match_patch();

        final LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(line1, line2);

        if (CollectionUtils.isEmpty(diffs)) {
            return null;
        } else {
            if (ignoreWhiteSpace) {
                final LinkedList<diff_match_patch.Diff> filteredDiffs = Lists.newLinkedList(Iterables.filter(diffs, new Predicate<diff_match_patch.Diff>() {
                    @Override
                    public boolean apply(diff_match_patch.Diff input) {
                        if (input.operation.equals(diff_match_patch.Operation.EQUAL))
                            return false;
                        else if (StringUtils.isBlank(input.text))
                            return false;

                        return true;
                    }
                }));

                if (filteredDiffs.isEmpty())
                    return null;
            }

            dmp.diff_cleanupSemantic(diffs);

            final String[] diffLines = diffToFormattedLines(diffs, ignoreWhiteSpace);
            if (ignoreWhiteSpace) {
                if (StringUtils.isBlank(diffLines[1]) && StringUtils.isBlank(diffLines[2]))
                    return null;
            }

            return diffLines;
        }
    }

    private static String[] diffToFormattedLines(final List<diff_match_patch.Diff> diffs, boolean ignoreWhiteSpace) {
        final StringBuilder modifiedSourceLineBuilder = new StringBuilder();
        final StringBuilder markerLineBuilder = new StringBuilder();
        final StringBuilder differenceLineBuilder = new StringBuilder();
        for (int i = 0; i < diffs.size(); i++) {
            final diff_match_patch.Diff currentDiff = diffs.get(i);

            if (ignoreWhiteSpace && StringUtils.isBlank(currentDiff.text))
                continue;

            final diff_match_patch.Diff previousDiff = getPreviousNotEqualNotBlankDiff(diffs, i);
            final diff_match_patch.Diff nextDiff = getNextNotEqualNotBlankDiff(diffs, i);
            switch (currentDiff.operation) {
                case INSERT: {
                    if (previousDiff != null && previousDiff.operation.equals(diff_match_patch.Operation.DELETE)) {
                        continue;
                    }
                    modifiedSourceLineBuilder.append(StringUtils.repeat(' ', currentDiff.text.length()));
                    markerLineBuilder.append(StringUtils.repeat('+', currentDiff.text.length()));
                    differenceLineBuilder.append(currentDiff.text);
                    break;
                }
                case DELETE: {
                    if (nextDiff != null && nextDiff.operation.equals(diff_match_patch.Operation.INSERT)) {
                        // consider as a replace, except trimmed str's are same. then we should act upon param ignoreWhiteSpace
                        final int lengthDifference = currentDiff.text.length() - nextDiff.text.length();
                        if (currentDiff.text.trim().equals(nextDiff.text.trim()) && ignoreWhiteSpace) {
                            modifiedSourceLineBuilder.append(currentDiff.text);
                            markerLineBuilder.append(StringUtils.repeat(' ', currentDiff.text.length()));
                            differenceLineBuilder.append(StringUtils.repeat(' ', currentDiff.text.length()));
                        } else {
                            if (lengthDifference < 0) {
                                modifiedSourceLineBuilder.append(currentDiff.text);
                                modifiedSourceLineBuilder.append(StringUtils.repeat(' ', -1 * lengthDifference));
                                markerLineBuilder.append(StringUtils.repeat('^', nextDiff.text.length()));
                                differenceLineBuilder.append(nextDiff.text);
                            } else {
                                modifiedSourceLineBuilder.append(currentDiff.text);
                                markerLineBuilder.append(StringUtils.repeat('^', currentDiff.text.length()));
                                differenceLineBuilder.append(nextDiff.text);
                                differenceLineBuilder.append(StringUtils.repeat(' ', lengthDifference));
                            }
                        }

                    } else {
                        modifiedSourceLineBuilder.append(currentDiff.text);
                        markerLineBuilder.append(StringUtils.repeat('-', currentDiff.text.length()));
                        differenceLineBuilder.append(StringUtils.repeat(' ', currentDiff.text.length()));
                    }

                    break;
                }
                case EQUAL: {
                    modifiedSourceLineBuilder.append(currentDiff.text);
                    markerLineBuilder.append(StringUtils.repeat(' ', currentDiff.text.length()));
                    differenceLineBuilder.append(StringUtils.repeat(' ', currentDiff.text.length()));
                    break;
                }
            }
        }
        return new String[]{modifiedSourceLineBuilder.toString(), markerLineBuilder.toString(), differenceLineBuilder.toString()};
    }

    private static diff_match_patch.Diff getPreviousNotEqualNotBlankDiff(List<diff_match_patch.Diff> diffs, int i) {
        for (int index = i - 1; index >= 0; index--) {
            final diff_match_patch.Diff diff = diffs.get(index);
            if (diff.operation.equals(diff_match_patch.Operation.EQUAL))
                //noinspection UnnecessaryContinue
                continue;
            else if (StringUtils.isBlank(diff.text))
                //noinspection UnnecessaryContinue
                continue;
            else
                return diff;
        }
        return null;
    }

    private static diff_match_patch.Diff getNextNotEqualNotBlankDiff(List<diff_match_patch.Diff> diffs, int i) {
        for (int index = i + 1; index < diffs.size(); index++) {
            final diff_match_patch.Diff diff = diffs.get(index);
            if (diff.operation.equals(diff_match_patch.Operation.EQUAL))
                //noinspection UnnecessaryContinue
                continue;
            else if (StringUtils.isBlank(diff.text))
                //noinspection UnnecessaryContinue
                continue;
            else
                return diff;
        }
        return null;
    }

}
