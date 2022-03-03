package me.lauriichan.minecraft.minigame.config;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import me.lauriichan.minecraft.minigame.util.JavaAccessor;

final class ConfigSync {

    private final Object instance;

    private final Field field;
    private final VarHandle handle;

    private final String path;
    private final String key;

    private final Object fallback;
    private final boolean _static;

    private final Class<?> type;
    private final boolean typeNumber;

    public ConfigSync(final Object instance, final Field field) {
        Config config = JavaAccessor.getAnnotation(field, Config.class);
        if (config == null) {
            throw new IllegalArgumentException("Field needs @Config annotation");
        }
        this.path = config.path();
        this.key = config.key();
        this.instance = instance;
        this.field = Objects.requireNonNull(field);
        this.type = field.getType();
        this.typeNumber = Number.class.isAssignableFrom(type);
        this.handle = JavaAccessor.accessField(field, true);
        Object tmp;
        if (_static = Modifier.isStatic(field.getModifiers())) {
            tmp = JavaAccessor.getStaticValue(handle);
        } else {
            tmp = JavaAccessor.getValue(instance, handle);
        }
        this.fallback = tmp;
    }

    public String getPath() {
        return path;
    }

    public String getKey() {
        return key;
    }

    public Object getFallback() {
        return fallback;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }

    public VarHandle getHandle() {
        return handle;
    }

    public Object getInstance() {
        return instance;
    }

    public void update(Object value) {
        Object set = getValue(value);
        if (_static) {
            JavaAccessor.setStaticValue(handle, set);
            return;
        }
        JavaAccessor.setValue(instance, handle, set);
    }

    private Object getValue(Object value) {
        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                return value;
            }
            if (Number.class.isAssignableFrom(value.getClass()) && typeNumber) {
                Class<?> complex = Primitives.fromPrimitive(type);
                Number number = (Number) value;
                if (complex == Byte.class) {
                    return number.byteValue();
                } else if (complex == Short.class) {
                    return number.shortValue();
                } else if (complex == Integer.class) {
                    return number.intValue();
                } else if (complex == Long.class) {
                    return number.longValue();
                } else if (complex == Float.class) {
                    return number.floatValue();
                } else if (complex == Double.class) {
                    return number.doubleValue();
                } else if (complex == BigInteger.class) {
                    return BigInteger.valueOf(number.longValue());
                } else if (complex == BigDecimal.class) {
                    return BigDecimal.valueOf(number.doubleValue());
                }
                return fallback;
            }
        }
        return fallback;
    }

}
