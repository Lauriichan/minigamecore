package me.lauriichan.minecraft.minigame.data.automatic.message;

import me.lauriichan.minecraft.minigame.MinigameCore;
import me.lauriichan.minecraft.minigame.data.config.json.JsonConfig;
import me.lauriichan.minecraft.minigame.data.io.ConfigReloadable;

final class MessageConfiguration extends ConfigReloadable<JsonConfig> {

    private final MessageManager messageManager;

    public MessageConfiguration(final MessageManager messageManager, final MinigameCore core) {
        super(JsonConfig.class, core.getResources().fileData("message.json").getSource());
        this.messageManager = messageManager;
    }

    @Override
    protected void onConfigLoad() throws Throwable {
        Message[] messages = Message.values();
        for (Message message : messages) {
            String value = config.getValue(message.getId(), String.class);
            if (value == null) {
                config.setValue(message.getId(), message.getFallback());
                message.setTranslation(null);
                continue;
            }
            message.setTranslation(value);
        }
        messageManager.update();
    }

}
