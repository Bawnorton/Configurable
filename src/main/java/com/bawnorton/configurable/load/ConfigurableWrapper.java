package com.bawnorton.configurable.load;

import com.bawnorton.configurable.api.ConfigurableApi;
import com.bawnorton.configurable.generated.GeneratedConfig;
import com.bawnorton.configurable.generated.GeneratedConfigLoader;
import com.bawnorton.configurable.generated.GeneratedConfigScreenFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import java.lang.reflect.Constructor;

public final class ConfigurableWrapper {
    private final ConfigurableApi apiImpl;

    private GeneratedConfigLoader<GeneratedConfig> loader;
    private GeneratedConfigScreenFactory screenFactory;
    private GeneratedConfig lastLoadedConfig;

    public ConfigurableWrapper(ConfigurableApi apiImpl) {
        this.apiImpl = apiImpl;
    }

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

    public boolean serverEnforces() {
        if(apiImpl == null) return true;

        return apiImpl.serverEnforces();
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
        if(apiImpl != null) {
            lastLoadedConfig = apiImpl.afterLoad(lastLoadedConfig);
        }
    }

    public void saveConfig() {
        if(apiImpl != null) {
            lastLoadedConfig = apiImpl.beforeSave(lastLoadedConfig);
        }
        loader.saveConfig(lastLoadedConfig);
    }

    public void deserializeConfig(String serialized) {
        lastLoadedConfig = loader.deserializeConfig(serialized);
    }

    public String serializeConfig(GeneratedConfig config) {
        return loader.serializeConfig(config);
    }

    public Screen createScreen(MinecraftClient client, Screen parent) {
        if(screenFactory == null) return parent;

        return screenFactory.createScreen(client, parent);
    }

    public void refreshConfigScreen() {
        if(screenFactory == null) return;

        screenFactory.refresh();
    }
}
