package me.lauriichan.minecraft.minigame.data.config.yaml;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import me.lauriichan.minecraft.minigame.data.config.IConfiguration;

public class YamlConfig extends YamlSection implements IConfiguration<Object, Class<?>> {

    public YamlConfig() {
        super(new YamlConfiguration(), null);
    }

    @Override
    public void load(File file) throws Throwable {
        ((YamlConfiguration) handle).load(file);
    }

    @Override
    public void save(File file) throws Throwable {
        ((YamlConfiguration) handle).save(file);
    }

}
