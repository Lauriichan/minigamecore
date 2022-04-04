package me.lauriichan.minecraft.minigame.data.automatic.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import org.bukkit.configuration.file.YamlConfiguration;

import me.lauriichan.minecraft.minigame.MinigameCore;
import me.lauriichan.minecraft.minigame.inject.InjectListener;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;

public final class ConfigManager implements InjectListener {

    private static final Function<String, ArrayList<IConfigSync>> LIST_FUNCTION = (ignore) -> new ArrayList<>();

    private final MinigameCore core;

    private final HashMap<String, FileConfig> configurations = new HashMap<>();
    private final HashMap<String, ArrayList<IConfigSync>> synchronize = new HashMap<>();

    public ConfigManager(final MinigameCore core, final InjectManager inject) {
        inject.listen(this);
        this.core = core;
    }

    @Override
    public void onInjectClass(Class<?> type) {
        Field[] fields = JavaAccessor.getFields(type);
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            syncValue(null, field);
            syncSection(null, field);
        }
    }

    @Override
    public void onInjectInstance(Class<?> type, Object instance) {
        Field[] fields = JavaAccessor.getFields(type);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            syncValue(instance, field);
            syncSection(instance, field);
        }
    }

    private void syncValue(Object instance, Field field) {
        Config configInfo = JavaAccessor.getAnnotation(field, Config.class);
        if (configInfo == null) {
            return;
        }
        ConfigSync sync = new ConfigSync(instance, field);
        sync.update(get(sync.getPath()).getConfig().get(sync.getKey()));
        synchronize.computeIfAbsent(sync.getPath(), LIST_FUNCTION).add(sync);
    }

    private void syncSection(Object instance, Field field) {
        ConfigSection configInfo = JavaAccessor.getAnnotation(field, ConfigSection.class);
        if (configInfo == null) {
            return;
        }
        ConfigSectionSync sync = new ConfigSectionSync(instance, field);
        sync.update(get(sync.getPath()).getConfig().get(sync.getKey()));
        synchronize.computeIfAbsent(sync.getPath(), LIST_FUNCTION).add(sync);
    }

    private FileConfig get(String path) {
        if (!configurations.containsKey(path)) {
            return configurations.get(path);
        }
        FileConfig config = new FileConfig(path, core.getResources().fileData(path).getSource(), core.getLogger(), this);
        configurations.put(path, config);
        config.load();
        return config;
    }

    void update(String path, YamlConfiguration configuration) {
        ArrayList<IConfigSync> values = synchronize.get(path);
        if (values == null || values.isEmpty()) {
            return;
        }
        for (int index = 0; index < values.size(); index++) {
            IConfigSync sync = values.get(index);
            sync.update(sync.extractValue(configuration, sync.getKey()));
        }
    }

}
