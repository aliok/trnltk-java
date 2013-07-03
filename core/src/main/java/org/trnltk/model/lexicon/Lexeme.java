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

import java.util.Set;

/**
 * A lexeme is an entry in lexicon. It has lemma (e.g yapmak),
 * lemmaRoot(e.g. yap), primaryPos(e.g. VERB), secondaryPos and attributes.
 * <p/>
 * Lexeme = lemma + POS
 * <p/>
 * It is important to know that there can be multiple lexemes for a lemmaRoot.
 * For example : yüz+VERB (denizde yüzmek), yüz+NUMERAL (yüz kişi)
 */
public interface Lexeme {
    String getLemma();

    String getLemmaRoot();

    PrimaryPos getPrimaryPos();

    SecondaryPos getSecondaryPos();

    Set<LexemeAttribute> getAttributes();
}
