package me.lauriichan.minecraft.minigame.util.source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public final class URLSource extends DataSource {

    private final URL url;

    URLSource(URL url) {
        this.url = Objects.requireNonNull(url);
    }

    @Override
    public URL getSource() {
        return url;
    }

    @Override
    public boolean exists() {
        return true; // Just expect that it exists, we don't know
    }

    @Override
    public InputStream openStream() throws IOException {
        return url.openStream();
    }

}
