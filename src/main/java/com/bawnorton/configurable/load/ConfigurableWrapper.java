package com.bawnorton.configurable.load;

import com.bawnorton.configurable.api.ConfigurableApi;
import com.bawnorton.configurable.generated.GeneratedConfig;
import com.bawnorton.configurable.generated.GeneratedConfigLoader;
import com.bawnorton.configurable.generated.GeneratedConfigScreenFactory;
import com.bawnorton.configurable.ref.gson.ItemTypeAdapter;
import com.google.gson.FieldNamingStrategy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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
        return apiImpl.serverEnforces();
    }

    public FieldNamingStrategy getFieldNamingStrategy() {
        return apiImpl.defaultFieldNamingStrategy();
    }

    public Map<Class<?>, Object> getTypeAdapters() {
        Map<Class<?>, Object> typeAdapters = new HashMap<>(apiImpl.getTypeAdapters());
        typeAdapters.put(Item.class, new ItemTypeAdapter());
        return typeAdapters;
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
        lastLoadedConfig = loader.loadConfig(apiImpl::beforeLoad);
        lastLoadedConfig = apiImpl.afterLoad(lastLoadedConfig);
    }

    public void saveConfig() {
        lastLoadedConfig = apiImpl.beforeSave(lastLoadedConfig);
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
