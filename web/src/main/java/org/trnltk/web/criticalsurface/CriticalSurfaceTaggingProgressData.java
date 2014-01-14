package org.trnltk.web.criticalsurface;

import org.trnltk.apps.criticalsurface.CriticalSurfaceEntry;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
@ManagedBean(name = "criticalSurfaceTaggingProgressData")
@ApplicationScoped
public class CriticalSurfaceTaggingProgressData implements Serializable {

    private int currentSurfaceIndex = -1;
    private int currentOccurrenceIndex = -1;
    private CriticalSurfaceEntry currentEntry = null;

    public boolean isAtTheEnd() {
        return currentEntry == null;
    }

    public int getCurrentSurfaceIndex() {
        return currentSurfaceIndex;
    }

    public int getCurrentOccurrenceIndex() {
        return currentOccurrenceIndex;
    }

    public CriticalSurfaceEntry getCurrentEntry() {
        return currentEntry;
    }

    public void setCurrentSurfaceIndex(int currentSurfaceIndex) {
        this.currentSurfaceIndex = currentSurfaceIndex;
    }

    public void setCurrentOccurrenceIndex(int currentOccurrenceIndex) {
        this.currentOccurrenceIndex = currentOccurrenceIndex;
    }

    public void setCurrentEntry(CriticalSurfaceEntry currentEntry) {
        this.currentEntry = currentEntry;
    }
}
