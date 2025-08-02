package com.bawnorton.configurable.helper;

import com.bawnorton.configurable.util.Pair;
import com.google.common.io.Resources;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import java.io.InputStream;

public class ConfigurableTestHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurableTestHelper.class);
    private static final String VERSION = /*? if 1.21.1 {*/ /*"1.21.1" *//*?} elif 1.21.8 {*/ "1.21.8" /*?}*/;
    private static final String LOADER = /*? if fabric {*/ "Fabric" /*?} elif neoforge {*/ /*"NeoForge" *//*?}*/;

    public static void logModule() {
        LOGGER.info(ConfigurableTestHelper::getModuleName);
    }

    public static String getModuleName() {
        return "%s-%s".formatted(VERSION, LOADER.toLowerCase());
    }

    public static Pair<JavaFileObject, JavaFileObject> getSourceAndExpected(String resourceName) {
        JavaFileObject sourceFile = JavaFileObjects.forResource(Resources.getResource(resourceName));
        JavaFileObject expectedFile = JavaFileObjects.forResource(Resources.getResource("expected/" + resourceName));
        return new Pair<>(sourceFile, expectedFile);
    }

    public static String getConfigLoaderName() {
        return "com.bawnorton.configurable.generated.test_project.GeneratedConfigLoader";
    }
}
