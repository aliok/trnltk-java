package org.trnltk.apps.criticalsurface;

import java.io.Serializable;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class SentenceIdentifier implements Comparable<SentenceIdentifier>, Serializable {
    private final String fileId;
    private final int line;

    public SentenceIdentifier(String fileId, int line) {
        this.fileId = fileId;
        this.line = line;
    }

    @Override
    public String toString() {
        return fileId + "#" + line;
    }

    public static SentenceIdentifier fromString(String input) {
        final int endIndexOfFileId = input.indexOf('#');
        final SentenceIdentifier sentenceIdentifier = new SentenceIdentifier(
                input.substring(0, endIndexOfFileId),
                Integer.parseInt(input.substring(endIndexOfFileId + 1)
                ));
        return sentenceIdentifier;
    }

    @Override
    public int compareTo(SentenceIdentifier other) {
        final int fileIdCompareResult = this.fileId.compareTo(other.fileId);
        if (fileIdCompareResult == 0)
            return Integer.compare(this.line, other.line);
        else
            return fileIdCompareResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SentenceIdentifier)) return false;

        SentenceIdentifier that = (SentenceIdentifier) o;

        if (line != that.line) return false;
        if (!fileId.equals(that.fileId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileId.hashCode();
        result = 31 * result + line;
        return result;
    }

    public String getFileId() {
        return fileId;
    }

    public int getLine() {
        return line;
    }
}
