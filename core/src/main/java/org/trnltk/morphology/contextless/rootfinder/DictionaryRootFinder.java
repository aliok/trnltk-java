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

package org.trnltk.morphology.contextless.rootfinder;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DictionaryRootFinder implements RootFinder {
    private final Multimap<String, ? extends Root> rootMap;

    public DictionaryRootFinder(Multimap<String, ? extends Root> rootMap) {
        Validate.notNull(rootMap);
        this.rootMap = rootMap;
    }

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence input) {
        return partialInput != null && !partialInput.isBlank();
    }

    @Override
    public Collection<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence _input) {
        final Collection<? extends Root> roots = this.rootMap.get(partialInput.getUnderlyingString());
        if (Character.isUpperCase(partialInput.charAt(0).getCharValue())) {
            final ArrayList<Root> result = new ArrayList<Root>();

            final Collection<? extends Root> lowerCaseRoots = this.rootMap.get(Character.toLowerCase((int)partialInput.getUnderlyingString().charAt(0)) + partialInput.getUnderlyingString().substring(1));
            result.addAll(roots);
            result.addAll(lowerCaseRoots);

            if (CollectionUtils.isEmpty(result))
                return Collections.EMPTY_LIST;
            else
                return result;

        } else {
            if (CollectionUtils.isEmpty(roots))
                return Collections.EMPTY_LIST;
            else
                return roots;
        }

    }
}
