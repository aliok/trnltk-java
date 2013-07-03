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

package org.trnltk.model.lexicon;

import org.trnltk.model.letter.TurkishSequence;

import java.util.Set;

/**
 * A root is a possible beginning of a surface. Every root is derived from a lexeme.
 *
 * To illustrate:
 * <table border="1">
 *     <tr>
 *         <th>Lexeme</th>
 *         <th>Roots</th>
 *         <th>Usages for roots</th>
 *     </tr>
 *     <tr>
 *         <td>kitap+Noun</td>
 *         <td>kitap, kitab</td>
 *         <td>kitapta, kitaba</td>
 *     </tr>
 *     <tr>
 *         <td>omuz+Noun</td>
 *         <td>omuz, omz</td>
 *         <td>omuzlarda, omzunu</td>
 *     </tr>
 *     <tr>
 *         <td>hak</td>
 *         <td>hak, hakk</td>
 *         <td>hak davası, hakkımı arıyorum</td>
 *     </tr>
 *     <tr>
 *         <td>kek+Noun</td>
 *         <td>kek*</td>
 *         <td>keki</td>
 *     </tr>
 * </table>
 * <pre>* Please note that there is no keg or keğ</pre>
 *
 * In other words, a root is a text derived from a lexeme with one of the possible phonetic rules
 * respecting the phonetic attributes and lexeme attributes of the lexeme and the lemma root sequence.
 */
public interface Root {
    public TurkishSequence getSequence();

    public Lexeme getLexeme();

    public Set<PhoneticAttribute> getPhoneticAttributes();

    public Set<PhoneticExpectation> getPhoneticExpectations();
}
