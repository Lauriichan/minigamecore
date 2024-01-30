package me.lauriichan.minecraft.minigame.data.config.yaml;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

class YamlConfigV1 extends YamlSectionV1 implements IYamlConfig {

    public YamlConfigV1() {
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
