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

package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import org.trnltk.model.suffix.SuffixTransition;
import org.trnltk.common.specification.AbstractSpecification;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;

public class LastSuffixGoesToStateWithType extends AbstractSpecification<MorphemeContainer> {
    private final SuffixGraphStateType suffixGraphStateType;

    public LastSuffixGoesToStateWithType(SuffixGraphStateType suffixGraphStateType) {
        this.suffixGraphStateType = suffixGraphStateType;
    }

    @Override
    public String describe() {
        return String.format("suffix_goes_to_state_type(%s)", this.suffixGraphStateType);
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        SuffixTransition lastSuffixTransition = morphemeContainer.getLastSuffixTransition();
        if (lastSuffixTransition == null)
            return false;

        return lastSuffixTransition.getTargetState().getType().equals(this.suffixGraphStateType);
    }
}
