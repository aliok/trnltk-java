/*
 * Copyright  2012  Ali Ok (aliokATapacheDOTorg)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trnltk.web.morphology.parser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.trnltk.morphology.contextless.parser.RootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.DictionaryRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.NumeralRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.ProperNounFromApostropheRootFinder;
import org.trnltk.morphology.contextless.parser.rootfinders.ProperNounWithoutApostropheRootFinder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@SessionScoped
public class RootFinderSelectionData implements Serializable {

    @ManagedProperty(value = "#{rootMapData}")
    private RootMapData rootMapData;

    private ImmutableMap<RootFinderOption, RootFinder> rootFindersMap;
    private ImmutableMap<RootFinderOption, RootFinder> rootFindersWithConvertedCircumflexesMap;

    private boolean convertCircumflexes = false;
    private List<RootFinderOption> selectedRootFinderOptions = Arrays.asList(RootFinderOption.DICTIONARY_ROOT_FINDER);

    public static enum RootFinderOption {
        DICTIONARY_ROOT_FINDER,
        NUMERAL_DICTIONARY_ROOT_FINDER,
        NUMERAL_REGEX_ROOT_FINDER,
        PROPER_NOUN_FROM_APOSTROPHE_ROOT_FINDER,
        PROPER_NOUN_WITHOUT_APOSTROPHE_ROOT_FINDER;

    }

    @PostConstruct
    public void initializeBean() {
        this.rootFindersMap = new ImmutableMap.Builder<RootFinderOption, RootFinder>()
                .put(RootFinderOption.DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getRootMap()))
                .put(RootFinderOption.NUMERAL_DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getNumeralRootMap()))
                .put(RootFinderOption.NUMERAL_REGEX_ROOT_FINDER, new NumeralRootFinder())
                .put(RootFinderOption.PROPER_NOUN_FROM_APOSTROPHE_ROOT_FINDER, new ProperNounFromApostropheRootFinder())
                .put(RootFinderOption.PROPER_NOUN_WITHOUT_APOSTROPHE_ROOT_FINDER, new ProperNounWithoutApostropheRootFinder())
                .build();

        this.rootFindersWithConvertedCircumflexesMap = new ImmutableMap.Builder<RootFinderOption, RootFinder>()
                .put(RootFinderOption.DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getRootMapWithConvertedCircumflexes()))
                .put(RootFinderOption.NUMERAL_DICTIONARY_ROOT_FINDER, new DictionaryRootFinder(rootMapData.getNumeralRootMapWithConvertedCircumflexes()))
                .put(RootFinderOption.NUMERAL_REGEX_ROOT_FINDER, new NumeralRootFinder())
                .put(RootFinderOption.PROPER_NOUN_FROM_APOSTROPHE_ROOT_FINDER, new ProperNounFromApostropheRootFinder())
                .put(RootFinderOption.PROPER_NOUN_WITHOUT_APOSTROPHE_ROOT_FINDER, new ProperNounWithoutApostropheRootFinder())
                .build();
    }

    public RootFinder[] getSelectedRootFinders() {
        final ArrayList<RootFinder> rootFinders = new ArrayList<RootFinder>();

        for (RootFinderOption option : selectedRootFinderOptions) {
            if (this.convertCircumflexes)
                rootFinders.add(this.rootFindersWithConvertedCircumflexesMap.get(option));
            else
                rootFinders.add(this.rootFindersMap.get(option));
        }

        return rootFinders.toArray(new RootFinder[0]);
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
