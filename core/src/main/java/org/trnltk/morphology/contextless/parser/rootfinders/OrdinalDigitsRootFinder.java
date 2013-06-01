package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.model.suffixbased.SuffixFormSequence;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.numeral.DigitsToTextConverter;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class OrdinalDigitsRootFinder implements RootFinder {

    private static List<Pattern> NUMBER_REGEXES = Arrays.asList(
            Pattern.compile("^[-+]?\\d+\\.$"),
            Pattern.compile("^[-+]?(\\d{1,3}\\.)+\\d{3}\\.$")
    );

    private static final char ORDINAL_CHAR = '.';
    private static final char APOSTROPHE = '\'';

    private static final SuffixFormSequence ORDINAL_SUFFIX_FORM = new SuffixFormSequence("+IncI");

    private final PhoneticsEngine phoneticsEngine;
    private final DigitsToTextConverter digitsToTextConverter;
    private final PhoneticsAnalyzer phoneticsAnalyzer;

    public OrdinalDigitsRootFinder() {
        this.digitsToTextConverter = new DigitsToTextConverter();
        this.phoneticsAnalyzer = new PhoneticsAnalyzer();
        this.phoneticsEngine = new PhoneticsEngine(new SuffixFormSequenceApplier());
    }

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence input) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (partialInput.getLastChar().getCharValue() != ORDINAL_CHAR)
            //quick check:
            return false;

        if (partialInput.length() < input.length()) {
            final char charAfterPartialInput = input.charAt(partialInput.length()).getCharValue();
            if (Character.isDigit(charAfterPartialInput)) {
                // if next char is also a digit, don't return a root for current partial input
                // so, for whole surface "9.123.", partial inputs "9" and "9.123" will not return any roots
                return false;
            } else if (charAfterPartialInput != APOSTROPHE) {
                // if next char is alpha or punc etc, don't return any roots for current partial input
                // only apostrophe is ok, because of this -> "1.'nin hediyesi"
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
        final String strNumeralWithoutOrdinalChar = partialInputUnderlyingString.substring(0, partialInputUnderlyingString.length() - 1);
        final String underlyingNumeralTextWithoutOrdinal = digitsToTextConverter.convert(strNumeralWithoutOrdinalChar);
        final boolean voicingAllowed = voicingAllowed(strNumeralWithoutOrdinalChar);
        final String underlyingNumeralText = this.applyOrdinalText(underlyingNumeralTextWithoutOrdinal, voicingAllowed);
        final ImmutableSet<PhoneticAttribute> phoneticAttributes = ImmutableSet.copyOf(this.phoneticsAnalyzer.calculatePhoneticAttributes(underlyingNumeralText, null));
        return Arrays.asList((Root) new NumeralRoot(partialInput, underlyingNumeralText, SecondaryPos.ORDINAL_DIGITS, phoneticAttributes));
    }

    private String applyOrdinalText(String underlyingNumeralTextWithoutOrdinal, boolean voicingAllowed) {
        final HashSet<LexemeAttribute> lexemeAttributes = voicingAllowed ? Sets.<LexemeAttribute>newHashSet() : Sets.newHashSet(LexemeAttribute.NoVoicing);
        final Pair<TurkishSequence, String> application = this.phoneticsEngine.apply(new TurkishSequence(underlyingNumeralTextWithoutOrdinal), ORDINAL_SUFFIX_FORM, lexemeAttributes);
        return application.getLeft().getUnderlyingString() + application.getRight();
    }

    private boolean voicingAllowed(String strNumeralWithoutOrdinalChar) {
        // only in the case of 'dördüncü' voicing is allowed
        // in other cases like 'üçüncü', 'kırkıncı' it is not allowed
        return strNumeralWithoutOrdinalChar.endsWith("4");
    }

}
