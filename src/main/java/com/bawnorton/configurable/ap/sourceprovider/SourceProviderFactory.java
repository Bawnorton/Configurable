package com.bawnorton.configurable.ap.sourceprovider;

import javax.annotation.processing.Filer;

public interface SourceProviderFactory {
    SourceProvider create(Filer filer);
}
