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

package org.trnltk.morphology.contextless.rootfinder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.lexicon.DynamicRoot;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.lexicon.PrimaryPos;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class BruteForceVerbRootFinderTest extends BaseRootFinderTest<DynamicRoot> {
    private static final Comparator<? super Root> ROOT_COMPARATOR_BY_LEXEME = new Comparator<Root>() {
        @Override
        public int compare(Root o1, Root o2) {

            // first try comparing with the lemma
            // then lexemeAttributes

            final String o1Lemma = o1.getLexeme().getLemma();
            final String o2Lemma = o2.getLexeme().getLemma();

            final int lemmaCompare = o1Lemma.compareTo(o2Lemma);
            if (lemmaCompare != 0)
                return lemmaCompare;

            final Set<LexemeAttribute> o1Attrs = o1.getLexeme().getAttributes();
            final Set<LexemeAttribute> o2Attrs = o2.getLexeme().getAttributes();

            final int lexemeAttrsCompare = Ordering.natural().compare(o1Attrs.size(), o2Attrs.size());
            if (lexemeAttrsCompare == 0) {
                final ArrayList<LexemeAttribute> o1AttrsList = new ArrayList<LexemeAttribute>(o1Attrs);
                final ArrayList<LexemeAttribute> o2AttrsList = new ArrayList<LexemeAttribute>(o2Attrs);
                Collections.sort(o1AttrsList);
                Collections.sort(o2AttrsList);
                final Iterator<LexemeAttribute> o1Iterator = o1AttrsList.iterator();
                final Iterator<LexemeAttribute> o2Iterator = o2AttrsList.iterator();
                while (true) {
                    if (!o1Iterator.hasNext() && !o2Iterator.hasNext())
                        return 0;
                    final LexemeAttribute o1Attr = o1Iterator.next();
                    final LexemeAttribute o2Attr = o2Iterator.next();

                    final int attrCompare = o1Attr.name().compareTo(o2Attr.name());
                    if (attrCompare == 0)
                        continue;
                    else
                        return attrCompare;

                }
            }
            return lexemeAttrsCompare;
        }
    };

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected RootFinder createRootFinder() {
        return new BruteForceVerbRootFinder();
    }

    @Test
    public void shouldFindNoRootsOnInvalidCases() {
        assertThat(findRootsForPartialInput(null, null), hasSize(0));
        assertThat(findRootsForPartialInput("", null), hasSize(0));
        assertThat(findRootsForPartialInput(null, ""), hasSize(0));
        assertThat(findRootsForPartialInput("", ""), hasSize(0));
        assertThat(findRootsForPartialInput("a", null), hasSize(0));
        assertThat(findRootsForPartialInput("a", ""), hasSize(0));
        assertThat(findRootsForPartialInput("ab", "a"), hasSize(0));
        assertThat(findRootsForPartialInput("ab", "ad"), hasSize(0));
        assertThat(findRootsForPartialInput("ab", "ada"), hasSize(0));
    }

    @Test
    public void should_return_no_results_for_short_verbs() {
        assertThat(findRootsForPartialInput("d", "de"), hasSize(0));
    }

    @Test
    public void should_return_no_results_for_invalid_verbs() {
        assertThat(findRootsForPartialInput("db", "dbe"), hasSize(0));
        assertThat(findRootsForPartialInput("abcd", "abcde"), hasSize(0));

        // yontmak, ürkmek, büyütlmek is fine

        // simple cases
        assertThat(findRootsForPartialInput("yoyt", "yoytacak"), hasSize(0));
        assertThat(findRootsForPartialInput("yoly", "yolyacak"), hasSize(0));
        assertThat(findRootsForPartialInput("yolz", "yolzacak"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yomd", "yomdacak"), hasSize(0));


        // invalid verb and progressive vowel drop
        assertThat(findRootsForPartialInput("yoly", "yolyuyor"), hasSize(2)); // has len 2, since yolyamak and yolyumak are valid
        assertThat(findRootsForPartialInput("yoyb", "yoybuyor"), hasSize(2)); // has len 2, since yoybamak and yoybumak are valid
        assertThat(findRootsForPartialInput("yoyh", "yoyhuyor"), hasSize(2)); // has len 2, since yoyhamak and yoyhumak are valid
        //........ with voicing
        // Voicing + ProgressiveVowelDrop is not supported!


        // invalid verb and aorist_A
        assertThat(findRootsForPartialInput("yonl", "yonlar"), hasSize(0));
        assertThat(findRootsForPartialInput("yoğt", "yoğtar"), hasSize(0));
        assertThat(findRootsForPartialInput("yoğğ", "yoğğar"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yoğd", "yoğdar"), hasSize(0));

        // invalid verb and aorist_I
        assertThat(findRootsForPartialInput("yanl", "yanlur"), hasSize(0));
        assertThat(findRootsForPartialInput("yağt", "yağtır"), hasSize(0));
        assertThat(findRootsForPartialInput("yağğ", "yağğır"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yağd", "yağdır"), hasSize(0));


        // invalid verb and causative_t
        assertThat(findRootsForPartialInput("yorz", "yorztrmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yozg", "yozgtrmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yozz", "yozztrmaz"), hasSize(0));

        // invalid verb and causative_Ir
        assertThat(findRootsForPartialInput("yarz", "yarzırmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgırmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazzırmaz"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yazd", "yazdırmaz"), hasSize(0));

        // invalid verb and causative_It
        assertThat(findRootsForPartialInput("yarz", "yarzıtmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgıtmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazzıtmaz"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yazd", "yazdıtmaz"), hasSize(0));

        // invalid verb and causative_Ar
        assertThat(findRootsForPartialInput("yorz", "yorzarmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yozg", "yozgarmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yozz", "yozzarmaz"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yozd", "yozdarmaz"), hasSize(0));

        // invalid verb and causative_dIr
        assertThat(findRootsForPartialInput("yarz", "yarzdırmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgdırmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazzdırmaz"), hasSize(0));
        //........ with DeVoicing
        assertThat(findRootsForPartialInput("yapt", "yapttırmaz"), hasSize(0));

        // invalid verb and causative_Il
        assertThat(findRootsForPartialInput("yarz", "yarzılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazzılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yarz", "yarzlmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazglmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazzlmaz"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yapt", "yaptılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yapt", "yaptlmaz"), hasSize(0));

        // invalid verb and causative_In
        assertThat(findRootsForPartialInput("yarz", "yarzınmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgınmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazzınmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yarz", "yarznmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgnmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazznmaz"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yapt", "yaptınmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yapt", "yaptnmaz"), hasSize(0));

        // invalid verb and causative_InIl
        assertThat(findRootsForPartialInput("yarz", "yarzınılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgınılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazzınılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yarz", "yarznılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazg", "yazgnılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yazz", "yazznılmaz"), hasSize(0));
        //........ with voicing
        assertThat(findRootsForPartialInput("yapt", "yaptınılmaz"), hasSize(0));
        assertThat(findRootsForPartialInput("yapt", "yaptnılmaz"), hasSize(0));

    }

    @Test
    public void should_create_roots_without_orthographic_changes_and_no_lexeme_attributes() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("al", "al");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("almak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("sal", "sal");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("sal"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("sal"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("salmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("al", "aldı");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("almak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("sal", "saldı");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("sal"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("sal"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("salmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
        }
    }

    @Test
    public void should_create_roots_with_progressive_vowel_drop() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("başl", "başlıyor");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("başl"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("başla"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("başlamak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("başl"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("başlı"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("başlımak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ell", "elliyorduk");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ell"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("elle"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ellemek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ell"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("elli"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ellimek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("oyn", "oynuyorlar");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("oyn"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("oyna"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("oynamak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("oyn"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("oynu"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("oynumak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("söyl", "söylüyorsun");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("söyl"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("söyle"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("söylemek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("söyl"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("söylü"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("söylümek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
        }
    }

    @Test
    public void should_create_roots_with_aorist_A_and_causative_Ar() {
        // each Aorist_A case is also a Causative_Ar case
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("çık", "çıkar");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("çık"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("çık"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("çıkmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("çık"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("çık"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("çıkmak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Aorist_A)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("çık"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("çık"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("çıkmak"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_Ar)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("öt", "ötermiş");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("öt"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("öt"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ötmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("öt"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("öt"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ötmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Aorist_A)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("öt"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("öt"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("ötmek"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_Ar)));
        }
    }

    @Test
    public void should_create_roots_with_aorist_I_and_causative_Ir() {
        // each Aorist_I case is also a Causative_Ir case
        // this actually doesn't make sense since yat+Aor->yatar but yat+Caus->yatir
        // however, there is no way to distinguish
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yat", "yatır");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yat"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yat"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yatmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("yat"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("yat"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("yatmak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Aorist_I)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("yat"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("yat"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("yatmak"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_Ir)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("gel", "gelir");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("gel"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("gel"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("gelmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("gel"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("gel"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("gelmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Aorist_I)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("gel"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("gel"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("gelmek"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_Ir)));
        }
        {
            // no Aorist_I for -ur, -ür
            final List<DynamicRoot> roots = findRootsForPartialInput("zop", "zopuracak");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("zop"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("zop"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("zopmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("zop"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("zop"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("zopmak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_Ir)));
        }

    }

    @Test
    public void should_create_roots_with_causative_t() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("kapa", "kapattım");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("kapa"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("kapa"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("kapamak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("kapa"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("kapa"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("kapamak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_t)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yürü", "yürütecekmiş");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yürü"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yürü"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yürümek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("yürü"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("yürü"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("yürümek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_t)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("köpür", "köpürttüm");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("köpür"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("köpür"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("köpürmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("köpür"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("köpür"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("köpürmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_t)));
        }
    }

    @Test
    public void shouldCreateRootsWithoutCausative_t() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("kapat", "kapattım");
            assertThat(roots, hasSize(1));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("kapat"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("kapat"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("kapatmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
        }
    }

    @Test
    public void should_create_roots_with_causative_It() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("ak", "akıtmışlar");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ak"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("akmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ak"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("ak"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("akmak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_It)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("kork", "korkutacaklar");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("kork"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("kork"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("korkmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("kork"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("kork"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("korkmak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_It)));
        }
    }

    @Test
    public void should_create_roots_with_causative_dIr() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("al", "aldırmışlar");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("almak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("al"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("al"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("almak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_dIr)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("öl", "öldürmüşcesine");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("öl"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("öl"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ölmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("öl"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("öl"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ölmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_dIr)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("öt", "öttürüyorum");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("öt"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("öt"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("ötmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("öt"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("öt"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("ötmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_dIr)));
        }
    }

    @Test
    public void should_create_roots_with_passive_Il() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("sat", "satılmış");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("sat"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("sat"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("satmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("sat"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("sat"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("satmak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Passive_Il)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("döv", "dövülen");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("döv"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("döv"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("dövmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("döv"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("döv"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("dövmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Passive_Il)));
        }
    }

    @Test
    public void should_create_roots_with_passive_In() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("al", "alındı");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("al"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("almak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("al"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("al"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("almak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Passive_In)));
        }
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("tekmele", "tekmelendim");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("tekmele"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("tekmele"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("tekmelemek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("tekmele"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("tekmele"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("tekmelemek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Passive_In)));
        }
    }

    @Test
    public void should_create_roots_with_passive_InIl() {
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("de", "denildi");
            assertThat(roots, hasSize(3));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("de"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("de"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("demek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("de"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("de"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("demek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Passive_In)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("de"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("de"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("demek"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Passive_InIl)));
        }
    }

    @Test
    public void should_create_roots_with_voicing() {
        // nothing but voicing
        {
            final List<DynamicRoot> roots = findRootsForPartialInput("gid", "gidip");
            assertThat(roots, hasSize(2));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("gid"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("gidmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("git"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("gitmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing)));
        }


        // skip progressive vowel drop and voicing


        {
            final List<DynamicRoot> roots = findRootsForPartialInput("yeld", "yeldiyorum");
            assertThat(roots, hasSize(4));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("yeld"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("yelde"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("yeldemek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("yeld"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("yeldi"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("yeldimek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.ProgressiveVowelDrop)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("yeld"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("yeld"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("yeldmek"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(3).getSequence().getUnderlyingString(), equalTo("yeld"));
            assertThat(roots.get(3).getLexeme().getLemmaRoot(), equalTo("yelt"));
            assertThat(roots.get(3).getLexeme().getLemma(), equalTo("yeltmek"));
            assertThat(roots.get(3).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(3).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing)));
            // NOTE: there is also "yeltemek" and "yeltimek", but doesn't seem likely. skipping it!
        }
        {
            // voicing and aorist_A and causative_Ar (ok, gidermek is not really git+Caus);
            final List<DynamicRoot> roots = findRootsForPartialInput("gid", "giderdi");
            assertThat(roots, hasSize(6));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("gid"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("gidmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("gid"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("gidmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Aorist_A)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("gid"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("gidmek"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_Ar)));
            assertThat(roots.get(3).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(3).getLexeme().getLemmaRoot(), equalTo("git"));
            assertThat(roots.get(3).getLexeme().getLemma(), equalTo("gitmek"));
            assertThat(roots.get(3).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(3).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing)));
            assertThat(roots.get(4).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(4).getLexeme().getLemmaRoot(), equalTo("git"));
            assertThat(roots.get(4).getLexeme().getLemma(), equalTo("gitmek"));
            assertThat(roots.get(4).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(4).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing, LexemeAttribute.Aorist_A)));
            assertThat(roots.get(5).getSequence().getUnderlyingString(), equalTo("gid"));
            assertThat(roots.get(5).getLexeme().getLemmaRoot(), equalTo("git"));
            assertThat(roots.get(5).getLexeme().getLemma(), equalTo("gitmek"));
            assertThat(roots.get(5).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(5).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing, LexemeAttribute.Causative_Ar)));
        }
        {
            // voicing and aorist_I and causative_Ir
            // couldn't find an example, but lets support it until we find out that it is impossible
            // imaginary verb "zantmak"
            final List<DynamicRoot> roots = findRootsForPartialInput("zand", "zandırmış");
            assertThat(roots, hasSize(6));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("zand"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("zand"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("zandmak"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("zand"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("zand"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("zandmak"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Aorist_I)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("zand"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("zand"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("zandmak"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Causative_Ir)));
            assertThat(roots.get(3).getSequence().getUnderlyingString(), equalTo("zand"));
            assertThat(roots.get(3).getLexeme().getLemmaRoot(), equalTo("zant"));
            assertThat(roots.get(3).getLexeme().getLemma(), equalTo("zantmak"));
            assertThat(roots.get(3).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(3).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing)));
            assertThat(roots.get(4).getSequence().getUnderlyingString(), equalTo("zand"));
            assertThat(roots.get(4).getLexeme().getLemmaRoot(), equalTo("zant"));
            assertThat(roots.get(4).getLexeme().getLemma(), equalTo("zantmak"));
            assertThat(roots.get(4).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(4).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing, LexemeAttribute.Aorist_I)));
            assertThat(roots.get(5).getSequence().getUnderlyingString(), equalTo("zand"));
            assertThat(roots.get(5).getLexeme().getLemmaRoot(), equalTo("zant"));
            assertThat(roots.get(5).getLexeme().getLemma(), equalTo("zantmak"));
            assertThat(roots.get(5).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(5).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing, LexemeAttribute.Causative_Ir)));
        }

        // skip {Causative_t, Causative_It, Causative_dIr} and voicing

        {
            // voicing and passive_Il
            // tadılan, güdülen, didilen, gidilen, hapsedilen ...
            final List<DynamicRoot> roots = findRootsForPartialInput("ed", "edilen");
            assertThat(roots, hasSize(4));
            assertThat(roots.get(0).getSequence().getUnderlyingString(), equalTo("ed"));
            assertThat(roots.get(0).getLexeme().getLemmaRoot(), equalTo("ed"));
            assertThat(roots.get(0).getLexeme().getLemma(), equalTo("edmek"));
            assertThat(roots.get(0).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(0).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing)));
            assertThat(roots.get(1).getSequence().getUnderlyingString(), equalTo("ed"));
            assertThat(roots.get(1).getLexeme().getLemmaRoot(), equalTo("ed"));
            assertThat(roots.get(1).getLexeme().getLemma(), equalTo("edmek"));
            assertThat(roots.get(1).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(1).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.NoVoicing, LexemeAttribute.Passive_Il)));
            assertThat(roots.get(2).getSequence().getUnderlyingString(), equalTo("ed"));
            assertThat(roots.get(2).getLexeme().getLemmaRoot(), equalTo("et"));
            assertThat(roots.get(2).getLexeme().getLemma(), equalTo("etmek"));
            assertThat(roots.get(2).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(2).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing)));
            assertThat(roots.get(3).getSequence().getUnderlyingString(), equalTo("ed"));
            assertThat(roots.get(3).getLexeme().getLemmaRoot(), equalTo("et"));
            assertThat(roots.get(3).getLexeme().getLemma(), equalTo("etmek"));
            assertThat(roots.get(3).getLexeme().getPrimaryPos(), equalTo(PrimaryPos.Verb));
            assertThat(roots.get(3).getLexeme().getAttributes(), equalTo((Set<LexemeAttribute>) ImmutableSet.of(LexemeAttribute.Voicing, LexemeAttribute.Passive_Il)));
        }

        // skip voicing and {passive_InIl, passive_In}

    }

    @Override
    public List<DynamicRoot> findRootsForPartialInput(String partialInput, String completeInput) {
        final List<DynamicRoot> roots = new ArrayList<DynamicRoot>(super.findRootsForPartialInput(partialInput, completeInput));
        Collections.sort(roots, ROOT_COMPARATOR_BY_LEXEME);
        return roots;
    }
}
                
