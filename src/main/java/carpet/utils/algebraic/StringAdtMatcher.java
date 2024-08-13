package carpet.utils.algebraic;

public class StringAdtMatcher extends SingleAdtMatcher<String> {
    @Override
    String parseDirectly(String raw) {
        return raw;
    }
}
