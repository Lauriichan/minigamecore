package me.lauriichan.minecraft.minigame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.JavaInstance;

public final class GameHolder {

    private final String id;
    private final String name;

    private final Game game;
    private final Class<?> type;

    private final HashMap<Class<? extends GamePhase>, GamePhase> phases = new HashMap<>();
    private final ArrayList<Class<? extends GamePhase>> order = new ArrayList<>();

    private Class<? extends GamePhase> current;
    private GamePhase currentPhase;

    private boolean active;

    GameHolder(final Class<? extends Game> type, final Game game, final Minigame minigame) {
        JavaInstance.put(game);
        this.type = type;
        this.game = game;
        this.id = minigame.id();
        String tmp = minigame.name();
        if (tmp.isBlank()) {
            tmp = id;
        }
        this.name = tmp;
    }

    void setPhases(final Logger logger, final InjectManager inject, final Set<Class<? extends GamePhase>> order) {
        phases.clear();
        this.order.clear();
        this.order.addAll(order);
        for (Class<? extends GamePhase> phaseType : this.order) {
            if(phaseType == null) {
                logger.log(Level.WARNING, "Found nulled GamePhase of game '" + name + "'!");
                continue;
            }
            GamePhase phase = inject.initialize(phaseType);
            if (phase == null) {
                logger.log(Level.WARNING, "Failed to load GamePhase '" + phaseType.getName() + "' of game '" + name + "'!");
                continue;
            }
            phases.put(phaseType, phase);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Game getGame() {
        return game;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    public Class<? extends GamePhase> getCurrentType() {
        return current;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public GamePhase getPhase(Class<? extends GamePhase> phaseType) {
        if (phaseType == null) {
            return null;
        }
        return phases.get(phaseType);
    }

    public boolean hasPhase(Class<? extends GamePhase> phaseType) {
        if (phaseType == null) {
            return false;
        }
        return order.contains(phaseType);
    }

    public int getPhaseId(Class<? extends GamePhase> phaseType) {
        if (phaseType == null) {
            return -1;
        }
        return order.indexOf(phaseType);
    }

    public void setCurrent(Class<? extends GamePhase> phaseType) {
        if (phaseType != null && !order.contains(phaseType)) {
            return;
        }
        if (currentPhase != null) {
            currentPhase.onEnd();
        }
        if (phaseType == null) {
            this.current = null;
            this.currentPhase = null;
            return;
        }
        this.current = phaseType;
        this.currentPhase = phases.get(phaseType);
        if (currentPhase != null) {
            currentPhase.onStart();
        }
    }

    public void nextPhase() {
        int index = 0;
        if (current != null) {
            index = order.indexOf(current) + 1;
        }
        if (index >= order.size()) {
            setCurrent(null);
            return;
        }
        setCurrent(order.get(index));
    }

}
