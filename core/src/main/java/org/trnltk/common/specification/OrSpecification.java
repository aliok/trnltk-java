package org.trnltk.common.specification;

public class OrSpecification<T> extends AbstractSpecification<T> {
    private Specification<T> spec1;
    private Specification<T> spec2;

    public OrSpecification(final Specification<T> spec1, final Specification<T> spec2) {
        this.spec1 = spec1;
        this.spec2 = spec2;
    }

    @Override
    public boolean isSatisfiedBy(final T object) {
        return spec1.isSatisfiedBy(object) || spec2.isSatisfiedBy(object);
    }

    @Override
    public String describe() {
        return spec1.toString() + " OR " + spec2.toString();
    }

}
