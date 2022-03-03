package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class IntegerParser implements IArgumentParser<Integer> {

    @Override
    public Tuple<Integer, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Integer.parseInt(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse integer from '" + arguments[offset] + "'!", nfe);
        }
    }

}
