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

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.ImmutableLexeme;
import org.trnltk.morphology.model.ImmutableRoot;
import org.trnltk.morphology.model.Lexeme;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.lexicon.PrimaryPos;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.lexicon.tr.PhoneticExpectation;

import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class ImmutableRootGeneratorTest {
    ImmutableRootGenerator generator;

    PhoneticAttribute LLV = PhoneticAttribute.LastLetterVowel;
    PhoneticAttribute LLC = PhoneticAttribute.LastLetterConsonant;

    PhoneticAttribute LVR = PhoneticAttribute.LastVowelRounded;
    PhoneticAttribute LVU = PhoneticAttribute.LastVowelUnrounded;
    PhoneticAttribute LVF = PhoneticAttribute.LastVowelFrontal;
    PhoneticAttribute LVB = PhoneticAttribute.LastVowelBack;

    PhoneticAttribute LLVless = PhoneticAttribute.LastLetterVoiceless;
    PhoneticAttribute LLVlessStop = PhoneticAttribute.LastLetterVoicelessStop;
    PhoneticAttribute LLNotVless = PhoneticAttribute.LastLetterNotVoiceless;

    PhoneticAttribute FLC = PhoneticAttribute.FirstLetterConsonant;
    PhoneticAttribute FLV = PhoneticAttribute.FirstLetterVowel;

    @Before
    public void setUp() throws Exception {
        generator = new ImmutableRootGenerator();
    }

    @Test
    public void shouldGenerateWithNoModifiers() throws Exception {
        {
            final Lexeme lexeme = new ImmutableLexeme("elma", "elma", PrimaryPos.Noun, null, null);
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("elma", lexeme, ImmutableSet.of(FLV, LLV, LVB, LLNotVless, LVU), null)));
        }
        {

            final Lexeme lexeme = new ImmutableLexeme("kek", "kek", PrimaryPos.Noun, null, null);
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kek", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LLVlessStop, LVU), null)));
        }
    }

    @Test
    public void shouldGenerateWithVoicing() {
        {
            final Lexeme lexeme = new ImmutableLexeme("armut", "armut", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("armut", lexeme, ImmutableSet.of(FLV, LVB, LLC, LLVless, LLVlessStop, LVR), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("armud", lexeme, ImmutableSet.of(FLV, LVB, LLC, LLVless, LVR), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }

        {
            final Lexeme lexeme = new ImmutableLexeme("kapak", "kapak", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kapak", lexeme, ImmutableSet.of(FLC, LVB, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kapağ", lexeme, ImmutableSet.of(FLC, LVB, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
        {
            final Lexeme lexeme = new ImmutableLexeme("cenk", "cenk", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("cenk", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ceng", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
        {
            final Lexeme lexeme = new ImmutableLexeme("kap", "kap", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kap", lexeme, ImmutableSet.of(FLC, LVB, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kab", lexeme, ImmutableSet.of(FLC, LVB, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
    }

    @Test
    public void shouldGenerateWithLastVowelDrop() {
        {
            final Lexeme lexeme = new ImmutableLexeme("ağız", "ağız", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.LastVowelDrop));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ağız", lexeme, ImmutableSet.of(FLV, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ağz", lexeme, ImmutableSet.of(FLV, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }

        {
            final Lexeme lexeme = new ImmutableLexeme("ahit", "ahit", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.LastVowelDrop, LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ahit", lexeme, ImmutableSet.of(FLV, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ahd", lexeme, ImmutableSet.of(FLV, LVF, LLVless, LLC, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
    }


    @Test
    public void shouldGenerateWithDoubling() {
        {
            final Lexeme lexeme = new ImmutableLexeme("hac", "hac", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Doubling));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("hac", lexeme, ImmutableSet.of(FLC, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("hacc", lexeme, ImmutableSet.of(FLC, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }

        {
            final Lexeme lexeme = new ImmutableLexeme("ret", "ret", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing, LexemeAttribute.Doubling));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ret", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("redd", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
    }

    @Test
    public void shouldGenerateWithProgressiveVowelDrop() {
        final Lexeme lexeme = new ImmutableLexeme("atamak", "ata", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.ProgressiveVowelDrop));
        final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
        assertThat(generatedRoots, hasSize(2));
        assertThat(generatedRoots, hasItem(new ImmutableRoot("ata", lexeme, ImmutableSet.of(FLV, LVB, LLV, LLNotVless, LVU), null)));
        assertThat(generatedRoots, hasItem(new ImmutableRoot("at", lexeme, ImmutableSet.of(FLV, LVB, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
    }

    @Test
    public void shouldGenerateWithInverseHarmony() {
        {
            final Lexeme lexeme = new ImmutableLexeme("kemal", "kemal", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.InverseHarmony));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kemal", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLNotVless, LVU), null)));
        }

        {
            final Lexeme lexeme = new ImmutableLexeme("kanaat", "kanaat", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.InverseHarmony));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kanaat", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LLVlessStop, LVU), null)));
        }
    }

    @Test
    public void test_should_generate_verbs_with_voicing_and_novoicing() {
        {
            final Lexeme lexeme = new ImmutableLexeme("gitmek", "git", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("git", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("gid", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
        {
            final Lexeme lexeme = new ImmutableLexeme("sürtmek", "sürt", PrimaryPos.Verb, null, null);
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("sürt", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLVless, LLVlessStop, LVR), null)));
        }
    }

    @Test
    public void shouldGenerateRootsForWordsEndingWithArabicAyn() {
        {
            final Lexeme lexeme = new ImmutableLexeme("cami", "cami", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.EndsWithAyn));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("cami", lexeme, ImmutableSet.of(FLC, LVF, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("cami", lexeme, ImmutableSet.of(FLC, LVF, LLV, LLNotVless, LVU), null)));
        }
        {
            // other phonetic events cannot occur when word ends with Ayn, but support it anyway for consistency
            // imaginary word 'abcde' which supports direct vovel concetanation, and
            final Lexeme lexeme = new ImmutableLexeme("abcde", "abcde", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.EndsWithAyn, LexemeAttribute.InverseHarmony));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("abcde", lexeme, ImmutableSet.of(FLV, LVF, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("abcde", lexeme, ImmutableSet.of(FLV, LVF, LLV, LLNotVless, LVU), null)));
        }
    }

}

