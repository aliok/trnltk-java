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
