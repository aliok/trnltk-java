package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Lists;
import org.trnltk.morphology.model.*;
import org.trnltk.morphology.model.structure.TurkishAlphabet;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.model.structure.TurkishChar;
import org.trnltk.morphology.model.lexicon.PrimaryPos;
import org.trnltk.morphology.model.lexicon.tr.PhoneticAttribute;
import org.trnltk.morphology.model.lexicon.tr.PhoneticExpectation;
import org.trnltk.morphology.model.structure.TurkicLetter;

import java.util.*;

/**
 * Tries to find the root by brute force.
 * <p/>
 * Checks for the signs of the orthographic changes, and finds roots according to that.
 * Checks for possible inverse harmony, doubling, voicing (except nk->nG voicing) and explicit no voicing.
 * Doesn't check possible vowel drops.
 */
public class BruteForceNounRootFinder implements RootFinder {

    final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence wholeSurface) {
        if (partialInput == null || partialInput.isBlank())
            return false;

        if (wholeSurface == null || wholeSurface.isBlank()) {
            return false;
        }

        if (wholeSurface.length() < partialInput.length())
            return false;

        if (partialInput.length() < 2 && wholeSurface.length() >= 2)
            return false;

        if (!wholeSurface.startsWith(partialInput))
            return false;

        return true;
    }

    @Override
    public Collection<DynamicRoot> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence wholeSurface) {
        final TurkishSequence rootSeq = partialInput;
        final TurkishSequence lemmaSeq = rootSeq;
        final TurkishSequence lemmaRootSeq = lemmaSeq;
        final PrimaryPos primaryPos = PrimaryPos.Noun;
        final SecondaryPos secondaryPos = null;
        final EnumSet<LexemeAttribute> lexemeAttributes = EnumSet.noneOf(LexemeAttribute.class);
        final DynamicLexeme lexeme = new DynamicLexeme(lemmaSeq.getUnderlyingString(), lemmaRootSeq.getUnderlyingString(), primaryPos, secondaryPos, lexemeAttributes);
        final EnumSet<PhoneticExpectation> phoneticExpectations = EnumSet.noneOf(PhoneticExpectation.class);
        final EnumSet<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes(partialInput, lexemeAttributes);

        final DynamicRoot noOrtographicRoot = new DynamicRoot(rootSeq, lexeme, phoneticAttributes, phoneticExpectations);


        if (wholeSurface.equals(partialInput) || partialInput.length() < 2)
            return Arrays.asList(noOrtographicRoot);

        final TurkishChar lastVowel = partialInput.getLastVowel();

        if (lastVowel == null)
            return Arrays.asList(noOrtographicRoot);

        final TurkishChar lastChar = partialInput.getLastChar();
        final TurkishChar firstCharAfterPartialInput = wholeSurface.charAt(partialInput.length());
        if (Character.isUpperCase(lastChar.getCharValue()) || Character.isUpperCase(firstCharAfterPartialInput.getCharValue()))
            return Arrays.asList(noOrtographicRoot);

        final List<DynamicRoot> roots = this.getVoicingAndDoublingRoots(partialInput, lastChar, firstCharAfterPartialInput, noOrtographicRoot);

        final TurkishChar firstVowelAfterPartialInput = wholeSurface.subsequence(partialInput.length() - 1).getFirstVowel();
        if (firstVowelAfterPartialInput != null) {
            if (lastVowel.getLetter().isFrontal() != firstVowelAfterPartialInput.getLetter().isFrontal()) {
                for (Root root : roots) {
                    root.getLexeme().getAttributes().add(LexemeAttribute.InverseHarmony);
                }
            }
        }

        for (DynamicRoot root : roots) {
            final EnumSet<PhoneticAttribute> phoneticAttributesOfRoot = this.phoneticsAnalyzer.calculatePhoneticAttributes(root.getSequence(), root.getLexeme().getAttributes());
            root.setPhoneticAttributes(phoneticAttributesOfRoot);
        }

        return roots;
    }

    private List<DynamicRoot> getVoicingAndDoublingRoots(TurkishSequence partialInput, TurkishChar lastChar, TurkishChar firstCharAfterPartialInput, DynamicRoot noOrtographicRoot) {
        final TurkicLetter lastLetter = lastChar.getLetter();
        final TurkicLetter firstLetterAfterPartialInput = firstCharAfterPartialInput.getLetter();

        final boolean noVoicingRuleApplies = TurkishAlphabet.Voicable_Letters.contains(lastLetter) && firstLetterAfterPartialInput.isVowel();
        final boolean voicingMightHaveHappened = TurkishAlphabet.Inverse_Voicing_Map.containsKey(lastLetter) && firstLetterAfterPartialInput.isVowel();
        final boolean doublingMightHaveHappened = partialInput.length() > 2
                && !lastLetter.isVowel()
                && partialInput.charAt(partialInput.length() - 1).equals(partialInput.charAt(partialInput.length() - 2))
                && firstLetterAfterPartialInput.isVowel();

        if (doublingMightHaveHappened) {
            final DynamicRoot doublingRoot = this.createDoublingRoot(noOrtographicRoot, lastChar);

            if (noVoicingRuleApplies) {
                noOrtographicRoot.getLexeme().setAttributes(EnumSet.of(LexemeAttribute.NoVoicing));
                doublingRoot.getLexeme().getAttributes().add(LexemeAttribute.NoVoicing);
                return Arrays.asList(noOrtographicRoot, doublingRoot);
            } else if (voicingMightHaveHappened) {
                final List<DynamicRoot> inverseDevoicingRoots = this.inverseDevoiceLastLetter(noOrtographicRoot, lastLetter);
                final List<DynamicRoot> devoicingDoublingRoots = Lists.transform(inverseDevoicingRoots, new Function<DynamicRoot, DynamicRoot>() {
                    @Override
                    public DynamicRoot apply(DynamicRoot input) {
                        final String lemmaRoot = input.getLexeme().getLemmaRoot();
                        return createDoublingRoot(input, TurkishAlphabet.getInstance().getChar(lemmaRoot.charAt(lemmaRoot.length() - 1)));
                    }
                });

                final ArrayList<DynamicRoot> roots = new ArrayList<DynamicRoot>();
                roots.add(noOrtographicRoot);
                roots.add(doublingRoot);
                roots.addAll(devoicingDoublingRoots);
                return roots;
            } else {
                return Arrays.asList(noOrtographicRoot, doublingRoot);
            }
        } else {
            if (noVoicingRuleApplies) {
                noOrtographicRoot.getLexeme().setAttributes(EnumSet.of(LexemeAttribute.NoVoicing));
                return Arrays.asList(noOrtographicRoot);
            } else if (voicingMightHaveHappened) {
                final List<DynamicRoot> devoicingRoots = this.inverseDevoiceLastLetter(noOrtographicRoot, lastLetter);
                ArrayList<DynamicRoot> roots = new ArrayList<DynamicRoot>();
                roots.add(noOrtographicRoot);
                roots.addAll(devoicingRoots);
                return roots;
            } else {
                return Arrays.asList(noOrtographicRoot);
            }
        }
    }

    private List<DynamicRoot> inverseDevoiceLastLetter(DynamicRoot noOrtographicRoot, TurkicLetter lastLetter) {
        final ImmutableCollection<TurkicLetter> inverseDevoicingLetters = TurkishAlphabet.Inverse_Voicing_Map.get(lastLetter);
        final List<DynamicRoot> inverseDevoicedRoots = new ArrayList<DynamicRoot>();
        final String lemmaRoot = noOrtographicRoot.getLexeme().getLemmaRoot();
        for (TurkicLetter inverseDevoicingLetter : inverseDevoicingLetters) {
            final DynamicRoot voicingRoot = new DynamicRoot(noOrtographicRoot);
            final String voicedLemma = lemmaRoot.substring(0, lemmaRoot.length() - 1) + inverseDevoicingLetter.charValue;
            voicingRoot.getLexeme().setLemma(voicedLemma);
            voicingRoot.getLexeme().setLemmaRoot(voicedLemma);
            inverseDevoicedRoots.add(voicingRoot);
        }

        return inverseDevoicedRoots;
    }

    private DynamicRoot createDoublingRoot(DynamicRoot noOrtographicRoot, TurkishChar lastChar) {
        final DynamicRoot doublingRoot = new DynamicRoot(noOrtographicRoot);
        final String originalLemmaRoot = doublingRoot.getLexeme().getLemmaRoot();
        final String doublingLemmaRootStr = originalLemmaRoot.substring(0, originalLemmaRoot.length() - 2) + lastChar.getCharValue();
        doublingRoot.getLexeme().setLemmaRoot(doublingLemmaRootStr);
        doublingRoot.getLexeme().setLemma(doublingLemmaRootStr);
        doublingRoot.getLexeme().getAttributes().add(LexemeAttribute.Doubling);
        return doublingRoot;
    }

}
