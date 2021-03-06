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

import org.trnltk.model.letter.TurkishAlphabet;
import org.trnltk.model.letter.TurkishChar;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.ImmutableRoot;
import org.trnltk.model.lexicon.Lexeme;

import java.util.HashSet;
import java.util.LinkedList;

public class CircumflexConvertingRootGenerator extends ImmutableRootGenerator {

    private static final char CIRCUMFLEX_A = TurkishAlphabet.L_ac.charValue();
    private static final char CIRCUMFLEX_I = TurkishAlphabet.L_ic.charValue();
    private static final char CIRCUMFLEX_U = TurkishAlphabet.L_uc.charValue();

    private static final char PLAIN_A = TurkishAlphabet.L_a.charValue();
    private static final char PLAIN_I = TurkishAlphabet.L_i.charValue();
    private static final char PLAIN_U = TurkishAlphabet.L_u.charValue();


    @Override
    public HashSet<ImmutableRoot> generate(Lexeme lexeme) {
        // FOR BETTER PERFORMANCE, DIRECTLY WORK ON THE CHAR ARRAY

        final HashSet<ImmutableRoot> rootsWithCircumflexes = super.generate(lexeme);

        // does following check provides a performance improvement?
        final char[] lemmaRootChars = lexeme.getLemmaRoot().toCharArray();
        LinkedList<Integer> circumflexed_A_indices = new LinkedList<Integer>();
        LinkedList<Integer> circumflexed_I_indices = new LinkedList<Integer>();
        LinkedList<Integer> circumflexed_U_indices = new LinkedList<Integer>();

        boolean hasCircumflex = false;

        // might result stupid things if there is a circumflex char at the end and it is dropped (because e.g. progressive vowel drop)
        for (int i = 0; i < lemmaRootChars.length; i++) {
            char lemmaRootChar = lemmaRootChars[i];
            if (lemmaRootChar == CIRCUMFLEX_A) {
                circumflexed_A_indices.add(i);
                hasCircumflex = true;
            } else if (lemmaRootChar == CIRCUMFLEX_I) {
                circumflexed_I_indices.add(i);
                hasCircumflex = true;
            } else if (lemmaRootChar == CIRCUMFLEX_U) {
                circumflexed_U_indices.add(i);
                hasCircumflex = true;
            }
        }

        if (!hasCircumflex) {
            return rootsWithCircumflexes;
        } else {
            final HashSet<ImmutableRoot> roots = new HashSet<ImmutableRoot>();
            roots.addAll(rootsWithCircumflexes);

            for (ImmutableRoot rootWithCircumflexes : rootsWithCircumflexes) {
                final TurkishChar[] underlyingRootChars = rootWithCircumflexes.getSequence().getChars();

                for (Integer circumflexed_A_index : circumflexed_A_indices) {
                    underlyingRootChars[circumflexed_A_index] = new TurkishChar(PLAIN_A, TurkishAlphabet.L_a);
                }
                for (Integer circumflexed_I_index : circumflexed_I_indices) {
                    underlyingRootChars[circumflexed_I_index] = new TurkishChar(PLAIN_I, TurkishAlphabet.L_i);
                }
                for (Integer circumflexed_U_index : circumflexed_U_indices) {
                    underlyingRootChars[circumflexed_U_index] = new TurkishChar(PLAIN_U, TurkishAlphabet.L_u);
                }

                final ImmutableRoot rootWithoutCircumflexes = new ImmutableRoot(new TurkishSequence(underlyingRootChars),
                        lexeme, rootWithCircumflexes.getPhoneticAttributes(), rootWithCircumflexes.getPhoneticExpectations());

                roots.add(rootWithoutCircumflexes);
            }

            return roots;
        }

    }
}
