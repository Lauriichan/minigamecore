package me.lauriichan.minecraft.minigame.game;

import java.util.Set;

public abstract class Game {

    protected abstract void onLoad(Set<Class<? extends GamePhase>> phases);

    protected void onStart(GameHolder holder) {}

    protected void onStop(GameHolder holder) {}

}
