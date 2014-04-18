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

package org.trnltk.morphology.morphotactics;

import org.trnltk.model.suffix.Suffix;
import org.trnltk.common.specification.Specification;
import org.trnltk.common.specification.Specifications;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.suffix.SuffixGroup;
import org.trnltk.model.lexicon.PrimaryPos;

import java.util.Arrays;
import java.util.Collection;

import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.DERIVATIONAL;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TRANSFER;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.comesAfter;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.doesntComeAfter;
import static org.trnltk.model.lexicon.PrimaryPos.*;

@SuppressWarnings("WeakerAccess")
public class CopulaSuffixGraph extends BaseSuffixGraph {
    private final SuffixGraphState NOUN_COPULA = registerState("NOUN_COPULA", DERIVATIONAL, Noun);
    private final SuffixGraphState ADJECTIVE_COPULA = registerState("ADJECTIVE_COPULA", DERIVATIONAL, Adjective);
    private final SuffixGraphState ADVERB_COPULA = registerState("ADVERB_COPULA", DERIVATIONAL, Adverb);
    private final SuffixGraphState PRONOUN_COPULA = registerState("PRONOUN_COPULA", DERIVATIONAL, Pronoun);

    private final SuffixGraphState VERB_DEGIL_ROOT = registerState("VERB_DEGIL_ROOT", TRANSFER, Verb);

    private final SuffixGraphState VERB_COPULA_WITHOUT_TENSE = registerState("VERB_COPULA_WITHOUT_TENSE", TRANSFER, Verb);
    private final SuffixGraphState VERB_COPULA_WITHOUT_TENSE_DERIV = registerState("VERB_COPULA_WITHOUT_TENSE_DERIV", DERIVATIONAL, Verb);
    private final SuffixGraphState VERB_COPULA_WITH_TENSE = registerState("VERB_COPULA_WITH_TENSE", TRANSFER, Verb);
    private final SuffixGraphState VERB_COPULA_WITH_TENSE_DERIV = registerState("VERB_COPULA_WITH_TENSE_DERIV", DERIVATIONAL, Verb);
    private final SuffixGraphState VERB_COPULA_WITH_SWAPPED_A3PL = registerState("VERB_COPULA_WITH_SWAPPED_A3PL", TRANSFER, Verb);
    private final SuffixGraphState VERB_COPULA_FROM_OTHERS_WITH_SWAPPED_A3PL = registerState("VERB_COPULA_FROM_OTHERS_WITH_SWAPPED_A3PL", DERIVATIONAL, Verb);

    /// from decorated
    private final SuffixGraphState DECORATED_ADJECTIVE_DERIV = getSuffixGraphState("ADJECTIVE_DERIV");
    private final SuffixGraphState DECORATED_ADVERB_ROOT = getSuffixGraphState("ADVERB_ROOT");

    private final SuffixGraphState DECORATED_NOUN_TERMINAL_TRANSFER = getSuffixGraphState("NOUN_TERMINAL_TRANSFER");
    private final SuffixGraphState DECORATED_ADJECTIVE_TERMINAL_TRANSFER = getSuffixGraphState("ADJECTIVE_TERMINAL_TRANSFER");
    private final SuffixGraphState DECORATED_ADVERB_TERMINAL_TRANSFER = getSuffixGraphState("ADVERB_TERMINAL_TRANSFER");
    private final SuffixGraphState DECORATED_PRONOUN_TERMINAL_TRANSFER = getSuffixGraphState("PRONOUN_TERMINAL_TRANSFER");
    private final SuffixGraphState DECORATED_VERB_TERMINAL_TRANSFER = getSuffixGraphState("VERB_TERMINAL_TRANSFER");

    private final SuffixGraphState DECORATED_VERB_WITH_TENSE = getSuffixGraphState("VERB_WITH_TENSE");
    private final SuffixGraphState DECORATED_VERB_TERMINAL = getSuffixGraphState("VERB_TERMINAL");

    private final SuffixGraphState DECORATED_QUESTION_WITH_AGREEMENT = getSuffixGraphState("QUESTION_WITH_AGREEMENT");

    // suffixes
    ///////////// Copula tenses
    private final Suffix Pres_Cop = registerSuffix("Pres_Cop", null, "Pres");
    private final Suffix Narr_Cop = registerSuffix("Narr_Cop", null, "Narr");
    private final Suffix Past_Cop = registerSuffix("Past_Cop", null, "Past");
    private final Suffix Cond_Cop = registerSuffix("Cond_Cop", null, "Cond");
    private final Suffix Cond_Cop_Secondary = registerSuffix("Cond_Cop_Secondary", null, "Cond");

    ///////////// Copula agreements
    private final SuffixGroup Copula_Agreements_Group = new SuffixGroup("Copula_Agreements_Group");
    private final Suffix A1Sg_Cop = registerSuffix("A1Sg_Cop", Copula_Agreements_Group, "A1sg");
    private final Suffix A2Sg_Cop = registerSuffix("A2Sg_Cop", Copula_Agreements_Group, "A2sg");
    private final Suffix A3Sg_Cop = registerSuffix("A3Sg_Cop", Copula_Agreements_Group, "A3sg");
    private final Suffix A1Pl_Cop = registerSuffix("A1Pl_Cop", Copula_Agreements_Group, "A1pl");
    private final Suffix A2Pl_Cop = registerSuffix("A2Pl_Cop", Copula_Agreements_Group, "A2pl");
    private final Suffix A3Pl_Cop = registerSuffix("A3Pl_Cop", Copula_Agreements_Group, "A3pl");

    ///////////// Copula tenses to Adverb
    private final Suffix While_Cop = registerSuffix("While_Cop", null, "While");
    private final Suffix AsIf_Cop = registerSuffix("AsIf_Cop", null, "AsIf");

    ///////////// Explicit Copula
    private final Suffix Cop_Verb = registerSuffix("Cop_Verb", null, "Cop");
    private final Suffix Cop_Verb_Swapped = registerSuffix("Cop_Verb_Swapped", null, "Cop");
    private final Suffix Cop_Others_Swapped = registerSuffix("Cop_Others_Swapped", null, "Cop");
    private final Suffix Cop_Ques = registerSuffix("Cop_Ques", null, "Cop");

    ///////////// from decorated
    private final Suffix Decorated_Aorist = getSuffix("Aor");
    private final Suffix Decorated_Past = getSuffix("Past");
    private final Suffix Decorated_Narr = getSuffix("Narr");
    private final Suffix Decorated_Fut = getSuffix("Fut");
    private final Suffix Decorated_Prog = getSuffix("Prog");
    private final Suffix Decorated_Neces = getSuffix("Neces");
    private final Suffix Decorated_Cond = getSuffix("Cond");
    private final Suffix Decorated_Narr_Ques = getSuffix("Narr_Ques");
    private final Suffix Decorated_Pres_Ques = getSuffix("Pres_Ques");
    private final Suffix Decorated_Past_Ques = getSuffix("Past_Ques");
    private final Suffix Decorated_Imp = getSuffix("Imp");
    private final Suffix Decorated_Opt = getSuffix("Opt");


    private static final String DEGIL = "değil";

    public CopulaSuffixGraph() {
        super();
    }

    public CopulaSuffixGraph(SuffixGraph decorated) {
        super(decorated);
    }

    @Override
    protected SuffixGraphState doGetDefaultStateForRoot(Root root) {
        final PrimaryPos primaryPos = root.getLexeme().getPrimaryPos();
        if (primaryPos.equals(Verb) && root.getSequence().getUnderlyingString().equals(DEGIL))
            return VERB_DEGIL_ROOT;

        return null;
    }

    @Override
    protected Collection<? extends SuffixGraphState> doGetRootSuffixGraphStates() {
        return Arrays.asList(VERB_DEGIL_ROOT);
    }

    @Override
    protected void registerEverything() {
        this.registerFreeTransitions();
        this.createSuffixEdges();
    }

    private void registerFreeTransitions() {
        ///////////////  Free transitions
        DECORATED_NOUN_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Noun_Cop_Free_Transition"), NOUN_COPULA);
        DECORATED_ADJECTIVE_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Adjective_Cop_Free_Transition"), ADJECTIVE_COPULA);
        DECORATED_ADVERB_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Adverb_Cop_Free_Transition"), ADVERB_COPULA);
        DECORATED_PRONOUN_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Pronoun_Cop_Free_Transition"), PRONOUN_COPULA);
        VERB_DEGIL_ROOT.addOutSuffix(registerFreeTransitionSuffix("Verb_Degil_Free_Transition"), VERB_COPULA_WITHOUT_TENSE);
        VERB_COPULA_WITHOUT_TENSE.addOutSuffix(registerFreeTransitionSuffix("Copula_Deriv_Free_Transition_1"), VERB_COPULA_WITHOUT_TENSE_DERIV);
        VERB_COPULA_WITH_TENSE.addOutSuffix(registerFreeTransitionSuffix("Copula_Deriv_Free_Transition_2"), VERB_COPULA_WITH_TENSE_DERIV);

        NOUN_COPULA.addOutSuffix(registerZeroTransitionSuffix("Noun_Copula_Zero_Transition"), VERB_COPULA_WITHOUT_TENSE);
        ADJECTIVE_COPULA.addOutSuffix(registerZeroTransitionSuffix("Adjective_Copula_Zero_Transition"), VERB_COPULA_WITHOUT_TENSE);
        ADVERB_COPULA.addOutSuffix(registerZeroTransitionSuffix("Adverb_Copula_Zero_Transition"), VERB_COPULA_WITHOUT_TENSE);
        PRONOUN_COPULA.addOutSuffix(registerZeroTransitionSuffix("Pronoun_Copula_Zero_Transition"), VERB_COPULA_WITHOUT_TENSE);

        DECORATED_ADJECTIVE_DERIV.addOutSuffix(registerZeroTransitionSuffix("Adjective_Adverb_Zero_Transition"), DECORATED_ADVERB_ROOT);
    }

    private void createSuffixEdges() {
        this.registerCopulaTenses();
        this.registerSwappedA3PlVerbs();
        this.registerCopulaAgreements();
        this.registerCopulaTensesToOtherCategories();
        this.registerVerbExplicitCopula();
        this.registerQuesExplicitCopula();
    }

    public void registerCopulaTenses() {
        VERB_COPULA_WITHOUT_TENSE.addOutSuffix(Pres_Cop, VERB_COPULA_WITH_TENSE);
        Pres_Cop.addSuffixForm("");

        VERB_COPULA_WITHOUT_TENSE.addOutSuffix(Narr_Cop, VERB_COPULA_WITH_TENSE);
        Narr_Cop.addSuffixForm("+ymIş");

        VERB_COPULA_WITHOUT_TENSE.addOutSuffix(Past_Cop, VERB_COPULA_WITH_TENSE);
        Past_Cop.addSuffixForm("+ydI");

        VERB_COPULA_WITHOUT_TENSE.addOutSuffix(Cond_Cop, VERB_COPULA_WITH_TENSE);
        Cond_Cop.addSuffixForm("+ysA");

        VERB_COPULA_WITH_TENSE.addOutSuffix(Cond_Cop_Secondary, VERB_COPULA_WITH_TENSE);
        Cond_Cop_Secondary.addSuffixForm("+ysA", doesntComeAfter(Pres_Cop));
    }

    private void registerSwappedA3PlVerbs() {
        @SuppressWarnings("unchecked")
        final Specification<MorphemeContainer> swappedVerbCopulaPrecondition = Specifications.or(
                comesAfter(Decorated_Neces),
                comesAfter(Decorated_Aorist),
                comesAfter(Decorated_Prog),
                comesAfter(Decorated_Fut),
                comesAfter(Decorated_Narr));

        DECORATED_VERB_WITH_TENSE.addOutSuffix(Cop_Verb_Swapped, VERB_COPULA_WITH_SWAPPED_A3PL);
        Cop_Verb_Swapped.addSuffixForm("dIr", swappedVerbCopulaPrecondition);

        VERB_COPULA_WITH_SWAPPED_A3PL.addOutSuffix(A3Pl_Cop, DECORATED_VERB_TERMINAL);

        VERB_COPULA_WITHOUT_TENSE.addOutSuffix(Cop_Others_Swapped, VERB_COPULA_FROM_OTHERS_WITH_SWAPPED_A3PL);
        Cop_Others_Swapped.addSuffixForm("dIr");
        VERB_COPULA_FROM_OTHERS_WITH_SWAPPED_A3PL.addOutSuffix(A3Pl_Cop, DECORATED_VERB_TERMINAL);
    }

    public void registerCopulaAgreements() {
        final Specification<MorphemeContainer> comesAfterCondOrPast = Specifications.or(
                comesAfter(Cond_Cop),
                comesAfter(Cond_Cop_Secondary),
                comesAfter(Past_Cop));

        VERB_COPULA_WITH_TENSE.addOutSuffix(A1Sg_Cop, DECORATED_VERB_TERMINAL_TRANSFER);
        A1Sg_Cop.addSuffixForm("+yIm");                       // (ben) elma-yim, (ben) armud-um, elma-ymis-im
        A1Sg_Cop.addSuffixForm("m", comesAfterCondOrPast);   // elma-ydi-m, elma-ysa-m

        VERB_COPULA_WITH_TENSE.addOutSuffix(A2Sg_Cop, DECORATED_VERB_TERMINAL_TRANSFER);
        A2Sg_Cop.addSuffixForm("sIn");                        // (sen) elma-sin, (sen) armutsun, elma-ymis-sin
        A2Sg_Cop.addSuffixForm("n", comesAfterCondOrPast);   // elma-ydi-n, elma-ysa-n

        VERB_COPULA_WITH_TENSE.addOutSuffix(A3Sg_Cop, DECORATED_VERB_TERMINAL_TRANSFER);
        A3Sg_Cop.addSuffixForm("");                         // (o) elma(dir), (o) armut(tur), elma-ymis, elma-ysa, elma-ydi

        VERB_COPULA_WITH_TENSE.addOutSuffix(A1Pl_Cop, DECORATED_VERB_TERMINAL_TRANSFER);
        A1Pl_Cop.addSuffixForm("+yIz");                          // (biz) elma-yiz, (biz) armud-uz, elma-ymis-iz
        A1Pl_Cop.addSuffixForm("!k", comesAfterCondOrPast);   // elma-ydi-k, elma-ysa-k

        VERB_COPULA_WITH_TENSE.addOutSuffix(A2Pl_Cop, DECORATED_VERB_TERMINAL_TRANSFER);
        A2Pl_Cop.addSuffixForm("sInIz");                        // (siz) elma-siniz, (siz) armut-sunuz, elma-ymis-siniz
        A2Pl_Cop.addSuffixForm("nIz", comesAfterCondOrPast); // elma-ydi-niz, elma-ysa-niz

        VERB_COPULA_WITH_TENSE.addOutSuffix(A3Pl_Cop, DECORATED_VERB_TERMINAL_TRANSFER);
        A3Pl_Cop.addSuffixForm("lAr");    // (onlar) elma-lar(dir), (onlar) armut-lar(dir), elma-ymis-lar, elma-ydi-lar, elma-ysa-lar
    }

    public void registerCopulaTensesToOtherCategories() {
        VERB_COPULA_WITHOUT_TENSE_DERIV.addOutSuffix(While_Cop, DECORATED_ADVERB_ROOT);
        While_Cop.addSuffixForm("+yken");

        VERB_COPULA_WITH_TENSE_DERIV.addOutSuffix(AsIf_Cop, DECORATED_ADVERB_ROOT);
        AsIf_Cop.addSuffixForm("cAs!InA", Specifications.or(comesAfter(Pres_Cop), comesAfter(Narr_Cop)));
    }

    public void registerVerbExplicitCopula() {
        @SuppressWarnings("unchecked")
        final Specification<MorphemeContainer> explicitVerbCopulaPrecondition = Specifications.and(
                doesntComeAfter(Decorated_Aorist),
                doesntComeAfter(Decorated_Past),
                doesntComeAfter(Decorated_Cond),
                doesntComeAfter(Decorated_Imp),
                doesntComeAfter(Decorated_Opt),
                doesntComeAfter(Cond_Cop),
                doesntComeAfter(Cond_Cop_Secondary),
                doesntComeAfter(Past_Cop),
                doesntComeAfter(Narr_Cop),
                doesntComeAfter(Decorated_Narr_Ques),
                doesntComeAfter(Decorated_Past_Ques));

        DECORATED_VERB_TERMINAL_TRANSFER.addOutSuffix(Cop_Verb, DECORATED_VERB_TERMINAL_TRANSFER);
        Cop_Verb.addSuffixForm("dIr", explicitVerbCopulaPrecondition);
    }

    public void registerQuesExplicitCopula() {
        final Specification<MorphemeContainer> explicitQuesCopulaPrecondition = comesAfter(Decorated_Pres_Ques);

        DECORATED_QUESTION_WITH_AGREEMENT.addOutSuffix(Cop_Verb, DECORATED_QUESTION_WITH_AGREEMENT);
        Cop_Ques.addSuffixForm("dIr", explicitQuesCopulaPrecondition);
    }
}
