package carpet.utils.algebraic;

public interface AdtMatcher<T> {
    AdtMatcher<T> clear();

    AdtMatcher<T> append(Iterable<String> strings);

    int maxMatchCount();

    /**
     * @return -1 indicates a structural failure, >0 indicates a complete object,
     * 0 indicates failure by not enough arguments
     */
    int matchedCount();

    T matchedValue();


    static <T> AdtMatcher<T> create(Class<T> type) {
        // Match raw strings
        if (type == String.class) return (AdtMatcher<T>) new StringAdtMatcher();
        // Match enum types
        if (type.isEnum()) return new EnumAdtMatcher<>(type);
        // Match primitive types
        if (PrimitiveAdtMatcher.tryBoxClass(type) != null)
            return new PrimitiveAdtMatcher<>(type);
        // Match BlockPos, ChunkPos, Vec2f, Vec3d, Vec3i
        VectorAdtMatcher<T> vectorAdtMatcher;
        if ((vectorAdtMatcher = VectorAdtMatcher.tryCreate(type)) != null)
            return vectorAdtMatcher;
        return new StructuredAdtMatcher<>(type);
    }
}
