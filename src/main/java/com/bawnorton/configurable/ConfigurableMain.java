package com.bawnorton.configurable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableMain {
    public static final String MOD_ID = "configurable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Configurable Initialized");
    }
}
