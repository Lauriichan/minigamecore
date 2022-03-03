package me.lauriichan.minecraft.minigame.game;

public abstract class GamePhase {

    public void onStart() {};

    public void onTick() {};

    public void onEnd() {};
    
    public abstract boolean nextPhase();

}
