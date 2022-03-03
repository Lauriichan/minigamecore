package me.lauriichan.minecraft.minigame.command;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import me.lauriichan.minecraft.minigame.command.annotation.Command;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;
import me.lauriichan.minecraft.minigame.util.MinecraftConstant;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public final class CommandManager implements CommandExecutor {

    private final Plugin plugin;
    private final ParserManager parser;
    private final InjectManager inject;
    private final HashMap<String, CommandInfo> commandMap = new HashMap<>();

    private final MethodHandle pluginCommandConstructor = JavaAccessor
        .accessConstructor(JavaAccessor.getConstructor(PluginCommand.class, String.class, Plugin.class));
    private final MethodHandle craftCommandMapGetCommands = JavaAccessor.accessMethod(JavaAccessor
        .getMethod(JavaAccessor.getClass(MinecraftConstant.CRAFTBUKKIT_PACKAGE + "command.CraftCommandMap"), "getKnownCommands"));
    private final MethodHandle craftServerGetCommandMap = JavaAccessor
        .accessMethod(JavaAccessor.getMethod(JavaAccessor.getClass("CraftServer"), "getCommandMap"));

    public CommandManager(final Plugin plugin, final ParserManager parser, final InjectManager inject) {
        this.plugin = plugin;
        this.parser = parser;
        this.inject = inject;
    }

    public boolean load(final Resources resources) {
        return load(resources.pathAnnotation(Command.class));
    }

    public boolean load(final DataSource data) {
        if (!commandMap.isEmpty()) {
            uninject();
            commandMap.clear();
        }
        boolean loaded = AnnotationTools.load(data, clazz -> {
            Command commandInfo = JavaAccessor.getAnnotation(clazz, Command.class);
            if (commandInfo == null) {
                return;
            }
            inject.inject(clazz);
            CommandInfo command = new CommandInfo(clazz, inject.initialize(clazz), commandInfo);
            inject.inject(command.instance());
            commandMap.put(command.name(), command);
            for (String alias : command.aliases()) {
                commandMap.put(alias, command);
            }
        });
        if (!loaded) {
            return loaded;
        }
        inject();
        return loaded;
    }

    @SuppressWarnings("unchecked")
    private void uninject() {
        final SimpleCommandMap commandMap = (SimpleCommandMap) JavaAccessor.invoke(Bukkit.getServer(), craftServerGetCommandMap);
        final Map<String, org.bukkit.command.Command> map = (Map<String, org.bukkit.command.Command>) JavaAccessor.invoke(commandMap,
            craftCommandMapGetCommands);
        final String[] names = this.commandMap.keySet().toArray(String[]::new);
        final String prefix = plugin.getName().toLowerCase();
        for (String name : names) {
            org.bukkit.command.Command command = map.remove(name);
            if (command != null && command instanceof PluginCommand && ((PluginCommand) command).getPlugin().equals(plugin)) {
                command.unregister(commandMap);
            }
            command = map.remove(prefix + ':' + name);
            if (command != null) {
                command.unregister(commandMap);
            }
        }
    }

    private void inject() {
        final SimpleCommandMap commandMap = (SimpleCommandMap) JavaAccessor.invoke(Bukkit.getServer(), craftServerGetCommandMap);
        final Set<CommandInfo> infos = this.commandMap.values().stream().collect(Collectors.toSet());
        final String prefix = plugin.getName().toLowerCase();
        for (CommandInfo info : infos) {
            PluginCommand command = (PluginCommand) JavaAccessor.invokeStatic(pluginCommandConstructor, info.name(), plugin);
            command.setAliases(new ArrayList<>(info.aliases()));
            command.setDescription(info.description());
            command.setExecutor(this);
            commandMap.register(prefix, command);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (label.contains(":")) {
            label = label.split(":", 2)[1];
        }
        CommandInfo info = commandMap.get(label.toLowerCase());
        Map<String, ActionInfo> actions = info.actions();
        int maxDepth = Math.min(info.pathDepth(), args.length);
        ActionInfo action = null;
        int offset = 0;
        for (int depth = maxDepth; depth >= -1; depth++) {
            action = actions.get(path(args, depth, maxDepth));
            if (action == null) {
                continue;
            }
            offset = depth + 1;
            break;
        }
        if (action == null) {
            // TODO: Command doesn't exist message
            return false;
        }
        Object[] arguments = parser.parseCommand(action, sender, offset, args);
        if (arguments == null) {
            // TODO: Command too less arguments message
            return false;
        }
        JavaAccessor.invoke(info.instance(), action.method(), arguments);
        return false;
    }

    private String path(String[] args, int depth, int maxDepth) {
        if (depth == -1) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maxDepth; i++) {
            builder.append(args[i]);
            if (i + 1 != maxDepth) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

}
