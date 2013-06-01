package org.trnltk.morphology.contextless.parser.rootfinders;

import org.junit.Test;
import org.trnltk.morphology.model.ImmutableRoot;
import org.trnltk.morphology.model.Root;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

public class PuncRootFinderTest extends BaseRootFinderTest<ImmutableRoot> {

    @Override
    protected RootFinder createRootFinder() {
        return new PuncRootFinder();
    }

    @Test
    public void shouldNotCreateRootsWhenPartialInputIsShort() {
        assertThat(findRootsForPartialInput(".", ".,"), hasSize(0));
        assertThat(findRootsForPartialInput(".", "  "), hasSize(0));
        assertThat(findRootsForPartialInput("*", "*+"), hasSize(0));
        assertThat(findRootsForPartialInput("¨", "¨¨"), hasSize(0));
    }

    @Test
    public void shouldNotCreateRootsWhenInputIsNotPunc() {
        assertThat(findRootsForPartialInput(".a", ".a"), hasSize(0));
        assertThat(findRootsForPartialInput(" 1", " 1"), hasSize(0));
        assertThat(findRootsForPartialInput(" .", " ."), hasSize(0));
        assertThat(findRootsForPartialInput("a..", "a.."), hasSize(0));
        assertThat(findRootsForPartialInput("abc", "abc"), hasSize(0));
    }

    @Test
    public void shouldThrowExceptionWhenPartialInputHasSameLengthWithWholeSurfaceButNotSameWithIt() {
        try {
            findRootsForPartialInput(" x", " a");
            fail();
        } catch (Exception e) {
        }

        try {
            findRootsForPartialInput(" .", " ,");
            fail();
        } catch (Exception e) {
        }

        try {
            findRootsForPartialInput("..!", "...");
            fail();
        } catch (Exception e) {
        }

        try {
            findRootsForPartialInput("!,.", "!,!");
            fail();
        } catch (Exception e) {
        }

        try {
            findRootsForPartialInput("'`", "''");
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testShouldRecognizePuncRoots() {
        {
            final List<? extends Root> rootsForPartialInput = findRootsForPartialInput(".", ".");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0).getSequence().getUnderlyingString(), equalTo("."));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemma(), equalTo("."));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemmaRoot(), equalTo("."));
        }
        {
            final List<? extends Root> rootsForPartialInput = findRootsForPartialInput("..................", "..................");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0).getSequence().getUnderlyingString(), equalTo(".................."));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemma(), equalTo(".................."));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemmaRoot(), equalTo(".................."));
        }
        {
            final List<? extends Root> rootsForPartialInput = findRootsForPartialInput("‿﹎﹏»”>", "‿﹎﹏»”>");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0).getSequence().getUnderlyingString(), equalTo("‿﹎﹏»”>"));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemma(), equalTo("‿﹎﹏»”>"));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemmaRoot(), equalTo("‿﹎﹏»”>"));
        }
        {
            final List<? extends Root> rootsForPartialInput = findRootsForPartialInput("„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°", "„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°");
            assertThat(rootsForPartialInput, hasSize(1));
            assertThat(rootsForPartialInput.get(0).getSequence().getUnderlyingString(), equalTo("„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°"));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemma(), equalTo("„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°"));
            assertThat(rootsForPartialInput.get(0).getLexeme().getLemmaRoot(), equalTo("„⁅{﹃｟&_!§՜։܀܍෴៘‱⁂〽﹌＠｡；゠﹣︾҂©°"));
        }

        // following chars are with type Cn (Character.UNASSIGNED). screw them!
        // ︘ ⸗
    }

}
