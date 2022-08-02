package me.lauriichan.minecraft.minigame.data.automatic.config;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;

import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import me.lauriichan.minecraft.minigame.util.JavaAccess;

final class ConfigSync implements IConfigSync {

    private final Object instance;

    private final Field field;

    private final String path;
    private final String key;

    private final Object fallback;

    private final Class<?> type;
    private final boolean typeNumber;

    public ConfigSync(final Object instance, final Field field) {
        Config config = JavaAccess.getAnnotation(field, Config.class);
        if (config == null) {
            throw new IllegalArgumentException("Field needs @Config annotation");
        }
        this.path = config.path();
        this.key = config.key();
        this.instance = instance;
        this.field = Objects.requireNonNull(field);
        this.type = field.getType();
        this.typeNumber = Number.class.isAssignableFrom(type);
        Object tmp = JavaAccess.getValue(instance, field);
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

    public Object getInstance() {
        return instance;
    }

    @Override
    public boolean setFallback(YamlConfiguration configuration, String key) {
        configuration.set(key, fallback);
        return false;
    }

    public void update(Object value) {
        JavaAccess.setValue(instance, field, getValue(value));
    }

    @SuppressWarnings("unchecked")
    private Object getValue(Object value) {
        if (value != null) {
            Class<?> typeComplex = Primitives.fromPrimitive(type);
            Class<?> complex = Primitives.fromPrimitive(value.getClass());
            if (typeComplex == complex || typeComplex.isAssignableFrom(complex)) {
                return value;
            }
            if (Number.class.isAssignableFrom(value.getClass()) && typeNumber) {
                Number number = (Number) value;
                if (typeComplex == Byte.class) {
                    return number.byteValue();
                } else if (typeComplex == Short.class) {
                    return number.shortValue();
                } else if (typeComplex == Integer.class) {
                    return number.intValue();
                } else if (typeComplex == Long.class) {
                    return number.longValue();
                } else if (typeComplex == Float.class) {
                    return number.floatValue();
                } else if (typeComplex == Double.class) {
                    return number.doubleValue();
                } else if (typeComplex == BigInteger.class) {
                    return BigInteger.valueOf(number.longValue());
                } else if (typeComplex == BigDecimal.class) {
                    return BigDecimal.valueOf(number.doubleValue());
                }
                return fallback;
            }
            if (type.isEnum()) {
                try {
                    return Enum.valueOf(type.asSubclass(Enum.class), value.toString());
                } catch (IllegalArgumentException iae) {
                    return fallback;
                }
            }
        }
        return fallback;
    }

}
