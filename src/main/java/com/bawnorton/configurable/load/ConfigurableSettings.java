package com.bawnorton.configurable.load;

public record ConfigurableSettings(
        String name,
        String config,
        String loader,
        String screenFactory,
        String packageName
) {
    public String fullyQualifiedConfig() {
        return "%s.%s".formatted(packageName, config);
    }

    public String fullyQualifiedLoader() {
        return "%s.%s".formatted(packageName, loader);
    }

    public String fullyQualifiedScreenFactory() {
        return "%s.client.%s".formatted(packageName, screenFactory);
    }

    public boolean hasScreenFactory() {
        return screenFactory != null;
    }
}