package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class LongParser implements IArgumentParser<Long> {

    @Override
    public Tuple<Long, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Long.parseLong(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse long from '" + arguments[offset] + "'!", nfe);
        }
    }

}
