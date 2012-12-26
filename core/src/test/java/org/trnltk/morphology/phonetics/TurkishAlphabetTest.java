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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TurkishAlphabetTest {

    @Test
    public void alphabetShouldNotHaveDuplicateLowerChars() {
        final HashMultiset<Character> lowerCaseChars = HashMultiset.create(Collections2.transform(TurkishAlphabet.Turkish_Letters, new Function<TurkishLetter, Character>() {
            @Override
            public Character apply(TurkishLetter input) {
                return input.getCharValue();
            }
        }));

        for (Multiset.Entry<Character> characterEntry : lowerCaseChars.entrySet()) {
            assertThat("For char " + characterEntry.getElement() + ", count must be null", characterEntry.getCount(), is(1));
        }
    }

    @Test
    public void alphabetShouldNotHaveDuplicateUpperChars() {
        final HashMultiset<Character> lowerCaseChars = HashMultiset.create(Collections2.transform(TurkishAlphabet.Turkish_Letters, new Function<TurkishLetter, Character>() {
            @Override
            public Character apply(TurkishLetter input) {
                return input.getUpperCaseCharValue();
            }
        }));

        for (Multiset.Entry<Character> characterEntry : lowerCaseChars.entrySet()) {
            assertThat("For char " + characterEntry.getElement() + ", count must be null", characterEntry.getCount(), is(1));
        }
    }

}
