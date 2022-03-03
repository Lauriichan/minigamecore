package me.lauriichan.minecraft.minigame.listener;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.bukkit.event.Event;

import me.lauriichan.minecraft.minigame.game.GamePhase;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;

public final class EventAction {

    private final EventListener parent;

    private final Method method;
    private final MethodHandle handle;
    private final Class<?> eventType;

    private final Listener listener;

    private final boolean _static;

    EventAction(EventListener parent, Method method, Listener listener) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException("Method has too many parameter!");
        }
        Class<?> type = method.getParameters()[0].getType();
        if (!Event.class.isAssignableFrom(type)) {
            throw new IllegalStateException("Paramter is not an event!");
        }
        if (Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalStateException("Event type is not allowed to be abstract!");
        }
        this.listener = Objects.requireNonNull(listener);
        this.parent = parent;
        this.method = method;
        this.handle = JavaAccessor.accessMethod(method);
        this.eventType = type;
        this._static = Modifier.isStatic(method.getModifiers());
    }
    
    public boolean ignoreCancelled() {
        return listener.handler().ignoreCancelled();
    }

    public EventListener getParent() {
        return parent;
    }

    public Method getMethod() {
        return method;
    }

    public MethodHandle getHandle() {
        return handle;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public Listener getListener() {
        return listener;
    }

    public Class<? extends GamePhase> getGamePhase() {
        return listener.phase();
    }

    public void call(Event event) {
        if (_static) {
            JavaAccessor.invokeStatic(handle, event);
            return;
        }
        JavaAccessor.invoke(parent.getInstance(), handle, event);
    }

}
