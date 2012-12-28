package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.ImmutableSet;
import org.trnltk.morphology.contextless.parser.RootFinder;
import org.trnltk.morphology.model.NumeralRoot;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.SecondaryPos;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.numeral.DigitsToTextConverter;
import zemberek3.lexicon.tr.PhonAttr;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class NumeralRootFinder implements RootFinder {
    private static List<Pattern> NUMBER_REGEXES = Arrays.asList(
            Pattern.compile("^[-+]?\\d+(,\\d)?\\d*$"),
            Pattern.compile("^[-+]?(\\d{1,3}\\.)+\\d{3}(,\\d)?\\d*$")
    );

    private final DigitsToTextConverter digitsToTextConverter;
    private final PhoneticsAnalyzer phoneticsAnalyzer;

    public NumeralRootFinder() {
        this.digitsToTextConverter = new DigitsToTextConverter();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Override
    public List<Root> findRootsForPartialInput(final TurkishSequence partialInput, final TurkishSequence input) {
        for (Pattern pattern : NUMBER_REGEXES) {
            final String partialInputUnderlyingString = partialInput.getUnderlyingString();
            if (pattern.matcher(partialInputUnderlyingString).matches()) {
                final String underlyingNumeralText = digitsToTextConverter.convert(partialInputUnderlyingString);
                final ImmutableSet<PhonAttr> phonAttrs = ImmutableSet.copyOf(this.phoneticsAnalyzer.calculatePhoneticAttributes(underlyingNumeralText, null));
                return Arrays.asList((Root) new NumeralRoot(partialInput, underlyingNumeralText, SecondaryPos.DIGITS, phonAttrs));
            }
        }

        return new ArrayList<Root>();
    }

}
