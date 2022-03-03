package me.lauriichan.minecraft.minigame.util.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public final class PathSource extends DataSource {

    private final Path path;

    PathSource(Path path) {
        if (Files.isDirectory(Objects.requireNonNull(path))) {
            throw new IllegalArgumentException("Directory can't be a DataSource!");
        }
        this.path = path;
    }

    @Override
    public Path getSource() {
        return path;
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public InputStream openStream() throws IOException {
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

}
