package com.bawnorton.configurable.ap.sourceprovider;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.io.Reader;

public abstract class SourceProvider {
    protected final Filer filer;

    protected SourceProvider(Filer filer) {
        this.filer = filer;
    }

    protected abstract Reader getConfigFile() throws IOException;

    public abstract boolean matches();

    public abstract String getName();
}
