package me.lauriichan.minecraft.minigame.util;

import java.util.HashMap;

public final class Placeholder {

    private final String id;
    private final String original;

    private final boolean text;

    private Placeholder(final String id, final String original) {
        this.id = (this.text = id.charAt(0) == '#') ? id.substring(1) : id;
        this.original = original;
    }

    public String getId() {
        return id;
    }

    public String getOriginal() {
        return original;
    }

    public boolean isText() {
        return text;
    }

    public String replace(final String message, final String value) {
        return message.replace(original, value);
    }

    public static Placeholder[] parse(final String message) {
        final int length = message.length();
        StringBuilder builder = null;
        final HashMap<String, Placeholder> placeholders = new HashMap<>();
        for (int index = 0; index < length; index++) {
            final char character = message.charAt(index);
            if (character == '$') {
                if (builder != null) {
                    build(placeholders, builder.toString());
                }
                builder = new StringBuilder().append(character);
                continue;
            }
            if (builder == null) {
                continue;
            }
            if (Character.isAlphabetic(character) || character == '.' || character == '#') {
                builder.append(character);
                continue;
            }
            build(placeholders, builder.toString());
            builder = null;
        }
        if (builder != null) {
            build(placeholders, builder.toString());
        }
        return placeholders.values().toArray(new Placeholder[placeholders.size()]);
    }

    private static void build(final HashMap<String, Placeholder> map, final String original) {
        final String id = original.substring(1);
        if (id.length() == 0 || id.length() == 1 && id.charAt(0) == '#' || map.containsKey(id)) {
            return;
        }
        map.put(id, new Placeholder(id, original));
    }

}
