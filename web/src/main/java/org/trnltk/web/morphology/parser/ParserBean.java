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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.apache.log4j.Logger;
import org.trnltk.morphology.contextless.parser.suffixbased.ContextlessMorphologicParser;
import org.trnltk.morphology.contextless.parser.suffixbased.PredefinedPaths;
import org.trnltk.morphology.contextless.parser.rootfinders.RootFinderChain;
import org.trnltk.morphology.contextless.parser.suffixbased.SuffixApplier;
import org.trnltk.morphology.model.suffixbased.Formatter;
import org.trnltk.morphology.model.suffixbased.MorphemeContainer;
import org.trnltk.morphology.model.TurkishSequence;
import org.trnltk.morphology.morphotactics.SuffixFormSequenceApplier;
import org.trnltk.morphology.morphotactics.SuffixGraph;
import org.trnltk.morphology.phonetics.PhoneticsEngine;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "parserBean")
@SessionScoped
public class ParserBean implements Serializable {
    private static Logger logger = Logger.getLogger(ParserBean.class);

    private String surface;
    private List<String> parseResults;

    @ManagedProperty(value = "#{suffixGraphSelectionData}")
    private SuffixGraphSelectionData suffixGraphData;

    @ManagedProperty(value = "#{rootMapData}")
    private RootMapData rootMapData;

    @ManagedProperty(value = "#{rootFinderSelectionData}")
    private RootFinderSelectionData rootFinderSelectionData;

    static final Ordering<String> byLengthOrdering = new Ordering<String>() {
        public int compare(String left, String right) {
            return Ints.compare(left.length(), right.length());
        }
    };

    static final Ordering<String> parseResultOrdering = Ordering.compound(Arrays.asList(byLengthOrdering, Ordering.<String>natural()));

    public void parse() {
        try {
            final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
            final PhoneticsEngine phoneticsEngine = new PhoneticsEngine(suffixFormSequenceApplier);
            final SuffixApplier suffixApplier = new SuffixApplier(phoneticsEngine);

            final SuffixGraph suffixGraph = this.suffixGraphData.getSelectedSuffixGraph();

            final PredefinedPaths predefinedPaths = new PredefinedPaths(suffixGraph, rootMapData.getRootMap(), suffixApplier);
            predefinedPaths.initialize();

            final RootFinderChain rootFinderChain = this.rootFinderSelectionData.getRootFinderChain();

            final ContextlessMorphologicParser morphologicParser = new ContextlessMorphologicParser(suffixGraph, predefinedPaths, rootFinderChain, suffixApplier);

            //TODO: add formatting option!
            this.parseResults = Lists.transform(morphologicParser.parse(new TurkishSequence(this.surface)), new Function<MorphemeContainer, String>() {
                @Override
                public String apply(MorphemeContainer input) {
                    return Formatter.formatMorphemeContainerWithForms(input);
                }
            });

            Collections.sort(new ArrayList<String>(this.parseResults), parseResultOrdering);

        } catch (RuntimeException e) {
            logger.error(e, e);

        }
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public List<String> getParseResults() {
        return parseResults;
    }

    public void setSuffixGraphData(SuffixGraphSelectionData suffixGraphData) {
        this.suffixGraphData = suffixGraphData;
    }

    public void setRootMapData(RootMapData rootMapData) {
        this.rootMapData = rootMapData;
    }

    public void setRootFinderSelectionData(RootFinderSelectionData rootFinderSelectionData) {
        this.rootFinderSelectionData = rootFinderSelectionData;
    }
}
