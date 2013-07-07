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

package org.trnltk.model.suffix;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Groups multiple {@link Suffix}es.
 * <p/>
 * This is useful when parsing (e.g. 2 suffixes from the same group cannot exist in a inflection group) and also
 * in graphical representation (e.g. suffixes from the same group are drawn with the same color).
 */
public class SuffixGroup {
    private final String name;
    private final Set<Suffix> suffixes = new LinkedHashSet<Suffix>();

    public SuffixGroup(String name) {
        this.name = name;
    }

    /**
     * @return Unique name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * @return Suffixes that belong to this group
     */
    public Set<Suffix> getSuffixes() {
        return suffixes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixGroup that = (SuffixGroup) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "SuffixGroup{" +
                "name='" + name + '\'' +
                '}';
    }
}
