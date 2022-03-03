package me.lauriichan.minecraft.minigame.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lauriichan.minecraft.minigame.command.annotation.Action;
import me.lauriichan.minecraft.minigame.command.annotation.Command;
import me.lauriichan.minecraft.minigame.util.Checks;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;

final class CommandInfo {

    private final String name;
    private final String description;
    private final List<String> aliases;

    private final Class<?> owner;
    private final Object instance;
    private final Map<String, ActionInfo> actions;

    private final int pathDepth;

    CommandInfo(final Class<?> owner, final Command command) {
        this.owner = Checks.isNotNull(owner);
        this.instance = Checks.isNotNull(JavaAccessor.instance(owner));
        this.name = Checks.isNotBlank(Checks.isNotNull(command).name().toLowerCase().replace(" ", ""), "Command name can't be blank");
        this.description = command.description();
        final ArrayList<String> aliases = new ArrayList<>();
        for (String alias : command.aliases()) {
            if (alias == null || alias.isBlank()) {
                continue;
            }
            String value = alias.toLowerCase().replace(" ", "");
            if (value.isEmpty()) {
                continue;
            }
            aliases.add(value);
        }
        this.aliases = aliases;
        final HashMap<String, ActionInfo> actionMap = new HashMap<>();
        final Method[] methods = JavaAccessor.getMethods(owner);
        int pathDepth = 0;
        for (int index = 0; index < methods.length; index++) {
            Method method = methods[index];
            Action[] actions = JavaAccessor.getAnnotations(method, Action.class);
            if (actions.length == 0) {
                continue;
            }
            ActionInfo info = new ActionInfo(this, method);
            for (int idx = 0; idx < actions.length; idx++) {
                String path = despacePath(actions[idx].path());
                if (actionMap.containsKey(path)) {
                    continue;
                }
                actionMap.put(path, info);
                int depth = resolveDepth(path);
                if (depth > pathDepth) {
                    pathDepth = depth;
                }
            }
        }
        this.pathDepth = pathDepth;
        this.actions = Collections.unmodifiableMap(actionMap);
    }

    private String despacePath(String path) {
        if (!(path = path.trim()).contains(" ")) {
            return path;
        }
        String[] parts = path.split(" ");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isBlank()) {
                continue;
            }
            output.append(part);
            if (i + 1 != parts.length) {
                output.append(' ');
            }
        }
        return output.toString().toLowerCase();
    }

    private int resolveDepth(String path) {
        if (path.isEmpty()) {
            return 0;
        }
        if (!path.contains(" ")) {
            return 1;
        }
        return path.split(" ").length;
    }
    
    public Object instance() {
        return instance;
    }

    public Class<?> owner() {
        return owner;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public List<String> aliases() {
        return aliases;
    }

    public int pathDepth() {
        return pathDepth;
    }

    public Map<String, ActionInfo> actions() {
        return actions;
    }

}
