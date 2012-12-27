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
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.ImmutableRoot;
import org.trnltk.morphology.model.Lexeme;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.phonetics.PhoneticAttribute;
import org.trnltk.morphology.phonetics.PhoneticExpectation;
import zemberek3.lexicon.PrimaryPos;

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

    PhoneticAttribute LLCont = PhoneticAttribute.LastLetterContinuant;
    PhoneticAttribute LLNotCont = PhoneticAttribute.LastLetterNotContinuant;

    PhoneticAttribute LLVless = PhoneticAttribute.LastLetterVoiceless;
    PhoneticAttribute LLVStop = PhoneticAttribute.LastLetterVoicedStop;
    PhoneticAttribute LLVlessStop = PhoneticAttribute.LastLetterVoicelessStop;
    PhoneticAttribute LLNotVless = PhoneticAttribute.LastLetterNotVoiceless;

    @Before
    public void setUp() throws Exception {
        generator = new ImmutableRootGenerator();
    }

    @Test
    public void shouldGenerateWithNoModifiers() throws Exception {
        {
            final Lexeme lexeme = new Lexeme("elma", "elma", PrimaryPos.Noun, null, null);
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("elma", lexeme, ImmutableSet.of(LLNotCont, LLV, LVB, LLNotVless, LVU), null)));
        }
        {

            final Lexeme lexeme = new Lexeme("kek", "kek", PrimaryPos.Noun, null, null);
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kek", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LLVlessStop, LVU), null)));
        }
    }

    @Test
    public void shouldGenerateWithVoicing() {
        {
            final Lexeme lexeme = new Lexeme("armut", "armut", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("armut", lexeme, ImmutableSet.of(LLNotCont, LVB, LLC, LLVless, LLVlessStop, LVR), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("armud", lexeme, ImmutableSet.of(LLNotCont, LVB, LLC, LLVless, LVR), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }

        {
            final Lexeme lexeme = new Lexeme("kapak", "kapak", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kapak", lexeme, ImmutableSet.of(LLNotCont, LVB, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kapağ", lexeme, ImmutableSet.of(LLCont, LVB, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
        {
            final Lexeme lexeme = new Lexeme("cenk", "cenk", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("cenk", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ceng", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
        {
            final Lexeme lexeme = new Lexeme("kap", "kap", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kap", lexeme, ImmutableSet.of(LLNotCont, LVB, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kab", lexeme, ImmutableSet.of(LLNotCont, LVB, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
    }

    @Test
    public void shouldGenerateWithLastVowelDrop() {
        {
            final Lexeme lexeme = new Lexeme("ağız", "ağız", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.LastVowelDrop));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ağız", lexeme, ImmutableSet.of(LLCont, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ağz", lexeme, ImmutableSet.of(LLCont, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }

        {
            final Lexeme lexeme = new Lexeme("ahit", "ahit", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.LastVowelDrop, LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ahit", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ahd", lexeme, ImmutableSet.of(LLNotCont, LVF, LLVless, LLC, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
    }


    @Test
    public void shouldGenerateWithDoubling() {
        {
            final Lexeme lexeme = new Lexeme("hac", "hac", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Doubling));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("hac", lexeme, ImmutableSet.of(LLNotCont, LLVStop, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("hacc", lexeme, ImmutableSet.of(LLNotCont, LLVStop, LVB, LLC, LLNotVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }

        {
            final Lexeme lexeme = new Lexeme("ret", "ret", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.Voicing, LexemeAttribute.Doubling));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("ret", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("redd", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
    }

    @Test
    public void shouldGenerateWithProgressiveVowelDrop() {
        final Lexeme lexeme = new Lexeme("atamak", "ata", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.ProgressiveVowelDrop));
        final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
        assertThat(generatedRoots, hasSize(2));
        assertThat(generatedRoots, hasItem(new ImmutableRoot("ata", lexeme, ImmutableSet.of(LLNotCont, LVB, LLV, LLNotVless, LVU), null)));
        assertThat(generatedRoots, hasItem(new ImmutableRoot("at", lexeme, ImmutableSet.of(LLNotCont, LVB, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
    }

    @Test
    public void shouldGenerateWithInverseHarmony() {
        {
            final Lexeme lexeme = new Lexeme("kemal", "kemal", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.InverseHarmony));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kemal", lexeme, ImmutableSet.of(LLCont, LVF, LLC, LLNotVless, LVU), null)));
        }

        {
            final Lexeme lexeme = new Lexeme("kanaat", "kanaat", PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.InverseHarmony));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("kanaat", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LLVlessStop, LVU), null)));
        }
    }

    @Test
    public void test_should_generate_verbs_with_voicing_and_novoicing() {
        {
            final Lexeme lexeme = new Lexeme("gitmek", "git", PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.Voicing));
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(2));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("git", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LLVlessStop, LVU), ImmutableSet.of(PhoneticExpectation.ConsonantStart))));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("gid", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LVU), ImmutableSet.of(PhoneticExpectation.VowelStart))));
        }
        {
            final Lexeme lexeme = new Lexeme("sürtmek", "sürt", PrimaryPos.Verb, null, null);
            final HashSet<ImmutableRoot> generatedRoots = generator.generate(lexeme);
            assertThat(generatedRoots, hasSize(1));
            assertThat(generatedRoots, hasItem(new ImmutableRoot("sürt", lexeme, ImmutableSet.of(LLNotCont, LVF, LLC, LLVless, LLVlessStop, LVR), null)));
        }
    }

}

