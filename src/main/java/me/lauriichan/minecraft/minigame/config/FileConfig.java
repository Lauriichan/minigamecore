package me.lauriichan.minecraft.minigame.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

final class FileConfig {
    
    private final String path;
    
    private final File file;
    private final YamlConfiguration configuration = new YamlConfiguration();

    private final Logger logger;
    private final ConfigManager configManager;

    public FileConfig(final String path, final File file, final Logger logger, final ConfigManager configManager) {
        this.path = path;
        this.file = file;
        this.logger = logger;
        this.configManager = configManager;
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException exp) {
            logger.log(Level.WARNING, "Failed to save config '" + file.getName() + "'!", exp);
        }
    }

    public void load() {
        try {
            if (file.exists()) {
                configuration.load(file);
            }
        } catch (IOException | InvalidConfigurationException exp) {
            logger.log(Level.WARNING, "Failed to load config '" + file.getName() + "'!", exp);
        }
        configManager.update(path, configuration);
    }

    public void reload() {
        load();
        save();
    }
    
    public YamlConfiguration getConfig() {
        return configuration;
    }

    public void clear() {
        String[] keys = configuration.getKeys(false).toArray(String[]::new);
        for (String key : keys) {
            configuration.set(key, null);
        }
    }

}
