package com.github.k24.genereg;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;
import com.github.k24.genereg.registry.Registry;

import javax.annotation.Nonnull;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by k24 on 2017/01/13.
 */
public class Genereg {
    final PrimitiveStore primitiveStore;
    private final PrimitiveStore.Editor primitiveEditor;
    private PrimitiveStore.Editor begunPrimitiveEditor;

    final boolean noThrowIfUnsupportedMethod;

    Genereg(Builder builder) {
        this.primitiveStore = builder.primitiveStore;
        this.primitiveEditor = builder.primitiveEditor;
        this.noThrowIfUnsupportedMethod = false;
    }

    //region Editor
    private synchronized PrimitiveStore.Editor ensurePrimitiveEditor() {
        if (begunPrimitiveEditor == null) {
            begunPrimitiveEditor = primitiveEditor.begin();
        }
        return begunPrimitiveEditor;
    }

    private synchronized void clearPrimitiveEditor() {
        ensurePrimitiveEditor().clear();
        begunPrimitiveEditor = null;
    }

    private synchronized void applyPrimitiveEditor() {
        if (begunPrimitiveEditor != null) {
            begunPrimitiveEditor.apply();
        }
        begunPrimitiveEditor = null;
    }

    public Genereg put(Registry<String> registry, @Nonnull String value) {
        ensurePrimitiveEditor().put(registry.name(), Primitive.valueOf(value));
        return this;
    }

    public Genereg put(Registry<Boolean> registry, boolean value) {
        ensurePrimitiveEditor().put(registry.name(), Primitive.valueOf(value));
        return this;
    }

    public Genereg put(Registry<Float> registry, float value) {
        ensurePrimitiveEditor().put(registry.name(), Primitive.valueOf(value));
        return this;
    }

    public Genereg put(Registry<Integer> registry, int value) {
        ensurePrimitiveEditor().put(registry.name(), Primitive.valueOf(value));
        return this;
    }

    public Genereg put(Registry<Long> registry, long value) {
        ensurePrimitiveEditor().put(registry.name(), Primitive.valueOf(value));
        return this;
    }

    public Genereg remove(Registry<?> registry) {
        ensurePrimitiveEditor().remove(registry.name());
        return this;
    }

    public void apply() {
        applyPrimitiveEditor();
    }

    public void clear() {
        clearPrimitiveEditor();
    }
    //endregion

    //region Registry
    public <T, R extends Registry<T>> R newRegistry(Class<R> registryClass, String name, T defaultValue) {
        try {
            return (R) findRegistryConstructor(registryClass).newInstance(primitiveStore, name, defaultValue);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T, R extends Registry<T>> R newRegistry(Class<R> registryClass, String name) {
        try {
            return (R) findRegistryConstructor(registryClass).newInstance(primitiveStore, name, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    //endregion

    //region Utility
    public Map<String, Object> toMap(Class<?> registrarClass) {
        return toMap(registrarClass, newRegistrar(registrarClass));
    }

    public static Map<String, Object> toMap(@Nonnull Object registrar) {
        Class<?> registrarClass = registrar.getClass();
        if (Proxy.isProxyClass(registrarClass)) {
            registrarClass = registrarClass.getInterfaces()[0];
        }
        return toMap(registrarClass, registrar);
    }

    public static Map<String, Object> toMap(Class<?> registrarClass, @Nonnull Object registrar) {
        checkRegistrarClass(registrarClass);

        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Method method : registrarClass.getMethods()) {
            Class<?> returnType = method.getReturnType();
            if (isVoidClass(returnType)) continue;
            if (method.getParameterTypes().length > 0) continue;

            // R method()
            try {
                if (Primitive.isPrimitive(returnType)) {
                    String name = normalizeGetterName(method.getName());
                    map.put(name, method.invoke(registrar));
                } else if (Registry.class.isAssignableFrom(returnType)) {
                    if (Modifier.isAbstract(returnType.getModifiers())) throw new IllegalArgumentException();
                    Registry<?> registry = (Registry<?>) method.invoke(registrar);
                    map.put(registry.name(), registry.get());
                } else {
                    // Ignore
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        return map;
    }

    public static Map<String, Object> toMap(Registry<?>... registries) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Registry<?> registry : registries) {
            map.put(registry.name(), registry.get());
        }
        return map;
    }
    //endregion

    //region Registrar
    @SuppressWarnings("unchecked")
    public <T> T newRegistrar(Class<T> registrarClass) {
        if (primitiveStore == null) throw new IllegalStateException("No PrimitiveStore");
        checkRegistrarClass(registrarClass);

        // TODO Cache
        Class[] regInterfaces = registrarClass.getInterfaces();
        Class[] interfaces;
        if (regInterfaces.length == 0) {
            interfaces = new Class[]{registrarClass};
        } else {
            interfaces = new Class[regInterfaces.length + 1];
            interfaces[0] = registrarClass;
            System.arraycopy(regInterfaces, 0, interfaces, 1, regInterfaces.length);
        }
        T registrar = (T) Proxy.newProxyInstance(registrarClass.getClassLoader(),
                new Class[]{registrarClass},
                new RegistrarProxy(this));

        return registrar;
    }

    private static void checkRegistrarClass(Class<?> registrarClass) {
        if (registrarClass.isPrimitive() || registrarClass.isArray() || registrarClass.isEnum())
            throw new IllegalArgumentException("Never been Registrar: " + registrarClass);
    }

    private static Constructor<?> findRegistryConstructor(Class<?> type) {
        for (Constructor<?> constructor : type.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 3
                    && parameterTypes[0].equals(PrimitiveStore.class)
                    && parameterTypes[1].equals(String.class)) {
                return constructor;
            }
        }
        throw new UnsupportedOperationException("Cannot detect Registry");
    }

    private static class RegistrarProxy implements InvocationHandler {
        private final Genereg genereg;

        public RegistrarProxy(Genereg genereg) {
            this.genereg = genereg;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class<?> type = method.getReturnType();
            if (isVoidClass(type)) {
                // Setter
                if (args == null || args.length == 0) {
                    if (genereg.noThrowIfUnsupportedMethod) {
                        return null;
                    }
                    throw new UnsupportedOperationException("No operation indicated");
                }
                if (args.length > 1) {
                    if (genereg.noThrowIfUnsupportedMethod) {
                        return null;
                    }
                    throw new UnsupportedOperationException("Setter must be 1 parameter");
                }
                Object value = args[0];

                Class<?> paramType = method.getParameterTypes()[0];
                if (paramType.isPrimitive()) {
                    genereg.primitiveStore.put(normalizeSetterName(method.getName()), Primitive.valueOf(paramType, value));
                } else {
                    throw new UnsupportedOperationException("Don't set not primitive");
                }

                return null;
            } else {
                // Getter
                Object defaultValue = null;
                if (args != null) {
                    for (Object arg : args) {
                        if (arg != null && type.equals(arg.getClass())) {
                            defaultValue = arg;
                        }
                    }
                }

                // Detect
                if (Primitive.isPrimitive(type)) {
                    String name = normalizeGetterName(method.getName());
                    Primitive primitive = genereg.primitiveStore.get(name);
                    if (primitive == null) return defaultValue;
                    return type.equals(Primitive.class) ? primitive : primitive.value();
                } else if (Registry.class.isAssignableFrom(type)) {
                    if (Modifier.isAbstract(type.getModifiers()))
                        throw new UnsupportedOperationException("Cannot handle this: " + type);
                    return findRegistryConstructor(type).newInstance(genereg.primitiveStore, normalizeGetterName(method.getName()), defaultValue);
                } else {
                    throw new UnsupportedOperationException("Cannot handle this: " + type);
                }
            }
        }
    }

    private static String normalizeGetterName(String name) {
        return shouldTrip("get", name) ? name.substring(3) : name;
    }

    private static String normalizeSetterName(String name) {
        return shouldTrip("set", name) ? name.substring(3) : name;
    }

    private static boolean shouldTrip(String prefix, String name) {
        return name.startsWith(prefix) && name.length() > 3 && name.charAt(3) == Character.toUpperCase(name.charAt(3));
    }

    private static boolean isVoidClass(Class<?> type) {
        return type.equals(void.class) || type.equals(Void.class);
    }
    //endregion

    public static class Builder {
        private PrimitiveStore primitiveStore;
        private PrimitiveStore.Editor primitiveEditor;

        public Builder primitiveStore(PrimitiveStore primitiveStore) {
            this.primitiveStore = primitiveStore;
            return this;
        }

        public Builder registryStore(PrimitiveStore.Editor primitiveEditor) {
            this.primitiveEditor = primitiveEditor;
            return this;
        }

        public Genereg build() {
            return new Genereg(this);
        }
    }
}
