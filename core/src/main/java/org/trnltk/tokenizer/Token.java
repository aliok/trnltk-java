package org.trnltk.tokenizer;

import java.util.List;

public class Token {
    private final String surface;
    private final List<TextBlockType> textBlockTypes;

    public Token(String surface, List<TextBlockType> textBlockTypes) {
        this.surface = surface;
        this.textBlockTypes = textBlockTypes;
    }

    public String getSurface() {
        return surface;
    }

    public List<TextBlockType> getTextBlockTypes() {
        return textBlockTypes;
    }
}
