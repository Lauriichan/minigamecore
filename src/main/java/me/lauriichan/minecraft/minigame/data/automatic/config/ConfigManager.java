package me.lauriichan.minecraft.minigame.data.automatic.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

import org.bukkit.configuration.file.YamlConfiguration;

import me.lauriichan.minecraft.minigame.MinigameCore;
import me.lauriichan.minecraft.minigame.inject.InjectListener;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.JavaAccess;

public final class ConfigManager implements InjectListener {

    private static final Function<String, ArrayList<IConfigSync>> LIST_FUNCTION = (ignore) -> new ArrayList<>();

    private final MinigameCore core;

    private final HashMap<String, FileConfig> configurations = new HashMap<>();
    private final HashMap<String, ArrayList<IConfigSync>> synchronize = new HashMap<>();

    public ConfigManager(final MinigameCore core, final InjectManager inject) {
        inject.listen(this);
        this.core = core;
    }

    public void reload() {
        FileConfig[] configs = configurations.values().toArray(FileConfig[]::new);
        for (FileConfig config : configs) {
            config.reload();
        }
    }

    public FileConfig getConfig(String path) {
        return configurations.get(path);
    }

    @Override
    public void onInjectClass(Class<?> type) {
        Field[] fields = JavaAccess.getFields(type);
        HashSet<String> path = new HashSet<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            path.add(syncValue(null, field));
            path.add(syncSection(null, field));
        }
        for (String current : path) {
            if (current == null) {
                continue;
            }
            get(current).reload();
        }
    }

    @Override
    public void onInjectInstance(Class<?> type, Object instance) {
        Field[] fields = JavaAccess.getFields(type);
        HashSet<String> path = new HashSet<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            path.add(syncValue(instance, field));
            path.add(syncSection(instance, field));
        }
        for (String current : path) {
            if (current == null) {
                continue;
            }
            get(current).reload();
        }
    }

    private String syncValue(Object instance, Field field) {
        Config configInfo = JavaAccess.getAnnotation(field, Config.class);
        if (configInfo == null) {
            return null;
        }
        ConfigSync sync = new ConfigSync(instance, field);
        synchronize.computeIfAbsent(sync.getPath(), LIST_FUNCTION).add(sync);
        return sync.getPath();
    }

    private String syncSection(Object instance, Field field) {
        ConfigSection configInfo = JavaAccess.getAnnotation(field, ConfigSection.class);
        if (configInfo == null) {
            return null;
        }
        ConfigSectionSync sync = new ConfigSectionSync(instance, field);
        synchronize.computeIfAbsent(sync.getPath(), LIST_FUNCTION).add(sync);
        return sync.getPath();
    }

    private FileConfig get(String path) {
        if (configurations.containsKey(path)) {
            return configurations.get(path);
        }
        FileConfig config = new FileConfig(path, core.getResources().fileData(path).getSource(), core.getLogger(), this);
        configurations.put(path, config);
        return config;
    }

    void update(String path, YamlConfiguration configuration) {
        ArrayList<IConfigSync> values = synchronize.get(path);
        if (values == null || values.isEmpty()) {
            return;
        }
        for (int index = 0; index < values.size(); index++) {
            IConfigSync sync = values.get(index);
            Object value = sync.extractValue(configuration, sync.getKey());
            if (value == null) {
                if (sync.setFallback(configuration, sync.getKey())) {
                    sync.update(sync.extractValue(configuration, sync.getKey()));
                }
                continue;
            }
            sync.update(value);
        }
    }

}
