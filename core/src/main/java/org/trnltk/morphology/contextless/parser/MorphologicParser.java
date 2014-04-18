/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.trnltk.morphology.contextless.parser;

import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.model.letter.TurkishSequence;

import java.util.List;

/**
 * The contract for the morphologic parser implementations.
 * <p/>
 * A morphologic parser takes the input and fragments it into smaller parts, namely morphemes.
 * These smaller parts are root, suffixes, etc.
 * Morphemes are contained within a {@link MorphemeContainer}.
 */
public interface MorphologicParser {

    /**
     * Parses the given string and returns all of the possible morphologic parse results for it.
     */
    public List<MorphemeContainer> parseStr(final String input);

    /**
     * Parses the given {@link TurkishSequence} and returns all of the possible morphologic parse results for it.
     */
    public List<MorphemeContainer> parse(final TurkishSequence input);

    /**
     * Parses all of the given strings and returns all possible results for each. Returned results are in inputs' order.
     */
    public List<List<MorphemeContainer>> parseAllStr(final List<String> input);

    /**
     * Parses all of the given {@link TurkishSequence}s and returns all possible results for each. Returned results are in inputs' order.
     */
    @SuppressWarnings("UnusedDeclaration")
    public List<List<MorphemeContainer>> parseAll(final List<TurkishSequence> input);
}
