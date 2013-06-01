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

package org.trnltk.morphology.lexicon;

import com.google.common.collect.HashMultimap;
import org.trnltk.morphology.model.Root;

import java.util.Collection;

public class RootMapGenerator {

    public HashMultimap<String, ? extends Root> generate(Collection<? extends Root> allRoots) {
        final HashMultimap<String, Root> map = HashMultimap.create(allRoots.size(), 2);
        for (Root root : allRoots) {
            final String rootStr = root.getSequence().getUnderlyingString();
            map.put(rootStr, root);
        }

        return map;
    }
}
