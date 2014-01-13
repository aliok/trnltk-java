package org.trnltk.apps.ambiguity;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.junit.runner.RunWith;
import org.trnltk.apps.commons.App;
import org.trnltk.apps.commons.AppProperties;
import org.trnltk.apps.commons.AppRunner;
import org.trnltk.apps.commons.SampleFiles;
import org.trnltk.common.structure.StringEnum;
import org.trnltk.common.structure.StringEnumMap;
import org.trnltk.model.lexicon.LexemeAttribute;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
@RunWith(AppRunner.class)
public class SentenceCollectorForCriticalSurfaces {
    private static final int REQUIRED_EXAMPLE_COUNT = 5;

    private final Map<String, ArrayList<String>> tokenizedFiles;

    private final Splitter onSpaceSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();

    public SentenceCollectorForCriticalSurfaces() {
        tokenizedFiles = new HashMap<String, ArrayList<String>>();
        final ImmutableMap<String, File> filesMap = SampleFiles.oneMillionSentencesTokenizedFilesMap();
        for (Map.Entry<String, File> entry : filesMap.entrySet()) {
            final CharSource charSource = Files.asCharSource(entry.getValue(), Charsets.UTF_8);
            final ImmutableList<String> lines;
            try {
                lines = charSource.readLines();
            } catch (IOException e) {
                throw new RuntimeException("Cannot read file " + entry.getValue(), e);
            }
            tokenizedFiles.put(entry.getKey(), Lists.newArrayList(lines));
        }
    }

    @App
    public void createInitialFile() throws IOException {
        final File topNFile = new File(AppProperties.generalFolder() + "/top_1K_most_ambiguous_entries_for_1m_sentences.txt");
        final CharSource topNFileSource = Files.asCharSource(topNFile, Charsets.UTF_8);
        final ImmutableList<String> topNWordLines = topNFileSource.readLines();

        final Splitter parseResultSplitter = Splitter.on(' ').trimResults().omitEmptyStrings();

        List<CriticalSurfaceEntry> criticalSurfaceEntryList = new ArrayList<CriticalSurfaceEntry>();

        for (String topNWordLine : topNWordLines) {
            final String criticalSurface = topNWordLine.substring(0, 20).trim();
            final String parseResultsStr = topNWordLine.substring(59).trim();
            final Iterable<String> parseResults = parseResultSplitter.split(parseResultsStr);

            final CriticalSurfaceEntry criticalSurfaceEntry = new CriticalSurfaceEntry(criticalSurface);
            for (String parseResult : parseResults) {
                criticalSurfaceEntry.parseResultSentences.put(parseResult, new TreeSet<SentenceIdentifier>());
            }


            criticalSurfaceEntryList.add(criticalSurfaceEntry);

        }

        final File criticalSurfacesFile = new File(AppProperties.generalFolder() + "/criticalSurfaces_initial.txt");
        writeCriticalSurfaceEntriesToFile(criticalSurfaceEntryList, criticalSurfacesFile);
    }

    @App
    public void findNewOccurrencesForCriticalSurfacesForRequired() {
        final File criticalSurfacesFile = new File(AppProperties.generalFolder() + "/criticalSurfaces.txt");
        final List<CriticalSurfaceEntry> criticalSurfaceEntries = readCriticalSurfacesFile(criticalSurfacesFile);
        for (CriticalSurfaceEntry criticalSurfaceEntry : criticalSurfaceEntries) {
            if (CollectionUtils.isEmpty(criticalSurfaceEntry.nonTaggedOccurrences)) {
                System.out.println("Gonna find new occurrences for critical surface " + criticalSurfaceEntry.criticalSurface);
                final SentenceIdentifier latestOccurrence = criticalSurfaceEntry.getLatestOccurrence();
                final String latestFileId;
                if (latestOccurrence == null)
                    latestFileId = SampleFiles.oneMillionSentencesTokenizedFilesMap().firstKey();
                else
                    latestFileId = latestOccurrence.fileId;

                final TreeSet<SentenceIdentifier> newOccurrences = findOccurrences(latestFileId, latestOccurrence==null ? -1 : latestOccurrence.line, criticalSurfaceEntry.criticalSurface);
                criticalSurfaceEntry.nonTaggedOccurrences.addAll(newOccurrences);
            }
        }

        writeCriticalSurfaceEntriesToFile(criticalSurfaceEntries, criticalSurfacesFile);
    }

    private TreeSet<SentenceIdentifier> findOccurrences(String fileId, int lineToStartAfter, String surfaceToSearch) {
        final TreeSet<SentenceIdentifier> occurrences = new TreeSet<SentenceIdentifier>();
        final ArrayList<String> lines = this.tokenizedFiles.get(fileId);
        outer:
        for (int i = lineToStartAfter + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            for (String surface : onSpaceSplitter.split(line)) {
                if (surface.equals(surfaceToSearch)) {
                    final SentenceIdentifier sentenceIdentifier = new SentenceIdentifier();
                    sentenceIdentifier.fileId = fileId;
                    sentenceIdentifier.line = i;
                    occurrences.add(sentenceIdentifier);
                    if (occurrences.size() >= REQUIRED_EXAMPLE_COUNT)
                        break outer;
                }
            }
        }

        return occurrences;
    }

    private void writeCriticalSurfaceEntriesToFile(List<CriticalSurfaceEntry> criticalSurfaceEntryList, File criticalSurfacesFile) {
        final Joiner sentenceIdentifierJoiner = Joiner.on(' ');

        BufferedWriter writer = null;
        try {
            writer = Files.newWriter(criticalSurfacesFile, Charsets.UTF_8);

            for (CriticalSurfaceEntry criticalSurfaceEntry : criticalSurfaceEntryList) {
                // write the surface
                writer.write(LineType.CriticalSurface.getStringForm() + " " + criticalSurfaceEntry.criticalSurface);
                writer.newLine();

                // write ignored occurrences
                writer.write("  " + LineType.IgnoredOccurrence.getStringForm() + " ");
                writer.write(sentenceIdentifierJoiner.join(criticalSurfaceEntry.ignoredOccurrences));
                writer.newLine();

                // write non-tagged occurrences
                writer.write("  " + LineType.NonTaggedOccurrence.getStringForm() + " ");
                writer.write(sentenceIdentifierJoiner.join(criticalSurfaceEntry.nonTaggedOccurrences));
                writer.newLine();

                // write each of the parse results and matching sentences
                for (Map.Entry<String, TreeSet<SentenceIdentifier>> parseResultEntry : criticalSurfaceEntry.parseResultSentences.entrySet()) {
                    writer.write("  " + LineType.ParseResult.getStringForm() + " " + parseResultEntry.getKey());
                    writer.write(sentenceIdentifierJoiner.join(parseResultEntry.getValue()));
                    writer.newLine();
                }
            }

            writer.flush();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to find file for writing " + criticalSurfacesFile, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close writer for file : " + criticalSurfacesFile, e);
                }
        }

    }

    private List<CriticalSurfaceEntry> readCriticalSurfacesFile(File file) {
        final List<CriticalSurfaceEntry> entries = new ArrayList<CriticalSurfaceEntry>();

        final CharSource reader;
        try {
            reader = Files.asCharSource(file, Charsets.UTF_8);
            final ImmutableList<String> lines = reader.readLines();

            CriticalSurfaceEntry currentEntry = null;

            for (String line : lines) {
                line = line.trim();

                final String strLineType = line.substring(0, 2);
                final String restOfTheLine = line.substring(2).trim();

                final LineType lineType = LineType.converter().getEnum(strLineType);

                if (lineType == LineType.CriticalSurface) {
                    if (currentEntry != null)
                        entries.add(currentEntry);

                    currentEntry = new CriticalSurfaceEntry(restOfTheLine);
                } else if (lineType == LineType.IgnoredOccurrence || lineType == LineType.NonTaggedOccurrence) {
                    final TreeSet<SentenceIdentifier> sentences = Sets.newTreeSet(Iterables.transform(onSpaceSplitter.split(restOfTheLine), new Function<String, SentenceIdentifier>() {
                        @Override
                        public SentenceIdentifier apply(String input) {
                            if (StringUtils.isEmpty(input))
                                return null;
                            else
                                return SentenceIdentifier.fromString(input);
                        }
                    }));

                    if (lineType == LineType.IgnoredOccurrence)
                        currentEntry.ignoredOccurrences = sentences;
                    else
                        currentEntry.nonTaggedOccurrences = sentences;

                } else if (lineType == LineType.ParseResult) {
                    final int endIndexOfParseResultStr = restOfTheLine.indexOf(' ');
                    final String parseResultStr = (endIndexOfParseResultStr > 0) ? restOfTheLine.substring(0, endIndexOfParseResultStr) : restOfTheLine;
                    final String sentenceIdsJoined = (endIndexOfParseResultStr > 0) ? restOfTheLine.substring(endIndexOfParseResultStr + 1) : StringUtils.EMPTY;

                    final TreeSet<SentenceIdentifier> sentences = Sets.newTreeSet(Iterables.transform(onSpaceSplitter.split(sentenceIdsJoined), new Function<String, SentenceIdentifier>() {
                        @Override
                        public SentenceIdentifier apply(String input) {
                            if (StringUtils.isEmpty(input))
                                return null;
                            else
                                return SentenceIdentifier.fromString(input);
                        }
                    }));
                    currentEntry.parseResultSentences.put(parseResultStr, sentences);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }

    private String readSentence(SentenceIdentifier sentenceIdentifier) {
        // for now, all sentences are in the memory
        // so, just read it directly
        // but when files are too big to fit the memory, then
        // * add line numbers in front of files
        // * try to estimate the location of line : averageLineLength * lineNumber
        // * seek to estimation point
        // * find previous line (cr, lf or crlf)
        // * read line number --> seekLineNumber
        // * if lineNumberToSearch < seekLineNumber, make a new estimation which is in a previous point than the current point. do it again recursively
        // * else if > ... do the similar, but on the latter part
        // * else, read until next line
        // this might not be ideal for SSD harddisks, make a research first!
        final ArrayList<String> fileLines = this.tokenizedFiles.get(sentenceIdentifier.fileId);
        Validate.notNull(fileLines, "No file content found for file id " + sentenceIdentifier.fileId);
        try {
            return fileLines.get(sentenceIdentifier.line);
        } catch (Exception e) {
            throw new RuntimeException("Line #" + sentenceIdentifier.line + " does not exist for the file " + sentenceIdentifier.fileId);
        }
    }

    private static class CriticalSurfaceEntry {
        final String criticalSurface;
        TreeSet<SentenceIdentifier> ignoredOccurrences = new TreeSet<SentenceIdentifier>();
        TreeSet<SentenceIdentifier> nonTaggedOccurrences = new TreeSet<SentenceIdentifier>();
        TreeMap<String, TreeSet<SentenceIdentifier>> parseResultSentences = new TreeMap<String, TreeSet<SentenceIdentifier>>();

        private CriticalSurfaceEntry(String criticalSurface) {
            this.criticalSurface = criticalSurface;
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
    }

    private static class SentenceIdentifier implements Comparable<SentenceIdentifier> {
        String fileId;
        int line;

        @Override
        public String toString() {
            return fileId + "#" + line;
        }

        public static SentenceIdentifier fromString(String input) {
            final int endIndexOfFileId = input.indexOf('#');
            final SentenceIdentifier sentenceIdentifier = new SentenceIdentifier();
            sentenceIdentifier.fileId = input.substring(0, endIndexOfFileId);
            sentenceIdentifier.line = Integer.parseInt(input.substring(endIndexOfFileId + 1));
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
    }

    private enum LineType implements StringEnum<LexemeAttribute> {
        CriticalSurface("CS"),
        ParseResult("PR"),
        IgnoredOccurrence("IO"),
        NonTaggedOccurrence("NT");

        private final static StringEnumMap<LineType> shortFormToPosMap = StringEnumMap.get(LineType.class);

        private final String type;

        LineType(String type) {
            this.type = type;
        }

        @Override
        public String getStringForm() {
            return this.type;
        }

        public static StringEnumMap<LineType> converter() {
            return shortFormToPosMap;
        }

    }
}
