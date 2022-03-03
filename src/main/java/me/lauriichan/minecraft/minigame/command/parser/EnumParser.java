package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class EnumParser implements IArgumentParser<Enum<?>> {

    @Override
    public Tuple<Enum<?>, Integer> parse(Class<?> type, int offset, String[] arguments) throws IllegalArgumentException {
        return Tuple.of(Enum.valueOf(type.asSubclass(Enum.class), arguments[offset].toUpperCase()), 1);
    }

}
