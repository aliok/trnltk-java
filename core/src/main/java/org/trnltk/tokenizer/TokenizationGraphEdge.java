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

package org.trnltk.tokenizer;

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ali Ok
 */
public class TokenizationGraphEdge {
    private final boolean inferred;
    private final TokenizationGraphNode target;
    private final boolean addSpace;
    private final List<List<TextBlock>> examples;

    public TokenizationGraphEdge(boolean inferred, TokenizationGraphNode target, boolean addSpace) {
        this.inferred = inferred;
        this.target = target;
        this.addSpace = addSpace;
        this.examples = new LinkedList<List<TextBlock>>();
    }

    public void addExample(List<TextBlock> example) {
        this.examples.add(example);
    }

    public boolean isInferred() {
        return inferred;
    }

    public TokenizationGraphNode getTarget() {
        return target;
    }

    public boolean isAddSpace() {
        return addSpace;
    }

    public List<List<TextBlock>> getExamples() {
        return examples;
    }

    @Override
    public String toString() {
        return "TokenizationGraphEdge{" +
                "addSpace=" + addSpace +
                ", inferred=" + inferred +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenizationGraphEdge that = (TokenizationGraphEdge) o;

        if (addSpace != that.addSpace) return false;
        else if (!target.equals(that.target)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + (addSpace ? 1 : 0);
        return result;
    }
}
