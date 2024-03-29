package me.lauriichan.minecraft.minigame.data.config.yaml;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import me.lauriichan.minecraft.minigame.data.config.ISection;

class YamlSectionV1 implements IYamlSection {

    protected final ConfigurationSection handle;
    protected final ISection<Object, Class<?>> parent;

    public YamlSectionV1(ConfigurationSection handle, ISection<Object, Class<?>> parent) {
        this.handle = Objects.requireNonNull(handle, "Handle can't be null!");
        if (this instanceof YamlConfigV1) {
            this.parent = parent;
            return;
        }
        this.parent = Objects.requireNonNull(parent, "Parent can't be null!");
    }

    @Override
    public ISection<Object, Class<?>> getParent() {
        return parent;
    }

    @Override
    public ISection<Object, Class<?>> getRoot() {
        if (parent == null) {
            return this;
        }
        return parent;
    }

    @Override
    public Set<String> keys() {
        return handle.getKeys(false);
    }

    @Override
    public String name() {
        return handle.getName();
    }

    @Override
    public void clear() {
        String[] keys = handle.getKeys(false).toArray(String[]::new);
        for (String key : keys) {
            handle.set(key, null);
        }
    }

    @Override
    public boolean has(String path) {
        return handle.contains(path);
    }

    @Override
    public boolean has(String path, Class<?> type) {
        Object object = handle.get(path);
        return object != null && type.isAssignableFrom(object.getClass());
    }

    @Override
    public boolean hasValue(String path) {
        return has(path);
    }

    @Override
    public boolean hasValue(String path, Class<?> sample) {
        return has(path, sample);
    }

    @Override
    public Object get(String path) {
        return handle.get(path);
    }

    @Override
    public Object get(String path, Class<?> type) {
        Object object = getFor(path, type);
        if (object == null || !type.isAssignableFrom(object.getClass())) {
            return null;
        }
        return object;
    }

    private Object getFor(String path, Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return handle.getList(path);
        }
        return handle.get(path);
    }

    @Override
    public Object getValue(String path) {
        return get(path);
    }

    @Override
    public <P> P getValue(String path, Class<P> sample) {
        return sample.cast(get(path, sample));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> P getValueOrDefault(String path, P fallback) {
        Object value = get(path, fallback.getClass());
        if (value == null) {
            return fallback;
        }
        return (P) value;
    }

    @Override
    public Number getValueOrDefault(String path, Number fallback) {
        Number value = getValue(path, Number.class);
        if (value == null) {
            return fallback;
        }
        return value;
    }

    @Override
    public boolean isSection(String path) {
        return handle.isConfigurationSection(path);
    }

    @Override
    public ISection<Object, Class<?>> getSection(String path) {
        ConfigurationSection section = handle.getConfigurationSection(path);
        if (section == null) {
            return null;
        }
        return new YamlSectionV1(section, this);
    }

    @Override
    public ISection<Object, Class<?>> createSection(String path) {
        return new YamlSectionV1(handle.createSection(path), this);
    }

    @Override
    public void set(String path, Object value) {
        handle.set(path, value);
    }

    @Override
    public void setValue(String path, Object value) {
        set(path, value);
    }

}
