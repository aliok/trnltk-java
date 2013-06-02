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

package org.trnltk.testutil.testmatchers;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.hamcrest.Description;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParseResultsEqualMatcher extends BaseParseResultsMatcher {

    private final List<String> expectedParseResults;
    private final boolean ignoreVerbPresA3Sg;

    public ParseResultsEqualMatcher(boolean ignoreVerbPresA3Sg, final String... expectedParseResults) {
        this.ignoreVerbPresA3Sg = ignoreVerbPresA3Sg;
        this.expectedParseResults = Arrays.asList(expectedParseResults);
        Validate.notNull(expectedParseResults);
    }

    @Override
    public boolean matchesSafely(Collection<String> item) {
        if (ignoreVerbPresA3Sg)      // filter out some verb results to make the test have less results
            item = Collections2.filter(item, Predicates.not(Predicates.containsPattern("\\Zero\\+Pres\\+")));
        return CollectionUtils.isEqualCollection(expectedParseResults, item);
    }

    @Override
    public void describeTo(Description description) {
        Collections.sort(this.expectedParseResults, BaseParseResultsMatcher.parseResultOrdering);
        description.appendValueList("    <", ",", ">", this.expectedParseResults);
    }

    @Override
    protected void describeMismatchSafely(Collection<String> item, Description mismatchDescription) {
        List<String> itemList = Lists.newArrayList(item);
        Collections.sort(itemList, BaseParseResultsMatcher.parseResultOrdering);
        mismatchDescription.appendValueList("was <", ",", ">", itemList);
    }
}