package com.bawnorton.configurable.api.impl;

import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.processor.ConfigurableSettings;
import com.bawnorton.configurable.service.ConfigLoader;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;
import java.io.InputStream;
import java.util.Properties;

@ApiStatus.Internal
public class ConfigurableApiImpl {
    public static void saveChanges(ServerLevel level, boolean sync) {
        ConfigLoader configLoader = getCallersConfigLoader();
        ConfigurableLoader.saveChanges(configLoader, level, sync);
    }

    /**
     * Attempts to find the caller's ConfigLoader class based on the stack trace.
     */
    private static ConfigLoader getCallersConfigLoader() {
        try {
            Class<?> caller = findCaller();
            ClassLoader classLoader = caller.getClassLoader();

            Properties properties = new Properties();
            try (InputStream in = classLoader.getResourceAsStream("META-INF/configurable.properties")) {
                if (in == null) {
                    throw new IllegalStateException("No configurable.properties found in caller's jar");
                }
                properties.load(in);
            }
            ConfigurableSettings settings = ConfigurableSettings.fromProperties(properties);
            String name = settings.name();
            return ConfigurableLoader.getConfigLoader(name);
        } catch (Exception e) {
            throw new RuntimeException("Could not find ConfigLoader for the caller", e);
        }
    }

    private static Class<?> findCaller() {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(frames -> frames
                .map(StackWalker.StackFrame::getDeclaringClass)
                .filter(c -> !c.getPackageName().startsWith("com.bawnorton.configurable"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No suitable caller found in the stack trace.")));
    }
}
