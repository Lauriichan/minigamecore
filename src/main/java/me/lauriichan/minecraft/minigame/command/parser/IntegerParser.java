package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.command.StringReader;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class IntegerParser extends NumberParser<Integer> {

    @Override
    protected Tuple<Integer, Integer> parseValue(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Integer.parseInt(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse integer from '" + arguments[offset] + "'!", nfe);
        }
    }

    @Override
    public Object readParam(Class<?> type, String name, StringReader reader) {
        return reader.parseInt();
    }

    @Override
    protected Integer range(Integer value, ParamMap params) {
        if (params.has("min") && value < params.get("min", Integer.class)) {
            return params.get("min", Integer.class);
        }
        if (params.has("max") && value > params.get("max", Integer.class)) {
            return params.get("max", Integer.class);
        }
        return value;
    }

}
