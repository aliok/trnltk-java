package org.trnltk.morphology.ambiguity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DataDiffUtilTest {

    @Test
    public void shouldComputeDiff_whenLastItemIsDifferent() {
        final ArrayList<Character> listA = Lists.newArrayList(ArrayUtils.toObject("abcd".toCharArray()));
        final ArrayList<Character> listB = Lists.newArrayList(ArrayUtils.toObject("abce".toCharArray()));

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Character>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[a, b, c]\"), Diff(DELETE,\"[d]\"), Diff(INSERT,\"[e]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenLastItemIsExtra() {
        final ArrayList<Integer> listA = Lists.newArrayList(1, 2, 3);
        final ArrayList<Integer> listB = Lists.newArrayList(1, 2, 3, 4);

        final DataDiffUtil<Integer> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Integer>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[1, 2, 3]\"), Diff(INSERT,\"[4]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenLastItemIsMissing() {
        final ArrayList<String> listA = Lists.newArrayList("AA", "BB", "CC");
        final ArrayList<String> listB = Lists.newArrayList("AA", "BB");

        final DataDiffUtil<String> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<String>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[AA, BB]\"), Diff(DELETE,\"[CC]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenFirstItemIsDifferent() {
        final ArrayList<Double> listA = Lists.newArrayList(1.1, 2.2, 3.3);
        final ArrayList<Double> listB = Lists.newArrayList(0.0, 2.2, 3.3);

        final DataDiffUtil<Double> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Double>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(DELETE,\"[1.1]\"), Diff(INSERT,\"[0.0]\"), Diff(EQUAL,\"[2.2, 3.3]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenFirstItemIsExtra() {
        final ArrayList<Double> listA = Lists.newArrayList(1.1, 2.2, 3.3);
        final ArrayList<Double> listB = Lists.newArrayList(2.2, 3.3);

        final DataDiffUtil<Double> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Double>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(DELETE,\"[1.1]\"), Diff(EQUAL,\"[2.2, 3.3]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenFirstItemIsMissing() {
        final ArrayList<Double> listA = Lists.newArrayList(2.2, 3.3);
        final ArrayList<Double> listB = Lists.newArrayList(1.1, 2.2, 3.3);

        final DataDiffUtil<Double> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Double>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(INSERT,\"[1.1]\"), Diff(EQUAL,\"[2.2, 3.3]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenMiddleItemIsDifferent() {
        final ArrayList<Character> listA = Lists.newArrayList('a', 'b', 'c');
        final ArrayList<Character> listB = Lists.newArrayList('a', 'x', 'c');

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Character>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[a]\"), Diff(DELETE,\"[b]\"), Diff(INSERT,\"[x]\"), Diff(EQUAL,\"[c]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenMiddleItemIsExtra() {
        final ArrayList<Character> listA = Lists.newArrayList('a', 'b', 'c');
        final ArrayList<Character> listB = Lists.newArrayList('a', 'c');

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Character>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[a]\"), Diff(DELETE,\"[b]\"), Diff(EQUAL,\"[c]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenMiddleItemIsMissing() {
        final ArrayList<Character> listA = Lists.newArrayList('a', 'c');
        final ArrayList<Character> listB = Lists.newArrayList('a', 'x', 'c');

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Character>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[a]\"), Diff(INSERT,\"[x]\"), Diff(EQUAL,\"[c]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenLastItemsAreDifferent() {
        final ArrayList<Character> listA = Lists.newArrayList('a', 'b', 'c', 'd');
        final ArrayList<Character> listB = Lists.newArrayList('a', 'b', 'x', 'y');

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Character>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[a, b]\"), Diff(DELETE,\"[c, d]\"), Diff(INSERT,\"[x, y]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenLastItemsAreExtra() {
        final ArrayList<Character> listA = Lists.newArrayList('a', 'b', 'c', 'd');
        final ArrayList<Character> listB = Lists.newArrayList('a', 'b');

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Character>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[a, b]\"), Diff(DELETE,\"[c, d]\")]"));
    }

    @Test
    public void shouldComputeDiff_whenLastItemsAreMissing() {
        final ArrayList<Character> listA = Lists.newArrayList('a', 'b');
        final ArrayList<Character> listB = Lists.newArrayList('a', 'b', 'x', 'y');

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        final LinkedList<DataDiffUtil.Diff<Character>> diffs = util.diff_main(listA, listB);

        assertThat(diffs.toString(), equalTo("[Diff(EQUAL,\"[a, b]\"), Diff(INSERT,\"[x, y]\")]"));
    }

    @Ignore
    @Test
    public void shouldComputeDiff_withCleanup() {
        final ArrayList<Character> listA = Lists.newArrayList(ArrayUtils.toObject("Does a substring of shorttext exisareithin longtext such that the".toCharArray()));
        final ArrayList<Character> listB = Lists.newArrayList(ArrayUtils.toObject("Does a substring of something exist within gogogogo such that the".toCharArray()));

        final DataDiffUtil<Character> util = new DataDiffUtil<>();
        LinkedList<DataDiffUtil.Diff<Character>> diffs;

        diffs = util.diff_main(listA, listB);
        printNonEqual(diffs);

        diffs = util.diff_main(listA, listB);
        util.diff_cleanupSemantic(diffs);
        printNonEqual(diffs);

        diffs = util.diff_main(listA, listB);
        util.diff_cleanupSemanticLossless(diffs);
        printNonEqual(diffs);

        diffs = util.diff_main(listA, listB);
        util.diff_cleanupEfficiency(diffs);
        printNonEqual(diffs);

        diffs = util.diff_main(listA, listB);
        util.diff_cleanupMerge(diffs);
        printNonEqual(diffs);
    }

    private void printNonEqual(LinkedList<DataDiffUtil.Diff<Character>> diffs) {
        final Iterable<DataDiffUtil.Diff<Character>> filtered = Iterables.filter(diffs, new Predicate<DataDiffUtil.Diff<Character>>() {
            @Override
            public boolean apply(DataDiffUtil.Diff<Character> input) {
                return !input.operation.equals(DataDiffUtil.Operation.EQUAL);
            }
        });

        System.out.println(filtered);
    }

}
