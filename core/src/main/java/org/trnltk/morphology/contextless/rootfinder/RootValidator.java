package org.trnltk.morphology.contextless.rootfinder;

import org.apache.commons.lang3.Validate;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.Root;

/**
 * Validates a root.
 */
public class RootValidator {

    /**
     * Checks if a root is valid for a partial surface.
     * <p/>
     * A root cannot be longer than the partial surface.
     * <p/>
     * A root must be the beginning of a partial surface, except when the found root is a noun compound with
     * implicit 3rd person possession.
     */
    public boolean isValid(Root root, TurkishSequence partialSurface) {
        // a root for a partial sequence must be the beginning of the partialSurface
        // except when it is a compound.
        // in case of compounds, root is not the actual root, but the root of the lexeme
        // e.g.
        // partial_input = atkuyrugu
        // results_with_partial_input_one_char_missing : <'atkuyruk', 'atkuyrug', 'atkuyrugh'>

        // these are all roots of noun compound. so the lexeme can be derived from any of
        // atkuyruk+u, atkuyrug+u, atkuyrugh+u

        // we would like to see all of the following as the possible parse results:
        // atkuyruk+P3sg
        // atkuyrug+P3sg
        // atkuyrugh+P3sg

        Validate.notNull(root, "Root to validate cannot be null.");
        Validate.notNull(partialSurface, "Partial surface for the root cannot be null");

        final TurkishSequence rootSequence = root.getSequence();
        if (rootSequence.length() > partialSurface.length())
            return false;

        if (root.getLexeme().getAttributes().contains(LexemeAttribute.CompoundP3sg))
            return true;

        return partialSurface.getUnderlyingString().toLowerCase().startsWith(rootSequence.getUnderlyingString());

    }

}
