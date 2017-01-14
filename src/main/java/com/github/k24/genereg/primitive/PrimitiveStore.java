package com.github.k24.genereg.primitive;

/**
 * Created by k24 on 2017/01/13.
 */
public interface PrimitiveStore {
    void put(String name, Primitive primitive);

    Primitive get(String name);

    void remove(String name);

    interface Editor {
        Editor begin();

        Editor put(String name, Primitive primitive);

        Editor remove(String name);

        void clear();

        void apply();
    }
}
