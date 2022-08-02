package me.lauriichan.minecraft.minigame.command;

public final class ParseResult {

    private final Object[] arguments;
    private final Class<?> type;
    private final String name;
    private final int index;

    public ParseResult(final Object[] arguments, final Class<?> type, final String name, final int index) {
        this.arguments = arguments;
        this.type = type;
        this.name = name;
        this.index = index;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

}
