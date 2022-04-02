package me.lauriichan.minecraft.minigame.data.config.json;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;

import me.lauriichan.minecraft.minigame.data.config.AbstractSection;
import me.lauriichan.minecraft.minigame.data.config.ConfigHelper;
import me.lauriichan.minecraft.minigame.data.config.IConfiguration;

public class JsonConfig extends AbstractSection<JsonValue<?>, ValueType> implements IConfiguration<JsonValue<?>, ValueType> {

    public static final JsonWriter WRITER = new JsonWriter().setPretty(true).setSpaces(true).setIndent(4);
    public static final JsonParser PARSER = new JsonParser();

    private JsonObject data = new JsonObject();

    public JsonConfig() {
        super(null, "");
    }

    @Override
    public void load(File file) throws Throwable {
        sectionMap.clear();
        try {
            JsonValue<?> value = PARSER.fromFile(file);
            if (value == null || !value.hasType(ValueType.OBJECT)) {
                data = new JsonObject();
                return;
            }
            data = (JsonObject) value;
        } catch (Throwable throwable) {
            data = new JsonObject();
            throw throwable;
        }
    }

    @Override
    public void save(File file) throws Throwable {
        WRITER.toFile(data, file);
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
        if (value == null) {
            data.remove(key);
            return;
        }
        data.set(key, value);
    }

    @Override
    protected void setValueImpl(String key, Object value) {
        if (value == null) {
            data.remove(key);
            return;
        }
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
