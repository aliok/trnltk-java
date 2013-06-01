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

import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.lexicon.SecondaryPos;
import org.trnltk.model.suffix.Suffix;

import java.util.Arrays;
import java.util.Collection;

import static org.trnltk.model.lexicon.PrimaryPos.Numeral;
import static org.trnltk.model.lexicon.SecondaryPos.*;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.DERIVATIONAL;
import static org.trnltk.morphology.morphotactics.SuffixGraphStateType.TRANSFER;
import static org.trnltk.morphology.morphotactics.suffixformspecifications.SuffixFormSpecifications.comesAfter;

public class NumeralSuffixGraph extends BaseSuffixGraph {
    private final SuffixGraphState NUMERAL_CARDINAL_ROOT = registerState("NUMERAL_CARDINAL_ROOT", TRANSFER, Numeral, Cardinal);
    private final SuffixGraphState NUMERAL_CARDINAL_DERIV = registerState("NUMERAL_CARDINAL_DERIV", DERIVATIONAL, Numeral, Cardinal);

    private final SuffixGraphState NUMERAL_DIGIT_CARDINAL_ROOT = registerState("NUMERAL_DIGIT_CARDINAL_ROOT", TRANSFER, Numeral, DigitsCardinal);
    private final SuffixGraphState NUMERAL_DIGIT_ORDINAL_ROOT = registerState("NUMERAL_DIGIT_ORDINAL_ROOT", TRANSFER, Numeral, DigitsOrdinal);
    private final SuffixGraphState NUMERAL_DIGIT_RANGE_ROOT = registerState("NUMERAL_DIGIT_RANGE_ROOT", TRANSFER, Numeral, Range);

    private final SuffixGraphState NUMERAL_ORDINAL_ROOT = registerState("NUMERAL_ORDINAL_ROOT", TRANSFER, Numeral, Ordinal);
    private final SuffixGraphState NUMERAL_ORDINAL_DERIV = registerState("NUMERAL_ORDINAL_DERIV", DERIVATIONAL, Numeral, Ordinal);
    private final SuffixGraphState NUMERAL_RANGE_DERIV = registerState("NUMERAL_RANGE_DERIV", DERIVATIONAL, Numeral, Range);

    private final SuffixGraphState DECORATED_ADJECTIVE_ROOT = getSuffixGraphState("ADJECTIVE_ROOT");


    // suffixes
    /////////////// Cardinal numbers to Adjective derivations
    private final Suffix NumbersOf = registerSuffix("NumbersOf");
    private final Suffix OfUnit_Number = registerSuffix("OfUnit_Number", "OfUnit");

    /////////////// Cardinal digits suffixes
    private final Suffix Apos_Digit = registerSuffix("Apos_Digit", "Apos");

    /////////////// Cardinal to Ordinal Derivation
    private final Suffix Ordinal_Text = registerSuffix("Ordinal_Text", null, "OrdT");

    ////////////// Range to Ordinal Derivation
    private final Suffix Ordinal_Dot = registerSuffix("Ordinal_Dot", null, "OrdDot");

    public NumeralSuffixGraph() {
        super();
    }

    public NumeralSuffixGraph(SuffixGraph decorated) {
        super(decorated);
    }

    @Override
    protected SuffixGraphState doGetDefaultStateForRoot(Root root) {
        final PrimaryPos primaryPos = root.getLexeme().getPrimaryPos();
        final SecondaryPos secondaryPos = root.getLexeme().getSecondaryPos();
        if (Numeral.equals(primaryPos)) {
            switch (secondaryPos) {
                case DigitsCardinal:
                    return NUMERAL_DIGIT_CARDINAL_ROOT;
                case DigitsOrdinal:
                    return NUMERAL_DIGIT_ORDINAL_ROOT;
                case Cardinal:
                    return NUMERAL_CARDINAL_ROOT;
                case Ordinal:
                    return NUMERAL_ORDINAL_ROOT;
                case Range:
                    return NUMERAL_DIGIT_RANGE_ROOT;
            }
        }

        return null;
    }

    @Override
    protected Collection<? extends SuffixGraphState> doGetRootSuffixGraphStates() {
        return Arrays.asList(
                NUMERAL_CARDINAL_ROOT,
                NUMERAL_DIGIT_CARDINAL_ROOT, NUMERAL_DIGIT_ORDINAL_ROOT, NUMERAL_DIGIT_RANGE_ROOT,
                NUMERAL_ORDINAL_ROOT);
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

        NUMERAL_DIGIT_CARDINAL_ROOT.addOutSuffix(registerFreeTransitionSuffix("DigitsC_Free_Transition_1"), NUMERAL_CARDINAL_DERIV);
        NUMERAL_DIGIT_ORDINAL_ROOT.addOutSuffix(registerFreeTransitionSuffix("DigitsO_Free_Transition_1"), NUMERAL_ORDINAL_DERIV);
        NUMERAL_DIGIT_RANGE_ROOT.addOutSuffix(registerFreeTransitionSuffix("Range_Free_Transition_1"), NUMERAL_RANGE_DERIV);

        NUMERAL_CARDINAL_DERIV.addOutSuffix(registerZeroTransitionSuffix("Numeral_Zero_Transition_1"), DECORATED_ADJECTIVE_ROOT);
        NUMERAL_ORDINAL_DERIV.addOutSuffix(registerZeroTransitionSuffix("Numeral_Zero_Transition_2"), DECORATED_ADJECTIVE_ROOT);
        NUMERAL_RANGE_DERIV.addOutSuffix(registerZeroTransitionSuffix("Numeral_Zero_Transition_3"), DECORATED_ADJECTIVE_ROOT);
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
        NUMERAL_DIGIT_ORDINAL_ROOT.addOutSuffix(Apos_Digit, NUMERAL_ORDINAL_DERIV);
        NUMERAL_DIGIT_RANGE_ROOT.addOutSuffix(Apos_Digit, NUMERAL_CARDINAL_DERIV);
        Apos_Digit.addSuffixForm("'");

        NUMERAL_CARDINAL_DERIV.addOutSuffix(Ordinal_Text, NUMERAL_ORDINAL_ROOT);
        // applies only to digits. 'birinci' is marked as ordinal already in the numeral master dictionary
        Ordinal_Text.addSuffixForm("+IncI", comesAfter(Apos_Digit));

        NUMERAL_RANGE_DERIV.addOutSuffix(Ordinal_Dot, NUMERAL_DIGIT_ORDINAL_ROOT);
        Ordinal_Dot.addSuffixForm(".");
    }
}
