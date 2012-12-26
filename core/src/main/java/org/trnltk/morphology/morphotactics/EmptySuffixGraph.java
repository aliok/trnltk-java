/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.morphology.morphotactics;

import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.Suffix;
import org.trnltk.morphology.model.SuffixForm;

import java.util.Collection;
import java.util.Collections;

public class EmptySuffixGraph implements SuffixGraph {
    @Override
    public SuffixGraphState getDefaultStateForRoot(Root root) {
        return null;
    }

    @Override
    public void initialize() {
        // do nothing
    }

    @Override
    public Suffix getSuffix(String name) {
        return null;
    }

    @Override
    public SuffixGraphState getSuffixGraphState(String stateName) {
        return null;
    }

    @Override
    public Collection<Suffix> getAllSuffixes() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public SuffixForm getSuffixForm(String suffixName, String suffixFormStr) {
        return null;
    }
}
