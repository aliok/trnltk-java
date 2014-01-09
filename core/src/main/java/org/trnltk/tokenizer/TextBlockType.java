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

package org.trnltk.tokenizer;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ali Ok
 */
public enum TextBlockType {
    // order is important!

    Sentence_Start {
        @Override
        public String findMatchFromBeginning(String text) {
            throw new UnsupportedOperationException("Sentence_Start should not be used for text matching");
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_WhiteSpace;
        }
    },
    Sentence_End {
        @Override
        public String findMatchFromBeginning(String text) {
            throw new UnsupportedOperationException("Sentence_End should not be used for text matching");
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_WhiteSpace;
        }
    },

    //    Url{
//        // regex for urls can be crazy: http://stackoverflow.com/questions/161738/what-is-the-best-regular-expression-to-check-if-a-string-is-a-valid-url
//        private Pattern PATTERN = Pattern.compile("");
//
//        @Override
//        public String findMatchFromBeginning(String text) {
//            return null;
//        }
//    },
    Comma {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch(",", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Colon {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch(":", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_Punc;
        }
    },
    SemiColon {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch(";", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_Punc;
        }
    },
    Percent {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("%", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_Punc;
        }
    },
    Dash {
        @Override
        public String findMatchFromBeginning(String text) {
            //other dash types from group Pd is converted to '-' anyway. See for others : http://www.fileformat.info/info/unicode/category/Pd/list.htm
            return getStringMatch("-", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_Dash;
        }
    },
    Apostrophe {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("'", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Quote {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("\"", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Underscore {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("_", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Parenthesis_Start {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("(", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Parenthesis_End {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch(")", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Slash {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("/", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_Punc;
        }
    },
    Ellipsis {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("...", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Ellipsis_Exclamation {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("!..", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Ellipsis;
        }
    },
    Ellipsis_Question {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch("?..", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Ellipsis;
        }
    },
    Dot {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch(".", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Other_Dash {
        // See http://www.fileformat.info/info/unicode/category/Pd/list.htm
        private final Pattern PATTERN = Pattern.compile("^\\p{Pd}+");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Math_Symbol {
        //see http://www.fileformat.info/info/unicode/category/Sm/list.htm
        private final Pattern PATTERN = Pattern.compile("^\\p{Sm}");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Other_Punc;
        }
    },
    Other_Punc {
        // See http://www.fileformat.info/info/unicode/category/index.htm
        private final Pattern PATTERN = Pattern.compile("^(\\p{Po}|\\p{Ps}|\\p{Pe})");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Space {
        @Override
        public String findMatchFromBeginning(String text) {
            return getStringMatch(" ", text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Other_WhiteSpace {
        private final Pattern PATTERN = Pattern.compile("^\\p{Space}+");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    OtherSymbol {
        private final Pattern PATTERN = Pattern.compile("^\\p{So}+");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Roman_Numeral {
        // beware: pattern also matches empty string
        // (?!(I|M|C|X|L|V|D)) part is negative look ahead. So, VIIII is not marked as roman numeral, but VIII is
        private Pattern PATTERN = Pattern.compile("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})(?!(I|M|C|X|L|V|D))");

        @Override
        public String findMatchFromBeginning(String text) {
            // since pattern also matches empty string, check it first
            final String patternMatch = getPatternMatch(PATTERN, text);
            if (StringUtils.isEmpty(patternMatch)) {
                return null;
            } else {
                // since 'V' in 'Veli' is also matched, check character after
                // can't be letter
                if (patternMatch.length() < text.length() && Character.isLetter(text.charAt(patternMatch.length())))
                    return null;
                else
                    return patternMatch;
            }
        }

        @Override
        public TextBlockType getInferenceType() {
            return Digits;
        }
    },
    Digits {
        private final Pattern PATTERN = Pattern.compile("^\\d+");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    Abbreviation {
        private final ImmutableSet<String> abbreviations;
        {
            try {
                final ImmutableSet.Builder<String> setBuilder = new ImmutableSet.Builder<String>();
                final List<String> lines = Resources.readLines(Resources.getResource("tokenizer/abbreviations.txt"), Charsets.UTF_8);
                for (String line : lines) {
                    //skip the ones without "." at the end
                    final int abbrEndIndex = line.indexOf(":");
                    Validate.isTrue(abbrEndIndex > 0, line);
                    final String abbr = line.substring(0, abbrEndIndex);
                    if (!abbr.endsWith("."))
                        continue;
                    setBuilder.add(abbr);
                }
                this.abbreviations = setBuilder.build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String findMatchFromBeginning(String text) {
            //go up to N
            int N = 6; // longest single word abbr in dictionary is "Gnkur."
            //try matching the longest
            for (int i = Math.min(N, text.length()); i >= 0; i--) {
                final String substring = text.substring(0, i);
                if (abbreviations.contains(substring))
                    return substring;
            }

            return null;
        }

        @Override
        public TextBlockType getInferenceType() {
            return Word;
        }
    },
    Capitalized_Word {
        private final Pattern PATTERN = Pattern.compile("^\\p{Lu}\\p{Ll}+");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Word;
        }
    },
    AllCaps_Word {
        private final Pattern PATTERN = Pattern.compile("^\\p{Lu}+");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return Capitalized_Word;
        }
    },
    Word {
        private final Pattern PATTERN = Pattern.compile("^\\p{L}+");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    },
    OtherAnyChar {
        private final Pattern PATTERN = Pattern.compile("^.");

        @Override
        public String findMatchFromBeginning(String text) {
            return getPatternMatch(PATTERN, text);
        }

        @Override
        public TextBlockType getInferenceType() {
            return null;
        }
    };

    public static final ImmutableSet<TextBlockType> META_TYPES = Sets.immutableEnumSet(Sentence_Start, Sentence_End);
    public static final ImmutableSet<TextBlockType> PHYSICAL_TYPES = Sets.immutableEnumSet(
            Sets.difference(
                    Sets.immutableEnumSet(Lists.newArrayList(TextBlockType.values())),
                    META_TYPES));

    // map of e.g [
    //      Word->{Capitalized_Word},
    //      Capitalized_Word->{AllCaps_Word},
    //      OtherPunc->{Ellipsis, MathSymbol}]
    public static final ImmutableMultimap<TextBlockType, TextBlockType> INFERENCE_MAP;

    static {
        final ImmutableMultimap.Builder<TextBlockType, TextBlockType> builder = new ImmutableMultimap.Builder<TextBlockType, TextBlockType>();
        for (TextBlockType keyType : TextBlockType.values()) {
            for (TextBlockType valueType : TextBlockType.values()) {
                if (keyType.equals(valueType.getInferenceType()))
                    builder.put(keyType, valueType);
            }
        }

        INFERENCE_MAP = builder.build();
    }


    public abstract String findMatchFromBeginning(String text);

    public abstract TextBlockType getInferenceType();

    private static String getPatternMatch(Pattern pattern, String text) {
        final Matcher matcher = pattern.matcher(text);
        if (matcher.find())
            return matcher.group();
        else
            return null;
    }

    private static String getStringMatch(String matchStr, String text) {
        if (text.startsWith(matchStr))
            return matchStr;
        else
            return null;
    }
}
