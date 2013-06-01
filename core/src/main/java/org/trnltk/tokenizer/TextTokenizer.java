package org.trnltk.tokenizer;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextTokenizer {

    private static final ImmutableSet<Character> PUNC_CHARS = new ImmutableSet.Builder<Character>()
            .add(
                    '.', ',', ',', '-', '!', '?', ':', '-', '\"', '(', ')',
                    ';', '/', '[', ']', '{', '}', '$', '€', '£', '¥', '#', '%', '+'
            )
            .build();     //  " ' " and " - ", is not included on purpose

    public Iterable<String> tokenize(String text) {
        if (Strings.isNullOrEmpty(text))
            return Collections.emptyList();

        text = text.trim();
        text = normalizeQuotesHyphens(text);
        text = separateParantsesQuotesFromSentence(text);
        text = this.insertSpacesForPuncChars(text);
        text = this.replaceAllWhiteSpaceWithSingleSpace(text);

        return Splitter.on(' ').trimResults().omitEmptyStrings().split(text);
    }

    /**
     * insert space before punc chars. except:
     * <p/>
     * 1. Dot comes after number : "3."
     * <p/>
     * 2. Punc char comes after punc char : "..." (except comma after dot)
     * <p/>
     * 3. The apostrophe used in a proper name or a number : "Ahmet'in", "3'e", "3.'ye"
     * <p/>
     * 4. The quoted stuff for emphasis : 'Cok "aptal"di o adam' #TODO
     * <p/>
     * 5. Punc chars between numbers : "5:20", "5.123.456", "5,12" (but not "5, 6, 7")
     * <p/>
     *
     * @param text input
     * @return modified input
     */
    private String insertSpacesForPuncChars(String text) {
        /*
        * # use a string builder(?). add chars directly, except we need to insert space in front of them.
        * # with a random access list(string builder), we can check the last char for the exception cases described above
         */

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char currentChar = text.charAt(i);
            if (!PUNC_CHARS.contains(currentChar)) {
                builder.append(currentChar);
            } else {
                if (builder.length() == 0) {
                    builder.append(currentChar);
                    continue;
                }

                final Character lastChar = builder.length() > 0 ? builder.charAt(builder.length() - 1) : null;
                final Character nextChar = i + 1 >= text.length() ? null : text.charAt(i + 1);

                boolean addSpace = true;

                final boolean lastCharIsPuncChar = lastChar != null && PUNC_CHARS.contains(lastChar);
                final boolean lastCharIsDigit = lastChar != null && Character.isDigit(lastChar);
                final boolean nextCharIsDigit = nextChar != null && Character.isDigit(nextChar);

                // check case #1
                if (lastCharIsDigit && currentChar == '.') {
                    addSpace = false;
                }

                // check case #2
                else if (lastCharIsPuncChar) {
                    addSpace = currentChar == ',' && lastChar == '.';
                }

                //check case #3
//                else if(){
//                    // apostrophe is not a punc char anymore
//                }

                //case #4 is not yet supported

                // check case #5
                else if (lastCharIsDigit && nextCharIsDigit) {
                    addSpace = false;
                }
                if (addSpace)
                    builder.append(' ');

                builder.append(currentChar);
            }
        }
        return builder.toString();
    }

    private String replaceAllWhiteSpaceWithSingleSpace(String text) {
        return text.replaceAll("\\s+", " ");
    }

    /**
     * applies separateBeginEndSymbolsFromWord to each key in the input string.
     * <p>examples:
     * <p>'evet, "%15'i" kadar mi yoksa 12? -> ' evet , " %15'i " kadar mi yoksa 12 ?
     *
     * @param input input sentence.
     * @return input sentence but symbols on the beginning and end sybols are separated.
     */
    public static String separateParantsesQuotesFromSentence(String input) {
        StringBuilder sb = new StringBuilder(input.length() + 7);
        for (String block : Splitter.on(" ").split(input)) {
            sb.append(separateParantsesQuotesFromWord(block)).append(" ");
        }
        return sb.toString().trim();
    }

    static Pattern PARANTHESIS_QUOTES_SEPARATOR = Pattern.compile("^(['\\\"\\(\\)\\[\\]{}]+|)(.+?)([\\)\\(\\[\\]{}'\\\"]+|)$");

    /**
     * <p>separates paranthesis and quotes from words.
     *
     * @param input input string.
     * @return output.
     */
    public static String separateParantsesQuotesFromWord(String input) {
        Matcher matcher = PARANTHESIS_QUOTES_SEPARATOR.matcher(input);
        if (!matcher.find()) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length() + 3);
        sb.append(matcher.group(1)).append(" ").append(matcher.group(2)).append(" ").append(matcher.group(3));
        return sb.toString().trim();
    }


    /**
     * This method converts different single and double quote symbols to a unified form.
     * also it reduces two connected single quotes to a one double quote.
     *
     * @param input input string.
     * @return cleaned input string.
     */
    public static String normalizeQuotesHyphens(String input) {
        // rdquo, ldquo, laquo, raquo, Prime sybols in unicode.
        return input
                .replaceAll("[\u201C\u201D\u00BB\u00AB\u2033\u0093\u0094]|''", "\"")
                .replaceAll("[\u0091\u0092\u2032´`’‘]", "'")
                .replaceAll("[\u0096\u0097–]", "-");
    }
}
