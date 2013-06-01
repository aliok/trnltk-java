package org.trnltk.morphology.ambiguity.model;

import com.google.common.base.Joiner;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParseResultDifference {
    private final RootDifference rootDifference;
    private final List<ParseResultPartDifference> parseResultPartDifferences = new ArrayList<>();

    public ParseResultDifference(RootDifference rootDifference) {
        this.rootDifference = rootDifference;
    }

    public RootDifference getRootDifference() {
        return rootDifference;
    }

    public List<ParseResultPartDifference> getParseResultPartDifferences() {
        return Collections.unmodifiableList(parseResultPartDifferences);
    }

    public boolean hasNoRootDifference() {
        return this.rootDifference == null;
    }

    public boolean hasRootDifference() {
        return !this.hasNoPartDifference();
    }

    public boolean hasNoPartDifference() {
        return CollectionUtils.isEmpty(this.parseResultPartDifferences);
    }

    public boolean hasPartDifference() {
        return !this.hasNoPartDifference();
    }

    public void addParseResultPartDifference(ParseResultPartDifference partDifference) {
        this.parseResultPartDifferences.add(partDifference);
    }

    @Override
    public String toString() {
        return "ParseResultDifference{" +
                "\n\trootDifference=" + rootDifference +
                ", \n\tparseResultPartDifferences=\n\t\t" + Joiner.on("\n\t").join(parseResultPartDifferences) +
                "\n}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseResultDifference that = (ParseResultDifference) o;

        if (!parseResultPartDifferences.equals(that.parseResultPartDifferences))
            return false;
        if (rootDifference != null ? !rootDifference.equals(that.rootDifference) : that.rootDifference != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootDifference != null ? rootDifference.hashCode() : 0;
        result = 31 * result + (parseResultPartDifferences.hashCode());
        return result;
    }
}
