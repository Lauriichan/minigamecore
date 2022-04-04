package me.lauriichan.minecraft.minigame.data.automatic.config;

import org.bukkit.configuration.file.YamlConfiguration;

public interface IConfigSync {
    
    String getPath();
    
    String getKey();
    
    default Object extractValue(YamlConfiguration configuration, String key) {
        return configuration.get(key);
    }
    
    void update(Object value);

}
