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

package org.trnltk.morphology.contextless.parser.parsing;

import com.google.common.collect.ImmutableMap;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.suffix.SuffixGroup;
import org.trnltk.morphology.morphotactics.BaseSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;
import org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications;
import org.trnltk.common.specification.Specification;
import org.trnltk.common.specification.Specifications;
import org.trnltk.common.specification.TrueSpecification;
import org.trnltk.model.lexicon.PrimaryPos;

import java.util.Collection;

import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.*;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.*;
import static org.trnltk.model.lexicon.PrimaryPos.Noun;
import static org.trnltk.model.lexicon.PrimaryPos.Verb;

public class SampleSuffixGraph extends BaseSuffixGraph {

    private final SuffixGraphState NOUN_ROOT = registerState("NOUN_ROOT", TRANSFER, Noun);
    private final SuffixGraphState NOUN_WITH_AGREEMENT = registerState("NOUN_WITH_AGREEMENT", TRANSFER, Noun);
    private final SuffixGraphState NOUN_WITH_POSSESSION = registerState("NOUN_WITH_POSSESSION", TRANSFER, Noun);
    private final SuffixGraphState NOUN_WITH_CASE = registerState("NOUN_WITH_CASE", TRANSFER, Noun);
    private final SuffixGraphState NOUN_TERMINAL_TRANSFER = registerState("NOUN_TERMINAL_TRANSFER", TRANSFER, Noun);
    private final SuffixGraphState NOUN_TERMINAL = registerState("NOUN_TERMINAL", TERMINAL, Noun);
    private final SuffixGraphState NOUN_NOM_DERIV = registerState("NOUN_NOM_DERIV", DERIVATIONAL, Noun);
    private final SuffixGraphState NOUN_POSSESSIVE_NOM_DERIV = registerState("NOUN_POSSESSIVE_NOM_DERIV", DERIVATIONAL, Noun);
    private final SuffixGraphState NOUN_DERIV_WITH_CASE = registerState("NOUN_DERIV_WITH_CASE", DERIVATIONAL, Noun);

    private final SuffixGraphState VERB_ROOT = registerState("VERB_ROOT", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_POLARITY = registerState("VERB_WITH_POLARITY", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_TENSE = registerState("VERB_WITH_TENSE", TRANSFER, Verb);
    private final SuffixGraphState VERB_TERMINAL = registerState("VERB_TERMINAL", TERMINAL, Verb);
    private final SuffixGraphState VERB_TERMINAL_TRANSFER = registerState("VERB_TERMINAL_TRANSFER", TRANSFER, Verb);
    private final SuffixGraphState VERB_PLAIN_DERIV = registerState("VERB_PLAIN_DERIV", DERIVATIONAL, Verb);
    private final SuffixGraphState VERB_POLARITY_DERIV = registerState("VERB_POLARITY_DERIV", DERIVATIONAL, Verb);
    private final SuffixGraphState VERB_WITH_TENSE_BEFORE_DERIV = registerState("VERB_WITH_TENSE_BEFORE_DERIV", TRANSFER, Verb);
    private final SuffixGraphState VERB_TENSE_DERIV = registerState("VERB_TENSE_DERIV", DERIVATIONAL, Verb);

    ///////////////  Noun Agreements
    private final SuffixGroup Noun_Agreements_Group = new SuffixGroup("Noun_Agreements_Group");
    private final Suffix A3Sg_Noun = registerSuffix("A3Sg_Noun", Noun_Agreements_Group, "A3sg");
    private final Suffix A3Pl_Noun = registerSuffix("A3Pl_Noun", Noun_Agreements_Group, "A3pl");

    ///////////////  Possessive agreements
    private final SuffixGroup Noun_Possessions_Group = new SuffixGroup("Noun_Possession_Group");

    private final Suffix Pnon_Noun = registerSuffix("Pnon_Noun", Noun_Possessions_Group, "Pnon");
    private final Suffix P1Sg_Noun = registerSuffix("P1Sg_Noun", Noun_Possessions_Group, "P1sg");
    private final Suffix P2Sg_Noun = registerSuffix("P2Sg_Noun", Noun_Possessions_Group, "P2sg");
    private final Suffix P3Sg_Noun = registerSuffix("P3Sg_Noun", Noun_Possessions_Group, "P3sg");
    private final Suffix P1Pl_Noun = registerSuffix("P1Pl_Noun", Noun_Possessions_Group, "P1pl");
    private final Suffix P2Pl_Noun = registerSuffix("P2Pl_Noun", Noun_Possessions_Group, "P2pl");
    private final Suffix P3Pl_Noun = registerSuffix("P3Pl_Noun", Noun_Possessions_Group, "P3pl");

    ///////////////  Noun cases
    private final SuffixGroup Noun_Cases_Group = new SuffixGroup("Noun_Case_Group");

    private final Suffix Nom_Noun = registerSuffix("Nom_Noun", Noun_Cases_Group, "Nom");
    private final Suffix Nom_Noun_Deriv = registerSuffix("Nom_Deriv_Noun", Noun_Cases_Group, "Nom");
    private final Suffix Nom_Noun_Possessive_Deriv = registerSuffix("Nom_Deriv_Possessive_Noun", Noun_Cases_Group, "Nom");
    private final Suffix Acc_Noun = registerSuffix("Acc_Noun", Noun_Cases_Group, "Acc");
    private final Suffix Dat_Noun = registerSuffix("Dat_Noun", Noun_Cases_Group, "Dat");
    private final Suffix Loc_Noun = registerSuffix("Loc_Noun", Noun_Cases_Group, "Loc");
    private final Suffix Abl_Noun = registerSuffix("Abl_Noun", Noun_Cases_Group, "Abl");

    private final Suffix Gen_Noun = registerSuffix("Gen_Noun", Noun_Cases_Group, "Gen");
    private final Suffix Ins_Noun = registerSuffix("Ins_Noun", Noun_Cases_Group, "Ins");

    /////////////// Noun to Noun derivations
    private final Suffix Dim = registerSuffix("Dim");
    private final Suffix Prof = registerSuffix("Prof");
    private final Suffix FitFor = registerSuffix("FitFor");
    private final Suffix Title = registerSuffix("Title");

    /////////////// Verb conditions
    private final SuffixGroup Verb_Polarity_Group = new SuffixGroup("Verb_Conditions_Group");
    private final Suffix Negative = registerSuffix("Neg", Verb_Polarity_Group);
    private final Suffix Positive = registerSuffix("Pos", Verb_Polarity_Group);

    /////////////// Verb agreements
    private final SuffixGroup Verb_Agreements_Group = new SuffixGroup("Verb_Agreements_Group");
    private final Suffix A1Sg_Verb = registerSuffix("A1Sg_Verb", Verb_Agreements_Group, "A1sg");
    private final Suffix A2Sg_Verb = registerSuffix("A2Sg_Verb", Verb_Agreements_Group, "A2sg");
    private final Suffix A3Sg_Verb = registerSuffix("A3Sg_Verb", Verb_Agreements_Group, "A3sg");
    private final Suffix A1Pl_Verb = registerSuffix("A1Pl_Verb", Verb_Agreements_Group, "A1pl");
    private final Suffix A2Pl_Verb = registerSuffix("A2Pl_Verb", Verb_Agreements_Group, "A2pl");
    private final Suffix A3Pl_Verb = registerSuffix("A3Pl_Verb", Verb_Agreements_Group, "A3pl");

    /////////////// Verbal tenses
    private final Suffix Aorist = registerSuffix("Aor");
    private final Suffix Progressive = registerSuffix("Prog");
    private final Suffix Future = registerSuffix("Fut");
    private final Suffix Narr = registerSuffix("Narr");
    private final Suffix Past = registerSuffix("Past");
    private final Suffix Pres = registerSuffix("Pres");

    @Override
    protected void registerEverything() {
        this.registerFreeTransitions();
        this.createSuffixEdges();
    }

    private void registerFreeTransitions() {
        ///////////////  Free transitions
        NOUN_WITH_CASE.addOutSuffix(registerFreeTransitionSuffix("Noun_Free_Transition_1"), NOUN_TERMINAL_TRANSFER);
        NOUN_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Noun_Free_Transition_2"), NOUN_TERMINAL);
        NOUN_WITH_CASE.addOutSuffix(registerFreeTransitionSuffix("Noun_Free_Transition_3"), NOUN_DERIV_WITH_CASE);

        VERB_ROOT.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_1"), VERB_PLAIN_DERIV);
        VERB_WITH_POLARITY.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_2"), VERB_POLARITY_DERIV);
        VERB_WITH_TENSE.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_3"), VERB_WITH_TENSE_BEFORE_DERIV);
        VERB_WITH_TENSE_BEFORE_DERIV.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_4"), VERB_TENSE_DERIV);
        VERB_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_5"), VERB_TERMINAL);
    }

    private void createSuffixEdges() {
        registerNounSuffixes();
        registerVerbSuffixes();
    }

    private void registerNounSuffixes() {
        registerNounAgreements();
        registerPossessiveAgreements();
        registerNounCases();
        registerNounToNounDerivations();
    }

    private void registerVerbSuffixes() {
        registerVerbAgreements();
        registerVerbPolarisations();
        registerVerbTenses();
    }

    private void registerNounAgreements() {
        NOUN_ROOT.addOutSuffix(A3Sg_Noun, NOUN_WITH_AGREEMENT);
        A3Sg_Noun.addSuffixForm("");

        NOUN_ROOT.addOutSuffix(A3Pl_Noun, NOUN_WITH_AGREEMENT);
        A3Pl_Noun.addSuffixForm("lAr");
    }

    private void registerPossessiveAgreements() {
        final Specification<MorphemeContainer> doesnt_come_after_PointerQual = TrueSpecification.INSTANCE;

        NOUN_WITH_AGREEMENT.addOutSuffix(Pnon_Noun, NOUN_WITH_POSSESSION);
        Pnon_Noun.addSuffixForm("");

        NOUN_WITH_AGREEMENT.addOutSuffix(P1Sg_Noun, NOUN_WITH_POSSESSION);
        P1Sg_Noun.addSuffixForm("+Im", doesnt_come_after_PointerQual);

        NOUN_WITH_AGREEMENT.addOutSuffix(P2Sg_Noun, NOUN_WITH_POSSESSION);
        P2Sg_Noun.addSuffixForm("+In", doesnt_come_after_PointerQual);

        NOUN_WITH_AGREEMENT.addOutSuffix(P3Sg_Noun, NOUN_WITH_POSSESSION);
        P3Sg_Noun.addSuffixForm("+sI", doesnt_come_after_PointerQual);

        NOUN_WITH_AGREEMENT.addOutSuffix(P1Pl_Noun, NOUN_WITH_POSSESSION);
        P1Pl_Noun.addSuffixForm("+ImIz", doesnt_come_after_PointerQual);

        NOUN_WITH_AGREEMENT.addOutSuffix(P2Pl_Noun, NOUN_WITH_POSSESSION);
        P2Pl_Noun.addSuffixForm("+InIz", doesnt_come_after_PointerQual);

        NOUN_WITH_AGREEMENT.addOutSuffix(P3Pl_Noun, NOUN_WITH_POSSESSION);
        P3Pl_Noun.addSuffixForm("lAr!I", doesnt_come_after_PointerQual);
        P3Pl_Noun.addSuffixForm("!I", comesAfter(A3Pl_Noun).and(doesnt_come_after_PointerQual));
    }

    private void registerNounCases() {
        final Specification<MorphemeContainer> comesAfterP3 = Specifications.or(
                comesAfter(P3Sg_Noun),
                comesAfter(P3Pl_Noun)
        );

        final Specification<MorphemeContainer> doesntComeAfterP3 = comesAfterP3.not();

        final Specification<MorphemeContainer> preconditionForConsonantInsertion_Y = doesntComeAfterP3;

        // kitabi-ni or kitaptaki-ni
        final Specification<MorphemeContainer> preconditionForConsonantInsertion_N = comesAfterP3;

        NOUN_WITH_POSSESSION.addOutSuffix(Nom_Noun, NOUN_WITH_CASE);
        Nom_Noun.addSuffixForm("");

        NOUN_WITH_POSSESSION.addOutSuffix(Nom_Noun_Deriv, NOUN_NOM_DERIV);
        Nom_Noun_Deriv.addSuffixForm("", comesAfter(Pnon_Noun));

        NOUN_WITH_POSSESSION.addOutSuffix(Nom_Noun_Possessive_Deriv, NOUN_POSSESSIVE_NOM_DERIV);
        Nom_Noun_Possessive_Deriv.addSuffixForm("", doesntComeAfter(Pnon_Noun));

        NOUN_WITH_POSSESSION.addOutSuffix(Acc_Noun, NOUN_WITH_CASE);
        Acc_Noun.addSuffixForm("+yI", preconditionForConsonantInsertion_Y);
        Acc_Noun.addSuffixForm("nI", preconditionForConsonantInsertion_N);

        NOUN_WITH_POSSESSION.addOutSuffix(Dat_Noun, NOUN_WITH_CASE);
        Dat_Noun.addSuffixForm("+yA", preconditionForConsonantInsertion_Y);
        Dat_Noun.addSuffixForm("nA", preconditionForConsonantInsertion_N);

        NOUN_WITH_POSSESSION.addOutSuffix(Loc_Noun, NOUN_WITH_CASE);
        Loc_Noun.addSuffixForm("dA", preconditionForConsonantInsertion_Y);
        Loc_Noun.addSuffixForm("ndA", preconditionForConsonantInsertion_N);

        NOUN_WITH_POSSESSION.addOutSuffix(Abl_Noun, NOUN_WITH_CASE);
        Abl_Noun.addSuffixForm("dAn", preconditionForConsonantInsertion_Y);
        Abl_Noun.addSuffixForm("ndAn", preconditionForConsonantInsertion_N);

        NOUN_WITH_POSSESSION.addOutSuffix(Gen_Noun, NOUN_WITH_CASE);
        Gen_Noun.addSuffixForm("+nIn");

        NOUN_WITH_POSSESSION.addOutSuffix(Ins_Noun, NOUN_WITH_CASE);
        Ins_Noun.addSuffixForm("+ylA");

    }

    private void registerNounToNounDerivations() {
        NOUN_NOM_DERIV.addOutSuffix(Dim, NOUN_ROOT);
        Dim.addSuffixForm("cIk", SuffixFormSpecifications.doesntComeAfter(A3Pl_Noun));

        NOUN_NOM_DERIV.addOutSuffix(Prof, NOUN_ROOT);
        Prof.addSuffixForm("lIk");

        NOUN_NOM_DERIV.addOutSuffix(FitFor, NOUN_ROOT);
        FitFor.addSuffixForm("lIk");

        NOUN_NOM_DERIV.addOutSuffix(Title, NOUN_ROOT);
        Title.addSuffixForm("lIk");
    }

    private void registerVerbPolarisations() {
        VERB_ROOT.addOutSuffix(Negative, VERB_WITH_POLARITY);
        Negative.addSuffixForm("m", null, doesnt(followedBySuffixGoesTo(SuffixGraphStateType.DERIVATIONAL)));
        Negative.addSuffixForm("mA");

        VERB_ROOT.addOutSuffix(Positive, VERB_WITH_POLARITY);
        Positive.addSuffixForm("");
    }

    private void registerVerbTenses() {
        final Specification<MorphemeContainer> followed_by_A1Sg_A1Pl = followedBy(A1Sg_Verb, "+Im").or(followedBy(A1Pl_Verb, "yIz"));

        Aorist.addSuffixForm("+Ir", hasLexemeAttributes(LexemeAttribute.Aorist_I).and(doesntComeAfter(Negative)));
        Aorist.addSuffixForm("+Ar", doesntComeAfter(Negative));
        Aorist.addSuffixForm("z", comesAfter(Negative), doesnt(followed_by_A1Sg_A1Pl));    // gel-me-z or gel-me-z-sin
        Aorist.addSuffixForm("", comesAfter(Negative), followed_by_A1Sg_A1Pl);     // gel-me-m or gel-me-yiz

        Progressive.addSuffixForm("Iyor");
        Progressive.addSuffixForm("mAktA");

        Future.addSuffixForm("+yAcAk");

        Narr.addSuffixForm("mIş");
        Narr.addSuffixForm("ymIş");

        Past.addSuffixForm("dI");
        Past.addSuffixForm("ydI");

        Pres.addSuffixForm("");

        VERB_WITH_POLARITY.addOutSuffix(Aorist, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Progressive, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Future, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Narr, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Past, VERB_WITH_TENSE);

        VERB_WITH_TENSE.addOutSuffix(Narr, VERB_WITH_TENSE);
        VERB_WITH_TENSE.addOutSuffix(Past, VERB_WITH_TENSE);
    }

    private void registerVerbAgreements() {
        VERB_WITH_TENSE.addOutSuffix(A1Sg_Verb, VERB_TERMINAL_TRANSFER);
        A1Sg_Verb.addSuffixForm("+Im");
        A1Sg_Verb.addSuffixForm("yIm");   //"yap-makta-yım", gel-meli-yim

        VERB_WITH_TENSE.addOutSuffix(A2Sg_Verb, VERB_TERMINAL_TRANSFER);
        A2Sg_Verb.addSuffixForm("sIn");

        VERB_WITH_TENSE.addOutSuffix(A3Sg_Verb, VERB_TERMINAL_TRANSFER);
        A3Sg_Verb.addSuffixForm("");

        VERB_WITH_TENSE.addOutSuffix(A1Pl_Verb, VERB_TERMINAL_TRANSFER);
        A1Pl_Verb.addSuffixForm("+Iz");
        A1Pl_Verb.addSuffixForm("!k", Specifications.or(comesAfter(Past)));   // only for "gel-di-k", "gelmis mi-ydi-k" or "gelsek"
        A1Pl_Verb.addSuffixForm("yIz");   // "yap-makta-yız" OR "gel-me-yiz"

        VERB_WITH_TENSE.addOutSuffix(A2Pl_Verb, VERB_TERMINAL_TRANSFER);
        A2Pl_Verb.addSuffixForm("sInIz");
        A2Pl_Verb.addSuffixForm("nIz");

        VERB_WITH_TENSE.addOutSuffix(A3Pl_Verb, VERB_TERMINAL_TRANSFER);
        A3Pl_Verb.addSuffixForm("lAr");
    }

    private final ImmutableMap<PrimaryPos, SuffixGraphState> rootStateMap = new ImmutableMap.Builder<PrimaryPos, SuffixGraphState>()
            .put(Noun, NOUN_ROOT)
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