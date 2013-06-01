package org.trnltk.tokenizer.experiment;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Ali Ok
 */
class TextBlockTypeGroup {
    private final ImmutableList<TextBlockType> textBlockTypes;

    public TextBlockTypeGroup(List<TextBlockType> textBlockTypes) {
        this.textBlockTypes = ImmutableList.copyOf(textBlockTypes);
    }

    public ImmutableList<TextBlockType> getTextBlockTypes() {
        return textBlockTypes;
    }

    public int getSize() {
        return this.textBlockTypes.size();
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

        if (!textBlockTypes.equals(that.textBlockTypes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return textBlockTypes.hashCode();
    }
}
