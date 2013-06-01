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

import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.suffixbased.SuffixFormSequence;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import zemberek3.shared.lexicon.tr.PhoneticExpectation;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class PhoneticsEngineTest {
    PhoneticsEngine engine;

    final ArrayList<PhoneticExpectation> EMPTY_PE_LIST = new ArrayList<PhoneticExpectation>();
    final PhoneticExpectation PE_CS = PhoneticExpectation.ConsonantStart;
    final PhoneticExpectation PE_VS = PhoneticExpectation.VowelStart;

    @Before
    public void setUp() throws Exception {
        engine = new PhoneticsEngine(new SuffixFormSequenceApplier());
    }

    @Test
    public void shouldCheckApplicableSuffixForms() throws Exception {
        assertThat("elma", suffixFormApplicable(""));
        assertThat("elma", suffixFormApplicable(" "));
        assertThat("elma", suffixFormApplicable("lAr"));
        assertThat("elma", suffixFormApplicable("cI"));
        assertThat("elma", suffixFormApplicable("lAş"));
        assertThat("elma", suffixFormApplicable("dIr"));
        assertThat("elma", suffixFormApplicable("nIn"));
        assertThat("elma", suffixFormApplicable("+nIn"));
        assertThat("elma", suffixFormApplicable("+yI"));
        assertThat("elma", suffixFormApplicable("+sI"));
        assertThat("elma", suffixFormApplicable("+dAn"));
        assertThat("elma", suffixFormApplicable("+Im"));
        assertThat("elma", suffixFormApplicable("+ylA"));

        assertThat("armut", suffixFormApplicable(""));
        assertThat("armut", suffixFormApplicable(" "));
        assertThat("armut", suffixFormApplicable("lAr"));
        assertThat("armut", suffixFormApplicable("cI"));
        assertThat("armut", suffixFormApplicable("lAş"));
        assertThat("armut", suffixFormApplicable("dIr"));
        assertThat("armut", suffixFormApplicable("In"));
        assertThat("armut", suffixFormApplicable("+nIn"));
        assertThat("armut", suffixFormApplicable("+yI"));
        assertThat("armut", suffixFormApplicable("+sI"));
        assertThat("armut", suffixFormApplicable("+dAn"));
        assertThat("armut", suffixFormApplicable("+Im"));
        assertThat("armut", suffixFormApplicable("+ylA"));

        assertThat("yap", suffixFormApplicable("+yAcAk"));
        assertThat("yap", suffixFormApplicable("dIk"));
        assertThat("yap", suffixFormApplicable("m"));
        assertThat("yap", suffixFormApplicable("+Iyor"));
        assertThat("yap", suffixFormApplicable("+Ar"));

        assertThat("yapacak", suffixFormApplicable("+Im"));
        assertThat("yaptı", suffixFormApplicable("+Im"));
        assertThat("yapıyor", suffixFormApplicable("+Im"));
        assertThat("yapmakta", suffixFormApplicable("+yIm"));
        assertThat("yapmış", suffixFormApplicable("+Im"));

        assertThat("ata", suffixFormApplicable("+yAcAk"));
        assertThat("ata", suffixFormApplicable("dIk"));
        assertThat("ata", suffixFormApplicable("m"));
        assertThat("ata", suffixFormApplicable("+Iyor"));
        assertThat("ata", suffixFormApplicable("+Ar"));
    }

    @Test
    public void shouldCheckNotApplicableSuffixForms() throws Exception {
        assertThat("elma", not(suffixFormApplicable("Ar")));
        assertThat("elma", not(suffixFormApplicable("In")));
        assertThat("elma", not(suffixFormApplicable("II")));


        assertThat("ata", not(suffixFormApplicable("Ar")));
        assertThat("ata", not(suffixFormApplicable("In")));

        // XXX: fix below
//        THESE CASES ARE NOT SUPPORTED. THEY SHOULD BE MARKED AS INVALID CASE BY THE ENGINE
//        assertThat("armut", not(suffixFormApplicable("ndA")));  // there can be no suffix like "nda", but "+nda"
//        assertThat("yap", not(suffixFormApplicable("ndIk")));  // there can be no suffix "ndik", but "+ndik"
//        assertThat("ata", not(suffixFormApplicable("+II")));
    }

    @Test
    public void shouldSatisfyNoExpectations() {
        assertThat(engine.expectationsSatisfied(EMPTY_PE_LIST, null), equalTo(true));
        assertThat(engine.expectationsSatisfied(EMPTY_PE_LIST, new SuffixFormSequence("")), equalTo(true));
        assertThat(engine.expectationsSatisfied(EMPTY_PE_LIST, new SuffixFormSequence("xxx")), equalTo(true));
    }

    @Test
    public void shouldSatisfyNoExpectationsWithNoForm() {
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), null), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("   ")), equalTo(false));
    }

    @Test
    public void shouldSatisfyVowelStarts() {
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("ir")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("Ir")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("Aa")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("aa")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("+Aa")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("+ia")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("+inda")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("+nIn")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("+na")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("+ya")), equalTo(true));
    }

    @Test
    public void shouldNotSatisfyVowelStarts() {
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("lir")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("lIr")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("da")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("dA")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_VS), new SuffixFormSequence("+nda")), equalTo(false));
    }

    @Test
    public void shouldSatisfyConsonantStarts() {
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("da")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("nda")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+ar")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+Ar")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+ir")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+Ir")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+nda")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+nin")), equalTo(true));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+nIn")), equalTo(true));
    }

    @Test
    public void shouldNotSatisfyConsonantStarts() {
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("a")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("aa")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("A")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("Aa")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("ada")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("Ada")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+aa")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+Aa")), equalTo(false));
        assertThat(engine.expectationsSatisfied(Arrays.asList(PE_CS), new SuffixFormSequence("+aa")), equalTo(false));
    }

    @Test
    public void shouldApplySuffixesWithAttributes() {
        assertThat(engine.apply(new TurkishSequence("yap"), new SuffixFormSequence("+yAcAk"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("yap"), "acak")));
        assertThat(engine.apply(new TurkishSequence("yap"), new SuffixFormSequence("+Iyor"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("yap"), "ıyor")));
        assertThat(engine.apply(new TurkishSequence("yap"), new SuffixFormSequence("+Ar"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("yap"), "ar")));
        assertThat(engine.apply(new TurkishSequence("yap"), new SuffixFormSequence("tI"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("yap"), "tı")));
        assertThat(engine.apply(new TurkishSequence("kek"), new SuffixFormSequence("+I"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("kek"), "i")));
        assertThat(engine.apply(new TurkishSequence("kek"), new SuffixFormSequence("+Im"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("kek"), "im")));
        assertThat(engine.apply(new TurkishSequence("kek"), new SuffixFormSequence("+A"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("kek"), "e")));
        assertThat(engine.apply(new TurkishSequence("kek"), new SuffixFormSequence("dA"), Arrays.asList(LexemeAttribute.NoVoicing)), equalTo(Pair.of(new TurkishSequence("kek"), "te")));
    }

    @Test
    public void shouldMatchApplication() {
        assertThat(engine.applicationMatches(new TurkishSequence("elma"), "elma", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "elma", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "elma", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("keklerim"), "kekler", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("armudunu"), "armut", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("armudunu"), "armudu", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("armudunu"), "armudunu", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("yapacağım"), "yap", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("yapacağım"), "yapacak", true), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("yapacağım"), "yapacağım", true), equalTo(true));

        assertThat(engine.applicationMatches(new TurkishSequence("yapacağım"), "yap", false), equalTo(true));
        assertThat(engine.applicationMatches(new TurkishSequence("armut"), "armut", false), equalTo(true));
    }

    @Test
    public void shouldNotMatchApplication() {
        assertThat(engine.applicationMatches(new TurkishSequence("elma"), null, true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elma"), "elmax", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "a", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "ea", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "ela", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "elmx", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "elmax", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "elmalx", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "elmalax", true), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("elmalar"), "elmalarx", true), equalTo(false));

        assertThat(engine.applicationMatches(new TurkishSequence("yapacağım"), "yapacak", false), equalTo(false));
        assertThat(engine.applicationMatches(new TurkishSequence("armudunu"), "armut", false), equalTo(false));
    }

    @Test
    public void shouldApplySuffixes() {
        assertThat(engine.apply(new TurkishSequence("elma"), null, null), equalToAppliedStr("elma"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence(""), null), equalToAppliedStr("elma"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence(" "), null), equalToAppliedStr("elma"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("lAr"), null), equalToAppliedStr("elmalar"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("cI"), null), equalToAppliedStr("elmacı"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("lAş"), null), equalToAppliedStr("elmalaş"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("dIr"), null), equalToAppliedStr("elmadır"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("nIn"), null), equalToAppliedStr("elmanın"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("+nIn"), null), equalToAppliedStr("elmanın"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("+yI"), null), equalToAppliedStr("elmayı"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("+sI"), null), equalToAppliedStr("elması"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("+dAn"), null), equalToAppliedStr("elmadan"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("+Im"), null), equalToAppliedStr("elmam"));
        assertThat(engine.apply(new TurkishSequence("elma"), new SuffixFormSequence("+ylA"), null), equalToAppliedStr("elmayla"));

        assertThat(engine.apply(new TurkishSequence("armut"), null, null), equalToAppliedStr("armut"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence(""), null), equalToAppliedStr("armut"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence(" "), null), equalToAppliedStr("armut"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("lAr"), null), equalToAppliedStr("armutlar"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("cI"), null), equalToAppliedStr("armutçu"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("lAş"), null), equalToAppliedStr("armutlaş"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("dIr"), null), equalToAppliedStr("armuttur"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("In"), null), equalToAppliedStr("armudun"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("+nIn"), null), equalToAppliedStr("armudun"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("+yI"), null), equalToAppliedStr("armudu"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("+sI"), null), equalToAppliedStr("armudu"));
        // following is not supported!
//        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("+dAn"), null), equalToAppliedStr("armudan"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("+Im"), null), equalToAppliedStr("armudum"));
        assertThat(engine.apply(new TurkishSequence("armut"), new SuffixFormSequence("+ylA"), null), equalToAppliedStr("armutla"));

        assertThat(engine.apply(new TurkishSequence("del"), new SuffixFormSequence("+yAcAk"), null), equalToAppliedStr("delecek"));
        assertThat(engine.apply(new TurkishSequence("del"), new SuffixFormSequence("dIk"), null), equalToAppliedStr("deldik"));
        assertThat(engine.apply(new TurkishSequence("del"), new SuffixFormSequence("m"), null), equalToAppliedStr("delm"));
        assertThat(engine.apply(new TurkishSequence("del"), new SuffixFormSequence("+Iyor"), null), equalToAppliedStr("deliyor"));
        assertThat(engine.apply(new TurkishSequence("del"), new SuffixFormSequence("+Ar"), null), equalToAppliedStr("deler"));

        assertThat(engine.apply(new TurkishSequence("ata"), new SuffixFormSequence("+yAcAk"), null), equalToAppliedStr("atayacak"));
        assertThat(engine.apply(new TurkishSequence("ata"), new SuffixFormSequence("dIk"), null), equalToAppliedStr("atadık"));
        assertThat(engine.apply(new TurkishSequence("ata"), new SuffixFormSequence("m"), null), equalToAppliedStr("atam"));
        assertThat(engine.apply(new TurkishSequence("ata"), new SuffixFormSequence("+Ar"), null), equalToAppliedStr("atar"));
        assertThat(engine.apply(new TurkishSequence("at"), new SuffixFormSequence("+Iyor"), Arrays.asList(LexemeAttribute.NoVoicing)), equalToAppliedStr("atıyor"));

        assertThat(engine.apply(new TurkishSequence("bul"), new SuffixFormSequence("mAlI"), null), equalToAppliedStr("bulmalu"));
        assertThat(engine.apply(new TurkishSequence("bul"), new SuffixFormSequence("mAl!I"), null), equalToAppliedStr("bulmalı"));
    }

    private BaseMatcher<Pair<TurkishSequence, String>> equalToAppliedStr(final String appliedStr) {
        return new BaseMatcher<Pair<TurkishSequence, String>>() {
            @Override
            public boolean matches(Object item) {
                Pair<TurkishSequence, String> pair = (Pair<TurkishSequence, String>) item;
                return appliedStr.equals(pair.getLeft().getUnderlyingString() + pair.getRight());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(describe());
            }

            private String describe() {
                return String.format("a pair that concats into '%s'", appliedStr);
            }
        };
    }

    private BaseMatcher<String> suffixFormApplicable(final String suffixForm) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return engine.isSuffixFormApplicable(new PhoneticsAnalyzer().calculatePhoneticAttributesOfPlainSequence(new TurkishSequence(o.toString())), new SuffixFormSequence(suffixForm));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(describe());
            }

            private String describe() {
                return String.format("an input that suffixForm '%s' could be applied", suffixForm);
            }
        };
    }
}
