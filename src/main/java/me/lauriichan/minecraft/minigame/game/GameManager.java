package me.lauriichan.minecraft.minigame.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lauriichan.minecraft.minigame.inject.InjectListener;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public final class GameManager implements InjectListener {

    private final Logger logger;
    private final InjectManager inject;

    private final HashMap<String, GameHolder> games = new HashMap<>();

    private String activeId;

    public GameManager(final Logger logger, final InjectManager inject) {
        this.logger = logger;
        this.inject = inject;
    }

    public boolean load(final Resources resources) {
        return load(resources.pathAnnotation(Minigame.class));
    }

    public boolean load(final DataSource data) {
        return AnnotationTools.load(data, clazz -> {
            Minigame minigame = JavaAccessor.getAnnotation(clazz, Minigame.class);
            if (minigame == null) {
                return;
            }
            Game game = inject.initialize(clazz);
            if (game == null) {
                return;
            }
            GameHolder holder = new GameHolder(clazz, game, minigame);
            try {
                HashSet<Class<? extends GamePhase>> phases = new HashSet<>();
                game.onLoad(phases);
                holder.setPhases(logger, inject, phases);
            } catch (Exception exception) {
                logger.log(Level.SEVERE, "Failed to load game '" + minigame.id() + "'!", exception);
                return;
            }
            games.put(holder.getId(), holder);
        }, Game.class);
    }

    public GameHolder get(String id) {
        return games.get(id);
    }

    public GameHolder getActive() {
        return games.get(activeId);
    }

    public Class<? extends GamePhase> getActivePhaseType() {
        GameHolder holder = getActive();
        if (holder == null) {
            return null;
        }
        return holder.getCurrentType();
    }

    public GamePhase getActivePhase() {
        GameHolder holder = getActive();
        if (holder == null) {
            return null;
        }
        return holder.getCurrentPhase();
    }

    public String getActiveId() {
        return activeId;
    }

    public boolean hasActive() {
        return activeId != null;
    }

    public boolean setActive(String id) {
        return setActive(games.get(id));
    }

    public boolean setActive(GameHolder holder) {
        if (holder != null && !games.containsValue(holder)) {
            return false;
        }
        if (activeId != null) {
            GameHolder current = games.get(activeId);
            if (current != null) {
                try {
                    current.getGame().onStop();
                } catch (Exception exception) {
                    logger.log(Level.SEVERE, "Failed to stop game '" + current.getName() + "'!", exception);
                }
                current.setActive(false);
            }
        }
        if (holder == null) {
            activeId = null;
            return true;
        }
        activeId = holder.getId();
        try {
            holder.getGame().onStart();
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Failed to start game '" + holder.getName() + "'!", exception);
            activeId = null;
            return false;
        }
        holder.setActive(true);
        return true;
    }

}
