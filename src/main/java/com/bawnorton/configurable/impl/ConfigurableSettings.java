package com.bawnorton.configurable.impl;

public record ConfigurableSettings(
        String name,
        String config,
        String loader,
        String packageName
) {
    public String fullyQualifiedConfig() {
        return "%s.%s".formatted(packageName, config);
    }

    public String fullyQualifiedLoader() {
        return "%s.%s".formatted(packageName, loader);
    }
}
