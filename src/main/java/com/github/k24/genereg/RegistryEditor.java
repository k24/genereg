package com.github.k24.genereg;

import com.github.k24.genereg.registry.PrimitivityRegistry;
import com.github.k24.genereg.registry.Registry;

import javax.annotation.Nonnull;

/**
 * Created by k24 on 2017/02/10.
 */
public interface RegistryEditor {
    RegistryEditor put(Registry<String> registry, @Nonnull String value);

    RegistryEditor put(Registry<Boolean> registry, boolean value);

    RegistryEditor put(Registry<Float> registry, float value);

    RegistryEditor put(Registry<Integer> registry, int value);

    RegistryEditor put(Registry<Long> registry, long value);

    <T> RegistryEditor put(PrimitivityRegistry<T> registry, @Nonnull T value);

    RegistryEditor remove(Registry<?> registry);

    void apply();

    void clear();
}
