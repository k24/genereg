package com.github.k24.genereg.registry;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;

import javax.annotation.Nonnull;

/**
 * Created by k24 on 2017/01/14.
 */
public class LongRegistry extends Registry<Long> {
    public LongRegistry(@Nonnull PrimitiveStore store, @Nonnull String name, Long defaultValue) {
        super(store, name, defaultValue);
    }

    protected Primitive toPrimitive(Long value) {
        if (value == null) return null;
        return Primitive.valueOf(value);
    }
}
