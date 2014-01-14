package org.trnltk.web.criticalsurface;

import org.trnltk.apps.criticalsurface.SentenceIdentifier;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class SentenceContainer implements Serializable {
    private final List<String> surfaces;
    private final SentenceIdentifier sentenceIdentifier;

    public SentenceContainer(List<String> surfaces, SentenceIdentifier sentenceIdentifier) {
        this.surfaces = surfaces;
        this.sentenceIdentifier = sentenceIdentifier;
    }

    public List<String> getSurfaces() {
        return surfaces;
    }

    public SentenceIdentifier getSentenceIdentifier() {
        return sentenceIdentifier;
    }
}
