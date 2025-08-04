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
import java.util.List;
import java.util.Objects;

public class SaveLoader {
    private final Path configPath;
    private final FileType fileType;

    public SaveLoader(Path configPath, FileType fileType) {
        this.configPath = configPath;
        this.fileType = fileType;
    }
    
    @SuppressWarnings("unchecked")
    public void load(List<FieldReference<?>> references) {
        if(!Files.exists(configPath) || references.isEmpty()) return;

        List<FieldReference<Object>> castedReferences = references.stream()
                .map(ref -> (FieldReference<Object>) ref)
                .toList();

        switch (fileType) {
            case JSON -> loadJson(castedReferences);
            case TOML -> loadToml(castedReferences);
        }
    }

    @SuppressWarnings("unchecked")
    public void save(List<FieldReference<?>> references) {
        if (references.isEmpty()) return;

        List<FieldReference<Object>> castedReferences = references.stream()
                .map(ref -> (FieldReference<Object>) ref)
                .toList();

        switch (fileType) {
            case JSON -> saveJson(castedReferences);
            case TOML -> saveToml(castedReferences);
        }
    }

    private void loadJson(List<FieldReference<Object>> references) {
        try (GsonReader reader = new GsonReader(JsonReader.json5(configPath))) {
            JsonElement tree = JsonParser.parseReader(reader);
            for (FieldReference<Object> ref : references) {
                JsonElement element = extractFieldFromJsonTree(tree, ref.group(), ref.name());
                if (element == null) {
                    handleMissingValue(ref);
                    continue;
                }

                Object value;
                try {
                    value = SerialisationHelper.interpret(element, ref.genericType());
                } catch (Exception e) {
                    ConfigurableMain.LOGGER.error("Failed to interpret value for JSON field '{}'", ref.fullName());
                    ConfigurableMain.LOGGER.debug("Exception details:", e);
                    handleInvalidValue(ref, null);
                    continue;
                }
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

    private void loadToml(List<FieldReference<Object>> references) {
        try {
            TomlParser tomlParser = new TomlParser();
            CommentedConfig parsed = tomlParser.parse(configPath, FileNotFoundAction.CREATE_EMPTY);
            for (FieldReference<Object> ref : references) {
                GenericType expectedType = ref.genericType();
                String coordinate = ref.group() == null ? ref.name() : "%s.%s".formatted(ref.group(), ref.name());
                Object value;
                try {
                    value = SerialisationHelper.interpret(parsed, coordinate, expectedType);
                } catch (Exception e) {
                    ConfigurableMain.LOGGER.error("Failed to interpret value for TOML field '{}'", ref.fullName());
                    ConfigurableMain.LOGGER.debug("Exception details:", e);
                    handleInvalidValue(ref, null);
                    continue;
                }
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
            ConfigurableMain.LOGGER.warn(ref.validator().messageProvider().getMessage(null));
            ref.set(ref.validator().defaultSupplier().get());
        } else {
            String message = "Field '%s' was expected but was not found.".formatted(ref.fullName());
            throw new IllegalConfigException(configPath.getFileName().toString(), message);
        }
    }

    private void handleInvalidValue(FieldReference<Object> ref, Object value) {
        String message = ref.validator().messageProvider().getMessage(value);
        if (ref.validator().fallback()) {
            ConfigurableMain.LOGGER.warn(message);
            ref.set(ref.validator().defaultSupplier().get());
        } else {
            throw new IllegalConfigException(configPath.getFileName().toString(), message);
        }
    }

    private void saveJson(List<FieldReference<Object>> references) {
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
            for (FieldReference<Object> ref : references) {
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
            int common = 0;
            while (common < currentParts.length && common < newParts.length && currentParts[common].equals(newParts[common])) {
                common++;
            }
            for (int i = currentParts.length - 1; i >= common; i--) {
                writer.endObject();
            }
            for (int i = common; i < newParts.length; i++) {
                writer.name(newParts[i]).beginObject();
            }
            return newGroup;
        } else {
            String[] parts = newGroup.split("\\.");
            for (String part : parts) {
                writer.name(part).beginObject();
            }
            return newGroup;
        }
    }

    private void saveToml(List<FieldReference<Object>> references) {
        try {
            TomlWriter tomlWriter = new TomlWriter();
            tomlWriter.setIndent(IndentStyle.TABS);
            CommentedConfig config = TomlFormat.newConfig();
            for (FieldReference<Object> ref : references) {
                String path = ref.group() == null ? ref.name() : "%s.%s".formatted(ref.group(), ref.name());
                if (ref.comment() != null) {
                    config.setComment(path, " %s".formatted(ref.comment()));
                }
                Object value = SerialisationHelper.safeForToml(ref.get());
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
