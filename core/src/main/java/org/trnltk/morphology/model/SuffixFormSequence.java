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

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.phonetics.PhoneticAttribute;
import org.trnltk.morphology.phonetics.TurkishAlphabet;
import org.trnltk.morphology.phonetics.TurkishChar;
import org.trnltk.morphology.phonetics.TurkishLetter;

import java.util.Set;

public class SuffixFormSequence {
    private final String suffixFormStr;
    private final ImmutableList<SuffixFormSequenceRule> rules;
    private static final char EXCLAMATION = '!';
    private static final Character PLUS = '+';
    private final boolean firstLetterVowel;

    public SuffixFormSequence(String suffixFormStr) {
        Validate.notNull(suffixFormStr);
        this.suffixFormStr = suffixFormStr;

        // optional letter can only exist as a first char
        Validate.isTrue(suffixFormStr.lastIndexOf(PLUS) == -1 || suffixFormStr.lastIndexOf(PLUS) == 0);

        final ImmutableList.Builder<SuffixFormSequenceRule> rulesBuilder = ImmutableList.builder();

        for (int i = 0; i < suffixFormStr.length(); i++) {
            char currentChar = suffixFormStr.charAt(i);

            if (currentChar == EXCLAMATION || currentChar == PLUS)
                continue;

            final Character previousChar = i > 0 ? suffixFormStr.charAt(i - 1) : null;
            final boolean previousCharIsPlus = previousChar != null && previousChar == PLUS;
            final boolean previousCharIsExclamation = previousChar != null && previousChar == EXCLAMATION;

            final boolean currentLetterIsUpperCase = TurkishAlphabet.isUpperCase(currentChar);
            final TurkishChar currentTurkishChar = TurkishAlphabet.getChar(currentChar);
            final TurkishLetter currentLetter = currentTurkishChar.getLetter();

            if (i == 0 && TurkishAlphabet.Devoicable_Letters.contains(currentLetter)) {
                rulesBuilder.add(new SuffixFormSequenceRule(currentTurkishChar, SuffixFormSequenceRuleType.INSERT_DEVOICABLE_LETTER));
                continue;
            }

            if (currentLetterIsUpperCase)
                Validate.isTrue(currentChar == 'A' || currentChar == 'I');

            if (previousCharIsExclamation)
                Validate.isTrue(currentChar == 'I');

            if (currentChar == 'A') {
                if (previousCharIsPlus)
                    rulesBuilder.add(new SuffixFormSequenceRule(SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_A_WITH_HARMONY));
                else
                    rulesBuilder.add(new SuffixFormSequenceRule(SuffixFormSequenceRuleType.INSERT_VOWEL_A_WITH_HARMONY));
            } else if (currentChar == 'I') {
                if (previousCharIsExclamation) {
                    // exclamation and plus cannot happen at the same time!
                    rulesBuilder.add(new SuffixFormSequenceRule(SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY_AND_NO_ROUNDING));
                } else {
                    if (previousCharIsPlus)
                        rulesBuilder.add(new SuffixFormSequenceRule(SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL_I_WITH_HARMONY));
                    else
                        rulesBuilder.add(new SuffixFormSequenceRule(SuffixFormSequenceRuleType.INSERT_VOWEL_I_WITH_HARMONY));
                }
            } else {
                if (previousCharIsPlus) {
                    if (currentLetter.isVowel())
                        rulesBuilder.add(new SuffixFormSequenceRule(currentTurkishChar, SuffixFormSequenceRuleType.INSERT_OPTIONAL_VOWEL));
                    else
                        rulesBuilder.add(new SuffixFormSequenceRule(currentTurkishChar, SuffixFormSequenceRuleType.INSERT_OPTIONAL_CONSONANT));
                } else {
                    if (currentLetter.isVowel())
                        rulesBuilder.add(new SuffixFormSequenceRule(currentTurkishChar, SuffixFormSequenceRuleType.INSERT_VOWEL_WITHOUT_HARMONY));
                    else
                        rulesBuilder.add(new SuffixFormSequenceRule(currentTurkishChar, SuffixFormSequenceRuleType.INSERT_NONVOWEL_LETTER));
                }
            }


        }

        this.firstLetterVowel = this.findIsFirstLetterVowel();

        this.rules = rulesBuilder.build();
    }

    public ImmutableList<SuffixFormSequenceRule> getRules() {
        return rules;
    }

    public String getSuffixFormStr() {
        return suffixFormStr;
    }

    public boolean isNotBlank() {
        return StringUtils.isNotBlank(this.suffixFormStr);
    }

    public boolean isFirstLetterVowel() {
        return this.firstLetterVowel;
    }

    private boolean findIsFirstLetterVowel() {
        if (!this.isNotBlank())
            return false;

        if (TurkishAlphabet.getLetterForChar(this.suffixFormStr.charAt(0)).isVowel())
            return true;

        if (this.suffixFormStr.charAt(0) == PLUS) {
            if (this.suffixFormStr.length() >= 3)
                return TurkishAlphabet.getLetterForChar(this.suffixFormStr.charAt(1)).isVowel()
                        || TurkishAlphabet.getLetterForChar(this.suffixFormStr.charAt(2)).isVowel();
            if (this.suffixFormStr.length() >= 2)
                return TurkishAlphabet.getLetterForChar(this.suffixFormStr.charAt(1)).isVowel();
        }


        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuffixFormSequence that = (SuffixFormSequence) o;

        if (!suffixFormStr.equals(that.suffixFormStr)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return suffixFormStr.hashCode();
    }

    @Override
    public String toString() {
        return "SuffixFormSequence{" +
                "suffixFormStr='" + suffixFormStr + '\'' +
                '}';
    }

    public static class SuffixFormSequenceRule {

        private final SuffixFormSequenceRuleType ruleType;
        private final TurkishChar charToAdd;

        SuffixFormSequenceRule(final TurkishChar charToAdd, final SuffixFormSequenceRuleType ruleType) {
            this.charToAdd = charToAdd;
            this.ruleType = ruleType;
        }

        SuffixFormSequenceRule(SuffixFormSequenceRuleType ruleType) {
            this(null, ruleType);
        }

        public SuffixFormSequenceRuleType getRuleType() {
            return ruleType;
        }

        public TurkishChar getCharToAdd() {
            return charToAdd;
        }

        public Character apply(Set<PhoneticAttribute> phoneticAttributesOfSurface) {
            return this.ruleType.apply(this.charToAdd, phoneticAttributesOfSurface);
        }
    }

    public static enum SuffixFormSequenceRuleType {
        INSERT_NONVOWEL_LETTER {
            @Override
            public Character apply(TurkishChar charToAdd, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                return charToAdd.getCharValue();
            }
        },

        INSERT_VOWEL_WITHOUT_HARMONY {
            @Override
            public Character apply(TurkishChar charToAdd, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                return charToAdd.getCharValue();
            }
        },

        INSERT_VOWEL_A_WITH_HARMONY {
            @Override
            public Character apply(TurkishChar _notUsed, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastVowelBack = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelBack) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelFrontal);

                if (lastVowelBack)
                    return 'a';
                else
                    return 'e';
            }
        },

        INSERT_VOWEL_I_WITH_HARMONY {
            @Override
            public Character apply(TurkishChar _notUsed, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastVowelBack = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelBack) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelFrontal);
                final boolean lastLetterUnrounded = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelUnrounded) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelRounded);

                if (lastVowelBack) {
                    if (lastLetterUnrounded)
                        return 'ı';
                    else
                        return 'u';
                } else {
                    if (lastLetterUnrounded)
                        return 'i';
                    else
                        return 'ü';
                }
            }
        },

        INSERT_VOWEL_I_WITH_HARMONY_AND_NO_ROUNDING {
            @Override
            public Character apply(TurkishChar _notUsed, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastVowelBack = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelBack) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastVowelFrontal);

                if (lastVowelBack)
                    return 'ı';
                else
                    return 'i';
            }
        },

        INSERT_OPTIONAL_VOWEL {
            @Override
            public Character apply(TurkishChar charToAdd, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastLetterVowel = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterVowel) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterConsonant);

                if (lastLetterVowel)
                    return null;
                else
                    return charToAdd.getCharValue();
            }
        },        //TODO: better naming for kaynastirma?

        INSERT_OPTIONAL_CONSONANT {
            @Override
            public Character apply(TurkishChar charToAdd, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastLetterConsonant = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterConsonant) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterVowel);

                if (lastLetterConsonant)
                    return null;
                else
                    return charToAdd.getCharValue();
            }
        },    //TODO: better naming for kaynastirma?

        INSERT_OPTIONAL_VOWEL_A_WITH_HARMONY {
            @Override
            public Character apply(TurkishChar _notUsed, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastLetterVowel = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterVowel) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterConsonant);

                if (lastLetterVowel)
                    return null;
                else
                    return INSERT_VOWEL_A_WITH_HARMONY.apply(null, phoneticAttributesOfSurface);
            }
        },

        INSERT_OPTIONAL_VOWEL_I_WITH_HARMONY {
            @Override
            public Character apply(TurkishChar _notUsed, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastLetterVowel = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterVowel) ||
                        !phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterConsonant);
                if (lastLetterVowel)
                    return null;
                else
                    return INSERT_VOWEL_I_WITH_HARMONY.apply(null, phoneticAttributesOfSurface);
            }
        },

        INSERT_DEVOICABLE_LETTER {
            @Override
            public Character apply(TurkishChar charToAdd, Set<PhoneticAttribute> phoneticAttributesOfSurface) {
                final boolean lastLetterVoiceless = phoneticAttributesOfSurface.contains(PhoneticAttribute.LastLetterVoiceless);
                if (lastLetterVoiceless)
                    return TurkishAlphabet.devoice(charToAdd.getLetter()).getCharValue();
                else
                    return charToAdd.getCharValue();
            }
        };

        public abstract Character apply(TurkishChar charToAdd, Set<PhoneticAttribute> phoneticAttributesOfSurface);

    }
}


