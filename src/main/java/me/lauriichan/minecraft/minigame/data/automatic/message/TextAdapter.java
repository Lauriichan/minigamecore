package me.lauriichan.minecraft.minigame.data.automatic.message;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

abstract class TextAdapter {

    abstract void send(final String message, final CommandSender sender, final ClickEvent clickEvent, final HoverEvent hoverEvent);

}
