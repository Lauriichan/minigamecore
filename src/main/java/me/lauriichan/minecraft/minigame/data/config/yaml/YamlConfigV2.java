package me.lauriichan.minecraft.minigame.data.config.yaml;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

class YamlConfigV2 extends YamlSectionV2 implements IYamlConfig {

    public YamlConfigV2() {
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
