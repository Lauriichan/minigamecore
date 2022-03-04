package me.lauriichan.minecraft.minigame.util.source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.PathUtils;

import me.lauriichan.minecraft.minigame.annotation.AnnotationId;
import me.lauriichan.minecraft.minigame.annotation.AnnotationProcessor;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;
import me.lauriichan.minecraft.minigame.util.Reference;

public final class Resources {

    private final File folder;
    private final URI jarUri;
    private final boolean jarFile;

    private final Logger logger;

    private final Reference<Path> root = Reference.of();

    public Resources(File folder, File jar, Logger logger) {
        this.logger = logger;
        this.folder = folder;
        this.jarFile = jar.isFile();
        this.jarUri = buildUri(jar);
    }

    public Path getInternalRoot() {
        if (root.isPresent()) {
            return root.get();
        }
        try {
            return root.set(jarFile ? createFileSystem(jarUri) : Paths.get(jarUri).resolve("classes")).lock().get();
        } catch (IOException exp) {
            logger.log(Level.SEVERE, "Failed to retrieve resource root!", exp);
            return null;
        }
    }

    public File getExternalRoot() {
        return folder;
    }

    public Path getInternalPath(final String path) {
        return getInternalRoot().resolveSibling(path);
    }

    public Path getExternalPath(final String path) {
        return getExternalPath(path, true);
    }

    public Path getExternalPath(final String path, final boolean copy) {
        try {
            final Path root = getInternalPath(path);
            final File target = new File(folder, path);
            if (root == null || !copy) {
                return target.toPath();
            }
            if (PathUtils.isDirectory(root)) {
                return copyDirectoryPath(root, target);
            }
            return copyFilePath(root, target);
        } catch (final Exception exp) {
            logger.log(Level.SEVERE, "Failed to retrieve resource '" + path + "'!", exp);
            return new File(folder, path).toPath();
        }
    }

    /*
     * Helper
     */

    private URI buildUri(File jar) {
        try {
            return new URI(("jar:file:/" + jar.getAbsolutePath().replace('\\', '/').replace(" ", "%20") + "!/").replace("//", "/"));
        } catch (URISyntaxException exp) {
            logger.log(Level.WARNING, "Failed to build resource uri", exp);
            logger.log(Level.WARNING, "Falling back to jar uri, could cause problems");
            return jar.toURI();
        }
    }

    private Path createFileSystem(final URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri).getPath("/");
        } catch (final Exception exp) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath("/");
        }
    }

    private Path copyDirectoryPath(final Path path, final File target) throws Exception {
        if (target.exists()) {
            return target.toPath();
        }
        if (!target.exists() && target.isDirectory()) {
            target.mkdirs();
        }
        try (Stream<Path> walk = java.nio.file.Files.walk(path, 1)) {
            for (final Iterator<Path> iterator = walk.iterator(); iterator.hasNext();) {
                final Path next = iterator.next();
                if (next == path) {
                    continue;
                }
                final File nextTarget = new File(target, next.getName(next.getNameCount() - 1).toString());
                if (PathUtils.isDirectory(next)) {
                    copyDirectoryPath(next, nextTarget);
                    continue;
                }
                copyFilePath(next, nextTarget);
            }
        }
        return target.toPath();
    }

    private Path copyFilePath(final Path path, final File target) throws Exception {
        if (target.exists()) {
            return target.toPath();
        }
        if (!target.exists() && !target.isDirectory()) {
            target.createNewFile();
        }
        try (InputStream input = path.getFileSystem().provider().newInputStream(path, StandardOpenOption.READ)) {
            try (FileOutputStream output = new FileOutputStream(target)) {
                IOUtils.copy(input, output);
            }
        }
        return target.toPath();
    }

    /*
     * Source Builder
     */

    public URLSource url(String url) throws MalformedURLException {
        return url(new URL(url));
    }

    public URLSource url(URI uri) throws MalformedURLException {
        return url(uri.toURL());
    }

    public URLSource url(URL url) {
        return new URLSource(url);
    }

    public PathSource pathAnnotation(Class<? extends Annotation> clazz) {
        AnnotationId id = JavaAccessor.getAnnotation(clazz, AnnotationId.class);
        if (id == null) {
            return path(getInternalPath(AnnotationProcessor.ANNOTATION_RESOURCE + clazz.getSimpleName()));
        }
        return path(getInternalPath(AnnotationProcessor.ANNOTATION_RESOURCE + id.name()));
    }

    public PathSource pathIntern(String path) {
        return path(getInternalPath(path));
    }

    public PathSource pathExtern(String path, boolean copy) {
        return path(getExternalPath(path, copy));
    }

    public PathSource pathExtern(String path) {
        return path(getExternalPath(path));
    }

    public PathSource path(String path) {
        return path(Paths.get(path));
    }

    public PathSource path(Path path) {
        return new PathSource(path);
    }

    public FileSource fileData(String directory, String path) {
        return file(new File(folder, directory), path);
    }

    public FileSource fileData(String path) {
        return new FileSource(new File(folder, path));
    }

    public FileSource file(String directory, String path) {
        return file(new File(directory, path));
    }

    public FileSource file(File directory, String path) {
        return file(new File(directory, path));
    }

    public FileSource file(String path) {
        return file(new File(path));
    }

    public FileSource file(File file) {
        return new FileSource(file);
    }

}
