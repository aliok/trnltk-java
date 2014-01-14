package org.trnltk.web.criticalsurface;

import org.trnltk.apps.criticalsurface.CriticalSurfaceEntry;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
@ManagedBean(name = "criticalSurfaceTaggingData")
@ApplicationScoped
public class CriticalSurfaceTaggingData implements Serializable {

    private Map<String, ArrayList<String>> tokenizedSentencesOfFiles;
    private List<CriticalSurfaceEntry> criticalSurfaceEntries;

    public Map<String, ArrayList<String>> getTokenizedSentencesOfFiles() {
        return tokenizedSentencesOfFiles;
    }

    public void setTokenizedSentencesOfFiles(Map<String, ArrayList<String>> tokenizedSentencesOfFiles) {
        this.tokenizedSentencesOfFiles = tokenizedSentencesOfFiles;
    }

    public List<CriticalSurfaceEntry> getCriticalSurfaceEntries() {
        return criticalSurfaceEntries;
    }

    public void setCriticalSurfaceEntries(List<CriticalSurfaceEntry> criticalSurfaceEntries) {
        this.criticalSurfaceEntries = criticalSurfaceEntries;
    }
}
