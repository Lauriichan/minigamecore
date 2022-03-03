package me.lauriichan.minecraft.minigame.game;

import java.util.Set;

public abstract class Game {

    protected abstract void onLoad(Set<Class<? extends GamePhase>> phases);

    protected abstract void onStart();

    protected abstract void onStop();

}
