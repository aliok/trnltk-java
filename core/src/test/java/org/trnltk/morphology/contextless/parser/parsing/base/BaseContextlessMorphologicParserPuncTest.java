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

package org.trnltk.morphology.contextless.parser.parsing.base;

import org.junit.Test;

public abstract class BaseContextlessMorphologicParserPuncTest extends BaseContextlessMorphologicParserTest {

    @Test
    public void shouldParsePuncStrings() {
        assertParseCorrect(".", ".(.)+Punc");
        assertParseCorrect("-", "-(-)+Punc");
        assertParseCorrect("....", "....(....)+Punc");
        assertParseCorrect("‿﹎﹏»”>", "‿﹎﹏»”>(‿﹎﹏»”>)+Punc");
        assertParseCorrect("„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°", "„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°(„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°)+Punc");
    }

    @Test
    public void shouldNotMarkAsPunc() {
        assertNotParsable("");
        assertNotParsable(". ");
        assertNotParsable(" .");
        assertNotParsable(".a");
        assertNotParsable(".1");
        assertNotParsable(".\n");
        assertNotParsable(".\t");
        assertNotParsable(".¨");    //has control char
    }
}
