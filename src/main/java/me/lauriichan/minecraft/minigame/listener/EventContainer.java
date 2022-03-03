package me.lauriichan.minecraft.minigame.listener;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import me.lauriichan.minecraft.minigame.game.GameManager;
import me.lauriichan.minecraft.minigame.game.NullPhase;

public final class EventContainer implements org.bukkit.event.Listener {

    private static final Function<EventPriority, ArrayList<EventAction>> LIST_BUILDER = (ignore) -> new ArrayList<>();

    private final EnumMap<EventPriority, ArrayList<EventAction>> map = new EnumMap<>(EventPriority.class);
    private final Class<? extends Event> clazz;

    private final EventExecutor[] executors = new EventExecutor[6];
    private final ListenerManager manager;

    EventContainer(final ListenerManager manager, final Class<? extends Event> clazz) {
        this.manager = manager;
        this.clazz = clazz;
    }

    public void add(EventAction action) {
        EventPriority priority = action.getListener().handler().priority();
        ArrayList<EventAction> actions = map.computeIfAbsent(priority, LIST_BUILDER);
        if (executors[priority.getSlot()] == null) {
            EventExecutor executor = new ListenerImpl(manager.getGameManager(), actions);
            executors[priority.getSlot()] = executor;
            Bukkit.getPluginManager().registerEvent(clazz, this, priority, executor, manager.getPlugin(), false);
        }
        if (actions.contains(action)) {
            return;
        }
        actions.add(action);
    }

    public void dispose() {
        for (EventPriority priority : EventPriority.values()) {
            ArrayList<EventAction> list = map.get(priority);
            if (list != null) {
                list.clear();
            }
        }
    }

    private static final class ListenerImpl implements EventExecutor {

        private final ArrayList<EventAction> actions;
        private final GameManager game;

        public ListenerImpl(final GameManager game, final ArrayList<EventAction> actions) {
            this.actions = actions;
            this.game = game;
        }

        @Override
        public void execute(Listener listener, Event event) throws EventException {
            if (!(event instanceof Cancellable)) {
                for (int index = 0; index < actions.size(); index++) {
                    EventAction action = actions.get(index);
                    if (action.getGamePhase() != NullPhase.class && action.getGamePhase() != game.getActivePhaseType()) {
                        continue;
                    }
                    action.call(event);
                }
                return;
            }
            Cancellable cancel = (Cancellable) event;
            for (int index = 0; index < actions.size(); index++) {
                EventAction action = actions.get(index);
                if (action.getGamePhase() != NullPhase.class && action.getGamePhase() != game.getActivePhaseType()) {
                    continue;
                }
                if (action.ignoreCancelled() && cancel.isCancelled()) {
                    continue;
                }
                action.call(event);
            }
        }

    }

}
