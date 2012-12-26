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

package org.trnltk.morphology.lexicon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.*;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class LexemeCreatorTest {

    LexemeCreator loader;

    @Before
    public void setUp() throws Exception {
        loader = new LexemeCreator();
    }

    @Test
    public void shouldInferMorphemicAttributesForVerbs() {
        LexemeAttribute PVD = LexemeAttribute.ProgressiveVowelDrop;
        LexemeAttribute PI = LexemeAttribute.Passive_In;
        LexemeAttribute AA = LexemeAttribute.Aorist_A;
        LexemeAttribute AI = LexemeAttribute.Aorist_I;
        LexemeAttribute VO = LexemeAttribute.Voicing;
        LexemeAttribute NVO = LexemeAttribute.NoVoicing;

        LexemeAttribute C_T = LexemeAttribute.Causative_t;
        LexemeAttribute C_IR = LexemeAttribute.Causative_Ir;
        LexemeAttribute C_IT = LexemeAttribute.Causative_It;
        LexemeAttribute C_AR = LexemeAttribute.Causative_Ar;
        LexemeAttribute C_DIR = LexemeAttribute.Causative_dIr;

        Set<LexemeAttribute> lexemeAttributes;

        lexemeAttributes = loader.inferMorphemicAttributes("git", SyntacticCategory.VERB, ImmutableSet.of(VO, C_DIR));
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(VO, C_DIR, AA)));

        lexemeAttributes = loader.inferMorphemicAttributes("gel", SyntacticCategory.VERB, ImmutableSet.of(AI, C_DIR));
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(AI, C_DIR, PI, NVO)));

        lexemeAttributes = loader.inferMorphemicAttributes("at", SyntacticCategory.VERB, ImmutableSet.of(NVO, C_DIR));
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(NVO, C_DIR, AA)));

        lexemeAttributes = loader.inferMorphemicAttributes("ata", SyntacticCategory.VERB, new HashSet<LexemeAttribute>());
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(PVD, PI, AI, C_T, NVO)));

        lexemeAttributes = loader.inferMorphemicAttributes("dola", SyntacticCategory.VERB, new HashSet<LexemeAttribute>());
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(PVD, PI, AI, C_T, NVO)));

        lexemeAttributes = loader.inferMorphemicAttributes("tanı", SyntacticCategory.VERB, ImmutableSet.of(AI));
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(AI, PVD, PI, AI, C_T, NVO)));

        lexemeAttributes = loader.inferMorphemicAttributes("getir", SyntacticCategory.VERB, ImmutableSet.of(AI));
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(AI, AI, C_T, NVO)));

        lexemeAttributes = loader.inferMorphemicAttributes("ürk", SyntacticCategory.VERB, ImmutableSet.of(C_IT));
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(C_IT, AA, NVO)));

        lexemeAttributes = loader.inferMorphemicAttributes("ağla", SyntacticCategory.VERB, new HashSet<LexemeAttribute>());
        assertThat(lexemeAttributes, Matchers.<Set<LexemeAttribute>>equalTo(ImmutableSet.of(PVD, PI, AI, C_T, NVO)));
    }
}
