package com.github.k24.genereg.test;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.registry.IntRegistry;
import com.github.k24.genereg.registry.StringRegistry;

/**
 * Created by k24 on 2017/01/14.
 */
public interface ComplexRegistrar {
    IntRegistry intReg();

    StringRegistry stringReg();

    boolean boolValue();

    float floatValue();

    int intValue();

    long longValue();

    String stringValue();

    Boolean boolObjValue();

    Float floatObjValue();

    Integer intObjValue();

    Long longObjValue();

    Primitive primitiveValue();
}
