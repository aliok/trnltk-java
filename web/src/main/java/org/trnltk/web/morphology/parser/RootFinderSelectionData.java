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

package org.trnltk.web.morphology.parser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.trnltk.morphology.contextless.rootfinder.RootFinder;
import org.trnltk.morphology.contextless.rootfinder.RootFinderChain;
import org.trnltk.morphology.contextless.rootfinder.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@SessionScoped
public class RootFinderSelectionData implements Serializable {

    @ManagedProperty(value = "#{rootMapData}")
    private RootMapData rootMapData;

    private ImmutableMap<RootFinderOption, RootFinder> rootFindersMap;
    private ImmutableMap<RootFinderOption, RootFinder> rootFindersWithConvertedCircumflexesMap;

    private boolean convertCircumflexes = false;
    private List<RootFinderOption> selectedRootFinderOptions = Arrays.asList(RootFinderOption.PUNC_ROOT_FINDER, RootFinderOption.DICTIONARY_ROOT_FINDER);

    public static enum RootFinderOption {
        PUNC_ROOT_FINDER(0, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED),
        CARDINAL_NUMBER_REGEX_ROOT_FINDER(1, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED),
        PROPER_NOUN_FROM_APOSTROPHE_ROOT_FINDER(2, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED),
        PROPER_NOUN_WITHOUT_APOSTROPHE_ROOT_FINDER(3, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN),
        DICTIONARY_ROOT_FINDER(4, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN),
        NUMERAL_DICTIONARY_ROOT_FINDER(5, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN),
        BRUTE_FORCE_NOUN_ROOT_FINDER(6, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN),
        BRUTE_FORCE_NOUN_COMPOUND_ROOT_FINDER(7, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN),
        BRUTE_FORCE_VERB_ROOT_FINDER(8, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN),
        ORDINAL_NUMBER_REGEX_ROOT_FINDER(9, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED),
        RANGE_NUMBER_REGEX_ROOT_FINDER(10, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED);

        private final int index;
        private final RootFinderChain.RootFinderPolicy policy;

        RootFinderOption(int index, RootFinderChain.RootFinderPolicy policy) {
            this.index = index;
            this.policy = policy;
        }

        public int getIndex() {
            return index;
        }

        public RootFinderChain.RootFinderPolicy getPolicy() {
            return policy;
        }
    }

    @PostConstruct
    public void initializeBean() {
        this.rootFindersMap = new ImmutableMap.Builder<RootFinderOption, RootFinder>()
                .put(RootFinderOption.DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getRootMap()))
                .put(RootFinderOption.NUMERAL_DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getNumeralRootMap()))

                .put(RootFinderOption.PUNC_ROOT_FINDER, new PuncRootFinder())
                .put(RootFinderOption.RANGE_NUMBER_REGEX_ROOT_FINDER, new RangeDigitsRootFinder())
                .put(RootFinderOption.ORDINAL_NUMBER_REGEX_ROOT_FINDER, new OrdinalDigitsRootFinder())
                .put(RootFinderOption.CARDINAL_NUMBER_REGEX_ROOT_FINDER, new CardinalDigitsRootFinder())
                .put(RootFinderOption.PROPER_NOUN_FROM_APOSTROPHE_ROOT_FINDER, new ProperNounFromApostropheRootFinder())
                .put(RootFinderOption.PROPER_NOUN_WITHOUT_APOSTROPHE_ROOT_FINDER, new ProperNounWithoutApostropheRootFinder())
                .put(RootFinderOption.BRUTE_FORCE_NOUN_ROOT_FINDER, new BruteForceNounRootFinder())
                .put(RootFinderOption.BRUTE_FORCE_NOUN_COMPOUND_ROOT_FINDER, new BruteForceCompoundNounRootFinder())
                .put(RootFinderOption.BRUTE_FORCE_VERB_ROOT_FINDER, new BruteForceVerbRootFinder())
                .build();

        this.rootFindersWithConvertedCircumflexesMap = new ImmutableMap.Builder<RootFinderOption, RootFinder>()
                .put(RootFinderOption.DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getRootMapWithConvertedCircumflexes()))
                .put(RootFinderOption.NUMERAL_DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getNumeralRootMapWithConvertedCircumflexes()))

                .put(RootFinderOption.PUNC_ROOT_FINDER, new PuncRootFinder())
                .put(RootFinderOption.RANGE_NUMBER_REGEX_ROOT_FINDER, new RangeDigitsRootFinder())
                .put(RootFinderOption.ORDINAL_NUMBER_REGEX_ROOT_FINDER, new OrdinalDigitsRootFinder())
                .put(RootFinderOption.CARDINAL_NUMBER_REGEX_ROOT_FINDER, new CardinalDigitsRootFinder())
                .put(RootFinderOption.PROPER_NOUN_FROM_APOSTROPHE_ROOT_FINDER, new ProperNounFromApostropheRootFinder())
                .put(RootFinderOption.PROPER_NOUN_WITHOUT_APOSTROPHE_ROOT_FINDER, new ProperNounWithoutApostropheRootFinder())
                .put(RootFinderOption.BRUTE_FORCE_NOUN_ROOT_FINDER, new BruteForceNounRootFinder())
                .put(RootFinderOption.BRUTE_FORCE_NOUN_COMPOUND_ROOT_FINDER, new BruteForceCompoundNounRootFinder())
                .put(RootFinderOption.BRUTE_FORCE_VERB_ROOT_FINDER, new BruteForceVerbRootFinder())
                .build();
    }

    public RootFinderChain getRootFinderChain() {
        final ArrayList<RootFinderOption> rootFinderOptionsToUse = new ArrayList<RootFinderOption>(selectedRootFinderOptions);
        Collections.sort(rootFinderOptionsToUse, new Comparator<RootFinderOption>() {
            @Override
            public int compare(RootFinderOption o1, RootFinderOption o2) {
                return Integer.valueOf(o1.getIndex()).compareTo(Integer.valueOf(o2.getIndex()));
            }
        });

        final RootFinderChain rootFinderChain = new RootFinderChain();

        for (RootFinderOption option : rootFinderOptionsToUse) {
            if (this.convertCircumflexes)
                rootFinderChain.offer(this.rootFindersWithConvertedCircumflexesMap.get(option), option.getPolicy());
            else
                rootFinderChain.offer(this.rootFindersMap.get(option), option.getPolicy());
        }

        return rootFinderChain;
    }

    public List<RootFinderOption> getAllRootFinderOptions() {
        return Lists.newArrayList(RootFinderOption.values());
    }

    public List<RootFinderOption> getSelectedRootFinderOptions() {
        return selectedRootFinderOptions;
    }

    public void setSelectedRootFinderOptions(List<RootFinderOption> selectedRootFinderOptions) {
        this.selectedRootFinderOptions = selectedRootFinderOptions;
    }

    public boolean isConvertCircumflexes() {
        return convertCircumflexes;
    }

    public void setConvertCircumflexes(boolean convertCircumflexes) {
        this.convertCircumflexes = convertCircumflexes;
    }

    public void setRootMapData(RootMapData rootMapData) {
        this.rootMapData = rootMapData;
    }
}
