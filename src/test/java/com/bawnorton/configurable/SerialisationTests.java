package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.CompilationHelper;
import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.service.ConfigLoader;
import com.google.testing.compile.Compilation;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;

public class SerialisationTests extends BaseTest {
    @Test
    public void testBasicSerialisation() {
        ConfigurableTestHelper.logModule();
        Compilation compilation = testCompilationSuccess("sources/serialisation/BasicSerialisation.java");

        ClassLoader generatedClassLoader = CompilationHelper.getGeneratedClassLoader(compilation);
        try {
            Class<?> generatedClass = generatedClassLoader.loadClass(ConfigurableTestHelper.getConfigLoaderName());
            ConfigLoader instance = (ConfigLoader) generatedClass.getDeclaredConstructor().newInstance();
            Path root = Path.of(System.getProperty("user.dir")).getParent().getParent();
            Path configDir = root.resolve("test-output/%s/".formatted(ConfigurableTestHelper.getModuleName()));
            SaveLoader saveLoader = new SaveLoader(
                configDir.resolve("basic_serialisation.json5"),
                FileType.JSON
            );
            ConfigurableMain.setCurrentSaveLoader(saveLoader);
            instance.save();
            saveLoader.save();
            instance.load();
            saveLoader.load();
            instance.save();
            saveLoader.save();

            saveLoader = new SaveLoader(
                configDir.resolve("basic_serialisation.toml"),
                FileType.TOML
            );
            ConfigurableMain.setCurrentSaveLoader(saveLoader);
            instance.save();
            saveLoader.save();
            instance.load();
            saveLoader.load();
            instance.save();
            saveLoader.save();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance or access fields", e);
        }
    }
}
