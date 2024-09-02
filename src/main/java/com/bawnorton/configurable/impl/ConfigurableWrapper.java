package com.bawnorton.configurable.impl;

import com.bawnorton.configurable.impl.generated.GeneratedConfig;
import com.bawnorton.configurable.impl.generated.GeneratedConfigLoader;
import com.bawnorton.configurable.impl.generated.GeneratedConfigScreenFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import java.lang.reflect.Constructor;

public final class ConfigurableWrapper {
    private GeneratedConfigLoader<GeneratedConfig> loader;
    private GeneratedConfigScreenFactory screenFactory;
    private GeneratedConfig lastLoadedConfig;

    public void setLoader(Class<? extends GeneratedConfigLoader<GeneratedConfig>> loaderClass) {
        if(loader != null) throw new IllegalStateException("loader already set");
        try {
            Constructor<? extends GeneratedConfigLoader<GeneratedConfig>> loaderCtor = loaderClass.getConstructor();
            loader = loaderCtor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setScreenFactory(Class<? extends GeneratedConfigScreenFactory> screenFactoryClass) {
        if(screenFactory != null) throw new IllegalStateException("screen factory already set");
        try {
            Constructor<? extends GeneratedConfigScreenFactory> screenFactoryCtor = screenFactoryClass.getConstructor();
            screenFactory = screenFactoryCtor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public GeneratedConfig getConfig() {
        if(lastLoadedConfig == null) {
            loadConfig();
            saveConfig();
        }

        return lastLoadedConfig;
    }

    public boolean hasLoader() {
        return loader != null;
    }

    public boolean hasScreenFactory() {
        return screenFactory != null;
    }

    public void loadConfig() {
        lastLoadedConfig = loader.loadConfig();
    }

    public void saveConfig() {
        loader.saveConfig(lastLoadedConfig);
    }

    public Screen createScreen(MinecraftClient client, Screen parent) {
        return screenFactory.createScreen(client, parent);
    }
}
