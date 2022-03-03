package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class FloatParser implements IArgumentParser<Float> {

    @Override
    public Tuple<Float, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Float.parseFloat(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse float from '" + arguments[offset] + "'!", nfe);
        }
    }

}
