package me.lauriichan.minecraft.minigame.data.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSection<C, T> implements ISection<C, T> {

    protected final HashMap<String, AbstractSection<C, T>> sectionMap = new HashMap<>();
    protected final AbstractSection<C, T> parent;
    protected final String name;

    public AbstractSection(AbstractSection<C, T> parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    /*
     * Implementation
     */

    @Override
    public AbstractSection<C, T> getParent() {
        return parent;
    }

    @Override
    public AbstractSection<C, T> getRoot() {
        if (parent == null) {
            return this;
        }
        return parent;
    }

    @Override
    public void clear() {
        sectionMap.clear();
        clearImpl();
    }

    @Override
    public Set<String> keys() {
        HashSet<String> set = new HashSet<>();
        set.addAll(sectionMap.keySet());
        set.addAll(keysImpl());
        return set;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public C get(String path) {
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        return get(key, ConfigHelper.getKeysWithout(keys, key));
    }

    @Override
    public C get(String path, T type) {
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        return get(key, ConfigHelper.getKeysWithout(keys, key), type);
    }

    @Override
    public AbstractSection<C, T> getSection(String path) {
        return getSection(ConfigHelper.getKeys(path), false);
    }

    @Override
    public boolean isSection(String path) {
        return getSection(path) != null;
    }

    @Override
    public AbstractSection<C, T> createSection(String path) {
        return getSection(ConfigHelper.getKeys(path), true);
    }

    @Override
    public Object getValue(String path) {
        C value = get(path);
        if (value == null) {
            return null;
        }
        return getValueImpl(value);
    }

    @Override
    public <P> P getValue(String path, Class<P> sample) {
        Object value = getValue(path);
        if (value == null || !sample.isAssignableFrom(value.getClass())) {
            return null;
        }
        return sample.cast(value);
    }

    @Override
    public Number getValueOrDefault(String path, Number fallback) {
        if (fallback == null) {
            return null;
        }
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        AbstractSection<C, T> section = getSection(ConfigHelper.getKeysWithout(keys, key), true);
        C value = section.getImpl(key);
        if (value == null) {
            section.setValueImpl(key, fallback);
            return fallback;
        }
        Object object = section.getValueImpl(value);
        if (object == null || !Number.class.isAssignableFrom(object.getClass())) {
            section.setValueImpl(key, fallback);
            return fallback;
        }
        return (Number) object;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> P getValueOrDefault(String path, P fallback) {
        if (fallback == null) {
            return null;
        }
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        AbstractSection<C, T> section = getSection(ConfigHelper.getKeysWithout(keys, key), true);
        C value = section.getImpl(key);
        if (value == null) {
            section.setValueImpl(key, fallback);
            return fallback;
        }
        Object object = section.getValueImpl(value);
        if (object == null || !fallback.getClass().isAssignableFrom(object.getClass())) {
            section.setValueImpl(key, fallback);
            return fallback;
        }
        return (P) object;
    }

    @Override
    public boolean has(String path) {
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        AbstractSection<C, T> section = getSection(ConfigHelper.getKeysWithout(keys, key), false);
        if (section == null) {
            return false;
        }
        return section.hasImpl(key);
    }

    @Override
    public boolean has(String path, T type) {
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        AbstractSection<C, T> section = getSection(ConfigHelper.getKeysWithout(keys, key), false);
        if (section == null) {
            return false;
        }
        return section.hasImpl(key, type);
    }

    @Override
    public void set(String path, C value) {
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        set(key, ConfigHelper.getKeysWithout(keys, key), value);
    }

    @Override
    public void setValue(String path, Object value) {
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        setValue(key, ConfigHelper.getKeysWithout(keys, key), value);
    }

    @Override
    public boolean hasValue(String path) {
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        AbstractSection<C, T> section = getSection(ConfigHelper.getKeysWithout(keys, key), false);
        if (section == null) {
            return false;
        }
        if (sectionMap.containsKey(key)) {
            return true;
        }
        C value = section.getImpl(key);
        if (value == null) {
            return false;
        }
        return section.getValueImpl(value) != null;
    }

    @Override
    public boolean hasValue(String path, Class<?> sample) {
        if (sample == null) {
            return false;
        }
        String[] keys = ConfigHelper.getKeys(path);
        String key = ConfigHelper.getLastKey(keys);
        AbstractSection<C, T> section = getSection(ConfigHelper.getKeysWithout(keys, key), false);
        if (section == null) {
            return false;
        }
        if (sectionMap.containsKey(key)) {
            return true;
        }
        C value = section.getImpl(key);
        if (value == null) {
            return false;
        }
        Object object = section.getValueImpl(value);
        if (object == null || !sample.isAssignableFrom(object.getClass())) {
            return false;
        }
        return true;
    }

    /*
     * Internals
     */

    protected void onSectionRemove(String path) {
        setImpl(path, null);
    }

    public AbstractSection<C, T> getSection(String[] path, boolean createIfNotThere) {
        if (path.length == 0) {
            return this;
        }
        AbstractSection<C, T> section = sectionMap.get(path[0]);
        if (section == null && createIfNotThere) {
            onSectionRemove(path[0]);
            section = createInstance(path[0]);
            sectionMap.put(path[0], section);
        }
        if (section == null) {
            return null;
        }
        return section.getSection(ConfigHelper.getNextKeys(path), createIfNotThere);
    }

    protected C get(String key, String[] path) {
        if (path.length == 0) {
            return getImpl(key);
        }
        AbstractSection<C, T> section = getSection(path, false);
        if (section == null) {
            return null;
        }
        return section.getImpl(key);
    }

    protected C get(String key, String[] path, T type) {
        if (path.length == 0) {
            return getImpl(key, type);
        }
        AbstractSection<C, T> section = getSection(path, false);
        if (section == null) {
            return null;
        }
        return section.getImpl(key, type);
    }

    protected void set(String key, String[] path, C value) {
        if (path.length == 0) {
            setImpl(key, value);
            return;
        }
        getSection(path, true).setImpl(key, value);
    }

    protected void setValue(String key, String[] path, Object value) {
        if (path.length == 0) {
            setValueImpl(key, value);
            return;
        }
        getSection(path, true).setValueImpl(key, value);
    }

    /*
     * Abstract
     */

    protected abstract void clearImpl();

    protected abstract Collection<String> keysImpl();

    protected abstract void setImpl(String key, C value);

    protected abstract void setValueImpl(String key, Object value);

    protected abstract C getImpl(String key);

    protected abstract C getImpl(String key, T type);

    protected abstract Object getValueImpl(C value);

    protected abstract boolean hasImpl(String key);

    protected abstract boolean hasImpl(String key, T type);

    protected abstract AbstractSection<C, T> createInstance(String name);

}
