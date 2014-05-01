package org.trnltk.morphology.contextless.parser.reducedambiguity;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.*;
import org.trnltk.morphology.contextless.parser.parsing.base.BaseContextlessMorphologicParserTest;
import org.trnltk.morphology.contextless.rootfinder.*;
import org.trnltk.morphology.lexicon.RootMapFactory;
import org.trnltk.morphology.morphotactics.*;
import org.trnltk.morphology.phonetics.PhoneticsAnalyzer;
import org.trnltk.morphology.phonetics.PhoneticsEngine;
import org.trnltk.util.MorphemeContainerFormatter;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

/**
 * @author Ali Ok (ali.ok@apache.org)
 */
public class ContextlessMorphologicParserNonParseableTest extends BaseContextlessMorphologicParserTest {

    private HashMultimap<String, ? extends Root> originalRootMap;
    private ContextlessMorphologicParser parser;

    private List<ParseGroup> nonParseableEntries;


    public ContextlessMorphologicParserNonParseableTest() throws FileNotFoundException {
        this.originalRootMap = RootMapFactory.createSimpleWithNumbersConvertCircumflexes();
        this.nonParseableEntries = getNonParseableEntries();
    }

    @Test
    public void noneOfTheEntriesShouldBeParseable() {
        final List<Pair<String, String>> failList = new ArrayList<>();

        for (ParseGroup parseGroup : nonParseableEntries) {
            // re-create root map, thus parser for every group.
            // this is because we remove some roots in some groups
            this.clonedRootMap = createRootMap();
            buildParser(this.clonedRootMap);

            // first, let us remove the roots
            if (StringUtils.isNotBlank(parseGroup.getIgnoreRoots())) {
                final Iterable<String> rootsToBeRemoved = Splitter.on(',').trimResults().omitEmptyStrings().split(parseGroup.getIgnoreRoots());
                for (String rootToBeRemoved : rootsToBeRemoved) {
                    if (rootToBeRemoved.contains("+")) {
                        final String[] split = rootToBeRemoved.split("\\+");
                        Validate.isTrue(split.length == 2);
                        final String root = split[0];
                        final String primaryPos = split[1];
                        removeRootsExceptTheOneWithPrimaryPos(root, PrimaryPos.converter().getEnum(primaryPos));
                    } else {
                        removeRoots(rootToBeRemoved);
                    }
                }
            }

            final Iterable<String> surfaces = Splitter.on('\n').trimResults().omitEmptyStrings().split(parseGroup.getWords());
            for (String surface : surfaces) {
                final List<MorphemeContainer> parseResults = this.parse(surface);

                if (CollectionUtils.isNotEmpty(parseResults)) {
                    final List<String> formattedParseResults = Lists.transform(parseResults, new Function<MorphemeContainer, String>() {
                        @Override
                        public String apply(MorphemeContainer input) {
                            return MorphemeContainerFormatter.formatMorphemeContainerWithForms(input);
                        }
                    });
                    failList.add(Pair.of(surface, Joiner.on(" ").join(formattedParseResults)));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(failList)) {
            for (Pair<String, String> pair : failList) {
                System.out.println(pair);
            }
            fail("There are surfaces which should not be parseable");
        }
    }

    @Test
    public void allGroupNamesShouldBeUnique() {
        final Iterable<String> groupNames = Iterables.transform(nonParseableEntries, new Function<ParseGroup, String>() {
            @Override
            public String apply(ParseGroup input) {
                return input.getGroup();
            }
        });

        final ArrayList<String> duplicateGroupNames = Lists.newArrayList();

        final HashSet<String> groupNameSet = Sets.newHashSet();
        for (String groupName : groupNames) {
            final boolean added = groupNameSet.add(groupName);
            if (!added) {
                duplicateGroupNames.add(groupName);
            }
        }

        assertThat("Following group names are not unique : " + duplicateGroupNames.toString(), duplicateGroupNames, hasSize(0));
    }

    @Test
    @Ignore
    public void dumpEntries() {
        for (ParseGroup parseGroup : nonParseableEntries) {
            System.out.println(parseGroup.getGroup() + "\n\t" + parseGroup.getIgnoreRoots() + "\n\t" + parseGroup.getWords());
        }
    }

    @Override
    protected void buildParser(final HashMultimap<String, Root> clonedRootMap) {
        final CopulaSuffixGraph copulaSuffixGraph = new CopulaSuffixGraph(new ProperNounSuffixGraph(new NumeralSuffixGraph(new BasicSuffixGraph())));
        copulaSuffixGraph.initialize();

        final SuffixFormSequenceApplier suffixFormSequenceApplier = new SuffixFormSequenceApplier();
        final SuffixApplier suffixApplier = new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier));
        final PredefinedPathProvider predefinedPathProvider = new PredefinedPathProviderImpl(copulaSuffixGraph, clonedRootMap, new SuffixApplier(new PhoneticsEngine(suffixFormSequenceApplier)));
        predefinedPathProvider.initialize();

        final DictionaryRootFinder dictionaryRootFinder = new DictionaryRootFinder(clonedRootMap);
        final RangeDigitsRootFinder rangeDigitsRootFinder = new RangeDigitsRootFinder();
        final OrdinalDigitsRootFinder ordinalDigitsRootFinder = new OrdinalDigitsRootFinder();
        final CardinalDigitsRootFinder cardinalDigitsRootFinder = new CardinalDigitsRootFinder();
        final ProperNounFromApostropheRootFinder properNounFromApostropheRootFinder = new ProperNounFromApostropheRootFinder();
        final ProperNounWithoutApostropheRootFinder properNounWithoutApostropheRootFinder = new ProperNounWithoutApostropheRootFinder();
        final PuncRootFinder puncRootFinder = new PuncRootFinder();


        final RootFinderChain rootFinderChain = new RootFinderChain(new RootValidator())
                .offer(puncRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(rangeDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(ordinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(cardinalDigitsRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounFromApostropheRootFinder, RootFinderChain.RootFinderPolicy.STOP_CHAIN_WHEN_INPUT_IS_HANDLED)
                .offer(properNounWithoutApostropheRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN)
                .offer(dictionaryRootFinder, RootFinderChain.RootFinderPolicy.CONTINUE_ON_CHAIN);

        final PhoneticAttributeSets phoneticAttributeSets = new PhoneticAttributeSets();
        final SuffixFormGraphExtractor charSuffixGraphExtractor = new SuffixFormGraphExtractor(suffixFormSequenceApplier, new PhoneticsAnalyzer(), phoneticAttributeSets);
        final SuffixFormGraph charSuffixGraph = charSuffixGraphExtractor.extract(copulaSuffixGraph);

        this.parser = new ContextlessMorphologicParser(charSuffixGraph, predefinedPathProvider, rootFinderChain, suffixApplier);
    }

    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return HashMultimap.create(this.originalRootMap);
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

    private List<ParseGroup> getNonParseableEntries() throws FileNotFoundException {
        TypeDescription dataDescription = new TypeDescription(ParseGroupData.class);
        dataDescription.putListPropertyType("entries", ParseGroup.class);

        Constructor constructor = new Constructor(ParseGroupData.class);
        constructor.addTypeDescription(dataDescription);
        Yaml yaml = new Yaml(constructor);

        final FileInputStream fileInputStream = new FileInputStream(new File(Resources.getResource("non-parseable-list.yaml").getFile()));
        final ParseGroupData data = (ParseGroupData) yaml.load(fileInputStream);
        return data.getEntries();
    }

    @SuppressWarnings("UnusedDeclaration")  // stuff in this class is used by YAML processor over reflection
    public static class ParseGroupData {
        private List<ParseGroup> entries;

        public List<ParseGroup> getEntries() {
            return entries;
        }

        public void setEntries(List<ParseGroup> entries) {
            this.entries = entries;
        }
    }

    @SuppressWarnings("UnusedDeclaration")  // stuff in this class is used by YAML processor over reflection
    public static class ParseGroup {
        private String group;
        private String ignoreRoots;
        private String words;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getIgnoreRoots() {
            return ignoreRoots;
        }

        public void setIgnoreRoots(String ignoreRoots) {
            this.ignoreRoots = ignoreRoots;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }

}
