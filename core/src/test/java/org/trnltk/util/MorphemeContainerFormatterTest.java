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

package org.trnltk.util;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParserBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MorphemeContainerFormatterTest {

    private MorphologicParser parser;

    @Before
    public void setUp() throws Exception {
        this.parser = ContextlessMorphologicParserBuilder.createSimple();
    }


    @Test
    public void shouldFormatWithDerivationGrouping() {
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerWithDerivationGrouping(result), equalTo("(1,\"kitap+Noun+A3sg+Pnon+Dat\")"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerWithDerivationGrouping(result), equalTo("(1,\"yap+Verb\")(2,\"Verb+Caus\")(3,\"Verb+Caus+Pos\")(4,\"Noun+Inf+A3sg+Pnon+Acc\")"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerWithDerivationGrouping(result, false), equalTo("(\"kitap+Noun+A3sg+Pnon+Dat\")"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerWithDerivationGrouping(result, false), equalTo("(\"yap+Verb\")(\"Verb+Caus\")(\"Verb+Caus+Pos\")(\"Noun+Inf+A3sg+Pnon+Acc\")"));
        }
    }

    @Test
    public void shouldFormatDetailed() throws JSONException {
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerDetailed(result), equalTo("{\"Root\":\"kitab\",\"Parts\":[{\"POS\":\"Noun\",\"Suffixes\":[\"A3sg\",\"Pnon\",\"Dat\"]}],\"LemmaRoot\":\"kitap\",\"RootPos\":\"Noun\"}"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerDetailed(result), equalTo("{\"Root\":\"yap\",\"Parts\":[{\"POS\":\"Verb\"},{\"POS\":\"Verb\",\"Suffixes\":[\"Caus\"]},{\"POS\":\"Verb\",\"Suffixes\":[\"Caus\",\"Pos\"]},{\"POS\":\"Noun\",\"Suffixes\":[\"Inf\",\"A3sg\",\"Pnon\",\"Acc\"]}],\"LemmaRoot\":\"yap\",\"RootPos\":\"Verb\"}"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("üzümcülükteki");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerDetailed(result), equalTo("{\"Root\":\"üzüm\",\"Parts\":[{\"POS\":\"Noun\",\"Suffixes\":[\"A3sg\",\"Pnon\",\"Nom\"]},{\"POS\":\"Adj\",\"Suffixes\":[\"Agt\"]},{\"POS\":\"Noun\",\"Suffixes\":[\"Ness\",\"A3sg\",\"Pnon\",\"Loc\"]},{\"POS\":\"Adj\",\"Suffixes\":[\"PointQual\"]}],\"LemmaRoot\":\"üzüm\",\"RootPos\":\"Noun\"}"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("bu");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerDetailed(result), equalTo("{\"Root\":\"bu\",\"LemmaRoot\":\"bu\",\"RootPos\":\"Det\"}"));
        }

    }

    @Test
    public void shouldFormatWithForms() {
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerWithForms(result), equalTo("kitab(kitap)+Noun+A3sg+Pnon+Dat(+yA[a])"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainerWithForms(result), equalTo("yap(yapmak)+Verb+Verb+Caus(dIr[tır])+Verb+Caus(!t[t])+Pos+Noun+Inf(mA[ma])+A3sg+Pnon+Acc(+yI[yı])"));
        }
    }


    @Test
    public void shouldFormat() {
        {
            final MorphemeContainer result = getFirstParseResult("kitaba");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainer(result), equalTo("kitap+Noun+A3sg+Pnon+Dat"));
        }
        {
            final MorphemeContainer result = getFirstParseResult("yaptırtmayı");
            assertThat(MorphemeContainerFormatter.formatMorphemeContainer(result), equalTo("yap+Verb+Verb+Caus+Verb+Caus+Pos+Noun+Inf+A3sg+Pnon+Acc"));
        }
    }


    private MorphemeContainer getFirstParseResult(String surface) {
        return this.parser.parse(new TurkishSequence(surface)).get(0);
    }

}
