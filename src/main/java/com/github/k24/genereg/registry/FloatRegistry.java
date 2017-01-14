package com.github.k24.genereg.registry;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;

import javax.annotation.Nonnull;

/**
 * Created by k24 on 2017/01/14.
 */
public class FloatRegistry extends Registry<Float> {
    public FloatRegistry(@Nonnull PrimitiveStore store, @Nonnull String name, Float defaultValue) {
        super(store, name, defaultValue);
    }

    protected Primitive toPrimitive(Float value) {
        if (value == null) return null;
        return Primitive.valueOf(value);
    }
}
