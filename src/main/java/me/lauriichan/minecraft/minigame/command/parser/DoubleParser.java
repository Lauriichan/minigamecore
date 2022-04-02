package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.command.StringReader;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class DoubleParser extends NumberParser<Double> {

    @Override
    protected Tuple<Double, Integer> parseValue(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Double.parseDouble(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse double from '" + arguments[offset] + "'!", nfe);
        }
    }

    @Override
    public Object readParam(Class<?> type, String name, StringReader reader) {
        return reader.parseDouble();
    }

    @Override
    protected Double range(Double value, ParamMap params) {
        if (params.has("min") && value < params.get("min", Double.class)) {
            return params.get("min", Double.class);
        }
        if (params.has("max") && value > params.get("max", Double.class)) {
            return params.get("max", Double.class);
        }
        return value;
    }

}
