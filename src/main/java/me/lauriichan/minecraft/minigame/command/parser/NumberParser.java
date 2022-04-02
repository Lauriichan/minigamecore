package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.util.Tuple;

public abstract class NumberParser<E extends Number> implements IArgumentParser<E> {

    public static final byte BYTE_STEP = 1;
    public static final short SHORT_STEP = 1;
    public static final int INTEGER_STEP = 1;
    public static final long LONG_STEP = 1;
    public static final float FLOAT_STEP = 1;
    public static final double DOUBLE_STEP = 1;

    @Override
    public final Tuple<E, Integer> parse(Class<?> type, int offset, String[] arguments, ParamMap params) throws IllegalArgumentException {
        Tuple<E, Integer> tuple = parseValue(type, offset, arguments);
        E value = tuple.getFirst();
        if (value == null) {
            return tuple;
        }
        E output = range(value, params);
        if (output == value) {
            return tuple;
        }
        return Tuple.of(output, tuple.getSecond());
    }

    protected abstract Tuple<E, Integer> parseValue(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException;

    protected abstract E range(E value, ParamMap params);

}
