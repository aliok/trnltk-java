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

package org.trnltk.model.ambiguity.morphology;

import com.google.common.base.Joiner;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParseResultDifference {
    private final RootDifference rootDifference;
    private final List<ParseResultPartDifference> parseResultPartDifferences = new ArrayList<ParseResultPartDifference>();

    public ParseResultDifference(RootDifference rootDifference) {
        this.rootDifference = rootDifference;
    }

    public RootDifference getRootDifference() {
        return rootDifference;
    }

    public List<ParseResultPartDifference> getParseResultPartDifferences() {
        return Collections.unmodifiableList(parseResultPartDifferences);
    }

    public boolean hasNoRootDifference() {
        return this.rootDifference == null;
    }

    public boolean hasRootDifference() {
        return !this.hasNoPartDifference();
    }

    public boolean hasNoPartDifference() {
        return CollectionUtils.isEmpty(this.parseResultPartDifferences);
    }

    public boolean hasPartDifference() {
        return !this.hasNoPartDifference();
    }

    public void addParseResultPartDifference(ParseResultPartDifference partDifference) {
        this.parseResultPartDifferences.add(partDifference);
    }

    @Override
    public String toString() {
        return "ParseResultDifference{" +
                "\n\trootDifference=" + rootDifference +
                ", \n\tparseResultPartDifferences=\n\t\t" + Joiner.on("\n\t").join(parseResultPartDifferences) +
                "\n}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResultDifference that = (ParseResultDifference) o;

        if (!parseResultPartDifferences.equals(that.parseResultPartDifferences))
            return false;
        if (rootDifference != null ? !rootDifference.equals(that.rootDifference) : that.rootDifference != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootDifference != null ? rootDifference.hashCode() : 0;
        result = 31 * result + (parseResultPartDifferences.hashCode());
        return result;
    }
}
