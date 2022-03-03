package me.lauriichan.minecraft.minigame.util;

public final class IntWrapper {

    private int value;

    public IntWrapper() {
        this.value = 0;
    }

    public IntWrapper(final int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public IntWrapper increment() {
        value++;
        return this;
    }

    public IntWrapper add(final int amount) {
        value += amount;
        return this;
    }

    public IntWrapper subtract(final int amount) {
        value -= amount;
        return this;
    }

}
