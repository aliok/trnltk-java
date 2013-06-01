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

package org.trnltk.morphology.contextless.parser.suffixbased;

import com.google.common.collect.HashMultimap;
import org.junit.Before;
import org.trnltk.morphology.contextless.parser.parsing.BaseContextlessMorphologicParserCopulaSuffixGraphTest;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;

import java.util.List;

public class ContextlessMorphologicParserCopulaSuffixGraphTest extends BaseContextlessMorphologicParserCopulaSuffixGraphTest {

    private HashMultimap<String, ? extends Root> originalRootMap;
    private ContextlessMorphologicParser parser;

    public ContextlessMorphologicParserCopulaSuffixGraphTest() {
        this.originalRootMap = RootMapFactory.createSimpleConvertCircumflexes();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return HashMultimap.create(this.originalRootMap);
    }

    @Override
    protected void buildParser(HashMultimap<String, Root> clonedRootMap) {
        this.parser = ContextlessMorphologicParserFactory.createWithBigGraphForRootMap(clonedRootMap);
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

}
