package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.ImmutableSet;
import org.trnltk.morphology.model.NumeralRoot;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.numeral.DigitsToTextConverter;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CardinalDigitsRootFinder implements RootFinder {

    // TODO: how about marking "10." as Ordinal?
    // TODO: how about marking "10%" as Percentage?
    // TODO: how about marking "10-20" as Range?
    // TODO: how about others?
    private static List<Pattern> NUMBER_REGEXES = Arrays.asList(
            Pattern.compile("^[-+]?\\d+(,\\d)?\\d*$"),
            Pattern.compile("^[-+]?(\\d{1,3}\\.)+\\d{3}(,\\d)?\\d*$")
    );

    private static final char APOSTROPHE = '\'';
    private static final char GROUPING_SEPARATOR = '.';
    private static final char FRACTION_SEPARATOR = ',';

    private final DigitsToTextConverter digitsToTextConverter;
    private final PhoneticsAnalyzer phoneticsAnalyzer;

    public CardinalDigitsRootFinder() {
        this.digitsToTextConverter = new DigitsToTextConverter();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence input) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (!Character.isDigit(partialInput.getLastChar().getCharValue()) &&
                partialInput.getLastChar().getCharValue() != GROUPING_SEPARATOR &&
                partialInput.getLastChar().getCharValue() != FRACTION_SEPARATOR)
            //quick check:
            return false;

        if (partialInput.getLastChar().getCharValue() == APOSTROPHE)
            // if last char is apostrophe, then a root was already created for the input with 1 char less
            return false;

        if (partialInput.length() < input.length()) {
            final char charAfterPartialInput = input.charAt(partialInput.length()).getCharValue();
            if (Character.isDigit(charAfterPartialInput)) {
                // if next char is also a digit, don't return a root for current partial input
                // so, for whole surface "123", partial inputs "1" and "12" will not return any roots
                return false;
            } else if (charAfterPartialInput == GROUPING_SEPARATOR) {
                // if next char is the grouping separator, don't return a root for current partial input
                // so, for whole surface "123.456'e", partial input "123" will not return any roots
                return false;
            } else if (charAfterPartialInput == FRACTION_SEPARATOR) {
                // if next char is also the fraction separator, don't return a root for current partial input
                // so, for whole surface "12,3", partial inputs "12" will not return any roots
                return false;
            } else {
                // if there is apostrophe in whole surface, but that apostrophe is not the char after current partial input, skip this one
                // thus, for surface "12'ye", only partial input "12" returns a root
                final String inputUnderlyingString = input.getUnderlyingString();
                final int lastIndexOfApostopheInInput = inputUnderlyingString.lastIndexOf(APOSTROPHE);
                if (lastIndexOfApostopheInInput > 0 && lastIndexOfApostopheInInput != partialInput.length()) {
                    return false;
                }
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
        final String underlyingNumeralText = digitsToTextConverter.convert(partialInputUnderlyingString);
        final ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.copyOf(this.phoneticsAnalyzer.calculatePhoneticAttributes(underlyingNumeralText, null));
        return Arrays.asList((Root) new NumeralRoot(partialInput, underlyingNumeralText, SecondaryPos.CARDINAL_DIGITS, phoneticAttributes));
    }

}
