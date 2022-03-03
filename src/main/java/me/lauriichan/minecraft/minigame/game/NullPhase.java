package me.lauriichan.minecraft.minigame.game;

public final class NullPhase extends GamePhase {

    private NullPhase() {
        throw new UnsupportedOperationException("Null phase");
    }

    @Override
    public boolean nextPhase() {
        return false;
    }

}
