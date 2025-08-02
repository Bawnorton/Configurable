package com.bawnorton.configurable.io;

import com.bawnorton.configurable.reference.FieldReference;
import java.nio.file.Path;

public class SaveLoader {
    private final Path configPath;

    public SaveLoader(Path configPath, FileType fileType) {
        this.configPath = configPath.resolve(fileType.getExtension());
    }

    public void markToBeLoaded(FieldReference<?> reference) {
        String name = reference.name();
        String group = reference.group();
        String expectedCoordinate = group == null ? name : group + "." + name;
    }

    public void markToBeSaved(FieldReference<?> reference) {
    }

    public void load() {

    }

    public void save() {
    }
}
