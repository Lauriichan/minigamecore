package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class DoubleParser implements IArgumentParser<Double> {

    @Override
    public Tuple<Double, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Double.parseDouble(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse double from '" + arguments[offset] + "'!", nfe);
        }
    }

}
