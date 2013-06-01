package org.trnltk.morphology.contextless.parser.rootfinders;

import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.ImmutableLexeme;
import org.trnltk.morphology.model.ImmutableRoot;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.lexicon.PrimaryPos;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class PuncRootFinder implements RootFinder {

    /**
     * You can check types defined in java.lang.Character class. such as START_PUNCTUATION
     * http://www.unicode.org/notes/tn36/Categories.txt
     * http://www.fileformat.info/info/unicode/category/index.htm
     * ALL Punc =
     * [Pc]	Punctuation, Connector
     * [Pd]	Punctuation, Dash
     * [Pe]	Punctuation, Close
     * [Pf]	Punctuation, Final quote (may behave like Ps or Pe depending on usage)
     * [Pi]	Punctuation, Initial quote (may behave like Ps or Pe depending on usage)
     * [Po]	Punctuation, Other
     * [Ps]	Punctuation, Open
     * [Sm]	Symbol, Math
     * [So]	Symbol, Other
     */
    private static final Pattern ALL_PUNC_PATTERN = Pattern.compile("^(\\p{Pc}|\\p{Pd}|\\p{Pe}|\\p{Pf}|\\p{Pi}|\\p{Po}|\\p{Ps}|\\p{Sm}|\\p{So})+$");

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence input) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (partialInput.length() == input.length()) {
            Validate.isTrue(input.equals(partialInput));
            return ALL_PUNC_PATTERN.matcher(partialInput.getUnderlyingString()).matches();
        }
        return false;
    }

    @Override
    public List<ImmutableRoot> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input) {
        final ImmutableLexeme lexeme = new ImmutableLexeme(partialInput.getUnderlyingString(), input.getUnderlyingString(), PrimaryPos.Punctuation, null, null);
        return Arrays.asList(new ImmutableRoot(partialInput, lexeme, null, null));
    }

}
