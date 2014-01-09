package org.trnltk.web.training;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "trainingFileData")
@ViewScoped
public class TrainingFileData implements Serializable {
    private String fileName;
    private String content;
    private boolean strictTokenization = true;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStrictTokenization() {
        return strictTokenization;
    }

    public void setStrictTokenization(boolean strictTokenization) {
        this.strictTokenization = strictTokenization;
    }
}
