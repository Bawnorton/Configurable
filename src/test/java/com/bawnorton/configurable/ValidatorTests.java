package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.service.ConfigLoader;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.testing.compile.Compilation;
import org.junit.jupiter.api.Test;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class ValidatorTests extends BaseTest {
    @Test
    public void testCustomValidator() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/validator/CustomValidator.java");
    }

    @Test
    public void testValidatorsAtRuntime() {
        ConfigurableTestHelper.logModule();
        Compilation compilation = testCompilationSuccess("sources/validator/CustomValidator.java");
        ConfigLoader instance = getLoaderFromCompilation(compilation);

        Path root = Path.of(System.getProperty("user.dir")).getParent().getParent();
        Path configDir = root.resolve("test-output/%s/".formatted(ConfigurableTestHelper.getModuleName()));
        Path configFile = configDir.resolve("custom_validator.json5");
        SaveLoader saveLoader = new SaveLoader(configFile, FileType.JSON);
        instance.save(saveLoader);

        Gson gson = new Gson();
        JsonObject json;
        try (GsonReader reader = new GsonReader(JsonReader.json5(configFile))) {
            json = gson.fromJson(reader, JsonObject.class);
            json.addProperty("FIELD", "wrongType");
            json.addProperty("FIELD_WITH_INTEGER", 400);
            json.addProperty("FIELD_WITH_MIN", -100);
            json.addProperty("FIELD_WITH_MAX", 200);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }

        try {
            GsonWriter writer = new GsonWriter(JsonWriter.json5(configFile));
            gson.toJson(json, JsonElement.class, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file", e);
        }

        instance.load(saveLoader);
        instance.save(saveLoader);
        URL expectedJson = Resources.getResource("expected/configs/custom_validator.json5");
        assertFileContentEquals(
                expectedJson,
                configDir.resolve("custom_validator.json5")
        );
    }

    @Test
    public void testCustomMessageProvider() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/validator/CustomMessageProvider.java");
    }

    @Test
    public void testDefaultValueGreaterThanBounds() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/DefaultValueGreaterThanBounds.java");
    }

    @Test
    public void testDefaultValueSmallerThanBounds() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/DefaultValueSmallerThanBounds.java");
    }

    @Test
    public void testMessageProviderDoesntExist() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/validator/MessageProviderDoesntExist.java");
    }

    @Test
    public void testMessageProviderWithMoreThanOneParameter() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithMoreThanOneParameter.java");
    }

    @Test
    public void testMessageProviderWithoutPublicModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithoutPublicModifier.java");
    }

    @Test
    public void testMessageProviderWithoutStaticModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithoutStaticModifier.java");
    }

    @Test
    public void testMessageProviderWithWrongParameterType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithWrongParameterType.java");
    }

    @Test
    public void testMessageProviderWithWrongReturnType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithWrongReturnType.java");
    }

    @Test
    public void testMinAndMaxConflict() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MinAndMaxConflict.java");
    }

    @Test
    public void testNonNumericWithNumericBounds() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/NonNumericWithNumericBounds.java");
    }

    @Test
    public void testValidatorDoesntExist() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorDoesntExist.java");
    }

    @Test
    public void testValidatorWithMoreThanOneParameter() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithMoreThanOneParameter.java");
    }

    @Test
    public void testValidatorWithoutPublicModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithoutPublicModifier.java");
    }

    @Test
    public void testValidatorWithoutStaticModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithoutStaticModifier.java");
    }

    @Test
    public void testValidatorWithWrongParameterType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithWrongParameterType.java");
    }

    @Test
    public void testValidatorWithWrongReturnType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithWrongReturnType.java");
    }
}
