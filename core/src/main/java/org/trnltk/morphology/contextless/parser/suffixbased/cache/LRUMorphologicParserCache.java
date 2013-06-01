package org.trnltk.morphology.contextless.parser.suffixbased.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.model.TurkishSequence;

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
