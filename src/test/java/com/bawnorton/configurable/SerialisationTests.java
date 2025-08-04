package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.google.common.io.Resources;
import com.google.testing.compile.Compilation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class SerialisationTests extends BaseTest {
    @Test
    public void testBasicSerialisation() {
        ConfigurableTestHelper.logModule();
        Compilation compilation = testCompilationSuccess("sources/serialisation/BasicSerialisation.java");
        ConfigLoader instance = getLoaderFromCompilation(compilation);

        Path root = Path.of(System.getProperty("user.dir")).getParent().getParent();
        Path configDir = root.resolve("test-output/%s/".formatted(ConfigurableTestHelper.getModuleName()));
        SaveLoader saveLoader = new SaveLoader(
            configDir.resolve("basic_serialisation.json5"),
            FileType.JSON
        );
        instance.save(saveLoader);
        instance.load(saveLoader);
        instance.save(saveLoader);
        URL expectedJson = Resources.getResource("expected/configs/basic_serialisation.json5");
        assertFileContentEquals(
            expectedJson,
            configDir.resolve("basic_serialisation.json5")
        );

        saveLoader = new SaveLoader(
            configDir.resolve("basic_serialisation.toml"),
            FileType.TOML
        );
        instance.save(saveLoader);
        instance.load(saveLoader);
        instance.save(saveLoader);
        URL expectedToml = Resources.getResource("expected/configs/basic_serialisation.toml");
        assertFileContentEquals(
            expectedToml,
            configDir.resolve("basic_serialisation.toml")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSavingModifications() {
        ConfigurableTestHelper.logModule();
        Compilation compilation = testCompilationSuccess("sources/serialisation/SavingModifications.java");
        ConfigLoader instance = getLoaderFromCompilation(compilation);

        Path root = Path.of(System.getProperty("user.dir")).getParent().getParent();
        Path configDir = root.resolve("test-output/%s/".formatted(ConfigurableTestHelper.getModuleName()));
        SaveLoader saveLoader = new SaveLoader(
            configDir.resolve("saving_modifications.json5"),
            FileType.JSON
        );
        FieldReference<Integer> fieldReference = (FieldReference<Integer>) instance.getFields().getFirst();

        fieldReference.set(80);
        instance.save(saveLoader);
        instance.load(saveLoader);
        Assertions.assertEquals(80, fieldReference.get(), "Field value should be 80 after loading from JSON");

        fieldReference.set(30);

        instance.save(saveLoader);
        URL expectedJson = Resources.getResource("expected/configs/saving_modifications.json5");
        assertFileContentEquals(
            expectedJson,
            configDir.resolve("saving_modifications.json5")
        );

        saveLoader = new SaveLoader(
            configDir.resolve("saving_modifications.toml"),
            FileType.TOML
        );
        fieldReference.set(80);
        instance.save(saveLoader);
        instance.load(saveLoader);
        Assertions.assertEquals(80, fieldReference.get(), "Field value should be 80 after loading from TOML");

        fieldReference = (FieldReference<Integer>) instance.getFields().getFirst();
        fieldReference.set(30);

        instance.save(saveLoader);
        URL expectedToml = Resources.getResource("expected/configs/saving_modifications.toml");
        assertFileContentEquals(
            expectedToml,
            configDir.resolve("saving_modifications.toml")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testComplexSavingModifications() {
        ConfigurableTestHelper.logModule();
        Compilation compilation = testCompilationSuccess("sources/serialisation/ComplexSavingModifications.java");
        ConfigLoader instance = getLoaderFromCompilation(compilation);

        Path root = Path.of(System.getProperty("user.dir")).getParent().getParent();
        Path configDir = root.resolve("test-output/%s/".formatted(ConfigurableTestHelper.getModuleName()));
        SaveLoader saveLoader = new SaveLoader(
            configDir.resolve("complex_saving_modifications.json5"),
            FileType.JSON
        );
        FieldReference<List<List<String[]>>> fieldReference = (FieldReference<List<List<String[]>>>) instance.getFields().getFirst();

        instance.save(saveLoader);
        instance.load(saveLoader);

        fieldReference.set(List.of(
            List.of(new String[]{"a", "b", "c"}, new String[]{"d", "e", "f"}),
            List.of(new String[]{"g", "h", "i"}, new String[]{"j", "k", "l"})
        ));

        instance.save(saveLoader);
        URL expectedJson = Resources.getResource("expected/configs/complex_saving_modifications.json5");
        assertFileContentEquals(
            expectedJson,
            configDir.resolve("complex_saving_modifications.json5")
        );

        saveLoader = new SaveLoader(
            configDir.resolve("complex_saving_modifications.toml"),
            FileType.TOML
        );
        fieldReference.set(fieldReference.validator().defaultSupplier().get());
        instance.save(saveLoader);
        instance.load(saveLoader);

        fieldReference.set(List.of(
            List.of(new String[]{"g", "h", "i"}, new String[]{"j", "k", "l"}),
            List.of(new String[]{"a", "b", "c"}, new String[]{"d", "e", "f"})
        ));

        instance.save(saveLoader);
        URL expectedToml = Resources.getResource("expected/configs/complex_saving_modifications.toml");
        assertFileContentEquals(
            expectedToml,
            configDir.resolve("complex_saving_modifications.toml")
        );
    }

    @Test
    public void testGroupSerialisation() {
        ConfigurableTestHelper.logModule();
        Compilation compilation = testCompilationSuccess("sources/serialisation/GroupSerialisation.java");
        ConfigLoader instance = getLoaderFromCompilation(compilation);

        Path root = Path.of(System.getProperty("user.dir")).getParent().getParent();
        Path configDir = root.resolve("test-output/%s/".formatted(ConfigurableTestHelper.getModuleName()));
        SaveLoader saveLoader = new SaveLoader(
            configDir.resolve("group_serialisation.json5"),
            FileType.JSON
        );
        instance.save(saveLoader);
        instance.load(saveLoader);
        instance.save(saveLoader);
        URL expectedJson = Resources.getResource("expected/configs/group_serialisation.json5");
        assertFileContentEquals(
            expectedJson,
            configDir.resolve("group_serialisation.json5")
        );

        saveLoader = new SaveLoader(
            configDir.resolve("group_serialisation.toml"),
            FileType.TOML
        );
        instance.save(saveLoader);
        instance.load(saveLoader);
        instance.save(saveLoader);
        URL expectedToml = Resources.getResource("expected/configs/group_serialisation.toml");
        assertFileContentEquals(
            expectedToml,
            configDir.resolve("group_serialisation.toml")
        );
    }
}
