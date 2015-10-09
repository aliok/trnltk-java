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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.trnltk.model.lexicon.Lexeme;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

/**
 * Creates {@link Lexeme}s from dictionary file(s).
 * <p/>
 * A dictionary file is in the following format:
 * <pre>
 *     # this is a comment
 *     word [P:PrimaryPos, SecondaryPos; A:Attribute1,Attribute2; R:RootOfCompound]
 * </pre>
 * Please see {@code master-dictionary.dict} and {@code master-numeral-dictionary.dict} for the bundled dictionaries.
 */
public class DictionaryLoader {

    private static final String COMMENT_SYMBOL = "#";

    public static HashSet<Lexeme> loadDefaultMasterDictionary() {
        final CharSource charSource = Resources.asCharSource(Resources.getResource("master-dictionary.dict"), Charset.forName("utf-8"));
        return new DictionaryLoader().load(charSource);
    }

    public static HashSet<Lexeme> loadDefaultNumeralMasterDictionary() {
        final CharSource charSource = Resources.asCharSource(Resources.getResource("master-numeral-dictionary.dict"), Charset.forName("utf-8"));
        return new DictionaryLoader().load(charSource);
    }

    public HashSet<Lexeme> load(CharSource charSource) {
        try {
            // could have created a line processor, but all file will be
            // read anyway while creating an in-memory lexeme map
            final List<String> lines = charSource.readLines();
            return this.createLexemesFromLines(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    HashSet<Lexeme> createLexemesFromLines(Iterable<String> lines) {
        final LexemeCreator loader = new LexemeCreator();

        final Iterable<Lexeme> lexemes = Iterables.transform(lines, new Function<String, Lexeme>() {
            @Override
            public Lexeme apply(String input) {
                if (StringUtils.isBlank(input))
                    return null;

                input = input.trim();

                if (input.startsWith(COMMENT_SYMBOL))
                    return null;

                return loader.createLexemeFromLine(input);
            }
        });

        return Sets.newHashSet(Iterables.filter(lexemes, Predicates.notNull()));
    }
}
