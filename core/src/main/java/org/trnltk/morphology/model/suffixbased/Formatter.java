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

package org.trnltk.morphology.model.suffixbased;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.trnltk.morphology.model.Lexeme;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.morphotactics.SuffixGraphState;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Formatter {

    private static final ImmutableSet<Pair<PrimaryPos, SecondaryPos>> DERIVATION_GROUPING_FORMAT_SECONDARY_POS_TO_SKIP
            = new ImmutableSet.Builder<Pair<PrimaryPos, SecondaryPos>>()
            .add(Pair.of(PrimaryPos.Adverb, SecondaryPos.Question))
            .add(Pair.of(PrimaryPos.Adverb, SecondaryPos.Time))
            .add(Pair.of(PrimaryPos.Adjective, SecondaryPos.Question))
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
            for (SuffixTransition suffixTransition : morphemeContainer.getSuffixTransitions()) {
                // don't add free transitions to the formatted string
                final Suffix suffix = suffixTransition.getSuffixFormApplication().getSuffixForm().getSuffix();
                if (suffix instanceof FreeTransitionSuffix)
                    continue;

                b.append("+").append(formatTransition(suffixTransition, false));
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
            for (SuffixTransition suffixTransition : morphemeContainer.getSuffixTransitions()) {
                // don't add free transitions to the formatted string
                final Suffix suffix = suffixTransition.getSuffixFormApplication().getSuffixForm().getSuffix();
                if (suffix instanceof FreeTransitionSuffix)
                    continue;

                b.append("+").append(formatTransition(suffixTransition, true));
            }
        }

        return b.toString();
    }

    /**
     * @param morphemeContainer the MC
     * @return (1,"kitap+Noun+A3sg+Pnon+Dat")
     */
    public static String formatMorphemeContainerWithDerivationGrouping(MorphemeContainer morphemeContainer) {
        return formatMorphemeContainerWithDerivationGrouping(morphemeContainer, true);
    }

    /**
     * @param morphemeContainer the MC
     * @return (1,"kitap+Noun+A3sg+Pnon+Dat") or ("kitap+Noun+A3sg+Pnon+Dat")
     */
    public static String formatMorphemeContainerWithDerivationGrouping(MorphemeContainer morphemeContainer, boolean addIndices) {
        final Lexeme lexeme = morphemeContainer.getRoot().getLexeme();
        final PrimaryPos primaryPos = lexeme.getPrimaryPos();
        final SecondaryPos secondaryPos = lexeme.getSecondaryPos();

        final String lemmaRoot = lexeme.getLemmaRoot();

        final String secondaryPosStr;
        if (secondaryPos != null) {
            if (DERIVATION_GROUPING_FORMAT_SECONDARY_POS_TO_SKIP.contains(Pair.of(primaryPos, secondaryPos)))
                secondaryPosStr = null;
            else
                secondaryPosStr = secondaryPos.getStringForm();
        } else {
            secondaryPosStr = null;
        }

        final String formattedLexeme = Joiner.on("+").skipNulls().join(Arrays.asList(lemmaRoot, primaryPos.getStringForm(), secondaryPosStr));

        final List<List<String>> groups = new ArrayList<List<String>>();
        List<String> currentGroup = new ArrayList<String>(Arrays.asList(formattedLexeme));

        for (SuffixTransition suffixTransition : morphemeContainer.getSuffixTransitions()) {
            if (suffixTransition.isDerivational()) {
                groups.add(currentGroup);
                currentGroup = new ArrayList<String>(Arrays.asList(suffixTransition.getTargetState().getPrimaryPos().getStringForm()));
            }

            final Suffix suffix = suffixTransition.getSuffixFormApplication().getSuffixForm().getSuffix();
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
            builder.append("(");
            if (addIndices)
                builder.append(i + 1).append(",");
            builder.append("\"").append(formattedGroup).append("\")");
        }

        return builder.toString();
    }

    /**
     * @param morphemeContainer the MC
     * @return {Root:"dörd", LemmaRoot:"dört", Parts:[{POS:"Num", SPOS:"Card"}, {POS:"Adj", Suffixes:["Ord"]}, {POS:"Adj", Suffixes:["Ness", "Sth"]}, {POS:"Noun", Suffixes:["A3Sg", "Pnon", "Nom"]}]}
     */
    public static String formatMorphemeContainerDetailed(MorphemeContainer morphemeContainer) {
        try {
            final List<JSONObject> parts = new ArrayList<JSONObject>();
            JSONObject currentPart = null;

            List<SuffixTransition> suffixTransitions = morphemeContainer.getSuffixTransitions();
            for (int i = 0; i < suffixTransitions.size(); i++) {
                SuffixTransition suffixTransition = suffixTransitions.get(i);
                final Suffix suffix = suffixTransition.getSuffixFormApplication().getSuffixForm().getSuffix();

                final SuffixGraphState targetState = suffixTransition.getTargetState();
                final boolean isDerivational = suffixTransition.isDerivational();
                if (isDerivational || i == 0) {
                    if (currentPart != null)
                        parts.add(currentPart);

                    currentPart = new JSONObject();
                    final String currentPos = targetState.getPrimaryPos().getStringForm();
                    final SecondaryPos currentSecondaryPos = targetState.getSecondaryPos();
                    final String currentSpos = currentSecondaryPos == null ? null : currentSecondaryPos.getStringForm();
                    currentPart.put("POS", currentPos);
                    if (StringUtils.isNotBlank(currentSpos))
                        currentPart.put("SPOS", currentSpos);
                }

                if (suffix instanceof FreeTransitionSuffix)
                    continue;

                JSONArray suffixes;
                if (currentPart.has("Suffixes")) {
                    suffixes = currentPart.getJSONArray("Suffixes");
                } else {
                    suffixes = new JSONArray();
                    currentPart.put("Suffixes", suffixes);
                }

                suffixes.put(suffix.getPrettyName());
            }

            if (currentPart != null)
                parts.add(currentPart);

            final Lexeme lexeme = morphemeContainer.getRoot().getLexeme();
            final String rootStr = morphemeContainer.getRoot().getSequence().getUnderlyingString();
            final String lemmaRoot = lexeme.getLemmaRoot();
            final PrimaryPos primaryPos = lexeme.getPrimaryPos();
            final SecondaryPos secondaryPos = lexeme.getSecondaryPos();

            final JSONObject parentObject = new JSONObject();
            parentObject.put("Root", rootStr);
            parentObject.put("LemmaRoot", lemmaRoot);
            parentObject.put("RootPos", primaryPos.getStringForm());
            if (secondaryPos != null)
                parentObject.put("RootSpos", secondaryPos.getStringForm());
            if (CollectionUtils.isNotEmpty(parts))
                parentObject.put("Parts", parts);

            return parentObject.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String formatTransition(final SuffixTransition suffixTransition, final boolean includeForm) {
        final StringBuilder b = new StringBuilder();

        if (suffixTransition.isDerivational()) {
            final SuffixGraphState targetState = suffixTransition.getTargetState();
            b.append(targetState.getPrimaryPos().getStringForm()).append("+");
            if (targetState.getSecondaryPos() != null)
                b.append(targetState.getSecondaryPos().getStringForm()).append("+");
        }

        final SuffixForm suffixForm = suffixTransition.getSuffixFormApplication().getSuffixForm();
        final String actualSuffixForm = suffixTransition.getSuffixFormApplication().getActualSuffixForm();
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
