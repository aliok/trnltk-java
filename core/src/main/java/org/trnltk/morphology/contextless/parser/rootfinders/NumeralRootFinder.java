package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.trnltk.morphology.contextless.parser.RootFinder;
import org.trnltk.morphology.model.NumeralRoot;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.numeral.DigitsToTextConverter;
import zemberek3.lexicon.tr.PhonAttr;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class NumeralRootFinder implements RootFinder {

    // TODO: how about marking "10." as Ordinal?
    private static List<Pattern> NUMBER_REGEXES = Arrays.asList(
            Pattern.compile("^[-+]?\\d+(,\\d)?\\d*$"),
            Pattern.compile("^[-+]?(\\d{1,3}\\.)+\\d{3}(,\\d)?\\d*$")
    );

    private static final char APOSTROPHE = '\'';
    private static final char GROUPING_SEPARATOR = '.';
    private static final char FRACTION_SEPARATOR = ',';

    private final DigitsToTextConverter digitsToTextConverter;
    private final PhoneticsAnalyzer phoneticsAnalyzer;

    public NumeralRootFinder() {
        this.digitsToTextConverter = new DigitsToTextConverter();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    public List<Root> findRootsForPartialInput(final TurkishSequence partialInput, final TurkishSequence input) {
        if (partialInput.length() < input.length()) {
            final char charAfterPartialInput = input.charAt(partialInput.length()).getCharValue();
            if (Character.isDigit(charAfterPartialInput))
                // if next char is also a digit, don't return a root for current partial input
                // so, for whole surface "123", partial inputs "1" and "12" will not return any roots
                return Lists.newArrayList();
            else if (charAfterPartialInput == GROUPING_SEPARATOR)
                // if next char is the grouping separator, don't return a root for current partial input
                // so, for whole surface "123.456'e", partial input "123" will not return any roots
                return Lists.newArrayList();
            else if (charAfterPartialInput == FRACTION_SEPARATOR)
                // if next char is also the fraction separator, don't return a root for current partial input
                // so, for whole surface "12,3", partial inputs "12" will not return any roots
                return Lists.newArrayList();
        }

        for (Pattern pattern : NUMBER_REGEXES) {
            final String partialInputUnderlyingString = partialInput.getUnderlyingString();
            if (pattern.matcher(partialInputUnderlyingString).matches()) {
                if (!input.equals(partialInput)) {
                    final String inputUnderlyingString = input.getUnderlyingString();
                    final int lastIndexOfApostopheInInput = inputUnderlyingString.lastIndexOf(APOSTROPHE);
                    if (lastIndexOfApostopheInInput > 0 && lastIndexOfApostopheInInput != partialInputUnderlyingString.length()) {
                        // if there is apostrophe in whole surface, but that apostrophe is not the char after current partial input, skip this one
                        // thus, for surface "12'ye", only partial input "12" returns a root
                        continue;
                    }
                }

                final String underlyingNumeralText = digitsToTextConverter.convert(partialInputUnderlyingString);
                final ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.copyOf(this.phoneticsAnalyzer.calculatePhoneticAttributes(underlyingNumeralText, null));
                return Arrays.asList((Root) new NumeralRoot(partialInput, underlyingNumeralText, SecondaryPos.DIGITS, phonAttrs));
            }
        }

        return Lists.newArrayList();
    }

}
