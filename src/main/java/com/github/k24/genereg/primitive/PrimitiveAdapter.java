package com.github.k24.genereg.primitive;

/**
 * Created by k24 on 2017/01/14.
 */
public interface PrimitiveAdapter {
    <T> Primitivity<T> primitivity(Class<T> valueType);
}
