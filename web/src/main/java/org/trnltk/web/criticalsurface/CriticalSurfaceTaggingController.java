package org.trnltk.web.criticalsurface;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.trnltk.apps.commons.AppProperties;
import org.trnltk.apps.commons.SampleFiles;
import org.trnltk.apps.criticalsurface.CriticalSurfaceEntry;
import org.trnltk.apps.criticalsurface.CriticalSurfaceFileHelper;
import org.trnltk.apps.criticalsurface.SentenceIdentifier;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
@ManagedBean(name = "criticalSurfaceTaggingController")
@ApplicationScoped
public class CriticalSurfaceTaggingController implements Serializable {

    private final Splitter onSpaceSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();

    @ManagedProperty(value = "#{criticalSurfaceTaggingData}")
    private CriticalSurfaceTaggingData criticalSurfaceTaggingData;

    @ManagedProperty(value = "#{criticalSurfaceTaggingProgressData}")
    private CriticalSurfaceTaggingProgressData criticalSurfaceTaggingProgressData;

    private CriticalSurfaceFileHelper criticalSurfaceFileHelper;

    @PostConstruct
    public void postConstruct() {
        criticalSurfaceFileHelper = new CriticalSurfaceFileHelper();

        final File criticalSurfacesFile = new File(AppProperties.generalFolder() + "/criticalSurfaces.txt");
        criticalSurfaceTaggingData.setCriticalSurfaceEntries(criticalSurfaceFileHelper.readCriticalSurfacesFile(criticalSurfacesFile));

        criticalSurfaceTaggingData.setTokenizedSentencesOfFiles(SampleFiles.getLinesOfOneMillionSentences());
    }

    public void initialize() {
        this.goBackToStart();
        this.findNextOccurrenceToTag();
    }

    public void flush() throws IOException {
        // 1. take a backup of the original file
        final File criticalSurfacesFile = new File(AppProperties.generalFolder() + "/criticalSurfaces.txt");
        final File criticalSurfacesBackupFile = new File(AppProperties.generalFolder() + "/criticalSurfaces_" + System.currentTimeMillis() + ".txt");
        FileUtils.copyFile(criticalSurfacesFile, criticalSurfacesBackupFile);

        // 2. write to file
        this.criticalSurfaceFileHelper.writeCriticalSurfaceEntriesToFile(criticalSurfaceTaggingData.getCriticalSurfaceEntries(), criticalSurfacesFile);
    }

    public void skipTaggingOccurrence() {
        final CriticalSurfaceEntry currentEntry = criticalSurfaceTaggingProgressData.getCurrentEntry();
        final int currentOccurrenceIndex = criticalSurfaceTaggingProgressData.getCurrentOccurrenceIndex();
        if ((currentOccurrenceIndex + 1) < currentEntry.getNonTaggedOccurrences().size()){
            criticalSurfaceTaggingProgressData.setCurrentOccurrenceIndex(currentOccurrenceIndex + 1);
        }
        else{
            final int currentSurfaceIndex = criticalSurfaceTaggingProgressData.getCurrentSurfaceIndex();
            if ((currentSurfaceIndex + 1) < criticalSurfaceTaggingData.getCriticalSurfaceEntries().size()) {
                criticalSurfaceTaggingProgressData.setCurrentSurfaceIndex(currentSurfaceIndex + 1);
                criticalSurfaceTaggingProgressData.setCurrentEntry(criticalSurfaceTaggingData.getCriticalSurfaceEntries().get(currentSurfaceIndex + 1));
                findNextOccurrenceToTag();
            } else {
                criticalSurfaceTaggingProgressData.setCurrentSurfaceIndex(-1);
                criticalSurfaceTaggingProgressData.setCurrentEntry(null);
            }
        }
    }

    public void tagOccurrence(int choice) {
        // 1. find the parse result for the choice
        final Map.Entry<String, TreeSet<SentenceIdentifier>> chosenParseResult = getChosenParseResult(choice);

        // 2. remove occurrence from nonTaggedOccurrences
        final SentenceIdentifier sentenceIdentifierToTag = popSentenceIdentifierFromNonTaggedOccurrences();

        // 3. add occurrence to that choice
        chosenParseResult.getValue().add(sentenceIdentifierToTag);

        // 4. go to next occurrence to tag
        findNextOccurrenceToTag();
    }

    public void ignoreOccurrence() {
        final CriticalSurfaceEntry currentEntry = criticalSurfaceTaggingProgressData.getCurrentEntry();

        // 1. remove occurrence from nonTaggedOccurrences
        final SentenceIdentifier sentenceIdentifierToTag = popSentenceIdentifierFromNonTaggedOccurrences();

        // 2. add occurrence to ignored list
        currentEntry.getIgnoredOccurrences().add(sentenceIdentifierToTag);

        // 3. go to next occurrence to tag
        findNextOccurrenceToTag();
    }

    public void goBackToStart() {
        criticalSurfaceTaggingProgressData.setCurrentSurfaceIndex(-1);
        criticalSurfaceTaggingProgressData.setCurrentEntry(null);
    }

    public List<ParseResultWithSentencesContainer> getChoices() {
        final CriticalSurfaceEntry currentEntry = this.criticalSurfaceTaggingProgressData.getCurrentEntry();
        return Lists.newArrayList(Iterables.transform(currentEntry.getParseResultSentences().entrySet(), new Function<Map.Entry<String, TreeSet<SentenceIdentifier>>, ParseResultWithSentencesContainer>() {
            @Override
            public ParseResultWithSentencesContainer apply(Map.Entry<String, TreeSet<SentenceIdentifier>> input) {
                final String parseResultStr = input.getKey();
                final TreeSet<SentenceIdentifier> parseResultExamples = input.getValue();
                final List<Pair<SentenceContainer, Integer>> sentences = new ArrayList<Pair<SentenceContainer, Integer>>();
                for (SentenceIdentifier parseResultExample : parseResultExamples) {
                    final String sentence = criticalSurfaceFileHelper.getSentenceFromMemory(criticalSurfaceTaggingData.getTokenizedSentencesOfFiles(), parseResultExample);
                    final ArrayList<String> surfacesInTheSentence = Lists.newArrayList(onSpaceSplitter.split(sentence));
                    final int indexOfSurfaceInTheSentence = surfacesInTheSentence.indexOf(currentEntry.getCriticalSurface());
                    Validate.isTrue(indexOfSurfaceInTheSentence >= 0);
                    sentences.add(ImmutablePair.of(new SentenceContainer(surfacesInTheSentence, parseResultExample), indexOfSurfaceInTheSentence));
                }
                return new ParseResultWithSentencesContainer(parseResultStr, sentences);
            }
        }));
    }

    private void findNextOccurrenceToTag() {
        // 1. if current entry has a nonTaggedOccurrence, then go to that
        // 2. if not, go to next words until one of them has a nonTaggedOccurrence


        // 1.
        final CriticalSurfaceEntry currentEntry = criticalSurfaceTaggingProgressData.getCurrentEntry();
        if (currentEntry != null && CollectionUtils.isNotEmpty(currentEntry.getNonTaggedOccurrences())) {
            criticalSurfaceTaggingProgressData.setCurrentOccurrenceIndex(0);
        }
        // 2.
        else {
            final int currentSurfaceIndex = criticalSurfaceTaggingProgressData.getCurrentSurfaceIndex();
            if ((currentSurfaceIndex + 1) < criticalSurfaceTaggingData.getCriticalSurfaceEntries().size()) {
                criticalSurfaceTaggingProgressData.setCurrentSurfaceIndex(currentSurfaceIndex + 1);
                criticalSurfaceTaggingProgressData.setCurrentEntry(criticalSurfaceTaggingData.getCriticalSurfaceEntries().get(currentSurfaceIndex + 1));
                findNextOccurrenceToTag();
            } else {
                criticalSurfaceTaggingProgressData.setCurrentSurfaceIndex(-1);
                criticalSurfaceTaggingProgressData.setCurrentEntry(null);
            }
        }
    }

    public SentenceIdentifier getCurrentSentenceIdentifier() {
        final CriticalSurfaceEntry currentEntry = this.criticalSurfaceTaggingProgressData.getCurrentEntry();
        if (currentEntry == null)
            return null;
        return Iterables.get(currentEntry.getNonTaggedOccurrences(), this.criticalSurfaceTaggingProgressData.getCurrentOccurrenceIndex());
    }

    public List<String> getCurrentSentence() {
        final CriticalSurfaceEntry currentEntry = this.criticalSurfaceTaggingProgressData.getCurrentEntry();
        if (currentEntry == null)
            return Collections.emptyList();
        final SentenceIdentifier identifierOfCurrentSentence = getCurrentSentenceIdentifier();
        final String sentence = criticalSurfaceFileHelper.getSentenceFromMemory(criticalSurfaceTaggingData.getTokenizedSentencesOfFiles(), identifierOfCurrentSentence);
        return Lists.newArrayList(onSpaceSplitter.split(sentence));
    }

    public int getIndexOfCurrentCriticalSurfaceInCurrentSentence() {
        if (this.criticalSurfaceTaggingProgressData.getCurrentEntry() == null)
            return -1;

        final List<String> currentSentence = this.getCurrentSentence();
        for (int i = 0; i < currentSentence.size(); i++) {
            final String surface = currentSentence.get(i);
            if (surface.equals(this.criticalSurfaceTaggingProgressData.getCurrentEntry().getCriticalSurface()))
                return i;
        }

        throw new RuntimeException("Current critical surface does not exist in the current sentence!");
    }

    private Map.Entry<String, TreeSet<SentenceIdentifier>> getChosenParseResult(int choice) {
        final CriticalSurfaceEntry currentEntry = criticalSurfaceTaggingProgressData.getCurrentEntry();
        return Iterables.get(currentEntry.getParseResultSentences().entrySet(), choice);
    }

    private SentenceIdentifier popSentenceIdentifierFromNonTaggedOccurrences() {
        final CriticalSurfaceEntry currentEntry = criticalSurfaceTaggingProgressData.getCurrentEntry();
        final int currentOccurrenceIndex = criticalSurfaceTaggingProgressData.getCurrentOccurrenceIndex();
        SentenceIdentifier sentenceIdentifierToTag = null;

        int i = 0;
        for (Iterator<SentenceIdentifier> iterator = currentEntry.getNonTaggedOccurrences().iterator(); iterator.hasNext(); i++) {
            sentenceIdentifierToTag = iterator.next();
            if (i == currentOccurrenceIndex) {
                iterator.remove();
                break;
            }
        }
        Validate.isTrue(i == currentOccurrenceIndex);
        Validate.notNull(sentenceIdentifierToTag);
        return sentenceIdentifierToTag;
    }

    public void setCriticalSurfaceTaggingData(CriticalSurfaceTaggingData criticalSurfaceTaggingData) {
        this.criticalSurfaceTaggingData = criticalSurfaceTaggingData;
    }

    public void setCriticalSurfaceTaggingProgressData(CriticalSurfaceTaggingProgressData criticalSurfaceTaggingProgressData) {
        this.criticalSurfaceTaggingProgressData = criticalSurfaceTaggingProgressData;
    }
}
