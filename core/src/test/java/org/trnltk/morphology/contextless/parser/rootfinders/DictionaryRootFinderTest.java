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

package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.ImmutableMultimap;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@RunWith(MockitoJUnitRunner.class)
public class DictionaryRootFinderTest {

    DictionaryRootFinder finder;
    PhoneticsAnalyzer phoneticsAnalyzer;

    @Mock
    Root root1_1;
    @Mock
    Root root1_2;
    @Mock
    Root root2_1;
    @Mock
    Root root2_2;

    @Before
    public void setUp() throws Exception {
        final ImmutableMultimap<String, Root> map = new ImmutableMultimap.Builder<String, Root>()
                .putAll("root1", Arrays.asList(root1_1, root1_2))
                .putAll("root2", Arrays.asList(root2_1, root2_2))
                .build();

        finder = new DictionaryRootFinder(map);
        phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void shouldFindRoots() {
        final String rootStr = "root1";
        final Collection<? extends Root> roots = finder.findRootsForPartialInput(new TurkishSequence(rootStr), null);
        assertThat(roots, hasSize(2));
        assertThat(roots, (Matcher) hasItem(root1_1));
        assertThat(roots, (Matcher) hasItem(root1_2));
    }

    @Test
    public void shouldNotFindRoots() {
        final String rootStr = "UNKNOWN";
        final Collection<? extends Root> roots = finder.findRootsForPartialInput(new TurkishSequence(rootStr), null);
        assertThat(roots, hasSize(0));
    }
}
