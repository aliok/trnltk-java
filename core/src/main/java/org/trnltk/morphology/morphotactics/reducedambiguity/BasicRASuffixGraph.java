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

package org.trnltk.morphology.morphotactics.reducedambiguity;

import com.google.common.collect.ImmutableMap;
import org.trnltk.common.specification.Specification;
import org.trnltk.common.specification.Specifications;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.lexicon.SecondaryPos;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.model.suffix.SuffixGroup;
import org.trnltk.morphology.morphotactics.BaseSuffixGraph;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.morphotactics.SuffixGraphStateType;

import java.util.ArrayList;
import java.util.Collection;

import static org.trnltk.model.lexicon.PrimaryPos.*;
import static org.trnltk.model.lexicon.PrimaryPos.Question;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.DERIVATIONAL;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TERMINAL;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TRANSFER;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.*;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.appliesToRoot;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.doesnt;

public class BasicRASuffixGraph extends BaseSuffixGraph {
    // states
    private final SuffixGraphState NOUN_ROOT = registerState("NOUN_ROOT", TRANSFER, Noun);
    private final SuffixGraphState NOUN_WITH_AGREEMENT = registerState("NOUN_WITH_AGREEMENT", TRANSFER, Noun);
    private final SuffixGraphState NOUN_WITH_POSSESSION = registerState("NOUN_WITH_POSSESSION", TRANSFER, Noun);
    private final SuffixGraphState NOUN_WITH_CASE = registerState("NOUN_WITH_CASE", TRANSFER, Noun);
    private final SuffixGraphState NOUN_TERMINAL_TRANSFER = registerState("NOUN_TERMINAL_TRANSFER", TRANSFER, Noun);
    private final SuffixGraphState NOUN_TERMINAL = registerState("NOUN_TERMINAL", TERMINAL, Noun);
    private final SuffixGraphState NOUN_NOM_DERIV = registerState("NOUN_NOM_DERIV", DERIVATIONAL, Noun);
    private final SuffixGraphState NOUN_POSSESSIVE_NOM_DERIV = registerState("NOUN_POSSESSIVE_NOM_DERIV", DERIVATIONAL, Noun);
    private final SuffixGraphState NOUN_DERIV_WITH_CASE = registerState("NOUN_DERIV_WITH_CASE", DERIVATIONAL, Noun);

    private final SuffixGraphState NOUN_COMPOUND_ROOT = registerState("NOUN_COMPOUND_ROOT", TRANSFER, Noun);
    private final SuffixGraphState NOUN_COMPOUND_WITH_AGREEMENT = registerState("NOUN_COMPOUND_WITH_AGREEMENT", TRANSFER, Noun);
    private final SuffixGraphState NOUN_COMPOUND_WITH_POSSESSION = registerState("NOUN_COMPOUND_WITH_POSSESSION", TRANSFER, Noun);

    private final SuffixGraphState VERB_ROOT = registerState("VERB_ROOT", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_POLARITY = registerState("VERB_WITH_POLARITY", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_TENSE = registerState("VERB_WITH_TENSE", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_SWAPPED_A3PL = registerState("VERB_WITH_SWAPPED_A3PL", TRANSFER, Verb);
    private final SuffixGraphState VERB_WITH_SWAPPED_PAST_COND = registerState("VERB_WITH_SWAPPED_PAST_COND", TRANSFER, Verb);
    private final SuffixGraphState VERB_TERMINAL = registerState("VERB_TERMINAL", TERMINAL, Verb);
    private final SuffixGraphState VERB_TERMINAL_TRANSFER = registerState("VERB_TERMINAL_TRANSFER", TRANSFER, Verb);
    private final SuffixGraphState VERB_PLAIN_DERIV = registerState("VERB_PLAIN_DERIV", DERIVATIONAL, Verb);
    private final SuffixGraphState VERB_POLARITY_DERIV = registerState("VERB_POLARITY_DERIV", DERIVATIONAL, Verb);
    private final SuffixGraphState VERB_WITH_TENSE_BEFORE_DERIV = registerState("VERB_WITH_TENSE_BEFORE_DERIV", TRANSFER, Verb);
    private final SuffixGraphState VERB_TENSE_DERIV = registerState("VERB_TENSE_DERIV", DERIVATIONAL, Verb);
    private final SuffixGraphState VERB_TENSE_ADJ_DERIV = registerState("VERB_TENSE_ADJ_DERIV", DERIVATIONAL, Verb);

    private final SuffixGraphState ADJECTIVE_ROOT = registerState("ADJECTIVE_ROOT", TRANSFER, Adjective);
    private final SuffixGraphState ADJECTIVE_PART_WITHOUT_POSSESSION = registerState("ADJECTIVE_PART_WITHOUT_POSSESSION", TRANSFER, Adjective);
    private final SuffixGraphState ADJECTIVE_TERMINAL = registerState("ADJECTIVE_TERMINAL", TERMINAL, Adjective);
    private final SuffixGraphState ADJECTIVE_TERMINAL_TRANSFER = registerState("ADJECTIVE_TERMINAL_TRANSFER", TRANSFER, Adjective);
    private final SuffixGraphState ADJECTIVE_DERIV = registerState("ADJECTIVE_DERIV", DERIVATIONAL, Adjective);

    private final SuffixGraphState ADVERB_ROOT = registerState("ADVERB_ROOT", TRANSFER, Adverb);
    private final SuffixGraphState ADVERB_TERMINAL = registerState("ADVERB_TERMINAL", TERMINAL, Adverb);
    private final SuffixGraphState ADVERB_TERMINAL_TRANSFER = registerState("ADVERB_TERMINAL_TRANSFER", TRANSFER, Adverb);
    private final SuffixGraphState ADVERB_DERIV = registerState("ADVERB_DERIV", DERIVATIONAL, Adverb);

    private final SuffixGraphState PRONOUN_ROOT = registerState("PRONOUN_ROOT", TRANSFER, Pronoun);
    private final SuffixGraphState PRONOUN_WITH_AGREEMENT = registerState("PRONOUN_WITH_AGREEMENT", TRANSFER, Pronoun);
    private final SuffixGraphState PRONOUN_WITH_POSSESSION = registerState("PRONOUN_WITH_POSSESSION", TRANSFER, Pronoun);
    private final SuffixGraphState PRONOUN_WITH_CASE = registerState("PRONOUN_WITH_CASE", TRANSFER, Pronoun);
    private final SuffixGraphState PRONOUN_NOM_DERIV = registerState("PRONOUN_NOM_DERIV", DERIVATIONAL, Pronoun);
    private final SuffixGraphState PRONOUN_TERMINAL = registerState("PRONOUN_TERMINAL", TERMINAL, Pronoun);
    private final SuffixGraphState PRONOUN_TERMINAL_TRANSFER = registerState("PRONOUN_TERMINAL_TRANSFER", TRANSFER, Pronoun);
    private final SuffixGraphState PRONOUN_DERIV_WITH_CASE = registerState("PRONOUN_DERIV_WITH_CASE", DERIVATIONAL, Pronoun);

    private final SuffixGraphState DETERMINER_ROOT_TERMINAL = registerState("DETERMINER_ROOT_TERMINAL", TERMINAL, Determiner);

    private final SuffixGraphState INTERJECTION_ROOT_TERMINAL = registerState("INTERJECTION_ROOT_TERMINAL", TERMINAL, Interjection);

    private final SuffixGraphState CONJUNCTION_ROOT_TERMINAL = registerState("CONJUNCTION_ROOT_TERMINAL", TERMINAL, Conjunction);

    private final SuffixGraphState QUESTION_ROOT = registerState("QUESTION_ROOT", TRANSFER, Question);
    private final SuffixGraphState QUESTION_WITH_TENSE = registerState("QUESTION_WITH_TENSE", TRANSFER, Question);
    private final SuffixGraphState QUESTION_WITH_AGREEMENT = registerState("QUESTION_WITH_AGREEMENT", TRANSFER, Question);
    private final SuffixGraphState QUESTION_TERMINAL = registerState("QUESTION_TERMINAL", TERMINAL, Question);

    private final SuffixGraphState DUP_ROOT_TERMINAL = registerState("DUP_ROOT_TERMINAL", TERMINAL, Duplicator);

    private final SuffixGraphState PUNC_ROOT_TERMINAL = registerState("PUNC_ROOT_TERMINAL", TERMINAL, Punctuation);

    private final SuffixGraphState POSTP_ROOT_TERMINAL = registerState("POSTP_ROOT_TERMINAL", TERMINAL, PostPositive);


    private final ImmutableMap<PrimaryPos, SuffixGraphState> rootStateMap = new ImmutableMap.Builder<PrimaryPos, SuffixGraphState>()
            .put(Noun, NOUN_ROOT)
            .put(Verb, VERB_ROOT)
            .put(Adverb, ADVERB_ROOT)
            .put(Adjective, ADJECTIVE_ROOT)
            .put(Pronoun, PRONOUN_ROOT)
            .put(Determiner, DETERMINER_ROOT_TERMINAL)
            .put(Interjection, INTERJECTION_ROOT_TERMINAL)
            .put(Conjunction, CONJUNCTION_ROOT_TERMINAL)
            .put(Punctuation, PUNC_ROOT_TERMINAL)
            .put(Duplicator, DUP_ROOT_TERMINAL)
            .put(PostPositive, POSTP_ROOT_TERMINAL)          //TODO-INTEGRATION: particle or postpositive?
            .put(Question, QUESTION_ROOT)
            .build();

    // suffixes
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

    /////////////// Noun to Verb derivations
    private final Suffix Acquire = registerSuffix("Acquire");
    private final Suffix Become_Noun = registerSuffix("Become_Noun", "Become");

    /////////////// Noun to Adjective derivations
    private final Suffix Agt_Noun_to_Adj = registerSuffix("Agt_Noun_to_Adj", "Agt");
    private final Suffix With = registerSuffix("With");
    private final Suffix Without = registerSuffix("Without");
    private final Suffix Related = registerSuffix("Related");

    private final Suffix PointQual_Noun = registerSuffix("PointQual_Noun", "PointQual");     //was marked as relative pronoun in other projects, but that is "Alininki"
    private final Suffix JustLike_Noun = registerSuffix("JustLike_Noun", "JustLike");
    private final Suffix Equ_Noun = registerSuffix("Equ_Noun", "Equ");
    private final Suffix Y = registerSuffix("Y");
    private final Suffix For = registerSuffix("For");
    private final Suffix DurationOf = registerSuffix("DurationOf");
    private final Suffix OfUnit_Noun = registerSuffix("OfUnit_Noun", "OfUnit");

    ///////////////// Noun to Adverb derivations
    private final Suffix InTermsOf = registerSuffix("InTermsOf");
    private final Suffix By_Pnon = registerSuffix("By_Pnon", "By");
    private final Suffix By_Possessive = registerSuffix("By_Possessive", "By");
    private final Suffix ManyOf = registerSuffix("ManyOf");
    private final Suffix ForALotOfTime = registerSuffix("ForALotOfTime");

    ///////////////// Noun to Pronoun derivations
    private final SuffixGroup Relative_Noun_Pronoun_Group = new SuffixGroup("Relative_Noun_Pronoun_Group");
    private final Suffix RelPron_A3Sg_Noun = registerSuffix("RelPron_A3Sg_Noun", Relative_Noun_Pronoun_Group, "A3sg");
    private final Suffix RelPron_A3Pl_Noun = registerSuffix("RelPron_A3Pl_Noun", Relative_Noun_Pronoun_Group, "A3pl");

    /////////////// Noun Compound suffixes
    private final Suffix A3Sg_Noun_Compound = registerSuffix("A3Sg_Noun_Compound", "A3sg");
    private final Suffix PNon_Noun_Compound = registerSuffix("Pnon_Noun_Compound", "Pnon");
    private final Suffix P3Sg_Noun_Compound = registerSuffix("P3Sg_Noun_Compound", "P3sg");
    private final Suffix P3Pl_Noun_Compound = registerSuffix("P3Pl_Noun_Compound", "P3pl");
    private final Suffix Nom_Noun_Compound_Deriv = registerSuffix("Nom_Noun_Compound_Deriv", "Nom");

    /////////////// Noun terminal suffixes
    private final Suffix NounTerminalTransition = registerConditionalFreeTransitionSuffix("Noun_Terminal_Conditional_Free_Transition");

    /////////////// Verb agreements
    private final SuffixGroup Verb_Agreements_Group = new SuffixGroup("Verb_Agreements_Group");
    private final Suffix A1Sg_Verb = registerSuffix("A1Sg_Verb", Verb_Agreements_Group, "A1sg");
    private final Suffix A2Sg_Verb = registerSuffix("A2Sg_Verb", Verb_Agreements_Group, "A2sg");
    private final Suffix A3Sg_Verb = registerSuffix("A3Sg_Verb", Verb_Agreements_Group, "A3sg");
    private final Suffix A1Pl_Verb = registerSuffix("A1Pl_Verb", Verb_Agreements_Group, "A1pl");
    private final Suffix A2Pl_Verb = registerSuffix("A2Pl_Verb", Verb_Agreements_Group, "A2pl");
    private final Suffix A3Pl_Verb = registerSuffix("A3Pl_Verb", Verb_Agreements_Group, "A3pl");

    ////////////// Swapped verb agreements
    private final SuffixGroup Verb_Agreements_Swapped_Group = new SuffixGroup("Verb_Agreements_Swapped_Group");
    private final Suffix A1Sg_Verb_Swapped = registerSuffix("A1Sg_Verb_Swapped", Verb_Agreements_Swapped_Group, "A1sg");
    private final Suffix A2Sg_Verb_Swapped = registerSuffix("A2Sg_Verb_Swapped", Verb_Agreements_Swapped_Group, "A2sg");
    private final Suffix A1Pl_Verb_Swapped = registerSuffix("A1Pl_Verb_Swapped", Verb_Agreements_Swapped_Group, "A1pl");
    private final Suffix A2Pl_Verb_Swapped = registerSuffix("A2Pl_Verb_Swapped", Verb_Agreements_Swapped_Group, "A2pl");

    /////////////// Verb conditions
    private final SuffixGroup Verb_Polarity_Group = new SuffixGroup("Verb_Conditions_Group");
    private final Suffix Negative = registerSuffix("Neg", Verb_Polarity_Group);
    private final Suffix Positive = registerSuffix("Pos", Verb_Polarity_Group);

    /////////////// Verbal tenses
    private final Suffix Aorist = registerSuffix("Aor");
    private final Suffix Progressive = registerSuffix("Prog");
    private final Suffix Future = registerSuffix("Fut");
    private final Suffix Narr = registerSuffix("Narr");
    private final Suffix Past = registerSuffix("Past");
    private final Suffix Pres = registerSuffix("Pres");

    private final Suffix Cond = registerSuffix("Cond");
    private final Suffix Imp = registerSuffix("Imp");

    ///////////////// Modals
    private final Suffix Neces = registerSuffix("Neces");
    private final Suffix Opt = registerSuffix("Opt");
    private final Suffix Desr = registerSuffix("Desr");

    ///////////////// Verb to Noun derivations
    private final Suffix Inf = registerSuffix("Inf");
    private final Suffix PastPart_Noun = registerSuffix("PastPart_Noun", "PastPart");
    private final Suffix FutPart_Noun = registerSuffix("FutPart_Noun", "FutPart");

    ///////////////// Verb to Verb derivations
    private final Suffix Able = registerSuffix("Able");
    private final Suffix Pass = registerSuffix("Pass");
    private final Suffix Recip = registerSuffix("Recip");
    private final Suffix Caus = registerSuffix("Caus", null, "Caus", true);

    private final Suffix Hastily = registerSuffix("Hastily");
    private final Suffix EverSince = registerSuffix("EverSince");
    private final Suffix Stay = registerSuffix("Stay");
    private final Suffix Almost = registerSuffix("Almost");
    private final Suffix Once = registerSuffix("Once");
    private final Suffix Gone = registerSuffix("Gone");
    private final Suffix Start = registerSuffix("Start");

    /////////////// Verb to Adverb derivations
    private final Suffix AfterDoingSo = registerSuffix("AfterDoingSo");
    private final Suffix WithoutHavingDoneSo = registerSuffix("WithoutHavingDoneSo");
    private final Suffix AsLongAs = registerSuffix("AsLongAs");
    private final Suffix ByDoingSo = registerSuffix("ByDoingSo");
    private final Suffix When = registerSuffix("When");
    private final Suffix Until = registerSuffix("Until");
    private final Suffix SinceDoingSo = registerSuffix("SinceDoingSo");
    private final Suffix While = registerSuffix("While");        // A3pl can come before
    private final Suffix AsIf = registerSuffix("AsIf");       // A3pl can come before
    private final Suffix A3Pl_Verb_For_Adv = registerSuffix("A3Pl_Verb_For_Adv", "A3pl");

    /////////////// Verb to Adjective derivations
    private final Suffix PresPart = registerSuffix("PresPart");
    private final Suffix PastPart_Adj = registerSuffix("PastPart_Adj", "PastPart");
    private final Suffix FutPart_Adj = registerSuffix("FutPart_Adj", "FutPart");
    private final Suffix Agt_Verb_to_Adj = registerSuffix("Agt_Verb_to_Adj", "Agt");
    private final Suffix Aorist_to_Adj = registerSuffix("Aorist_to_Adj", "Aor");
    private final Suffix Future_to_Adj = registerSuffix("Future_to_Adj", "Fut");
    private final Suffix Narr_to_Adj = registerSuffix("Narr_to_Adj", "Narr");

    /////////////// Adjective to Adjective derivations
    private final Suffix JustLike_Adj = registerSuffix("JustLike_Adj", "JustLike");
    private final Suffix Equ_Adj = registerSuffix("Equ_Adj", "Equ");
    private final Suffix Quite = registerSuffix("Quite");

    /////////////// Adjective to Adverb derivations
    private final Suffix Ly = registerSuffix("Ly");

    /////////////// Adjective to Noun derivations
    private final Suffix Ness = registerSuffix("Ness");

    /////////////// Adjective to Verb derivations
    private final Suffix Become_Adj = registerSuffix("Become_Adj", "Become");

    /////////////// Adjective possessions
    private final SuffixGroup Adjective_Possessions_Group = new SuffixGroup("Adjective_Possessions_Group");
    private final Suffix Pnon_Adj = registerSuffix("Pnon_Adj", Adjective_Possessions_Group, "Pnon");
    private final Suffix P1Sg_Adj = registerSuffix("P1Sg_Adj", Adjective_Possessions_Group, "P1sg");
    private final Suffix P2Sg_Adj = registerSuffix("P2Sg_Adj", Adjective_Possessions_Group, "P2sg");
    private final Suffix P3Sg_Adj = registerSuffix("P3Sg_Adj", Adjective_Possessions_Group, "P3sg");
    private final Suffix P1Pl_Adj = registerSuffix("P1Pl_Adj", Adjective_Possessions_Group, "P1pl");
    private final Suffix P2Pl_Adj = registerSuffix("P2Pl_Adj", Adjective_Possessions_Group, "P2pl");
    private final Suffix P3Pl_Adj = registerSuffix("P3Pl_Adj", Adjective_Possessions_Group, "P3pl");

    ///////////////  Pronoun Agreements
    private final SuffixGroup Pronoun_Agreements_Group = new SuffixGroup("Pronoun_Agreements_Group");
    private final Suffix A1Sg_Pron = registerSuffix("A1Sg_Pron", Pronoun_Agreements_Group, "A1sg");
    private final Suffix A2Sg_Pron = registerSuffix("A2Sg_Pron", Pronoun_Agreements_Group, "A2sg");
    private final Suffix A3Sg_Pron = registerSuffix("A3Sg_Pron", Pronoun_Agreements_Group, "A3sg");
    private final Suffix A1Pl_Pron = registerSuffix("A1Pl_Pron", Pronoun_Agreements_Group, "A1pl");
    private final Suffix A2Pl_Pron = registerSuffix("A2Pl_Pron", Pronoun_Agreements_Group, "A2pl");
    private final Suffix A3Pl_Pron = registerSuffix("A3Pl_Pron", Pronoun_Agreements_Group, "A3pl");

    /////////////// Pronoun possessions
    private final SuffixGroup Pronoun_Possessions_Group = new SuffixGroup("Pronoun_Possessions_Group");
    private final Suffix Pnon_Pron = registerSuffix("Pnon_Pron", Pronoun_Possessions_Group, "Pnon");
    private final Suffix P1Sg_Pron = registerSuffix("P1Sg_Pron", Pronoun_Possessions_Group, "P1sg");
    private final Suffix P2Sg_Pron = registerSuffix("P2Sg_Pron", Pronoun_Possessions_Group, "P2sg");
    private final Suffix P3Sg_Pron = registerSuffix("P3Sg_Pron", Pronoun_Possessions_Group, "P3sg");
    private final Suffix P1Pl_Pron = registerSuffix("P1Pl_Pron", Pronoun_Possessions_Group, "P1pl");
    private final Suffix P2Pl_Pron = registerSuffix("P2Pl_Pron", Pronoun_Possessions_Group, "P2pl");
    private final Suffix P3Pl_Pron = registerSuffix("P3Pl_Pron", Pronoun_Possessions_Group, "P3pl");

    ///////////////  Pronoun cases
    private final SuffixGroup Pronoun_Case_Group = new SuffixGroup("Pronoun_Case_Group");
    private final Suffix Nom_Pron = registerSuffix("Nom_Pron", Pronoun_Case_Group, "Nom");
    private final Suffix Nom_Pron_Deriv = registerSuffix("Nom_Pron_Deriv", Pronoun_Case_Group, "Nom");
    private final Suffix Acc_Pron = registerSuffix("Acc_Pron", Pronoun_Case_Group, "Acc");
    private final Suffix Dat_Pron = registerSuffix("Dat_Pron", Pronoun_Case_Group, "Dat");
    private final Suffix Loc_Pron = registerSuffix("Loc_Pron", Pronoun_Case_Group, "Loc");
    private final Suffix Abl_Pron = registerSuffix("Abl_Pron", Pronoun_Case_Group, "Abl");

    /////////////// Pronoun case-likes
    private final Suffix Gen_Pron = registerSuffix("Gen_Pron", Pronoun_Case_Group, "Gen");
    private final Suffix Ins_Pron = registerSuffix("Ins_Pron", Pronoun_Case_Group, "Ins");
    private final Suffix AccordingTo = registerSuffix("AccordingTo", Pronoun_Case_Group);

    /////////////// Pronoun to Adjective derivations
    private final Suffix Without_Pron = registerSuffix("Without_Pron", "Without");
    private final Suffix PointQual_Pron = registerSuffix("PointQual_Pron", "PointQual");

    /////////////// Pronoun to Pronoun derivations
    private final SuffixGroup Relative_Pron_Pronoun_Group = new SuffixGroup("Relative_Pron_Pronoun_Group");
    private final Suffix RelPron_A3Sg_Pron = registerSuffix("RelPron_A3Sg_Pron", Relative_Pron_Pronoun_Group, "A3sg");
    private final Suffix RelPron_A3Pl_Pron = registerSuffix("RelPron_A3Pl_Pron", Relative_Pron_Pronoun_Group, "A3pl");

    /////////////// Adverb to Adjective derivations
    private final Suffix PointQual_Adv = registerSuffix("PointQual_Adv", "PointQual");

    ///////////////// Question Tenses
    private final SuffixGroup Question_Tense_Group = new SuffixGroup("Question_Tense_Group");

    private final Suffix Pres_Ques = registerSuffix("Pres_Ques", Question_Tense_Group, "Pres");
    private final Suffix Past_Ques = registerSuffix("Past_Ques", Question_Tense_Group, "Past");
    private final Suffix Narr_Ques = registerSuffix("Narr_Ques", Question_Tense_Group, "Narr");

    ///////////////// Question Agreements
    private final SuffixGroup Question_Agreements_Group = new SuffixGroup("Question_Agreements_Group");

    private final Suffix A1Sg_Ques = registerSuffix("A1Sg_Ques", Question_Agreements_Group, "A1sg");
    private final Suffix A2Sg_Ques = registerSuffix("A2Sg_Ques", Question_Agreements_Group, "A2sg");
    private final Suffix A3Sg_Ques = registerSuffix("A3Sg_Ques", Question_Agreements_Group, "A3sg");
    private final Suffix A1Pl_Ques = registerSuffix("A1Pl_Ques", Question_Agreements_Group, "A1pl");
    private final Suffix A2Pl_Ques = registerSuffix("A2Pl_Ques", Question_Agreements_Group, "A2pl");
    private final Suffix A3Pl_Ques = registerSuffix("A3Pl_Ques", Question_Agreements_Group, "A3pl");


    @Override
    protected SuffixGraphState doGetDefaultStateForRoot(Root root) {
        final SuffixGraphState defaultState = this.rootStateMap.get(root.getLexeme().getPrimaryPos());
        if (defaultState == null)
            return null;

        if (defaultState.equals(NOUN_ROOT) && root.getLexeme().getAttributes().contains(LexemeAttribute.CompoundP3sg))
            return NOUN_COMPOUND_ROOT;
        else
            return defaultState;

    }

    @Override
    protected Collection<? extends SuffixGraphState> doGetRootSuffixGraphStates() {
        final ArrayList<SuffixGraphState> states = new ArrayList<SuffixGraphState>();
        states.add(NOUN_COMPOUND_ROOT);
        states.addAll(this.rootStateMap.values());
        return states;
    }

    @Override
    protected void registerEverything() {
        this.registerFreeTransitions();
        this.createSuffixEdges();
    }

    private void registerFreeTransitions() {
        ///////////////  Free transitions
        NOUN_WITH_CASE.addOutSuffix(registerFreeTransitionSuffix("Noun_Free_Transition_1"), NOUN_TERMINAL_TRANSFER);
        NOUN_WITH_CASE.addOutSuffix(registerFreeTransitionSuffix("Noun_Free_Transition_2"), NOUN_DERIV_WITH_CASE);

        VERB_ROOT.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_1"), VERB_PLAIN_DERIV);
        VERB_WITH_POLARITY.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_2"), VERB_POLARITY_DERIV);
        VERB_WITH_TENSE.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_3"), VERB_WITH_TENSE_BEFORE_DERIV);
        VERB_WITH_TENSE_BEFORE_DERIV.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_4"), VERB_TENSE_DERIV);
        VERB_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Verb_Free_Transition_5"), VERB_TERMINAL);

        ADJECTIVE_ROOT.addOutSuffix(registerFreeTransitionSuffix("Adj_Free_Transition_1"), ADJECTIVE_TERMINAL_TRANSFER);
        ADJECTIVE_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Adj_Free_Transition_2"), ADJECTIVE_TERMINAL);
        ADJECTIVE_ROOT.addOutSuffix(registerFreeTransitionSuffix("Adj_Free_Transition_3"), ADJECTIVE_DERIV);

        ADVERB_ROOT.addOutSuffix(registerFreeTransitionSuffix("Adv_Free_Transition_1"), ADVERB_TERMINAL_TRANSFER);
        ADVERB_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Adv_Free_Transition_2"), ADVERB_TERMINAL);
        ADVERB_ROOT.addOutSuffix(registerFreeTransitionSuffix("Adv_Free_Transition_3"), ADVERB_DERIV);

        PRONOUN_WITH_CASE.addOutSuffix(registerFreeTransitionSuffix("Pronoun_Free_Transition_1"), PRONOUN_TERMINAL_TRANSFER);
        PRONOUN_TERMINAL_TRANSFER.addOutSuffix(registerFreeTransitionSuffix("Pronoun_Free_Transition_2"), PRONOUN_TERMINAL);
        PRONOUN_WITH_CASE.addOutSuffix(registerFreeTransitionSuffix("Pronoun_Free_Transition_3"), PRONOUN_DERIV_WITH_CASE);

        QUESTION_WITH_AGREEMENT.addOutSuffix(registerFreeTransitionSuffix("Question_Free_Transition_1"), QUESTION_TERMINAL);

        ///////////////  Zero transitions
        ADJECTIVE_DERIV.addOutSuffix(registerZeroTransitionSuffix("Adj_to_Noun_Zero_Transition"), NOUN_ROOT);
        VERB_TENSE_ADJ_DERIV.addOutSuffix(registerZeroTransitionSuffix("Verb_to_Adj_Zero_Transition"), ADJECTIVE_ROOT);
    }

    private void createSuffixEdges() {
        registerNounSuffixes();
        registerVerbSuffixes();
        registerAdjectiveSuffixes();
        registerPronounSuffixes();
        registerAdverbSuffixes();
        registerQuestionSuffixes();
    }

    private void registerNounSuffixes() {
        registerNounAgreements();
        registerPossessiveAgreements();
        registerNounCases();
        registerNounToNounDerivations();
        registerNounToVerbDerivations();
        registerNounToAdjectiveDerivations();
        registerNounToAdverbDerivations();
        registerNounToPronounDerivations();
        registerNounCompoundSuffixes();
        registerNounConditionalFreeTransitions();
    }

    private void registerVerbSuffixes() {
        registerVerbAgreements();
        registerVerbPolarisations();
        registerVerbTenses();
        registerSwappedPastCond();
        registerModalVerbs();
        registerSwappedA3PlVerbs();
        registerVerbToVerbDerivations();
        registerVerbToNounDerivations();
        registerVerbToAdverbDerivations();
        registerVerbToAdjectiveDerivations();
    }

    private void registerAdjectiveSuffixes() {
        registerAdjectiveToAdjectiveDerivations();
        registerAdjectiveToAdverbDerivations();
        registerAdjectiveToNounDerivations();
        registerAdjectiveToVerbDerivations();
        registerAdjectivePossessions();
    }

    private void registerPronounSuffixes() {
        registerPronounAgreements();
        registerPronounPossessions();
        registerPronounCases();
        registerPronounToAdjectiveSuffixes();
        registerPronounToPronounDerivations();
    }

    private void registerAdverbSuffixes() {
        registerAdverbToAdjectiveDerivations();
    }

    private void registerQuestionSuffixes() {
        registerQuestionTenses();
        registerQuestionAgreements();
    }

    private void registerNounAgreements() {
        NOUN_ROOT.addOutSuffix(A3Sg_Noun, NOUN_WITH_AGREEMENT);
        A3Sg_Noun.addSuffixForm("");

        NOUN_ROOT.addOutSuffix(A3Pl_Noun, NOUN_WITH_AGREEMENT);
        A3Pl_Noun.addSuffixForm("lAr");
    }

    private void registerPossessiveAgreements() {
        final Specification<MorphemeContainer> doesnt_come_after_PointerQual = Specifications.and(
                doesnt(comesAfterLastNonBlankDerivation(PointQual_Adv)),
                doesnt(comesAfterLastNonBlankDerivation(PointQual_Noun)),
                doesnt(comesAfterLastNonBlankDerivation(PointQual_Pron))
        );

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
                comesAfter(P3Pl_Noun),
                comesAfter(P3Sg_Noun_Compound),
                comesAfter(P3Pl_Noun_Compound)
        );

        final Specification<MorphemeContainer> doesntComeAfterP3 = comesAfterP3.not();

        final Specification<MorphemeContainer> comesAfterPointQual = Specifications.or(
                comesAfterLastNonBlankDerivation(PointQual_Adv),
                comesAfterLastNonBlankDerivation(PointQual_Noun),
                comesAfterLastNonBlankDerivation(PointQual_Pron)
        );

        final Specification<MorphemeContainer> comesAfterPointQualFollowedByA3sg = Specifications.and(
                comesAfterPointQual,
                Specifications.or(
                        comesAfter(A3Sg_Noun), comesAfter(A3Sg_Noun_Compound)
                )
        );
        final Specification<MorphemeContainer> comesAfterPointQualFollowedByA3Pl = Specifications.and(
                comesAfterPointQual,
                comesAfter(A3Pl_Noun)
        );

        final Specification<MorphemeContainer> preconditionForConsonantInsertion_Y = Specifications.or(
                (doesntComeAfterP3.and(doesnt(comesAfterPointQualFollowedByA3sg))),
                comesAfterPointQualFollowedByA3Pl);

        // kitabi-ni or kitaptaki-ni
        final Specification<MorphemeContainer> preconditionForConsonantInsertion_N = Specifications.or(comesAfterP3, comesAfterPointQualFollowedByA3sg);

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
        Dim.addSuffixForm("cIk");

        NOUN_NOM_DERIV.addOutSuffix(Prof, NOUN_ROOT);
        Prof.addSuffixForm("lIk");

        NOUN_NOM_DERIV.addOutSuffix(FitFor, NOUN_ROOT);
        FitFor.addSuffixForm("lIk");

        NOUN_NOM_DERIV.addOutSuffix(Title, NOUN_ROOT);
        Title.addSuffixForm("lIk");

    }

    private void registerNounToVerbDerivations() {
        NOUN_NOM_DERIV.addOutSuffix(Acquire, VERB_ROOT);
        Acquire.addSuffixForm("lAn");

        NOUN_NOM_DERIV.addOutSuffix(Become_Noun, VERB_ROOT);
        Become_Noun.addSuffixForm("lAş");
    }

    private void registerNounToAdjectiveDerivations() {
        NOUN_NOM_DERIV.addOutSuffix(Agt_Noun_to_Adj, ADJECTIVE_ROOT);
        Agt_Noun_to_Adj.addSuffixForm("cI");

        NOUN_NOM_DERIV.addOutSuffix(With, ADJECTIVE_ROOT);
        With.addSuffixForm("lI");

        NOUN_NOM_DERIV.addOutSuffix(Without, ADJECTIVE_ROOT);
        Without.addSuffixForm("sIz", doesntComeAfter(A3Pl_Noun));

        NOUN_NOM_DERIV.addOutSuffix(Related, ADJECTIVE_ROOT);
        Related.addSuffixForm("sAl", doesntComeAfter(A3Pl_Noun));

        NOUN_NOM_DERIV.addOutSuffix(JustLike_Noun, ADJECTIVE_ROOT);
        JustLike_Noun.addSuffixForm("+ImsI");

        NOUN_NOM_DERIV.addOutSuffix(Equ_Noun, ADJECTIVE_ROOT);
        Equ_Noun.addSuffixForm("cA");

        NOUN_NOM_DERIV.addOutSuffix(Y, ADJECTIVE_ROOT);
        Y.addSuffixForm("lIk");

        NOUN_NOM_DERIV.addOutSuffix(For, ADJECTIVE_ROOT);
        For.addSuffixForm("lIk");

        NOUN_NOM_DERIV.addOutSuffix(DurationOf, ADJECTIVE_ROOT);
        DurationOf.addSuffixForm("lIk");

        NOUN_NOM_DERIV.addOutSuffix(OfUnit_Noun, ADJECTIVE_ROOT);
        OfUnit_Noun.addSuffixForm("lIk");

        NOUN_DERIV_WITH_CASE.addOutSuffix(PointQual_Noun, ADJECTIVE_ROOT);
        PointQual_Noun.addSuffixForm("ki", comesAfter(Loc_Noun));

    }

    private void registerNounToAdverbDerivations() {
        NOUN_NOM_DERIV.addOutSuffix(InTermsOf, ADVERB_ROOT);
        InTermsOf.addSuffixForm("cA");

        NOUN_NOM_DERIV.addOutSuffix(By_Pnon, ADVERB_ROOT);
        By_Pnon.addSuffixForm("cA");

        NOUN_POSSESSIVE_NOM_DERIV.addOutSuffix(By_Possessive, ADVERB_ROOT);
        By_Possessive.addSuffixForm("ncA");

        NOUN_NOM_DERIV.addOutSuffix(ManyOf, ADVERB_ROOT);
        ManyOf.addSuffixForm("lArcA");

        NOUN_NOM_DERIV.addOutSuffix(ForALotOfTime, ADVERB_ROOT);
        ForALotOfTime.addSuffixForm("lArcA", rootHasSecondaryPos(SecondaryPos.Time));
    }

    private void registerNounToPronounDerivations() {
        final Specification<MorphemeContainer> comes_after_Gen_Noun = comesAfter(Gen_Noun);  // since it only works for nouns after Gen : "masaninki", "kardesiminkiler"
        final Specification<MorphemeContainer> followed_by_Pnon_Pron = followedBy(Pnon_Pron); // since sth like this doesn"t work: "masanınkim"

        NOUN_DERIV_WITH_CASE.addOutSuffix(RelPron_A3Sg_Noun, PRONOUN_WITH_AGREEMENT);
        RelPron_A3Sg_Noun.addSuffixForm("ki", comes_after_Gen_Noun, followed_by_Pnon_Pron);

        NOUN_DERIV_WITH_CASE.addOutSuffix(RelPron_A3Pl_Noun, PRONOUN_WITH_AGREEMENT);
        RelPron_A3Pl_Noun.addSuffixForm("kiler", comes_after_Gen_Noun, followed_by_Pnon_Pron);
    }

    private void registerNounCompoundSuffixes() {
        NOUN_COMPOUND_ROOT.addOutSuffix(A3Sg_Noun_Compound, NOUN_COMPOUND_WITH_AGREEMENT);
        A3Sg_Noun_Compound.addSuffixForm("");

        NOUN_COMPOUND_WITH_AGREEMENT.addOutSuffix(P3Sg_Noun_Compound, NOUN_WITH_POSSESSION);
        P3Sg_Noun_Compound.addSuffixForm("+sI");

        NOUN_COMPOUND_WITH_AGREEMENT.addOutSuffix(P3Pl_Noun_Compound, NOUN_WITH_POSSESSION);
        P3Pl_Noun_Compound.addSuffixForm("lAr!I");

        NOUN_COMPOUND_WITH_AGREEMENT.addOutSuffix(PNon_Noun_Compound, NOUN_COMPOUND_WITH_POSSESSION);
        PNon_Noun_Compound.addSuffixForm("");

        NOUN_COMPOUND_WITH_POSSESSION.addOutSuffix(Nom_Noun_Compound_Deriv, NOUN_NOM_DERIV);
        Nom_Noun_Compound_Deriv.addSuffixForm("");
    }

    private void registerNounConditionalFreeTransitions(){
        Specification<MorphemeContainer> notDerivedFromAdjThenFollowedByA3sgPnonNom =
                doesnt(Specifications.and(comesAfterDerivation(getSuffix("Adj_to_Noun_Zero_Transition")), comesAfter(A3Sg_Noun), comesAfter(Pnon_Noun), comesAfter(Nom_Noun)));

        NOUN_TERMINAL_TRANSFER.addOutSuffix(NounTerminalTransition, NOUN_TERMINAL);
        NounTerminalTransition.addSuffixForm("", notDerivedFromAdjThenFollowedByA3sgPnonNom);
    }

    private void registerVerbPolarisations() {
        VERB_ROOT.addOutSuffix(Negative, VERB_WITH_POLARITY);
        Negative.addSuffixForm("m", null, doesnt(followedBySuffixGoesTo(SuffixGraphStateType.DERIVATIONAL)));
        Negative.addSuffixForm("mA");
        Negative.addSuffixForm("", comesAfterDerivation(Able, "+yA"),
                Specifications.and(doesnt(followedBy(Imp)), doesnt(followedBy(Past)), doesnt(followedBy(Narr))),
                followedByDerivation(WithoutHavingDoneSo, "mAdAn")); // very special

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

        Cond.addSuffixForm("+ysA");

        Imp.addSuffixForm("", null, Specifications.or(followedBy(A2Sg_Verb), followedBy(A3Sg_Verb), followedBy(A2Pl_Verb), followedBy(A3Pl_Verb)));
        Imp.addSuffixForm("sAnA", null, followedBy(A2Sg_Verb));
        Imp.addSuffixForm("sAnIzA", null, followedBy(A2Pl_Verb));

        Pres.addSuffixForm("");

        VERB_WITH_POLARITY.addOutSuffix(Aorist, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Progressive, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Future, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Narr, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Past, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Cond, VERB_WITH_TENSE);
        VERB_WITH_POLARITY.addOutSuffix(Imp, VERB_WITH_TENSE);

        VERB_WITH_TENSE.addOutSuffix(Cond, VERB_WITH_TENSE);
        VERB_WITH_TENSE.addOutSuffix(Narr, VERB_WITH_TENSE);
        VERB_WITH_TENSE.addOutSuffix(Past, VERB_WITH_TENSE);
    }

    private void registerSwappedPastCond() {
        final Specification<MorphemeContainer> comesAfterPast_dI = comesAfter(Past, "dI");

        VERB_WITH_TENSE.addOutSuffix(A1Sg_Verb_Swapped, VERB_WITH_SWAPPED_PAST_COND);
        A1Sg_Verb_Swapped.addSuffixForm("m", comesAfterPast_dI);

        VERB_WITH_TENSE.addOutSuffix(A2Sg_Verb_Swapped, VERB_WITH_SWAPPED_PAST_COND);
        A2Sg_Verb_Swapped.addSuffixForm("n", comesAfterPast_dI);

        VERB_WITH_TENSE.addOutSuffix(A1Pl_Verb_Swapped, VERB_WITH_SWAPPED_PAST_COND);
        A1Pl_Verb_Swapped.addSuffixForm("!k", comesAfterPast_dI);

        VERB_WITH_TENSE.addOutSuffix(A2Pl_Verb_Swapped, VERB_WITH_SWAPPED_PAST_COND);
        A2Pl_Verb_Swapped.addSuffixForm("nIz", comesAfterPast_dI);

        VERB_WITH_SWAPPED_PAST_COND.addOutSuffix(Cond, VERB_TERMINAL);
    }

    private void registerVerbAgreements() {
        final Specification<MorphemeContainer> comesAfterImperative = comesAfter(Imp);
        final Specification<MorphemeContainer> doesntComeAfterImperative = doesnt(comesAfterImperative);
        final Specification<MorphemeContainer> comes_after_empty_imperative = comesAfter(Imp, "");
        final Specification<MorphemeContainer> doesnt_come_after_empty_imperative = doesnt(comes_after_empty_imperative);

        VERB_WITH_TENSE.addOutSuffix(A1Sg_Verb, VERB_TERMINAL_TRANSFER);
        A1Sg_Verb.addSuffixForm("+Im");
        A1Sg_Verb.addSuffixForm("yIm");   //"yap-makta-yım", gel-meli-yim

        VERB_WITH_TENSE.addOutSuffix(A2Sg_Verb, VERB_TERMINAL_TRANSFER);
        A2Sg_Verb.addSuffixForm("n", doesntComeAfterImperative.and(doesntComeAfter(Opt)));
        A2Sg_Verb.addSuffixForm("sIn", doesntComeAfterImperative);
        A2Sg_Verb.addSuffixForm("", comesAfterImperative);

        VERB_WITH_TENSE.addOutSuffix(A3Sg_Verb, VERB_TERMINAL_TRANSFER);
        A3Sg_Verb.addSuffixForm("", doesntComeAfterImperative);
        A3Sg_Verb.addSuffixForm("sIn", comesAfterImperative);

        VERB_WITH_TENSE.addOutSuffix(A1Pl_Verb, VERB_TERMINAL_TRANSFER);
        A1Pl_Verb.addSuffixForm("+Iz", doesntComeAfter(Opt));
        A1Pl_Verb.addSuffixForm("!k", Specifications.or(comesAfter(Past), comesAfter(Past_Ques), comesAfter(Cond), comesAfter(Desr)));   // only for "gel-di-k", "gelmis mi-ydi-k" or "gelsek"
        A1Pl_Verb.addSuffixForm("yIz", doesntComeAfter(Opt));   // "yap-makta-yız" OR "gel-me-yiz"
        A1Pl_Verb.addSuffixForm("lIm", comesAfter(Opt));    // only for "gel-e-lim"

        VERB_WITH_TENSE.addOutSuffix(A2Pl_Verb, VERB_TERMINAL_TRANSFER);
        A2Pl_Verb.addSuffixForm("", comesAfterImperative.and(doesnt_come_after_empty_imperative));
        A2Pl_Verb.addSuffixForm("sInIz", doesntComeAfterImperative);
        A2Pl_Verb.addSuffixForm("nIz", doesntComeAfterImperative);
        A2Pl_Verb.addSuffixForm("+yIn", comes_after_empty_imperative);
        A2Pl_Verb.addSuffixForm("+yInIz", comes_after_empty_imperative);

        VERB_WITH_TENSE.addOutSuffix(A3Pl_Verb, VERB_TERMINAL_TRANSFER);
        A3Pl_Verb.addSuffixForm("lAr", doesntComeAfterImperative);
        A3Pl_Verb.addSuffixForm("sInlAr", comesAfterImperative);
    }

    private void registerModalVerbs() {
        final Specification<MorphemeContainer> followed_by_modal_followers = Specifications.or(
                followedBy(Past),
                followedBy(Narr),
                followedByOneFromGroup(Verb_Agreements_Group));

        VERB_WITH_POLARITY.addOutSuffix(Neces, VERB_WITH_TENSE);
        Neces.addSuffixForm("mAl!I");

        VERB_WITH_POLARITY.addOutSuffix(Opt, VERB_WITH_TENSE);
        Opt.addSuffixForm("Ay");
        Opt.addSuffixForm("A", doesntComeAfter(Negative), followed_by_modal_followers);
        Opt.addSuffixForm("yA", null, followed_by_modal_followers);

        VERB_WITH_POLARITY.addOutSuffix(Desr, VERB_WITH_TENSE);
        Desr.addSuffixForm("sA");
    }

    private void registerSwappedA3PlVerbs() {
        VERB_WITH_TENSE.addOutSuffix(A3Pl_Verb, VERB_WITH_SWAPPED_A3PL);

        VERB_WITH_SWAPPED_A3PL.addOutSuffix(Cond, VERB_TERMINAL_TRANSFER);
        VERB_WITH_SWAPPED_A3PL.addOutSuffix(Narr, VERB_TERMINAL_TRANSFER);
        VERB_WITH_SWAPPED_A3PL.addOutSuffix(Past, VERB_TERMINAL_TRANSFER);
    }

    private void registerVerbToVerbDerivations() {
        /*
        |----------------------------------------------------------------------------------------------------------|
        |Name      |  Exists   |  Pos+Pos         |  Pos+Neg         |  Neg+Pos         |  Neg+Neg                 |
        |          | in Oflazer|  Applicable      |  Applicable      |  Applicable      |  Applicable              |
        |----------------------------------------------------------------------------------------------------------|
        |Ability   |   1      |   yapabilir       |  yapamaz         |  yapmayabilir    |  yapmayamaz              |
        |          |          |   1               |      1           |      1           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
        |Hastily   |   1      |   bakiver         |  bakiverme       |  bakmayiver(sin) |  bakmayiverme(sin)       |
        |          |          |   1               |      0           |      1           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
        |EverSince |   1      |   olageldi        |  olagelmedi      |  olmayageldi     |  olmayagelmedi           |
        |          |          |   1               |      1           |      0           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
        |Stay      |   1      |   bakakaldi       |  bakakalmadi     |  bakmayakaldi    |  bakmayakalmadi          |
        |          |          |   1               |      0           |      0           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
        |Almost    |   1      |   duseyazdi       |  duzeyazmadi     |  dusmeyeyazdi    |  dusmeyeyazmadi          |
        |          |          |   1               |      0           |      0           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
        |Once      |   0      |   yapagorsun      |  yapagormesin    |  yapmayagorsun   |  yapmayagormesin         |
        |          |          |   0               |      0           |      1           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
        |Gone      |   0      |   atilagidecektir |atilagitmeyecektir|atilmayagidecektir|atilmayagitmeyecektir     |
        |          |          |   1               |      0           |      0           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
        |Start     |   1      |   calisakoy       |  calisakoyma     |  calismayakoy    |  calismayakoyma          |
        |          |          |   1               |      0           |      0           |      0                   |
        |----------|----------|-------------------|------------------|------------------|--------------------------|
         */

        // not applicable cases are not explicitly prevented by the suffix graph. the focus is parsing anyway...

        VERB_PLAIN_DERIV.addOutSuffix(Able, VERB_ROOT);
        Able.addSuffixForm("+yAbil", null, doesnt(followedBy(Negative)));
        Able.addSuffixForm("+yA", null, followedBy(Negative));

        VERB_POLARITY_DERIV.addOutSuffix(Hastily, VERB_ROOT);
        Hastily.addSuffixForm("+yIver");

        VERB_POLARITY_DERIV.addOutSuffix(EverSince, VERB_ROOT);
        EverSince.addSuffixForm("+yAgel");

        VERB_POLARITY_DERIV.addOutSuffix(Stay, VERB_ROOT);
        Stay.addSuffixForm("+yAkal");

        VERB_POLARITY_DERIV.addOutSuffix(Almost, VERB_ROOT);
        Almost.addSuffixForm("+yAyaz");

        VERB_POLARITY_DERIV.addOutSuffix(Once, VERB_ROOT);
        Once.addSuffixForm("+yAgör");

        VERB_POLARITY_DERIV.addOutSuffix(Gone, VERB_ROOT);
        Gone.addSuffixForm("+yAgi!t");
        Gone.addSuffixForm("+yAgid");

        VERB_POLARITY_DERIV.addOutSuffix(Start, VERB_ROOT);
        Start.addSuffixForm("+yAkoy");


        final Specification<MorphemeContainer> passive_Il = Specifications.or(
                hasLexemeAttributes(LexemeAttribute.Passive_Il),
                Specifications.and(
                        doesntHaveLexemeAttributes(LexemeAttribute.Passive_In),
                        doesntHaveLexemeAttributes(LexemeAttribute.Passive_InIl)));

        VERB_PLAIN_DERIV.addOutSuffix(Pass, VERB_ROOT);
        Pass.addSuffixForm("+In", hasLexemeAttributes(LexemeAttribute.Passive_In));
        Pass.addSuffixForm("+nIl", passive_Il);
        Pass.addSuffixForm("+InIl", hasLexemeAttributes(LexemeAttribute.Passive_InIl));

        VERB_PLAIN_DERIV.addOutSuffix(Recip, VERB_ROOT);
        Recip.addSuffixForm("+Iş", null, null, doesnt(followedByDerivation(Caus)).or(followedByDerivation(Caus, "dIr")));

        VERB_PLAIN_DERIV.addOutSuffix(Caus, VERB_ROOT);
        Caus.addSuffixForm("!t", hasLexemeAttributes(LexemeAttribute.Causative_t).and(doesntComeAfterDerivation(Caus, "!t").and(doesntComeAfterDerivation(Caus, "I!t"))));
        Caus.addSuffixForm("Ir", hasLexemeAttributes(LexemeAttribute.Causative_Ir).and(doesntComeAfterDerivation(Able)));
        Caus.addSuffixForm("I!t", hasLexemeAttributes(LexemeAttribute.Causative_It).and(doesntComeAfterDerivation(Able)));
        Caus.addSuffixForm("Ar", hasLexemeAttributes(LexemeAttribute.Causative_Ar).and(doesntComeAfterDerivation(Able)));
        Caus.addSuffixForm("dIr", hasLexemeAttributes(LexemeAttribute.Causative_dIr));

    }

    private void registerVerbToNounDerivations() {
        VERB_POLARITY_DERIV.addOutSuffix(Inf, NOUN_ROOT);
        Inf.addSuffixForm("mAk");
        Inf.addSuffixForm("mA");
        Inf.addSuffixForm("+yIş");

        VERB_POLARITY_DERIV.addOutSuffix(PastPart_Noun, NOUN_ROOT);
        PastPart_Noun.addSuffixForm("dIk");

        VERB_POLARITY_DERIV.addOutSuffix(FutPart_Noun, NOUN_ROOT);
        FutPart_Noun.addSuffixForm("+yAcAk");

    }

    private void registerVerbToAdverbDerivations() {
        VERB_POLARITY_DERIV.addOutSuffix(AfterDoingSo, ADVERB_ROOT);
        AfterDoingSo.addSuffixForm("+yI!p");

        VERB_POLARITY_DERIV.addOutSuffix(WithoutHavingDoneSo, ADVERB_ROOT);
        WithoutHavingDoneSo.addSuffixForm("mAdAn");
        WithoutHavingDoneSo.addSuffixForm("mAksIzIn");

        VERB_POLARITY_DERIV.addOutSuffix(AsLongAs, ADVERB_ROOT);
        AsLongAs.addSuffixForm("dIkçA");

        VERB_POLARITY_DERIV.addOutSuffix(ByDoingSo, ADVERB_ROOT);
        ByDoingSo.addSuffixForm("+yArA!k");

        VERB_POLARITY_DERIV.addOutSuffix(When, ADVERB_ROOT);
        When.addSuffixForm("+yIncA");

        VERB_POLARITY_DERIV.addOutSuffix(Until, ADVERB_ROOT);
        Until.addSuffixForm("+yIncAyA");

        VERB_POLARITY_DERIV.addOutSuffix(SinceDoingSo, ADVERB_ROOT);
        SinceDoingSo.addSuffixForm("+yAl!I");

        VERB_WITH_TENSE_BEFORE_DERIV.addOutSuffix(A3Pl_Verb_For_Adv, VERB_TENSE_DERIV);
        A3Pl_Verb_For_Adv.addSuffixForm("lAr");

        VERB_TENSE_DERIV.addOutSuffix(While, ADVERB_ROOT);
        While.addSuffixForm("ken");

        VERB_TENSE_DERIV.addOutSuffix(AsIf, ADVERB_ROOT);
        AsIf.addSuffixForm("cAs!InA", Specifications.or(comesAfter(Aorist), comesAfter(Progressive), comesAfter(Future), comesAfter(Narr)));
    }

    private void registerVerbToAdjectiveDerivations() {
        VERB_POLARITY_DERIV.addOutSuffix(PresPart, ADJECTIVE_ROOT);
        PresPart.addSuffixForm("+yAn");

        VERB_POLARITY_DERIV.addOutSuffix(PastPart_Adj, ADJECTIVE_PART_WITHOUT_POSSESSION);
        PastPart_Adj.addSuffixForm("dIk");

        VERB_POLARITY_DERIV.addOutSuffix(FutPart_Adj, ADJECTIVE_PART_WITHOUT_POSSESSION);
        FutPart_Adj.addSuffixForm("+yAcAk");

        VERB_POLARITY_DERIV.addOutSuffix(Agt_Verb_to_Adj, ADJECTIVE_ROOT);
        Agt_Verb_to_Adj.addSuffixForm("+yIcI");


        VERB_WITH_POLARITY.addOutSuffix(Aorist_to_Adj, VERB_TENSE_ADJ_DERIV);
        Aorist_to_Adj.addSuffixForm("+Ir", hasLexemeAttributes(LexemeAttribute.Aorist_I));
        Aorist_to_Adj.addSuffixForm("+Ar");
        Aorist_to_Adj.addSuffixForm("z", comesAfter(Negative));    // gel-me-z

        VERB_WITH_POLARITY.addOutSuffix(Future_to_Adj, VERB_TENSE_ADJ_DERIV);
        Future_to_Adj.addSuffixForm("+yAcAk");

        VERB_WITH_POLARITY.addOutSuffix(Narr_to_Adj, VERB_TENSE_ADJ_DERIV);
        Narr_to_Adj.addSuffixForm("mIş");
        Narr_to_Adj.addSuffixForm("ymIş");

    }

    private void registerAdjectiveToAdjectiveDerivations() {
        ADJECTIVE_DERIV.addOutSuffix(JustLike_Adj, ADJECTIVE_ROOT);
        JustLike_Adj.addSuffixForm("+ImsI");

        ADJECTIVE_DERIV.addOutSuffix(Equ_Adj, ADJECTIVE_ROOT);
        Equ_Adj.addSuffixForm("cA");

        ADJECTIVE_DERIV.addOutSuffix(Quite, ADJECTIVE_ROOT);
        Quite.addSuffixForm("cA");

    }

    private void registerAdjectiveToAdverbDerivations() {
        ADJECTIVE_DERIV.addOutSuffix(Ly, ADVERB_ROOT);
        Ly.addSuffixForm("cA");

    }

    private void registerAdjectiveToNounDerivations() {
        ADJECTIVE_DERIV.addOutSuffix(Ness, NOUN_ROOT);
        Ness.addSuffixForm("lIk");

    }

    private void registerAdjectiveToVerbDerivations() {
        ADJECTIVE_DERIV.addOutSuffix(Become_Adj, VERB_ROOT);
        Become_Adj.addSuffixForm("lAş");
    }

    private void registerAdjectivePossessions() {
        ADJECTIVE_PART_WITHOUT_POSSESSION.addOutSuffix(Pnon_Adj, ADJECTIVE_TERMINAL_TRANSFER);
        Pnon_Adj.addSuffixForm("");

        ADJECTIVE_PART_WITHOUT_POSSESSION.addOutSuffix(P1Sg_Adj, ADJECTIVE_TERMINAL_TRANSFER);
        P1Sg_Adj.addSuffixForm("+Im");

        ADJECTIVE_PART_WITHOUT_POSSESSION.addOutSuffix(P2Sg_Adj, ADJECTIVE_TERMINAL_TRANSFER);
        P2Sg_Adj.addSuffixForm("+In");

        ADJECTIVE_PART_WITHOUT_POSSESSION.addOutSuffix(P3Sg_Adj, ADJECTIVE_TERMINAL_TRANSFER);
        P3Sg_Adj.addSuffixForm("+sI");

        ADJECTIVE_PART_WITHOUT_POSSESSION.addOutSuffix(P1Pl_Adj, ADJECTIVE_TERMINAL_TRANSFER);
        P1Pl_Adj.addSuffixForm("+ImIz");

        ADJECTIVE_PART_WITHOUT_POSSESSION.addOutSuffix(P2Pl_Adj, ADJECTIVE_TERMINAL_TRANSFER);
        P2Pl_Adj.addSuffixForm("+InIz");

        ADJECTIVE_PART_WITHOUT_POSSESSION.addOutSuffix(P3Pl_Adj, ADJECTIVE_TERMINAL_TRANSFER);
        P3Pl_Adj.addSuffixForm("lAr!I");

    }

    private void registerPronounAgreements() {
        PRONOUN_ROOT.addOutSuffix(A1Sg_Pron, PRONOUN_WITH_AGREEMENT);
        //A1Sg_Pron forms are predefined, "ben" and "kendi"

        PRONOUN_ROOT.addOutSuffix(A2Sg_Pron, PRONOUN_WITH_AGREEMENT);
        //A2Sg_Pron forms are predefined, "sen" and "kendi"

        PRONOUN_ROOT.addOutSuffix(A3Sg_Pron, PRONOUN_WITH_AGREEMENT);
        A3Sg_Pron.addSuffixForm("");
        //A3Sg_Pron forms for "o", "bu", "su", "kendi" are predefined

        PRONOUN_ROOT.addOutSuffix(A1Pl_Pron, PRONOUN_WITH_AGREEMENT);
        //A1Pl_Pron forms are predefined, "biz" and "kendi"

        PRONOUN_ROOT.addOutSuffix(A2Pl_Pron, PRONOUN_WITH_AGREEMENT);
        //A2Pl_Pron forms are predefined, "siz" and "kendi"

        PRONOUN_ROOT.addOutSuffix(A3Pl_Pron, PRONOUN_WITH_AGREEMENT);
        A3Pl_Pron.addSuffixForm("lAr");
        //A3Pl_Pron forms for "onlar", "bunlar", "sunlar", "kendileri" are predefined

    }

    private void registerPronounPossessions() {
        PRONOUN_WITH_AGREEMENT.addOutSuffix(Pnon_Pron, PRONOUN_WITH_POSSESSION);
        Pnon_Pron.addSuffixForm("");
        //Pnon_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_AGREEMENT.addOutSuffix(P1Sg_Pron, PRONOUN_WITH_POSSESSION);
        P1Sg_Pron.addSuffixForm("+Im");
        //P1Sg_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_AGREEMENT.addOutSuffix(P2Sg_Pron, PRONOUN_WITH_POSSESSION);
        P2Sg_Pron.addSuffixForm("+In");
        //P2Sg_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_AGREEMENT.addOutSuffix(P3Sg_Pron, PRONOUN_WITH_POSSESSION);
        P3Sg_Pron.addSuffixForm("+sI");
        //P3Sg_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_AGREEMENT.addOutSuffix(P1Pl_Pron, PRONOUN_WITH_POSSESSION);
        P1Pl_Pron.addSuffixForm("+ImIz");
        //P1Pl_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_AGREEMENT.addOutSuffix(P2Pl_Pron, PRONOUN_WITH_POSSESSION);
        P2Pl_Pron.addSuffixForm("+InIz");
        //P2Pl_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_AGREEMENT.addOutSuffix(P3Pl_Pron, PRONOUN_WITH_POSSESSION);
        P3Pl_Pron.addSuffixForm("lAr!I");
        P3Pl_Pron.addSuffixForm("!I", comesAfter(A3Pl_Pron));
        //P3Pl_Pron forms for ""ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

    }

    private void registerPronounCases() {
        final Specification<MorphemeContainer> comes_after_P3 = Specifications.or(
                comesAfter(P3Sg_Pron),
                comesAfter(P3Pl_Pron),
                comesAfterDerivation(RelPron_A3Sg_Noun),
                comesAfterDerivation(RelPron_A3Sg_Pron)
        );

        final Specification<MorphemeContainer> doesnt_come_after_P3 = comes_after_P3.not();

        PRONOUN_WITH_POSSESSION.addOutSuffix(Nom_Pron, PRONOUN_WITH_CASE);
        Nom_Pron.addSuffixForm("");

        PRONOUN_WITH_POSSESSION.addOutSuffix(Nom_Pron_Deriv, PRONOUN_NOM_DERIV);
        Nom_Pron_Deriv.addSuffixForm("", comesAfter(Pnon_Pron));

        PRONOUN_WITH_POSSESSION.addOutSuffix(Acc_Pron, PRONOUN_WITH_CASE);
        Acc_Pron.addSuffixForm("+yI", doesnt_come_after_P3);
        Acc_Pron.addSuffixForm("nI", comes_after_P3);
        //Acc_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "b", "su", "kendi" are predefined

        PRONOUN_WITH_POSSESSION.addOutSuffix(Dat_Pron, PRONOUN_WITH_CASE);
        Dat_Pron.addSuffixForm("+yA", doesnt_come_after_P3);
        Dat_Pron.addSuffixForm("nA", comes_after_P3);
        //Dat_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_POSSESSION.addOutSuffix(Loc_Pron, PRONOUN_WITH_CASE);
        Loc_Pron.addSuffixForm("dA", doesnt_come_after_P3);
        Loc_Pron.addSuffixForm("ndA", comes_after_P3);
        //Loc_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_POSSESSION.addOutSuffix(Abl_Pron, PRONOUN_WITH_CASE);
        Abl_Pron.addSuffixForm("dAn", doesnt_come_after_P3);
        Abl_Pron.addSuffixForm("ndAn", comes_after_P3);
        //Abl_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_POSSESSION.addOutSuffix(Gen_Pron, PRONOUN_WITH_CASE);
        Gen_Pron.addSuffixForm("+nIn");
        //Gen_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_POSSESSION.addOutSuffix(Ins_Pron, PRONOUN_WITH_CASE);
        Ins_Pron.addSuffixForm("+ylA");
        //Ins_Pron forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "kendi" are predefined

        PRONOUN_WITH_POSSESSION.addOutSuffix(AccordingTo, PRONOUN_WITH_CASE);
        AccordingTo.addSuffixForm("cA");
        //AccordingTo forms for "ben", "sen", "o", "biz", "siz", "onlar", "bu", "su", "hepsi" are predefined
    }

    private void registerPronounToAdjectiveSuffixes() {
        final Specification<MorphemeContainer> applies_to_bu_su_o = Specifications.or(
                appliesToRoot("o"),
                appliesToRoot("bu"),
                appliesToRoot("şu")
        );

        final Specification<MorphemeContainer> comes_after_A3Sg_pnon = Specifications.and(
                comesAfter(A3Sg_Pron),
                comesAfter(Pnon_Pron)
        );

        final Specification<MorphemeContainer> comes_after_bu_su_o_pnon = Specifications.and(
                comes_after_A3Sg_pnon,
                applies_to_bu_su_o
        );

        PRONOUN_NOM_DERIV.addOutSuffix(Without_Pron, ADJECTIVE_ROOT);
        Without_Pron.addSuffixForm("sIz", doesnt(comes_after_bu_su_o_pnon));  // ben-siz, onlar-siz
        Without_Pron.addSuffixForm("nsuz", comes_after_bu_su_o_pnon); // o-nsuz, bu-nsuz, su-nsuz

        PRONOUN_DERIV_WITH_CASE.addOutSuffix(PointQual_Pron, ADJECTIVE_ROOT);
        PointQual_Pron.addSuffixForm("ki", comesAfter(Loc_Pron));

    }

    private void registerPronounToPronounDerivations() {
        final Specification<MorphemeContainer> comes_after_Gen_Pron = comesAfter(Gen_Pron);   // since it only works for pronouns after Gen : "oraninki", "senin oraninki", "benimki"
        final Specification<MorphemeContainer> followed_by_Pnon_Pron = followedBy(Pnon_Pron); // since sth like this doesn"t work: "oraninkim", "benimkin"

        PRONOUN_DERIV_WITH_CASE.addOutSuffix(RelPron_A3Sg_Noun, PRONOUN_WITH_AGREEMENT);
        RelPron_A3Sg_Noun.addSuffixForm("ki", comes_after_Gen_Pron, followed_by_Pnon_Pron);

        PRONOUN_DERIV_WITH_CASE.addOutSuffix(RelPron_A3Pl_Noun, PRONOUN_WITH_AGREEMENT);
        RelPron_A3Pl_Noun.addSuffixForm("kiler", comes_after_Gen_Pron, followed_by_Pnon_Pron);

    }

    private void registerAdverbToAdjectiveDerivations() {
        final Specification<MorphemeContainer> PointQual_form_ku_applicable = Specifications.or(
                appliesToRoot("bugün"),
                appliesToRoot("dün"),
                appliesToRoot("gün"),
                appliesToRoot("öbür")
        );
        //TODO: only applies to time adverbs

        ADVERB_DERIV.addOutSuffix(PointQual_Adv, ADJECTIVE_ROOT);
        PointQual_Adv.addSuffixForm("ki", doesnt(PointQual_form_ku_applicable));
        PointQual_Adv.addSuffixForm("kü", PointQual_form_ku_applicable);
    }

    private void registerQuestionTenses() {
        // Question tenses are all predefined
        QUESTION_ROOT.addOutSuffix(Pres_Ques, QUESTION_WITH_TENSE);
        QUESTION_ROOT.addOutSuffix(Narr_Ques, QUESTION_WITH_TENSE);
        QUESTION_ROOT.addOutSuffix(Past_Ques, QUESTION_WITH_TENSE);
    }

    private void registerQuestionAgreements() {
        // Question agreements are all predefined
        QUESTION_WITH_TENSE.addOutSuffix(A1Sg_Ques, QUESTION_WITH_AGREEMENT);
        QUESTION_WITH_TENSE.addOutSuffix(A2Sg_Ques, QUESTION_WITH_AGREEMENT);
        QUESTION_WITH_TENSE.addOutSuffix(A3Sg_Ques, QUESTION_WITH_AGREEMENT);
        QUESTION_WITH_TENSE.addOutSuffix(A1Pl_Ques, QUESTION_WITH_AGREEMENT);
        QUESTION_WITH_TENSE.addOutSuffix(A2Pl_Ques, QUESTION_WITH_AGREEMENT);
        QUESTION_WITH_TENSE.addOutSuffix(A3Pl_Ques, QUESTION_WITH_AGREEMENT);
    }

}
