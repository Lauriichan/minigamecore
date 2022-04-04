package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.command.StringReader;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class ByteParser extends NumberParser<Byte> {

    @Override
    protected Tuple<Byte, Integer> parseValue(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        try {
            return Tuple.of(Byte.parseByte(arguments[offset]), 1);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Failed to parse byte from '" + arguments[offset] + "'!", nfe);
        }
    }

    @Override
    public Object readParam(Class<?> type, String name, StringReader reader) {
        return reader.parseByte();
    }

    @Override
    protected Byte range(Byte value, ParamMap params) {
        if (params.has("min") && value < params.get("min", Byte.class)) {
            return params.get("min", Byte.class);
        }
        if (params.has("max") && value > params.get("max", Byte.class)) {
            return params.get("max", Byte.class);
        }
        return value;
    }

}
