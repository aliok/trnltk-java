package org.trnltk.experiments;

import com.google.common.io.Resources;
import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.trnltk.tokenizer.data.TokenizerTrainingData;
import org.trnltk.tokenizer.data.TokenizerTrainingEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class YAMLExperiments {

    @Test
    public void testYamlLoadNoType() throws FileNotFoundException {
        final Yaml yaml = new Yaml();
        final Object load = yaml.load(new FileInputStream(new File(Resources.getResource("tokenizer/training-data.yaml").getFile())));
        System.out.println(load);
    }

    @Test
    public void testYamlLoadWithType() throws FileNotFoundException {
        TypeDescription dataDescription = new TypeDescription(TokenizerTrainingData.class);
        dataDescription.putListPropertyType("entries", TokenizerTrainingEntry.class);

        Constructor constructor = new Constructor(TokenizerTrainingData.class);
        constructor.addTypeDescription(dataDescription);
        Yaml yaml = new Yaml(constructor);

        final FileInputStream fileInputStream = new FileInputStream(new File(Resources.getResource("tokenizer/training-data.yaml").getFile()));
        final TokenizerTrainingData data = (TokenizerTrainingData) yaml.load(fileInputStream);
        for (TokenizerTrainingEntry tokenizerTrainingEntry : data.getEntries()) {
            System.out.println(tokenizerTrainingEntry.getText() + " " + tokenizerTrainingEntry.getTknz());
        }
    }

}
