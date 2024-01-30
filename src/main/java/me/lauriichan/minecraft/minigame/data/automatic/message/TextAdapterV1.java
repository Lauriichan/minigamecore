package me.lauriichan.minecraft.minigame.data.automatic.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

final class TextAdapterV1 extends TextAdapter {

    @Override
    void send(String message, CommandSender sender, ClickEvent clickEvent, HoverEvent hoverEvent) {
        if (sender instanceof Player) {
            final BaseComponent[] components = TextComponent.fromLegacyText(message);
            for (BaseComponent component : components) {
                component.setClickEvent(clickEvent);
                component.setHoverEvent(hoverEvent);
            }
            ((Player) sender).spigot().sendMessage(components);
            return;
        }
        sender.sendMessage(message);
    }

}
