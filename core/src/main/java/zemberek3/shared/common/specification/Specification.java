package zemberek3.shared.common.specification;

// http://en.wikipedia.org/wiki/Specification_pattern
public interface Specification<T> {

    boolean isSatisfiedBy(T object);

    Specification<T> and(Specification<T> other);

    Specification<T> not();

    Specification<T> or(Specification<T> other);
}