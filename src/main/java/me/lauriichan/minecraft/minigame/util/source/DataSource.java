package me.lauriichan.minecraft.minigame.util.source;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public abstract class DataSource {

    public abstract boolean exists();

    public abstract Object getSource();

    public abstract InputStream openStream() throws IOException;

    public InputStreamReader openReader() throws IOException {
        return new InputStreamReader(openStream());
    }

    public BufferedReader openBufferedReader() throws IOException {
        return new BufferedReader(new InputStreamReader(openStream()));
    }

}
