package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.command.StringReader;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class ShortParser extends NumberParser<Short> {

    @Override
    protected Tuple<Short, Integer> parseValue(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Short.parseShort(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse short from '" + arguments[offset] + "'!", nfe);
        }
    }

    @Override
    public Object readParam(Class<?> type, String name, StringReader reader) {
        return reader.parseShort();
    }

    @Override
    protected Short range(Short value, ParamMap params) {
        if (params.has("min") && value < params.get("min", Short.class)) {
            return params.get("min", Short.class);
        }
        if (params.has("max") && value > params.get("max", Short.class)) {
            return params.get("max", Short.class);
        }
        return value;
    }

}
