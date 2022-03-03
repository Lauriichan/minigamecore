package me.lauriichan.minecraft.minigame.util;

import org.bukkit.Bukkit;

public final class MinecraftConstant {
    
    public static final String SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.", 4)[3];
    public static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + SERVER_VERSION + ".";

}
