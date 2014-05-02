package org.trnltk.morphology.contextless.parser.parsing.suffixbased;

import com.google.common.collect.HashMultimap;
import org.junit.Before;
import org.junit.Test;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.Root;
import org.trnltk.model.morpheme.MorphemeContainer;
import org.trnltk.morphology.contextless.parser.parsing.base.BaseContextlessMorphologicParserTest;
import org.trnltk.morphology.contextless.parser.suffixbased.ContextlessMorphologicParser;
import org.trnltk.morphology.lexicon.RootMapFactory;

import java.util.List;

/**
 * This test is used to track parsing problems as it is much harder with suffixForm based parser.
 *
 * @author Ali Ok (ali.ok@apache.org)
 */
public class ContextlessMorphologicParserRandomTest extends BaseContextlessMorphologicParserTest {
    private HashMultimap<String, ? extends Root> originalRootMap;
    private ContextlessMorphologicParser parser;

    public ContextlessMorphologicParserRandomTest() {
        this.originalRootMap = RootMapFactory.createSimpleConvertCircumflexes();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }


    @Override
    protected HashMultimap<String, Root> createRootMap() {
        return HashMultimap.create(this.originalRootMap);
    }

    @Override
    protected void buildParser(HashMultimap<String, Root> clonedRootMap) {
        this.parser = ContextlessMorphologicParserFactory.createWithBigGraphForRootMap(clonedRootMap);
        this.parser = ContextlessMorphologicParserFactory.createWithRAGraphForRootMap(clonedRootMap);
    }

    @Override
    protected List<MorphemeContainer> parse(String surfaceToParse) {
        return this.parser.parse(new TurkishSequence(surfaceToParse));
    }

    @Test
    public void testSomething() {
        removeRoots("kut");

        //assertParseCorrect("kutulu", "kutu(kutu)+Noun+A3sg+Pnon+Nom+Adj+With(lI[lu])");
    }
}
