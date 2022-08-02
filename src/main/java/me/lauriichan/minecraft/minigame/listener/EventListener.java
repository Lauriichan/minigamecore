package me.lauriichan.minecraft.minigame.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Objects;

import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.JavaAccess;

public final class EventListener {

    private final Class<?> type;
    private final Object instance;

    private final EventAction[] actions;

    EventListener(final InjectManager inject, final Class<?> type) {
        this.type = Objects.requireNonNull(type);
        Method[] methods = JavaAccess.getMethods(type);
        Object tmp = null;
        ArrayList<EventAction> actions = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                continue;
            }
            if (Modifier.isStatic(method.getModifiers())) {
                Listener listener = JavaAccess.getAnnotation(method, Listener.class);
                if (listener == null) {
                    continue;
                }
                actions.add(new EventAction(this, method, listener));
                continue;
            }
            Listener listener = JavaAccess.getAnnotation(method, Listener.class);
            if (listener == null) {
                continue;
            }
            if (tmp == null) {
                tmp = inject.initialize(type);
                if (tmp == null) {
                    throw new IllegalStateException("Failed to instanciate listener '" + type.getName() + "' for declared method!");
                }
            }
            actions.add(new EventAction(this, method, listener));
        }
        this.instance = tmp;
        this.actions = actions.toArray(EventAction[]::new);
    }

    public boolean hasActions() {
        return actions.length != 0;
    }

    public boolean hasInstance() {
        return instance != null;
    }

    public Object getInstance() {
        return instance;
    }

    public Class<?> getType() {
        return type;
    }

    public EventAction[] getActions() {
        return actions;
    }

}
