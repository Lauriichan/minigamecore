
package me.lauriichan.minecraft.minigame;

import org.bukkit.plugin.java.JavaPlugin;

import me.lauriichan.minecraft.minigame.command.CommandManager;
import me.lauriichan.minecraft.minigame.command.ParserManager;
import me.lauriichan.minecraft.minigame.util.Reference;
import me.lauriichan.minecraft.minigame.util.source.Resources;

public abstract class MinigameCore extends JavaPlugin {

    protected final ParserManager parserManager = new ParserManager(getLogger());
    protected final CommandManager commandManager = new CommandManager(this, parserManager);

    private final Reference<Resources> resources = Reference.of();

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
        
        resources.set(null);
        onPluginDisable();
    }

    public final Resources getResources() {
        if (resources.isPresent()) {
            return resources.get();
        }
        return resources.set(new Resources(getDataFolder(), getFile(), getLogger())).get();
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
     * Runs on plugin enable after the minigame core code
     * 
     * Is normally not required by most minigames
     */
    protected void onPluginEnable() {}

    /**
     * Runs on plugin disable after the minigame core code
     * 
     * Is normally not required by most minigames
     */
    protected void onPluginDisable() {}

    /**
     * Runs on plugin load after the minigame core code
     * 
     * Is normally not required by most minigames
     */
    protected void onPluginLoad() {}

}
