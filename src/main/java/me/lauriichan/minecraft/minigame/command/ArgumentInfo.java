package me.lauriichan.minecraft.minigame.command;

final class ArgumentInfo implements Comparable<ArgumentInfo> {

    private final ActionInfo action;

    private final Class<? extends IArgumentParser<?>> parser;
    private final Class<?> type;

    private final int index;
    private final boolean sender;

    ArgumentInfo(final ActionInfo action, final Class<? extends IArgumentParser<?>> parser, final int index, final Class<?> type,
        final boolean sender) {
        this.action = action;
        this.parser = parser;
        this.index = index;
        this.type = type;
        this.sender = sender;
    }

    public ActionInfo action() {
        return action;
    }

    public Class<? extends IArgumentParser<?>> parser() {
        return parser;
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

    @Override
    public int compareTo(final ArgumentInfo other) {
        return Integer.compare(index, other.index);
    }

}
