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

package org.trnltk.morphology.contextless.parser;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.lexicon.SecondaryPos;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.suffix.Suffix;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.model.lexicon.PrimaryPos;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Defines paths for special words / roots.
 * <p/>
 * There are phonetic and morphotactic exceptions for some special words in Turkish.
 * For these words, special suffixes or suffix forms are applied within this class. These exceptions are
 * separated from suffixes and forms defined in a {@link SuffixGraph} since they require a lot of new rules
 * to be created which are only applicable for these special words.
 * <p/>
 * One example is word "ben" whose one usage is <i>1st person <b>singular</b> personal pronoun</i>. Since there is another word
 * for <i>1st person <b>plural</b> personal pronoun</i>, "biz", this word should not accept the suffix <i>plural agreement</i>;
 * ie. "benler" is not valid. However, <i>plural agreement</i> suffix is normally applicable to all pronouns : "kimler", "neler", etc.
 * Thus, all possible paths that could be gone using this root is predefined in the system.
 * <p/>
 * Another example is word "onlar" which is <i>3rd person plural personal pronoun</i>. "onlar" is actually composed of
 * <i>3rd person singular personal pronoun</i>, "o", and the <i>plural agreement</i> "lar". However, suffix form "lar"
 * becomes "nlar" in this particular case. It doesn't make sense to add this as a new morphotactic rule in the
 * {@link SuffixGraph} since we have to define what is applicable (word "o") and what is not applicable (lots of words).
 * Thus, all possible paths in the {@link SuffixGraph} are build in advance for this root.
 * <p/>
 * Paths defined here must be checked when a new root is found for a surface. If found, predefined paths
 * ({@link MorphemeContainer}s) must be used as the starting point of the traversal in the {@link SuffixGraph}.
 * <p/>
 * There is a balance and trade off to define phonetic and morphotactic rules for these kind of words in here and defining
 * the rules in the {@link SuffixGraph}. More added here results in a more hardcoded system. More added in the
 * {@link SuffixGraph} results in a hard-to-maintain graph. Current approach is to define rules here if the exception is
 * for less than 3 roots and these roots are frequent ones in Turkish.
 */
public class PredefinedPaths {

    private final SuffixGraph suffixGraph;
    private final Multimap<String, ? extends Root> rootMap;
    private final SuffixApplier suffixApplier;

    private HashMultimap<Root, MorphemeContainer> morphemeContainerMap;

    public PredefinedPaths(SuffixGraph suffixGraph, Multimap<String, ? extends Root> rootMap, SuffixApplier suffixApplier) {
        this.suffixGraph = suffixGraph;
        this.rootMap = rootMap;
        this.suffixApplier = suffixApplier;
        this.morphemeContainerMap = HashMultimap.create();
    }

    public void initialize() {
        this.createPaths();
    }

    public boolean hasPathsForRoot(Root root) {
        if (this.morphemeContainerMap.isEmpty())
            throw new RuntimeException("Predefined paths are not yet created. Maybe you forgot to run 'initialize' ?");

        return this.morphemeContainerMap.containsKey(root);
    }

    public Set<MorphemeContainer> getPaths(Root root) {
        if (this.morphemeContainerMap.isEmpty())
            throw new RuntimeException("Predefined paths are not yet created. Maybe you forgot to run 'initialize' ?");

        return Collections.unmodifiableSet(this.morphemeContainerMap.get(root));
    }

    void createPaths() {
        this.createPredefinedPathOf_di();
        this.createPredefinedPathOf_yi();

        this.createPredefinedPathOf_su();

        this.createPredefinedPathOf_ben();
        this.createPredefinedPathOf_sen();
        this.createPredefinedPathOf_o_pron_pers();
        this.createPredefinedPathOf_biz();
        this.createPredefinedPathOf_siz();
        this.createPredefinedPathOf_onlar_pron_pers();

        this.createPredefinedPathOf_bu_pron_demons();
        this.createPredefinedPathOf_su_pron_demons();
        this.createPredefinedPathOf_o_pron_demons();
        this.createPredefinedPathOf_bunlar_pron_demons();
        this.createPredefinedPathOf_sunlar_pron_demons();
        this.createPredefinedPathOf_onlar_pron_demons();

        this.createPredefinedPathOf_kendi();
        this.createPredefinedPathOf_hepsi();
        this.createPredefinedPathOf_herkes();

        this.createPredefinedPathOf_question_particles();
        this.createPredefinedPathOf_ne();

        this.createPredefinedPathOf_ora_bura_sura_nere();

        this.createPredefinedPathOf_iceri_disari();

        this.createPredefinedPathOf_bazilari_bazisi();
        this.createPredefinedPathOf_kimileri_kimisi_kimi();
        this.createPredefinedPathOf_birileri_birisi_biri();
        this.createPredefinedPathOf_hicbirisi_hicbiri();
        this.createPredefinedPathOf_birbiri();
        this.createPredefinedPathOf_cogu_bircogu_coklari_bircoklari();
        this.createPredefinedPathOf_birkaci();
        this.createPredefinedPathOf_cumlesi();
        this.createPredefinedPathOf_digeri_digerleri();
    }

    void createPredefinedPathOf_di() {
        // the only documented one. you get the idea for the others ;)

        final Root root_di = this.findRoot("di", PrimaryPos.Verb, null);

        final Suffix Positive = this.suffixGraph.getSuffix("Pos");
        final Suffix Negative = this.suffixGraph.getSuffix("Neg");

        // all possible routes in the SuffixGraph for root "di" of dictionary item "demek"
        // since root "de" for the same item is not irregular, we don't define paths for it
        // --> it is traversed within the regular Turkish suffix graph with regular morhpotactics and phonetics

        //one path is di+Positive(EMPTY_STR)+Future("yecek") -> diyecek.
        // it is irregular since the regular form, demek+Future->"deyecek", is not valid
        this.pathBuilder(root_di).s(Positive).s("Fut", "yecek").add();
        //one path is di+Positive(EMPTY_STR)+Future("yeceğ") -> diyeceğ
        this.pathBuilder(root_di).s(Positive).s("Fut", "yeceğ").add();
        //..
        this.pathBuilder(root_di).s(Positive).s("Future_to_Adj", "yecek").add();
        this.pathBuilder(root_di).s(Positive).s("Future_to_Adj", "yeceğ").add();
        this.pathBuilder(root_di).s(Positive).s("FutPart_Noun", "yecek").add();
        this.pathBuilder(root_di).s(Positive).s("FutPart_Noun", "yeceğ").add();
        this.pathBuilder(root_di).s(Positive).s("FutPart_Adj", "yecek").add();
        this.pathBuilder(root_di).s(Positive).s("FutPart_Adj", "yeceğ").add();

        //one path is di+Positive(EMPTY_STR)+Progressive("yor") -> diyor
        this.pathBuilder(root_di).s(Positive).s("Prog", "yor").add();

        //one path is di+Positive(EMPTY_STR)+PresentParticle("yen") -> diyen
        this.pathBuilder(root_di).s(Positive).s("PresPart", "yen").add();

        //one path is di+Positive(EMPTY_STR)+Ability("yebil") -> diyebil
        this.pathBuilder(root_di).s("Able", "yebil").s(Positive).add();
        //one path is di+Ability(ye)+Negative("me") -> diyeme
        this.pathBuilder(root_di).s("Able", "ye").s(Negative, "me").add();
        //one path is di+Ability(ye)+WithoutHavingDoneSo("meden") -> diyemeden
        this.pathBuilder(root_di).s("Able", "ye").s(Negative, "").s("WithoutHavingDoneSo", "meden").add();

        //..
        this.pathBuilder(root_di).s(Positive).s("Opt", "ye").add();

        //..
        this.pathBuilder(root_di).s(Positive).s("ByDoingSo", "yerek").add();

    }

    void createPredefinedPathOf_yi() {
        final Root root_yi = this.findRoot("yi", PrimaryPos.Verb, null);

        final Suffix Positive = this.suffixGraph.getSuffix("Pos");
        final Suffix Negative = this.suffixGraph.getSuffix("Neg");

        this.pathBuilder(root_yi).s(Positive).s("Fut", "yecek").add();
        this.pathBuilder(root_yi).s(Positive).s("Fut", "yeceğ").add();
        this.pathBuilder(root_yi).s(Positive).s("Future_to_Adj", "yecek").add();
        this.pathBuilder(root_yi).s(Positive).s("Future_to_Adj", "yeceğ").add();
        this.pathBuilder(root_yi).s(Positive).s("FutPart_Noun", "yecek").add();
        this.pathBuilder(root_yi).s(Positive).s("FutPart_Noun", "yeceğ").add();
        this.pathBuilder(root_yi).s(Positive).s("FutPart_Adj", "yecek").add();
        this.pathBuilder(root_yi).s(Positive).s("FutPart_Adj", "yeceğ").add();

        this.pathBuilder(root_yi).s(Positive).s("Prog", "yor").add();

        this.pathBuilder(root_yi).s(Positive).s("PresPart", "yen").add();

        this.pathBuilder(root_yi).s("Able", "yebil").s(Positive).add();
        this.pathBuilder(root_yi).s("Able", "ye").s(Negative, "me").add();
        this.pathBuilder(root_yi).s("Able", "ye").s(Negative, "").s("WithoutHavingDoneSo", "meden").add();

        this.pathBuilder(root_yi).s(Positive).s("Opt", "ye").add();

        this.pathBuilder(root_yi).s(Positive).s("ByDoingSo", "yerek").add();

        // different from "demek"
        this.pathBuilder(root_yi).s(Positive).s("AfterDoingSo", "yip").add();
        this.pathBuilder(root_yi).s(Positive).s("Imp").s("A2Pl_Verb", "yin").add();
    }

    void createPredefinedPathOf_ben() {
        final Root root_ben = this.findRoot("ben", PrimaryPos.Pronoun, SecondaryPos.Personal);
        final Root root_ban = this.findRoot("ban", PrimaryPos.Pronoun, SecondaryPos.Personal);

        final Suffix A1Sg_Pron = this.suffixGraph.getSuffix("A1Sg_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Acc_Pron", "i").add();
        this.pathBuilder(root_ban).s(A1Sg_Pron).s(Pnon_Pron).s("Dat_Pron", "a").add();
        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "de").add();
        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "den").add();
        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "le").add();
        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "imle").add();
        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Gen_Pron", "im").add();
        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("AccordingTo", "ce").add();

        this.pathBuilder(root_ben).s(A1Sg_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_sen() {
        final Root root_sen = this.findRoot("sen", PrimaryPos.Pronoun, SecondaryPos.Personal);
        final Root root_san = this.findRoot("san", PrimaryPos.Pronoun, SecondaryPos.Personal);

        final Suffix A2Sg_Pron = this.suffixGraph.getSuffix("A2Sg_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Acc_Pron", "i").add();
        this.pathBuilder(root_san).s(A2Sg_Pron).s(Pnon_Pron).s("Dat_Pron", "a").add();
        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "de").add();
        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "den").add();
        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "le").add();
        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "inle").add();
        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Gen_Pron", "in").add();
        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("AccordingTo", "ce").add();

        this.pathBuilder(root_sen).s(A2Sg_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_o_pron_pers() {
        final Root root_o = this.findRoot("o", PrimaryPos.Pronoun, SecondaryPos.Personal);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Acc_Pron", "nu").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Dat_Pron", "na").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "nda").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "ndan").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nla").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nunla").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Gen_Pron", "nun").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("AccordingTo", "nca").add();

        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_biz() {
        final Root root_biz = this.findRoot("biz", PrimaryPos.Pronoun, SecondaryPos.Personal);

        final Suffix A1Pl_Pron = this.suffixGraph.getSuffix("A1Pl_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Acc_Pron", "i").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Dat_Pron", "e").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Loc_Pron", "de").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Abl_Pron", "den").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Ins_Pron", "le").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Ins_Pron", "imle").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Gen_Pron", "im").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("AccordingTo", "ce").add();

        this.pathBuilder(root_biz).s(A1Pl_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();

        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Acc_Pron", "i").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Dat_Pron", "e").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Loc_Pron", "de").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Abl_Pron", "den").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Ins_Pron", "le").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Gen_Pron", "in").add();
        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("AccordingTo", "ce").add();

        this.pathBuilder(root_biz).s(A1Pl_Pron, "ler").s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_siz() {
        final Root root_siz = this.findRoot("siz", PrimaryPos.Pronoun, SecondaryPos.Personal);

        final Suffix A2Pl_Pron = this.suffixGraph.getSuffix("A2Pl_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Acc_Pron", "i").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Dat_Pron", "e").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Loc_Pron", "de").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Abl_Pron", "den").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Ins_Pron", "le").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Ins_Pron", "inle").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Gen_Pron", "in").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("AccordingTo", "ce").add();

        this.pathBuilder(root_siz).s(A2Pl_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();

        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Acc_Pron", "i").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Dat_Pron", "e").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Loc_Pron", "de").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Abl_Pron", "den").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Ins_Pron", "le").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Gen_Pron", "in").add();
        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("AccordingTo", "ce").add();

        this.pathBuilder(root_siz).s(A2Pl_Pron, "ler").s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_onlar_pron_pers() {
        final Root root_o = this.findRoot("o", PrimaryPos.Pronoun, SecondaryPos.Personal);

        final Suffix A3Pl_Pron = this.suffixGraph.getSuffix("A3Pl_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Acc_Pron", "ı").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Dat_Pron", "a").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Loc_Pron", "da").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Abl_Pron", "dan").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Ins_Pron", "la").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Gen_Pron", "ın").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("AccordingTo", "ca").add();

        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_bu_pron_demons() {
        final Root root_bu = this.findRoot("bu", PrimaryPos.Pronoun, SecondaryPos.Demonstrative);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Acc_Pron", "nu").add();
        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Dat_Pron", "na").add();
        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "nda").add();
        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "ndan").add();
        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nla").add();
        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nunla").add();
        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Gen_Pron", "nun").add();

        this.pathBuilder(root_bu).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_su_pron_demons() {
        final Root root_su = this.findRoot("şu", PrimaryPos.Pronoun, SecondaryPos.Demonstrative);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Acc_Pron", "nu").add();
        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Dat_Pron", "na").add();
        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "nda").add();
        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "ndan").add();
        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nla").add();
        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nunla").add();
        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Gen_Pron", "nun").add();

        this.pathBuilder(root_su).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_o_pron_demons() {
        final Root root_o = this.findRoot("o", PrimaryPos.Pronoun, SecondaryPos.Demonstrative);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Acc_Pron", "nu").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Dat_Pron", "na").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "nda").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "ndan").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nla").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Ins_Pron", "nunla").add();
        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Gen_Pron", "nun").add();

        this.pathBuilder(root_o).s(A3Sg_Pron).s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_bunlar_pron_demons() {
        final Root root_bu = this.findRoot("bu", PrimaryPos.Pronoun, SecondaryPos.Demonstrative);

        final Suffix A3Pl_Pron = this.suffixGraph.getSuffix("A3Pl_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Acc_Pron", "ı").add();
        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Dat_Pron", "a").add();
        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Loc_Pron", "da").add();
        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Abl_Pron", "dan").add();
        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Ins_Pron", "la").add();
        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Gen_Pron", "ın").add();

        this.pathBuilder(root_bu).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_sunlar_pron_demons() {
        final Root root_su = this.findRoot("şu", PrimaryPos.Pronoun, SecondaryPos.Demonstrative);

        final Suffix A3Pl_Pron = this.suffixGraph.getSuffix("A3Pl_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Acc_Pron", "ı").add();
        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Dat_Pron", "a").add();
        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Loc_Pron", "da").add();
        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Abl_Pron", "dan").add();
        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Ins_Pron", "la").add();
        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Gen_Pron", "ın").add();

        this.pathBuilder(root_su).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_onlar_pron_demons() {
        final Root root_o = this.findRoot("o", PrimaryPos.Pronoun, SecondaryPos.Demonstrative);

        final Suffix A3Pl_Pron = this.suffixGraph.getSuffix("A3Pl_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Acc_Pron", "ı").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Dat_Pron", "a").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Loc_Pron", "da").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Abl_Pron", "dan").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Ins_Pron", "la").add();
        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Gen_Pron", "ın").add();

        this.pathBuilder(root_o).s(A3Pl_Pron, "nlar").s(Pnon_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_kendi() {
        final Root root_kendi = this.findRoot("kendi", PrimaryPos.Pronoun, SecondaryPos.Reflexive);

        final Suffix A1Sg_Pron = this.suffixGraph.getSuffix("A1Sg_Pron");
        final Suffix P1Sg_Pron = this.suffixGraph.getSuffix("P1Sg_Pron");
        final Suffix A2Sg_Pron = this.suffixGraph.getSuffix("A2Sg_Pron");
        final Suffix P2Sg_Pron = this.suffixGraph.getSuffix("P2Sg_Pron");
        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");
        final Suffix P3Sg_Pron = this.suffixGraph.getSuffix("P3Sg_Pron");
        final Suffix A1Pl_Pron = this.suffixGraph.getSuffix("A1Pl_Pron");
        final Suffix P1Pl_Pron = this.suffixGraph.getSuffix("P1Pl_Pron");
        final Suffix A2Pl_Pron = this.suffixGraph.getSuffix("A2Pl_Pron");
        final Suffix P2Pl_Pron = this.suffixGraph.getSuffix("P2Pl_Pron");
        final Suffix A3Pl_Pron = this.suffixGraph.getSuffix("A3Pl_Pron");
        final Suffix P3Pl_Pron = this.suffixGraph.getSuffix("P3Pl_Pron");

        ////////// A1Sg
        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Acc_Pron", "i").add();
        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Dat_Pron", "e").add();
        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Loc_Pron", "de").add();
        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Abl_Pron", "den").add();
        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Ins_Pron", "le").add();
        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Gen_Pron", "in").add();

        this.pathBuilder(root_kendi).s(A1Sg_Pron).s(P1Sg_Pron, "m").s("Nom_Pron_Deriv").add();

        ////////// A2Sg
        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Acc_Pron", "i").add();
        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Dat_Pron", "e").add();
        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Loc_Pron", "de").add();
        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Abl_Pron", "den").add();
        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Ins_Pron", "le").add();
        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Gen_Pron", "in").add();

        this.pathBuilder(root_kendi).s(A2Sg_Pron).s(P2Sg_Pron, "n").s("Nom_Pron_Deriv").add();

        ////////// A3Sg
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Acc_Pron", "ni").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Dat_Pron", "ne").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Loc_Pron", "nde").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Abl_Pron", "nden").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Ins_Pron", "yle").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Gen_Pron", "nin").add();

        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron).s("Nom_Pron_Deriv").add();

        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Acc_Pron", "ni").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Dat_Pron", "ne").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Loc_Pron", "nde").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Abl_Pron", "nden").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Ins_Pron", "yle").add();
        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Gen_Pron", "nin").add();

        this.pathBuilder(root_kendi).s(A3Sg_Pron).s(P3Sg_Pron, "si").s("Nom_Pron_Deriv").add();


        ////////// A1pl
        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Acc_Pron", "i").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Dat_Pron", "e").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Loc_Pron", "de").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Abl_Pron", "den").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Ins_Pron", "le").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Gen_Pron", "in").add();

        this.pathBuilder(root_kendi).s(A1Pl_Pron).s(P1Pl_Pron, "miz").s("Nom_Pron_Deriv").add();

        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Acc_Pron", "i").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Dat_Pron", "e").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Loc_Pron", "de").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Abl_Pron", "den").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Ins_Pron", "le").add();
        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Gen_Pron", "in").add();

        this.pathBuilder(root_kendi).s(A1Pl_Pron, "ler").s(P1Pl_Pron, "imiz").s("Nom_Pron_Deriv").add();

        ////////// A2pl
        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Acc_Pron", "i").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Dat_Pron", "e").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Loc_Pron", "de").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Abl_Pron", "den").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Ins_Pron", "le").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Gen_Pron", "in").add();

        this.pathBuilder(root_kendi).s(A2Pl_Pron).s(P2Pl_Pron, "niz").s("Nom_Pron_Deriv").add();

        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Acc_Pron", "i").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Dat_Pron", "e").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Loc_Pron", "de").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Abl_Pron", "den").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Ins_Pron", "le").add();
        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Gen_Pron", "in").add();

        this.pathBuilder(root_kendi).s(A2Pl_Pron, "ler").s(P2Pl_Pron, "iniz").s("Nom_Pron_Deriv").add();

        ////////// A3pl
        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Acc_Pron", "ni").add();
        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Dat_Pron", "ne").add();
        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Loc_Pron", "nde").add();
        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Abl_Pron", "nden").add();
        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Ins_Pron", "yle").add();
        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Gen_Pron", "nin").add();

        this.pathBuilder(root_kendi).s(A3Pl_Pron, "leri").s(P3Pl_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_hepsi() {
        final Root root_hep = this.findRoot("hep", PrimaryPos.Pronoun, null);
        final Root root_hepsi = this.findRoot("hepsi", PrimaryPos.Pronoun, null);

        final Suffix A1Pl_Pron = this.suffixGraph.getSuffix("A1Pl_Pron");
        final Suffix P1Pl_Pron = this.suffixGraph.getSuffix("P1Pl_Pron");
        final Suffix A2Pl_Pron = this.suffixGraph.getSuffix("A2Pl_Pron");
        final Suffix P2Pl_Pron = this.suffixGraph.getSuffix("P2Pl_Pron");
        final Suffix A3Pl_Pron = this.suffixGraph.getSuffix("A3Pl_Pron");
        final Suffix P3Pl_Pron = this.suffixGraph.getSuffix("P3Pl_Pron");

        ////////// No A1Sg

        ////////// No A2Sg

        ////////// No A3Sg

        ////////// A1pl
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Nom_Pron").add();
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Acc_Pron", "i").add();
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Dat_Pron", "e").add();
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Loc_Pron", "de").add();
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Abl_Pron", "den").add();
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Ins_Pron", "le").add();
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Gen_Pron", "in").add();
        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("AccordingTo", "ce").add();

        this.pathBuilder(root_hep).s(A1Pl_Pron).s(P1Pl_Pron, "imiz").s("Nom_Pron_Deriv").add();

        ////////// A2pl
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Nom_Pron").add();
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Acc_Pron", "i").add();
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Dat_Pron", "e").add();
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Loc_Pron", "de").add();
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Abl_Pron", "den").add();
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Ins_Pron", "le").add();
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Gen_Pron", "in").add();
        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("AccordingTo", "ce").add();

        this.pathBuilder(root_hep).s(A2Pl_Pron).s(P2Pl_Pron, "iniz").s("Nom_Pron_Deriv").add();

        ////////// A3pl

        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Nom_Pron").add();
        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Acc_Pron", "ni").add();
        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Dat_Pron", "ne").add();
        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Loc_Pron", "nde").add();
        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Abl_Pron", "nden").add();
        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Ins_Pron", "yle").add();
        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Gen_Pron", "nin").add();
        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("AccordingTo", "nce").add();

        this.pathBuilder(root_hepsi).s(A3Pl_Pron).s(P3Pl_Pron).s("Nom_Pron_Deriv").add();
    }

    void createPredefinedPathOf_herkes() {
        final Root root_herkes = this.findRoot("herkes", PrimaryPos.Pronoun, null);

        this.pathBuilder(root_herkes).s("A3Sg_Pron").s("Pnon_Pron").add();
    }

    void createPredefinedPathOf_question_particles() {
        final Root root_mii = this.findRoot("mı", PrimaryPos.Question, null);
        final Root root_mi = this.findRoot("mi", PrimaryPos.Question, null);
        final Root root_mu = this.findRoot("mu", PrimaryPos.Question, null);
        final Root root_muu = this.findRoot("mü", PrimaryPos.Question, null);

        final Suffix Pres_Ques = this.suffixGraph.getSuffix("Pres_Ques");
        final Suffix Past_Ques = this.suffixGraph.getSuffix("Past_Ques");
        final Suffix Narr_Ques = this.suffixGraph.getSuffix("Narr_Ques");

        final Suffix A1Sg_Ques = this.suffixGraph.getSuffix("A1Sg_Ques");
        final Suffix A2Sg_Ques = this.suffixGraph.getSuffix("A2Sg_Ques");
        final Suffix A3Sg_Ques = this.suffixGraph.getSuffix("A3Sg_Ques");
        final Suffix A1Pl_Ques = this.suffixGraph.getSuffix("A1Pl_Ques");
        final Suffix A2Pl_Ques = this.suffixGraph.getSuffix("A2Pl_Ques");
        final Suffix A3Pl_Ques = this.suffixGraph.getSuffix("A3Pl_Ques");

        ////////// Pres
        this.pathBuilder(root_mii).s(Pres_Ques).s(A1Sg_Ques, "yım").add();
        this.pathBuilder(root_mii).s(Pres_Ques).s(A2Sg_Ques, "sın").add();
        this.pathBuilder(root_mii).s(Pres_Ques).s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mii).s(Pres_Ques).s(A1Pl_Ques, "yız").add();
        this.pathBuilder(root_mii).s(Pres_Ques).s(A2Pl_Ques, "sınız").add();
        this.pathBuilder(root_mii).s(Pres_Ques).s(A3Pl_Ques, "lar").add();

        this.pathBuilder(root_mi).s(Pres_Ques).s(A1Sg_Ques, "yim").add();
        this.pathBuilder(root_mi).s(Pres_Ques).s(A2Sg_Ques, "sin").add();
        this.pathBuilder(root_mi).s(Pres_Ques).s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mi).s(Pres_Ques).s(A1Pl_Ques, "yiz").add();
        this.pathBuilder(root_mi).s(Pres_Ques).s(A2Pl_Ques, "siniz").add();
        this.pathBuilder(root_mi).s(Pres_Ques).s(A3Pl_Ques, "ler").add();

        this.pathBuilder(root_mu).s(Pres_Ques).s(A1Sg_Ques, "yum").add();
        this.pathBuilder(root_mu).s(Pres_Ques).s(A2Sg_Ques, "sun").add();
        this.pathBuilder(root_mu).s(Pres_Ques).s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mu).s(Pres_Ques).s(A1Pl_Ques, "yuz").add();
        this.pathBuilder(root_mu).s(Pres_Ques).s(A2Pl_Ques, "sunuz").add();
        this.pathBuilder(root_mu).s(Pres_Ques).s(A3Pl_Ques, "lar").add();

        this.pathBuilder(root_muu).s(Pres_Ques).s(A1Sg_Ques, "yüm").add();
        this.pathBuilder(root_muu).s(Pres_Ques).s(A2Sg_Ques, "sün").add();
        this.pathBuilder(root_muu).s(Pres_Ques).s(A3Sg_Ques, "").add();
        this.pathBuilder(root_muu).s(Pres_Ques).s(A1Pl_Ques, "yüz").add();
        this.pathBuilder(root_muu).s(Pres_Ques).s(A2Pl_Ques, "sünüz").add();
        this.pathBuilder(root_muu).s(Pres_Ques).s(A3Pl_Ques, "ler").add();

        ////////// Past
        this.pathBuilder(root_mii).s(Past_Ques, "ydı").s(A1Sg_Ques, "m").add();
        this.pathBuilder(root_mii).s(Past_Ques, "ydı").s(A2Sg_Ques, "n").add();
        this.pathBuilder(root_mii).s(Past_Ques, "ydı").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mii).s(Past_Ques, "ydı").s(A1Pl_Ques, "k").add();
        this.pathBuilder(root_mii).s(Past_Ques, "ydı").s(A2Pl_Ques, "nız").add();
        this.pathBuilder(root_mii).s(Past_Ques, "ydı").s(A3Pl_Ques, "lar").add();

        this.pathBuilder(root_mi).s(Past_Ques, "ydi").s(A1Sg_Ques, "m").add();
        this.pathBuilder(root_mi).s(Past_Ques, "ydi").s(A2Sg_Ques, "n").add();
        this.pathBuilder(root_mi).s(Past_Ques, "ydi").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mi).s(Past_Ques, "ydi").s(A1Pl_Ques, "k").add();
        this.pathBuilder(root_mi).s(Past_Ques, "ydi").s(A2Pl_Ques, "niz").add();
        this.pathBuilder(root_mi).s(Past_Ques, "ydi").s(A3Pl_Ques, "ler").add();

        this.pathBuilder(root_mu).s(Past_Ques, "ydu").s(A1Sg_Ques, "m").add();
        this.pathBuilder(root_mu).s(Past_Ques, "ydu").s(A2Sg_Ques, "n").add();
        this.pathBuilder(root_mu).s(Past_Ques, "ydu").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mu).s(Past_Ques, "ydu").s(A1Pl_Ques, "k").add();
        this.pathBuilder(root_mu).s(Past_Ques, "ydu").s(A2Pl_Ques, "nuz").add();
        this.pathBuilder(root_mu).s(Past_Ques, "ydu").s(A3Pl_Ques, "lar").add();

        this.pathBuilder(root_muu).s(Past_Ques, "ydü").s(A1Sg_Ques, "m").add();
        this.pathBuilder(root_muu).s(Past_Ques, "ydü").s(A2Sg_Ques, "n").add();
        this.pathBuilder(root_muu).s(Past_Ques, "ydü").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_muu).s(Past_Ques, "ydü").s(A1Pl_Ques, "k").add();
        this.pathBuilder(root_muu).s(Past_Ques, "ydü").s(A2Pl_Ques, "nüz").add();
        this.pathBuilder(root_muu).s(Past_Ques, "ydü").s(A3Pl_Ques, "ler").add();

        ////////// Narr
        this.pathBuilder(root_mii).s(Narr_Ques, "ymış").s(A1Sg_Ques, "ım").add();
        this.pathBuilder(root_mii).s(Narr_Ques, "ymış").s(A2Sg_Ques, "sın").add();
        this.pathBuilder(root_mii).s(Narr_Ques, "ymış").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mii).s(Narr_Ques, "ymış").s(A1Pl_Ques, "ız").add();
        this.pathBuilder(root_mii).s(Narr_Ques, "ymış").s(A2Pl_Ques, "sınız").add();
        this.pathBuilder(root_mii).s(Narr_Ques, "ymış").s(A3Pl_Ques, "lar").add();

        this.pathBuilder(root_mi).s(Narr_Ques, "ymiş").s(A1Sg_Ques, "im").add();
        this.pathBuilder(root_mi).s(Narr_Ques, "ymiş").s(A2Sg_Ques, "sin").add();
        this.pathBuilder(root_mi).s(Narr_Ques, "ymiş").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mi).s(Narr_Ques, "ymiş").s(A1Pl_Ques, "iz").add();
        this.pathBuilder(root_mi).s(Narr_Ques, "ymiş").s(A2Pl_Ques, "siniz").add();
        this.pathBuilder(root_mi).s(Narr_Ques, "ymiş").s(A3Pl_Ques, "ler").add();

        this.pathBuilder(root_mu).s(Narr_Ques, "ymuş").s(A1Sg_Ques, "um").add();
        this.pathBuilder(root_mu).s(Narr_Ques, "ymuş").s(A2Sg_Ques, "sun").add();
        this.pathBuilder(root_mu).s(Narr_Ques, "ymuş").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_mu).s(Narr_Ques, "ymuş").s(A1Pl_Ques, "uz").add();
        this.pathBuilder(root_mu).s(Narr_Ques, "ymuş").s(A2Pl_Ques, "sunuz").add();
        this.pathBuilder(root_mu).s(Narr_Ques, "ymuş").s(A3Pl_Ques, "lar").add();

        this.pathBuilder(root_muu).s(Narr_Ques, "ymüş").s(A1Sg_Ques, "üm").add();
        this.pathBuilder(root_muu).s(Narr_Ques, "ymüş").s(A2Sg_Ques, "sün").add();
        this.pathBuilder(root_muu).s(Narr_Ques, "ymüş").s(A3Sg_Ques, "").add();
        this.pathBuilder(root_muu).s(Narr_Ques, "ymüş").s(A1Pl_Ques, "üz").add();
        this.pathBuilder(root_muu).s(Narr_Ques, "ymüş").s(A2Pl_Ques, "sünüz").add();
        this.pathBuilder(root_muu).s(Narr_Ques, "ymüş").s(A3Pl_Ques, "ler").add();
    }

    void createPredefinedPathOf_ne() {
        final Root root_ne = this.findRoot("ne", PrimaryPos.Pronoun, SecondaryPos.Question);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P1Sg_Pron", "m").add();
        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P1Sg_Pron", "yim").add();

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P2Sg_Pron", "n").add();
        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P2Sg_Pron", "yin").add();

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P3Sg_Pron", "yi").add();
        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P3Sg_Pron", "si").add();

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P1Pl_Pron", "yimiz").add();

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P2Pl_Pron", "yiniz").add();

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("P3Pl_Pron", "leri").add();

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("Pnon_Pron").s("Gen_Pron", "yin").add();

        this.pathBuilder(root_ne).s(A3Sg_Pron).s("Pnon_Pron").add();

        this.pathBuilder(root_ne).s("A3Pl_Pron", "ler").s("Pnon_Pron").add();
    }

    void createPredefinedPathOf_su() {
        final Root root_su = this.findRoot("su", PrimaryPos.Noun, null);

        final Suffix A3Sg_Noun = this.suffixGraph.getSuffix("A3Sg_Noun");

        this.pathBuilder(root_su).s(A3Sg_Noun).s("P1Sg_Noun", "yum").add();
        this.pathBuilder(root_su).s(A3Sg_Noun).s("P2Sg_Noun", "yun").add();
        this.pathBuilder(root_su).s(A3Sg_Noun).s("P3Sg_Noun", "yu").add();
        this.pathBuilder(root_su).s(A3Sg_Noun).s("P1Pl_Noun", "yumuz").add();
        this.pathBuilder(root_su).s(A3Sg_Noun).s("P2Pl_Noun", "yunuz").add();
        this.pathBuilder(root_su).s(A3Sg_Noun).s("P3Pl_Noun", "ları").add();
        this.pathBuilder(root_su).s(A3Sg_Noun).s("Pnon_Noun").s("Gen_Noun", "yun").add();
        this.pathBuilder(root_su).s(A3Sg_Noun).s("Pnon_Noun").add();

        this.pathBuilder(root_su).s("A3Pl_Noun", "lar").s("Pnon_Noun").add();
    }

    void createPredefinedPathOf_ora_bura_sura_nere() {
        final Root root_or = this.findRoot("or", PrimaryPos.Pronoun, null);
        final Root root_bur = this.findRoot("bur", PrimaryPos.Pronoun, null);
        final Root root_sur = this.findRoot("şur", PrimaryPos.Pronoun, null);
        final Root root_ner = this.findRoot("ner", PrimaryPos.Pronoun, SecondaryPos.Question);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");
        final Suffix Pnon_Pron = this.suffixGraph.getSuffix("Pnon_Pron");

        // define predefined paths for "orda" and "ordan" etc.

        this.pathBuilder(root_or).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "da").add();
        this.pathBuilder(root_or).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "dan").add();

        this.pathBuilder(root_bur).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "da").add();
        this.pathBuilder(root_bur).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "dan").add();

        this.pathBuilder(root_sur).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "da").add();
        this.pathBuilder(root_sur).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "dan").add();

        this.pathBuilder(root_ner).s(A3Sg_Pron).s(Pnon_Pron).s("Loc_Pron", "de").add();
        this.pathBuilder(root_ner).s(A3Sg_Pron).s(Pnon_Pron).s("Abl_Pron", "den").add();
    }

    void createPredefinedPathOf_iceri_disari() {
        final Root root_icer = this.findRoot("içer", PrimaryPos.Noun, null);
        final Root root_disar = this.findRoot("dışar", PrimaryPos.Noun, null);

        final Suffix A3Sg_Noun = this.suffixGraph.getSuffix("A3Sg_Noun");
        final Suffix Pnon_Noun = this.suffixGraph.getSuffix("Pnon_Noun");
        final Suffix P3Sg_Noun = this.suffixGraph.getSuffix("P3Sg_Noun");

        // define predefined paths for "içerde" and "dışardan" etc.

        this.pathBuilder(root_icer).s(A3Sg_Noun).s(Pnon_Noun).s("Loc_Noun", "de").add();
        this.pathBuilder(root_icer).s(A3Sg_Noun).s(Pnon_Noun).s("Abl_Noun", "den").add();
        this.pathBuilder(root_icer).s(A3Sg_Noun).s(P3Sg_Noun, "si").add();

        this.pathBuilder(root_disar).s(A3Sg_Noun).s(Pnon_Noun).s("Loc_Noun", "da").add();
        this.pathBuilder(root_disar).s(A3Sg_Noun).s(Pnon_Noun).s("Abl_Noun", "dan").add();
        this.pathBuilder(root_disar).s(A3Sg_Noun).s(P3Sg_Noun, "sı").add();
    }

    void createPredefinedPathOf_bazilari_bazisi() {
        final Root root_bazisi = this.findRoot("bazısı", PrimaryPos.Pronoun, null);
        final Root root_bazilari = this.findRoot("bazıları", PrimaryPos.Pronoun, null);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_bazilari).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_bazilari).s(A3Sg_Pron).s("P1Pl_Pron", "mız").add();
        this.pathBuilder(root_bazilari).s(A3Sg_Pron).s("P2Pl_Pron", "nız").add();

        this.pathBuilder(root_bazisi).s(A3Sg_Pron).s("P3Sg_Pron").add();
    }

    void createPredefinedPathOf_kimileri_kimisi_kimi() {
        final Root root_kimi = this.findRoot("kimi", PrimaryPos.Pronoun, null);
        final Root root_kimisi = this.findRoot("kimisi", PrimaryPos.Pronoun, null);
        final Root root_kimileri = this.findRoot("kimileri", PrimaryPos.Pronoun, null);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_kimileri).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_kimileri).s(A3Sg_Pron).s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_kimileri).s(A3Sg_Pron).s("P2Pl_Pron", "niz").add();

        this.pathBuilder(root_kimi).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_kimi).s(A3Sg_Pron).s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_kimi).s(A3Sg_Pron).s("P2Pl_Pron", "niz").add();

        this.pathBuilder(root_kimisi).s(A3Sg_Pron).s("P3Sg_Pron").add();
    }

    void createPredefinedPathOf_birileri_birisi_biri() {
        final Root root_biri = this.findRoot("biri", PrimaryPos.Pronoun, null);
        final Root root_birisi = this.findRoot("birisi", PrimaryPos.Pronoun, null);
        final Root root_birileri = this.findRoot("birileri", PrimaryPos.Pronoun, null);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_birileri).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_birileri).s(A3Sg_Pron).s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_birileri).s(A3Sg_Pron).s("P2Pl_Pron", "niz").add();

        this.pathBuilder(root_biri).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_biri).s(A3Sg_Pron).s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_biri).s(A3Sg_Pron).s("P2Pl_Pron", "niz").add();

        this.pathBuilder(root_birisi).s(A3Sg_Pron).s("P3Sg_Pron").add();
    }

    void createPredefinedPathOf_hicbirisi_hicbiri() {
        final Root root_hicbiri = this.findRoot("hiçbiri", PrimaryPos.Pronoun, null);
        final Root root_hicbirisi = this.findRoot("hiçbirisi", PrimaryPos.Pronoun, null);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_hicbiri).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_hicbiri).s(A3Sg_Pron).s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_hicbiri).s(A3Sg_Pron).s("P2Pl_Pron", "niz").add();

        this.pathBuilder(root_hicbirisi).s(A3Sg_Pron).s("P3Sg_Pron").add();
    }

    void createPredefinedPathOf_birbiri() {
        final Root root_birbir = this.findRoot("birbir", PrimaryPos.Pronoun, null);
        final Root root_birbiri = this.findRoot("birbiri", PrimaryPos.Pronoun, null);

        this.pathBuilder(root_birbiri).s("A3Sg_Pron").s("P3Sg_Pron").add();
        this.pathBuilder(root_birbiri).s("A1Pl_Pron").s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_birbiri).s("A2Pl_Pron").s("P2Pl_Pron", "niz").add();

        this.pathBuilder(root_birbir).s("A3Pl_Pron").s("P3Pl_Pron", "leri").add();
    }

    void createPredefinedPathOf_cogu_bircogu_coklari_bircoklari() {
        final Root root_cogu = this.findRoot("çoğu", PrimaryPos.Pronoun, null);
        final Root root_bircogu = this.findRoot("birçoğu", PrimaryPos.Pronoun, null);
        final Root root_coklari = this.findRoot("çokları", PrimaryPos.Pronoun, null);
        final Root root_bircoklari = this.findRoot("birçokları", PrimaryPos.Pronoun, null);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_cogu).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_cogu).s(A3Sg_Pron).s("P1Pl_Pron", "muz").add();
        this.pathBuilder(root_cogu).s(A3Sg_Pron).s("P2Pl_Pron", "nuz").add();

        this.pathBuilder(root_bircogu).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_bircogu).s(A3Sg_Pron).s("P1Pl_Pron", "muz").add();
        this.pathBuilder(root_bircogu).s(A3Sg_Pron).s("P2Pl_Pron", "nuz").add();

        this.pathBuilder(root_coklari).s(A3Sg_Pron).s("P3Pl_Pron").add();

        this.pathBuilder(root_bircoklari).s(A3Sg_Pron).s("P3Pl_Pron").add();
    }

    void createPredefinedPathOf_birkaci() {
        final Root root_birkaci = this.findRoot("birkaçı", PrimaryPos.Pronoun, null);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_birkaci).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_birkaci).s(A3Sg_Pron).s("P1Pl_Pron", "mız").add();
        this.pathBuilder(root_birkaci).s(A3Sg_Pron).s("P2Pl_Pron", "nız").add();
    }

    void createPredefinedPathOf_cumlesi() {
        final Root root_cumlesi = this.findRoot("cümlesi", PrimaryPos.Pronoun, null);

        this.pathBuilder(root_cumlesi).s("A3Sg_Pron").s("P3Sg_Pron").add();
    }

    void createPredefinedPathOf_digeri_digerleri() {
        final Root root_digeri = this.findRoot("diğeri", PrimaryPos.Pronoun, null);
        final Root root_digerleri = this.findRoot("diğerleri", PrimaryPos.Pronoun, null);

        final Suffix A3Sg_Pron = this.suffixGraph.getSuffix("A3Sg_Pron");

        this.pathBuilder(root_digeri).s(A3Sg_Pron).s("P3Sg_Pron").add();
        this.pathBuilder(root_digeri).s(A3Sg_Pron).s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_digeri).s(A3Sg_Pron).s("P2Pl_Pron", "niz").add();

        this.pathBuilder(root_digerleri).s(A3Sg_Pron).s("P3Pl_Pron").add();
        this.pathBuilder(root_digerleri).s(A3Sg_Pron).s("P1Pl_Pron", "miz").add();
        this.pathBuilder(root_digerleri).s(A3Sg_Pron).s("P2Pl_Pron", "niz").add();
    }

    private PredefinedPathBuilder pathBuilder(Root root) {
        return new PredefinedPathBuilder(suffixGraph, suffixApplier, morphemeContainerMap).root(root);
    }

    private Root findRoot(final String strRoot, final PrimaryPos primaryPos, final SecondaryPos secondaryPos) {
        if (!this.rootMap.containsKey(strRoot))
            throw new RuntimeException("Unable to find root " + strRoot);

        final Collection<? extends Root> roots = this.rootMap.get(strRoot);
        final Collection<? extends Root> filteredRoots = Collections2.filter(roots, new Predicate<Root>() {
            @Override
            public boolean apply(final Root root) {
                return Objects.equal(root.getLexeme().getPrimaryPos(), primaryPos) &&
                        Objects.equal(root.getLexeme().getSecondaryPos(), secondaryPos);
            }
        });

        Validate.isTrue(filteredRoots.size() == 1, "Found more than once roots for given parameters :" + filteredRoots.toString());
        return filteredRoots.iterator().next();
    }

}