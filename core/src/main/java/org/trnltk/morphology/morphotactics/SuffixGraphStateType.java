package org.trnltk.morphology.morphotactics;

public enum SuffixGraphStateType {
    DERIVATIONAL("DERIVATIONAL"),
    TRANSFER("TRANSFER"),
    TERMINAL("TERMINAL");

    private final String str;

    SuffixGraphStateType(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "SuffixGraphStateType{" +
                "str='" + str + '\'' +
                '}';
    }
}
