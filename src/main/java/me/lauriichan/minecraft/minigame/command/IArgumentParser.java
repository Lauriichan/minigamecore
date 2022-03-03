package me.lauriichan.minecraft.minigame.command;

import me.lauriichan.minecraft.minigame.util.Tuple;

public interface IArgumentParser<E> {

    Tuple<E, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException;

}
