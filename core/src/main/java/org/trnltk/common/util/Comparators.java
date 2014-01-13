package org.trnltk.common.util;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import java.util.Arrays;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class Comparators {

    public static final Ordering<String> byLengthOrdering = new Ordering<String>() {
        public int compare(String left, String right) {
            return Ints.compare(left.length(), right.length());
        }
    };

    public static final Ordering<String> parseResultOrdering = Ordering.compound(Arrays.asList(byLengthOrdering, Ordering.<String>natural()));

}
