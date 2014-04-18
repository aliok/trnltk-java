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

package org.trnltk.experiment.morphology.ambiguity;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.junit.Test;
import org.trnltk.experiment.model.ambiguity.morphology.ParseResult;
import org.trnltk.experiment.model.ambiguity.morphology.ParseResultDifference;
import org.trnltk.experiment.model.ambiguity.morphology.ParseResultPartDifference;
import org.trnltk.experiment.model.ambiguity.morphology.RootDifference;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ParseResultDiffToolTest {

    ParseResultDiffTool diffTool = new ParseResultDiffTool();
    ParseResultReader parseResultReader = new ParseResultReader();

    ParseResult ev_in__P2sg = createParseResultObject("{'Parts':[{'POS':'Noun','Suffixes':['A3sg','P2sg','Nom']}],'LemmaRoot':'ev','RootPos':'Noun','Root':'ev'}");
    ParseResult ev_in__Pnon = createParseResultObject("{'Parts':[{'POS':'Noun','Suffixes':['A3sg','Pnon','Gen']}],'LemmaRoot':'ev','RootPos':'Noun','Root':'ev'}");
    ParseResult evin__Nom = createParseResultObject("{'Parts':[{'POS':'Noun','Suffixes':['A3sg','Pnon','Nom']}],'LemmaRoot':'evin','RootPos':'Noun','Root':'evin'}");
    ParseResult ev_in__P2sg_Verb = createParseResultObject("{'Parts':[{'POS':'Noun','Suffixes':['A3sg','P2sg','Nom']},{'POS':'Verb','Suffixes':['Zero','Pres','A3sg']}],'LemmaRoot':'ev','RootPos':'Noun','Root':'ev'}");
    ParseResult evin__Nom_Prop = createParseResultObject("{'Parts':[{'POS':'Noun','Suffixes':['A3sg','Pnon','Nom']}],'LemmaRoot':'Evin','RootPos':'Noun','Root':'Evin','RootSpos':'Prop'}");

    ParseResult fake_1 = createParseResultObject("{'Parts':[{'POS':'Verb','Suffixes':['Pos']},{'POS':'Adj','Suffixes':['PastPart','P3sg']}],'LemmaRoot':'xxx','RootPos':'Verb','Root':'xxx'}");
    ParseResult fake_2 = createParseResultObject("{'Parts':[{'POS':'Verb','Suffixes':['Pos']},{'POS':'Sth','Suffixes':['Fake']},{'POS':'Adj','Suffixes':['PastPart','P3sg']}],'LemmaRoot':'xxx','RootPos':'Verb','Root':'xxx'}");


    public ParseResultDiffToolTest() throws JSONException {
    }


    @Test
    public void shouldFindDiff_sameRoot_sameLength_differentLastPart() throws Exception {
        final ParseResultDifference difference = diffTool.findDifference(ev_in__P2sg, ev_in__Pnon);
        final List<ParseResultPartDifference> parseResultPartDifferences = difference.getParseResultPartDifferences();

        assertThat(difference.hasNoRootDifference(), equalTo(true));
        assertThat(parseResultPartDifferences, hasSize(1));

        final ParseResultPartDifference parseResultPartDifference = parseResultPartDifferences.get(0);

        assertThat(parseResultPartDifference.getParts().getLeft().toString(), equalTo("[ParseResultPart{primaryPos='Noun', secondaryPos='null', suffixes=[A3sg, P2sg, Nom]}]"));
        assertThat(parseResultPartDifference.getParts().getRight().toString(), equalTo("[ParseResultPart{primaryPos='Noun', secondaryPos='null', suffixes=[A3sg, Pnon, Gen]}]"));
    }

    @Test
    public void shouldFindDiff_differentRoot_sameLength_differentLastPart_sc1() throws Exception {
        final ParseResultDifference difference = diffTool.findDifference(ev_in__P2sg, evin__Nom);
        final RootDifference rootDifference = difference.getRootDifference();
        final List<ParseResultPartDifference> parseResultPartDifferences = difference.getParseResultPartDifferences();

        assertThat(rootDifference.getRootStrDiff(), equalTo(Pair.of("ev", "evin")));
        assertThat(rootDifference.getLemmaRootStrDiff(), equalTo(Pair.of("ev", "evin")));
        assertThat(rootDifference.getPosDiff(), nullValue());
        assertThat(rootDifference.getSposDiff(), nullValue());

        assertThat(parseResultPartDifferences, hasSize(1));

        final ParseResultPartDifference parseResultPartDifference = parseResultPartDifferences.get(0);

        assertThat(parseResultPartDifference.getParts().getLeft().toString(), equalTo("[ParseResultPart{primaryPos='Noun', secondaryPos='null', suffixes=[A3sg, P2sg, Nom]}]"));
        assertThat(parseResultPartDifference.getParts().getRight().toString(), equalTo("[ParseResultPart{primaryPos='Noun', secondaryPos='null', suffixes=[A3sg, Pnon, Nom]}]"));
    }

    @Test
    public void shouldFindDiff_differentRoot_sameLength_differentLastPart_sc2() throws Exception {
        final ParseResultDifference difference = diffTool.findDifference(ev_in__Pnon, evin__Nom);
        final RootDifference rootDifference = difference.getRootDifference();
        final List<ParseResultPartDifference> parseResultPartDifferences = difference.getParseResultPartDifferences();

        assertThat(rootDifference.getRootStrDiff(), equalTo(Pair.of("ev", "evin")));
        assertThat(rootDifference.getLemmaRootStrDiff(), equalTo(Pair.of("ev", "evin")));
        assertThat(rootDifference.getPosDiff(), nullValue());
        assertThat(rootDifference.getSposDiff(), nullValue());

        assertThat(parseResultPartDifferences, hasSize(1));

        final ParseResultPartDifference parseResultPartDifference = parseResultPartDifferences.get(0);

        assertThat(parseResultPartDifference.getParts().getLeft().toString(), equalTo("[ParseResultPart{primaryPos='Noun', secondaryPos='null', suffixes=[A3sg, Pnon, Gen]}]"));
        assertThat(parseResultPartDifference.getParts().getRight().toString(), equalTo("[ParseResultPart{primaryPos='Noun', secondaryPos='null', suffixes=[A3sg, Pnon, Nom]}]"));
    }

    @Test
    public void shouldFindDiff_differentSpos() throws Exception {
        final ParseResultDifference difference = diffTool.findDifference(evin__Nom, evin__Nom_Prop);
        final RootDifference rootDifference = difference.getRootDifference();
        final List<ParseResultPartDifference> parseResultPartDifferences = difference.getParseResultPartDifferences();

        assertThat(rootDifference.getRootStrDiff(), equalTo(Pair.of("evin", "Evin")));
        assertThat(rootDifference.getLemmaRootStrDiff(), equalTo(Pair.of("evin", "Evin")));
        assertThat(rootDifference.getPosDiff(), nullValue());
        assertThat(rootDifference.getSposDiff(), equalTo(Pair.<String, String>of(null, "Prop")));

        assertThat(parseResultPartDifferences, hasSize(0));
    }

    @Test
    public void shouldFindDiff_sameRoot_withOneAdditionalPart_atTheEnd() throws Exception {
        final ParseResultDifference difference = diffTool.findDifference(ev_in__P2sg, ev_in__P2sg_Verb);
        final List<ParseResultPartDifference> parseResultPartDifferences = difference.getParseResultPartDifferences();

        assertThat(difference.hasNoRootDifference(), equalTo(true));
        assertThat(parseResultPartDifferences, hasSize(1));

        final ParseResultPartDifference parseResultPartDifference = parseResultPartDifferences.get(0);

        assertThat(parseResultPartDifference.getParts().getLeft(), nullValue());
        assertThat(parseResultPartDifference.getParts().getRight().toString(), equalTo("[ParseResultPart{primaryPos='Verb', secondaryPos='null', suffixes=[Zero, Pres, A3sg]}]"));
    }

    @Test
    public void shouldFindDiff_sameRoot_withOneAdditionalPart_inTheMiddle() throws Exception {
        final ParseResultDifference difference = diffTool.findDifference(fake_1, fake_2);
        final RootDifference rootDifference = difference.getRootDifference();
        final List<ParseResultPartDifference> parseResultPartDifferences = difference.getParseResultPartDifferences();

        assertThat(rootDifference, nullValue());

        assertThat(parseResultPartDifferences, hasSize(1));

        final ParseResultPartDifference parseResultPartDifference = parseResultPartDifferences.get(0);

        assertThat(parseResultPartDifference.getParts().getLeft(), nullValue());
        assertThat(parseResultPartDifference.getParts().getRight().toString(), equalTo("[ParseResultPart{primaryPos='Sth', secondaryPos='null', suffixes=[Fake]}]"));
    }

    @Test
    public void orderShouldNotMatterWhileFindingDifference() {
        final List<ParseResult> parseResults = getDeclaredParseResults();

        for (int i = 0; i < parseResults.size(); i++) {
            final ParseResult leftParseResult = parseResults.get(i);

            for (int j = i; j < parseResults.size(); j++) {
                final ParseResult rightParseResult = parseResults.get(j);

                final ParseResultDifference differenceA = this.diffTool.findDifference(leftParseResult, rightParseResult);
                final ParseResultDifference differenceB = this.diffTool.findDifference(rightParseResult, leftParseResult);

                assertThat(differenceA, equalTo(differenceB));
            }
        }
    }

    @Test
    public void shouldNotFindDifferenceOnSameItems() {
        final List<ParseResult> parseResults = getDeclaredParseResults();
        for (int i = 0; i < parseResults.size(); i++) {
            final ParseResult parseResult = parseResults.get(i);
            final ParseResultDifference difference = this.diffTool.findDifference(parseResult, parseResult);
            assertThat(difference.hasNoPartDifference(), equalTo(true));
            assertThat(difference.hasNoPartDifference(), equalTo(true));
        }
    }

    private List<ParseResult> getDeclaredParseResults() {
        final Field[] declaredFields = this.getClass().getDeclaredFields();
        final Iterable<Field> parseResultFields = Iterables.filter(Lists.newArrayList(declaredFields), new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return input.getType().isAssignableFrom(ParseResult.class);
            }
        });

        return Lists.newArrayList(Iterables.transform(parseResultFields, new Function<Field, ParseResult>() {
            @Override
            public ParseResult apply(Field input) {
                try {
                    return (ParseResult) input.get(ParseResultDiffToolTest.this);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    private ParseResult createParseResultObject(String s) throws JSONException {
        return parseResultReader.createParseResultObject(s);
    }
}
