package me.lauriichan.minecraft.minigame.command.parser;

import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.command.ParserManager;
import me.lauriichan.minecraft.minigame.command.StringReader;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class ObjectParser implements IArgumentParser<Object> {

    private final ParserManager manager;

    public ObjectParser(final ParserManager manager) {
        this.manager = manager;
    }

    @Override
    public Tuple<Object, Integer> parse(Class<?> type, final int offset, final String[] arguments, final ParamMap params)
        throws IllegalArgumentException {
        final IArgumentParser<?> parser = manager.getParserFor(type = Primitives.fromPrimitive(type));
        if (parser == null) {
            throw new IllegalArgumentException("Couldn't find parser for type '" + type.getSimpleName() + "'!");
        }
        return parser.parse(type, offset, arguments, params).mapFirst(v -> v);
    }

    @Override
    public Object readParam(Class<?> type, final String name, final StringReader reader) {
        final IArgumentParser<?> parser = manager.getParserFor(type = Primitives.fromPrimitive(type));
        if (parser == null) {
            throw new IllegalArgumentException("Couldn't find parser for type '" + type.getSimpleName() + "'!");
        }
        return parser.readParam(type, name, reader);
    }

}
