package org.trnltk.apps.criticalsurface;

import org.trnltk.common.util.Comparators;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class CriticalSurfaceEntry implements Serializable {
    private final String criticalSurface;
    private TreeSet<SentenceIdentifier> ignoredOccurrences = new TreeSet<SentenceIdentifier>();
    private TreeSet<SentenceIdentifier> nonTaggedOccurrences = new TreeSet<SentenceIdentifier>();
    private TreeMap<String, TreeSet<SentenceIdentifier>> parseResultSentences = new TreeMap<String, TreeSet<SentenceIdentifier>>(Comparators.parseResultOrdering);

    public CriticalSurfaceEntry(String criticalSurface) {
        this.criticalSurface = criticalSurface;
    }

    public SentenceIdentifier getLatestOccurrence() {
        SentenceIdentifier latestOne = null;

        for (SentenceIdentifier ignoredOccurrence : ignoredOccurrences) {
            if (latestOne == null || ignoredOccurrence.compareTo(latestOne) > 0)
                latestOne = ignoredOccurrence;
        }
        for (SentenceIdentifier nonTaggedOccurrence : nonTaggedOccurrences) {
            if (latestOne == null || nonTaggedOccurrence.compareTo(latestOne) > 0)
                latestOne = nonTaggedOccurrence;
        }
        for (Map.Entry<String, TreeSet<SentenceIdentifier>> entry : parseResultSentences.entrySet()) {
            for (SentenceIdentifier identifier : entry.getValue()) {
                if (latestOne == null || identifier.compareTo(latestOne) > 0)
                    latestOne = identifier;
            }
        }
        return latestOne;
    }

    public String getCriticalSurface() {
        return criticalSurface;
    }

    public TreeSet<SentenceIdentifier> getIgnoredOccurrences() {
        return ignoredOccurrences;
    }

    public TreeSet<SentenceIdentifier> getNonTaggedOccurrences() {
        return nonTaggedOccurrences;
    }

    public TreeMap<String, TreeSet<SentenceIdentifier>> getParseResultSentences() {
        return parseResultSentences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CriticalSurfaceEntry)) return false;

        CriticalSurfaceEntry that = (CriticalSurfaceEntry) o;

        if (!criticalSurface.equals(that.criticalSurface)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return criticalSurface.hashCode();
    }
}
