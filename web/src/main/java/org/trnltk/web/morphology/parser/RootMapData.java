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

package org.trnltk.web.morphology.parser;

import com.google.common.collect.Multimap;
import org.trnltk.morphology.lexicon.CircumflexConvertingRootGenerator;
import org.trnltk.morphology.lexicon.DictionaryLoader;
import org.trnltk.morphology.lexicon.ImmutableRootGenerator;
import org.trnltk.morphology.lexicon.RootMapGenerator;
import org.trnltk.morphology.model.ImmutableRoot;
import org.trnltk.morphology.model.Lexeme;
import org.trnltk.morphology.model.Root;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.Collection;
import java.util.HashSet;

@ManagedBean
@ApplicationScoped
public class RootMapData {

    private final Multimap<String, ? extends Root> rootMap;
    private final Multimap<String, ? extends Root> numeralRootMap;

    private final Multimap<String, ? extends Root> rootMapWithoutCircumflexes;
    private final Multimap<String, ? extends Root> numeralRootMapWithoutCircumflexes;

    public RootMapData() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        final HashSet<Lexeme> numeralLexemes = DictionaryLoader.loadDefaultNumeralMasterDictionary();

        final ImmutableRootGenerator immutableRootGenerator = new ImmutableRootGenerator();

        final Collection<ImmutableRoot> roots = immutableRootGenerator.generateAll(lexemes);
        final Collection<ImmutableRoot> numeralRoots = new ImmutableRootGenerator().generateAll(numeralLexemes);

        final CircumflexConvertingRootGenerator circumflexConvertingRootGenerator = new CircumflexConvertingRootGenerator();
        final Collection<ImmutableRoot> rootsWithoutCircumflexes = circumflexConvertingRootGenerator.generateAll(lexemes);
        final Collection<ImmutableRoot> numeralRootsWithoutCircumflexes = circumflexConvertingRootGenerator.generateAll(numeralLexemes);

        final RootMapGenerator rootMapGenerator = new RootMapGenerator();

        this.rootMap = rootMapGenerator.generate(roots);
        this.numeralRootMap = rootMapGenerator.generate(numeralRoots);

        this.rootMapWithoutCircumflexes = rootMapGenerator.generate(rootsWithoutCircumflexes);
        this.numeralRootMapWithoutCircumflexes = rootMapGenerator.generate(numeralRootsWithoutCircumflexes);
    }

    public Multimap<String, ? extends Root> getRootMap() {
        return rootMap;
    }

    public Multimap<String, ? extends Root> getNumeralRootMap() {
        return numeralRootMap;
    }

    public Multimap<String, ? extends Root> getRootMapWithConvertedCircumflexes() {
        return rootMapWithoutCircumflexes;
    }

    public Multimap<String, ? extends Root> getNumeralRootMapWithConvertedCircumflexes() {
        return numeralRootMapWithoutCircumflexes;
    }
}
