package org.trnltk.morphology.contextless.parser.cache;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MorphologicParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An offline cache to use in morphologic parsing.
 * <p/>
 * This is the simplest form of a cache where the words whose results are to be cached given in advance.
 * Even if it is the simplest, it improves the performance very good.
 * <p/>
 * If you have the text to parse already, you can go through the words and find most used ones and cache
 * their parse results. That way, system does not have to parse same words again and again.
 * <p/>
 * If you don't have the text already (online case), you can still use this cache. In that case, you can put the most
 * frequent 20000 words in Turkish into the cache. This list of words is bundled.
 */
public class SimpleOfflineCache implements MorphologicParserCache {
    private Map<String, List<MorphemeContainer>> map;
    private Collection<String> cacheKeys;
    private boolean built;

    /**
     * Builds a cache with values from bundled list of most frequent 20K words in Turkish.
     */
    public static SimpleOfflineCache forTop20kWords() {
        final URL resource = Resources.getResource("top20kwords.txt");
        return fromFile(resource);
    }

    /**
     * Builds a cache with values from bundled list of most frequent 2000 words in Turkish.
     */
    public static SimpleOfflineCache forTop2kWords() {
        final URL resource = Resources.getResource("top2kwords.txt");
        return fromFile(resource);
    }

    private static SimpleOfflineCache fromFile(URL resource) {
        final InputSupplier<InputStreamReader> supplier = Resources.newReaderSupplier(resource, Charset.forName("utf-8"));
        final List<String> lines;
        try {
            lines = CharStreams.readLines(supplier);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find bundled most frequent word list!", e);
        }
        // read eagerly
        final List<String> cacheKeys = Lists.newArrayList(lines);
        return new SimpleOfflineCache(cacheKeys);
    }

    /**
     * Builds a cache with values of given words and their parse results.
     */
    public SimpleOfflineCache(Collection<String> words) {
        Validate.notEmpty(words, "Cache keys cannot be null or empty.");
        this.cacheKeys = words;
    }

    @Override
    public void build(MorphologicParser parser) {
        this.map = new HashMap<String, List<MorphemeContainer>>(this.cacheKeys.size());

        for (String cacheKey : cacheKeys) {
            this.map.put(cacheKey, parser.parseStr(cacheKey));
        }

        //remove reference as we don't need it anymore. help GC
        this.cacheKeys = null;

        this.built = true;
    }

    @Override
    public boolean isBuilt() {
        return this.built;
    }

    @Override
    public List<MorphemeContainer> get(String input) {
        return this.map.get(input);
    }

    @Override
    public void put(String input, List<MorphemeContainer> morphemeContainers) {
        //do nothing as what to store is given in advance
    }

    @Override
    public void putAll(Map<String, List<MorphemeContainer>> map) {
        //do nothing as what to store is given in advance
    }
}
