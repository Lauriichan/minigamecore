package me.lauriichan.minecraft.minigame.data.config.yaml;

import me.lauriichan.minecraft.minigame.data.config.IConfiguration;
import me.lauriichan.minecraft.minigame.util.MinigameVersion;

public interface IYamlConfig extends IYamlSection, IConfiguration<Object, Class<?>> {

    public static IYamlConfig create() {
        switch (MinigameVersion.VERSION) {
        case V1:
            return new YamlConfigV1();
        default:
            return new YamlConfigV2();
        }
    }

}
