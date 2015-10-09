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

package org.trnltk.tokenizer.data;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.net.URL;
import java.util.List;

public class TokenizerTrainingData {
    private List<TokenizerTrainingEntry> entries;

    public List<TokenizerTrainingEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TokenizerTrainingEntry> entries) {
        this.entries = entries;
    }

    public static TokenizerTrainingData createDefaultTrainingData() throws IOException {
        URL resourceURL = Resources.getResource("tokenizer/training-data.yaml");
        ByteSource byteSource = Resources.asByteSource(resourceURL);
        return createFromYamlByteSource(byteSource);
    }

    public static TokenizerTrainingData createFromYamlFile(File file) throws FileNotFoundException {
        ByteSource byteSource = Files.asByteSource(file);
        return createFromYamlByteSource(byteSource);
    }

    public static TokenizerTrainingData createFromYamlByteSource(ByteSource byteSource) {
        TypeDescription dataDescription = new TypeDescription(TokenizerTrainingData.class);
        dataDescription.putListPropertyType("entries", TokenizerTrainingEntry.class);

        Constructor constructor = new Constructor(TokenizerTrainingData.class);
        constructor.addTypeDescription(dataDescription);
        Yaml yaml = new Yaml(constructor);

        try {
			InputStream str = byteSource.openBufferedStream();
			return (TokenizerTrainingData) yaml.load(str);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }
}