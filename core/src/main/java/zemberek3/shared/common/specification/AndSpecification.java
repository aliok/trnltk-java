package zemberek3.shared.common.specification;

public class AndSpecification<T> extends AbstractSpecification<T> {
    private final Specification<T> spec1;
    private final Specification<T> spec2;

    public AndSpecification(final Specification<T> spec1, final Specification<T> spec2) {
        this.spec1 = spec1;
        this.spec2 = spec2;
    }

    @Override
    public boolean isSatisfiedBy(T object) {
        return spec1.isSatisfiedBy(object) && spec2.isSatisfiedBy(object);
    }

    @Override
    public String describe() {
        return spec1.toString() + " AND " + spec2.toString();
    }
}
