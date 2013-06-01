package zemberek3.shared.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Pattern;

public class RegexMatcher {
    private static abstract class AbstractRegexpMatcher extends TypeSafeMatcher<String> {
        protected final String regex;
        protected final Pattern compiledRegex;

        private AbstractRegexpMatcher(final String regex) {
            this.regex = regex;
            compiledRegex = Pattern.compile(regex);
        }
    }

    private static class MatchesRegexpMatcher extends AbstractRegexpMatcher {
        private MatchesRegexpMatcher(final String regex) {
            super(regex);
        }

        @Override
        public boolean matchesSafely(final String item) {
            return compiledRegex.matcher(item).matches();
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("matches regex ").appendValue(regex);
        }
    }

    private static class ContainsMatchRegexpMatcher extends AbstractRegexpMatcher {
        private ContainsMatchRegexpMatcher(final String regex) {
            super(regex);
        }

        @Override
        public boolean matchesSafely(final String item) {
            return compiledRegex.matcher(item).find();
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("contains match for regex ").appendValue(regex);
        }
    }

    /**
     * Match the regexp against the whole input string
     *
     * @param regex the regular expression to match
     * @return a matcher which matches the whole input string
     */
    public static Matcher<String> matches(final String regex) {
        return new MatchesRegexpMatcher(regex);
    }

    /**
     * Match the regexp against any substring of the input string
     *
     * @param regex the regular expression to match
     * @return a matcher which matches anywhere in the input string
     */
    public static Matcher<String> containsMatch(final String regex) {
        return new ContainsMatchRegexpMatcher(regex);
    }
}