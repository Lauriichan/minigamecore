package me.lauriichan.minecraft.minigame.data.automatic.message;

import com.syntaxphoenix.syntaxapi.json.*;
import com.syntaxphoenix.syntaxapi.json.value.*;

import me.lauriichan.minecraft.minigame.MinigameCore;
import me.lauriichan.minecraft.minigame.data.config.json.JsonConfig;
import me.lauriichan.minecraft.minigame.data.io.ConfigReloadable;

final class MessageConfiguration extends ConfigReloadable<JsonConfig> {

    public MessageConfiguration(final MinigameCore core) {
        super(JsonConfig.class, core.getResources().fileData("core-message.json").getSource());
    }

    @Override
    protected void onConfigLoad() throws Throwable {
        for (final Text text : Text.values()) {
            final String content = get(config.get(text.getId()));
            if (content == null) {
                text.setTranslation(null);
                set(text.getId(), text.getFallback());
                continue;
            }
            text.setTranslation(content);
        }
    }

    private void set(final String id, final String content) {
        if (!content.contains("\n")) {
            config.set(id, new JsonString(content));
            return;
        }
        final JsonArray array = new JsonArray();
        final String[] lines = content.split("\n");
        for (final String line : lines) {
            array.add(new JsonString(line));
        }
        config.set(id, array);
    }

    private String get(final JsonValue<?> value) {
        if (value == null || (!value.hasType(ValueType.STRING) && !value.hasType(ValueType.ARRAY))) {
            return null;
        }
        if (value.hasType(ValueType.STRING)) {
            return value.getValue().toString();
        }
        final JsonArray array = (JsonArray) value;
        final StringBuilder builder = new StringBuilder();
        for (final JsonValue<?> item : array) {
            if (!item.hasType(ValueType.STRING)) {
                continue;
            }
            builder.append(item.getValue().toString()).append('\n');
        }
        if (builder.length() == 0) {
            return "";
        }
        return builder.substring(0, builder.length() - 1);
    }

}
