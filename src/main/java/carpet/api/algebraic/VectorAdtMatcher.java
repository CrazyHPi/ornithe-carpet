package carpet.api.algebraic;

import net.minecraft.util.math.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class VectorAdtMatcher<T> extends AbstractAdtMatcher<T> {
    private final Constructor<T> constructor;
    private final int size;
    private final PrimitiveAdtMatcher<?> primitiveMatcher;

    private Object[] values;
    private T matchedValue;

    public static <T> VectorAdtMatcher<T> tryCreate(Class<T> type) {
        Class<?> primitive = null;
        int size = 0;
        if (type == BlockPos.class) {
            primitive = int.class;
            size = 3;
        } else if (type == ChunkPos.class) {
            primitive = int.class;
            size = 2;
        } else if (type == Vec2f.class) {
            primitive = float.class;
            size = 2;
        } else if (type == Vec3d.class) {
            primitive = double.class;
            size = 3;
        } else if (type == Vec3i.class) {
            primitive = int.class;
            size = 3;
        }
        if (primitive != null) {
            return new VectorAdtMatcher<>(type, primitive, size);
        }
        return null;
    }

    public VectorAdtMatcher(Class<T> type, Class<?> primitiveType, int size) {
        if (!primitiveType.isPrimitive())
            throw new IllegalArgumentException("Creating VectorAdtMatcher with non-primitive component");
        this.primitiveMatcher = new PrimitiveAdtMatcher<>(primitiveType);
        this.size = size;
        Class[] constructorParams = new Class[size];
        Arrays.fill(constructorParams, primitiveType);
        try {
            this.constructor = type.getConstructor(constructorParams);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Specified vector type does not have a constructor with specified number of params");
        }
        this.values = new Object[size];
    }

    @Override
    public AdtMatcher<T> clear() {
        super.clear();
        matchedValue = null;
        return this;
    }

    @Override
    public AdtMatcher<T> append(Iterable<String> strings) {
        super.append(strings);
        matchedValue = null;
        if (data.size() >= size) {
            for (int i = 0; i < size; i++) {
                Object primitive = primitiveMatcher.parseDirectly(data.get(i));
                if (primitive == null) break;
                values[i] = primitive;
            }
            try {
                matchedValue = constructor.newInstance(values);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new AssertionError(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return this;
    }

    @Override
    public int maxMatchCount() {
        return size;
    }

    @Override
    public int matchedCount() {
        if (data.size() < size) return 0;
        return matchedValue == null ? -1 : size;
    }

    @Override
    public T matchedValue() {
        return matchedValue;
    }
}
