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
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.ReflectiveOperationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public final class ConfigLoader implements GeneratedConfigLoader<Config> {
    private static final Path configPath = Platform.getConfigDir()
            .resolve("configurable/<file_name>.json5");
    private static final Path legacyConfigPath = Platform.getConfigDir()
            .resolve("configurable/<file_name>.json");
    private static final Map<String, Field> FIELDS_BY_KEY_PATH = new HashMap<>();
    private static final Gson GSON = createGson();
    
    private static Gson createGson() {
       GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Reference.class, new ReferenceSerializer());
       ConfigurableMain.getTypeAdapters("<name>", "<source_set>").forEach(builder::registerTypeHierarchyAdapter);
       FieldNamingStrategy namingStrategy = ConfigurableMain.getFieldNamingStrategy("<name>", "<source_set>");
       builder.setFieldNamingStrategy(namingStrategy);
       
       recordNestedKeyPaths(Config.class, "", namingStrategy, FIELDS_BY_KEY_PATH::put);
       
       return builder.create();
    }
    
    private static void recordNestedKeyPaths(Class<?> clazz, String keyPath, FieldNamingStrategy namingStrategy, BiConsumer<String, Field> setter) {
        for (Field field : clazz.getDeclaredFields()) {
            if(field.getName().equals("CONFIGURABLE_COMMENT")) continue;
            
            String fieldPath = keyPath + namingStrategy.translateName(field);
            setter.accept(fieldPath, field);
            if(!field.getType().equals(Reference.class)) {
                recordNestedKeyPaths(field.getType(), "%s.".formatted(fieldPath), namingStrategy, setter);
            }
        }
    }
    
    @Override
    public Config loadConfig(UnaryOperator<String> datafixer) {
        try {
            boolean usingLegacyConfig = false;
            Path loadingPath = configPath;
            if(Files.exists(legacyConfigPath)) {
                loadingPath = legacyConfigPath;
                usingLegacyConfig = true;
            }
            
            if(!Files.exists(loadingPath)) {
                Files.createDirectories(loadingPath.getParent());
                Files.createFile(loadingPath);
                return new Config();
            }
            try {
                GsonReader reader = new GsonReader(JsonReader.json5(Files.newBufferedReader(loadingPath)));
                JsonObject config = GSON.fromJson(reader, JsonObject.class);
                if(config == null) {
                    ConfigurableMain.LOGGER.warn("No config \\"<file_name>\\" found, using default");
                    return new Config();
                }
                
                Config parsed = parseConfig(config, true);
                
                if(usingLegacyConfig) {
                    ConfigurableMain.LOGGER.info("Migrating legacy config \\"<file_name>\\"");
                    Files.deleteIfExists(legacyConfigPath);
                    saveConfig(parsed);
                }
                
                ConfigurableMain.LOGGER.info("Successfully loaded config \\"<file_name>\\"");
                return parsed;
            } catch (JsonSyntaxException e) {
                ConfigurableMain.LOGGER.error("Failed to parse \\"<file_name>\\" config file, using default", e);
            }
        } catch (IOException | RuntimeException e) {
            ConfigurableMain.LOGGER.error("Failed to load \\"<file_name>\\" config file, using default", e);
        }
        return new Config();
    }
    
    @Override
    public void saveConfig(Config config) {
        try(StringWriter stringWriter = new StringWriter()) {
            JsonWriter writer = JsonWriter.json5(stringWriter);
            writer.beginObject();
            
            Field[] fields = Config.class.getDeclaredFields();
            for(Field field : fields) {
                writeField(field, config, writer);
            }
            
            writer.endObject();
            writer.flush();
            
            Files.writeString(configPath, stringWriter.toString(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            ConfigurableMain.LOGGER.error("Failed to write \\"<file_name>\\" config file", e);
        }
    }
    
    private void writeField(Field field, Object instance, JsonWriter writer) throws IOException {
        try {
            boolean isRef = field.getType().equals(Reference.class);
            if(isRef) {
                writeRefField(field, instance, writer);
            } else {
                writeNestedField(field, field.get(instance), writer);
            }
        } catch (ReflectiveOperationException e) {
            throw new IOException(e);
        }
    }
    
    private void writeRefField(Field field, Object instance, JsonWriter writer) throws IOException, ReflectiveOperationException {
        Reference<?> ref = (Reference<?>) field.get(instance);
        if(ref.hasComment()) {
            writer.blockComment(ref.getComment());
        }
        writer.name(GSON.fieldNamingStrategy().translateName(field));
        JsonElement elemnt = GSON.toJsonTree(ref, field.getGenericType());
        GSON.toJson(elemnt, new GsonWriter(writer));
    }
    
    private void writeNestedField(Field field, Object instance, JsonWriter writer) throws IOException, ReflectiveOperationException {
        try {
            Field commentField = instance.getClass().getDeclaredField("CONFIGURABLE_COMMENT");
            String comment = (String) commentField.get(instance);
            if(comment != null && !comment.isEmpty()) {
                writer.blockComment(comment);
            }
        } catch (NoSuchFieldException ignored) {}
        writer.name(GSON.fieldNamingStrategy().translateName(field));
        writer.beginObject();
        Field[] nestedFields = instance.getClass().getDeclaredFields();
        for(Field nestedField : nestedFields) {
            if(nestedField.getName().equals("CONFIGURABLE_COMMENT")) continue;
            
            nestedField.setAccessible(true);
            writeField(nestedField, instance, writer);
        }
        writer.endObject();
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
        
        Field target = FIELDS_BY_KEY_PATH.get(keyPath);
        Object instance = config;
        
        try {
            if (!parents.isEmpty()) {
                String parentKeyPath = parents.get(0);
                Field parentField = FIELDS_BY_KEY_PATH.get(parentKeyPath);
                
                for (int i = 0; i < parents.size(); i++) {
                    if (i > 0) {
                        parentKeyPath += "." + parents.get(i);
                        parentField = FIELDS_BY_KEY_PATH.get(parentKeyPath);
                    }
            
                    try {
                        instance = parentField.get(instance);
                    } catch (ReflectiveOperationException e) {
                        ConfigurableMain.LOGGER.error("Field: \\"%s\\" could not be set.".formatted(keyPath), e);
                    }
                }
            }
        } catch (RuntimeException ignored) {
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
            } catch (ClassCastException | IllegalArgumentException e) {
               ConfigurableMain.LOGGER.warn("Field: \\"%s\\" of type \\"%s\\" could not be set to \\"%s\\". Falling back to default.".formatted(keyPath, expected, value.toString()));
            }
        } catch (IllegalAccessException e) {
            ConfigurableMain.LOGGER.error("Field: \\"%s\\" could not be set. Falling back to default".formatted(keyPath), e);
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
