package me.lauriichan.minecraft.minigame.data.automatic.config;

import java.lang.reflect.Field;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.lauriichan.minecraft.minigame.util.JavaAccess;

final class ConfigSectionSync implements IConfigSync {

    private final Object instance;

    private final Field field;

    private final String path;
    private final String key;

    public ConfigSectionSync(final Object instance, final Field field) {
        ConfigSection config = JavaAccess.getAnnotation(field, ConfigSection.class);
        if (config == null) {
            throw new IllegalArgumentException("Field needs @ConfigSection annotation");
        }
        this.path = config.path();
        this.key = config.key();
        this.instance = instance;
        this.field = Objects.requireNonNull(field);

        if (!ConfigurationSection.class.equals(field.getType())) {
            throw new IllegalArgumentException(
                "Field '" + field.getDeclaringClass().getName() + '#' + field.getName() + "' needs type ConfigurationSection!");
        }
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

    public Object getInstance() {
        return instance;
    }

    @Override
    public Object extractValue(YamlConfiguration configuration, String key) {
        if (key.isBlank()) {
            return configuration;
        }
        if (configuration.isConfigurationSection(key)) {
            return configuration.getConfigurationSection(key);
        }
        return configuration.createSection(key);
    }

    public void update(Object value) {
        ConfigurationSection set = null;
        if (value instanceof ConfigurationSection) {
            set = (ConfigurationSection) value;
        }
        JavaAccess.setValue(instance, field, set);
    }

}
