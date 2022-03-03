package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class ShortParser implements IArgumentParser<Short> {

    @Override
    public Tuple<Short, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Short.parseShort(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse short from '" + arguments[offset] + "'!", nfe);
        }
    }

}
