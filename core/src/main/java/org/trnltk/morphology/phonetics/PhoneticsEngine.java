/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.morphology.phonetics;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.trnltk.morphology.model.LexemeAttribute;
import org.trnltk.morphology.model.SuffixFormSequence;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import zemberek3.structure.TurkicLetter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class PhoneticsEngine {

    private static final char PLUS = '+';

    private final SuffixFormSequenceApplier suffixFormSequenceApplier;
    private final PhoneticsAnalyzer phoneticsAnalyzer = new PhoneticsAnalyzer();

    public PhoneticsEngine(SuffixFormSequenceApplier suffixFormSequenceApplier) {
        this.suffixFormSequenceApplier = suffixFormSequenceApplier;
    }

    public boolean isSuffixFormApplicable(final Set<PhoneticAttribute> phoneticAttributes, final SuffixFormSequence suffixFormSequence) {
        if (!suffixFormSequence.isNotBlank())
            return true;

        if (CollectionUtils.isEmpty(phoneticAttributes))
            return false;

        return this.suffixFormSequenceApplier.isApplicable(suffixFormSequence, phoneticAttributes);
    }

    public Pair<TurkishSequence, String> apply(TurkishSequence surface, SuffixFormSequence form, Collection<LexemeAttribute> lexemeAttributes) {
        return this.apply(surface, phoneticsAnalyzer.calculatePhoneticAttributes(surface, lexemeAttributes), form, lexemeAttributes);
    }

    public Pair<TurkishSequence, String> apply(final TurkishSequence surface, final Set<PhoneticAttribute> _phoneticAttributes, final SuffixFormSequence suffixFormSequence, final Collection<LexemeAttribute> _lexemeAttributes) {
        if (surface == null || surface.isBlank())
            return Pair.of(null, null);

        if (suffixFormSequence == null || !suffixFormSequence.isNotBlank())
            return Pair.of(surface, StringUtils.EMPTY);

        final Collection<LexemeAttribute> lexemeAttributes = _lexemeAttributes == null ? new ArrayList<LexemeAttribute>() : _lexemeAttributes;
        final Set<PhoneticAttribute> phoneticAttributes = _phoneticAttributes == null ? ImmutableSet.<PhoneticAttribute>of() : _phoneticAttributes;

        return this.handlePhonetics(surface, phoneticAttributes, suffixFormSequence, lexemeAttributes);
    }

    private Pair<TurkishSequence, String> handlePhonetics(final TurkishSequence _surface, final Set<PhoneticAttribute> phoneticAttributes, final SuffixFormSequence suffixFormSequence, final Collection<LexemeAttribute> lexemeAttributes) {
        TurkishSequence newSurface = _surface;

        // first try voicing
        if (!lexemeAttributes.contains(LexemeAttribute.NoVoicing) && phoneticAttributes.contains(PhoneticAttribute.LastLetterVoicelessStop) && suffixFormSequence.isFirstLetterVowel()) {
            newSurface = _surface.voiceLastLetterIfPossible();
        }

        final String appliedSuffixForm = suffixFormSequenceApplier.apply(suffixFormSequence, phoneticAttributes);

        return Pair.of(newSurface, appliedSuffixForm);
    }

    public boolean expectationsSatisfied(final Collection<PhoneticExpectation> phoneticExpectations, final SuffixFormSequence _form) {
        if (CollectionUtils.isEmpty(phoneticExpectations))
            return true;

        if (_form == null)
            return false;

        final String suffixFormStr = _form.getSuffixFormStr().trim();

        if (StringUtils.isBlank(suffixFormStr))
            return false;

        return Iterables.all(phoneticExpectations, new Predicate<PhoneticExpectation>() {
            @Override
            public boolean apply(PhoneticExpectation input) {
                return expectationSatisfied(input, suffixFormStr);
            }
        });
    }

    private boolean expectationSatisfied(PhoneticExpectation phoneticExpectation, String form) {
        final char firstCharOfForm = form.charAt(0);

        if (firstCharOfForm == PLUS)
            return this.expectationSatisfied(phoneticExpectation, form.substring(1)) || this.expectationSatisfied(phoneticExpectation, form.substring(2));

        switch (phoneticExpectation) {
            case VowelStart:
                return TurkishAlphabet.getLetterForChar(firstCharOfForm).isVowel();
            case ConsonantStart:
                return !TurkishAlphabet.getLetterForChar(firstCharOfForm).isVowel();
            default:
                throw new IllegalArgumentException("Unknown phonetic expectation : " + phoneticExpectation);
        }
    }

    /**
     * Checks if a suffix applied word is matched by a surface.
     * <p/>
     * >>> applicationMatches(u'armudunu', u'armut', True) ==> True
     * <br/>
     * >>> applicationMatches(u'armudunu', u'armut', False) ==> False
     * <br/>
     * >>> applicationMatches(u'armudunu', u'armudu', True) ==> True
     * <br/>
     * >>> applicationMatches(u'armudunu', u'armudu', False) ==> True
     * <br/>
     *
     * @param input          The input
     * @param appliedStr     appliedStr
     * @param voicingAllowed voicingAllowed
     * @return whether application matches
     */
    public boolean applicationMatches(final TurkishSequence input, final String appliedStr, final boolean voicingAllowed) {      //TODO: voicingAllowed is very ugly!
        if (StringUtils.isBlank(appliedStr) || appliedStr.length() > input.length())
            return false;

        final String inputUnderlyingString = input.getUnderlyingString();


        if (inputUnderlyingString.equals(appliedStr) || inputUnderlyingString.startsWith(appliedStr))
            return true;

        else if (voicingAllowed && inputUnderlyingString.startsWith(appliedStr.substring(0, appliedStr.length() - 1))) {
            final TurkicLetter lastLetterOfApplication = TurkishAlphabet.getLetterForChar(appliedStr.charAt(appliedStr.length() - 1));
            final TurkishChar lastLetterOfInputPart = input.charAt(appliedStr.length() - 1);
            return lastLetterOfInputPart.getLetter().equals(TurkishAlphabet.voiceLetter(lastLetterOfApplication));
        }

        return false;
    }
}
