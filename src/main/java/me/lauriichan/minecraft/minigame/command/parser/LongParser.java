package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.command.StringReader;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class LongParser extends NumberParser<Long> {

    @Override
    protected Tuple<Long, Integer> parseValue(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Long.parseLong(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse long from '" + arguments[offset] + "'!", nfe);
        }
    }

    @Override
    public Object readParam(Class<?> type, String name, StringReader reader) {
        return reader.parseLong();
    }

    @Override
    protected Long range(Long value, ParamMap params) {
        if (params.has("min") && value < params.get("min", Long.class)) {
            return params.get("min", Long.class);
        }
        if (params.has("max") && value > params.get("max", Long.class)) {
            return params.get("max", Long.class);
        }
        return value;
    }

}
