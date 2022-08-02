package me.lauriichan.minecraft.minigame.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import me.lauriichan.minecraft.minigame.command.annotation.Command;
import me.lauriichan.minecraft.minigame.data.io.Reloadable;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.AnnotationTools;
import me.lauriichan.minecraft.minigame.util.JavaAccess;
import me.lauriichan.minecraft.minigame.util.MinecraftConstant;
import me.lauriichan.minecraft.minigame.util.source.DataSource;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public final class CommandManager implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final Logger logger;
    private final ParserManager parserManager;
    private final InjectManager injectManager;
    private final HashMap<String, CommandInfo> commandMap = new HashMap<>();

    private final Constructor<?> pluginCommandConstructor = JavaAccess.getConstructor(PluginCommand.class, String.class, Plugin.class);
    private final Method craftServerGetCommandMap = JavaAccess
        .getMethod(JavaAccess.findClass(MinecraftConstant.CRAFTBUKKIT_PACKAGE + "CraftServer"), "getCommandMap");
    private final Method commandMapGetCommands = JavaAccess.getMethod(SimpleCommandMap.class, "knownCommands");

    private ICommandMessageProvider messageProvider = ICommandMessageProvider.NOP;

    public CommandManager(final Plugin plugin, final ParserManager parserManager, final InjectManager injectManager) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.parserManager = parserManager;
        this.injectManager = injectManager;
    }

    public ICommandMessageProvider getMessageProvider() {
        return messageProvider;
    }

    public void setMessageProvider(ICommandMessageProvider messageProvider) {
        this.messageProvider = messageProvider == null ? ICommandMessageProvider.NOP : messageProvider;
    }

    public CommandInfo getCommand(final String alias) {
        return commandMap.get(alias);
    }

    public boolean load(final Resources resources) {
        return load(resources.pathAnnotation(Command.class));
    }

    public boolean load(final DataSource data) {
        unload();
        final boolean loaded = AnnotationTools.load(data, clazz -> {
            final Command commandInfo = JavaAccess.getAnnotation(clazz, Command.class);
            if (commandInfo == null) {
                return;
            }
            final CommandInfo command = new CommandInfo(clazz, injectManager.initialize(clazz), commandInfo);
            commandMap.put(command.name(), command);
            for (final String alias : command.aliases()) {
                commandMap.put(alias, command);
            }
        });
        if (!loaded) {
            return loaded;
        }
        inject();
        return loaded;
    }

    public void unload() {
        if (commandMap.isEmpty()) {
            return;
        }
        try {
            uninject();
        } catch (final Throwable throwable) {
            // Ignore because server is probably shutting down
        }
        commandMap.clear();
    }

    @SuppressWarnings("unchecked")
    private void uninject() {
        final SimpleCommandMap commandMap = (SimpleCommandMap) JavaAccess.invoke(Bukkit.getServer(), craftServerGetCommandMap);
        final Map<String, org.bukkit.command.Command> map = (Map<String, org.bukkit.command.Command>) JavaAccess.invoke(commandMap,
            commandMapGetCommands);
        final String[] names = this.commandMap.keySet().toArray(new String[0]);
        final String prefix = plugin.getName().toLowerCase();
        for (final String name : names) {
            org.bukkit.command.Command command = map.remove(name);
            if (command instanceof PluginCommand && ((PluginCommand) command).getPlugin().equals(plugin)) {
                command.unregister(commandMap);
            }
            command = map.remove(prefix + ':' + name);
            if (command != null) {
                command.unregister(commandMap);
            }
        }
    }

    private void inject() {
        final SimpleCommandMap commandMap = (SimpleCommandMap) JavaAccess.invoke(Bukkit.getServer(), craftServerGetCommandMap);
        final Set<CommandInfo> infos = this.commandMap.values().stream().collect(Collectors.toSet());
        final String prefix = plugin.getName().toLowerCase();
        for (final CommandInfo info : infos) {
            final PluginCommand command = (PluginCommand) JavaAccess.instance(pluginCommandConstructor, info.name(), plugin);
            command.setAliases(new ArrayList<>(info.aliases()));
            command.setDescription(info.description());
            command.setExecutor(this);
            command.setTabCompleter(this);
            commandMap.register(prefix, command);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, String label, final String[] args) {
        if (label.contains(":")) {
            label = label.split(":", 2)[1];
        }
        final CommandInfo info = commandMap.get(label.toLowerCase());
        final Map<String, ActionInfo> actions = info.actions();
        final int maxDepth = Math.min(info.pathDepth(), args.length);
        ActionInfo action = null;
        int offset = 0;
        for (int depth = maxDepth; depth >= -1; depth--) {
            action = actions.get(path(args, depth));
            if (action == null) {
                continue;
            }
            offset = depth;
            break;
        }
        if (action == null) {
            messageProvider.onNonExistent(sender, args.length == 0 ? label : label + ' ',
                Arrays.stream(args).collect(Collectors.joining(" ")));
            return false;
        }
        final ParseResult result = parserManager.parseCommand(action, sender, offset, args);
        if (result.getArguments() == null) {
            if (result.getType() == null) {
                messageProvider.onInvalid(sender, args.length == 0 ? label : label + ' ',
                    Arrays.stream(args).collect(Collectors.joining(" ")));
                return false;
            }
            if (result.getIndex() < 1) {
                messageProvider.onUnsupportedSender(sender, JavaAccess.getClassName(result.getType()));
                return false;
            }
            messageProvider.onMissingArgument(sender, result.getName(), JavaAccess.getClassName(result.getType()), result.getIndex());
            return false;
        }
        try {
            JavaAccess.invokeThrows(info.instance(), action.method(), result.getArguments());
        } catch (final Throwable throwable) {
            if (Reloadable.DEBUG) {
                logger.log(Level.SEVERE, "Failed to execute command", throwable);
            }
            messageProvider.onFail(sender, args.length == 0 ? label : label + ' ', Arrays.stream(args).collect(Collectors.joining(" ")));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final org.bukkit.command.Command command, String label,
        final String[] args) {
        if (label.contains(":")) {
            label = label.split(":", 2)[1];
        }
        final CommandInfo info = commandMap.get(label.toLowerCase());
        final Map<String, ActionInfo> actions = info.actions();
        final int maxDepth = Math.min(info.pathDepth(), args.length);
        ActionInfo action = null;
        int offset = 0;
        for (int depth = maxDepth; depth >= -1; depth--) {
            action = actions.get(path(args, depth));
            if (action == null) {
                continue;
            }
            offset = depth;
            break;
        }
        final ArrayList<String> suggestions = new ArrayList<>();
        try {
            if (action == null) {
                suggestions.addAll(actions.keySet());
                return suggestions;
            }
            parserManager.suggestCommand(action, sender, offset, args, suggestions);
            return suggestions;
        } finally {
            final String arg = args[args.length - 1];
            for (int index = 0; index < suggestions.size(); index++) {
                final String suggestion = suggestions.get(index);
                if (suggestion.startsWith(arg) || suggestion.endsWith(arg) || suggestion.contains(arg)) {
                    continue;
                }
                suggestions.remove(index--);
            }
            Collections.sort(suggestions, new ArgComparator(arg));
        }
    }

    private String path(final String[] args, final int depth) {
        if (depth == -1) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            builder.append(args[i]);
            if (i + 1 != depth) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

}
