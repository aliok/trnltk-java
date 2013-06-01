package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import org.trnltk.morphology.model.NumeralRoot;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.numeral.DigitsToTextConverter;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RangeDigitsRootFinder implements RootFinder {

    private static List<Pattern> NUMBER_REGEXES = Arrays.asList(
            // ^(NormalNr or GroupedNr) ( RangeOp [NormalNr or GroupedNr] )* RangeOp (NormalNr or GroupedNr)$
            Pattern.compile("^((\\d{1,3}\\.)+\\d{3}|\\d+)(-((\\d{1,3}\\.)+\\d{3}|\\d+))*-((\\d{1,3}\\.)+\\d{3}|\\d+)$")
    );

    private static final char RANGE_CHAR = '-';
    private static final char ORDINAL_CHAR = '.';
    private static final char APOSTROPHE = '\'';

    private final DigitsToTextConverter digitsToTextConverter;
    private final PhoneticsAnalyzer phoneticsAnalyzer;

    public RangeDigitsRootFinder() {
        this.digitsToTextConverter = new DigitsToTextConverter();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence input) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (!Character.isDigit(partialInput.getLastChar().getCharValue()))
            //quick check:
            return false;

        if (partialInput.length() < input.length()) {
            final char charAfterPartialInput = input.charAt(partialInput.length()).getCharValue();
            if (Character.isDigit(charAfterPartialInput)) {
                // if next char is also a digit, don't return a root for current partial input
                // so, for whole surface "10-20", partial inputs "10-2" and "1" will not return any roots
                return false;
            } else if (charAfterPartialInput != APOSTROPHE && charAfterPartialInput != ORDINAL_CHAR) {
                // if next char is alpha or punc etc, don't return any roots for current partial input
                // only apostrophe and ordinalChar ok, because of
                // "1-2'nin hediyesi" and
                // "1-2. oldu"
                return false;
            }
        }

        final String partialInputUnderlyingString = partialInput.getUnderlyingString();

        for (Pattern pattern : NUMBER_REGEXES) {
            if (pattern.matcher(partialInputUnderlyingString).matches()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Root> findRootsForPartialInput(final TurkishSequence partialInput, final TurkishSequence input) {
        final String partialInputUnderlyingString = partialInput.getUnderlyingString();
        final String underlyingNumeralText = this.getUnderlyingNumeralTextForRange(partialInputUnderlyingString);
        final ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.copyOf(this.phoneticsAnalyzer.calculatePhoneticAttributes(underlyingNumeralText, null));
        return Arrays.asList((Root) new NumeralRoot(partialInput, underlyingNumeralText, SecondaryPos.Range, phoneticAttributes));
    }

    private String getUnderlyingNumeralTextForRange(String partialInputUnderlyingString) {
        final Iterable<String> parts = Splitter.on(RANGE_CHAR).split(partialInputUnderlyingString);
        final StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(this.digitsToTextConverter.convert(part)).append(" ");
        }
        return builder.toString().trim();
    }

}
