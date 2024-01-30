package me.lauriichan.minecraft.minigame.data.automatic.message;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

final class TextAdapterV2 extends TextAdapter {

    @Override
    void send(String message, CommandSender sender, ClickEvent clickEvent, HoverEvent hoverEvent) {
        final BaseComponent[] components = TextComponent.fromLegacyText(message, ChatColor.GRAY);
        for (BaseComponent component : components) {
            component.setClickEvent(clickEvent);
            component.setHoverEvent(hoverEvent);
        }
        sender.spigot().sendMessage(components);
    }

}
