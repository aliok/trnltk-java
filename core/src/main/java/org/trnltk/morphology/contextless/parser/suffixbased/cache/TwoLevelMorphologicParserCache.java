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

package org.trnltk.morphology.contextless.parser.suffixbased.cache;

import org.trnltk.morphology.model.suffixbased.MorphemeContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A L2 cache which also uses a L1 cache. When a value is not found in L2 cache (self), value from L1 cache is returned.
 * Likewise, when a new value is put on the cache, it will be put on the L1 cache after a number of items ({@code l2MaxSize}).
 * <p/>
 * L2 cache is cleared when {@code l2MaxSize} is reached.
 * <p/>
 * It is important to choose {@code l2MaxSize} wisely.
 * <p/>
 * Too large: L2 cache will get slower and blocking while putting the values in L1 takes too much time.
 * <p/>
 * Too small: L1 will be used unnecessarily and too much blocking while putting the values in L1.
 */
public class TwoLevelMorphologicParserCache implements MorphologicParserCache {

    private int l2MaxSize;
    private final MorphologicParserCache l1Cache;

    private int l2Size;
    private Map<String, List<MorphemeContainer>> l2Cache;

    public TwoLevelMorphologicParserCache(int l2MaxSize, MorphologicParserCache l1Cache) {
        this.l2MaxSize = l2MaxSize;
        this.l1Cache = l1Cache;
        this.l2Cache = new HashMap<String, List<MorphemeContainer>>(l2MaxSize);
        this.l2Size = 0;
    }

    @Override
    public List<MorphemeContainer> get(String input) {
        final List<MorphemeContainer> morphemeContainers = l2Cache.get(input);
        if (morphemeContainers != null) {
            return morphemeContainers;
        } else {
            return l1Cache.get(input);
        }
    }

    @Override
    public void put(String input, List<MorphemeContainer> morphemeContainers) {
        synchronized (l2Cache) {
            // l2Cache should not be changed during put operation.
            // remember : there can be multiple threads accessing this instance of TwoLevelMorphologicParserCache
            l2Cache.put(input, morphemeContainers == null ? (List<MorphemeContainer>) Collections.EMPTY_LIST : morphemeContainers);
            l2Size++;
        }
        if (l2Size >= l2MaxSize) {
            synchronized (l2Cache) {
                // l2Cache should not be changed during bulk insert to l1Cache operation
                l1Cache.putAll(l2Cache);
                l2Cache = new HashMap<String, List<MorphemeContainer>>(l2MaxSize);
                l2Size = 0;
            }
        }
    }

    @Override
    public void putAll(Map<String, List<MorphemeContainer>> map) {
        if (l2Size + map.size() >= l2MaxSize) {
            synchronized (l2Cache) {
                l1Cache.putAll(l2Cache);
                l1Cache.putAll(map);
                l2Cache = new HashMap<String, List<MorphemeContainer>>(l2MaxSize);
                l2Size = 0;
            }
        } else {
            synchronized (l2Cache) {
                l2Cache.putAll(map);
                l2Size += map.size();
            }
        }
    }

}