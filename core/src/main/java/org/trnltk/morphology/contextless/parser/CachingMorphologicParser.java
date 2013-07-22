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

import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.cache.MorphologicParserCache;

import java.util.*;

/**
 * Uses a caching with compute-if-absent logic. Different cache algorithms could be injected (One level, Two level, LRU, Time-based, etc.)
 */
public class CachingMorphologicParser implements MorphologicParser {

    private final MorphologicParser delegate;
    private final MorphologicParserCache cache;
    private final boolean useLocalCache;

    /**
     * Create a new caching parser.
     *
     * @param cache         Cache implementation instance
     * @param delegate      Morphologic parser to delegate parsing if results are absent
     * @param useLocalCache if true, a method-local cache is used while doing a batch parse
     */
    public CachingMorphologicParser(MorphologicParserCache cache, MorphologicParser delegate, boolean useLocalCache) {
        this.cache = cache;
        this.delegate = delegate;
        this.useLocalCache = useLocalCache;
        this.cache.build(delegate);
    }

    /**
     * @see CachingMorphologicParser#parse(org.trnltk.model.letter.TurkishSequence)
     * @deprecated Throws {@link UnsupportedOperationException}
     */
    @Override
    public List<List<MorphemeContainer>> parseAll(List<TurkishSequence> inputs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<List<MorphemeContainer>> parseAllStr(List<String> inputs) {
        final List<List<MorphemeContainer>> results = new ArrayList<List<MorphemeContainer>>(inputs.size());

        final Map<String, List<MorphemeContainer>> newValuesMap = new HashMap<String, List<MorphemeContainer>>();
        if (useLocalCache) {
            // a method-local cache and values to update.
            // this is done to prevent blocking the cache (I mean the one which is field, not the local variable)

            for (String input : inputs) {
                final List<MorphemeContainer> locallyCachedValues = newValuesMap.get(input);
                if (locallyCachedValues != null) {
                    results.add(locallyCachedValues);
                } else {
                    final List<MorphemeContainer> cachedResult = this.cache.get(input);
                    if (cachedResult != null) {
                        results.add(cachedResult);
                    } else {
                        List<MorphemeContainer> morphemeContainers = this.delegate.parseStr(input);
                        morphemeContainers = morphemeContainers == null ? Collections.EMPTY_LIST : morphemeContainers;
                        results.add(morphemeContainers);
                        newValuesMap.put(input, morphemeContainers);
                    }
                }
            }
        } else {
            for (String input : inputs) {
                final List<MorphemeContainer> result = this.delegate.parseStr(input);
                newValuesMap.put(input, result);
            }
        }

        cache.putAll(newValuesMap);

        return results;
    }

    @Override
    public List<MorphemeContainer> parseStr(String input) {
        final List<MorphemeContainer> cachedResult = this.cache.get(input);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            final List<MorphemeContainer> morphemeContainers = this.delegate.parseStr(input);
            cache.put(input, morphemeContainers);
            return morphemeContainers == null ? Collections.EMPTY_LIST : morphemeContainers;
        }
    }

    /**
     * Parsing a {@link TurkishSequence} is not supported since {@link MorphologicParserCache} is only using strings
     * as cache keys.
     * <p/>
     * This method could just convert the {@link TurkishSequence} to string and use it, but in this case client of this method
     * wouldn't notice that he/she is doing an expensive operation (creating {@link TurkishSequence}s) without need.
     * <p/>
     * * = in case of caching
     *
     * @deprecated Throws {@link UnsupportedOperationException}
     */
    @Override
    public List<MorphemeContainer> parse(TurkishSequence input) {
        throw new UnsupportedOperationException();
    }

}
