package com.bawnorton.configurable.ap.sourceprovider;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class SourceProvider {
    protected final Filer filer;
    private final Path buildPath;

    protected SourceProvider(Filer filer, Path buildPath) {
        this.filer = filer;
        this.buildPath = buildPath;
    }

    protected abstract Reader getConfigFile() throws IOException;

    public abstract boolean matches();

    public abstract String getName();

    protected Path findProjectRoot() {
        Path current = buildPath;

        while (current != null &&
               !Files.exists(current.resolve("build.gradle")) &&
               !Files.exists(current.resolve("build.gradle.kts")) &&
               !Files.exists(current.resolve("pom.xml"))) {
            current = current.getParent();
        }

        return current == null ? Path.of("") : current;
    }
}
