package com.github.k24.genereg.test;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;
import com.github.k24.genereg.primitive.Primitivity;
import com.github.k24.genereg.registry.PrimitivityRegistry;

/**
 * Created by k24 on 2017/01/14.
 */
public interface PrimitivityRegistrar {

    DoubleRegistry doubleReg();

    class DoubleRegistry extends PrimitivityRegistry<Double> {

        public DoubleRegistry(Primitivity<Double> primitivity, PrimitiveStore store, String name, Double defaultValue) {
            super(primitivity, store, name, defaultValue);
        }
    }

    class Factory implements Primitivity.Factory {
        @Override
        public <T> Primitivity<T> primitivity(Class<T> valueType) {
            return (Primitivity<T>) new Primitivity<Double>() {
                @Override
                public Primitive toPrimitive(Double value) {
                    return Primitive.valueOf(String.valueOf(value));
                }

                @Override
                public Double fromPrimitive(Primitive primitive) {
                    return Double.valueOf(String.valueOf(primitive.value()));
                }
            };
        }
    }
}
