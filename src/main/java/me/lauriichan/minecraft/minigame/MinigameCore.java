
package me.lauriichan.minecraft.minigame;

import org.bukkit.plugin.java.JavaPlugin;

import me.lauriichan.minecraft.minigame.command.CommandManager;
import me.lauriichan.minecraft.minigame.command.ParserManager;
import me.lauriichan.minecraft.minigame.config.ConfigManager;
import me.lauriichan.minecraft.minigame.game.GameManager;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.listener.ListenerManager;
import me.lauriichan.minecraft.minigame.util.JavaInstance;
import me.lauriichan.minecraft.minigame.util.Reference;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public abstract class MinigameCore extends JavaPlugin {

    protected final InjectManager injectManager = new InjectManager(getLogger());

    protected final ParserManager parserManager = new ParserManager(getLogger(), injectManager);
    protected final CommandManager commandManager = new CommandManager(this, parserManager, injectManager);

    protected final ConfigManager configManager = new ConfigManager(this, injectManager);
    protected final GameManager gameManager = new GameManager(getLogger(), injectManager);

    protected final ListenerManager listenerManager = new ListenerManager(this, gameManager, injectManager);

    private final Reference<Resources> resources = Reference.of();

    public MinigameCore() {
        JavaInstance.put(this);
        JavaInstance.put(getLogger());
        JavaInstance.put(injectManager);
        JavaInstance.put(parserManager);
        JavaInstance.put(commandManager);
        JavaInstance.put(configManager);
        JavaInstance.put(gameManager);
    }

    @Override
    public final void onLoad() {
        commandManager.load(getResources());
        parserManager.load(getResources());
        onPluginLoad();
    }

    @Override
    public final void onEnable() {

        onPluginEnable();
    }

    @Override
    public final void onDisable() {
        onPluginDisable();
        resources.set(null);
        JavaInstance.clear();
    }

    public final Resources getResources() {
        if (resources.isPresent()) {
            return resources.get();
        }
        Resources resource = new Resources(getDataFolder(), getFile(), getLogger());
        JavaInstance.put(resource);
        return resources.set(resource).get();
    }

    public final ParserManager getParserManager() {
        return parserManager;
    }

    public final CommandManager getCommandManager() {
        return commandManager;
    }

    /*
     * Delegate
     */

    /**
     * Runs on plugin enable after the minigame core code Is normally not required
     * by most minigames
     */
    protected void onPluginEnable() {}

    /**
     * Runs on plugin disable before the minigame core code Is normally not required
     * by most minigames
     */
    protected void onPluginDisable() {}

    /**
     * Runs on plugin load after the minigame core code Is normally not required by
     * most minigames
     */
    protected void onPluginLoad() {}

}
