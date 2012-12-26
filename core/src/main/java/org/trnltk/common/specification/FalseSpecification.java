package org.trnltk.common.specification;

public class FalseSpecification<T> extends AbstractSpecification<T> {
    public static FalseSpecification INSTANCE = new FalseSpecification();

    private FalseSpecification() {
    }

    @Override
    public String describe() {
        return "FALSE";
    }

    @Override
    public boolean isSatisfiedBy(T object) {
        return false;
    }
}
