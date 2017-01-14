package com.github.k24.genereg.primitive;

/**
 * Created by k24 on 2017/01/14.
 */
public interface Primitivity<T> {
    Primitive toPrimitive(T value);

    T fromPrimitive(Primitive primitive);

    interface Factory {
        <T> Primitivity<T> primitivity(Class<T> valueType);
    }
}
