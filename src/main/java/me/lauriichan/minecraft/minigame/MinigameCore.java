
package me.lauriichan.minecraft.minigame;

import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import me.lauriichan.minecraft.minigame.command.CommandManager;
import me.lauriichan.minecraft.minigame.command.ParserManager;
import me.lauriichan.minecraft.minigame.data.automatic.config.ConfigManager;
import me.lauriichan.minecraft.minigame.data.automatic.message.Message;
import me.lauriichan.minecraft.minigame.data.automatic.message.MessageManager;
import me.lauriichan.minecraft.minigame.data.io.IOManager;
import me.lauriichan.minecraft.minigame.data.io.Reloadable;
import me.lauriichan.minecraft.minigame.game.GameManager;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.listener.ListenerManager;
import me.lauriichan.minecraft.minigame.util.JavaInstance;
import me.lauriichan.minecraft.minigame.util.Reference;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public final class MinigameCore {

    private final InjectManager injectManager;

    private final ParserManager parserManager;
    private final CommandManager commandManager;

    private final GameManager gameManager;
    private final ListenerManager listenerManager;

    private final IOManager ioManager;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    private final Logger logger;

    private final Supplier<File> fileSupplier;
    private final File dataFolder;

    private final Reference<Resources> resources = Reference.of();

    public MinigameCore(final JavaPlugin plugin, final Supplier<File> fileSupplier) {
        this.fileSupplier = fileSupplier;
        this.logger = plugin.getLogger();
        this.dataFolder = plugin.getDataFolder();
        this.injectManager = new InjectManager(logger);
        this.parserManager = new ParserManager(logger, injectManager);
        this.commandManager = new CommandManager(plugin, parserManager, injectManager);
        this.ioManager = new IOManager(injectManager);
        this.configManager = new ConfigManager(this, injectManager);
        this.messageManager = new MessageManager(this, injectManager);
        this.gameManager = new GameManager(logger, injectManager);
        this.listenerManager = new ListenerManager(plugin, gameManager, injectManager);
        JavaInstance.put(plugin);
        JavaInstance.put(logger);
        JavaInstance.put(injectManager);
        JavaInstance.put(parserManager);
        JavaInstance.put(commandManager);
        JavaInstance.put(ioManager);
        JavaInstance.put(configManager);
        JavaInstance.put(messageManager);
        JavaInstance.put(gameManager);
        injectManager.inject(Message.class);
        injectManager.inject(Reloadable.class);
    }

    public final void load() {
        commandManager.load(getResources());
        parserManager.load(getResources());
    }

    public final void enable(final Runnable runnable) {
        preEnable();
        runnable.run();
        postEnable();
    }

    public final void preEnable() {
        gameManager.load(getResources());
        listenerManager.load(getResources());
    }

    public final void postEnable() {
        injectManager.load(getResources());
    }

    public final void disable(final Runnable runnable) {
        preDisable();
        runnable.run();
        postDisable();
    }

    public final void preDisable() {
        gameManager.getTicker().stop();
    }

    public final void postDisable() {
        resources.set(null);
        JavaInstance.clear();
    }

    public final Resources getResources() {
        if (resources.isPresent()) {
            return resources.get();
        }
        Resources resource = new Resources(dataFolder, fileSupplier.get(), getLogger());
        JavaInstance.put(resource);
        return resources.set(resource).get();
    }

    public final Logger getLogger() {
        return logger;
    }

    public final InjectManager getInjectManager() {
        return injectManager;
    }

    public final ParserManager getParserManager() {
        return parserManager;
    }

    public final CommandManager getCommandManager() {
        return commandManager;
    }

    public final IOManager getIOManager() {
        return ioManager;
    }

    public final ConfigManager getConfigManager() {
        return configManager;
    }

    public final MessageManager getMessageManager() {
        return messageManager;
    }

    public final GameManager getGameManager() {
        return gameManager;
    }

    public final ListenerManager getListenerManager() {
        return listenerManager;
    }

}
