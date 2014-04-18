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

package org.trnltk.model.suffix;

import org.trnltk.common.specification.Specification;
import org.trnltk.model.morpheme.MorphemeContainer;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Suffix that is used to transition in a {@link org.trnltk.morphology.morphotactics.SuffixGraph}.
 * <p/>
 * A {@code Suffix} can have multiple {@link SuffixForm}s. For example, <i>Causative</i> suffix has the following forms:
 * <i>t</i>, <i>It</i>, <i>dIr</i> ..
 */
public class Suffix {
    private final String name;
    private final Set<SuffixForm> suffixForms = new LinkedHashSet<SuffixForm>();    //use LinkedHashSet to keep insertion order
    private final SuffixGroup suffixGroup;
    private final String prettyName;
    private final boolean allowRepetition;


    /**
     * Creates a new <code>Suffix</code>.
     *
     * @param name            Unique name of the Suffix
     * @param suffixGroup     Group of the suffix
     * @param prettyName      Pretty name of the suffix which is used e.g. formatting the MorphemeContainer.
     *                        It doesn't have to be unique
     * @param allowRepetition Pass <code>true</code> if suffix can repeat in a row. For example,
     *                        <code>Causative</code> suffix can repeat: <i>yaptırmak</i>, <i>yaptırtmak</i>, <i>yaptırttırmak</i> ...
     */
    public Suffix(String name, SuffixGroup suffixGroup, String prettyName, boolean allowRepetition) {
        this.name = name;
        this.suffixGroup = suffixGroup;
        this.prettyName = prettyName;
        this.allowRepetition = allowRepetition;
    }


    /**
     * Creates a new {@link SuffixForm} and adds it as a form to current <code>Suffix</code>.
     *
     * @param suffixFormStr String form of {@link SuffixForm}. e.g. <code>"+Iyor"</code>
     * @see Suffix#addSuffixForm(String, org.trnltk.common.specification.Specification, org.trnltk.common.specification.Specification, org.trnltk.common.specification.Specification)
     */
    public void addSuffixForm(String suffixFormStr) {
        this.addSuffixForm(suffixFormStr, null);
    }


    /**
     * Creates a new {@link SuffixForm} and adds it as a form to current <code>Suffix</code>.
     *
     * @param suffixFormStr String form of {@link SuffixForm}. e.g. <code>"+Iyor"</code>
     * @param precondition  Condition to permit transition of current <code>Suffix</code> with the {@link SuffixForm} that is being added
     * @see Suffix#addSuffixForm(String, org.trnltk.common.specification.Specification, org.trnltk.common.specification.Specification, org.trnltk.common.specification.Specification)
     */
    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition) {
        this.addSuffixForm(suffixFormStr, precondition, null);
    }


    /**
     * Creates a new {@link SuffixForm} and adds it as a form to current <code>Suffix</code>.
     *
     * @param suffixFormStr String form of {@link SuffixForm}. e.g. <code>"+Iyor"</code>
     * @param precondition  Condition to permit transition of current <code>Suffix</code> with the {@link SuffixForm} that is being added
     * @param postCondition Condition to permit transitions after current <code>Suffix</code> with the {@link SuffixForm} that is being added
     * @see Suffix#addSuffixForm(String, org.trnltk.common.specification.Specification, org.trnltk.common.specification.Specification, org.trnltk.common.specification.Specification)
     */
    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition, Specification<MorphemeContainer> postCondition) {
        this.addSuffixForm(suffixFormStr, precondition, postCondition, null);
    }

    /**
     * Creates a new {@link SuffixForm} and adds it as a form to current <code>Suffix</code>.
     *
     * @param suffixFormStr           String form of {@link SuffixForm}. e.g. <code>"+Iyor"</code>
     * @param precondition            Condition to permit transition of current <code>Suffix</code> with the {@link SuffixForm} that is being added
     * @param postCondition           Condition to permit transitions after current <code>Suffix</code> with the {@link SuffixForm} that is being added
     * @param postDerivativeCondition Condition to permit derivational transitions after current <code>Suffix</code> with the {@link SuffixForm} that is being added
     */
    public void addSuffixForm(String suffixFormStr, Specification<MorphemeContainer> precondition,
                              Specification<MorphemeContainer> postCondition, Specification<MorphemeContainer> postDerivativeCondition) {
        final SuffixForm suffixForm = new SuffixForm(this, suffixFormStr, precondition, postCondition, postDerivativeCondition);
        this.suffixForms.add(suffixForm);
    }

    /**
     * Finds the {@link SuffixForm} of the <code>Suffix</code> with given string representation of the {@link SuffixForm} and returns it.
     *
     * @param suffixFormStr String representation of the {@link SuffixForm} that is searched
     * @return null if not found
     */
    @SuppressWarnings("UnusedDeclaration")
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

    /**
     * @return The unique name of the <code>Suffix</code>. Some examples: <code>A3Sg_Noun</code>, <code>Causative</code>
     * @see Suffix#Suffix(String, SuffixGroup, String, boolean)
     */
    public String getName() {
        return name;
    }

    /**
     * @return Added {@link SuffixForm}s of <code>Suffix</code>
     * @see Suffix#Suffix(String, SuffixGroup, String, boolean)
     */
    public Set<SuffixForm> getSuffixForms() {
        return suffixForms;
    }

    /**
     * @return {@link SuffixGroup} that <code>Suffix</code> belongs, or null if it doesn't belong to any
     * @see Suffix#Suffix(String, SuffixGroup, String, boolean)
     */
    public SuffixGroup getSuffixGroup() {
        return suffixGroup;
    }

    /**
     * @return Pretty name of the suffix for formatting purposes. Some examples: for <code>A3Sg_Noun</code> ->
     *         <code>A3Sg</code>, for <code>Causative</code> -> <code>Causative</code>
     * @see Suffix#Suffix(String, SuffixGroup, String, boolean)
     */
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * @return true if <code>Suffix</code> is allowed to be repeated in a row, false otherwise
     * @see Suffix#Suffix(String, SuffixGroup, String, boolean)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAllowRepetition() {
        return allowRepetition;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suffix suffix = (Suffix) o;

        return name.equals(suffix.name);
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
