package com.github.k24.genereg.registry;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;
import com.github.k24.genereg.primitive.Primitivity;

import javax.annotation.Nonnull;

/**
 * Created by k24 on 2017/01/14.
 */
public abstract class PrimitivityRegistry<T> extends Registry<T> {
    protected final Primitivity<T> primitivity;

    public PrimitivityRegistry(Primitivity<T> primitivity, @Nonnull PrimitiveStore store, @Nonnull String name, T defaultValue) {
        super(store, name, defaultValue);
        this.primitivity = primitivity;
    }

    protected Primitive toPrimitive(T value) {
        return value == null ? null : primitivity.toPrimitive(value);
    }

    @Override
    protected T fromPrimitive(Primitive primitive) {
        return primitive == null ? null : primitivity.fromPrimitive(primitive);
    }

    protected abstract T toPrimitivity(@Nonnull Primitive primitive);
}
