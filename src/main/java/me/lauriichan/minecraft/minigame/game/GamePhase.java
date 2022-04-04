package me.lauriichan.minecraft.minigame.game;

public abstract class GamePhase {

    public void onStart() {};

    public void onTick(long delta) {};

    public void onEnd() {};
    
    public abstract boolean nextPhase();

}

