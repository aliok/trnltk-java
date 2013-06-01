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

package org.trnltk.morphology.ambiguity.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ParseResultPartDifference {
    private final Pair<List<ParseResultPart>, List<ParseResultPart>> parts;

    public ParseResultPartDifference(Pair<List<ParseResultPart>, List<ParseResultPart>> parts) {
        this.parts = parts;
    }

    public Pair<List<ParseResultPart>, List<ParseResultPart>> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "ParseResultPartDifference{\n\t\t\t" +
                "parts=\n\t\t\t\t" +
                parts.getLeft() + "\n\t\t\t\t" +
                parts.getRight() +
                "\n\t\t}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResultPartDifference that = (ParseResultPartDifference) o;

        if (parts != null ? !parts.equals(that.parts) : that.parts != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return parts != null ? parts.hashCode() : 0;
    }
}
