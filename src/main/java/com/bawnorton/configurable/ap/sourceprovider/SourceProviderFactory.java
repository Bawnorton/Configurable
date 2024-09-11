package com.bawnorton.configurable.ap.sourceprovider;

import javax.annotation.processing.Filer;
import java.nio.file.Path;

public interface SourceProviderFactory {
    SourceProvider create(Filer filer, Path buildPath);
}
