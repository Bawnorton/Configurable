package com.bawnorton.configurable.io;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.FieldValidator;
import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.IndentStyle;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SaveLoader {
    private final Path configPath;
    private final FileType fileType;
    private final List<FieldReference<Object>> toBeSaved = new ArrayList<>();
    private final List<FieldReference<Object>> toBeLoaded = new ArrayList<>();

    public SaveLoader(Path configPath, FileType fileType) {
        this.configPath = configPath;
        this.fileType = fileType;
    }

    @SuppressWarnings("unchecked")
    public void markToBeLoaded(FieldReference<?> reference) {
        toBeLoaded.add((FieldReference<Object>) reference);
    }

    @SuppressWarnings("unchecked")
    public void markToBeSaved(FieldReference<?> reference) {
        toBeSaved.add((FieldReference<Object>) reference);
    }

    public void load() {
        if(!Files.exists(configPath) || toBeLoaded.isEmpty()) return;

        switch (fileType) {
            case JSON -> loadJson();
            case TOML -> loadToml();
        }

        toBeLoaded.clear();
    }

    public void save() {
        if (toBeSaved.isEmpty()) return;

        toBeSaved.sort(Comparator.comparing(ref -> "%s.%s".formatted(ref.group(), ref.name())));

        switch (fileType) {
            case JSON -> saveJson();
            case TOML -> saveToml();
        }

        toBeSaved.clear();
    }

    private void loadJson() {
        try (GsonReader reader = new GsonReader(JsonReader.json5(configPath))) {
            JsonElement tree = JsonParser.parseReader(reader);
            for (FieldReference<Object> ref : toBeLoaded) {
                JsonElement element = extractFieldFromJsonTree(tree, ref.group(), ref.name());
                if (element == null) {
                    handleMissingValue(ref);
                    continue;
                }

                Object value = Interpreter.interpret(element, ref.genericType());
                if (value == null) {
                    handleInvalidValue(ref, null);
                    continue;
                }
                FieldValidator<Object> validator = ref.validator().fieldValidator();
                if (validator != null && !validator.isValid(value)) {
                    handleInvalidValue(ref, value);
                    continue;
                }

                ref.set(value);
            }
        } catch (IOException e) {
            ConfigurableMain.LOGGER.error("Failed to load JSON config from '{}'", configPath.getFileName(), e);
        }
    }

    private JsonElement extractFieldFromJsonTree(JsonElement current, String group, String name) {
        JsonElement node = current;
        if (group != null) {
            for (String part : group.split("\\.")) {
                node = node.getAsJsonObject().get(part);
                if (node == null || !node.isJsonObject()) return null;
            }
        }
        boolean exists = node != null && node.getAsJsonObject().has(name);
        JsonElement leaf = exists ? node.getAsJsonObject().get(name) : null;
        if (leaf == null || leaf.isJsonObject()) return null;

        return leaf;
    }

    private void loadToml() {
        try {
            TomlParser tomlParser = new TomlParser();
            CommentedConfig parsed = tomlParser.parse(configPath, FileNotFoundAction.CREATE_EMPTY);
            for (FieldReference<Object> ref : toBeLoaded) {
                GenericType expectedType = ref.genericType();
                String coordinate = ref.group() == null ? ref.name() : "%s.%s".formatted(ref.group(), ref.name());
                Object value = Interpreter.interpret(parsed, coordinate, expectedType);
                if (value == null) {
                    handleMissingValue(ref);
                    continue;
                }
                FieldValidator<Object> validator = ref.validator().fieldValidator();
                if (validator != null && !validator.isValid(value)) {
                    handleInvalidValue(ref, value);
                    continue;
                }
                ref.set(value);
            }
        } catch (ParsingException | ClassCastException e) {
            ConfigurableMain.LOGGER.error("Failed to load TOML config from '{}'", configPath.getFileName(), e);
        }
    }

    private void handleMissingValue(FieldReference<Object> ref) {
        if(ref.validator().fallback()) {
            ref.set(ref.validator().defaultSupplier().get());
        } else {
            throw new IllegalConfigException(configPath.getFileName().toString(), "Field '%s' in group '%s' was expected but was not found.".formatted(ref.name(), ref.group()));
        }
    }

    private void handleInvalidValue(FieldReference<Object> ref, Object value) {
        if (ref.validator().fallback()) {
            ref.set(ref.validator().defaultSupplier().get());
        } else {
            String message = ref.validator().messageProvider().getMessage(value);
            throw new IllegalConfigException(configPath.getFileName().toString(), message);
        }
    }

    private void saveJson() {
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
            } catch (IOException e) {
                ConfigurableMain.LOGGER.error("Failed to create JSON config file '{}'", configPath.getFileName(), e);
                return;
            }
        }
        try (JsonWriter writer = JsonWriter.json5(configPath)) {
            writer.beginObject();
            String currentGroup = null;
            for (FieldReference<Object> ref : toBeSaved) {
                if(ref.comment() != null) {
                    writer.comment(ref.comment());
                }
                currentGroup = changeGroupPath(writer, ref.group(), currentGroup);
                writer.name(ref.name());
                writeValue(writer, ref.get());
            }
            if (currentGroup != null) {
                for (String ignored : currentGroup.split("\\.")) {
                    writer.endObject();
                }
            }
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            ConfigurableMain.LOGGER.error("Failed to save JSON config to '{}'", configPath.getFileName(), e);
        }
    }

    private void writeValue(JsonWriter writer, Object value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else if (value.getClass().isArray()) {
            writer.beginArray();
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                writeValue(writer, Array.get(value, i));
            }
            writer.endArray();
        } else if (value instanceof List<?> list) {
            writer.beginArray();
            for (Object item : list) {
                writeValue(writer, item);
            }
            writer.endArray();
        } else {
            switch (value) {
                case String str -> writer.value(str);
                case Number n -> writer.value(n);
                case Boolean b -> writer.value(b);
                case Character c -> writer.value(String.valueOf(c));
                default -> throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
            }
        }
    }

    private String changeGroupPath(JsonWriter writer, String newGroup, String currentGroup) throws IOException {
        if (Objects.equals(newGroup, currentGroup)) return currentGroup;

        if (newGroup == null) {
            String[] parts = currentGroup.split("\\.");
            for (int i = parts.length - 1; i >= 0; i--) {
                writer.endObject();
            }
            return null;
        } else if (currentGroup != null) {
            String[] currentParts = currentGroup.split("\\.");
            String[] newParts = newGroup.split("\\.");
            String needToOpen = null;
            int minLength = Math.min(currentParts.length, newParts.length);
            for (int i = 0; i < minLength; i++) {
                if (!currentParts[i].equals(newParts[i])) {
                    for (int j = currentParts.length - 1; j >= i; j--) {
                        writer.endObject();
                    }
                    needToOpen = newGroup.substring(newParts[i].length() + 1);
                    break;
                }
            }
            for (int i = minLength; i < currentParts.length; i++) {
                writer.endObject();
            }
            if (needToOpen != null) {
                String[] parts = needToOpen.split("\\.");
                for (String part : parts) {
                    writer.name(part).beginObject();
                }
                return newGroup;
            }
            return currentGroup;
        } else {
            String[] parts = newGroup.split("\\.");
            for (String part : parts) {
                writer.name(part).beginObject();
            }
            return newGroup;
        }
    }

    private void saveToml() {
        try {
            TomlWriter tomlWriter = new TomlWriter();
            tomlWriter.setIndent(IndentStyle.TABS);
            CommentedConfig config = TomlFormat.newConfig();
            for (FieldReference<Object> ref : toBeSaved) {
                String path = ref.group() == null ? ref.name() : "%s.%s".formatted(ref.group(), ref.name());
                if (ref.comment() != null) {
                    config.setComment(path, ref.comment());
                }
                Object value = Interpreter.safeForToml(ref.get());
                if (value != null) {
                    config.set(path, value);
                }
            }
            tomlWriter.write(config.unmodifiable(), configPath, WritingMode.REPLACE);
        } catch (WritingException e) {
            ConfigurableMain.LOGGER.error("Failed to save TOML config to '{}'", configPath.getFileName(), e);
        }
    }
}
