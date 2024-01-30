package me.lauriichan.minecraft.minigame.data.io;

import java.io.File;

import me.lauriichan.minecraft.minigame.data.config.IConfiguration;
import me.lauriichan.minecraft.minigame.data.config.yaml.IYamlConfig;
import me.lauriichan.minecraft.minigame.util.JavaInstance;

public abstract class ConfigReloadable<T extends IConfiguration<?, ?>> extends DataReloadable {

    protected final T config;

    public ConfigReloadable(T config, File file) {
        super(file);
        this.config = config;
    }

    public ConfigReloadable(T config, File file, boolean saveOnExit) {
        super(file, saveOnExit);
        this.config = config;
    }

    public ConfigReloadable(Class<T> clazz, File file) {
        super(file);
        this.config = createConfig(clazz);
    }

    public ConfigReloadable(Class<T> clazz, File file, boolean saveOnExit) {
        super(file, saveOnExit);
        this.config = createConfig(clazz);
    }
    
    private T createConfig(Class<T> clazz) {
        if (IYamlConfig.class.isAssignableFrom(clazz)) {
            return clazz.cast(IYamlConfig.create());
        }
        return JavaInstance.initialize(clazz);
    }

    @Override
    protected final void onLoad() throws Throwable {
        if (!file.exists()) {
            config.clear();
            onConfigLoad();
            return;
        }
        try {
            config.load(file);
        } catch (Throwable throwable) {
            onConfigLoad();
            throw throwable;
        }
        onConfigLoad();
    }

    protected void onConfigLoad() throws Throwable {}

    @Override
    protected final void onSave() throws Throwable {
        onConfigSave();
        config.save(file);
    }

    protected void onConfigSave() throws Throwable {}

}
