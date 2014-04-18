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

public class MissingTokenizationRuleException extends RuntimeException {
    private final TextBlockGroup leftTextBlockGroup;
    private final TextBlockGroup rightTextBlockGroup;
    private final TextBlockGroup contextBlockGroup;

    public MissingTokenizationRuleException(TextBlockGroup leftTextBlockGroup, TextBlockGroup rightTextBlockGroup, String msg, TextBlockGroup contextBlockGroup) {
        super(msg);
        this.leftTextBlockGroup = leftTextBlockGroup;
        this.rightTextBlockGroup = rightTextBlockGroup;
        this.contextBlockGroup = contextBlockGroup;
    }

    public TextBlockGroup getLeftTextBlockGroup() {
        return leftTextBlockGroup;
    }

    public TextBlockGroup getRightTextBlockGroup() {
        return rightTextBlockGroup;
    }

    public TextBlockGroup getContextBlockGroup() {
        return contextBlockGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MissingTokenizationRuleException that = (MissingTokenizationRuleException) o;

        if (!contextBlockGroup.equals(that.contextBlockGroup)) return false;
        else if (!leftTextBlockGroup.equals(that.leftTextBlockGroup)) return false;
        else if (!rightTextBlockGroup.equals(that.rightTextBlockGroup)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftTextBlockGroup.hashCode();
        result = 31 * result + rightTextBlockGroup.hashCode();
        result = 31 * result + contextBlockGroup.hashCode();
        return result;
    }
}
