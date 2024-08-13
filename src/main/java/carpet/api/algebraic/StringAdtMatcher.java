package carpet.api.algebraic;

public class StringAdtMatcher extends SingleAdtMatcher<String> {
    @Override
    String parseDirectly(String raw) {
        return raw;
    }
}
