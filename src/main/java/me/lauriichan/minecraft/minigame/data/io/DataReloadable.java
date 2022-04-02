package me.lauriichan.minecraft.minigame.data.io;

import java.io.File;
import java.util.logging.Level;

import me.lauriichan.minecraft.minigame.data.automatic.message.Messages;
import me.lauriichan.minecraft.minigame.util.Tuple;

public abstract class DataReloadable extends Reloadable {

    private final Tuple<String, Object> name;

    public DataReloadable(File file) {
        super(file);
        this.name = Tuple.of("name", file.getName());
    }

    public DataReloadable(File file, boolean saveOnExit) {
        super(file, saveOnExit);
        this.name = Tuple.of("name", file.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void load() {
        Messages.DATA_LOAD_START.log(Level.INFO, Tuple.of("name", name));
        if (!file.exists()) {
            Messages.DATA_LOAD_SUCCESS.log(Level.INFO, Tuple.of("name", name));
            return;
        }
        try {
            onLoad();
            Messages.DATA_LOAD_SUCCESS.log(Level.INFO, Tuple.of("name", name));
        } catch (Throwable exp) {
            Messages.DATA_LOAD_FAILED.log(Level.WARNING, exp, Tuple.of("name", name));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void save() {
        Messages.DATA_SAVE_START.log(Level.INFO, Tuple.of("name", name));
        try {
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            onSave();
            Messages.DATA_SAVE_SUCCESS.log(Level.INFO, Tuple.of("name", name));
        } catch (Throwable exp) {
            Messages.DATA_SAVE_FAILED.log(Level.WARNING, exp, Tuple.of("name", name));
        }
    }

    protected void onLoad() throws Throwable {}

    protected void onSave() throws Throwable {}

}
