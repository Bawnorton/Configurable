package com.bawnorton.configurable;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

public class ConfigurableTests {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurableTests.class);
    private static final String VERSION = /*? if 1.21.1 {*/ "1.21.1" /*?} elif 1.21.8 {*/ /*"1.21.8" *//*?}*/;
    private static final String LOADER = /*? if fabric {*/ "Fabric" /*?} elif neoforge {*/ /*"NeoForge" *//*?}*/;

    @BeforeAll
    public static void setup() {
    }

    @Test
    public void assertSingleFieldCompiles() {
        logModule();
        CompilationHelper.compile("SingleField");
    }

    private static void logModule() {
        LOGGER.info(() -> "%s %s".formatted(LOADER, VERSION));
    }
}
