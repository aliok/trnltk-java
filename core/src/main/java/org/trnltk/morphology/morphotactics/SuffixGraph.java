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

import org.trnltk.model.lexicon.Root;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.suffix.SuffixForm;

import java.util.Collection;

public interface SuffixGraph {

    SuffixGraphState getDefaultStateForRoot(Root root);

    Collection<SuffixGraphState> getRootSuffixGraphStates();

    void initialize();

    Suffix getSuffix(String name);

    SuffixGraphState getSuffixGraphState(String stateName);

    Collection<Suffix> getAllSuffixes();

    SuffixForm getSuffixForm(String suffixName, String suffixFormStr);
}
