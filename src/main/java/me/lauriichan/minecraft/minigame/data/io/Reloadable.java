package me.lauriichan.minecraft.minigame.data.io;

import java.io.File;

import me.lauriichan.minecraft.minigame.data.automatic.config.Config;
import me.lauriichan.minecraft.minigame.util.DynamicArray;
import me.lauriichan.minecraft.minigame.util.Reference;

public abstract class Reloadable {

    @Config(key = "debug")
    public static boolean DEBUG = false;

    private static final DynamicArray<Reloadable> RELOAD = new DynamicArray<>();
    private static final Reference<Thread> THREAD = Reference.of();

    protected final File file;
    private long lastUpdated = 0L;

    private final boolean saveOnExit;

    public Reloadable(File file, boolean saveOnExit) {
        this.saveOnExit = saveOnExit;
        this.file = file;
        RELOAD.add(this);
    }

    public Reloadable(File file) {
        this(file, false);
    }

    public final File getFile() {
        return file;
    }

    protected void load() {}

    protected void save() {}

    public void forceUpdate() {
        lastUpdated = -1;
        update();
    }

    public void remove() {
        RELOAD.remove(this);
        if (!saveOnExit) {
            return;
        }
        save();
    }

    public static void forceUpdateAll() {
        for (int index = 0; index < RELOAD.length(); index++) {
            RELOAD.get(index).forceUpdate();
        }
    }

    public static void update() {
        for (int index = 0; index < RELOAD.length(); index++) {
            final Reloadable reloadable = RELOAD.get(index);
            if (!reloadable.file.exists()) {
                reloadable.load();
                reloadable.save();
                reloadable.lastUpdated = reloadable.file.lastModified();
                continue;
            }
            if (reloadable.lastUpdated == reloadable.file.lastModified()) {
                continue;
            }
            reloadable.load();
            reloadable.save();
            reloadable.lastUpdated = reloadable.file.lastModified();
        }
    }

    private static void tick() {
        try {
            while (true) {
                update();
                Thread.sleep(5000);
            }
        } catch (InterruptedException exp) {
            return;
        }
    }

    public static void start() {
        if (THREAD.isPresent()) {
            return;
        }
        Thread thread = new Thread(Reloadable::tick);
        thread.setName("Reloadable");
        thread.setDaemon(true);
        THREAD.set(thread);
        thread.start();
    }

    public static void shutdown() {
        if (THREAD.isEmpty()) {
            return;
        }
        THREAD.get().interrupt();
        THREAD.set(null);
        for (int index = 0; index < RELOAD.length(); index++) {
            final Reloadable reloadable = RELOAD.get(index);
            if (!reloadable.saveOnExit) {
                continue;
            }
            reloadable.save();
        }
    }

}
