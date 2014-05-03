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

package org.trnltk.model.lexicon;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.trnltk.common.structure.StringEnum;
import org.trnltk.common.structure.StringEnumMap;

/**
 * Attributes that are modifiers to lexemes. These attributes are defined with the {@link Lexeme}s on creation.
 * <p/>
 * A <code>LexemeAttribute</code> defines how a {@link Lexeme} behaves.
 * <p/>
 * For example, a {@link Lexeme} with attribute {@link LexemeAttribute#NoVoicing}, such as 'kek'
 * won't accept 'keğ' or 'keg'.
 * <p/>
 * Any number of {@code LexemeAttribute}s can be used for a {@link Lexeme}. For example, the lexeme 'serhat+Noun' has
 * {@link LexemeAttribute#Voicing}, {@link LexemeAttribute#Doubling}, {@link LexemeAttribute#InverseHarmony} because of the cases
 * <i>serhat</i>, <i>serhaddim</i>, ...
 */
public enum LexemeAttribute implements StringEnum<LexemeAttribute> {
    // verb related

    /**
     * Marks a verb as using Aorist suffix I. For example, <i>gelir</i>, <i>vurur</i> but not <i>geler</i>, <i>vurar</i>.
     * There are a couple of rules about Aorist_A and Aorist_I in {@link org.trnltk.morphology.lexicon.LexemeCreator}
     * <p/>
     * All verbs have either Aorist_I or Aorist_A attribute. One must exist.
     */
    Aorist_I(Predicates.APPLICABLE_FOR_VERBS),
    /**
     * Marks a verb as using Aorist suffix A. For example, <i>gider</i>, <i>yazar</i> but not <i>gidir</i>, <i>yazir</i>.
     * There are a couple of rules about Aorist_A and Aorist_I in {@link org.trnltk.morphology.lexicon.LexemeCreator}
     * <p/>
     * All verbs have either Aorist_I or Aorist_A attribute. One must exist.
     */
    Aorist_A(Predicates.APPLICABLE_FOR_VERBS),

    /**
     * Marks a verb as a candidate for progressive vowel drop.
     * <ul>
     * <li>No {@code ProgressiveVowelDrop} --> geliyor(gelmek), atıyor(atmak)</li>
     * <lı>With {@code ProgressiveVowelDrop} --> tarıyor(taramak), atıyor(atamak)</lı>
     * </ul>
     */
    ProgressiveVowelDrop(Predicates.APPLICABLE_FOR_VERBS),

    /**
     * Marks a verb lexeme as using passive suffix Il. For example <i>yazılmak</i>, <i>kapılmak</i>
     */
    Passive_Il(Predicates.APPLICABLE_FOR_VERBS),
    /**
     * Marks a verb lexeme as using passive suffix In. For example <i>gelinmek</i>, <i>kalınmak</i>
     */
    Passive_In(Predicates.APPLICABLE_FOR_VERBS),
    /**
     * Marks a verb lexeme as using passive suffix InIl. For example <i>denilmek</i>, <i>yenilmek</i>
     */
    Passive_InIl(Predicates.APPLICABLE_FOR_VERBS),


    /**
     * Marks a verb lexeme as using causative suffix t. For example <i>kapatmak</i>, <i>duyumsatmak</i>
     */
    Causative_t(Predicates.APPLICABLE_FOR_VERBS),
    /**
     * Marks a verb lexeme as using causative suffix Ir. For example <i>uçurmak</i>, <i>doğurmak</i>
     */
    Causative_Ir(Predicates.APPLICABLE_FOR_VERBS),
    /**
     * Marks a verb lexeme as using causative suffix It. For example <i>korkutmak</i>, <i>tozutmak</i>
     */
    Causative_It(Predicates.APPLICABLE_FOR_VERBS),
    /**
     * Marks a verb lexeme as using causative suffix Ar. For example <i>çıkarmak</i>, <i>koparmak</i>
     */
    Causative_Ar(Predicates.APPLICABLE_FOR_VERBS),
    /**
     * Marks a verb lexeme as using causative suffix dIr. For example <i>değdirmek</i>, <i>ettirmek</i>
     */
    Causative_dIr(Predicates.APPLICABLE_FOR_VERBS),

    // phonetic
    /**
     * Marks a lexeme as no voicing applicable. For example <i>keki</i>, <i>yapar</i>
     */
    NoVoicing(Predicates.APPLICABLE_FOR_ALL_LEXEMES),
    /**
     * Marks a lexeme as voicing required. For example <i>kitabı</i>, <i>durağı</i>
     */
    Voicing(Predicates.APPLICABLE_FOR_ALL_LEXEMES),
    /**
     * Marks a lexeme as no voicing applicable but not required. For example <i>yoka</i>, <i>yoğa</i>
     */
    VoicingOpt(Predicates.APPLICABLE_FOR_ALL_LEXEMES),
    /**
     * Marks a lexeme as having inverse harmony. For example <i>alkole</i>, <i>beraati</i>
     */
    InverseHarmony(Predicates.APPLICABLE_FOR_ALL_LEXEMES),
    /**
     * Marks a lexeme as doubling required. For example <i>halline</i>, <i>hacca</i>
     */
    Doubling(Predicates.APPLICABLE_FOR_ALL_LEXEMES),
    /**
     * Marks a lexeme as ending with Arabic letter 'ayn'. For example <i>camii</i>, <i>sanayii</i>
     */
    EndsWithAyn(Predicates.APPLICABLE_FOR_ALL_LEXEMES),

    // noun related
    /**
     * Marks a noun (or an adjective converted into a noun) as a compound noun with P3Sg suffix already included. For example <i>zeytinyağı</i>, <i>havaalanı</i>
     */
    CompoundP3sg(Predicates.APPLICABLE_FOR_NOUNS_OR_ADJECTIVES),
    /**
     * Marks a lexeme as having the last vowel drop required in case of a suffix which starts with a vovel. For example <i>omzu</i>, <i>alnı</i>
     */
    LastVowelDrop(Predicates.APPLICABLE_FOR_ALL_LEXEMES),

    // other
    /**
     * Marks a lexeme as a special lexeme, who has a lot of exceptions in terms of phonetics and/or orthographics. Special roots are created for
     * them and these roots are handled specially. Examples are : <i>demek</i>, <i>yemek</i>, <i>ben</i>
     */
    Special(Predicates.APPLICABLE_FOR_ALL_LEXEMES),
    /**
     * Lexemes having this attribute cannot have any suffixes. For example : <i>dank</i>, <i>dekore</i>
     */
    NoSuffix(Predicates.APPLICABLE_FOR_ALL_LEXEMES),
    /**
     * Lexemes having this attribute are plural nouns borrowed from other languages, which don't have the Turkish plural suffix <i>-ler/lar</i>
     */
    Plural(Predicates.APPLICABLE_FOR_NOUNS);


    public static final ImmutableSet<LexemeAttribute> CAUSATIVES = Sets.immutableEnumSet(Causative_t, Causative_dIr, Causative_Ar, Causative_Ir, Causative_It);

    private final static StringEnumMap<LexemeAttribute> shortFormToPosMap = StringEnumMap.get(LexemeAttribute.class);
    private final Predicate<Lexeme> lexemePredicate;

    private LexemeAttribute(Predicate<Lexeme> lexemePredicate) {
        this.lexemePredicate = lexemePredicate;
    }

    /**
     * Checks if a {@code LexemeAttribute} is applicable for a {@link Lexeme}.
     * <p/>
     * For example, {@link LexemeAttribute#Causative_Ar} is only applicable to Verbs.
     * <p/>
     * See docs of possible {@code LexemeAttribute} values for more explanation.
     *
     * @param lexeme Lexeme to check if attr is applicable
     * @return true/false
     */
    public boolean isApplicable(Lexeme lexeme) {
        return this.lexemePredicate.apply(lexeme);
    }

    @Override
    public String getStringForm() {
        return this.name();
    }

    public static StringEnumMap<LexemeAttribute> converter() {
        return shortFormToPosMap;
    }

    private static class Predicates {
        private static final Predicate<Lexeme> APPLICABLE_FOR_VERBS = new Predicate<Lexeme>() {
            @Override
            public boolean apply(org.trnltk.model.lexicon.Lexeme lexeme) {
                return PrimaryPos.Verb.equals(lexeme.getPrimaryPos());
            }
        };

        private static final Predicate<Lexeme> APPLICABLE_FOR_NOUNS = new Predicate<Lexeme>() {
            @Override
            public boolean apply(org.trnltk.model.lexicon.Lexeme lexeme) {
                return PrimaryPos.Noun.equals(lexeme.getPrimaryPos());
            }
        };

        private static final Predicate<Lexeme> APPLICABLE_FOR_NOUNS_OR_ADJECTIVES = new Predicate<Lexeme>() {
            @Override
            public boolean apply(org.trnltk.model.lexicon.Lexeme lexeme) {
                return PrimaryPos.Noun.equals(lexeme.getPrimaryPos()) || PrimaryPos.Adjective.equals(lexeme.getPrimaryPos());
            }
        };

        private static final Predicate<Lexeme> APPLICABLE_FOR_ALL_LEXEMES = com.google.common.base.Predicates.alwaysTrue();
    }

}
