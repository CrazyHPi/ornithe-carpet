package carpet.utils;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ReflectionUtil {
    private static final Map<String, Optional<Class<?>>> classCache = new ConcurrentHashMap<>();

    public static Optional<Class<?>> getClass(String name) {
        return classCache.computeIfAbsent(name, k -> {
            try {
                return Optional.of(Class.forName(name));
            } catch (ClassNotFoundException e) {
                return Optional.empty();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getObjectField(Object o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return Optional.of((T) field.get(o));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
