package me.lauriichan.minecraft.minigame.command;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.lauriichan.minecraft.minigame.command.annotation.Argument;
import me.lauriichan.minecraft.minigame.command.parser.ObjectParser;
import me.lauriichan.minecraft.minigame.util.Checks;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;

final class ActionInfo {

    private final CommandInfo command;

    private final MethodHandle method;
    private final List<ArgumentInfo> arguments;
    private final List<ArgumentInfo> sortedArguments;

    ActionInfo(final CommandInfo command, final Method method) {
        this.command = Checks.isNotNull(command);
        this.method = Checks.isNotNull(JavaAccessor.accessMethod(method));
        final Parameter[] parameters = method.getParameters();
        final ArrayList<ArgumentInfo> arguments = new ArrayList<>();
        boolean senderFound = false;
        for (int idx = 0; idx < parameters.length; idx++) {
            Parameter parameter = parameters[idx];
            Argument argument = JavaAccessor.getAnnotation(parameter, Argument.class);
            if (argument == null) {
                arguments.add(new ArgumentInfo(this, ObjectParser.class, 0, parameter.getType(), false));
                continue;
            }
            if (senderFound && argument.sender()) {
                throw new IllegalStateException("Action can't have two sender arguments!");
            }
            arguments.add(new ArgumentInfo(this, argument.parser(), argument.index(), parameter.getType(), argument.sender()));
        }
        this.arguments = Collections.unmodifiableList(arguments);
        final ArrayList<ArgumentInfo> sortedArguments = new ArrayList<>(arguments);
        Collections.sort(sortedArguments);
        this.sortedArguments = Collections.unmodifiableList(sortedArguments);
    }

    public CommandInfo command() {
        return command;
    }

    public MethodHandle method() {
        return method;
    }

    public List<ArgumentInfo> arguments() {
        return arguments;
    }

    public List<ArgumentInfo> sortedArguments() {
        return sortedArguments;
    }

}
