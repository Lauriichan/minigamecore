package me.lauriichan.minecraft.minigame.listener;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import me.lauriichan.minecraft.minigame.game.GameManager;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public final class ListenerManager {

    private final GameManager gameManager;
    private final InjectManager injectManager;

    private final ArrayList<EventListener> listeners = new ArrayList<>();
    private final ConcurrentHashMap<Class<? extends Event>, EventContainer> events = new ConcurrentHashMap<>();

    private final Plugin plugin;
    private final Logger logger;

    private final Function<Class<? extends Event>, EventContainer> containerBuilder = (clazz) -> new EventContainer(this, clazz);

    public ListenerManager(final Plugin plugin, final GameManager gameManager, final InjectManager injectManager) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.gameManager = gameManager;
        this.injectManager = injectManager;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public InjectManager getInjectManager() {
        return injectManager;
    }

    public boolean load(Resources resources) {
        return load(resources.pathAnnotation(Listener.class));
    }

    public boolean load(DataSource source) {
        if (!events.isEmpty()) {
            listeners.clear();
            for (EventContainer container : events.values()) {
                container.dispose();
            }
        }
        return AnnotationTools.load(source, (clazz) -> {
            try {
                logger.log(Level.INFO, "Loading '" + clazz.getName() + "'");
                EventListener listener = new EventListener(injectManager, logger, clazz);
                if (!listener.hasActions()) {
                    return;
                }
                listeners.add(listener);
                EventAction[] actions = listener.getActions();
                for (EventAction action : actions) {
                    events.computeIfAbsent(action.getEventType(), containerBuilder).add(action);
                }
            } catch (IllegalStateException | NullPointerException exp) {
                logger.log(Level.WARNING, "Failed to load EventListener '" + clazz.getName() + "'!", exp);
            }
        });
    }

}
