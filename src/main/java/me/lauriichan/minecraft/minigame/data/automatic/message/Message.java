package me.lauriichan.minecraft.minigame.data.automatic.message;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.lauriichan.minecraft.minigame.data.io.Reloadable;
import me.lauriichan.minecraft.minigame.inject.Inject;
import me.lauriichan.minecraft.minigame.util.BukkitColor;
import me.lauriichan.minecraft.minigame.util.Placeholder;
import me.lauriichan.minecraft.minigame.util.Reference;
import me.lauriichan.minecraft.minigame.util.Tuple;

@SuppressWarnings("unchecked")
public final class Message {

    @Inject
    private static Logger LOGGER;

    private static final ArrayList<Message> VALUES = new ArrayList<>();
    private static final ArrayList<String> IDS = new ArrayList<>();

    public static Message get(final String id) {
        final int idx = IDS.indexOf(id);
        if (idx == -1) {
            return null;
        }
        return VALUES.get(idx);
    }

    public static Message[] values() {
        return VALUES.toArray(Message[]::new);
    }

    public static Message register(final String id, final String... fallback) {
        if (fallback.length == 0) {
            return register(id, "");
        }
        final StringBuilder builder = new StringBuilder(fallback[0]);
        for (int index = 1; index < fallback.length; index++) {
            builder.append('\n').append(fallback[index]);
        }
        return register(id, builder.toString());
    }

    public static Message register(final String id, final String fallback) {
        if (IDS.contains(id)) {
            throw new IllegalArgumentException("Id has to be unique! (" + id + ")");
        }
        final Message message = new Message(id, fallback);
        IDS.add(id);
        VALUES.add(message);
        return message;
    }

    private final String id;
    private final String fallback;

    private final Reference<String> translation = Reference.of();

    private Message(final String id, final String fallback) {
        this.id = id;
        this.fallback = fallback;
    }

    public void sendBroadcast() {
        Bukkit.broadcastMessage(asColoredMessageString());
    }

    public void sendBroadcast(final Tuple<String, Object>... placeholders) {
        Bukkit.broadcastMessage(asColoredMessageString(placeholders));
    }

    public void sendBroadcast(final String permission) {
        Bukkit.broadcast(asColoredMessageString(), permission);
    }

    public void sendBroadcast(final String permission, final Tuple<String, Object>... placeholders) {
        Bukkit.broadcast(asColoredMessageString(placeholders), permission);
    }

    public void sendBroadcast(final World world) {
        final String message = asColoredMessageString();
        for (Player player : world.getPlayers()) {
            player.sendMessage(message);
        }
    }

    public void sendBroadcast(final World world, final Tuple<String, Object>... placeholders) {
        final String message = asColoredMessageString(placeholders);
        for (Player player : world.getPlayers()) {
            player.sendMessage(message);
        }
    }

    public void sendConsole() {
        Bukkit.getConsoleSender().sendMessage(asColoredMessageString());
    }

    public void sendConsole(final Tuple<String, Object>... placeholders) {
        Bukkit.getConsoleSender().sendMessage(asColoredMessageString(placeholders));
    }

    public void send(final CommandSender sender) {
        sender.sendMessage(asColoredMessageString());
    }

    public void send(final CommandSender sender, final Tuple<String, Object>... placeholders) {
        sender.sendMessage(asColoredMessageString(placeholders));
    }

    public void log(final Level level) {
        LOGGER.log(level, asColoredMessageString());
    }

    public void log(final Level level, final Tuple<String, Object>... placeholders) {
        LOGGER.log(level, asColoredMessageString());
    }

    public void log(final Level level, final Throwable throwable) {
        if (Reloadable.DEBUG || level == Level.SEVERE) {
            LOGGER.log(level, asColoredMessageString(), throwable);
            return;
        }
        LOGGER.log(level, asColoredMessageString());
    }

    public void log(final Level level, final Throwable throwable, final Tuple<String, Object>... placeholders) {
        if (Reloadable.DEBUG || level == Level.SEVERE) {
            LOGGER.log(level, asColoredMessageString(placeholders), throwable);
            return;
        }
        LOGGER.log(level, asColoredMessageString(placeholders));
    }

    public String asColoredMessageString(final Tuple<String, Object>... placeholders) {
        return BukkitColor.apply(asMessageString(placeholders));
    }

    public String asStrippedMessageString(final Tuple<String, Object>... placeholders) {
        return BukkitColor.stripPlain(asMessageString(placeholders));
    }

    public String asMessageString(final Tuple<String, Object>... placeholders) {
        String output = asString();
        final Placeholder[] values = Placeholder.parse(output);
        if (values.length == 0) {
            return output;
        }
        for (final Placeholder value : values) {
            if (value.isMessage()) {
                final Message message = get(value.getId());
                if (message == null) {
                    continue;
                }
                output = value.replace(output, message.asMessageString(placeholders));
                continue;
            }
            for (final Tuple<String, Object> tuple : placeholders) {
                if (!value.getId().equals(tuple.getFirst())) {
                    continue;
                }
                output = value.replace(output, tuple.getSecondOrDefault("null").toString());
                break;
            }
        }
        return output;
    }

    public String asString() {
        if (translation.isEmpty()) {
            return fallback;
        }
        return translation.get();
    }

    public void setTranslation(final String value) {
        translation.set(value);
    }

    public String getTranslation() {
        return translation.get();
    }

    public String getId() {
        return id;
    }

    public String getFallback() {
        return fallback;
    }

}
