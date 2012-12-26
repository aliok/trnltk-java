package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.contextless.parser.RootFinder;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.ArrayList;
import java.util.Collection;

public class DictionaryRootFinder implements RootFinder {
    private final Multimap<String, ? extends Root> rootMap;

    public DictionaryRootFinder(Multimap<String, ? extends Root> rootMap) {
        Validate.notNull(rootMap);
        this.rootMap = rootMap;
    }

    @Override
    public Collection<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input) {
        final Collection<? extends Root> roots = this.rootMap.get(partialInput.getUnderlyingString());
        if (CollectionUtils.isEmpty(roots))
            return new ArrayList<Root>();
        else
            return roots;
    }
}
