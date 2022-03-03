package me.lauriichan.minecraft.minigame.util.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class FileSource extends DataSource {

    private final File file;

    FileSource(File file) {
        if (Objects.requireNonNull(file).isDirectory()) {
            throw new IllegalArgumentException("Directory can't be a DataSource!");
        }
        this.file = file;
    }

    @Override
    public File getSource() {
        return file;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public InputStream openStream() throws IOException {
        return new FileInputStream(file);
    }

}
