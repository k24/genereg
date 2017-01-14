package com.github.k24.genereg.registry;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;

import javax.annotation.Nonnull;

/**
 * Created by k24 on 2017/01/13.
 */
public abstract class Registry<T> {
    private final PrimitiveStore store;
    private final String name;
    private final T defaultValue;

    public Registry(@Nonnull PrimitiveStore store, @Nonnull String name, T defaultValue) {
        this.store = store;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    protected abstract Primitive toPrimitive(T value);

    @SuppressWarnings("unchecked")
    protected T fromPrimitive(Primitive primitive) {
        return primitive == null ? null : (T) primitive.value();
    }

    public final synchronized T put(T value) {
        Primitive primitive = store.get(name);
        store.put(name, toPrimitive(value));
        return fromPrimitiveOrNull(primitive);
    }

    public final synchronized T get() {
        Primitive primitive = store.get(name);
        return primitive == null ? defaultValue : fromPrimitive(primitive);
    }

    public final synchronized T remove() {
        Primitive primitive = store.get(name);
        store.remove(name);
        return fromPrimitiveOrNull(primitive);
    }

    public final String name() {
        return name;
    }

    public final T defaultValue() {
        return defaultValue;
    }

    private final T fromPrimitiveOrNull(Primitive primitive) {
        return primitive == null ? null : fromPrimitive(primitive);
    }
}
