package org.trnltk.morphology.contextless.parser.suffixbased.cache;

import org.trnltk.morphology.model.suffixbased.MorphemeContainer;

import java.util.List;
import java.util.Map;

public interface MorphologicParserCache {
    List<MorphemeContainer> get(String input);

    void put(String input, List<MorphemeContainer> morphemeContainers);

    void putAll(Map<String, List<MorphemeContainer>> map);
}
