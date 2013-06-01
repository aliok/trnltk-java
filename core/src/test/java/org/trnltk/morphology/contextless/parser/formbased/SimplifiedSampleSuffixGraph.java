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

package org.trnltk.morphology.contextless.parser.formbased;

import com.google.common.collect.ImmutableMap;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.suffixbased.Suffix;
import org.trnltk.morphology.model.suffixbased.SuffixGroup;
import org.trnltk.morphology.morphotactics.BaseSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.Collection;

import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TERMINAL;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TRANSFER;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.comesAfter;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.doesntComeAfter;
import static org.trnltk.morphology.model.lexicon.PrimaryPos.Verb;

public class SimplifiedSampleSuffixGraph extends BaseSuffixGraph {

    private final SuffixGraphState VERB_ROOT = registerState("VERB_ROOT", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_POLARITY = registerState("VERB_WITH_POLARITY", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_TENSE = registerState("VERB_WITH_TENSE", TRANSFER, Verb);
    private final SuffixGraphState VERB_TERMINAL = registerState("VERB_TERMINAL", TERMINAL, Verb);
    private final SuffixGraphState VERB_TERMINAL_TRANSFER = registerState("VERB_TERMINAL_TRANSFER", TRANSFER, Verb);

    /////////////// Verb conditions
    private final SuffixGroup Verb_Polarity_Group = new SuffixGroup("Verb_Conditions_Group");
    private final Suffix Negative = registerSuffix("Neg", Verb_Polarity_Group);
    private final Suffix Positive = registerSuffix("Pos", Verb_Polarity_Group);

    /////////////// Verb agreements
    private final SuffixGroup Verb_Agreements_Group = new SuffixGroup("Verb_Agreements_Group");
    private final Suffix A1Pl_Verb = registerSuffix("A1Pl_Verb", Verb_Agreements_Group, "A1pl");
    private final Suffix A2Pl_Verb = registerSuffix("A2Pl_Verb", Verb_Agreements_Group, "A2pl");

    /////////////// Verbal tenses
    private final Suffix Aorist = registerSuffix("Aor");
    private final Suffix Progressive = registerSuffix("Prog");
    private final Suffix Past = registerSuffix("Past");

    @Override
    protected void registerEverything() {
        this.registerFreeTransitions();
        this.createSuffixEdges();
    }

    private void registerFreeTransitions() {
        ///////////////  Free transitions
        VERB_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_5"), VERB_TERMINAL);
    }

    private void createSuffixEdges() {
        registerVerbSuffixes();
    }

    private void registerVerbSuffixes() {
        registerVerbAgreements();
        registerVerbPolarisations();
        registerVerbTenses();
    }

    private void registerVerbPolarisations() {
        VERB_ROOT.addOutSuffix(Negative, VERB_WITH_POLARITY);
        Negative.addSuffixForm("mA");

        VERB_ROOT.addOutSuffix(Positive, VERB_WITH_POLARITY);
        Positive.addSuffixForm("");
    }

    private void registerVerbTenses() {
        Aorist.addSuffixForm("+Ar", doesntComeAfter(Negative));

        Progressive.addSuffixForm("Iyor");

        Past.addSuffixForm("dI");

        VERB_WITH_POLARITY.addOutSuffix(Aorist, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Progressive, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Past, VERB_WITH_TENSE);
    }

    private void registerVerbAgreements() {
        VERB_WITH_TENSE.addOutSuffix(A1Pl_Verb, VERB_TERMINAL_TRANSFER);
        A1Pl_Verb.addSuffixForm("+Iz");
        A1Pl_Verb.addSuffixForm("!k", comesAfter(Past));   // only for "gel-di-k", "gelmis mi-ydi-k" or "gelsek"

        VERB_WITH_TENSE.addOutSuffix(A2Pl_Verb, VERB_TERMINAL_TRANSFER);
        A2Pl_Verb.addSuffixForm("sInIz");
        A2Pl_Verb.addSuffixForm("nIz");
    }

    private final ImmutableMap<PrimaryPos, SuffixGraphState> rootStateMap = new ImmutableMap.Builder<PrimaryPos, SuffixGraphState>()
//            .put(Noun, NOUN_ROOT)
            .put(Verb, VERB_ROOT)
//            .put(Adverb, ADVERB_ROOT)
//            .put(Adjective, ADJECTIVE_ROOT)
//            .put(Pronoun, PRONOUN_ROOT)
//            .put(Determiner, DETERMINER_ROOT_TERMINAL)
//            .put(Interjection, INTERJECTION_ROOT_TERMINAL)
//            .put(Conjunction, CONJUNCTION_ROOT_TERMINAL)
//            .put(Punctuation, PUNC_ROOT_TERMINAL)
//            .put(Duplicator, DUP_ROOT_TERMINAL)
//            .put(PostPositive, POSTP_ROOT_TERMINAL)          //TODO-INTEGRATION: particle or postpositive?
//            .put(Question, QUESTION_ROOT)
            .build();

    @Override
    protected SuffixGraphState doGetDefaultStateForRoot(Root root) {
        final SuffixGraphState defaultState = this.rootStateMap.get(root.getLexeme().getPrimaryPos());
        if (defaultState == null)
            return null;

        return defaultState;

    }

    @Override
    protected Collection<? extends SuffixGraphState> doGetRootSuffixGraphStates() {
        return this.rootStateMap.values();
    }
}