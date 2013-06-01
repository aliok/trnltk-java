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

package org.trnltk.morphology.morphotactics.suffixformspecifications;

import org.apache.commons.lang3.Validate;
import org.trnltk.model.lexicon.Lexeme;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.common.specification.AbstractSpecification;

public class RootHasVowelDrop extends AbstractSpecification<MorphemeContainer> {

    @Override
    public String describe() {
        return "root_has_vowel_drop()";
    }

    @Override
    public boolean isSatisfiedBy(MorphemeContainer morphemeContainer) {
        Validate.notNull(morphemeContainer);

        final Root root = morphemeContainer.getRoot();
        final Lexeme lexeme = root.getLexeme();
        return lexeme.getAttributes().contains(LexemeAttribute.ProgressiveVowelDrop) &&
                root.getSequence().length() == lexeme.getLemmaRoot().length() - 1;

    }
}
