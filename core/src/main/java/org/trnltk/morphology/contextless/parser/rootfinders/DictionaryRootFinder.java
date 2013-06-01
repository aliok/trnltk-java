package org.trnltk.morphology.contextless.parser.rootfinders;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.morphology.model.Root;
import org.trnltk.morphology.model.TurkishSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DictionaryRootFinder implements RootFinder {
    private final Multimap<String, ? extends Root> rootMap;

    public DictionaryRootFinder(Multimap<String, ? extends Root> rootMap) {
        Validate.notNull(rootMap);
        this.rootMap = rootMap;
    }

    @Override
    public boolean handles(TurkishSequence partialInput, TurkishSequence input) {
        return partialInput != null && !partialInput.isBlank();
    }

    @Override
    public Collection<? extends Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence _input) {
        final Collection<? extends Root> roots = this.rootMap.get(partialInput.getUnderlyingString());
        if (Character.isUpperCase(partialInput.charAt(0).getCharValue())) {
            final ArrayList<Root> result = new ArrayList<Root>();

            final Collection<? extends Root> lowerCaseRoots = this.rootMap.get(Character.toLowerCase(partialInput.getUnderlyingString().charAt(0)) + partialInput.getUnderlyingString().substring(1));
            result.addAll(roots);
            result.addAll(lowerCaseRoots);

            if (CollectionUtils.isEmpty(result))
                return Collections.EMPTY_LIST;
            else
                return result;

        } else {
            if (CollectionUtils.isEmpty(roots))
                return Collections.EMPTY_LIST;
            else
                return roots;
        }

    }
}
