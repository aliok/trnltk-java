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

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.lexicon.*;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DictionaryLoaderTest {

    DictionaryLoader loader;

    @Before
    public void setUp() throws Exception {
        loader = new DictionaryLoader();
    }

    @Test
    public void shouldCreateLexemesFromLines() {
        String lines = "a [P:Interj]\n" +
                "aba [P:Adj]\n" +
                "abadî\n" +
                "abat [P:Adj; A:NoVoicing]\n" +
                "abdest [A:NoVoicing]\n" +
                "abes [P:Adj]\n" +
                "abes [P:Adv]\n" +
                "ablak [P:Adj; A:NoVoicing]\n" +
                "abuk [P:Adj, Dup;A:NoVoicing, NoSuffix]\n" +
                "acemborusu [A:CompoundP3sg; R:acemboru]\n" +
                "acembuselik\n" +
                "aciz [A:LastVowelDrop]\n" +
                "âciz [P:Adj]\n" +
                "açık [P:Adj]\n" +
                "# comment \n" +
                "        \n" +
                "ad\n" +
                "ad [P:Noun; A:Doubling, InverseHarmony]\n" +
                "addetmek [A:Voicing, Aorist_A]\n" +
                "addolmak [A:Causative_dIr]\n" +
                "ahlat [A:NoVoicing, Plural]\n" +
                "akşam [P:Noun, Time]\n" +
                "atamak [A:Causative_It]\n" +
                "sürtmek\n" +
                "yemek [P:Noun]\n" +
                "yemek [A:Causative_dIr]\n" +
                "ürkmek [A:Causative_It]";

        final int lineCount = 25;   // 27 lines - 1 comment line - 1 blank line

        final Set<Lexeme> lexemes = loader.createLexemesFromLines(Splitter.on(CharMatcher.anyOf("\n\r")).split(lines));
        assertThat(lexemes, hasSize(lineCount));

        assertThat(lexemes, hasItem(new ImmutableLexeme("a", "a", PrimaryPos.Interjection, null, null)));
        assertThat(lexemes, hasItem(new ImmutableLexeme("aba", "aba", PrimaryPos.Adjective, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("abadî", "abadî", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("abat", "abat", PrimaryPos.Adjective, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("abdest", "abdest", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("abes", "abes", PrimaryPos.Adjective, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("abes", "abes", PrimaryPos.Adverb, null, null)));
        assertThat(lexemes, hasItem(new ImmutableLexeme("ablak", "ablak", PrimaryPos.Adjective, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("abuk", "abuk", PrimaryPos.Adjective, SecondaryPos.Duplicator, ImmutableSet.of(LexemeAttribute.NoSuffix, LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("acemborusu", "acemboru", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.CompoundP3sg, LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("acembuselik", "acembuselik", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("aciz", "aciz", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.LastVowelDrop, LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("âciz", "âciz", PrimaryPos.Adjective, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("açık", "açık", PrimaryPos.Adjective, null, ImmutableSet.of(LexemeAttribute.Voicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("ad", "ad", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("ad", "ad", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Doubling, LexemeAttribute.InverseHarmony, LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("addetmek", "addet", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Aorist_A, LexemeAttribute.Causative_dIr, LexemeAttribute.Voicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("addolmak", "addol", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Aorist_I, LexemeAttribute.Causative_dIr, LexemeAttribute.NoVoicing, LexemeAttribute.Passive_In))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("ahlat", "ahlat", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Plural))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("akşam", "akşam", PrimaryPos.Noun, SecondaryPos.Time, ImmutableSet.of(LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("atamak", "ata", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Aorist_A, LexemeAttribute.Causative_It, LexemeAttribute.NoVoicing, LexemeAttribute.Passive_In, LexemeAttribute.ProgressiveVowelDrop))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("sürtmek", "sürt", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Aorist_A, LexemeAttribute.Causative_Ir, LexemeAttribute.NoVoicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("yemek", "yemek", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("yemek", "ye", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Aorist_A, LexemeAttribute.Causative_dIr, LexemeAttribute.NoVoicing, LexemeAttribute.Passive_In, LexemeAttribute.ProgressiveVowelDrop))));
        assertThat(lexemes, hasItem(new ImmutableLexeme("ürkmek", "ürk", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Aorist_A, LexemeAttribute.Causative_It, LexemeAttribute.NoVoicing))));
    }

    @Test
    public void shouldCreateLexemesFromMasterDictionary() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultMasterDictionary();
        assertThat(lexemes.size() > 1, equalTo(true));
        for (Lexeme immutableLexeme : lexemes) {
            assertThat(immutableLexeme.getLemma(), not(isEmptyOrNullString()));
            assertThat(immutableLexeme.getLemmaRoot(), not(isEmptyOrNullString()));
            assertThat(immutableLexeme.getPrimaryPos(), notNullValue());
            assertThat(immutableLexeme.getPrimaryPos(), not(equalTo(PrimaryPos.Numeral)));
            assertThat(immutableLexeme.getSecondaryPos(), not(isIn(SecondaryPos.NUMERAL_APPLICABLE)));
            assertThat(immutableLexeme.getAttributes(), notNullValue());
        }
    }

    @Test
    public void shouldCreateLexemesFromNumeralMasterDictionary() {
        final HashSet<Lexeme> lexemes = DictionaryLoader.loadDefaultNumeralMasterDictionary();
        assertThat(lexemes.size() > 1, equalTo(true));
        for (Lexeme immutableLexeme : lexemes) {
            assertThat(immutableLexeme.getLemma(), not(isEmptyOrNullString()));
            assertThat(immutableLexeme.getLemmaRoot(), not(isEmptyOrNullString()));
            assertThat(immutableLexeme.getPrimaryPos(), equalTo(PrimaryPos.Numeral));
            assertThat(immutableLexeme.getSecondaryPos(), isIn(SecondaryPos.NUMERAL_APPLICABLE));
            assertThat(immutableLexeme.getAttributes(), notNullValue());
        }
    }
}
