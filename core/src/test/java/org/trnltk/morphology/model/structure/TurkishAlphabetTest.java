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
        TurkishAlphabet alphabet = new TurkishAlphabet();
        Assert.assertEquals(alphabet.getLetter('c'), TurkishAlphabet.L_c);
        Assert.assertEquals(alphabet.getLetter('a'), TurkishAlphabet.L_a);
        Assert.assertEquals(alphabet.getLetter('w'), TurkishAlphabet.L_w);
        Assert.assertEquals(alphabet.getLetter('z'), TurkishAlphabet.L_z);
        Assert.assertEquals(alphabet.getLetter('x'), TurkishAlphabet.L_x);
        Assert.assertEquals(alphabet.getLetter(TurkishAlphabet.C_cc), TurkishAlphabet.L_cc);
        Assert.assertEquals(alphabet.getLetter(TurkishAlphabet.C_ii), TurkishAlphabet.L_ii);
    }

    @Test
    public void getLetterByIndex() {
        TurkishAlphabet alphabet = new TurkishAlphabet();
        Assert.assertEquals(alphabet.getLetter(4), TurkishAlphabet.L_cc);
        Assert.assertEquals(alphabet.getLetter(1), TurkishAlphabet.L_a);
        Assert.assertEquals(alphabet.getLetter(3), TurkishAlphabet.L_c);
        Assert.assertEquals(alphabet.getLetter(29), TurkishAlphabet.L_z);
        Assert.assertEquals(alphabet.getLetter(32), TurkishAlphabet.L_x);
        Assert.assertEquals(alphabet.getLetter(11), TurkishAlphabet.L_ii);
    }

    @Test
    public void getAlphabeticIndex() {
        TurkishAlphabet alphabet = new TurkishAlphabet();
        Assert.assertEquals(alphabet.getAlphabeticIndex('a'), 1);
        Assert.assertEquals(alphabet.getAlphabeticIndex('c'), 3);
        Assert.assertEquals(alphabet.getAlphabeticIndex('z'), 29);
        Assert.assertEquals(alphabet.getAlphabeticIndex('x'), 32);
        Assert.assertEquals(alphabet.getAlphabeticIndex(TurkishAlphabet.C_cc), 4);
        Assert.assertEquals(alphabet.getAlphabeticIndex(TurkishAlphabet.C_ii), 11);
    }

    @Test
    public void getCharByAlphabeticIndex() {
        TurkishAlphabet alphabet = new TurkishAlphabet();
        Assert.assertEquals(alphabet.getCharByAlphabeticIndex(1), 'a');
        Assert.assertEquals(alphabet.getCharByAlphabeticIndex(3), 'c');
        Assert.assertEquals(alphabet.getCharByAlphabeticIndex(29), 'z');
        Assert.assertEquals(alphabet.getCharByAlphabeticIndex(32), 'x');
        Assert.assertEquals(alphabet.getCharByAlphabeticIndex(4), TurkishAlphabet.C_cc);
        Assert.assertEquals(alphabet.getCharByAlphabeticIndex(11), TurkishAlphabet.C_ii);
    }

    @Test
    public void isVowelTest() {
        TurkishAlphabet alphabet = new TurkishAlphabet();
        String vowels = "aeiuüıoöâîû";
        for (char c : vowels.toCharArray()) {
            Assert.assertTrue(alphabet.isVowel(c));
        }
        String nonvowels = "bcçdfgğjklmnprştvxwzq.";
        for (char c : nonvowels.toCharArray()) {
            Assert.assertFalse(alphabet.isVowel(c));
        }
    }

    @Test
    public void vowelCountTest() {
        TurkishAlphabet alphabet = new TurkishAlphabet();
        String[] entries = {"a", "aa", "", "bb", "bebaba"};
        int[] expCounts = {1, 2, 0, 0, 3};
        int i = 0;
        for (String entry : entries) {
            Assert.assertEquals(expCounts[i++], alphabet.vowelCount(entry));
        }
    }

    @Test
    public void toIndexes() {
        TurkishAlphabet alphabet = new TurkishAlphabet();
        byte[] expected = {1, 2, 3};
        Assert.assertArrayEquals(expected, alphabet.toByteIndexes("abc"));
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
