package org.trnltk.morphology.ambiguity.model;

import java.util.List;

public class ParseResult {
    private final String str;
    private final String root;
    private final String lemmaRoot;
    private final String rootPos;
    private final String rootSpos;
    private final List<ParseResultPart> parts;

    public ParseResult(String str, String root, String lemmaRoot, String rootPos, String rootSpos, List<ParseResultPart> parts) {
        this.str = str;
        this.root = root;
        this.lemmaRoot = lemmaRoot;
        this.rootPos = rootPos;
        this.rootSpos = rootSpos;
        this.parts = parts;
    }

    public List<ParseResultPart> getParts() {
        return parts;
    }

    public String getRoot() {
        return root;
    }

    public String getLemmaRoot() {
        return lemmaRoot;
    }

    public String getRootPos() {
        return rootPos;
    }

    public String getRootSpos() {
        return rootSpos;
    }

    public String getStr() {
        return str;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "root='" + root + '\'' +
                ", lemmaRoot='" + lemmaRoot + '\'' +
                ", rootPos='" + rootPos + '\'' +
                ", rootSpos='" + rootSpos + '\'' +
                ", parts=" + parts +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResult that = (ParseResult) o;

        if (!str.equals(that.str)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return str.hashCode();
    }
}
