package com.bawnorton.configurable.ap.generator;

import com.bawnorton.configurable.impl.ConfigurableSettings;
import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public final class ConfigGenerator {
    //language=Java
    private static final String LOADER_SPEC = """


            package <configurable_package>;

import <config_class_name>;
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.impl.Reference;
import com.bawnorton.configurable.impl.gson.ReferenceSerializer;
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
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConfigLoader {
    private static final Gson GSON = createGson();
    private static final Path configPath = Platform.getConfigDir()
            .resolve("configurable/<name>.json");
   \s
    private static final Map<Class<?>, Object> instanceCache = new HashMap<>();
   \s
    private static Gson createGson() {
       return new GsonBuilder()
            .registerTypeAdapter(Reference.class, new ReferenceSerializer(() -> GSON))
            .setPrettyPrinting()
            .create();
    }
   \s
    public static Config loadConfig() {
        try {
            if(!Files.exists(configPath)) {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
                return new Config();
            }
            try {
                JsonObject config = GSON.fromJson(Files.newBufferedReader(configPath), JsonObject.class);
                Config parsed = parseConfig(config);
                ConfigurableMain.LOGGER.info("Successfully loaded config \\"<name>\\"");
            } catch (JsonSyntaxException e) {
                ConfigurableMain.LOGGER.error("Failed to parse \\"<name>\\" config file, using default", e);
            }
        } catch (IOException e) {
            ConfigurableMain.LOGGER.error("Failed to load \\"<name>\\" config file, using default", e);
        }
        return new Config();
    }
   \s
    public static void saveConfig(Config config) {
        try {
            Files.write(configPath, GSON.toJson(config).getBytes());
        } catch (IOException e) {
            ConfigurableMain.LOGGER.error("Failed to write \\"<name>\\" config file", e);
        }
    }
   \s
    private static Config parseConfig(JsonObject configJson) {
        List<String> stack = new ArrayList<>();
        Config config = new Config();
        parseNested(stack, configJson, config);
        return config;
    }
   \s
    private static void parseNested(List<String> stack, JsonObject nestedJson, Config config) {
       Set<String> keys = nestedJson.keySet();
       for(String key : keys) {
           JsonElement element = nestedJson.get(key);
           if(element.isJsonObject()) {
               stack.addLast(key);
               parseNested(stack, element.getAsJsonObject(), config);
               stack.removeLast();
           } else if (element.isJsonNull()) {
               parseReference(key, null, stack, config);
           } else if (element.isJsonPrimitive()) {
               parseReference(key, element.getAsJsonPrimitive(), stack, config);
           }
       }
    }
   \s
    private static void parseReference(String key, JsonPrimitive value, List<String> parents, Config config) {
        Class<? extends Config> configClass = config.getClass();
        Field target;
        Object instance;
        String keyPath = "";
        if(!parents.isEmpty()) {
            keyPath = String.join(".", parents) + ".";
        }
        keyPath += key;
       \s
        try {
            if(parents.isEmpty()) {
                instance = config;
                target = configClass.getDeclaredField(key);
            } else {
                Class<?> nested = configClass;
                for(String parent : parents) {
                    Field parentField = nested.getDeclaredField(parent);
                    nested = parentField.getType();
                }
                instance = instanceCache.get(nested);
                if(instance == null) {
                    try {
                        Constructor<?> ctor = nested.getConstructor();
                        instance = ctor.newInstance();
                        instanceCache.put(nested, instance);
                    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                        ConfigurableMain.LOGGER.error("Field: \\"%s\\" could not be set.".formatted(keyPath), e);
                        return;
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
                reference.set(getRefValue(value, expected));
            } catch (ClassCastException e) {
               ConfigurableMain.LOGGER.error("Field: \\"%s\\" of type \\"%s\\" could not be set.".formatted(keyPath, expected), e);
            }
        } catch (IllegalAccessException e) {
            ConfigurableMain.LOGGER.error("Field: \\"%s\\" could not be set.".formatted(keyPath), e);
        }
    }

    private static Object getRefValue(JsonPrimitive value, Class<?> expected) {
        if (expected.equals(int.class)) {
            return value.getAsInt();
        } else if (expected.equals(long.class)) {
            return value.getAsLong();
        } else if (expected.equals(float.class)) {
            return value.getAsFloat();
        } else if (expected.equals(double.class)) {
            return value.getAsDouble();
        } else if (expected.equals(boolean.class)) {
            return value.getAsBoolean();
        } else if (expected.equals(byte.class)) {
            return value.getAsByte();
        } else if (expected.equals(short.class)) {
            return value.getAsShort();
        } else if (expected.equals(String.class)) {
            return value.getAsString();
        } else {
            return null;
        }
    }
}
""";

    //language=Java
    private static final String CONFIG_SPEC = """

package <configurable_package>;

<imports>
import com.bawnorton.configurable.impl.Reference;

public final class Config {
<content>
}
    """;

    private final Filer filer;
    private final ConfigurableSettings settings;

    public ConfigGenerator(Filer filer, ConfigurableSettings settings) {
        this.filer = filer;
        this.settings = settings;
    }

    public void generateConfigLoader() throws IOException {
        String spec = LOADER_SPEC;
        spec = spec.replaceAll("<configurable_package>", settings.packageName());
        spec = spec.replaceAll("<config_class_name>", settings.fullyQualifiedConfig());
        spec = spec.replaceAll("<name>", settings.name());
        JavaFileObject configLoader = filer.createSourceFile(settings.fullyQualifiedLoader());
        try (PrintWriter out = new PrintWriter(configLoader.openWriter())) {
            out.println(spec);
        }
    }

    public void generateConfig(String content, Set<String> neededImports) throws IOException {
        String spec = CONFIG_SPEC;
        spec = spec.replaceAll("<configurable_package>", settings.packageName());
        spec = spec.replaceAll("<content>", content);

        StringBuilder importBuilder = new StringBuilder();
        for(String neededImport : neededImports) {
            importBuilder.append("import ")
                    .append(neededImport)
                    .append(";\n");
        }
        spec = spec.replaceAll("<imports>", importBuilder.toString());

        JavaFileObject config = filer.createSourceFile(settings.fullyQualifiedConfig());
        try (PrintWriter out = new PrintWriter(config.openWriter())) {
            out.println(spec);
        }
    }
}
