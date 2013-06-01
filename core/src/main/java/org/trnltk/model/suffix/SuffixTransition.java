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

import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;

public class SuffixTransition {
    private final SuffixGraphState sourceState;
    private final SuffixFormApplication suffixFormApplication;
    private final SuffixGraphState targetState;

    public SuffixTransition(SuffixGraphState sourceState, SuffixFormApplication suffixFormApplication, SuffixGraphState targetState) {
        this.sourceState = sourceState;
        this.suffixFormApplication = suffixFormApplication;
        this.targetState = targetState;
    }

    public boolean isDerivational() {
        return SuffixGraphStateType.DERIVATIONAL.equals(this.sourceState.getType());
    }

    public SuffixGraphState getSourceState() {
        return sourceState;
    }

    public SuffixFormApplication getSuffixFormApplication() {
        return suffixFormApplication;
    }

    public SuffixGraphState getTargetState() {
        return targetState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixTransition that = (SuffixTransition) o;

        if (!sourceState.equals(that.sourceState)) return false;
        if (!suffixFormApplication.equals(that.suffixFormApplication)) return false;
        if (!targetState.equals(that.targetState)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourceState.hashCode();
        result = 31 * result + suffixFormApplication.hashCode();
        result = 31 * result + targetState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SuffixTransition{" +
                "sourceState=" + sourceState +
                ", targetState=" + targetState +
                ", suffixFormApplication=" + suffixFormApplication +
                '}';
    }
}
