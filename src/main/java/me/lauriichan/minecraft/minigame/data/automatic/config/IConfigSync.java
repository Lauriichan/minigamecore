package me.lauriichan.minecraft.minigame.data.automatic.config;

import org.bukkit.configuration.file.YamlConfiguration;

public interface IConfigSync {

    String getPath();

    String getKey();

    default boolean setFallback(YamlConfiguration configuration, String key) {
        return false;
    }

    default Object extractValue(YamlConfiguration configuration, String key) {
        if (configuration.get(key, null) == null) {
            return null;
        }
        return configuration.get(key);
    }

    void update(Object value);

}
