package me.lauriichan.minecraft.minigame.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Consumer;

import me.lauriichan.minecraft.minigame.util.source.DataSource;

public final class AnnotationTools {

    private AnnotationTools() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean load(final DataSource source, final Consumer<Class<?>> consumer) {
        if (source == null || !source.exists()) {
            return false;
        }
        try {
            BufferedReader reader = source.openBufferedReader();
            String classPath;
            while ((classPath = reader.readLine()) != null) {
                Class<?> clazz = JavaAccessor.getClass(classPath);
                if (clazz == null) {
                    continue;
                }
                consumer.accept(clazz);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static <E> boolean load(final DataSource source, final Consumer<Class<? extends E>> consumer, final Class<E> superType) {
        if (source == null || !source.exists()) {
            return false;
        }
        try {
            BufferedReader reader = source.openBufferedReader();
            String classPath;
            while ((classPath = reader.readLine()) != null) {
                Class<?> clazz = JavaAccessor.getClass(classPath);
                if (clazz == null || !superType.isAssignableFrom(clazz)) {
                    continue;
                }
                consumer.accept(clazz.asSubclass(superType));
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
