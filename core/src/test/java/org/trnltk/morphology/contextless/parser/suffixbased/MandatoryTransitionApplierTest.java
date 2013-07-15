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

package org.trnltk.morphology.contextless.parser.suffixbased;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.*;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MandatoryTransitionApplier;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.morphotactics.BasicSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MandatoryTransitionApplierTest {

    MandatoryTransitionApplier transitionApplier;
    SuffixGraph suffixGraph;

    @Before
    public void setUp() throws Exception {
        suffixGraph = new BasicSuffixGraph();
        suffixGraph.initialize();

        SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(new SuffixFormSequenceApplier()));

        transitionApplier = new MandatoryTransitionApplier(suffixGraph, suffixApplier);
    }

    @Test
    public void shouldAddRequiredTransitions_forProgressiveVowelDrop() throws Exception {
        String rootStr = "at";
        String lemmaRootStr = "ata";
        String lemmaStr = "atamak";

        final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(new PhoneticsAnalyzer().calculatePhoneticAttributes(rootStr, null));
        final ImmutableSet<PhoneticExpectation> phoneticExpectations = ImmutableSet.of(PhoneticExpectation.VowelStart);

        final Lexeme lexeme = new ImmutableLexeme(lemmaStr, lemmaRootStr, PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.ProgressiveVowelDrop, LexemeAttribute.NoVoicing));

        final ImmutableRoot root = new ImmutableRoot(rootStr, lexeme, phoneticAttributes, phoneticExpectations);

        final MorphemeContainer morphemeContainer = new MorphemeContainer(root, this.suffixGraph.getSuffixGraphState("VERB_ROOT"), "ıyorum");

        final List<MorphemeContainer> retrievedList = transitionApplier.applyMandatoryTransitionsToMorphemeContainers(Arrays.asList(morphemeContainer), new TurkishSequence("atıyorum"));
        assertThat(retrievedList, hasSize(1));
        assertThat(retrievedList.get(0).getRoot(), Matchers.<Root>equalTo(root));

        assertThat(retrievedList.get(0).getSuffixTransitions(), hasSize(2));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(0).getSuffixFormApplication().getSuffixForm().getSuffix().getName(), equalTo("Pos"));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(0).getSuffixFormApplication().getSuffixForm().getForm().getSuffixFormStr(), equalTo(""));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(0).getSuffixFormApplication().getActualSuffixForm(), equalTo(""));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(0).getSuffixFormApplication().getFittingSuffixForm(), equalTo(""));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(1).getSuffixFormApplication().getSuffixForm().getSuffix().getName(), equalTo("Prog"));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(1).getSuffixFormApplication().getSuffixForm().getForm().getSuffixFormStr(), equalTo("Iyor"));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(1).getSuffixFormApplication().getActualSuffixForm(), equalTo("ıyor"));
        assertThat(retrievedList.get(0).getSuffixTransitions().get(1).getSuffixFormApplication().getFittingSuffixForm(), equalTo("ıyor"));
    }

    @Test
    public void shouldNotAddRequiredTransitions_forProgressiveVowelDrop_whenSynCatIsDifferent() throws Exception {
        String rootStr = "at";
        String lemmaRootStr = "ata";
        String lemmaStr = "atamak";

        final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(new PhoneticsAnalyzer().calculatePhoneticAttributes(rootStr, null));
        final ImmutableSet<PhoneticExpectation> phoneticExpectations = ImmutableSet.of(PhoneticExpectation.VowelStart);

        final Lexeme lexeme = new ImmutableLexeme(lemmaStr, lemmaRootStr, PrimaryPos.Noun, null, ImmutableSet.of(LexemeAttribute.ProgressiveVowelDrop, LexemeAttribute.NoVoicing));

        final ImmutableRoot root = new ImmutableRoot(rootStr, lexeme, phoneticAttributes, phoneticExpectations);

        final MorphemeContainer morphemeContainer = new MorphemeContainer(root, this.suffixGraph.getSuffixGraphState("VERB_ROOT"), "ıyorum");

        final List<MorphemeContainer> retrievedList = transitionApplier.applyMandatoryTransitionsToMorphemeContainers(Arrays.asList(morphemeContainer), new TurkishSequence("atıyorum"));
        assertThat(retrievedList, hasSize(1));
        assertThat(retrievedList.get(0), sameInstance(morphemeContainer));
    }

    @Test
    public void shouldNotAddRequiredTransitions_forProgressiveVowelDrop_whenLexemeAttrDoesntExist() throws Exception {
        String rootStr = "at";
        String lemmaRootStr = "ata";
        String lemmaStr = "atamak";

        final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(new PhoneticsAnalyzer().calculatePhoneticAttributes(rootStr, null));
        final ImmutableSet<PhoneticExpectation> phoneticExpectations = ImmutableSet.of(PhoneticExpectation.VowelStart);

        final Lexeme lexeme = new ImmutableLexeme(lemmaStr, lemmaRootStr, PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.NoVoicing));

        final ImmutableRoot root = new ImmutableRoot(rootStr, lexeme, phoneticAttributes, phoneticExpectations);

        final MorphemeContainer morphemeContainer = new MorphemeContainer(root, this.suffixGraph.getSuffixGraphState("VERB_ROOT"), "ıyorum");

        final List<MorphemeContainer> retrievedList = transitionApplier.applyMandatoryTransitionsToMorphemeContainers(Arrays.asList(morphemeContainer), new TurkishSequence("atıyorum"));
        assertThat(retrievedList, hasSize(1));
        assertThat(retrievedList.get(0), sameInstance(morphemeContainer));
    }

    @Test
    public void shouldNotAddRequiredTransitions_forProgressiveVowelDrop_whenRootStrIsNotProgressiveDropOne() throws Exception {
        String rootStr = "ata";
        String lemmaRootStr = "ata";
        String lemmaStr = "atamak";

        final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(new PhoneticsAnalyzer().calculatePhoneticAttributes(rootStr, null));
        final ImmutableSet<PhoneticExpectation> phoneticExpectations = ImmutableSet.of(PhoneticExpectation.VowelStart);

        final Lexeme lexeme = new ImmutableLexeme(lemmaStr, lemmaRootStr, PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.ProgressiveVowelDrop, LexemeAttribute.NoVoicing));

        final ImmutableRoot root = new ImmutableRoot(rootStr, lexeme, phoneticAttributes, phoneticExpectations);

        final MorphemeContainer morphemeContainer = new MorphemeContainer(root, this.suffixGraph.getSuffixGraphState("VERB_ROOT"), "ıyorum");

        final List<MorphemeContainer> retrievedList = transitionApplier.applyMandatoryTransitionsToMorphemeContainers(Arrays.asList(morphemeContainer), new TurkishSequence("atıyorum"));
        assertThat(retrievedList, hasSize(1));
        assertThat(retrievedList.get(0), sameInstance(morphemeContainer));
    }

    @Test
    public void shouldNotAddRequiredTransitions_forProgressiveVowelDrop_whenVoicingIsAccidentallyApplied() throws Exception {
        String rootStr = "at";
        String lemmaRootStr = "ata";
        String lemmaStr = "atamak";

        final ImmutableSet<PhoneticAttribute> phoneticAttributes = Sets.immutableEnumSet(new PhoneticsAnalyzer().calculatePhoneticAttributes(rootStr, null));
        final ImmutableSet<PhoneticExpectation> phoneticExpectations = ImmutableSet.of(PhoneticExpectation.VowelStart);

        final Lexeme lexeme = new ImmutableLexeme(lemmaStr, lemmaRootStr, PrimaryPos.Verb, null, ImmutableSet.of(LexemeAttribute.ProgressiveVowelDrop));

        final ImmutableRoot root = new ImmutableRoot(rootStr, lexeme, phoneticAttributes, phoneticExpectations);

        final MorphemeContainer morphemeContainer = new MorphemeContainer(root, this.suffixGraph.getSuffixGraphState("VERB_ROOT"), "ıyorum");

        final List<MorphemeContainer> retrievedList = transitionApplier.applyMandatoryTransitionsToMorphemeContainers(Arrays.asList(morphemeContainer), new TurkishSequence("atıyorum"));
        assertThat(retrievedList, hasSize(0));
    }
}
