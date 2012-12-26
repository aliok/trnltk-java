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

import org.trnltk.morphology.morphotactics.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "suffixGraphSelectionData")
@SessionScoped
public class SuffixGraphSelectionData implements Serializable {
    private boolean includeNumeralGraph;
    private boolean includeProperNounGraph;
    private boolean includeCopulaGraph;


    public SuffixGraph getSelectedSuffixGraph() {
        SuffixGraph suffixGraph = new BasicSuffixGraph();
        if (includeNumeralGraph)
            suffixGraph = new NumeralSuffixGraph(suffixGraph);
        if (includeProperNounGraph)
            suffixGraph = new ProperNounSuffixGraph(suffixGraph);
        if (includeCopulaGraph)
            suffixGraph = new CopulaSuffixGraph(suffixGraph);

        suffixGraph.initialize();

        return suffixGraph;
    }

    public boolean isIncludeNumeralGraph() {
        return includeNumeralGraph;
    }

    public void setIncludeNumeralGraph(boolean includeNumeralGraph) {
        this.includeNumeralGraph = includeNumeralGraph;
    }

    public boolean isIncludeProperNounGraph() {
        return includeProperNounGraph;
    }

    public void setIncludeProperNounGraph(boolean includeProperNounGraph) {
        this.includeProperNounGraph = includeProperNounGraph;
    }

    public boolean isIncludeCopulaGraph() {
        return includeCopulaGraph;
    }

    public void setIncludeCopulaGraph(boolean includeCopulaGraph) {
        this.includeCopulaGraph = includeCopulaGraph;
    }
}
