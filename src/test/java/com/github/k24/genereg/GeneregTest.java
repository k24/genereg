package com.github.k24.genereg;

import com.github.k24.genereg.primitive.Primitive;
import com.github.k24.genereg.primitive.PrimitiveStore;
import com.github.k24.genereg.registry.*;
import com.github.k24.genereg.test.*;
import org.assertj.core.api.Condition;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by k24 on 2017/01/14.
 */
public class GeneregTest {

    private PrimitiveStore mockPrimitiveStore;
    private PrimitiveStore.Editor mockPrimitiveEditor;
    private Genereg genereg;

    @org.junit.Before
    public void setUp() throws Exception {
        mockPrimitiveStore = mock(PrimitiveStore.class);
        mockPrimitiveEditor = mock(PrimitiveStore.Editor.class);
        genereg = new Genereg.Builder()
                .primitiveStore(mockPrimitiveStore)
                .primitiveEditor(mockPrimitiveEditor)
                .addPrimitivityFactory(new PrimitivityRegistrar.Factory())
                .build();
    }

    @org.junit.Test
    public void apply() throws Exception {
        // Prepare
        when(mockPrimitiveEditor.begin()).thenReturn(mockPrimitiveEditor);

        // Run
        genereg
                .put(genereg.newRegistry(BooleanRegistry.class, "boolean"), true)
                .put(genereg.newRegistry(FloatRegistry.class, "float"), 123f)
                .put(genereg.newRegistry(IntRegistry.class, "int"), 456)
                .put(genereg.newRegistry(LongRegistry.class, "long"), 789L)
                .put(genereg.newRegistry(StringRegistry.class, "string"), "value")
                .apply();

        // Verify
        verify(mockPrimitiveEditor, times(1)).begin();
        verify(mockPrimitiveEditor, times(1)).put("boolean", Primitive.valueOf(true));
        verify(mockPrimitiveEditor, times(1)).put("float", Primitive.valueOf(123f));
        verify(mockPrimitiveEditor, times(1)).put("int", Primitive.valueOf(456));
        verify(mockPrimitiveEditor, times(1)).put("long", Primitive.valueOf(789L));
        verify(mockPrimitiveEditor, times(1)).put("string", Primitive.valueOf("value"));
        verify(mockPrimitiveEditor, times(1)).apply();
    }

    @org.junit.Test
    public void clear() throws Exception {
        // Prepare
        when(mockPrimitiveEditor.begin()).thenReturn(mockPrimitiveEditor);

        // Run
        genereg.clear();

        // Verify
        verify(mockPrimitiveEditor, times(1)).begin();
        verify(mockPrimitiveEditor, times(1)).clear();
    }

    @org.junit.Test
    public void newRegistry() throws Exception {
        // Prepare
        when(mockPrimitiveStore.get("string")).thenReturn(Primitive.valueOf("value"));
        when(mockPrimitiveStore.get("int")).thenReturn(Primitive.valueOf(123));

        // Run
        StringRegistry stringRegistry = genereg.newRegistry(StringRegistry.class, "string");
        IntRegistry intRegistry = genereg.newRegistry(IntRegistry.class, "int");

        // Verify
        assertThat(stringRegistry)
                .isNotNull()
                .has(new Condition<StringRegistry>() {
                    @Override
                    public boolean matches(StringRegistry stringRegistry) {
                        return "string".equals(stringRegistry.name())
                                && stringRegistry.defaultValue() == null
                                && stringRegistry.get().equals("value");
                    }
                });
        assertThat(intRegistry)
                .isNotNull()
                .has(new Condition<IntRegistry>() {
                    @Override
                    public boolean matches(IntRegistry intRegistry) {
                        return "int".equals(intRegistry.name())
                                && intRegistry.defaultValue() == null
                                && intRegistry.get().equals(123);
                    }
                });
    }

    @org.junit.Test
    public void newRegistry_withDefaultValue() throws Exception {
        // Prepare
        when(mockPrimitiveStore.get("string")).thenReturn(null);
        when(mockPrimitiveStore.get("int")).thenReturn(null);

        // Run
        StringRegistry stringRegistry = genereg.newRegistry(StringRegistry.class, "string", "value");
        IntRegistry intRegistry = genereg.newRegistry(IntRegistry.class, "int", 123);

        // Verify
        assertThat(stringRegistry)
                .isNotNull()
                .has(new Condition<StringRegistry>() {
                    @Override
                    public boolean matches(StringRegistry stringRegistry) {
                        return "string".equals(stringRegistry.name())
                                && stringRegistry.defaultValue().equals("value")
                                && stringRegistry.get().equals("value");
                    }
                });
        assertThat(intRegistry)
                .isNotNull()
                .has(new Condition<IntRegistry>() {
                    @Override
                    public boolean matches(IntRegistry intRegistry) {
                        return "int".equals(intRegistry.name())
                                && intRegistry.defaultValue().equals(123)
                                && intRegistry.get().equals(123);
                    }
                });
    }

    @org.junit.Test
    public void toMap() throws Exception {
        // Prepare
        when(mockPrimitiveStore.get("string")).thenReturn(Primitive.valueOf("value"));
        when(mockPrimitiveStore.get("int")).thenReturn(Primitive.valueOf(123));

        // Run
        Map<String, Object> map = Genereg.toMap(
                genereg.newRegistry(StringRegistry.class, "string"),
                genereg.newRegistry(IntRegistry.class, "int")
        );

        // Verify
        assertThat(map)
                .isNotNull()
                .containsEntry("string", "value")
                .containsEntry("int", 123);
    }

    @org.junit.Test
    public void toMap_registrar() throws Exception {
        // Prepare
        when(mockPrimitiveStore.get("intReg")).thenReturn(Primitive.valueOf(456));
        when(mockPrimitiveStore.get("stringReg")).thenReturn(Primitive.valueOf("value"));

        when(mockPrimitiveStore.get("boolValue")).thenReturn(Primitive.valueOf(false));
        when(mockPrimitiveStore.get("floatValue")).thenReturn(Primitive.valueOf(12f));
        when(mockPrimitiveStore.get("intValue")).thenReturn(Primitive.valueOf(34));
        when(mockPrimitiveStore.get("longValue")).thenReturn(Primitive.valueOf(56L));
        when(mockPrimitiveStore.get("stringValue")).thenReturn(Primitive.valueOf("78"));

        when(mockPrimitiveStore.get("boolObjValue")).thenReturn(Primitive.valueOf(true));
        when(mockPrimitiveStore.get("floatObjValue")).thenReturn(Primitive.valueOf(1f));
        when(mockPrimitiveStore.get("intObjValue")).thenReturn(Primitive.valueOf(2));
        when(mockPrimitiveStore.get("longObjValue")).thenReturn(Primitive.valueOf(3L));
        when(mockPrimitiveStore.get("primitiveValue")).thenReturn(Primitive.valueOf(4));

        // Run
        Map<String, Object> map = Genereg.toMap(genereg.newRegistrar(ExtendedRegistrar.class));

        // Verify
        assertThat(map)
                .isNotNull()
                .containsEntry("intReg", 456)
                .containsEntry("stringReg", "value")
                .containsEntry("boolValue", false)
                .containsEntry("floatValue", 12f)
                .containsEntry("intValue", 34)
                .containsEntry("longValue", 56L)
                .containsEntry("stringValue", "78")
                .containsEntry("boolObjValue", true)
                .containsEntry("floatObjValue", 1f)
                .containsEntry("intObjValue", 2)
                .containsEntry("longObjValue", 3L)
                .containsEntry("primitiveValue", Primitive.valueOf(4));
    }

    @org.junit.Test
    public void newRegistrar() throws Exception {
        // Prepare
        when(mockPrimitiveStore.get("boolReg")).thenReturn(Primitive.valueOf(true));
        when(mockPrimitiveStore.get("floatReg")).thenReturn(Primitive.valueOf(123f));
        when(mockPrimitiveStore.get("intReg")).thenReturn(Primitive.valueOf(456));
        when(mockPrimitiveStore.get("longReg")).thenReturn(Primitive.valueOf(789L));
        when(mockPrimitiveStore.get("stringReg")).thenReturn(Primitive.valueOf("value"));

        when(mockPrimitiveStore.get("boolValue")).thenReturn(Primitive.valueOf(false));
        when(mockPrimitiveStore.get("floatValue")).thenReturn(Primitive.valueOf(12f));
        when(mockPrimitiveStore.get("intValue")).thenReturn(Primitive.valueOf(34));
        when(mockPrimitiveStore.get("longValue")).thenReturn(Primitive.valueOf(56L));
        when(mockPrimitiveStore.get("stringValue")).thenReturn(Primitive.valueOf("78"));

        when(mockPrimitiveStore.get("boolObjValue")).thenReturn(Primitive.valueOf(true));
        when(mockPrimitiveStore.get("floatObjValue")).thenReturn(Primitive.valueOf(1f));
        when(mockPrimitiveStore.get("intObjValue")).thenReturn(Primitive.valueOf(2));
        when(mockPrimitiveStore.get("longObjValue")).thenReturn(Primitive.valueOf(3L));
        when(mockPrimitiveStore.get("primitiveValue")).thenReturn(Primitive.valueOf(4));

        // Run
        RegistryRegistrar registryRegistrar = genereg.newRegistrar(RegistryRegistrar.class);
        ComplexRegistrar complexRegistrar = genereg.newRegistrar(ComplexRegistrar.class);

        // Verify
        assertThat(registryRegistrar.boolReg().get()).isTrue();
        assertThat(registryRegistrar.floatReg().get()).isEqualTo(123f);
        assertThat(registryRegistrar.intReg().get()).isEqualTo(456);
        assertThat(registryRegistrar.longReg().get()).isEqualTo(789L);
        assertThat(registryRegistrar.stringReg().get()).isEqualTo("value");

        assertThat(complexRegistrar.stringReg().get()).isEqualTo("value");
        assertThat(complexRegistrar.intReg().get()).isEqualTo(456);
        assertThat(complexRegistrar.boolValue()).isFalse();
        assertThat(complexRegistrar.floatValue()).isEqualTo(12f);
        assertThat(complexRegistrar.intValue()).isEqualTo(34);
        assertThat(complexRegistrar.longValue()).isEqualTo(56L);
        assertThat(complexRegistrar.stringValue()).isEqualTo("78");
        assertThat(complexRegistrar.boolObjValue()).isTrue();
        assertThat(complexRegistrar.floatObjValue()).isEqualTo(1f);
        assertThat(complexRegistrar.intObjValue()).isEqualTo(2);
        assertThat(complexRegistrar.longObjValue()).isEqualTo(3L);
        assertThat(complexRegistrar.primitiveValue()).isEqualTo(Primitive.valueOf(4));
    }

    @org.junit.Test
    public void newRegistrar_getterSetter() throws Exception {
        // Prepare
        when(mockPrimitiveStore.get("boolValue")).thenReturn(null);
        when(mockPrimitiveStore.get("floatValue")).thenReturn(null);
        when(mockPrimitiveStore.get("intValue")).thenReturn(null);
        when(mockPrimitiveStore.get("longValue")).thenReturn(null);
        when(mockPrimitiveStore.get("stringValue")).thenReturn(null);

        // Run
        GetterSetterRegistrar registrar = genereg.newRegistrar(GetterSetterRegistrar.class);

        // Verify
        assertThat(registrar.boolValue(false)).isFalse();
        assertThat(registrar.floatValue(12f)).isEqualTo(12f);
        assertThat(registrar.intValue(34)).isEqualTo(34);
        assertThat(registrar.longValue(56L)).isEqualTo(56L);
        assertThat(registrar.stringValue("value")).isEqualTo("value");

        // Prepare
        when(mockPrimitiveStore.get("boolValue")).thenReturn(Primitive.valueOf(true));
        when(mockPrimitiveStore.get("floatValue")).thenReturn(Primitive.valueOf(12f));
        when(mockPrimitiveStore.get("intValue")).thenReturn(Primitive.valueOf(34));
        when(mockPrimitiveStore.get("longValue")).thenReturn(Primitive.valueOf(56L));
        when(mockPrimitiveStore.get("stringValue")).thenReturn(Primitive.valueOf("78"));

        // Run
        registrar.setBoolValue(true);
        registrar.setFloatValue(12f);
        registrar.setIntValue(34);
        registrar.setLongValue(56L);
        registrar.setStringValue("78");

        // Verify
        assertThat(registrar.boolValue(false)).isTrue();
        assertThat(registrar.floatValue(0)).isEqualTo(12f);
        assertThat(registrar.intValue(0)).isEqualTo(34);
        assertThat(registrar.longValue(0)).isEqualTo(56L);
        assertThat(registrar.stringValue(null)).isEqualTo("78");
        assertThat(registrar.getBoolValue()).isTrue();
        assertThat(registrar.getFloatValue()).isEqualTo(12f);
        assertThat(registrar.getIntValue()).isEqualTo(34);
        assertThat(registrar.getLongValue()).isEqualTo(56L);
        assertThat(registrar.getStringValue()).isEqualTo("78");
        verify(mockPrimitiveStore).put("boolValue", Primitive.valueOf(true));
        verify(mockPrimitiveStore).put("floatValue", Primitive.valueOf(12f));
        verify(mockPrimitiveStore).put("intValue", Primitive.valueOf(34));
        verify(mockPrimitiveStore).put("longValue", Primitive.valueOf(56L));
        verify(mockPrimitiveStore).put("stringValue", Primitive.valueOf("78"));

    }

    @org.junit.Test
    public void newRegistrar_primitivity() throws Exception {
        // Prepare
        when(mockPrimitiveStore.get("doubleReg")).thenReturn(Primitive.valueOf("0.0"));

        // Run
        PrimitivityRegistrar primitivityRegistrar = genereg.newRegistrar(PrimitivityRegistrar.class);

        // Verify
        assertThat(primitivityRegistrar.doubleReg().get()).isEqualTo(0.0);
    }
}