package me.lauriichan.minecraft.minigame.command;

import org.bukkit.command.CommandSender;

import me.lauriichan.minecraft.minigame.data.automatic.message.Key;

public interface ICommandMessageProvider {

    public static final ICommandMessageProvider NOP = new ICommandMessageProvider() {};

    default void onFail(CommandSender sender, String label, String command) {}

    default void onMissingArgument(CommandSender sender, String name, String type, int index) {}

    default void onUnsupportedSender(CommandSender sender, String type) {}

    default void onInvalid(CommandSender sender, String label, String command) {}

    default void onNonExistent(CommandSender sender, String label, String command) {}

    default String helpOptionalFor(CommandSender sender, Key... placeholders) {
        return "";
    }

    default String helpRequiredFor(CommandSender sender, Key... placeholders) {
        return "";
    }

    default void sendHelp(CommandSender sender, Key... placeholders) {}

}
