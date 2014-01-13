package org.trnltk.apps.criticalsurface;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.junit.runner.RunWith;
import org.trnltk.apps.commons.App;
import org.trnltk.apps.commons.AppProperties;
import org.trnltk.apps.commons.AppRunner;
import org.trnltk.apps.commons.SampleFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
@RunWith(AppRunner.class)
public class SentenceCollectorForCriticalSurfaces {
    private static final int REQUIRED_EXAMPLE_COUNT = 5;

    private final Map<String, ArrayList<String>> tokenizedFiles;

    private final Splitter onSpaceSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();

    private CriticalSurfaceFileHelper criticalSurfaceFileHelper;

    public SentenceCollectorForCriticalSurfaces() {
        tokenizedFiles = SampleFiles.getLinesOfOneMillionSentences();
        criticalSurfaceFileHelper = new CriticalSurfaceFileHelper();
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
                criticalSurfaceEntry.getParseResultSentences().put(parseResult, new TreeSet<SentenceIdentifier>());
            }

            criticalSurfaceEntryList.add(criticalSurfaceEntry);
        }

        final File criticalSurfacesFile = new File(AppProperties.generalFolder() + "/criticalSurfaces_initial.txt");
        criticalSurfaceFileHelper.writeCriticalSurfaceEntriesToFile(criticalSurfaceEntryList, criticalSurfacesFile);
    }

    @App
    public void findNewOccurrencesForCriticalSurfacesForRequired() {
        final File criticalSurfacesFile = new File(AppProperties.generalFolder() + "/criticalSurfaces.txt");
        final List<CriticalSurfaceEntry> criticalSurfaceEntries = criticalSurfaceFileHelper.readCriticalSurfacesFile(criticalSurfacesFile);
        for (CriticalSurfaceEntry criticalSurfaceEntry : criticalSurfaceEntries) {
            if (CollectionUtils.isEmpty(criticalSurfaceEntry.getNonTaggedOccurrences())) {
                System.out.println("Gonna find new occurrences for critical surface " + criticalSurfaceEntry.getCriticalSurface());
                final SentenceIdentifier latestOccurrence = criticalSurfaceEntry.getLatestOccurrence();
                final String latestFileId;
                if (latestOccurrence == null)
                    latestFileId = SampleFiles.oneMillionSentencesTokenizedFilesMap().firstKey();
                else
                    latestFileId = latestOccurrence.getFileId();

                final TreeSet<SentenceIdentifier> newOccurrences = findOccurrences(latestFileId, latestOccurrence == null ? -1 : latestOccurrence.getLine(), criticalSurfaceEntry.getCriticalSurface());
                criticalSurfaceEntry.getNonTaggedOccurrences().addAll(newOccurrences);
            }
        }

        criticalSurfaceFileHelper.writeCriticalSurfaceEntriesToFile(criticalSurfaceEntries, criticalSurfacesFile);
    }

    private TreeSet<SentenceIdentifier> findOccurrences(String fileId, int lineToStartAfter, String surfaceToSearch) {
        final TreeSet<SentenceIdentifier> occurrences = new TreeSet<SentenceIdentifier>();
        final ArrayList<String> lines = this.tokenizedFiles.get(fileId);
        outer:
        for (int i = lineToStartAfter + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            for (String surface : onSpaceSplitter.split(line)) {
                if (surface.equals(surfaceToSearch)) {
                    final SentenceIdentifier sentenceIdentifier = new SentenceIdentifier(fileId, i);
                    occurrences.add(sentenceIdentifier);
                    if (occurrences.size() >= REQUIRED_EXAMPLE_COUNT)
                        break outer;
                }
            }
        }

        return occurrences;
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
        final ArrayList<String> fileLines = this.tokenizedFiles.get(sentenceIdentifier.getFileId());
        Validate.notNull(fileLines, "No file content found for file id " + sentenceIdentifier.getFileId());
        try {
            return fileLines.get(sentenceIdentifier.getLine());
        } catch (Exception e) {
            throw new RuntimeException("Line #" + sentenceIdentifier.getLine() + " does not exist for the file " + sentenceIdentifier.getFileId());
        }
    }

}
