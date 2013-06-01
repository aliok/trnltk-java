package org.trnltk.morphology.model.structure;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class TurkishAlphabetTest {

    @Test
    public void getLetterByChar() {
        Assert.assertEquals(TurkishAlphabet.getLetter('c'), TurkishAlphabet.L_c);
        Assert.assertEquals(TurkishAlphabet.getLetter('a'), TurkishAlphabet.L_a);
        Assert.assertEquals(TurkishAlphabet.getLetter('w'), TurkishAlphabet.L_w);
        Assert.assertEquals(TurkishAlphabet.getLetter('z'), TurkishAlphabet.L_z);
        Assert.assertEquals(TurkishAlphabet.getLetter('x'), TurkishAlphabet.L_x);
        Assert.assertEquals(TurkishAlphabet.getLetter(TurkishAlphabet.C_cc), TurkishAlphabet.L_cc);
        Assert.assertEquals(TurkishAlphabet.getLetter(TurkishAlphabet.C_ii), TurkishAlphabet.L_ii);
    }

    @Test
    public void isVowelTest() {
        String vowels = "aeiuüıoöâîû";
        for (char c : vowels.toCharArray()) {
            Assert.assertTrue(TurkishAlphabet.getLetter(c).isVowel());
        }
        String nonvowels = "bcçdfgğjklmnprştvxwzq.";
        for (char c : nonvowels.toCharArray()) {
            Assert.assertFalse(TurkishAlphabet.getLetter(c).isVowel());
        }
    }

    @Test
    public void alphabetShouldNotHaveDuplicateChars() {
        final HashMultiset<Character> lowerCaseChars = HashMultiset.create(Collections2.transform(Lists.newArrayList(TurkishAlphabet.TURKISH_LETTERS),
                new Function<TurkicLetter, Character>() {
                    @Override
                    public Character apply(TurkicLetter input) {
                        return input.charValue();
                    }
                }));

        for (Multiset.Entry<Character> characterEntry : lowerCaseChars.entrySet()) {
            Assert.assertThat("For char " + characterEntry.getElement() + ", count must be null", characterEntry.getCount(), is(1));
        }
    }
}
