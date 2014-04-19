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

package org.trnltk.morphology.contextless.parser.parsing.base;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.trnltk.model.lexicon.PrimaryPos;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.MandatoryTransitionApplier;
import org.trnltk.morphology.contextless.parser.SuffixApplier;
import org.trnltk.morphology.contextless.parser.ContextlessMorphologicParser;
import org.trnltk.testutil.testmatchers.ParseResultsDontExistMatcher;
import org.trnltk.testutil.testmatchers.ParseResultsEqualMatcher;
import org.trnltk.testutil.testmatchers.ParseResultsExistMatcher;
import org.trnltk.util.MorphemeContainerFormatter;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class BaseContextlessMorphologicParserTest {
    protected HashMultimap<String, Root> clonedRootMap;

    @Before
    public void setUp() throws Exception {
        this.clonedRootMap = createRootMap();
        this.buildParser(clonedRootMap);

        // set the appender every time!
        final Enumeration currentLoggers = Logger.getLogger("org.trnltk").getLoggerRepository().getCurrentLoggers();
        while (currentLoggers.hasMoreElements()) {
            final Logger logger = (Logger) currentLoggers.nextElement();
            logger.setLevel(Level.WARN);
        }
    }

    // useful while running tests individually
    // since parser and other things are really verbose, we cannot enable logging by default
    protected void turnParserLoggingOn() {
        Logger.getLogger(ContextlessMorphologicParser.class).setLevel(Level.DEBUG);
        Logger.getLogger(ContextlessMorphologicParser.class).setLevel(Level.DEBUG);
    }

    // useful while running tests individually
    // since parser and other things are really verbose, we cannot enable logging by default
    protected void turnSuffixApplierLoggingOn() {
        // set the appender every time!
        Logger.getLogger(SuffixApplier.class).setLevel(Level.DEBUG);
    }

    // useful while running tests individually
    // since parser and other things are really verbose, we cannot enable logging by default
    protected void turnMandatoryTransitionApplierLoggingOn() {
        // set the appender every time!
        Logger.getLogger(MandatoryTransitionApplier.class).setLevel(Level.DEBUG);
    }

    protected abstract HashMultimap<String, Root> createRootMap();

    protected abstract void buildParser(HashMultimap<String, Root> clonedRootMap);

    protected abstract List<MorphemeContainer> parse(String surfaceToParse);

    public void assertParseCorrect(String surfaceToParse, String... expectedParseResults) {
        assertThat(this.getFormattedParseResults(surfaceToParse), new ParseResultsEqualMatcher(true, expectedParseResults));
    }

    protected void assertParseCorrectForVerb(String surfaceToParse, String... expectedParseResults) {
        assertThat(this.getFormattedParseResults(surfaceToParse), new ParseResultsEqualMatcher(false, expectedParseResults));
    }

    protected void assertNotParsable(String surfaceToParse) {
        assertThat(this.getFormattedParseResults(surfaceToParse), Matchers.<Collection<String>>equalTo(new ArrayList<String>()));
    }

    protected void assertParseExists(String surfaceToParse, String... expectedParseResults) {
        assertThat(this.getFormattedParseResults(surfaceToParse), new ParseResultsExistMatcher(expectedParseResults));

    }

    protected void assertParseDoesntExist(String surfaceToParse, String... expectedParseResults) {
        assertThat(this.getFormattedParseResults(surfaceToParse), new ParseResultsDontExistMatcher(expectedParseResults));
    }

    protected Collection<String> getFormattedParseResults(String surfaceToParse) {
        final List<MorphemeContainer> morphemeContainers = this.parse(surfaceToParse);
        return Lists.transform(morphemeContainers, new Function<MorphemeContainer, String>() {
            @Override
            public String apply(MorphemeContainer input) {
                return MorphemeContainerFormatter.formatMorphemeContainerWithForms(input);
            }
        });
    }

    protected void removeRoots(String... root) {
        for (String s : root) {
            this.clonedRootMap.removeAll(s);
        }
    }

    protected void removeRootsExceptTheOneWithPrimaryPos(final String root, final PrimaryPos primaryPos) {
        final Set<Root> roots = this.clonedRootMap.get(root);
        final Iterable<Root> filteredRoots = Lists.newArrayList(Iterables.filter(roots, new Predicate<Root>() {
            @Override
            public boolean apply(Root input) {
                return input.getLexeme().getPrimaryPos().equals(primaryPos);
            }
        }));
        this.clonedRootMap.replaceValues(root, filteredRoots);
    }

}
