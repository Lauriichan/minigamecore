package me.lauriichan.minecraft.minigame.data.config.json;

import java.util.Arrays;
import java.util.Collection;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;

import me.lauriichan.minecraft.minigame.data.config.AbstractSection;
import me.lauriichan.minecraft.minigame.data.config.ConfigHelper;

public class JsonSection extends AbstractSection<JsonValue<?>, ValueType> {

    private final JsonObject data;

    public JsonSection(AbstractSection<JsonValue<?>, ValueType> parent, String name) {
        super(parent, name);
        if (parent.has(name, ValueType.OBJECT)) {
            this.data = (JsonObject) parent.get(name);
            return;
        }
        this.data = new JsonObject();
        parent.set(name, data);
    }

    @Override
    protected void clearImpl() {
        for (String key : data.keys()) {
            data.set(key, null);
        }
    }

    @Override
    protected Collection<String> keysImpl() {
        return Arrays.asList(data.keys());
    }

    @Override
    protected void setImpl(String key, JsonValue<?> value) {
        data.set(key, value);
    }

    @Override
    protected void setValueImpl(String key, Object value) {
        data.set(key, value);
    }

    @Override
    protected JsonValue<?> getImpl(String key) {
        return data.get(key);
    }

    @Override
    protected JsonValue<?> getImpl(String key, ValueType type) {
        JsonValue<?> value = data.get(key);
        if (value == null || !value.hasType(type)) {
            return null;
        }
        return value;
    }

    @Override
    protected Object getValueImpl(JsonValue<?> value) {
        return value.getValue();
    }

    @Override
    protected boolean hasImpl(String key) {
        return data.has(key);
    }

    @Override
    protected boolean hasImpl(String key, ValueType type) {
        return data.has(key, type);
    }

    @Override
    public AbstractSection<JsonValue<?>, ValueType> getSection(String[] path, boolean createIfNotThere) {
        if (path.length == 0) {
            return this;
        }
        AbstractSection<JsonValue<?>, ValueType> section = sectionMap.get(path[0]);
        if (section == null && (hasImpl(path[0], ValueType.OBJECT) || createIfNotThere)) {
            section = createInstance(path[0]);
            sectionMap.put(path[0], section);
        }
        if (section == null) {
            return null;
        }
        return section.getSection(ConfigHelper.getNextKeys(path), createIfNotThere);
    }

    @Override
    protected AbstractSection<JsonValue<?>, ValueType> createInstance(String name) {
        return new JsonSection(this, name);
    }

}
