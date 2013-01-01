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

package org.trnltk.morphology.model;

import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParserFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FormatterTest {

    private ContextlessMorphologicParser parser;

    @Before
    public void setUp() throws Exception {
        this.parser = ContextlessMorphologicParserFactory.createSimple();
    }


    @Test
    public void shouldFormatWithDerivationGrouping() {
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(Formatter.formatMorphemeContainerWithDerivationGrouping(result), equalTo("(1,\"kitap+Noun+A3sg+Pnon+Dat\")"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(Formatter.formatMorphemeContainerWithDerivationGrouping(result), equalTo("(1,\"yap+Verb\")(2,\"Verb+Caus\")(3,\"Verb+Caus+Pos\")(4,\"Noun+Inf+A3sg+Pnon+Acc\")"));
        }
    }

    @Test
    public void shouldFormatWithForms() {
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(Formatter.formatMorphemeContainerWithForms(result), equalTo("kitab(kitap)+Noun+A3sg+Pnon+Dat(+yA[a])"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(Formatter.formatMorphemeContainerWithForms(result), equalTo("yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Caus(t[t])+Pos+Noun+Inf(mA[ma])+A3sg+Pnon+Acc(+yI[yı])"));
        }
    }


    @Test
    public void shouldFormat() {
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(Formatter.formatMorphemeContainer(result), equalTo("kitap+Noun+A3sg+Pnon+Dat"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(Formatter.formatMorphemeContainer(result), equalTo("yap+Verb+Verb+Caus+Verb+Caus+Pos+Noun+Inf+A3sg+Pnon+Acc"));
        }
    }


    private MorphemeContainer getFirstParseResult(String surface) {
        return this.parser.parse(new TurkishSequence(surface)).get(0);
    }

}
