package org.trnltk.common.specification;

public abstract class AbstractSpecification<T> implements Specification<T> {
    public Specification<T> and(final Specification<T> specification) {
        return new AndSpecification<T>(this, specification);
    }

    public Specification<T> or(final Specification<T> specification) {
        return new OrSpecification<T>(this, specification);
    }

    public Specification<T> not() {
        return new NotSpecification<T>(this);
    }

    @Override
    public String toString() {
        return this.describe();
    }

    public abstract String describe();
}
