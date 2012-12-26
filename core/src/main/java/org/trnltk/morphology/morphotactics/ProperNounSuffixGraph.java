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

package org.trnltk.morphology.morphotactics;

import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondarySyntacticCategory;
import org.trnltk.morphology.model.Suffix;
import org.trnltk.morphology.model.SyntacticCategory;

import static org.trnltk.morphology.model.SyntacticCategory.NOUN;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TERMINAL;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TRANSFER;

public class ProperNounSuffixGraph extends BaseSuffixGraph {

    private final SuffixGraphState PROPER_NOUN_ROOT = registerState("PROPER_NOUN_ROOT", TRANSFER, NOUN);
    private final SuffixGraphState PROPER_NOUN_WITH_AGREEMENT = registerState("PROPER_NOUN_WITH_AGREEMENT", TRANSFER, NOUN);
    private final SuffixGraphState PROPER_NOUN_WITH_POSSESSION = registerState("PROPER_NOUN_WITH_POSSESSION", TRANSFER, NOUN);
    private final SuffixGraphState PROPER_NOUN_WITH_CASE = registerState("PROPER_NOUN_WITH_CASE", TRANSFER, NOUN);
    private final SuffixGraphState PROPER_NOUN_TERMINAL = registerState("PROPER_NOUN_TERMINAL", TERMINAL, NOUN);

    // from decorated
    private final SuffixGraphState DECORATED_NOUN_ROOT = getSuffixGraphState("NOUN_ROOT");

    // suffixes

    // free transitions, but named
    private final Suffix A3Sg_Proper_Noun = registerSuffix("A3Sg_Proper_Noun", null, "A3sg");
    private final Suffix Pnon_Proper_Noun = registerSuffix("Pnon_Proper_Noun", null, "Pnon");
    private final Suffix Nom_Proper_Noun = registerSuffix("Nom_Proper_Noun", null, "Nom");

    private final Suffix Apos_Proper_Noun = registerSuffix("Apos_Proper_Noun", null, "Apos");

    public ProperNounSuffixGraph() {
        super();
    }

    public ProperNounSuffixGraph(SuffixGraph decorated) {
        super(decorated);
    }

    @Override
    protected SuffixGraphState doGetDefaultStateForRoot(Root root) {
        final SyntacticCategory syntacticCategory = root.getLexeme().getSyntacticCategory();
        final SecondarySyntacticCategory secondarySyntacticCategory = root.getLexeme().getSecondarySyntacticCategory();
        if (secondarySyntacticCategory == null)
            return null;

        if (NOUN.equals(syntacticCategory) &&
                (secondarySyntacticCategory.equals(SecondarySyntacticCategory.PROPER_NOUN) || secondarySyntacticCategory.equals(SecondarySyntacticCategory.ABBREVIATION)))
            return PROPER_NOUN_ROOT;

        return null;
    }

    @Override
    protected void registerEverything() {
        this.registerFreeTransitions();
        this.createSuffixEdges();
    }

    private void registerFreeTransitions() {
        ///////////////  Free transitions
        PROPER_NOUN_WITH_CASE.addOutSuffix(registerFreeTransitionSuffix("Proper_Noun_Free_Transition_1"), PROPER_NOUN_TERMINAL);
    }

    private void createSuffixEdges() {
        PROPER_NOUN_ROOT.addOutSuffix(A3Sg_Proper_Noun, PROPER_NOUN_WITH_AGREEMENT);
        A3Sg_Proper_Noun.addSuffixForm("");

        PROPER_NOUN_WITH_AGREEMENT.addOutSuffix(Pnon_Proper_Noun, PROPER_NOUN_WITH_POSSESSION);
        Pnon_Proper_Noun.addSuffixForm("");

        PROPER_NOUN_WITH_POSSESSION.addOutSuffix(Nom_Proper_Noun, PROPER_NOUN_WITH_CASE);
        Nom_Proper_Noun.addSuffixForm("");

        PROPER_NOUN_ROOT.addOutSuffix(Apos_Proper_Noun, DECORATED_NOUN_ROOT);
        Apos_Proper_Noun.addSuffixForm("'");
    }

}
