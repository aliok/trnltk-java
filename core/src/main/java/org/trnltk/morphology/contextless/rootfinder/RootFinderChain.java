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

package org.trnltk.morphology.contextless.rootfinder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.letter.TurkishSequence;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RootFinderChain {

    private LinkedList<RootFinderChainItem> rootFinderChainItems = new LinkedList<RootFinderChainItem>();

    public RootFinderChain offer(RootFinder rootFinder, RootFinderPolicy rootFinderPolicy) {
        if (RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED.equals(rootFinderPolicy)) {
            final RootFinderChainItem lastChainItem = rootFinderChainItems.peekLast();
            if (lastChainItem != null && lastChainItem.getRootFinderPolicy().equals(RootFinderPolicy.CONTINUE_ON_CHAIN))
                throw new IllegalStateException(String.format("Unoptimal RootFinderChain! RootFinder instance %s " +
                        "with policy STOP_CHAIN_WHEN_INPUT_IS_HANDLED was tried to be added after a RootFinder instance with policy CONTINUE_ON_CHAIN",
                        rootFinder.getClass().getName()));
        }
        this.rootFinderChainItems.offer(new RootFinderChainItem(rootFinder, rootFinderPolicy));
        return this;
    }

    public List<Root> findRootsForPartialInput(TurkishSequence partialInput, TurkishSequence input) {
        LinkedList<Root> roots = new LinkedList<Root>();
        for (RootFinderChainItem rootFinderChainItem : rootFinderChainItems) {
            final RootFinder rootFinder = rootFinderChainItem.getRootFinder();
            final RootFinderPolicy rootFinderPolicy = rootFinderChainItem.getRootFinderPolicy();

            if (!rootFinder.handles(partialInput, input))
                continue;
            final Collection<? extends Root> rootsForPartialInput = rootFinder.findRootsForPartialInput(partialInput, input);
            if (CollectionUtils.isNotEmpty(rootsForPartialInput))
                roots.addAll(rootsForPartialInput);

            if (RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED.equals(rootFinderPolicy))
                break;
            else if (RootFinderPolicy.CONTINUE_ON_CHAIN.equals(rootFinderPolicy))
                continue;
            else
                throw new IllegalStateException();
        }
        return roots;
    }

    public enum RootFinderPolicy {
        STOP_CHAIN_WHEN_INPUT_IS_HANDLED,
        CONTINUE_ON_CHAIN
    }

    private static class RootFinderChainItem {
        private final RootFinder rootFinder;
        private final RootFinderPolicy rootFinderPolicy;

        private RootFinderChainItem(RootFinder rootFinder, RootFinderPolicy rootFinderPolicy) {
            Validate.notNull(rootFinder);
            Validate.notNull(rootFinderPolicy);

            this.rootFinder = rootFinder;
            this.rootFinderPolicy = rootFinderPolicy;
        }

        public RootFinder getRootFinder() {
            return rootFinder;
        }

        public RootFinderPolicy getRootFinderPolicy() {
            return rootFinderPolicy;
        }
    }
}
