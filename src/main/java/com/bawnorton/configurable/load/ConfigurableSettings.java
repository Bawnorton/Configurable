package com.bawnorton.configurable.load;

public record ConfigurableSettings(
        String sourceSet,
        String name,
        String config,
        String loader,
        String screenFactory,
        String packageName
) {
    public String sourceSet() {
        return sourceSet == null ? "main" : sourceSet;
    }

    public String configFileName() {
        return sourceSet == null ? name : "%s-%s".formatted(name, sourceSet);
    }

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

    public String name() {
        return name;
    }

    public String config() {
        return config;
    }

    public String packageName() {
        return packageName;
    }
}
