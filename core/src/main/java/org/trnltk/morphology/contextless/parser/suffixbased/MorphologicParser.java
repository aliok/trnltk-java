package org.trnltk.morphology.contextless.parser.suffixbased;

import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.List;

/**
 * The contract for the morphologic parser implementations.
 *
 * A morphologic parser takes the input and fragments it into smaller parts. These smaller parts are root, suffixes, etc.
 * These parts are contained within a {@link MorphemeContainer}.
 */
public interface MorphologicParser {
    public List<MorphemeContainer> parseStr(final String input);
    public List<MorphemeContainer> parse(final TurkishSequence input);

    public List<List<MorphemeContainer>> parseAllStr(final List<String> input);
    public List<List<MorphemeContainer>> parseAll(final List<TurkishSequence> input);
}
