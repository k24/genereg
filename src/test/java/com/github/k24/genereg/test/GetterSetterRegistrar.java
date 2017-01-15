package com.github.k24.genereg.test;

/**
 * Created by k24 on 2017/01/15.
 */
public interface GetterSetterRegistrar {

    boolean boolValue(boolean defaultValue);

    float floatValue(float defaultValue);

    int intValue(int defaultValue);

    long longValue(long defaultValue);

    String stringValue(String defaultValue);

    boolean getBoolValue();

    float getFloatValue();

    int getIntValue();

    long getLongValue();

    String getStringValue();

    void setBoolValue(boolean defaultValue);

    void setFloatValue(float defaultValue);

    void setIntValue(int defaultValue);

    void setLongValue(long defaultValue);

    void setStringValue(String defaultValue);
}
