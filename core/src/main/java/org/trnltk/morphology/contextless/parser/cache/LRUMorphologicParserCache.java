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

import java.util.List;
import java.util.Map;

public class LRUMorphologicParserCache implements MorphologicParserCache {

    private final Cache<String, List<MorphemeContainer>> cache;

    public LRUMorphologicParserCache(int concurrencyLevel, int initialCapacity, long maximumSize) {
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(concurrencyLevel)
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .build();
    }

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
}
