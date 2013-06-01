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

package org.trnltk.morphology.ambiguity.model;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

public class RootDifference {
    private Pair<String, String> rootStrDiff;
    private Pair<String, String> lemmaRootStrDiff;
    private Pair<String, String> posDiff;
    private Pair<String, String> sposDiff;

    public RootDifference(Pair<String, String> rootStrDiff, Pair<String, String> lemmaRootStrDiff, Pair<String, String> posDiff, Pair<String, String> sposDiff) {
        this.rootStrDiff = rootStrDiff;
        this.lemmaRootStrDiff = lemmaRootStrDiff;
        this.posDiff = posDiff;
        this.sposDiff = sposDiff;
    }

    public boolean differs() {
        boolean differs = false;
        if (!equalStringPair(rootStrDiff))
            differs = true;
        else
            rootStrDiff = null;

        if (!equalStringPair(lemmaRootStrDiff))
            differs = true;
        else
            lemmaRootStrDiff = null;

        if (!equalStringPair(posDiff))
            differs = true;
        else
            posDiff = null;

        if (!equalStringPair(sposDiff))
            differs = true;
        else
            sposDiff = null;

        return differs;
    }

    public boolean equalStringPair(Pair<String, String> pair) {
        return ObjectUtils.equals(pair.getLeft(), pair.getRight());
    }

    public Pair<String, String> getRootStrDiff() {
        return rootStrDiff;
    }

    public Pair<String, String> getLemmaRootStrDiff() {
        return lemmaRootStrDiff;
    }

    public Pair<String, String> getPosDiff() {
        return posDiff;
    }

    public Pair<String, String> getSposDiff() {
        return sposDiff;
    }

    @Override
    public String toString() {
        return "RootDifference{" +
                "rootStrDiff=" + rootStrDiff +
                ", lemmaRootStrDiff=" + lemmaRootStrDiff +
                ", posDiff=" + posDiff +
                ", sposDiff=" + sposDiff +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RootDifference that = (RootDifference) o;

        if (lemmaRootStrDiff != null ? !lemmaRootStrDiff.equals(that.lemmaRootStrDiff) : that.lemmaRootStrDiff != null)
            return false;
        if (posDiff != null ? !posDiff.equals(that.posDiff) : that.posDiff != null) return false;
        if (rootStrDiff != null ? !rootStrDiff.equals(that.rootStrDiff) : that.rootStrDiff != null) return false;
        if (sposDiff != null ? !sposDiff.equals(that.sposDiff) : that.sposDiff != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootStrDiff != null ? rootStrDiff.hashCode() : 0;
        result = 31 * result + (lemmaRootStrDiff != null ? lemmaRootStrDiff.hashCode() : 0);
        result = 31 * result + (posDiff != null ? posDiff.hashCode() : 0);
        result = 31 * result + (sposDiff != null ? sposDiff.hashCode() : 0);
        return result;
    }
}
