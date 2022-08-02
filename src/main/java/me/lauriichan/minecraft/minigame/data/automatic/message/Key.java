package me.lauriichan.minecraft.minigame.data.automatic.message;

public final class Key {

    private final String key;
    private Object value;

    private Key(final String key) {
        this.key = key;
    }

    private Key(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    public Key setValue(final Object value) {
        this.value = value;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public Object getValueOrDefault(final Object fallback) {
        if (value == null) {
            return fallback;
        }
        return value;
    }

    public String getKey() {
        return key;
    }

    public static Key of(final String key) {
        return new Key(key);
    }

    public static Key of(final String key, final Object value) {
        return new Key(key, value);
    }

}
