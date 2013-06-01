package org.trnltk.tokenizer;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Ali Ok
 */
class TextBlockGroup {
    private final ImmutableList<TextBlock> textBlocks;
    private final TextBlockTypeGroup textBlockTypeGroup;
    private final TextBlock firstTextBlock;

    public TextBlockGroup(List<TextBlock> textBlocks) {
        this.textBlocks = ImmutableList.copyOf(textBlocks);
        this.textBlockTypeGroup = new TextBlockTypeGroup(Lists.transform(textBlocks, new Function<TextBlock, TextBlockType>() {
            @Override
            public TextBlockType apply(TextBlock input) {
                return input.getTextBlockType();
            }
        }));
        this.firstTextBlock = textBlocks.get(0);
    }

    public ImmutableList<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public TextBlockTypeGroup getTextBlockTypeGroup() {
        return textBlockTypeGroup;
    }

    public TextBlock getFirstTextBlock() {
        return firstTextBlock;
    }

    public String getText() {
        return Joiner.on("").join(Lists.transform(this.textBlocks, new Function<TextBlock, String>() {
            @Override
            public String apply(TextBlock input) {
                return input.getText();
            }
        }));
    }

    @Override
    public String toString() {
        return "TextBlockGroup{" +
                "textBlocks=" + textBlocks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextBlockGroup that = (TextBlockGroup) o;

        if (!textBlocks.equals(that.textBlocks)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return textBlocks.hashCode();
    }
}
