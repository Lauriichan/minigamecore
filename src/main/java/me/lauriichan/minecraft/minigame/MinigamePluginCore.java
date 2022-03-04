package me.lauriichan.minecraft.minigame;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class MinigamePluginCore extends JavaPlugin {

    protected final MinigameCore core = new MinigameCore(this, this::getFile);

    @Override
    public final void onLoad() {
        onCorePreLoad();
        core.load();
        onCoreLoad();
    }

    @Override
    public final void onEnable() {
        onCoreEnable();
    }

    @Override
    public final void onDisable() {
        core.disable(this::onCoreDisable);
    }

    protected void onCorePreLoad() {}

    protected void onCoreLoad() {}

    protected void onCoreEnable() {}

    protected void onCoreDisable() {}

}
