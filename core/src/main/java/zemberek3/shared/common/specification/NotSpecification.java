package zemberek3.shared.common.specification;

public class NotSpecification<T> extends AbstractSpecification<T> {
    private final Specification<T> wrapped;

    public NotSpecification(Specification<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isSatisfiedBy(T object) {
        return !wrapped.isSatisfiedBy(object);
    }

    @Override
    public String describe() {
        return "NOT " + wrapped.toString();
    }
}
