package me.lauriichan.minecraft.minigame.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;
import me.lauriichan.minecraft.minigame.util.JavaInstance;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public class InjectManager {

    private final ArrayList<InjectListener> listeners = new ArrayList<>();
    private final Logger logger;

    public InjectManager(Logger logger) {
        this.logger = logger;
    }

    public boolean load(Resources resources) {
        return load(resources.pathAnnotation(Constant.class));
    }

    public boolean load(DataSource source) {
        return AnnotationTools.load(source, this::inject);
    }

    public void listen(InjectListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void inject(Class<?> type) {
        Objects.requireNonNull(type);
        for (int index = 0; index < listeners.size(); index++) {
            listeners.get(index).onInjectClass(type);
        }
        Field[] fields = JavaAccessor.getFields(type);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            inject(null, field);
        }
    }

    public void inject(Object object) {
        Class<?> type = Objects.requireNonNull(object).getClass();
        for (int index = 0; index < listeners.size(); index++) {
            listeners.get(index).onInjectInstance(type, object);
        }
        Field[] fields = JavaAccessor.getFields(type);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            inject(object, field);
        }
    }

    private void inject(Object instance, Field field) {
        if (!JavaAccessor.hasAnnotation(field, Inject.class)) {
            return;
        }
        Object value = JavaInstance.get(field.getType());
        if (value == null) {
            logger.log(Level.WARNING, "Field '" + field.getDeclaringClass().getName() + "/" + field.getName()
                + "' can't be injected as there is no value to be set!");
            return;
        }
        if (instance == null) {
            JavaAccessor.setStaticValue(field, value);
            return;
        }
        JavaAccessor.setObjectValue(instance, field, value);
    }

    public <E> E initialize(Class<E> type) {
        inject(type);
        Object object = JavaInstance.initialize(type);
        if (object == null) {
            return null;
        }
        inject(object);
        return type.cast(object);
    }

}
