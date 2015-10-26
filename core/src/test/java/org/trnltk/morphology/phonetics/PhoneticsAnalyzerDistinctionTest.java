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

package org.trnltk.morphology.phonetics;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.lexicon.PhoneticAttribute;
import org.trnltk.model.lexicon.PhoneticAttributeMetadata;
import org.trnltk.model.lexicon.Root;
import org.trnltk.morphology.contextless.parser.parsing.base.BaseContextlessMorphologicParserSimpleParseSetCharacterTest;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.testutil.TestEnvironment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

public class PhoneticsAnalyzerDistinctionTest {

    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void shouldFindDistinctPhoneticAttributesInSimpleParseset005() throws Exception {
        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = getDistinctPhoneticAttributeSetsFromSimpleParseset("005");

        System.out.println("Distinct phonetic attributes count : " + distinctPhonAttrs.size());

        for (EnumSet<PhoneticAttribute> distinctPhonAttr : distinctPhonAttrs) {
            System.out.println(distinctPhonAttr);
        }
    }

    @Test
    public void shouldFindDistinctPhoneticAttributesInSimpleParseset999() throws Exception {
        Assume.assumeTrue(TestEnvironment.hasBigParseSets());

        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = getDistinctPhoneticAttributeSetsFromSimpleParseset("999");

        System.out.println("Distinct phonetic attributes count : " + distinctPhonAttrs.size());

        for (EnumSet<PhoneticAttribute> distinctPhonAttr : distinctPhonAttrs) {
            System.out.println(distinctPhonAttr);
        }
    }

    @Test
    public void shouldFindDistinctPhoneticAttributesInMasterDictionary() {
        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = getDistinctPhoneticAttributeSetsFromMasterDictionary();

        System.out.println("Distinct phonetic attributes count : " + distinctPhonAttrs.size());

        for (EnumSet<PhoneticAttribute> distinctPhonAttr : distinctPhonAttrs) {
            System.out.println(distinctPhonAttr);
        }
    }

    private Set<EnumSet<PhoneticAttribute>> getDistinctPhoneticAttributeSetsFromMasterDictionary() {
        final HashMultimap<String, ? extends Root> rootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
        final Collection<? extends Root> roots = rootMap.values();

        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = new HashSet<EnumSet<PhoneticAttribute>>();

        for (Root root : roots) {
            distinctPhonAttrs.add(EnumSet.copyOf(root.getPhoneticAttributes()));
        }
        return distinctPhonAttrs;
    }

    private Set<EnumSet<PhoneticAttribute>> getDistinctPhoneticAttributeSetsFromSimpleParseset(String number) throws IOException {
		final CharSource charSource = Resources.asCharSource(
				Resources.getResource("simpleparsesets/simpleparseset" + number + ".txt"), Charset.forName("utf-8"));

        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = new HashSet<EnumSet<PhoneticAttribute>>();

        LineProcessor<List<Pair<String, String>>> lineProcessor = new BaseContextlessMorphologicParserSimpleParseSetCharacterTest.SimpleParseSetValidationLineProcessor();
		final List<Pair<String, String>> lines = charSource.readLines(lineProcessor);

        for (Pair<String, String> line : lines) {
            final String surfaceToParse = line.getLeft();
            final EnumSet<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes(surfaceToParse, null);

            distinctPhonAttrs.add(phoneticAttributes);
        }
        return distinctPhonAttrs;
    }

    @Test
    public void shouldTryEveryPossiblePhoneticAttributeSet() throws IOException {
        Assume.assumeTrue(TestEnvironment.hasBigParseSets());

        final Set<Set<PhoneticAttribute>> powerSets = Sets.powerSet(new HashSet<PhoneticAttribute>(Lists.newArrayList(PhoneticAttribute.values())));

        final Set<Set<PhoneticAttribute>> validPhonAttrSets = new HashSet<Set<PhoneticAttribute>>();

        for (Set<PhoneticAttribute> set : powerSets) {
            if (PhoneticAttributeMetadata.isValid(set))
                validPhonAttrSets.add(set);
        }

        System.out.println("Valid phonetic attribute set count : " + validPhonAttrSets.size());

        final Set<EnumSet<PhoneticAttribute>> discoveredPhonAttrSets = new HashSet<EnumSet<PhoneticAttribute>>();
        discoveredPhonAttrSets.addAll(this.getDistinctPhoneticAttributeSetsFromMasterDictionary());
        discoveredPhonAttrSets.addAll(this.getDistinctPhoneticAttributeSetsFromSimpleParseset("999"));

        System.out.println("Discovered phonetic attribute set count : " + discoveredPhonAttrSets.size());
        System.out.println("Undiscovered valid phonetic attribute set count : " + (validPhonAttrSets.size() - discoveredPhonAttrSets.size()));

        System.out.println("\n\n\nDiscovered phonetic attribute sets");
        for (EnumSet<PhoneticAttribute> discoveredPhonAttrSet : discoveredPhonAttrSets) {
            System.out.println(discoveredPhonAttrSet);
        }

        System.out.println("\n\n\nUndiscovered valid phonetic attribute sets");
        final Sets.SetView<Set<PhoneticAttribute>> difference = Sets.difference(validPhonAttrSets, discoveredPhonAttrSets);
        for (Set<PhoneticAttribute> undiscoveredPhonAttrSet : difference) {
            System.out.println(undiscoveredPhonAttrSet);
        }

        if ((validPhonAttrSets.size() - discoveredPhonAttrSets.size()) > 0)
            fail("Found new undiscovered case, please add more metadata");
    }
}