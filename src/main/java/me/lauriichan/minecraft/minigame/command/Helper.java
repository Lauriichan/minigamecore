package me.lauriichan.minecraft.minigame.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import me.lauriichan.minecraft.minigame.data.automatic.message.Key;
import me.lauriichan.minecraft.minigame.util.JavaAccess;
import me.lauriichan.minecraft.minigame.util.JavaInstance;

public final class Helper {

    private static class Help {

        private final ArrayList<String> names = new ArrayList<>();
        private final ActionInfo action;

        public Help(final ActionInfo action) {
            this.action = action;
        }

        public ActionInfo getAction() {
            return action;
        }

        public ArrayList<String> getNames() {
            return names;
        }

    }

    private static final ConcurrentHashMap<String, Helper> HELPERS = new ConcurrentHashMap<>();

    public static Helper getHelper(final String commandName) {
        if (HELPERS.containsKey(commandName)) {
            return HELPERS.get(commandName);
        }
        final CommandManager commandManager = JavaInstance.get(CommandManager.class);
        final CommandInfo command = commandManager.getCommand(commandName);
        if (command == null) {
            return null;
        }
        try {
            final Helper helper = new Helper(commandManager, command);
            HELPERS.put(commandName, helper);
            return helper;
        } catch (final Exception exp) {
            return null;
        }
    }

    private final Help[] paths;
    private final CommandInfo command;
    private final CommandManager commandManager;

    private Helper(final CommandManager commandManager, final CommandInfo command) {
        this.commandManager = commandManager;
        final Map<String, ActionInfo> actions = command.actions();
        final ArrayList<Help> pathList = new ArrayList<>();
        for (final Map.Entry<String, ActionInfo> entry : actions.entrySet()) {
            Help help = pathList.stream().filter(test -> test.getAction() == entry.getValue()).findFirst().orElse(null);
            if (help == null) {
                help = new Help(entry.getValue());
                pathList.add(help);
            }
            help.getNames().add(entry.getKey());
        }
        this.command = command;
        this.paths = pathList.toArray(new Help[pathList.size()]);
    }

    public void show(final CommandSender sender) {
        ICommandMessageProvider provider = commandManager.getMessageProvider();
        for (final Help help : paths) {
            String path = help.getNames().stream().collect(Collectors.joining("|"));
            if (!path.isEmpty()) {
                path += ' ';
            }
            final List<ArgumentInfo> argumentInfos = help.getAction().sortedArguments();
            final ArrayList<String> parts = new ArrayList<>();
            for (final ArgumentInfo info : argumentInfos) {
                if (info.sender()) {
                    continue;
                }
                if (info.optional()) {
                    parts.add(provider.helpOptionalFor(sender, Key.of("name", info.name()),
                        Key.of("type", JavaAccess.getClassName(info.type()))));
                    continue;
                }
                parts.add(
                    provider.helpRequiredFor(sender, Key.of("name", info.name()), Key.of("type", JavaAccess.getClassName(info.type()))));
            }
            if (parts.isEmpty()) {
                provider.sendHelp(sender, Key.of("name", command.name()), Key.of("path", path), Key.of("arguments", ""),
                    Key.of("description", help.getAction().description()));
                continue;
            }
            provider.sendHelp(sender, Key.of("name", command.name()), Key.of("path", path),
                Key.of("arguments", parts.stream().collect(Collectors.joining(" ")) + ' '),
                Key.of("description", help.getAction().description()));
        }
    }

}
