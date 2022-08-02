package me.lauriichan.minecraft.minigame.command;

import java.util.List;

import me.lauriichan.minecraft.minigame.util.Tuple;

public interface IArgumentParser<E> {

    Tuple<E, Integer> parse(Class<?> type, int offset, String[] arguments, ParamMap params) throws IllegalArgumentException;

    default int suggest(List<String> list, Class<?> type, int offset, String[] arguments, ParamMap params) {
        Tuple<E, Integer> tuple;
        try {
            tuple = parse(type, offset, arguments, params);
        } catch (IllegalArgumentException exp) {
            tuple = Tuple.of(null, 0);
        }
        suggest(list, tuple, type, offset, arguments, params);
        return tuple.getSecond();
    }

    default void suggest(final List<String> list, final Tuple<E, Integer> tuple, final Class<?> type, final int offset,
        final String[] arguments, final ParamMap params) {}

    default Object readParam(final Class<?> type, final String name, final StringReader reader) {
        return null;
    }

}
