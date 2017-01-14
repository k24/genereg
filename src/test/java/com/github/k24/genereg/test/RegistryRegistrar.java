package com.github.k24.genereg.test;

import com.github.k24.genereg.registry.*;

/**
 * Created by k24 on 2017/01/14.
 */
public interface RegistryRegistrar {
    BooleanRegistry boolReg();

    FloatRegistry floatReg();

    IntRegistry intReg();

    LongRegistry longReg();

    StringRegistry stringReg();
}
