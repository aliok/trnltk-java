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

package org.trnltk.morphology.model.suffixbased;

import org.trnltk.common.specification.Specification;

import java.util.LinkedHashSet;
import java.util.Set;

public class Suffix {
    private final String name;
    private final Set<SuffixForm> suffixForms = new LinkedHashSet<SuffixForm>();    //used LinkedHashSet to keep insertion order 
    private final SuffixGroup suffixGroup;
    private final String prettyName;
    private final boolean allowRepetition;

    public Suffix(String name, SuffixGroup suffixGroup, String prettyName) {
        this(name, suffixGroup, prettyName, false);
    }

    public Suffix(String name, SuffixGroup suffixGroup, String prettyName, boolean allowRepetition) {
        this.name = name;
        this.suffixGroup = suffixGroup;
        this.prettyName = prettyName;
        this.allowRepetition = allowRepetition;
    }

    public void addSuffixForm(String suffixFormStr) {
        this.addSuffixForm(suffixFormStr, null);
    }

    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition) {
        this.addSuffixForm(suffixFormStr, precondition, null);
    }

    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition, Specification<MorphemeContainer> postcondition) {
        this.addSuffixForm(suffixFormStr, precondition, postcondition, null);
    }

    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition,
                              Specification<MorphemeContainer> postCondition, Specification<MorphemeContainer> postDerivativeCondition) {
        final SuffixForm suffixForm = new SuffixForm(this, suffixFormStr, precondition, postCondition, postDerivativeCondition);
        this.suffixForms.add(suffixForm);
    }

    public SuffixForm getSuffixForm(String suffixFormStr) {
        SuffixForm result = null;

        for (SuffixForm suffixForm : suffixForms) {
            if (suffixFormStr.equals(suffixForm.getForm().getSuffixFormStr())) {
                if (result != null)
                    throw new IllegalStateException("Multiple suffix forms found for suffix " + this + " and form " + suffixFormStr);
                else
                    result = suffixForm;
            }
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public Set<SuffixForm> getSuffixForms() {
        return suffixForms;
    }

    public SuffixGroup getSuffixGroup() {
        return suffixGroup;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public boolean isAllowRepetition() {
        return allowRepetition;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suffix suffix = (Suffix) o;

        if (!name.equals(suffix.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Suffix{" +
                "name='" + name + '\'' +
                ", prettyName='" + prettyName + '\'' +
                '}';
    }
}
