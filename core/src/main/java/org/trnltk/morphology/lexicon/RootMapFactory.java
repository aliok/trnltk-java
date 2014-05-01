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
import org.trnltk.model.lexicon.Lexeme;
import org.trnltk.model.lexicon.Root;

import java.util.Collection;
import java.util.HashSet;

@SuppressWarnings({"WeakerAccess", "UnusedDeclaration"})
public class RootMapFactory {
    public static HashMultimap<String, ? extends Root> createSimple() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        return buildWithLexemes(lexemes);
    }

    public static HashMultimap<String, ? extends Root> createSimpleConvertCircumflexes() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        return buildWithLexemesConvertCircumflexes(lexemes);
    }

    public static HashMultimap<String, ? extends Root> createSimpleConvertCircumflexes(boolean reducedAmbiguity) {
        if (!reducedAmbiguity) {
            return createSimpleConvertCircumflexes();
        } else {
            final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary(true);
            return buildWithLexemesConvertCircumflexes(lexemes);
        }
    }

    public static HashMultimap<String, ? extends Root> createNumbers() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultNumeralMasterDictionary();
        return buildWithLexemes(lexemes);
    }

    public static HashMultimap<String, ? extends Root> createSimpleWithNumbers() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        lexemes.addAll(DictionaryLoader.loadDefaultNumeralMasterDictionary());
        return buildWithLexemes(lexemes);
    }

    public static HashMultimap<String, ? extends Root> createSimpleWithNumbersConvertCircumflexes() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        lexemes.addAll(DictionaryLoader.loadDefaultNumeralMasterDictionary());
        return buildWithLexemesConvertCircumflexes(lexemes);
    }

    public static HashMultimap<String, ? extends Root> buildWithLexemes(HashSet<Lexeme> lexemes) {
        final ImmutableRootGenerator immutableRootGenerator = new ImmutableRootGenerator();
        Collection<? extends Root> roots = immutableRootGenerator.generateAll(lexemes);

        return new RootMapGenerator().generate(roots);
    }

    public static HashMultimap<String, ? extends Root> buildWithLexemesConvertCircumflexes(HashSet<Lexeme> lexemes) {
        final CircumflexConvertingRootGenerator rootGenerator = new CircumflexConvertingRootGenerator();
        Collection<? extends Root> roots = rootGenerator.generateAll(lexemes);

        return new RootMapGenerator().generate(roots);
    }
}
