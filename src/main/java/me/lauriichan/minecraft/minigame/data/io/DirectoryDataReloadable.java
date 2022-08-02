package me.lauriichan.minecraft.minigame.data.io;

import java.io.File;
import java.util.logging.Level;

import me.lauriichan.minecraft.minigame.data.CoreMessages;
import me.lauriichan.minecraft.minigame.data.automatic.message.Key;

public abstract class DirectoryDataReloadable extends Reloadable {

    private final Key name;

    public DirectoryDataReloadable(File file) {
        super(file);
        this.name = Key.of("name", file.getName());
    }

    public DirectoryDataReloadable(File file, boolean saveOnExit) {
        super(file, saveOnExit);
        this.name = Key.of("name", file.getName());
    }

    @Override
    public final void load() {
        CoreMessages.DATA_LOAD_START.log(Level.INFO, name);
        try {
            onLoad();
            CoreMessages.DATA_LOAD_SUCCESS.log(Level.INFO, name);
        } catch (Throwable exp) {
            CoreMessages.DATA_LOAD_FAILED.log(Level.WARNING, exp, name);
        }
    }

    @Override
    public final void save() {
        CoreMessages.DATA_SAVE_START.log(Level.INFO, name);
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            onSave();
            CoreMessages.DATA_SAVE_SUCCESS.log(Level.INFO, name);
        } catch (Throwable exp) {
            CoreMessages.DATA_SAVE_FAILED.log(Level.WARNING, exp, name);
        }
    }

    protected void onLoad() throws Throwable {}

    protected void onSave() throws Throwable {}

}
