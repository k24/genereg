package com.github.k24.genereg.registry;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;

import javax.annotation.Nonnull;

/**
 * Created by k24 on 2017/01/13.
 */
public class IntRegistry extends Registry<Integer> {
    public IntRegistry(@Nonnull PrimitiveStore store, @Nonnull String name, Integer defaultValue) {
        super(store, name, defaultValue);
    }

    protected Primitive toPrimitive(Integer value) {
        if (value == null) return null;
        return Primitive.valueOf(value);
    }
}
