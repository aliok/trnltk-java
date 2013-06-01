package org.trnltk.morphology.ambiguity;

/*
 * Original Work:
 * Copyright 2006 Google Inc.
 * http://code.google.com/p/google-diff-match-patch/
 *
 * Derivative Work:
 * Ali Ok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;

import java.util.*;
import java.util.regex.Pattern;

/*
 * Functions for diff, match and patch.
 * Computes the difference between two texts to create a patch.
 * Applies the patch onto another text, allowing for errors.
 *
 * @author fraser@google.com (Neil Fraser)
 */

/**
 * <b>Modified to support objects, instead of just chars/strings.</b>
 * <p/>
 * Class containing the diff, match and patch methods.
 * Also contains the behaviour settings.
 */
public class DataDiffUtil<T> {

    // Defaults.
    // Set these on your diff_match_patch instance to override the defaults.

    /**
     * Number of seconds to map a diff before giving up (0 for infinity).
     */
    public float Diff_Timeout = 1.0f;
    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    public short Diff_EditCost = 4;


    /**
     * The data structure representing a diff is a Linked list of Diff objects:
     * {Diff(Operation.DELETE, "Hello"), Diff(Operation.INSERT, "Goodbye"),
     * Diff(Operation.EQUAL, " world.")}
     * which means: delete "Hello", add "Goodbye" and keep " world."
     */
    public enum Operation {
        DELETE, INSERT, EQUAL
    }

    /**
     * Find the differences between two texts.
     * Run a faster, slightly less optimal diff.
     * This method allows the 'checklines' of diff_main() to be optional.
     * Most of the time checklines is wanted, so default to true.
     *
     * @param list1 Old string to be diffed.
     * @param list2 New string to be diffed.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff<T>> diff_main(List<T> list1, List<T> list2) {
        return diff_main(list1, list2, true);
    }

    /**
     * Find the differences between two texts.
     *
     * @param list1      Old string to be diffed.
     * @param list2      New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *                   line-level diff first to identify the changed areas.
     *                   If true, then run a faster slightly less optimal diff.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff<T>> diff_main(List<T> list1, List<T> list2,
                                         boolean checklines) {
        // Set a deadline by which time the diff must be complete.
        long deadline;
        if (Diff_Timeout <= 0) {
            deadline = Long.MAX_VALUE;
        } else {
            deadline = System.currentTimeMillis() + (long) (Diff_Timeout * 1000);
        }
        return diff_main(list1, list2, checklines, deadline);
    }

    /**
     * Find the differences between two texts.  Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     *
     * @param list1      Old string to be diffed.
     * @param list2      New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *                   line-level diff first to identify the changed areas.
     *                   If true, then run a faster slightly less optimal diff.
     * @param deadline   Time when the diff should be complete by.  Used
     *                   internally for recursive calls.  Users should set DiffTimeout instead.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff<T>> diff_main(List<T> list1, List<T> list2,
                                          boolean checklines, long deadline) {
        // Check for null inputs.
        if (list1 == null || list2 == null) {
            throw new IllegalArgumentException("Null inputs. (diff_main)");
        }

        // Check for equality (speedup).
        LinkedList<Diff<T>> diffs;
        if (list1.equals(list2)) {
            diffs = new LinkedList<Diff<T>>();
            if (list1.size() != 0) {
                diffs.add(new Diff<T>(Operation.EQUAL, list1));
            }
            return diffs;
        }

        // Trim off common prefix (speedup).
        int commonlength = diff_commonPrefix(list1, list2);
        List<T> commonprefix = list1.subList(0, commonlength);
        list1 = list1.subList(commonlength, list1.size());
        list2 = list2.subList(commonlength, list2.size());

        // Trim off common suffix (speedup).
        commonlength = diff_commonSuffix(list1, list2);
        List<T> commonsuffix = list1.subList(list1.size() - commonlength, list1.size());
        list1 = list1.subList(0, list1.size() - commonlength);
        list2 = list2.subList(0, list2.size() - commonlength);

        // Compute the diff on the middle block.
        diffs = diff_compute(list1, list2, checklines, deadline);

        // Restore the prefix and suffix.
        if (commonprefix.size() != 0) {
            diffs.addFirst(new Diff<T>(Operation.EQUAL, commonprefix));
        }
        if (commonsuffix.size() != 0) {
            diffs.addLast(new Diff<T>(Operation.EQUAL, commonsuffix));
        }

        diff_cleanupMerge(diffs);
        return diffs;
    }

    /**
     * Find the differences between two texts.  Assumes that the texts do not
     * have any common prefix or suffix.
     *
     * @param list1      Old string to be diffed.
     * @param list2      New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *                   line-level diff first to identify the changed areas.
     *                   If true, then run a faster slightly less optimal diff.
     * @param deadline   Time when the diff should be complete by.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff<T>> diff_compute(List<T> list1, List<T> list2,
                                             boolean checklines, long deadline) {
        LinkedList<Diff<T>> diffs = new LinkedList<Diff<T>>();

        if (list1.size() == 0) {
            // Just add some text (speedup).
            diffs.add(new Diff(Operation.INSERT, list2));
            return diffs;
        }

        if (list2.size() == 0) {
            // Just delete some text (speedup).
            diffs.add(new Diff(Operation.DELETE, list1));
            return diffs;
        }

        List<T> longtext = list1.size() > list2.size() ? list1 : list2;
        List<T> shorttext = list1.size() > list2.size() ? list2 : list1;
        int i = longtext.indexOf(shorttext);        //TODO
        if (i != -1) {
            // Shorter text is inside the longer text (speedup).
            Operation op = (list1.size() > list2.size()) ?
                    Operation.DELETE : Operation.INSERT;
            diffs.add(new Diff(op, longtext.subList(0, i)));
            diffs.add(new Diff(Operation.EQUAL, shorttext));
            diffs.add(new Diff(op, longtext.subList(i + shorttext.size(), longtext.size())));
            return diffs;
        }

        if (shorttext.size() == 1) {
            // Single character string.
            // After the previous speedup, the character can't be an equality.
            diffs.add(new Diff(Operation.DELETE, list1));
            diffs.add(new Diff(Operation.INSERT, list2));
            return diffs;
        }

        // Check to see if the problem can be split in two.
        List<List<T>> hm = diff_halfMatch(list1, list2);
        if (hm != null) {
            // A half-match was found, sort out the return data.
            List<T> text1_a = hm.get(0);
            List<T> text1_b = hm.get(1);
            List<T> text2_a = hm.get(2);
            List<T> text2_b = hm.get(3);
            List<T> mid_common = hm.get(4);
            // Send both pairs off for separate processing.
            LinkedList<Diff<T>> diffs_a = diff_main(text1_a, text2_a,
                    checklines, deadline);
            LinkedList<Diff<T>> diffs_b = diff_main(text1_b, text2_b,
                    checklines, deadline);
            // Merge the results.
            diffs = diffs_a;
            diffs.add(new Diff<T>(Operation.EQUAL, mid_common));
            diffs.addAll(diffs_b);
            return diffs;
        }

        return diff_bisect(list1, list2, deadline);
    }

    /**
     * Find the 'middle snake' of a diff, split the problem in two
     * and return the recursively constructed diff.
     * See Myers 1986 paper: An O(ND) Difference Algorithm and Its Variations.
     *
     * @param list1    Old string to be diffed.
     * @param list2    New string to be diffed.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    protected LinkedList<Diff<T>> diff_bisect(List<T> list1, List<T> list2,
                                              long deadline) {
        // Cache the text lengths to prevent multiple calls.
        int text1_length = list1.size();
        int text2_length = list2.size();
        int max_d = (text1_length + text2_length + 1) / 2;
        int v_offset = max_d;
        int v_length = 2 * max_d;
        int[] v1 = new int[v_length];
        int[] v2 = new int[v_length];
        for (int x = 0; x < v_length; x++) {
            v1[x] = -1;
            v2[x] = -1;
        }
        v1[v_offset + 1] = 0;
        v2[v_offset + 1] = 0;
        int delta = text1_length - text2_length;
        // If the total number of characters is odd, then the front path will
        // collide with the reverse path.
        boolean front = (delta % 2 != 0);
        // Offsets for start and end of k loop.
        // Prevents mapping of space beyond the grid.
        int k1start = 0;
        int k1end = 0;
        int k2start = 0;
        int k2end = 0;
        for (int d = 0; d < max_d; d++) {
            // Bail out if deadline is reached.
            if (System.currentTimeMillis() > deadline) {
                break;
            }

            // Walk the front path one step.
            for (int k1 = -d + k1start; k1 <= d - k1end; k1 += 2) {
                int k1_offset = v_offset + k1;
                int x1;
                if (k1 == -d || (k1 != d && v1[k1_offset - 1] < v1[k1_offset + 1])) {
                    x1 = v1[k1_offset + 1];
                } else {
                    x1 = v1[k1_offset - 1] + 1;
                }
                int y1 = x1 - k1;
                while (x1 < text1_length && y1 < text2_length
                        && list1.get(x1).equals(list2.get(y1))) {
                    x1++;
                    y1++;
                }
                v1[k1_offset] = x1;
                if (x1 > text1_length) {
                    // Ran off the right of the graph.
                    k1end += 2;
                } else if (y1 > text2_length) {
                    // Ran off the bottom of the graph.
                    k1start += 2;
                } else if (front) {
                    int k2_offset = v_offset + delta - k1;
                    if (k2_offset >= 0 && k2_offset < v_length && v2[k2_offset] != -1) {
                        // Mirror x2 onto top-left coordinate system.
                        int x2 = text1_length - v2[k2_offset];
                        if (x1 >= x2) {
                            // Overlap detected.
                            return diff_bisectSplit(list1, list2, x1, y1, deadline);
                        }
                    }
                }
            }

            // Walk the reverse path one step.
            for (int k2 = -d + k2start; k2 <= d - k2end; k2 += 2) {
                int k2_offset = v_offset + k2;
                int x2;
                if (k2 == -d || (k2 != d && v2[k2_offset - 1] < v2[k2_offset + 1])) {
                    x2 = v2[k2_offset + 1];
                } else {
                    x2 = v2[k2_offset - 1] + 1;
                }
                int y2 = x2 - k2;
                while (x2 < text1_length && y2 < text2_length
                        && list1.get(text1_length - x2 - 1).equals(list2.get(text2_length - y2 - 1))) {
                    x2++;
                    y2++;
                }
                v2[k2_offset] = x2;
                if (x2 > text1_length) {
                    // Ran off the left of the graph.
                    k2end += 2;
                } else if (y2 > text2_length) {
                    // Ran off the top of the graph.
                    k2start += 2;
                } else if (!front) {
                    int k1_offset = v_offset + delta - k2;
                    if (k1_offset >= 0 && k1_offset < v_length && v1[k1_offset] != -1) {
                        int x1 = v1[k1_offset];
                        int y1 = v_offset + x1 - k1_offset;
                        // Mirror x2 onto top-left coordinate system.
                        x2 = text1_length - x2;
                        if (x1 >= x2) {
                            // Overlap detected.
                            return diff_bisectSplit(list1, list2, x1, y1, deadline);
                        }
                    }
                }
            }
        }
        // Diff took too long and hit the deadline or
        // number of diffs equals number of characters, no commonality at all.
        LinkedList<Diff<T>> diffs = new LinkedList<Diff<T>>();
        diffs.add(new Diff(Operation.DELETE, list1));
        diffs.add(new Diff(Operation.INSERT, list2));
        return diffs;
    }

    /**
     * Given the location of the 'middle snake', split the diff in two parts
     * and recurse.
     *
     * @param text1    Old string to be diffed.
     * @param text2    New string to be diffed.
     * @param x        Index of split point in text1.
     * @param y        Index of split point in text2.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    private LinkedList<Diff<T>> diff_bisectSplit(List<T> text1, List<T> text2,
                                                 int x, int y, long deadline) {
        List<T> text1a = text1.subList(0, x);
        List<T> text2a = text2.subList(0, y);
        List<T> text1b = text1.subList(x, text1.size());
        List<T> text2b = text2.subList(y, text2.size());

        // Compute both diffs serially.
        LinkedList<Diff<T>> diffs = diff_main(text1a, text2a, false, deadline);
        LinkedList<Diff<T>> diffsb = diff_main(text1b, text2b, false, deadline);

        diffs.addAll(diffsb);
        return diffs;
    }


    /**
     * Determine the common prefix of two strings
     *
     * @param list1 First string.
     * @param list2 Second string.
     * @return The number of characters common to the start of each string.
     */
    public int diff_commonPrefix(List<T> list1, List<T> list2) {
        // Performance analysis: http://neil.fraser.name/news/2007/10/09/
        int n = Math.min(list1.size(), list2.size());
        for (int i = 0; i < n; i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return i;
            }
        }
        return n;
    }

    /**
     * Determine the common suffix of two strings
     *
     * @param list1 First string.
     * @param list2 Second string.
     * @return The number of characters common to the end of each string.
     */
    public int diff_commonSuffix(List<T> list1, List<T> list2) {
        // Performance analysis: http://neil.fraser.name/news/2007/10/09/
        int text1_length = list1.size();
        int text2_length = list2.size();
        int n = Math.min(text1_length, text2_length);
        for (int i = 1; i <= n; i++) {
            if (!list1.get(text1_length - i).equals(list2.get(text2_length - i))) {
                return i - 1;
            }
        }
        return n;
    }

    /**
     * Determine if the suffix of one string is the prefix of another.
     *
     * @param list1 First string.
     * @param list2 Second string.
     * @return The number of characters common to the end of the first
     *         string and the start of the second string.
     */
    protected int diff_commonOverlap(List<T> list1, List<T> list2) {
        // Cache the text lengths to prevent multiple calls.
        int text1_length = list1.size();
        int text2_length = list2.size();
        // Eliminate the null case.
        if (text1_length == 0 || text2_length == 0) {
            return 0;
        }
        // Truncate the longer string.
        if (text1_length > text2_length) {
            list1 = list1.subList(text1_length - text2_length, list1.size());
        } else if (text1_length < text2_length) {
            list2 = list2.subList(0, text1_length);
        }
        int text_length = Math.min(text1_length, text2_length);
        // Quick check for the worst case.
        if (list1.equals(list2)) {
            return text_length;
        }

        // Start by looking for a single character match
        // and increase length until no match is found.
        // Performance analysis: http://neil.fraser.name/news/2010/11/04/
        int best = 0;
        int length = 1;
        while (true) {
            List<T> pattern = list1.subList(text_length - length, list1.size());
            int found = list2.indexOf(pattern);     //TODO
            if (found == -1) {
                return best;
            }
            length += found;
            if (found == 0 || list1.subList(text_length - length, list1.size()).equals(list2.subList(0, length))) {
                best = length;
                length++;
            }
        }
    }

    /**
     * Do the two texts share a substring which is at least half the length of
     * the longer text?
     * This speedup can produce non-minimal diffs.
     *
     * @param list1 First string.
     * @param list2 Second string.
     * @return Five element String array, containing the prefix of list1, the
     *         suffix of list1, the prefix of list2, the suffix of list2 and the
     *         common middle.  Or null if there was no match.
     */
    protected List<List<T>> diff_halfMatch(List<T> list1, List<T> list2) {
        if (Diff_Timeout <= 0) {
            // Don't risk returning a non-optimal diff if we have unlimited time.
            return null;
        }
        List<T> longtext = list1.size() > list2.size() ? list1 : list2;
        List<T> shorttext = list1.size() > list2.size() ? list2 : list1;
        if (longtext.size() < 4 || shorttext.size() * 2 < longtext.size()) {
            return null;  // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        List<T>[] hm1 = diff_halfMatchI(longtext, shorttext,
                (longtext.size() + 3) / 4);
        // Check again based on the third quarter.
        List<T>[] hm2 = diff_halfMatchI(longtext, shorttext,
                (longtext.size() + 1) / 2);
        List<T>[] hm;
        if (hm1 == null && hm2 == null) {
            return null;
        } else if (hm2 == null) {
            hm = hm1;
        } else if (hm1 == null) {
            hm = hm2;
        } else {
            // Both matched.  Select the longest.
            //TODO
            //hm = hm1[4].length() > hm2[4].length() ? hm1 : hm2;
            hm = hm1;
        }

        // A half-match was found, sort out the return data.
        if (list1.size() > list2.size()) {
            return Lists.newArrayList(hm);
            //return new String[]{hm[0], hm[1], hm[2], hm[3], hm[4]};
        } else {
            return Lists.newArrayList(hm[2], hm[3], hm[0], hm[1], hm[4]);
        }
    }

    /**
     * Does a substring of shorttext exist within longtext such that the
     * substring is at least half the length of longtext?
     *
     * @param longtext  Longer string.
     * @param shorttext Shorter string.
     * @param i         Start index of quarter length substring within longtext.
     * @return Five element String array, containing the prefix of longtext, the
     *         suffix of longtext, the prefix of shorttext, the suffix of shorttext
     *         and the common middle.  Or null if there was no match.
     */
    private List<T>[] diff_halfMatchI(List<T> longtext, List<T> shorttext, int i) {
        // Start with a 1/4 length substring at position i as a seed.
        List<T> seed = longtext.subList(i, i + longtext.size() / 4);
        int j = -1;
        List<T> best_common = new ArrayList<T>();
        List<T> best_longtext_a = new ArrayList<T>(), best_longtext_b = new ArrayList<T>();
        List<T> best_shorttext_a = new ArrayList<T>(), best_shorttext_b = new ArrayList<T>();
        while ((j = indexOf(shorttext, seed, j + 1)) != -1) {
            int prefixLength = diff_commonPrefix(longtext.subList(i, longtext.size()), shorttext.subList(j, shorttext.size()));
            int suffixLength = diff_commonSuffix(longtext.subList(0, i), shorttext.subList(0, j));
            if (best_common.size() < suffixLength + prefixLength) {
                best_common = ListUtils.union(shorttext.subList(j - suffixLength, j), shorttext.subList(j, j + prefixLength));
                best_longtext_a = longtext.subList(0, i - suffixLength);
                best_longtext_b = longtext.subList(i + prefixLength, longtext.size());
                best_shorttext_a = shorttext.subList(0, j - suffixLength);
                best_shorttext_b = shorttext.subList(j + prefixLength, shorttext.size());
            }
        }
        if (best_common.size() * 2 >= longtext.size()) {
            return new List[]{best_longtext_a, best_longtext_b,
                    best_shorttext_a, best_shorttext_b, best_common};
        } else {
            return null;
        }
    }

    /**
     * Reduce the number of edits by eliminating semantically trivial equalities.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupSemantic(LinkedList<Diff<T>> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        Stack<Diff<T>> equalities = new Stack<Diff<T>>();  // Stack of qualities.
        List<T> lastequality = null; // Always equal to equalities.lastElement().text
        ListIterator<Diff<T>> pointer = diffs.listIterator();
        // Number of characters that changed prior to the equality.
        int length_insertions1 = 0;
        int length_deletions1 = 0;
        // Number of characters that changed after the equality.
        int length_insertions2 = 0;
        int length_deletions2 = 0;
        Diff<T> thisDiff = pointer.next();
        while (thisDiff != null) {
            if (thisDiff.operation == Operation.EQUAL) {
                // Equality found.
                equalities.push(thisDiff);
                length_insertions1 = length_insertions2;
                length_deletions1 = length_deletions2;
                length_insertions2 = 0;
                length_deletions2 = 0;
                lastequality = thisDiff.text;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == Operation.INSERT) {
                    length_insertions2 += thisDiff.text.size();
                } else {
                    length_deletions2 += thisDiff.text.size();
                }
                // Eliminate an equality that is smaller or equal to the edits on both
                // sides of it.
                if (lastequality != null && (lastequality.size()
                        <= Math.max(length_insertions1, length_deletions1))
                        && (lastequality.size()
                        <= Math.max(length_insertions2, length_deletions2))) {
                    //System.out.println("Splitting: '" + lastequality + "'");
                    // Walk back to offending equality.
                    while (thisDiff != equalities.lastElement()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(Operation.DELETE, lastequality));
                    // Insert a corresponding an insert.
                    pointer.add(new Diff(Operation.INSERT, lastequality));

                    equalities.pop();  // Throw away the equality we just deleted.
                    if (!equalities.empty()) {
                        // Throw away the previous equality (it needs to be reevaluated).
                        equalities.pop();
                    }
                    if (equalities.empty()) {
                        // There are no previous equalities, walk back to the start.
                        while (pointer.hasPrevious()) {
                            pointer.previous();
                        }
                    } else {
                        // There is a safe equality we can fall back to.
                        thisDiff = equalities.lastElement();
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                    }

                    length_insertions1 = 0;  // Reset the counters.
                    length_insertions2 = 0;
                    length_deletions1 = 0;
                    length_deletions2 = 0;
                    lastequality = null;
                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        // Normalize the diff.
        if (changes) {
            diff_cleanupMerge(diffs);
        }
        diff_cleanupSemanticLossless(diffs);

        // Find any overlaps between deletions and insertions.
        // e.g: <del>abcxxx</del><ins>xxxdef</ins>
        //   -> <del>abc</del>xxx<ins>def</ins>
        // e.g: <del>xxxabc</del><ins>defxxx</ins>
        //   -> <ins>def</ins>xxx<del>abc</del>
        // Only extract an overlap if it is as big as the edit ahead or behind it.
        pointer = diffs.listIterator();
        Diff<T> prevDiff = null;
        thisDiff = null;
        if (pointer.hasNext()) {
            prevDiff = pointer.next();
            if (pointer.hasNext()) {
                thisDiff = pointer.next();
            }
        }
        while (thisDiff != null) {
            if (prevDiff.operation == Operation.DELETE &&
                    thisDiff.operation == Operation.INSERT) {
                List<T> deletion = prevDiff.text;
                List<T> insertion = thisDiff.text;
                int overlap_length1 = this.diff_commonOverlap(deletion, insertion);
                int overlap_length2 = this.diff_commonOverlap(insertion, deletion);
                if (overlap_length1 >= overlap_length2) {
                    if (overlap_length1 >= deletion.size() / 2.0 ||
                            overlap_length1 >= insertion.size() / 2.0) {
                        // Overlap found. Insert an equality and trim the surrounding edits.
                        pointer.previous();
                        pointer.add(new Diff<T>(Operation.EQUAL, insertion.subList(0, overlap_length1)));
                        prevDiff.text = deletion.subList(0, deletion.size() - overlap_length1);
                        thisDiff.text = insertion.subList(overlap_length1, insertion.size());
                        // pointer.add inserts the element before the cursor, so there is
                        // no need to step past the new element.
                    }
                } else {
                    if (overlap_length2 >= deletion.size() / 2.0 ||
                            overlap_length2 >= insertion.size() / 2.0) {
                        // Reverse overlap found.
                        // Insert an equality and swap and trim the surrounding edits.
                        pointer.previous();
                        pointer.add(new Diff<T>(Operation.EQUAL,
                                deletion.subList(0, overlap_length2)));
                        prevDiff.operation = Operation.INSERT;
                        prevDiff.text =
                                insertion.subList(0, insertion.size() - overlap_length2);
                        thisDiff.operation = Operation.DELETE;
                        thisDiff.text = deletion.subList(overlap_length2, deletion.size());
                        // pointer.add inserts the element before the cursor, so there is
                        // no need to step past the new element.
                    }
                }
                thisDiff = pointer.hasNext() ? pointer.next() : null;
            }
            prevDiff = thisDiff;
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Look for single edits surrounded on both sides by equalities
     * which can be shifted sideways to align the edit to a word boundary.
     * e.g: The c<ins>at c</ins>ame. -> The <ins>cat </ins>came.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupSemanticLossless(LinkedList<Diff<T>> diffs) {
        List<T> equality1, edit, equality2;
        List<T> commonString;
        int commonOffset;
        int score, bestScore;
        List<T> bestEquality1, bestEdit, bestEquality2;
        // Create a new iterator at the start.
        ListIterator<Diff<T>> pointer = diffs.listIterator();
        Diff<T> prevDiff = pointer.hasNext() ? pointer.next() : null;
        Diff<T> thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff<T> nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need checking).
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL &&
                    nextDiff.operation == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                equality1 = prevDiff.text;
                edit = thisDiff.text;
                equality2 = nextDiff.text;

                // First, shift the edit as far left as possible.
                commonOffset = diff_commonSuffix(equality1, edit);
                if (commonOffset != 0) {
                    commonString = edit.subList(edit.size() - commonOffset, edit.size());
                    equality1 = equality1.subList(0, equality1.size() - commonOffset);
                    edit = ListUtils.union(commonString, edit.subList(0, edit.size() - commonOffset));
                    equality2 = ListUtils.union(commonString, equality2);
                }

                // Second, step character by character right, looking for the best fit.
                bestEquality1 = equality1;
                bestEdit = edit;
                bestEquality2 = equality2;
                bestScore = diff_cleanupSemanticScore(equality1, edit)
                        + diff_cleanupSemanticScore(edit, equality2);
                while (edit.size() != 0 && equality2.size() != 0
                        && edit.get(0).equals(equality2.get(0))) {
                    equality1 = ListUtils.union(equality1, Arrays.asList(edit.get(0)));
                    edit = ListUtils.union(edit.subList(1, edit.size()), Arrays.asList(equality2.get(0)));
                    equality2 = equality2.subList(1, equality2.size());
                    score = diff_cleanupSemanticScore(equality1, edit)
                            + diff_cleanupSemanticScore(edit, equality2);
                    // The >= encourages trailing rather than leading whitespace on edits.
                    if (score >= bestScore) {
                        bestScore = score;
                        bestEquality1 = equality1;
                        bestEdit = edit;
                        bestEquality2 = equality2;
                    }
                }

                if (!prevDiff.text.equals(bestEquality1)) {
                    // We have an improvement, save it back to the diff.
                    if (bestEquality1.size() != 0) {
                        prevDiff.text = bestEquality1;
                    } else {
                        pointer.previous(); // Walk past nextDiff.
                        pointer.previous(); // Walk past thisDiff.
                        pointer.previous(); // Walk past prevDiff.
                        pointer.remove(); // Delete prevDiff.
                        pointer.next(); // Walk past thisDiff.
                        pointer.next(); // Walk past nextDiff.
                    }
                    thisDiff.text = bestEdit;
                    if (bestEquality2.size() != 0) {
                        nextDiff.text = bestEquality2;
                    } else {
                        pointer.remove(); // Delete nextDiff.
                        nextDiff = thisDiff;
                        thisDiff = prevDiff;
                    }
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Given two strings, compute a score representing whether the internal
     * boundary falls on logical boundaries.
     * Scores range from 6 (best) to 0 (worst).
     *
     * @param one First string.
     * @param two Second string.
     * @return The score.
     */
    private int diff_cleanupSemanticScore(List<T> one, List<T> two) {
        /*if (one.size() == 0 || two.size() == 0) {
            // Edges are the best.
            return 6;
        }

        // Each port of this function behaves slightly differently due to
        // subtle differences in each language's definition of things like
        // 'whitespace'.  Since this function's purpose is largely cosmetic,
        // the choice has been made to use each language's native features
        // rather than force total conformity.
        T char1 = one.get(one.size() - 1);
        T char2 = two.get(0);
        boolean nonAlphaNumeric1 = !Character.isLetterOrDigit(char1);
        boolean nonAlphaNumeric2 = !Character.isLetterOrDigit(char2);
        boolean whitespace1 = nonAlphaNumeric1 && Character.isWhitespace(char1);
        boolean whitespace2 = nonAlphaNumeric2 && Character.isWhitespace(char2);
        boolean lineBreak1 = whitespace1 && Character.getType(char1) == Character.CONTROL;
        boolean lineBreak2 = whitespace2 && Character.getType(char2) == Character.CONTROL;
        boolean blankLine1 = lineBreak1 && BLANKLINEEND.matcher(one).find();
        boolean blankLine2 = lineBreak2 && BLANKLINESTART.matcher(two).find();

        if (blankLine1 || blankLine2) {
            // Five points for blank lines.
            return 5;
        } else if (lineBreak1 || lineBreak2) {
            // Four points for line breaks.
            return 4;
        } else if (nonAlphaNumeric1 && !whitespace1 && whitespace2) {
            // Three points for end of sentences.
            return 3;
        } else if (whitespace1 || whitespace2) {
            // Two points for whitespace.
            return 2;
        } else if (nonAlphaNumeric1 || nonAlphaNumeric2) {
            // One point for non-alphanumeric.
            return 1;
        }
        return 0;*/
        return 3;
    }

    // Define some regex patterns for matching boundaries.
    private Pattern BLANKLINEEND
            = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
    private Pattern BLANKLINESTART
            = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

    /**
     * Reduce the number of edits by eliminating operationally trivial equalities.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupEfficiency(LinkedList<Diff<T>> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        Stack<Diff> equalities = new Stack<Diff>();  // Stack of equalities.
        List<T> lastequality = null; // Always equal to equalities.lastElement().text
        ListIterator<Diff<T>> pointer = diffs.listIterator();
        // Is there an insertion operation before the last equality.
        boolean pre_ins = false;
        // Is there a deletion operation before the last equality.
        boolean pre_del = false;
        // Is there an insertion operation after the last equality.
        boolean post_ins = false;
        // Is there a deletion operation after the last equality.
        boolean post_del = false;
        Diff<T> thisDiff = pointer.next();
        Diff<T> safeDiff = thisDiff;  // The last Diff that is known to be unsplitable.
        while (thisDiff != null) {
            if (thisDiff.operation == Operation.EQUAL) {
                // Equality found.
                if (thisDiff.text.size() < Diff_EditCost && (post_ins || post_del)) {
                    // Candidate found.
                    equalities.push(thisDiff);
                    pre_ins = post_ins;
                    pre_del = post_del;
                    lastequality = thisDiff.text;
                } else {
                    // Not a candidate, and can never become one.
                    equalities.clear();
                    lastequality = null;
                    safeDiff = thisDiff;
                }
                post_ins = post_del = false;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == Operation.DELETE) {
                    post_del = true;
                } else {
                    post_ins = true;
                }
        /*
         * Five types to be split:
         * <ins>A</ins><del>B</del>XY<ins>C</ins><del>D</del>
         * <ins>A</ins>X<ins>C</ins><del>D</del>
         * <ins>A</ins><del>B</del>X<ins>C</ins>
         * <ins>A</del>X<ins>C</ins><del>D</del>
         * <ins>A</ins><del>B</del>X<del>C</del>
         */
                if (lastequality != null
                        && ((pre_ins && pre_del && post_ins && post_del)
                        || ((lastequality.size() < Diff_EditCost / 2)
                        && ((pre_ins ? 1 : 0) + (pre_del ? 1 : 0)
                        + (post_ins ? 1 : 0) + (post_del ? 1 : 0)) == 3))) {
                    //System.out.println("Splitting: '" + lastequality + "'");
                    // Walk back to offending equality.
                    while (thisDiff != equalities.lastElement()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(Operation.DELETE, lastequality));
                    // Insert a corresponding an insert.
                    pointer.add(thisDiff = new Diff(Operation.INSERT, lastequality));

                    equalities.pop();  // Throw away the equality we just deleted.
                    lastequality = null;
                    if (pre_ins && pre_del) {
                        // No changes made which could affect previous entry, keep going.
                        post_ins = post_del = true;
                        equalities.clear();
                        safeDiff = thisDiff;
                    } else {
                        if (!equalities.empty()) {
                            // Throw away the previous equality (it needs to be reevaluated).
                            equalities.pop();
                        }
                        if (equalities.empty()) {
                            // There are no previous questionable equalities,
                            // walk back to the last known safe diff.
                            thisDiff = safeDiff;
                        } else {
                            // There is an equality we can fall back to.
                            thisDiff = equalities.lastElement();
                        }
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                        post_ins = post_del = false;
                    }

                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        if (changes) {
            diff_cleanupMerge(diffs);
        }
    }

    /**
     * Reorder and merge like edit sections.  Merge equalities.
     * Any edit section can move as long as it doesn't cross an equality.
     *
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupMerge(LinkedList<Diff<T>> diffs) {
        diffs.add(new Diff<T>(Operation.EQUAL, new ArrayList<T>()));  // Add a dummy entry at the end.
        ListIterator<Diff<T>> pointer = diffs.listIterator();
        int count_delete = 0;
        int count_insert = 0;
        List<T> text_delete = new ArrayList<T>();
        List<T> text_insert = new ArrayList<T>();
        Diff thisDiff = pointer.next();
        Diff prevEqual = null;
        int commonlength;
        while (thisDiff != null) {
            switch (thisDiff.operation) {
                case INSERT:
                    count_insert++;
                    text_insert = ListUtils.union(text_insert, thisDiff.text);
                    prevEqual = null;
                    break;
                case DELETE:
                    count_delete++;
                    text_delete = ListUtils.union(text_delete, thisDiff.text);
                    prevEqual = null;
                    break;
                case EQUAL:
                    if (count_delete + count_insert > 1) {
                        boolean both_types = count_delete != 0 && count_insert != 0;
                        // Delete the offending records.
                        pointer.previous();  // Reverse direction.
                        while (count_delete-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }
                        while (count_insert-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }
                        if (both_types) {
                            // Factor out any common prefixies.
                            commonlength = diff_commonPrefix(text_insert, text_delete);
                            if (commonlength != 0) {
                                if (pointer.hasPrevious()) {
                                    thisDiff = pointer.previous();
                                    assert thisDiff.operation == Operation.EQUAL
                                            : "Previous diff should have been an equality.";
                                    thisDiff.text = ListUtils.union(thisDiff.text, text_insert.subList(0, commonlength));
                                    pointer.next();
                                } else {
                                    pointer.add(new Diff(Operation.EQUAL,
                                            text_insert.subList(0, commonlength)));
                                }
                                text_insert = text_insert.subList(commonlength, text_insert.size());
                                text_delete = text_delete.subList(commonlength, text_delete.size());
                            }
                            // Factor out any common suffixies.
                            commonlength = diff_commonSuffix(text_insert, text_delete);
                            if (commonlength != 0) {
                                thisDiff = pointer.next();
                                thisDiff.text = ListUtils.union(text_insert.subList(text_insert.size() - commonlength, text_insert.size()), thisDiff.text);
                                text_insert = text_insert.subList(0, text_insert.size() - commonlength);
                                text_delete = text_delete.subList(0, text_delete.size() - commonlength);
                                pointer.previous();
                            }
                        }
                        // Insert the merged records.
                        if (text_delete.size() != 0) {
                            pointer.add(new Diff(Operation.DELETE, text_delete));
                        }
                        if (text_insert.size() != 0) {
                            pointer.add(new Diff(Operation.INSERT, text_insert));
                        }
                        // Step forward to the equality.
                        thisDiff = pointer.hasNext() ? pointer.next() : null;
                    } else if (prevEqual != null) {
                        // Merge this equality with the previous one.
                        prevEqual.text = ListUtils.union(prevEqual.text, thisDiff.text);
                        pointer.remove();
                        thisDiff = pointer.previous();
                        pointer.next();  // Forward direction
                    }
                    count_insert = 0;
                    count_delete = 0;
                    text_delete = new ArrayList<T>();
                    text_insert = new ArrayList<T>();
                    prevEqual = thisDiff;
                    break;
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        if (diffs.getLast().text.size() == 0) {
            diffs.removeLast();  // Remove the dummy entry at the end.
        }

    /*
     * Second pass: look for single edits surrounded on both sides by equalities
     * which can be shifted sideways to eliminate an equality.
     * e.g: A<ins>BA</ins>C -> <ins>AB</ins>AC
     */
        boolean changes = false;
        // Create a new iterator at the start.
        // (As opposed to walking the current one back.)
        pointer = diffs.listIterator();
        Diff<T> prevDiff = pointer.hasNext() ? pointer.next() : null;
        thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need checking).
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL &&
                    nextDiff.operation == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                if (endsWith(thisDiff.text, prevDiff.text)) {
                    // Shift the edit over the previous equality.
                    thisDiff.text = ListUtils.union(prevDiff.text, thisDiff.text.subList(0, thisDiff.text.size() - prevDiff.text.size()));
                    nextDiff.text = ListUtils.union(prevDiff.text, nextDiff.text);
                    pointer.previous(); // Walk past nextDiff.
                    pointer.previous(); // Walk past thisDiff.
                    pointer.previous(); // Walk past prevDiff.
                    pointer.remove(); // Delete prevDiff.
                    pointer.next(); // Walk past thisDiff.
                    thisDiff = pointer.next(); // Walk past nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                } else if (startsWith(thisDiff.text, nextDiff.text)) {
                    // Shift the edit over the next equality.
                    prevDiff.text = ListUtils.union(prevDiff.text, nextDiff.text);
                    thisDiff.text = ListUtils.union(thisDiff.text.subList(nextDiff.text.size(), thisDiff.text.size()), nextDiff.text);
                    pointer.remove(); // Delete nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
        // If shifts were made, the diff needs reordering and another shift sweep.
        if (changes) {
            diff_cleanupMerge(diffs);
        }
    }

    /**
     * Class representing one diff operation.
     */
    public static class Diff<P> {
        /**
         * One of: INSERT, DELETE or EQUAL.
         */
        public Operation operation;
        /**
         * The text associated with this diff operation.
         */
        public List<P> text;

        /**
         * Constructor.  Initializes the diff with the provided values.
         *
         * @param operation One of INSERT, DELETE or EQUAL.
         * @param text      The text being applied.
         */
        public Diff(Operation operation, List<P> text) {
            // Construct a diff with the specified operation and text.
            this.operation = operation;
            this.text = text;
        }

        /**
         * Display a human-readable version of this Diff.
         *
         * @return text version.
         */
        public String toString() {
            //String prettyText = this.text.replace('\n', '\u00b6');
            String prettyText = this.text.toString();
            return "Diff(" + this.operation + ",\"" + prettyText + "\")";
        }

        /**
         * Create a numeric hash value for a Diff.
         * This function is not used by DMP.
         *
         * @return Hash value.
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = (operation == null) ? 0 : operation.hashCode();
            result += prime * ((text == null) ? 0 : text.hashCode());
            return result;
        }

        /**
         * Is this Diff equivalent to another Diff?
         *
         * @param obj Another Diff to compare against.
         * @return true or false.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Diff other = (Diff) obj;
            if (operation != other.operation) {
                return false;
            }
            if (text == null) {
                if (other.text != null) {
                    return false;
                }
            } else if (!text.equals(other.text)) {
                return false;
            }
            return true;
        }
    }

    private static <S> boolean startsWith(List<S> source, List<S> prefix) {
        return startsWith(source, prefix, 0);
    }

    private static <S> boolean startsWith(List<S> source, List<S> prefix, int toffset) {
        // bad copy from java.lang.String.startsWith(String, int)

        List<S> ta = source;
        int to = toffset;
        List<S> pa = prefix;
        int po = 0;
        int pc = prefix.size();
        // Note: toffset might be near -1>>>1.
        if ((toffset < 0) || (toffset > source.size() - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (ta.get(to++) != pa.get(po++)) {
                return false;
            }
        }
        return true;
    }

    public static <S> boolean endsWith(List<S> source, List<S> prefix) {
        return endsWith(source, prefix, 0);
    }

    public static <S> boolean endsWith(List<S> source, List<S> prefix, int toffset) {
        List<S> ta = source;
        int to = toffset;
        List<S> pa = prefix;
        int po = 0;
        int pc = prefix.size();
        // Note: toffset might be near -1>>>1.
        if ((toffset < 0) || (toffset > source.size() - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (ta.get(to++) != pa.get(po++)) {
                return false;
            }
        }
        return true;
    }

    public static <S> int indexOf(List<S> source, List<S> target) {
        return indexOf(source, target, 0);
    }

    public static <S> int indexOf(List<S> source, List<S> target, int fromIndex) {
        return indexOf(source, 0, source.size(), target, 0, target.size(), fromIndex);
    }

    public static <S> int indexOf(List<S> source, int sourceOffset, int sourceCount,
                                  List<S> target, int targetOffset, int targetCount,
                                  int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        S first = target.get(targetOffset);
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
                /* Look for first character. */
            if (!source.get(i).equals(first)) {
                while (++i <= max && !source.get(i).equals(first)) ;
            }

                /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source.get(j).equals(target.get(k)); j++, k++)
                    ;

                if (j == end) {
                        /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }
}