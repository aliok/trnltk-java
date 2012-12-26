/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.morphology.contextless.parser.testmatchers;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.hamcrest.Description;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ParseResultsDontExistMatcher extends BaseParseResultsMatcher {
    private final List<String> expectedParseResults;

    public ParseResultsDontExistMatcher(String... expectedParseResults) {
        Validate.notNull(expectedParseResults);
        this.expectedParseResults = Arrays.asList(expectedParseResults);
    }

    @Override
    public boolean matchesSafely(Collection<String> item) {
        return CollectionUtils.isNotEmpty(item) && !CollectionUtils.containsAny(item, expectedParseResults);
    }

    @Override
    public void describeTo(Description description) {
        Collections.sort(this.expectedParseResults, BaseParseResultsMatcher.parseResultOrdering);
        description.appendValueList("parse results not containing any of <", ",", ">", this.expectedParseResults);
    }

    @Override
    protected void describeMismatchSafely(Collection<String> item, Description mismatchDescription) {
        List<String> itemList = Lists.newArrayList(item);
        Collections.sort(itemList, BaseParseResultsMatcher.parseResultOrdering);
        mismatchDescription.appendValueList("was                                 <", ",", ">", itemList);
    }
}