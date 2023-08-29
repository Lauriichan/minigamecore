package me.lauriichan.minecraft.minigame.game;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lauriichan.minecraft.minigame.inject.InjectListener;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.JavaAccess;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public final class GameManager implements InjectListener {

    private final Logger logger;
    private final InjectManager inject;

    private final GameTicker ticker;

    private final HashMap<String, GameHolder> games = new HashMap<>();

    private String activeId;

    public GameManager(final Logger logger, final InjectManager inject) {
        this.ticker = new GameTicker(logger, this::tick);
        this.logger = logger;
        this.inject = inject;
    }

    private void tick(long delta) {
        GamePhase phase = getActivePhase();
        if (phase == null) {
            return;
        }
        phase.onTick(delta);
        if (phase.nextPhase()) {
            ticker.pauseNonInt();
            try {
                getActive().nextPhase();
            } finally {
                ticker.startNonInt();
            }
        }
    }

    public GameTicker getTicker() {
        return ticker;
    }

    public boolean load(final Resources resources) {
        return load(resources.pathAnnotation(Minigame.class));
    }

    public boolean load(final DataSource data) {
        return AnnotationTools.load(data, clazz -> {
            Minigame minigame = JavaAccess.getAnnotation(clazz, Minigame.class);
            if (minigame == null) {
                return;
            }
            Game game = inject.initialize(clazz);
            if (game == null) {
                return;
            }
            GameHolder holder = new GameHolder(clazz, game, minigame);
            try {
                LinkedHashSet<Class<? extends GamePhase>> phases = new LinkedHashSet<>();
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
        ticker.pauseNonInt();
        if (activeId != null) {
            GameHolder current = games.get(activeId);
            if (current != null) {
                try {
                    current.getGame().onStop(current);
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
        try {
            holder.getGame().onStart(holder);
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Failed to start game '" + holder.getName() + "'!", exception);
            activeId = null;
            return false;
        }
        activeId = holder.getId();
        holder.setActive(true);
        ticker.startNonInt();
        return true;
    }

}
