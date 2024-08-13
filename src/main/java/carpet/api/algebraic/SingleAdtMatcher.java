package carpet.api.algebraic;

public abstract class SingleAdtMatcher<T> extends AbstractAdtMatcher<T> {
    protected T parsedValue;

    abstract T parseDirectly(String raw);

    @Override
    public AdtMatcher<T> clear() {
        super.clear();
        parsedValue = null;
        return this;
    }

    @Override
    public AdtMatcher<T> append(Iterable<String> strings) {
        super.append(strings);
        parsedValue = null;
        if (!data.isEmpty()) parsedValue = parseDirectly(data.get(0));
        return this;
    }

    @Override
    public int maxMatchCount() {
        return 1;
    }

    @Override
    public int matchedCount() {
        if (data.isEmpty()) return 0;
        return parsedValue == null ? -1 : 1;
    }

    @Override
    public T matchedValue() {
        return parsedValue;
    }
}
