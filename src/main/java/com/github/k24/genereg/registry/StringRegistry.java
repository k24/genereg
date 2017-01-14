package com.github.k24.genereg.registry;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;

import javax.annotation.Nonnull;

/**
 * Created by k24 on 2017/01/13.
 */
public class StringRegistry extends Registry<String> {
    public StringRegistry(@Nonnull PrimitiveStore store, @Nonnull String name, String defaultValue) {
        super(store, name, defaultValue);
    }

    protected Primitive toPrimitive(String value) {
        if (value == null) return null;
        return Primitive.valueOf(value);
    }
}
