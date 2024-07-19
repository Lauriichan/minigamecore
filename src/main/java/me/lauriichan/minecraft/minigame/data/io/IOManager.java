package me.lauriichan.minecraft.minigame.data.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;

import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.JavaAccess;
import me.lauriichan.minecraft.minigame.util.Reference;

public final class IOManager {

    private static final Function<String, ArrayList<DataInfo>> FUNC = ignore -> new ArrayList<>();

    private final HashMap<String, ArrayList<DataInfo>> map = new HashMap<>();
    
    private final Logger logger;
    private final InjectManager injector;

    public IOManager(final Logger logger, final InjectManager injector) {
        this.logger = Objects.requireNonNull(logger);
        this.injector = Objects.requireNonNull(injector);
    }

    public boolean register(final Class<? extends IDataExtension<?, ?>> clazz) {
        final TypeId typeId = JavaAccess.getAnnotation(clazz, TypeId.class);
        if (typeId == null) {
            return false;
        }
        injector.inject(clazz);
        final IDataExtension<?, ?> extension = (IDataExtension<?, ?>) JavaAccess.instance(clazz);
        if (extension == null) {
            return false;
        }
        injector.inject(extension);
        return register(new DataInfo(typeId, extension));
    }

    boolean register(final DataInfo info) {
        final ArrayList<DataInfo> list = map.computeIfAbsent(info.hasId() ? info.getId() : null, FUNC);
        if (list.contains(info)) {
            return false;
        }
        list.add(info);
        return true;
    }

    public Object convert(final String id, final Object object) {
        try {
            final ArrayList<DataInfo> list = map.get(id);
            if (list == null) {
                return null;
            }
            for (int index = 0; index < list.size(); index++) {
                final DataInfo info = list.get(index);
                final Object out = info.convert(object);
                if (out == null) {
                    continue;
                }
                return out;
            }
            return null;
        } catch (Throwable throwable) {
            if (Reloadable.DEBUG) {
                logger.log(Level.SEVERE, "Failed to convert data with id '%s'".formatted(id), throwable);
            }
            return null;
        }
    }

    public <E> E convert(final Object object, final Class<E> abstraction) {
        return convertImpl(object, abstraction, null);
    }

    private <E> E convertImpl(final Object object, final Class<E> abstraction, final Reference<String> id) {
        try {
            final ArrayList<DataInfo> nullInfo = map.get(null);
            for (final ArrayList<DataInfo> infos : map.values()) {
                if (nullInfo == infos) {
                    continue;
                }
                final E val = convertImpl(infos, object, abstraction, id);
                if (val == null) {
                    continue;
                }
                return val;
            }
            return nullInfo == null ? null : convertImpl(nullInfo, object, abstraction, id);
        } catch (Throwable throwable) {
            if (Reloadable.DEBUG) {
                logger.log(Level.SEVERE, "Failed to convert data with id '%s'".formatted(id), throwable);
            }
            return null;
        }
    }

    private <E> E convertImpl(final ArrayList<DataInfo> infos, final Object object, final Class<E> abstraction,
        final Reference<String> id) {
        for (final DataInfo info : infos) {
            if (!abstraction.isAssignableFrom(info.getOutputType())) {
                continue;
            }
            final Object out = info.convert(object);
            if (out == null) {
                continue;
            }
            id.set(info.getId());
            return abstraction.cast(out);
        }
        return null;
    }

    public JsonObject serializeJson(final Object object) {
        final Reference<String> id = Reference.of();
        final JsonValue<?> tag = convertImpl(object, JsonValue.class, id);
        if (id.isEmpty()) {
            return null;
        }
        final JsonObject jsonObject = new JsonObject();
        jsonObject.set("id", id.get());
        jsonObject.set("data", tag);
        return jsonObject;
    }

    public Object deserializeJson(final JsonObject object) {
        if (!object.has("id", ValueType.STRING) || !object.has("data")) {
            return null;
        }
        final String id = object.get("id").getValue().toString();
        final JsonValue<?> data = object.get("data");
        return convert(id, data);
    }

}
