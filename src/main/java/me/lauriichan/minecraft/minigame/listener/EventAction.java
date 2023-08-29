package me.lauriichan.minecraft.minigame.listener;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import org.bukkit.event.Event;

import me.lauriichan.minecraft.minigame.game.GamePhase;
import me.lauriichan.minecraft.minigame.util.JavaAccess;

public final class EventAction {

    private final EventListener parent;

    private final Method method;
    private final MethodHandle handle;
    private final Class<? extends Event> eventType;

    private final Listener listener;

    private final boolean _static;
    private final List<Class<? extends GamePhase>> phases;

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
        this.phases = Arrays.asList(listener.phase());
        this.parent = parent;
        this.method = method;
        this.handle = JavaAccess.accessMethod(method);
        this.eventType = type.asSubclass(Event.class);
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

    public Class<? extends Event> getEventType() {
        return eventType;
    }

    public Listener getListener() {
        return listener;
    }

    public List<Class<? extends GamePhase>> getPhases() {
        return phases;
    }

    public boolean isAllowed(Class<? extends GamePhase> phase) {
        if (phases.isEmpty()) {
            return true;
        }
        return listener.blacklist() != phases.contains(phase);
    }

    public void call(Event event) {
        if (_static) {
            try {
                handle.invoke(event);
            } catch (Throwable e) {
                parent.getLogger().log(Level.SEVERE,
                    "Failed to invoke event '" + method.getName() + "' for event '" + event.getEventName() + "'", e);
            }
            return;
        }
        try {
            handle.invoke(parent.getInstance(), event);
        } catch (Throwable e) {
            parent.getLogger().log(Level.SEVERE,
                "Failed to invoke event '" + method.getName() + "' for event '" + event.getEventName() + "'", e);
        }
    }

}
