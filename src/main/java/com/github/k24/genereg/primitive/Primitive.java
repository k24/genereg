package com.github.k24.genereg.primitive;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by k24 on 2017/01/13.
 */
public final class Primitive {
    private static final Class<?>[] CLASSES = {String.class, int.class, long.class, float.class, boolean.class, Primitive.class,
            Integer.class, Long.class, Boolean.class, Float.class};
    private final Class<?> valueClass;
    private final Object value;

    Primitive(Class<?> valueClass, Object value) {
        this.valueClass = valueClass;
        this.value = value;
    }

    public static Primitive valueOf(String value) {
        return CacheHolder.getOrNew(String.class, value);
    }

    public static Primitive valueOf(Integer value) {
        return CacheHolder.getOrNew(Integer.class, value);
    }

    public static Primitive valueOf(Long value) {
        return CacheHolder.getOrNew(Long.class, value);
    }

    public static Primitive valueOf(Float value) {
        return CacheHolder.getOrNew(float.class, value);
    }

    public static Primitive valueOf(Boolean value) {
        return CacheHolder.getOrNew(boolean.class, value);
    }

    public static Primitive valueOf(int value) {
        return CacheHolder.getOrNew(int.class, value);
    }

    public static Primitive valueOf(long value) {
        return CacheHolder.getOrNew(long.class, value);
    }

    public static Primitive valueOf(float value) {
        return CacheHolder.getOrNew(float.class, value);
    }

    public static Primitive valueOf(boolean value) {
        return CacheHolder.getOrNew(boolean.class, value);
    }

    public static Primitive valueOf(@Nonnull Object object) {
        return valueOf(object.getClass(), object);
    }

    public static Primitive valueOf(Class<?> primitiveClass, Object value) {
        if (primitiveClass.equals(Primitive.class)) return (Primitive) value;
        if (!isPrimitive(primitiveClass)) throw new IllegalArgumentException("Not Primitive: " + primitiveClass);
        return CacheHolder.getOrNew(primitiveClass, value);
    }

    public static void compact() {
        CacheHolder.clear();
    }

    public Class<?> valueClass() {
        return valueClass;
    }

    public Object value() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T valueOr(T defaultValue) {
        return value == null ? defaultValue : (T) value;
    }

    public static boolean isPrimitive(Class<?> clazz) {
        for (Class<?> c : CLASSES) {
            if (c.equals(clazz)) return true;
        }
        return false;
    }

    public static List<Class<?>> classes() {
        return Arrays.asList(CLASSES);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Primitive primitive = (Primitive) o;

        if (valueClass != null ? !valueClass.equals(primitive.valueClass) : primitive.valueClass != null) return false;
        return value != null ? value.equals(primitive.value) : primitive.value == null;
    }

    @Override
    public int hashCode() {
        int result = valueClass != null ? valueClass.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return valueClass + ": " + value;
    }

    private static class CacheHolder {
        private static final Map<Object, Primitive> MAP = new HashMap<Object, Primitive>();

        public static synchronized Primitive getOrNew(Class<?> clazz, Object value) {
            Map<Object, Primitive> map = MAP;
            Primitive primitive = map.get(value);
            if (primitive == null) {
                primitive = new Primitive(clazz, value);
                map.put(value, primitive);
            }
            return primitive;
        }

        public static synchronized void clear() {
            MAP.clear();
        }
    }
}
