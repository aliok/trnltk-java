package org.trnltk.morphology.contextless.parser.suffixbased;

import org.trnltk.morphology.contextless.parser.rootfinders.RootFinderChain;
import org.trnltk.morphology.contextless.parser.suffixbased.cache.MorphologicParserCache;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.morphotactics.SuffixGraph;

import java.util.*;

/**
 * Uses a caching with compute-if-absent logic. Different cache algorithms could be injected (One level, Two level, LRU, Time-based, etc.)
 */
public class CachingMorphologicParser implements MorphologicParser {

    private final MorphologicParser delegate;
    private final MorphologicParserCache cache;
    private final boolean useLocalCache;

    public CachingMorphologicParser(MorphologicParserCache cache, MorphologicParser delegate, boolean useLocalCache) {
        this.cache = cache;
        this.delegate = delegate;
        this.useLocalCache = useLocalCache;
    }

    /**
     * Uses a new {@link ContextlessMorphologicParser} as a delegate.
     *
     * @param cache           The cache
     * @param suffixGraph     suffixGraph
     * @param predefinedPaths predefinedPaths
     * @param rootFinderChain rootFinderChain
     * @param suffixApplier   suffixApplier
     */
    public CachingMorphologicParser(final MorphologicParserCache cache, final SuffixGraph suffixGraph, final boolean useLocalCache, final PredefinedPaths predefinedPaths, final RootFinderChain rootFinderChain, final SuffixApplier suffixApplier) {
        this.delegate = new ContextlessMorphologicParser(suffixGraph, predefinedPaths, rootFinderChain, suffixApplier);
        this.cache = cache;
        this.useLocalCache = useLocalCache;
    }

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

    @Override
    public List<MorphemeContainer> parse(TurkishSequence input) {
        throw new UnsupportedOperationException();
    }

}
