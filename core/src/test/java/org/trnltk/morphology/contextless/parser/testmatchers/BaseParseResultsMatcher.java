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

package org.trnltk.morphology.contextless.parser.testmatchers;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.Collection;

public abstract class BaseParseResultsMatcher extends TypeSafeMatcher<Collection<String>> {

    public static final Ordering<String> byLengthOrdering = new Ordering<String>() {
        public int compare(String left, String right) {
            return Ints.compare(left.length(), right.length());
        }
    };

    public static final Ordering<String> parseResultOrdering = Ordering.compound(Arrays.asList(byLengthOrdering, Ordering.<String>natural()));

}
