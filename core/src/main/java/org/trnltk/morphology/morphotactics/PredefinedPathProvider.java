package org.trnltk.morphology.morphotactics;

import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;

import java.util.Set;

/**
 * Defines paths for special words / roots.
 * <p/>
 * There are phonetic and morphotactic exceptions for some special words in Turkish.
 * For these words, special suffixes or suffix forms are applied within this class. These exceptions are
 * separated from suffixes and forms defined in a {@link SuffixGraph} since they require a lot of new rules
 * to be created which are only applicable for these special words.
 * <p/>
 * One example is word "ben" whose one usage is <i>1st person <b>singular</b> personal pronoun</i>. Since there is another word
 * for <i>1st person <b>plural</b> personal pronoun</i>, "biz", this word should not accept the suffix <i>plural agreement</i>;
 * ie. "benler" is not valid. However, <i>plural agreement</i> suffix is normally applicable to all pronouns : "kimler", "neler", etc.
 * Thus, all possible paths that could be gone using this root is predefined in the system.
 * <p/>
 * Another example is word "onlar" which is <i>3rd person plural personal pronoun</i>. "onlar" is actually composed of
 * <i>3rd person singular personal pronoun</i>, "o", and the <i>plural agreement</i> "lar". However, suffix form "lar"
 * becomes "nlar" in this particular case. It doesn't make sense to add this as a new morphotactic rule in the
 * {@link SuffixGraph} since we have to define what is applicable (word "o") and what is not applicable (lots of words).
 * Thus, all possible paths in the {@link SuffixGraph} are build in advance for this root.
 * <p/>
 * Paths defined here must be checked when a new root is found for a surface. If found, predefined paths
 * ({@link MorphemeContainer}s) must be used as the starting point of the traversal in the {@link SuffixGraph}.
 * <p/>
 * There is a balance and trade off to define phonetic and morphotactic rules for these kind of words in here and defining
 * the rules in the {@link SuffixGraph}. More added here results in a more hardcoded system. More added in the
 * {@link SuffixGraph} results in a hard-to-maintain graph. Current approach is to define rules here if the exception is
 * for less than 3 roots and these roots are frequent ones in Turkish.
 */
public interface PredefinedPathProvider {
    void initialize();

    boolean hasPathsForRoot(Root root);

    Set<MorphemeContainer> getPaths(Root root);
}
