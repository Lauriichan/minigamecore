package me.lauriichan.minecraft.minigame.command.parser;

import java.util.List;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class EnumParser implements IArgumentParser<Enum<?>> {

    @Override
    public Tuple<Enum<?>, Integer> parse(final Class<?> type, final int offset, final String[] arguments, final ParamMap params)
        throws IllegalArgumentException {
        return Tuple.of(Enum.valueOf(type.asSubclass(Enum.class), arguments[offset].toUpperCase()), 1);
    }

    @Override
    public void suggest(final List<String> list, final Tuple<Enum<?>, Integer> tuple, final Class<?> type, final int offset,
        final String[] arguments, final ParamMap params) {
        for (final Enum<?> value : type.asSubclass(Enum.class).getEnumConstants()) {
            list.add(value.name().toLowerCase());
        }
    }

}
