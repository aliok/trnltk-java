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

package org.trnltk.morphology.contextless.parser.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;

import java.util.List;
import java.util.Map;

/**
 * A LRU cache for a {@link org.trnltk.morphology.contextless.parser.CachingMorphologicParser}.
 * <p/>
 * Discards least recently used items first. Maximum size must be wisely selected, since a big size
 * can cause a {@link java.lang.OutOfMemoryError}.
 * <p/>
 * An entry in the cache is consisted of a key (surface string) and a list of {@link MorphemeContainer}. It is worth
 * noting a {@link MorphemeContainer} is a heavy weight object.
 */
public class LRUMorphologicParserCache implements MorphologicParserCache {

    private final Cache<String, List<MorphemeContainer>> cache;

    /**
     * @param concurrencyLevel Guides underlying cache mechanism to permit concurrency. Ideally, value should be
     *                         number of threads that access the
     *                         {@link org.trnltk.morphology.contextless.parser.CachingMorphologicParser}
     * @param initialCapacity  Initial capacity of the cache to reserve memory. Since it is expensive to allocate memory
     *                         dynamically, use this parameter and reserve some memory for the cache.
     * @param maximumSize      Maximum number of entries in the cache.
     */
    public LRUMorphologicParserCache(int concurrencyLevel, int initialCapacity, long maximumSize) {
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(concurrencyLevel)
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .build();
    }

    /**
     * Builds the cache from an existing Guava {@link Cache}
     *
     * @param cache The Guava cache
     */
    public LRUMorphologicParserCache(Cache<String, List<MorphemeContainer>> cache) {
        this.cache = cache;
    }

    @Override
    public List<MorphemeContainer> get(String input) {
        return this.cache.getIfPresent(input);
    }

    @Override
    public void put(String input, List<MorphemeContainer> morphemeContainers) {
        synchronized (this.cache) {
            this.cache.put(input, morphemeContainers);
        }
    }

    @Override
    public void putAll(Map<String, List<MorphemeContainer>> map) {
        synchronized (this.cache) {
            this.cache.putAll(map);
        }
    }

    @Override
    public void build(MorphologicParser parser) {
        // od nothing since it is an online cache
    }
}
