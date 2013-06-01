package org.trnltk.morphology.phonetics;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.morphology.contextless.parser.suffixbased.ContextlessMorphologicParserSimpleParseSetCharacterTest;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.model.Root;
import zemberek3.shared.lexicon.tr.PhoneticAttribute;
import zemberek3.shared.lexicon.tr.PhoneticAttributeMetadata;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.fail;

public class PhoneticsAnalyzerDistinctionTest {

    PhoneticsAnalyzer phoneticsAnalyzer;

    @Before
    public void setUp() throws Exception {
        phoneticsAnalyzer = new PhoneticsAnalyzer();
    }

    @Test
    public void shouldFindDistinctPhoneticAttributesInSimpleParseset999() throws Exception {
        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = getDistinctPhoneticAttributeSetsFromSimpleParseset999();

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

        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = new HashSet<>();

        for (Root root : roots) {
            distinctPhonAttrs.add(EnumSet.copyOf(root.getPhoneticAttributes()));
        }
        return distinctPhonAttrs;
    }

    private Set<EnumSet<PhoneticAttribute>> getDistinctPhoneticAttributeSetsFromSimpleParseset999() throws IOException {
        final InputSupplier<InputStreamReader> supplier = Resources.newReaderSupplier(Resources.getResource("simpleparsesets/simpleparseset999.txt"),
                Charset.forName("utf-8"));

        final Set<EnumSet<PhoneticAttribute>> distinctPhonAttrs = new HashSet<>();

        final List<Pair<String, String>> lines = CharStreams.readLines(supplier, new ContextlessMorphologicParserSimpleParseSetCharacterTest.SimpleParseSetValidationLineProcessor());

        for (Pair<String, String> line : lines) {
            final String surfaceToParse = line.getLeft();
            final EnumSet<PhoneticAttribute> phoneticAttributes = phoneticsAnalyzer.calculatePhoneticAttributes(surfaceToParse, null);

            distinctPhonAttrs.add(phoneticAttributes);
        }
        return distinctPhonAttrs;
    }

    @Test
    public void shouldTryEveryPossiblePhoneticAttributeSet() throws IOException {
        final Set<Set<PhoneticAttribute>> powerSets = Sets.powerSet(new HashSet<>(Lists.newArrayList(PhoneticAttribute.values())));

        final Set<Set<PhoneticAttribute>> validPhonAttrSets = new HashSet<>();

        for (Set<PhoneticAttribute> set : powerSets) {
            if (PhoneticAttributeMetadata.isValid(set))
                validPhonAttrSets.add(set);
        }

        System.out.println("Valid phonetic attribute set count : " + validPhonAttrSets.size());

        final Set<EnumSet<PhoneticAttribute>> discoveredPhonAttrSets = new HashSet<>();
        discoveredPhonAttrSets.addAll(this.getDistinctPhoneticAttributeSetsFromMasterDictionary());
        discoveredPhonAttrSets.addAll(this.getDistinctPhoneticAttributeSetsFromSimpleParseset999());

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
