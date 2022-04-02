package me.lauriichan.minecraft.minigame.command;

import me.lauriichan.minecraft.minigame.command.annotation.Param;
import me.lauriichan.minecraft.minigame.util.Reference;

final class ArgumentInfo implements Comparable<ArgumentInfo> {

    private final ActionInfo action;

    private final Param[] params;

    private final Class<? extends IArgumentParser<?>> parserType;
    private final Class<?> type;

    private final int index;
    private final boolean sender;
    private final boolean optional;

    private final Reference<IArgumentParser<?>> parser = Reference.of();
    private final Reference<ParamMap> paramMap = Reference.of();

    ArgumentInfo(final ActionInfo action, final Param[] params, final Class<? extends IArgumentParser<?>> parserType, final int index,
        final Class<?> type, final boolean sender, final boolean optional) {
        this.action = action;
        this.params = params;
        this.parserType = parserType;
        this.index = index;
        this.type = type;
        this.sender = sender;
        this.optional = optional;
    }

    public ActionInfo action() {
        return action;
    }

    public Class<? extends IArgumentParser<?>> parserType() {
        return parserType;
    }

    public Class<?> type() {
        return type;
    }

    public int index() {
        return index;
    }

    public boolean sender() {
        return sender;
    }

    public boolean optional() {
        return optional;
    }

    public Reference<IArgumentParser<?>> parser() {
        return parser;
    }

    public Reference<ParamMap> paramMap() {
        return paramMap;
    }

    public Param[] params() {
        return params;
    }

    @Override
    public int compareTo(final ArgumentInfo other) {
        if (other.optional != optional) {
            return Boolean.compare(optional, other.optional);
        }
        return Integer.compare(index, other.index);
    }

}
