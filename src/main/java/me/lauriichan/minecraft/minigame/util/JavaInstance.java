package me.lauriichan.minecraft.minigame.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class JavaInstance {

    private JavaInstance() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final ConcurrentHashMap<Class<?>, Object> INSTANCES = new ConcurrentHashMap<>();
    private static final ArrayList<Class<?>> TYPES = new ArrayList<>();

    public static <E> E get(Class<E> clazz) {
        Object object = INSTANCES.get(clazz);
        if (object != null) {
            return clazz.cast(object);
        }
        for (int index = 0; index < TYPES.size(); index++) {
            Class<?> type = TYPES.get(index);
            if (!clazz.isAssignableFrom(type)) {
                continue;
            }
            return clazz.cast(INSTANCES.get(type));
        }
        if (!Modifier.isAbstract(clazz.getModifiers())) {
            E value = initialize(clazz);
            if (value == null) {
                return null;
            }
            TYPES.add(clazz);
            INSTANCES.put(clazz, value);
            return value;
        }
        return null;
    }

    public static void remove(Class<?> type) {
        INSTANCES.remove(type);
        TYPES.remove(type);
    }

    public static void clear() {
        INSTANCES.clear();
        TYPES.clear();
    }

    @SuppressWarnings("unchecked")
    public static <E> boolean put(E object) {
        return put((Class<E>) object.getClass(), object);
    }

    public static <E> boolean put(Class<E> clazz, E object) {
        if (clazz == null || object == null || TYPES.contains(clazz)) {
            return false;
        }
        TYPES.add(clazz);
        INSTANCES.put(clazz, object);
        return true;
    }

    public static <E> E initialize(Class<E> clazz) {
        Constructor<?>[] constructors = JavaAccess.getConstructors(clazz);
        final Class<?>[] arguments = TYPES.toArray(Class[]::new);
        final int max = arguments.length;
        Constructor<?> builder = null;
        int args = 0;
        int[] argIdx = new int[max];
        for (final Constructor<?> constructor : constructors) {
            final int count = constructor.getParameterCount();
            if (count > max || count < args) {
                continue;
            }
            final int[] tmpIdx = new int[max];
            for (int idx = 0; idx < max; idx++) {
                tmpIdx[idx] = -1;
            }
            final Class<?>[] types = constructor.getParameterTypes();
            int tmpArgs = 0;
            for (int index = 0; index < count; index++) {
                for (int idx = 0; idx < max; idx++) {
                    if (!types[index].isAssignableFrom(arguments[idx])) {
                        continue;
                    }
                    tmpIdx[idx] = index;
                    tmpArgs++;
                }
            }
            if (tmpArgs != count) {
                continue;
            }
            argIdx = tmpIdx;
            args = tmpArgs;
            builder = constructor;
        }
        if (builder == null) {
            return null;
        }
        if (args == 0) {
            return clazz.cast(JavaAccess.instance(builder));
        }
        final Object[] parameters = new Object[args];
        for (int idx = 0; idx < max; idx++) {
            if (argIdx[idx] == -1) {
                continue;
            }
            parameters[argIdx[idx]] = get(arguments[idx]);
        }
        return clazz.cast(JavaAccess.instance(builder, parameters));
    }

}
