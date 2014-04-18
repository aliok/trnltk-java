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

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Ali Ok
 */
public class TextBlockTypeGroup {
    private final ImmutableList<TextBlockType> textBlockTypes;

    public TextBlockTypeGroup(List<TextBlockType> textBlockTypes) {
        this.textBlockTypes = ImmutableList.copyOf(textBlockTypes);
    }

    public ImmutableList<TextBlockType> getTextBlockTypes() {
        return textBlockTypes;
    }

    @Override
    public String toString() {
        return "TextBlockTypeGroup{" +
                "textBlockTypes=" + textBlockTypes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextBlockTypeGroup that = (TextBlockTypeGroup) o;

        return textBlockTypes.equals(that.textBlockTypes);
    }

    @Override
    public int hashCode() {
        return textBlockTypes.hashCode();
    }
}
