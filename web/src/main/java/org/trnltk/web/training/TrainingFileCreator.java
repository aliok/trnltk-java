package org.trnltk.web.training;

import com.google.common.base.Joiner;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.Validate;
import org.trnltk.tokenizer.TextTokenizer;
import org.trnltk.tokenizer.TextTokenizerTrainer;
import org.trnltk.tokenizer.Token;
import org.trnltk.tokenizer.TokenizationGraph;
import org.trnltk.web.common.Constants;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

@ManagedBean(name = "trainingFileCreator")
@ViewScoped
public class TrainingFileCreator implements Serializable {

    @ManagedProperty(value = "#{trainingFileData}")
    private TrainingFileData trainingFileData;

    public void create() {
        try {
            doCreate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void doCreate() {
        final String fileName = trainingFileData.getFileName();
        final String content = trainingFileData.getContent();
        Validate.notBlank(fileName);
        Validate.notBlank(content);

        // poor implementation : builds the graph every time

        final TokenizationGraph graph;
        try {
            graph = TextTokenizerTrainer.buildDefaultTokenizationGraph(true);
        } catch (Exception e) {
            System.err.println("Error creating tokenization graph");
            e.printStackTrace();
            return;
        }
        final TextTokenizer tokenizer = TextTokenizer.newBuilder()
                .blockSize(2)
                .graph(graph)
                .strict()
                .build();

        final LinkedList<Token> tokens = tokenizer.tokenize(content);

        final File trainingSetsFolder = new File(Constants.TRAINING_FILES_FOLDER_PATH);

        final File trainingFile = new File(trainingSetsFolder, fileName + FilenameUtils.EXTENSION_SEPARATOR_STR + Constants.TRAINING_FILE_EXTENSION);

        // not very good file handling, but nvm
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriterWithEncoding(trainingFile, "UTF-8", true));
            for (Token token : tokens) {
                bufferedWriter.write(token.getSurface());
                bufferedWriter.write(" ");
                bufferedWriter.write(Joiner.on('+').join(token.getTextBlockTypes()));
                bufferedWriter.write("NOT_PARSED_YET");
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.err.println("Unable to close the file");
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTrainingFileData(TrainingFileData trainingFileData) {
        this.trainingFileData = trainingFileData;
    }

}
