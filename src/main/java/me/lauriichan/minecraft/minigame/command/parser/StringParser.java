package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class StringParser implements IArgumentParser<String> {

    @Override
    public Tuple<String, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        return Tuple.of(arguments[offset], 1);
    }

}
