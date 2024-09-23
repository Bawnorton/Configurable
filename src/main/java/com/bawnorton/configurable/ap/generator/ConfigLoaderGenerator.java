package com.bawnorton.configurable.ap.generator;

import com.bawnorton.configurable.load.ConfigurableSettings;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public final class ConfigLoaderGenerator extends ConfigurableGenerator {
    //language=Java
    private static final String LOADER_SPEC = """

package <configurable_package>;

import <config_class_name>;
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.generated.GeneratedConfigLoader;
import com.bawnorton.configurable.ref.Reference;
import com.bawnorton.configurable.ref.gson.ReferenceSerializer;
import com.bawnorton.configurable.platform.Platform;
import com.bawnorton.configurable.libs.gson.Gson;
import com.bawnorton.configurable.libs.gson.GsonBuilder;
import com.bawnorton.configurable.libs.gson.JsonPrimitive;
import com.bawnorton.configurable.libs.gson.JsonSyntaxException;
import com.bawnorton.configurable.libs.gson.JsonObject;
import com.bawnorton.configurable.libs.gson.JsonElement;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.ReflectiveOperationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConfigLoader implements GeneratedConfigLoader<Config> {
    private static final Gson GSON = createGson();
    private static final Path configPath = Platform.getConfigDir()
            .resolve("configurable/<file_name>.json");
    
    private static Gson createGson() {
       GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Reference.class, new ReferenceSerializer());
       ConfigurableMain.getTypeAdapters("<name>").forEach(builder::registerTypeHierarchyAdapter);
       return builder.create();
    }
    
    @Override
    public Config loadConfig() {
        try {
            if(!Files.exists(configPath)) {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
                return new Config();
            }
            try {
                JsonObject config = GSON.fromJson(Files.newBufferedReader(configPath), JsonObject.class);
                Config parsed = parseConfig(config, true);
                ConfigurableMain.LOGGER.info("Successfully loaded config \\"<file_name>\\"");
                return parsed;
            } catch (JsonSyntaxException e) {
                ConfigurableMain.LOGGER.error("Failed to parse \\"<file_name>\\" config file, using default", e);
            }
        } catch (IOException e) {
            ConfigurableMain.LOGGER.error("Failed to load \\"<file_name>\\" config file, using default", e);
        }
        return new Config();
    }
    
    @Override
    public void saveConfig(Config config) {
        try {
            Files.write(configPath, GSON.toJson(config).getBytes());
        } catch (IOException e) {
            ConfigurableMain.LOGGER.error("Failed to write \\"<file_name>\\" config file", e);
        }
    }
    
    @Override
    public String serializeConfig(Config config) {
        return GSON.toJson(config);
    }
    
    @Override
    public Config deserializeConfig(String serializedConfig) {
        return parseConfig(GSON.fromJson(serializedConfig, JsonObject.class), false);
    }
    
    private Config parseConfig(JsonObject configJson, boolean set) {
        List<String> stack = new ArrayList<>();
        Config config = new Config();
        parseNested(stack, configJson, config, set);
        return config;
    }
    
    private void parseNested(List<String> stack, JsonObject nestedJson, Config config, boolean set) {
       Set<String> keys = nestedJson.keySet();
       for(String key : keys) {
           JsonElement element = nestedJson.get(key);
           if(element.isJsonObject()) {
               stack.add(key);
               parseNested(stack, element.getAsJsonObject(), config, set);
               stack.remove(stack.size() - 1);
           } else if (element.isJsonNull()) {
               parseReference(key, null, stack, config, set);
           } else if (element.isJsonPrimitive()) {
               parseReference(key, element.getAsJsonPrimitive(), stack, config, set);
           }
       }
    }
    
    private void parseReference(String key, JsonPrimitive value, List<String> parents, Config config, boolean set) {
        Class<? extends Config> configClass = config.getClass();
        String keyPath = "";
        if(!parents.isEmpty()) {
            keyPath = String.join(".", parents) + ".";
        }
        keyPath += key;
        
        Field target;
        Object instance = config;
        
        try {
            if(parents.isEmpty()) {
                target = configClass.getDeclaredField(key);
            } else {
                Class<?> nested = configClass;
                for(String parent : parents) {
                    Field parentField = nested.getDeclaredField(parent);
                    nested = parentField.getType();
                    try {
                        instance = parentField.get(instance);
                    } catch (ReflectiveOperationException e) {
                        ConfigurableMain.LOGGER.error("Field: \\"%s\\" could not be set.".formatted(keyPath), e);
                    }
                }
                target = nested.getDeclaredField(key);
            }
        } catch (NoSuchFieldException ignored) {
            ConfigurableMain.LOGGER.warn("Field: \\"%s\\" could not be found.".formatted(keyPath));
            return;
        }
        try {
            Reference<?> reference = (Reference<?>) target.get(instance);
            Class<?> expected = reference.getType();
            try {
                Object refValue = getRefValue(value, expected);
                if(set) {
                    reference.set(refValue);
                } else {
                    reference.setMemento(refValue);
                }
            } catch (ClassCastException e) {
               ConfigurableMain.LOGGER.error("Field: \\"%s\\" of type \\"%s\\" could not be set.".formatted(keyPath, expected), e);
            }
        } catch (IllegalAccessException e) {
            ConfigurableMain.LOGGER.error("Field: \\"%s\\" could not be set.".formatted(keyPath), e);
        }
    }

    private Object getRefValue(JsonPrimitive value, Class<?> expected) {
        if(value == null) return null;
        
        return GSON.getAdapter(expected).fromJsonTree(value);
    }
}
""";

    public ConfigLoaderGenerator(Filer filer, Types types, Messager messager, ConfigurableSettings settings) {
        super(filer, types, messager, settings);
    }

    public void generateConfigLoader() throws IOException {
        String spec = LOADER_SPEC;
        spec = applyReplacements(spec);
        JavaFileObject configLoader = filer.createSourceFile(settings.fullyQualifiedLoader());
        try (PrintWriter out = new PrintWriter(configLoader.openWriter())) {
            out.println(spec);
        }
    }
}
