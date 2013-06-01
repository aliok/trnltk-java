package org.trnltk.morphology.phonetics;

import org.junit.Test;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttributeMetadata;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class PhoneticsAnalyzerValidityBruteForceTest {
    PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    @Test
    public void shouldTryEveryPossibleWordWithLength_4() throws Exception {
        // check the results of every possible word with length N

        char[] chars = "abçcdefghıijklmnoöpqrsştuüvwxyz".toCharArray();
        int len = 4;

        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = new HashSet<EnumSet<PhoneticAttribute>>();

        final WordGenerator wordGenerator = new WordGenerator(chars, len);
        int i = 0;
        while (wordGenerator.hasNext()) {
            final String word = wordGenerator.generateNext();
            if (i % 100000 == 0) {
                System.out.println("Processed word " + word + " #" + i);
            }

            final EnumSet<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes(word, null);
            distinctPhonAttrs.add(phoneticAttributes);
            if (!PhoneticAttributeMetadata.isValid(phoneticAttributes))
                throw new RuntimeException(String.format("For word %s, phonetic attributes are invalid: %s", word, phoneticAttributes));

            i++;
        }

        System.out.println("Distinct phonetic attributes count : " + distinctPhonAttrs.size());

        for (EnumSet<PhoneticAttribute> distinctPhonAttr : distinctPhonAttrs) {
            System.out.println(distinctPhonAttr);
        }
    }

    static class WordGenerator {
        char[] chars;
        int len;

        int index;
        final double maxIndex;

        WordGenerator(char[] chars, int len) {
            this.chars = chars;
            this.len = len;
            maxIndex = Math.pow(chars.length, len);
        }

        public String generateNext() {
            int n;
            char tmp[] = new char[len];
            if (hasNext()) {
                n = index;
                for (int k = 0; k < len; k++) {
                    tmp[len - k - 1] = chars[n % chars.length];
                    n /= chars.length;
                }
                index++;
                return new String(tmp);
            } else
                return null;
        }

        public boolean hasNext() {
            return index < maxIndex;
        }
    }
}
