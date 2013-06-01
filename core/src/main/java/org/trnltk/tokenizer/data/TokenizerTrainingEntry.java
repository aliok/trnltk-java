package org.trnltk.tokenizer.data;

public class TokenizerTrainingEntry {
    private String text;
    private String tknz;

    public TokenizerTrainingEntry(String text, String tknz) {
        this.text = text;
        this.tknz = tknz;
    }

    public TokenizerTrainingEntry(String text) {
        this(text, null);
    }

    public TokenizerTrainingEntry() {
        this(null);
    }

    public String getText() {
        return text;
    }

    public void setText(String T) {
        this.text = T;
    }

    public String getTknz() {
        return tknz;
    }

    public void setTknz(String tknz) {
        this.tknz = tknz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenizerTrainingEntry wheel = (TokenizerTrainingEntry) o;

        if (!text.equals(wheel.text)) return false;
        if (!tknz.equals(wheel.tknz)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + tknz.hashCode();
        return result;
    }
}
