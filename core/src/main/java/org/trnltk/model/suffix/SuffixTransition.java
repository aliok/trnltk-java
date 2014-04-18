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

/**
 * A transition in the suffix graph finite state machine with a {@link SuffixFormApplication}.
 */
public class SuffixTransition {
    private final SuffixGraphState sourceState;
    private final SuffixFormApplication suffixFormApplication;
    private final SuffixGraphState targetState;

    /**
     * Create a new <code>SuffixTransition</code>.
     *
     * @param sourceState           Source state in the FSM
     * @param suffixFormApplication Application of a {@link SuffixForm}, {@link SuffixFormApplication}
     * @param targetState           Target state in the FSM
     */
    public SuffixTransition(SuffixGraphState sourceState, SuffixFormApplication suffixFormApplication, SuffixGraphState targetState) {
        this.sourceState = sourceState;
        this.suffixFormApplication = suffixFormApplication;
        this.targetState = targetState;
    }

    /**
     * @return true if source state is derivational; false otherwise.
     */
    public boolean isDerivational() {
        return SuffixGraphStateType.DERIVATIONAL.equals(this.sourceState.getType());
    }

    /**
     * @return source state in the FSM
     */
    public SuffixGraphState getSourceState() {
        return sourceState;
    }

    /**
     * @return the application which transition makes
     */
    public SuffixFormApplication getSuffixFormApplication() {
        return suffixFormApplication;
    }

    /**
     * @return target state in the FSM
     */
    public SuffixGraphState getTargetState() {
        return targetState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixTransition that = (SuffixTransition) o;

        if (!sourceState.equals(that.sourceState)) return false;
        else if (!suffixFormApplication.equals(that.suffixFormApplication)) return false;
        else if (!targetState.equals(that.targetState)) return false;

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
