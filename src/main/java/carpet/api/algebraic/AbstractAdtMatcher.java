package carpet.api.algebraic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractAdtMatcher<T> implements AdtMatcher<T> {
    protected final List<String> data = new ArrayList<>();

    @Override
    public AdtMatcher<T> clear() {
        data.clear();
        return this;
    }

    @Override
    public AdtMatcher<T> append(Iterable<String> strings) {
        if (strings instanceof Collection) {
            data.addAll((Collection<? extends String>) strings);
        } else {
            for (String str : strings) data.add(str);
        }
        return this;
    }
}
