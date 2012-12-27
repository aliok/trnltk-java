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
import zemberek3.lexicon.PrimaryPos;

import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.DERIVATIONAL;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TRANSFER;
import static zemberek3.lexicon.PrimaryPos.Numeral;

public class NumeralSuffixGraph extends BaseSuffixGraph {
    private final SuffixGraphState NUMERAL_CARDINAL_ROOT = registerState("NUMERAL_CARDINAL_ROOT", TRANSFER, Numeral);
    private final SuffixGraphState NUMERAL_CARDINAL_DERIV = registerState("NUMERAL_CARDINAL_DERIV", DERIVATIONAL, Numeral);

    private final SuffixGraphState NUMERAL_DIGIT_CARDINAL_ROOT = registerState("NUMERAL_DIGIT_CARDINAL_ROOT", TRANSFER, Numeral);

    private final SuffixGraphState NUMERAL_ORDINAL_ROOT = registerState("NUMERAL_ORDINAL_ROOT", TRANSFER, Numeral);
    private final SuffixGraphState NUMERAL_ORDINAL_DERIV = registerState("NUMERAL_ORDINAL_DERIV", DERIVATIONAL, Numeral);

    private final SuffixGraphState DECORATED_ADJECTIVE_ROOT = getSuffixGraphState("ADJECTIVE_ROOT");


    // suffixes
    /////////////// Cardinal numbers to Adjective derivations
    private final Suffix NumbersOf = registerSuffix("NumbersOf");
    private final Suffix OfUnit_Number = registerSuffix("OfUnit_Number", "OfUnit");

    /////////////// Cardinal digits suffixes
    private final Suffix Apos_Digit = registerSuffix("Apos_Digit", "Apos");

    public NumeralSuffixGraph() {
        super();
    }

    public NumeralSuffixGraph(SuffixGraph decorated) {
        super(decorated);
    }

    @Override
    protected SuffixGraphState doGetDefaultStateForRoot(Root root) {
        final PrimaryPos primaryPos = root.getLexeme().getPrimaryPos();
        final SecondarySyntacticCategory secondarySyntacticCategory = root.getLexeme().getSecondarySyntacticCategory();
        if (Numeral.equals(primaryPos)) {
            switch (secondarySyntacticCategory) {
                case DIGITS:
                    return NUMERAL_DIGIT_CARDINAL_ROOT;
                case CARD:
                    return NUMERAL_CARDINAL_ROOT;
                case ORD:
                    return NUMERAL_ORDINAL_ROOT;
            }
        }

        return null;
    }

    @Override
    protected void registerEverything() {
        this.registerFreeTransitions();
        this.createSuffixEdges();
    }

    private void registerFreeTransitions() {
        ///////////////  Free transitions
        NUMERAL_CARDINAL_ROOT.addOutSuffix(registerFreeTransitionSuffix("Numeral_Free_Transition_1"), NUMERAL_CARDINAL_DERIV);
        NUMERAL_ORDINAL_ROOT.addOutSuffix(registerFreeTransitionSuffix("Numeral_Free_Transition_2"), NUMERAL_ORDINAL_DERIV);

        NUMERAL_DIGIT_CARDINAL_ROOT.addOutSuffix(registerFreeTransitionSuffix("Digits_Free_Transition_1"), NUMERAL_CARDINAL_DERIV);

        NUMERAL_CARDINAL_DERIV.addOutSuffix(registerZeroTransitionSuffix("Numeral_Zero_Transition_1"), DECORATED_ADJECTIVE_ROOT);
        NUMERAL_ORDINAL_DERIV.addOutSuffix(registerZeroTransitionSuffix("Numeral_Zero_Transition_2"), DECORATED_ADJECTIVE_ROOT);
    }

    private void createSuffixEdges() {
        this.registerNumeralSuffixes();
    }

    private void registerNumeralSuffixes() {
        this.registerCardinalToAdjectiveSuffixes();
        this.registerDigitsSuffixes();
    }

    private void registerCardinalToAdjectiveSuffixes() {
        NUMERAL_CARDINAL_DERIV.addOutSuffix(NumbersOf, DECORATED_ADJECTIVE_ROOT);
        NumbersOf.addSuffixForm("lArcA");

        NUMERAL_CARDINAL_DERIV.addOutSuffix(OfUnit_Number, DECORATED_ADJECTIVE_ROOT);
        OfUnit_Number.addSuffixForm("lIk");
    }

    private void registerDigitsSuffixes() {
        NUMERAL_DIGIT_CARDINAL_ROOT.addOutSuffix(Apos_Digit, NUMERAL_CARDINAL_DERIV);
        Apos_Digit.addSuffixForm("'");
    }
}
