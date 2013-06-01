package org.trnltk.tokenizer;

/**
 * @author Ali Ok
 */
class TextBlock {
    private String text;
    private TextBlockType textBlockType;

    public TextBlock(String text, TextBlockType textBlockType) {
        this.text = text;
        this.textBlockType = textBlockType;
    }

    public String getText() {
        return text;
    }

    public TextBlockType getTextBlockType() {
        return textBlockType;
    }

    @Override
    public String toString() {
        return "TextBlock{" +
                "text='" + text + '\'' +
                ", textBlockType=" + textBlockType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextBlock textBlock = (TextBlock) o;

        if (!text.equals(textBlock.text)) return false;
        if (textBlockType != textBlock.textBlockType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + textBlockType.hashCode();
        return result;
    }
}
