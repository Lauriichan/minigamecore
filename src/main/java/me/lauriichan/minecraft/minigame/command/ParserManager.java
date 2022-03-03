package me.lauriichan.minecraft.minigame.command;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import me.lauriichan.minecraft.minigame.command.annotation.Parser;
import me.lauriichan.minecraft.minigame.command.parser.*;
import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.IntWrapper;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;
import me.lauriichan.minecraft.minigame.util.Tuple;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

@SuppressWarnings("rawtypes")
public final class ParserManager {

    private static final Object[] EMPTY_ARGUMENTS = {};

    private final HashMap<Class<? extends IArgumentParser>, IArgumentParser<?>> parsers = new HashMap<>();
    private final HashMap<Class<?>, Class<? extends IArgumentParser>> types = new HashMap<>();

    private final Logger logger;

    public ParserManager(final Logger logger) {
        this.logger = logger;
        parsers.put(ObjectParser.class, new ObjectParser(this));
        parsers.put(StringParser.class, new StringParser());
        parsers.put(ByteParser.class, new ByteParser());
        parsers.put(ShortParser.class, new ShortParser());
        parsers.put(IntegerParser.class, new IntegerParser());
        parsers.put(LongParser.class, new LongParser());
        parsers.put(FloatParser.class, new FloatParser());
        parsers.put(DoubleParser.class, new DoubleParser());
        parsers.put(EnumParser.class, new EnumParser());
        parsers.put(BlockDataParser.class, new BlockDataParser());
    }

    public boolean load(final Resources resources) {
        return load(resources.pathIntern("generated/argument-parsers"));
    }

    public boolean load(final DataSource data) {
        return AnnotationTools.load(data, clazz -> {
            Parser parserInfo = JavaAccessor.getAnnotation(clazz, Parser.class);
            if (parserInfo == null) {
                return;
            }
            IArgumentParser<?> parser = (IArgumentParser<?>) JavaAccessor.instance(clazz);
            if (parser == null) {
                return;
            }
            parsers.put(clazz, parser);
            types.put(parserInfo.type(), clazz);
        }, IArgumentParser.class);
    }

    public IArgumentParser<?> getParserFor(final Class<?> type) {
        if (type == null) {
            return null;
        }
        Class<? extends IArgumentParser> parserType = types.get(type);
        if (parserType == null) {
            final Class<?>[] classes = types.keySet().toArray(Class[]::new);
            for (int index = 0; index < classes.length; index++) {
                if (!type.isAssignableFrom(classes[index])) {
                    parserType = types.get(type);
                    break;
                }
            }
            if (parserType == null) {
                return null;
            }
        }
        return parsers.get(parserType);
    }

    public Object[] parse(final ActionInfo action, final CommandSender sender, final int rawOffset, final String[] args) {
        if (action.arguments().size() == 0) {
            return EMPTY_ARGUMENTS;
        }
        List<ArgumentInfo> arguments = action.arguments();
        List<ArgumentInfo> sorted = action.sortedArguments();
        Object[] output = new Object[sorted.size()];
        Class<?> senderType = sender.getClass();
        IntWrapper offset = new IntWrapper(rawOffset);
        for (int index = 0; index < sorted.size(); index++) {
            ArgumentInfo info = sorted.get(index);
            if (info.sender()) {
                if (!info.type().isAssignableFrom(senderType)) {
                    return null;
                }
                output[arguments.indexOf(info)] = sender;
                continue;
            }
            try {
                output[arguments.indexOf(info)] = parse(info.parser(), info.type(), offset, args);
            } catch (Exception exp) { // Safety
                if (exp instanceof IllegalArgumentException) {
                    throw exp;
                }
                logger.log(Level.WARNING, "Failed to parse argument for '" + action.command().name() + "'!", exp);
                return null;
            }
        }
        return output;
    }

    public <E> E parse(final Class<? extends IArgumentParser<E>> parserType, final Class<?> type, final IntWrapper offset,
        final String[] arguments) throws IllegalArgumentException {
        final IArgumentParser<?> rawParser = parsers.get(parserType);
        if (rawParser == null) {
            throw new IllegalStateException("Couldn't find parser '" + parserType.getName() + "'");
        }
        final IArgumentParser<E> parser = parserType.cast(rawParser);
        final Tuple<E, Integer> tuple = parser.parse(type, offset.value(), arguments);
        offset.add(tuple.getSecond());
        return tuple.getFirst();
    }

}
