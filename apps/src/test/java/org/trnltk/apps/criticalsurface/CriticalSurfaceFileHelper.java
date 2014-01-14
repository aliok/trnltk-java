package org.trnltk.apps.criticalsurface;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.trnltk.common.structure.StringEnum;
import org.trnltk.common.structure.StringEnumMap;
import org.trnltk.model.lexicon.LexemeAttribute;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class CriticalSurfaceFileHelper implements Serializable {

    private final Splitter onSpaceSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();

    public List<CriticalSurfaceEntry> readCriticalSurfacesFile(File file) {
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
                        currentEntry.getIgnoredOccurrences().addAll(sentences);
                    else
                        currentEntry.getNonTaggedOccurrences().addAll(sentences);

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
                    currentEntry.getParseResultSentences().put(parseResultStr, sentences);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }

    public void writeCriticalSurfaceEntriesToFile(List<CriticalSurfaceEntry> criticalSurfaceEntryList, File criticalSurfacesFile) {
        final Joiner sentenceIdentifierJoiner = Joiner.on(' ');

        BufferedWriter writer = null;
        try {
            writer = Files.newWriter(criticalSurfacesFile, Charsets.UTF_8);

            for (CriticalSurfaceEntry criticalSurfaceEntry : criticalSurfaceEntryList) {
                // write the surface
                writer.write(LineType.CriticalSurface.getStringForm() + " " + criticalSurfaceEntry.getCriticalSurface());
                writer.newLine();

                // write ignored occurrences
                writer.write("  " + LineType.IgnoredOccurrence.getStringForm() + " ");
                writer.write(sentenceIdentifierJoiner.join(criticalSurfaceEntry.getIgnoredOccurrences()));
                writer.newLine();

                // write non-tagged occurrences
                writer.write("  " + LineType.NonTaggedOccurrence.getStringForm() + " ");
                writer.write(sentenceIdentifierJoiner.join(criticalSurfaceEntry.getNonTaggedOccurrences()));
                writer.newLine();

                // write each of the parse results and matching sentences
                for (Map.Entry<String, TreeSet<SentenceIdentifier>> parseResultEntry : criticalSurfaceEntry.getParseResultSentences().entrySet()) {
                    writer.write("  " + LineType.ParseResult.getStringForm() + " " + parseResultEntry.getKey() + " ");
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

    public String getSentenceFromMemory(Map<String, ArrayList<String>> tokenizedFiles, SentenceIdentifier sentenceIdentifier) {
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
        final ArrayList<String> fileLines = tokenizedFiles.get(sentenceIdentifier.getFileId());
        Validate.notNull(fileLines, "No file content found for file id " + sentenceIdentifier.getFileId());
        try {
            return fileLines.get(sentenceIdentifier.getLine());
        } catch (Exception e) {
            throw new RuntimeException("Line #" + sentenceIdentifier.getLine() + " does not exist for the file " + sentenceIdentifier.getFileId());
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
