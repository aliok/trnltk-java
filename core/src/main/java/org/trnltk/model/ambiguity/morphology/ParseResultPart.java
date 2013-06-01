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

import java.util.List;

public class ParseResultPart {
    private final String primaryPos;
    private final String secondaryPos;
    private final List<String> suffixes;

    public ParseResultPart(String primaryPos, String secondaryPos, List<String> suffixes) {
        this.primaryPos = primaryPos;
        this.secondaryPos = secondaryPos;
        this.suffixes = suffixes;
    }

    public String getPrimaryPos() {
        return primaryPos;
    }

    public String getSecondaryPos() {
        return secondaryPos;
    }

    public List<String> getSuffixes() {
        return suffixes;
    }

    @Override
    public String toString() {
        return "ParseResultPart{" +
                "primaryPos='" + primaryPos + '\'' +
                ", secondaryPos='" + secondaryPos + '\'' +
                ", suffixes=" + suffixes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResultPart that = (ParseResultPart) o;

        if (primaryPos != null ? !primaryPos.equals(that.primaryPos) : that.primaryPos != null) return false;
        if (secondaryPos != null ? !secondaryPos.equals(that.secondaryPos) : that.secondaryPos != null) return false;
        if (suffixes != null ? !suffixes.equals(that.suffixes) : that.suffixes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = primaryPos != null ? primaryPos.hashCode() : 0;
        result = 31 * result + (secondaryPos != null ? secondaryPos.hashCode() : 0);
        result = 31 * result + (suffixes != null ? suffixes.hashCode() : 0);
        return result;
    }
}