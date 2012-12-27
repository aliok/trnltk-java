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

package org.trnltk.morphology.model;

import org.apache.commons.lang3.StringUtils;

public class Formatter {

    /**
     * @param morphemeContainer the MC
     * @return kitap+Noun+A3sg+Pnon+Dat for word 'kitaba'
     */
    public static String formatMorphemeContainer(final MorphemeContainer morphemeContainer) {
        final Root root = morphemeContainer.getRoot();
        final Lexeme lexeme = root.getLexeme();

        final StringBuilder b = new StringBuilder();
        b.append(String.format("%s+%s", lexeme.getLemmaRoot(), morphemeContainer.getRootState().getPrimaryPos().getStringForm()));

        if (lexeme.getSecondarySyntacticCategory() != null)
            b.append("+").append(lexeme.getSecondarySyntacticCategory());

        if (morphemeContainer.hasTransitions()) {
            for (Transition transition : morphemeContainer.getTransitions()) {
                // don't add free transitions to the formatted string
                final Suffix suffix = transition.getSuffixFormApplication().getSuffixForm().getSuffix();
                if (suffix instanceof FreeTransitionSuffix)
                    continue;

                b.append("+").append(formatTransition(transition, false));
            }
        }

        return b.toString();
    }

    /**
     * @param morphemeContainer the MC
     * @return kitab(kitap)+Noun+A3sg+Pnon+Dat(+yA[a]) for word 'kitaba'
     */
    public static String formatMorphemeContainerWithForms(final MorphemeContainer morphemeContainer) {
        final StringBuilder b = new StringBuilder();
        final Root root = morphemeContainer.getRoot();
        final Lexeme lexeme = root.getLexeme();

        b.append(String.format("%s(%s)+%s", root.getSequence().getUnderlyingString(), lexeme.getLemma(), morphemeContainer.getRootState().getPrimaryPos().getStringForm()));
        if (lexeme.getSecondarySyntacticCategory() != null)
            b.append("+").append(lexeme.getSecondarySyntacticCategory().getLookupKey());

        if (morphemeContainer.hasTransitions()) {
            for (Transition transition : morphemeContainer.getTransitions()) {
                // don't add free transitions to the formatted string
                final Suffix suffix = transition.getSuffixFormApplication().getSuffixForm().getSuffix();
                if (suffix instanceof FreeTransitionSuffix)
                    continue;

                b.append("+").append(formatTransition(transition, true));
            }
        }

        return b.toString();
    }

    private static String formatTransition(final Transition transition, final boolean includeForm) {
        final StringBuilder b = new StringBuilder();

        if (transition.isDerivational())
            b.append(transition.getTargetState().getPrimaryPos().getStringForm()).append("+");

        final SuffixForm suffixForm = transition.getSuffixFormApplication().getSuffixForm();
        final String actualSuffixForm = transition.getSuffixFormApplication().getActualSuffixForm();
        if (includeForm && StringUtils.isNotBlank(actualSuffixForm) && StringUtils.isAlphanumeric(actualSuffixForm)) {
            b.append(String.format("%s(%s[%s])", suffixForm.getSuffix().getPrettyName(),
                    suffixForm.getForm().getSuffixFormStr(), actualSuffixForm));
        } else {
            b.append(suffixForm.getSuffix().getPrettyName());
        }

        return b.toString();
    }

}
