package me.lauriichan.minecraft.minigame.config;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.lauriichan.minecraft.minigame.util.JavaAccessor;

final class ConfigSectionSync implements IConfigSync {

    private final Object instance;

    private final Field field;
    private final VarHandle handle;

    private final String path;
    private final String key;

    private final boolean _static;

    public ConfigSectionSync(final Object instance, final Field field) {
        ConfigSection config = JavaAccessor.getAnnotation(field, ConfigSection.class);
        if (config == null) {
            throw new IllegalArgumentException("Field needs @ConfigSection annotation");
        }
        this.path = config.path();
        this.key = config.key();
        this.instance = instance;
        this.field = Objects.requireNonNull(field);
        if (ConfigurationSection.class.equals(field.getType())) {
            throw new IllegalArgumentException("Field needs type ConfigurationSection!");
        }
        this.handle = JavaAccessor.accessField(field, true);
        this._static = Modifier.isStatic(field.getModifiers());
    }

    public String getPath() {
        return path;
    }

    public String getKey() {
        return key;
    }

    public Field getField() {
        return field;
    }

    public VarHandle getHandle() {
        return handle;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public Object extractValue(YamlConfiguration configuration, String key) {
        if (key.isBlank()) {
            return configuration;
        }
        ConfigurationSection section = configuration.getConfigurationSection(key);
        if (section != null) {
            return section;
        }
        return configuration.createSection(key);
    }

    public void update(Object value) {
        ConfigurationSection set = null;
        if (value instanceof ConfigurationSection) {
            set = (ConfigurationSection) value;
        }
        if (_static) {
            JavaAccessor.setStaticValue(handle, set);
            return;
        }
        JavaAccessor.setValue(instance, handle, set);
    }

}
