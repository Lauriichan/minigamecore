package me.lauriichan.minecraft.minigame.inject;

public interface InjectListener {

    default void onInjectClass(Class<?> type) {};

    default void onInjectInstance(Class<?> type, Object instance) {}

}
