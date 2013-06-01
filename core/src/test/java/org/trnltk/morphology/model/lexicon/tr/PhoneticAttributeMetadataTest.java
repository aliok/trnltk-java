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

package org.trnltk.morphology.model.lexicon.tr;


import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute.*;

public class PhoneticAttributeMetadataTest {
    //TODO
//    @Test
//    public void shouldCheckValidCases() {
//        assertTrue(PhoneticAttributeMetadata.isValid(Arrays.asList(FirstLetterConsonant)));
//        assertTrue(PhoneticAttributeMetadata.isValid(Arrays.asList(LastLetterConsonant, LastLetterVoiceless, FirstLetterVowel, LastVowelBack, LastVowelRounded)));
//        assertTrue(PhoneticAttributeMetadata.isValid(Arrays.asList(LastLetterVowel, LastVowelBack, LastVowelRounded, LastLetterNotVoiceless)));
//        assertTrue(PhoneticAttributeMetadata.isValid(Arrays.asList(FirstLetterConsonant, LastLetterConsonant, HasNoVowel, LastLetterNotVoiceless)));
//    }

    @Test
    public void shouldCheckInvalidCases() {
        assertFalse(PhoneticAttributeMetadata.isValid(Arrays.asList(FirstLetterConsonant, FirstLetterVowel)));
        assertFalse(PhoneticAttributeMetadata.isValid(Arrays.asList(LastLetterConsonant, LastLetterVowel)));
        assertFalse(PhoneticAttributeMetadata.isValid(Arrays.asList(LastLetterVowel, LastVowelBack, LastVowelRounded, LastLetterNotVoiceless, HasNoVowel)));
        assertFalse(PhoneticAttributeMetadata.isValid(Arrays.asList(FirstLetterConsonant, LastLetterConsonant, HasNoVowel)));
    }

}
