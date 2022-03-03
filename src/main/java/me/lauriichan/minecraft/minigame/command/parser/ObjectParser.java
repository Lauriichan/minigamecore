package me.lauriichan.minecraft.minigame.command.parser;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.ParserManager;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class ObjectParser implements IArgumentParser<Object> {

    private final ParserManager manager;

    public ObjectParser(final ParserManager manager) {
        this.manager = manager;
    }

    @Override
    public Tuple<Object, Integer> parse(final Class<?> type, final int offset, final String[] arguments) throws IllegalArgumentException {
        final IArgumentParser<?> parser = manager.getParserFor(type);
        if (parser == null) {
            throw new IllegalArgumentException("Couldn't find parser for type '" + type.getSimpleName() + "'!");
        }
        return parser.parse(type, offset, arguments).mapFirst(v -> v);
    }

}
