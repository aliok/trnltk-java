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

package org.trnltk.morphology.phonetics;

import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import java.util.EnumSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PhoneticsAnalyzerTest {

    PhoneticsAnalyzer phoneticsAnalyzer;

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
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void shouldCalculatePhoneticAttributes() {
        assertThat(EnumSet.of(FLV, LLV, LVU, LVF, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("e"))));
        assertThat(EnumSet.of(FLC, LLC, LVU, LVF, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("kel"))));
        assertThat(EnumSet.of(FLV, LLC, LVU, LVF, LLVless, LLVlessStop), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("ek"))));
        assertThat(EnumSet.of(FLC, LLC, LVU, LVF, LLVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("seh"))));
        assertThat(EnumSet.of(FLV, LLC, LVU, LVF, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("elm"))));
        assertThat(EnumSet.of(FLV, LLC, LVU, LVF, LLVless, LLVlessStop), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("elk"))));
        assertThat(EnumSet.of(FLC, LLV, LVU, LVB, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("belma"))));
        assertThat(EnumSet.of(FLV, LLV, LVR, LVB, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("elmo"))));
        assertThat(EnumSet.of(FLC, LLC, LVU, LVB, LLVless, LLVlessStop), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("kapak"))));
    }

    @Test
    public void shouldCalculatePhoneticAttributesForWordsEndingWithArabicAyn() {
        assertThat(EnumSet.of(FLC, LLC, LVU, LVF, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributes(new TurkishSequence("cami"), EnumSet.of(LexemeAttribute.EndsWithAyn))));
        assertThat(EnumSet.of(FLC, LLC, LVR, LVB, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributes(new TurkishSequence("mevzu"), EnumSet.of(LexemeAttribute.EndsWithAyn))));
    }

    @Test
    public void shouldCalculateNewPhoneticAttributes() {
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("elma", null), 'm');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("elmam", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("kapak", null), 'm');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("kapakm", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("dana", null), 'a');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("danaa", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("schwyz", null), 's');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("schwyzs", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("aabb", null), 'a');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("aabba", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("ap", null), 'a');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("apa", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("aba", null), 'p');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("abap", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("caba", null), 'l');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("cabal", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
        {
            final Set<PhoneticAttribute> newAttrs = phoneticsAnalyzer.calculateNewPhoneticAttributes(phoneticsAnalyzer.calculatePhoneticAttributes("jarz", null), 'g');
            final Set<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes("jarzg", null);
            assertThat(phoneticAttributes, equalTo(newAttrs));
        }
    }
}
