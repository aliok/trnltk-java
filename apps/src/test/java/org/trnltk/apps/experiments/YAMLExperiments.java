/*
 * Copyright  2013  Ali Ok (aliokATapacheDOTorg)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.trnltk.apps.experiments;

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
