package me.lauriichan.minecraft.minigame.util;

public final class Checks {

    private Checks() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String isNotBlank(final String string) {
        if (string.isBlank()) {
            throw new IllegalStateException("String is blank");
        }
        return string;
    }

    public static String isNotBlank(final String string, final String message) {
        if (string.isBlank()) {
            throw new IllegalStateException(message);
        }
        return string;
    }

    public static <E> E isNotNull(final E value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }

    public static <E> E isNotNull(final E value, final String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

}
