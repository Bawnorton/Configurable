package com.bawnorton.configurable.ap.sourceprovider;

import javax.annotation.processing.Filer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class SourceProviders {
    private static final Set<SourceProviderFactory> factories = new HashSet<>();

    public static void registerDefaultSourceProviders() {
        //? if fabric
        /*registerSourceProvider(FabricSourceProvider::new);*/
        //? if neoforge
        registerSourceProvider(NeoForgeSourceProvider::new);
    }

    public static void registerSourceProvider(SourceProviderFactory factory) {
        factories.add(factory);
    }

    public static SourceProvider getSourceProvider(Filer filer, Path buildPath) {
        for (SourceProviderFactory factory : factories) {
            SourceProvider provier = factory.create(filer, buildPath);
            if (provier.matches()) {
                return provier;
            }
        }
        return null;
    }
}
