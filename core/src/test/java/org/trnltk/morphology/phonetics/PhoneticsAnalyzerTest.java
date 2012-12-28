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

package org.trnltk.morphology.phonetics;

import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.EnumSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PhoneticsAnalyzerTest {

    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void shouldCalculatePhoneticAttributes() {
        PhoneticAttribute LLV = PhoneticAttribute.LastLetterVowel;
        PhoneticAttribute LLC = PhoneticAttribute.LastLetterConsonant;

        PhoneticAttribute LVR = PhoneticAttribute.LastVowelRounded;
        PhoneticAttribute LVU = PhoneticAttribute.LastVowelUnrounded;
        PhoneticAttribute LVF = PhoneticAttribute.LastVowelFrontal;
        PhoneticAttribute LVB = PhoneticAttribute.LastVowelBack;

        PhoneticAttribute LLVless = PhoneticAttribute.LastLetterVoiceless;
        PhoneticAttribute LLVlessStop = PhoneticAttribute.LastLetterVoicelessStop;
        PhoneticAttribute LLNotVless = PhoneticAttribute.LastLetterNotVoiceless;

        assertThat(EnumSet.of(LLV, LVU, LVF, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("e"))));
        assertThat(EnumSet.of(LLC, LVU, LVF, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("el"))));
        assertThat(EnumSet.of(LLC, LVU, LVF, LLVless, LLVlessStop), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("ek"))));
        assertThat(EnumSet.of(LLC, LVU, LVF, LLVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("eh"))));
        assertThat(EnumSet.of(LLC, LVU, LVF, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("elm"))));
        assertThat(EnumSet.of(LLC, LVU, LVF, LLVless, LLVlessStop), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("elk"))));
        assertThat(EnumSet.of(LLV, LVU, LVB, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("elma"))));
        assertThat(EnumSet.of(LLV, LVR, LVB, LLNotVless), equalTo(phoneticsAnalyzer.calculatePhoneticAttributesOfPlainSequence(new TurkishSequence("elmo"))));
    }
}
