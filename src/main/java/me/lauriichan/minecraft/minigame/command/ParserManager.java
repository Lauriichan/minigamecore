package me.lauriichan.minecraft.minigame.command;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import me.lauriichan.minecraft.minigame.command.annotation.Parser;
import me.lauriichan.minecraft.minigame.command.parser.*;
import me.lauriichan.minecraft.minigame.data.io.Reloadable;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.IntWrapper;
import me.lauriichan.minecraft.minigame.util.JavaAccess;
import me.lauriichan.minecraft.minigame.util.Reference;
import me.lauriichan.minecraft.minigame.util.Tuple;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

@SuppressWarnings("rawtypes")
public final class ParserManager {

    private static final ParseResult INVALID_RESULT = new ParseResult(null, null, null, 0);
    private static final ParseResult EMPTY_RESULT = new ParseResult(new Object[0], null, null, 0);

    private final HashMap<Class<? extends IArgumentParser>, IArgumentParser<?>> parsers = new HashMap<>();
    private final LinkedHashMap<Class<?>, Class<? extends IArgumentParser>> types = new LinkedHashMap<>();

    private final Logger logger;
    private final InjectManager injectManager;

    public ParserManager(Logger logger, InjectManager injectManager) {
        this.logger = logger;
        this.injectManager = injectManager;
        parsers.put(ObjectParser.class, new ObjectParser(this));
        register(String.class, new StringParser());
        register(Byte.class, new ByteParser());
        register(Short.class, new ShortParser());
        register(Integer.class, new IntegerParser());
        register(Long.class, new LongParser());
        register(Float.class, new FloatParser());
        register(Double.class, new DoubleParser());
        register(Enum.class, new EnumParser());
    }

    public boolean load(final Resources resources) {
        return load(resources.pathAnnotation(Parser.class));
    }

    public boolean load(final DataSource data) {
        return AnnotationTools.load(data, clazz -> {
            final Parser parserInfo = JavaAccess.getAnnotation(clazz, Parser.class);
            if (parserInfo == null) {
                return;
            }
            final IArgumentParser<?> parser = injectManager.initialize(clazz);
            if (parser == null) {
                return;
            }
            parsers.put(clazz, parser);
            types.put(parserInfo.type(), clazz);
        }, IArgumentParser.class);
    }

    private void register(final Class<?> clazz, final IArgumentParser<?> parser) {
        parsers.put(parser.getClass().asSubclass(IArgumentParser.class), parser);
        types.put(clazz, parser.getClass());
    }

    public IArgumentParser<?> getParserFor(final Class<?> type) {
        if (type == null) {
            return null;
        }
        Class<? extends IArgumentParser> parserType = types.get(type);
        if (parserType == null) {
            final Class<?>[] classes = types.keySet().toArray(new Class[0]);
            for (int index = 0; index < classes.length; index++) {
                if (classes[index].isAssignableFrom(type)) {
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

    public ParseResult parseCommand(final ActionInfo action, final CommandSender sender, final int rawOffset, final String[] args) {
        if (action.arguments().size() == 0) {
            return EMPTY_RESULT;
        }
        final List<ArgumentInfo> arguments = action.arguments();
        final List<ArgumentInfo> sorted = action.sortedArguments();
        final Object[] output = new Object[sorted.size()];
        final Class<?> senderType = sender.getClass();
        final IntWrapper offset = new IntWrapper(rawOffset);
        int argIndex = 1;
        for (int index = 0; index < sorted.size(); index++) {
            final ArgumentInfo info = sorted.get(index);
            if (info.sender()) {
                if (!CommandSender.class.isAssignableFrom(info.type())) {
                    return INVALID_RESULT;
                }
                if (!info.type().isAssignableFrom(senderType)) {
                    return new ParseResult(null, info.type(), null, 0);
                }
                output[arguments.indexOf(info)] = sender;
                continue;
            }
            try {
                output[arguments.indexOf(info)] = parse(info, info.parserType(), offset, args);
            } catch (final Exception exp) { // Safety
                if (info.optional()) {
                    if (Primitives.isPrimitive(info.type()) && Number.class.isAssignableFrom(Primitives.fromPrimitive(info.type()))) {
                        output[arguments.indexOf(info)] = 0;
                        continue;
                    }
                    output[arguments.indexOf(info)] = null;
                    continue;
                }
                if (Reloadable.DEBUG) {
                    logger.log(Level.WARNING,
                        "Failed to parse argument '" + info.name() + "' of type '" + info.type() + "' at position " + argIndex, exp);
                }
                return new ParseResult(null, info.type(), info.name(), argIndex);
            } finally {
                argIndex++;
            }
        }
        return new ParseResult(output, null, null, 0);
    }

    public void suggestCommand(final ActionInfo action, final CommandSender sender, final int rawOffset, final String[] args,
        final List<String> suggestions) {
        if (action.arguments().size() == 0) {
            return;
        }
        final List<ArgumentInfo> sorted = action.sortedArguments();
        final IntWrapper offset = new IntWrapper(rawOffset);
        for (int index = 0; index < sorted.size(); index++) {
            final ArgumentInfo info = sorted.get(index);
            if (info.sender()) {
                continue;
            }
            try {
                suggest(info, info.parserType(), offset, args, suggestions);
            } catch (final Exception exp) { // Safety
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <A extends IArgumentParser<?>> void suggest(final ArgumentInfo info, final Class<A> parserType, final IntWrapper offset,
        final String[] arguments, final List<String> suggestions) {
        final A parser = getParser(info, parserType);
        Tuple<?, Integer> tuple;
        try {
            tuple = parser.parse(parserType, offset.value(), arguments, info.paramMap().get());
        } catch (final IllegalArgumentException exp) {
            tuple = Tuple.of(null, 0);
        }
        if (offset.value() + tuple.getSecond() + 1 < arguments.length) {
            return;
        }
        parser.suggest(suggestions, (Tuple) tuple, info.type(), offset.value(), arguments, info.paramMap().get());
    }

    public <A extends IArgumentParser<?>> Object parse(final ArgumentInfo info, final Class<A> parserType, final IntWrapper offset,
        final String[] arguments) throws IllegalArgumentException {
        final Tuple tuple = getParser(info, parserType).parse(info.type(), offset.value(), arguments, info.paramMap().get());
        offset.add((Integer) tuple.getSecond());
        return tuple.getFirst();
    }

    public <A extends IArgumentParser<?>> A getParser(final ArgumentInfo info, final Class<A> parserType) {
        final Reference<IArgumentParser<?>> rawParser = info.parser();
        if (rawParser.isEmpty()) {
            initParser(info);
        }
        return parserType.cast(rawParser.get());
    }

    private void initParser(final ArgumentInfo info) {
        final IArgumentParser<?> parser = parsers.get(info.parserType());
        if (parser == null) {
            throw new IllegalStateException("Couldn't find parser '" + info.parserType().getName() + "'");
        }
        info.paramMap().set(new ParamMap(info.type(), parser, info.params())).lock();
        info.parser().set(parser).lock();
    }

}
