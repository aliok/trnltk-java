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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Formatter {

    private static final ImmutableSet<Pair<SyntacticCategory, SecondarySyntacticCategory>> DERIVATION_GROUPING_FORMAT_SECONDARY_POS_TO_SKIP
            = new ImmutableSet.Builder<Pair<SyntacticCategory, SecondarySyntacticCategory>>()
            .add(Pair.of(SyntacticCategory.ADVERB, SecondarySyntacticCategory.QUESTION))
            .add(Pair.of(SyntacticCategory.ADVERB, SecondarySyntacticCategory.TIME))
            .add(Pair.of(SyntacticCategory.ADJECTIVE, SecondarySyntacticCategory.QUESTION))
            .build();

    /**
     * @param morphemeContainer the MC
     * @return kitap+Noun+A3sg+Pnon+Dat for word 'kitaba'
     */
    public static String formatMorphemeContainer(final MorphemeContainer morphemeContainer) {
        final Root root = morphemeContainer.getRoot();
        final Lexeme lexeme = root.getLexeme();

        final StringBuilder b = new StringBuilder();
        b.append(String.format("%s+%s", lexeme.getLemmaRoot(), morphemeContainer.getRootState().getPrimaryPos().getStringForm()));

        if (lexeme.getSecondaryPos() != null)
            b.append("+").append(lexeme.getSecondaryPos());

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

    public static Collection<String> formatMorphemeContainers(final Collection<MorphemeContainer> morphemeContainers) {
        return Collections2.transform(morphemeContainers, new Function<MorphemeContainer, String>() {
            @Override
            public String apply(MorphemeContainer input) {
                return Formatter.formatMorphemeContainer(input);
            }
        });
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
        if (lexeme.getSecondaryPos() != null)
            b.append("+").append(lexeme.getSecondaryPos().getStringForm());

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

    /**
     * @param morphemeContainer the MC
     * @return (1,"kitap+Noun+A3sg+Pnon+Dat")
     */
    public static String formatMorphemeContainerWithDerivationGrouping(MorphemeContainer morphemeContainer) {
        final Lexeme lexeme = morphemeContainer.getRoot().getLexeme();
        final SyntacticCategory primaryPos = lexeme.getSyntacticCategory();
        final SecondarySyntacticCategory secondaryPos = lexeme.getSecondarySyntacticCategory();

        final String lemmaRoot = lexeme.getLemmaRoot();

        final String secondaryPosStr;
        if (secondaryPos != null) {
            if (DERIVATION_GROUPING_FORMAT_SECONDARY_POS_TO_SKIP.contains(Pair.of(primaryPos, secondaryPos)))
                secondaryPosStr = null;
            else
                secondaryPosStr = secondaryPos.getLookupKey();
        } else {
            secondaryPosStr = null;
        }

        final String formattedLexeme = Joiner.on("+").skipNulls().join(Arrays.asList(lemmaRoot, primaryPos.getLookupKey(), secondaryPosStr));

        final List<List<String>> groups = new ArrayList<List<String>>();
        List<String> currentGroup = new ArrayList<String>(Arrays.asList(formattedLexeme));

        for (Transition transition : morphemeContainer.getTransitions()) {
            if (transition.isDerivational()) {
                groups.add(currentGroup);
                currentGroup = new ArrayList<String>(Arrays.asList(transition.getTargetState().getSyntacticCategory().getLookupKey()));
            }

            final Suffix suffix = transition.getSuffixFormApplication().getSuffixForm().getSuffix();
            if (suffix instanceof FreeTransitionSuffix)
                continue;
            else
                currentGroup.add(suffix.getPrettyName());

        }

        groups.add(currentGroup);

        final List<String> formattedGroups = Lists.transform(groups, new Function<List<String>, String>() {
            @Override
            public String apply(List<String> input) {
                return Joiner.on("+").join(input);
            }
        });

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < formattedGroups.size(); i++) {
            final String formattedGroup = formattedGroups.get(i);
            builder.append("(").append(i + 1).append(",\"").append(formattedGroup).append("\")");
        }

        return builder.toString();
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

    public static Collection<String> formatMorphemeContainersWithDerivationGrouping(Collection<MorphemeContainer> morphemeContainers) {
        return Collections2.transform(morphemeContainers, new Function<MorphemeContainer, String>() {
            @Override
            public String apply(MorphemeContainer input) {
                return Formatter.formatMorphemeContainerWithDerivationGrouping(input);
            }
        });
    }
}
