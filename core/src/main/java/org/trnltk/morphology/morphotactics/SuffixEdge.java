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

package org.trnltk.morphology.morphotactics;

import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.suffixbased.Suffix;

public class SuffixEdge {
    private final Suffix suffix;
    private final SuffixGraphState targetState;

    public SuffixEdge(Suffix suffix, SuffixGraphState targetState) {
        Validate.notNull(suffix);
        Validate.notNull(targetState);

        this.suffix = suffix;
        this.targetState = targetState;
    }

    public Suffix getSuffix() {
        return suffix;
    }

    public SuffixGraphState getTargetState() {
        return targetState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixEdge that = (SuffixEdge) o;

        if (!suffix.equals(that.suffix)) return false;
        if (!targetState.equals(that.targetState)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = suffix.hashCode();
        result = 31 * result + targetState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SuffixEdge{" +
                "suffix=" + suffix +
                ", targetState=" + targetState +
                '}';
    }
}
