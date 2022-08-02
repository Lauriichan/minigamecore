package me.lauriichan.minecraft.minigame.data.automatic.message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

import me.lauriichan.minecraft.minigame.util.BukkitColor;
import me.lauriichan.minecraft.minigame.util.JavaAccess;
import me.lauriichan.minecraft.minigame.util.Placeholder;
import me.lauriichan.minecraft.minigame.util.Reference;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public final class Text {

    private static final ArrayList<Text> VALUES = new ArrayList<>();
    private static final ArrayList<String> IDS = new ArrayList<>();

    private static Logger LOGGER;

    public static void setLogger(final Logger logger) {
        if (LOGGER != null) {
            return;
        }
        LOGGER = logger;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void load(final Class<?> clazz) {
        final Field[] fields = JavaAccess.getFields(clazz);
        for (final Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != Text.class) {
                continue;
            }
            final Message message = JavaAccess.getAnnotation(field, Message.class);
            if (IDS.contains(message.id())) {
                continue;
            }
            JavaAccess.setStaticValue(field, register(message.id(), message.content()));
        }
    }

    public static Text get(final String id) {
        final int idx = IDS.indexOf(id);
        if (idx == -1) {
            return null;
        }
        return VALUES.get(idx);
    }

    public static Text[] values() {
        return VALUES.toArray(new Text[VALUES.size()]);
    }

    public static Text register(final String id, final String... fallback) {
        if (fallback.length == 0) {
            return register(id, "");
        }
        final StringBuilder builder = new StringBuilder(fallback[0]);
        for (int index = 1; index < fallback.length; index++) {
            builder.append('\n').append(fallback[index]);
        }
        return register(id, builder.toString());
    }

    public static Text register(final String id, final String fallback) {
        if (IDS.contains(id)) {
            throw new IllegalArgumentException("Id has to be unique! (" + id + ")");
        }
        final Text message = new Text(id, fallback);
        IDS.add(id);
        VALUES.add(message);
        return message;
    }

    private final String id;
    private final String fallback;

    private final Reference<String> translation = Reference.of();

    private Text(final String id, final String fallback) {
        this.id = id;
        this.fallback = fallback;
    }

    public void send(final CommandSender sender, final Key... placeholders) {
        String string = asColoredMessageString(placeholders);
        if (string.contains("\n")) {
            sender.sendMessage(string.split("\n"));
            return;
        }
        sender.sendMessage(string);
    }

    public void send(final CommandSender sender, final ClickEvent clickEvent, final HoverEvent hoverEvent, final Key... placeholders) {
        final BaseComponent[] components = TextComponent.fromLegacyText(asColoredMessageString(placeholders), ChatColor.GRAY);
        for (BaseComponent component : components) {
            component.setClickEvent(clickEvent);
            component.setHoverEvent(hoverEvent);
        }
        sender.spigot().sendMessage(components);
    }

    public void sendConsole(final Key... placeholders) {
        send(Bukkit.getConsoleSender(), placeholders);
    }

    public void log(final Level level, final Throwable throwable, final Key... placeholders) {
        final CommandSender sender = Bukkit.getConsoleSender();
        final String name = levelName(level);
        sender.sendMessage(name + asColoredMessageString(placeholders));
        final String[] lines = Exceptions.stackTraceToStringArray(throwable);
        for (final String line : lines) {
            sender.sendMessage(name + line);
        }
    }

    public void log(final Level level, final Key... placeholders) {
        Bukkit.getConsoleSender().sendMessage(levelName(level) + asColoredMessageString(placeholders));
    }

    private String levelName(final Level level) {
        String color;
        switch (level.intValue()) {
        case 1000:
            color = "&c";
            break;
        case 900:
            color = "&e";
            break;
        default:
            color = "&7";
            break;
        }
        return BukkitColor.apply(color + '[' + level.getName() + "]: ");
    }

    public String asStrippedMessageString(final Key... placeholders) {
        return BukkitColor.stripPlain(asMessageString(placeholders));
    }

    public String asColoredMessageString(final Key... placeholders) {
        return BukkitColor.apply(asMessageString(placeholders));
    }

    public BaseComponent[] asColoredMessageComponent(final Key... placeholders) {
        return new BaseComponent[] {
            new TextComponent(BukkitColor.apply(asMessageString(placeholders)))
        };
    }

    public String asMessageString(final Key... placeholders) {
        return apply(asString(), placeholders);
    }

    private String apply(String output, final Key[] placeholders) {
        final Placeholder[] values = Placeholder.parse(output);
        if (values.length == 0) {
            return output;
        }
        for (final Placeholder value : values) {
            if (value.isText()) {
                final Text text = get(value.getId());
                if (text == null) {
                    continue;
                }
                output = value.replace(output, text.asMessageString(placeholders));
                continue;
            }
            for (final Key key : placeholders) {
                if (!value.getId().equals(key.getKey())) {
                    continue;
                }
                String content = key.getValueOrDefault("null").toString();
                if (!"null".equals(content)) {
                    content = apply(content, placeholders);
                }
                output = value.replace(output, content);
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
