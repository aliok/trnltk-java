package org.trnltk.morphology.contextless.rootfinder;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.trnltk.model.letter.TurkishSequence;
import org.trnltk.model.lexicon.Lexeme;
import org.trnltk.model.lexicon.LexemeAttribute;
import org.trnltk.model.lexicon.Root;

import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RootValidatorTest {

    RootValidator rootValidator;

    @Before
    public void setUp() throws Exception {
        rootValidator = new RootValidator();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullRoot() {
        rootValidator.isValid(null, new TurkishSequence(""));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullPartialSurface() {
        rootValidator.isValid(Mockito.mock(Root.class), null);
    }

    @Test
    public void shouldSayInvalidWhenRootIsLongerThanPartialSequence() {
        final Root root = Mockito.mock(Root.class);
        final TurkishSequence rootSequence = new TurkishSequence("1234");
        when(root.getSequence()).thenReturn(rootSequence);

        final TurkishSequence partialSurface = new TurkishSequence("123");
        boolean value = rootValidator.isValid(root, partialSurface);
        assertThat(value, equalTo(false));
    }

    @Test
    public void shouldSayValidWhenRootHasCompoundP3sg() {
        final Root root = Mockito.mock(Root.class);
        final TurkishSequence rootSequence = new TurkishSequence("abc");
        final Lexeme lexeme = mock(Lexeme.class);
        when(root.getSequence()).thenReturn(rootSequence);
        when(root.getLexeme()).thenReturn(lexeme);
        when(lexeme.getAttributes()).thenReturn(Sets.newHashSet(LexemeAttribute.CompoundP3sg));

        {
            final TurkishSequence partialSurface = new TurkishSequence("abd");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("abde");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("Abd");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("Abde");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
    }

    @Test
    public void shouldSayInvalidWhenPartialSurfaceDoesNotStartWithRootSequence() {
        final Root root = Mockito.mock(Root.class);
        final TurkishSequence rootSequence = new TurkishSequence("abc");
        final Lexeme lexeme = mock(Lexeme.class);
        when(root.getSequence()).thenReturn(rootSequence);
        when(root.getLexeme()).thenReturn(lexeme);
        when(lexeme.getAttributes()).thenReturn(new HashSet<LexemeAttribute>());

        {
            final TurkishSequence partialSurface = new TurkishSequence("abd");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(false));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("ab");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(false));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("Abd");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(false));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("Ab");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(false));
        }
    }

    @Test
    public void shouldSayValidWhenPartialSurfaceDoesStartsWithRootSequence() {
        final Root root = Mockito.mock(Root.class);
        final TurkishSequence rootSequence = new TurkishSequence("abc");
        final Lexeme lexeme = mock(Lexeme.class);
        when(root.getSequence()).thenReturn(rootSequence);
        when(root.getLexeme()).thenReturn(lexeme);
        when(lexeme.getAttributes()).thenReturn(new HashSet<LexemeAttribute>());

        {
            final TurkishSequence partialSurface = new TurkishSequence("abc");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("abcd");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("Abc");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
        {
            final TurkishSequence partialSurface = new TurkishSequence("Abcd");
            assertThat(rootValidator.isValid(root, partialSurface), equalTo(true));
        }
    }
}
