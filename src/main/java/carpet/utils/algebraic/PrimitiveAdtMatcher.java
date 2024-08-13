package carpet.utils.algebraic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class PrimitiveAdtMatcher<T> extends SingleAdtMatcher<T> {
    private final Method valueOf;

    private static final Map<Class<?>, Class<?>> BOX_MAP = createBoxingMap();

    private static Map<Class<?>, Class<?>> createBoxingMap() {
        Map<Class<?>, Class<?>> map = new LinkedHashMap<>();
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        map.put(char.class, Character.class);
        map.put(boolean.class, Boolean.class);
        return map;
    }

    public static <T> Class<T> tryBoxClass(Class<T> type) {
        if (type.isPrimitive()) return (Class<T>) BOX_MAP.get(type);
        else if (BOX_MAP.containsValue(type)) return type;
        return null;
    }

    public PrimitiveAdtMatcher(Class<T> type) {
        type = tryBoxClass(type);
        if (type == null) throw new AssertionError("Creating PrimitiveAdtMatcher for non-primitive or boxed class");
        try {
            valueOf = type.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public T parseDirectly(String raw) {
        try {
            return (T) valueOf.invoke(null, raw);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            // ill-formatted number
            return null;
        }
    }
}
