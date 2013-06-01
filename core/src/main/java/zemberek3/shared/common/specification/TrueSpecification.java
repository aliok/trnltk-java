package zemberek3.shared.common.specification;

public class TrueSpecification<T> extends AbstractSpecification<T> {
    public static TrueSpecification INSTANCE = new TrueSpecification();

    private TrueSpecification() {
    }

    @Override
    public String describe() {
        return "TRUE";
    }

    @Override
    public boolean isSatisfiedBy(T object) {
        return true;
    }
}
