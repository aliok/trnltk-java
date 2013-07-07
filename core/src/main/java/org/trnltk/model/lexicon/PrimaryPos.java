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

import org.trnltk.common.structure.StringEnum;
import org.trnltk.common.structure.StringEnumMap;

/**
 * Primary part of speech tags.
 * <p/>
 * See <a href="http://en.wikipedia.org/wiki/Part_of_speech"> this Wikipedia article </a>
 * for definition of the part of speech.
 */
public enum PrimaryPos implements StringEnum<PrimaryPos> {
    Noun("Noun"),
    Adjective("Adj"),
    Adverb("Adv"),
    Conjunction("Conj"),
    Interjection("Interj"),
    Verb("Verb"),
    Pronoun("Pron"),
    Numeral("Num"),
    Determiner("Det"),
    PostPositive("Postp"),
    Question("Ques"),
    Duplicator("Dup"),
    Punctuation("Punc"),
    Unknown("Unk");

    public String shortForm;

    PrimaryPos(String shortForm) {
        this.shortForm = shortForm;
    }

    private final static StringEnumMap<PrimaryPos> shortFormToPosMap = StringEnumMap.get(PrimaryPos.class);

    public static StringEnumMap<PrimaryPos> converter() {
        return shortFormToPosMap;
    }

    public String getStringForm() {
        return shortForm;
    }
}
