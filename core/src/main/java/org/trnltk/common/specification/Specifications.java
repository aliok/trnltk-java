package org.trnltk.common.specification;

@SuppressWarnings("unchecked")
public abstract class Specifications {
    public static <T> Specification<T> or(Specification<T>... specifications) {
        Specification<T> returnValue = (Specification<T>)FalseSpecification.INSTANCE;
        for (Specification specification : specifications) {
            returnValue = returnValue.or(specification);
        }
        return returnValue;
    }

    public static <T> Specification<T> and(Specification<T>... specifications) {
        Specification<T> returnValue = (Specification<T>)TrueSpecification.INSTANCE;
        for (Specification<T> specification : specifications) {
            returnValue = returnValue.and(specification);
        }
        return returnValue;
    }
}
