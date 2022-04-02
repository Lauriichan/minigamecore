package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.command.StringReader;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class FloatParser extends NumberParser<Float> {

    @Override
    protected Tuple<Float, Integer> parseValue(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Float.parseFloat(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse float from '" + arguments[offset] + "'!", nfe);
        }
    }

    @Override
    public Object readParam(Class<?> type, String name, StringReader reader) {
        return reader.parseFloat();
    }

    @Override
    protected Float range(Float value, ParamMap params) {
        if (params.has("min") && value < params.get("min", Float.class)) {
            return params.get("min", Float.class);
        }
        if (params.has("max") && value > params.get("max", Float.class)) {
            return params.get("max", Float.class);
        }
        return value;
    }

}
