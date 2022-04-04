package me.lauriichan.minecraft.minigame.command;

import java.util.HashMap;

import me.lauriichan.minecraft.minigame.command.annotation.Param;

public final class ParamMap {

    private final HashMap<String, Object> map = new HashMap<>();

    public ParamMap(Class<?> type, IArgumentParser<?> parser, Param[] params) {
        for (int index = 0; index < params.length; index++) {
            Param param = params[index];
            try {
                map.put(param.name(), parser.readParam(type, param.name(), new StringReader(param.value())));
            } catch (Exception exp) {
            }
        }
    }

    public boolean has(String name) {
        return map.containsKey(name);
    }

    public boolean has(String name, Class<?> type) {
        if (!map.containsKey(name)) {
            return false;
        }
        return type.isAssignableFrom(map.get(name).getClass());
    }

    public <E> E get(String name, Class<E> type) {
        if (!map.containsKey(name)) {
            return null;
        }
        Object value = map.get(name);
        if (type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <E> E getOrDefault(String name, E value) {
        Object object = get(name, value.getClass());
        if (object == null) {
            return value;
        }
        return (E) object;
    }

    public Number getOrDefault(String name, Number number) {
        Number object = get(name, Number.class);
        if (object == null) {
            return number;
        }
        return object;
    }

    public byte getOrDefault(String name, byte num) {
        return getOrDefault(name, (Number) num).byteValue();
    }

    public short getOrDefault(String name, short num) {
        return getOrDefault(name, (Number) num).shortValue();
    }

    public int getOrDefault(String name, int num) {
        return getOrDefault(name, (Number) num).intValue();
    }

    public long getOrDefault(String name, long num) {
        return getOrDefault(name, (Number) num).longValue();
    }

    public float getOrDefault(String name, float num) {
        return getOrDefault(name, (Number) num).floatValue();
    }

    public double getOrDefault(String name, double num) {
        return getOrDefault(name, (Number) num).doubleValue();
    }

}
