package me.lauriichan.minecraft.minigame.command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.lauriichan.minecraft.minigame.command.annotation.Argument;
import me.lauriichan.minecraft.minigame.command.annotation.Description;
import me.lauriichan.minecraft.minigame.command.annotation.Param;
import me.lauriichan.minecraft.minigame.command.parser.ObjectParser;
import me.lauriichan.minecraft.minigame.util.Checks;
import me.lauriichan.minecraft.minigame.util.JavaAccess;

public final class ActionInfo {

    private final CommandInfo command;

    private final Method method;
    private final List<ArgumentInfo> arguments;
    private final List<ArgumentInfo> sortedArguments;

    private final String description;

    ActionInfo(final CommandInfo command, final Method method) {
        this.command = Checks.isNotNull(command);
        this.method = Checks.isNotNull(method);
        final Parameter[] parameters = method.getParameters();
        final ArrayList<ArgumentInfo> arguments = new ArrayList<>();
        boolean senderFound = false;
        for (int idx = 0; idx < parameters.length; idx++) {
            final Parameter parameter = parameters[idx];
            final Argument argument = JavaAccess.getAnnotation(parameter, Argument.class);
            if (argument == null) {
                arguments.add(
                    new ArgumentInfo(this, parameter.getName(), new Param[0], ObjectParser.class, 0, parameter.getType(), false, true));
                continue;
            }
            if (argument.sender()) {
                if (senderFound) {
                    throw new IllegalStateException("Action can't have two sender arguments!");
                }
                senderFound = true;
            }
            arguments.add(new ArgumentInfo(this, argument.name().trim().isEmpty() ? parameter.getName() : argument.name(),
                argument.params(), argument.parser(), argument.index(), parameter.getType(), argument.sender(), argument.optional()));
        }
        final Description description = JavaAccess.getAnnotation(method, Description.class);
        this.description = description == null ? "" : description.value();
        this.arguments = Collections.unmodifiableList(arguments);
        final ArrayList<ArgumentInfo> sortedArguments = new ArrayList<>(arguments);
        Collections.sort(sortedArguments);
        this.sortedArguments = Collections.unmodifiableList(sortedArguments);
    }

    public CommandInfo command() {
        return command;
    }

    public String description() {
        return description;
    }

    public Method method() {
        return method;
    }

    public List<ArgumentInfo> arguments() {
        return arguments;
    }

    public List<ArgumentInfo> sortedArguments() {
        return sortedArguments;
    }

}
