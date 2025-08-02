package com.bawnorton.configurable.processor;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.io.FileType;
import org.jetbrains.annotations.NotNull;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.function.UnaryOperator;

public record ConfigurableSettings(String name, FileType fileType, NamingPolicy namingPolicy) {
    public static ConfigurableSettings fromProperties(Properties properties) {
        String name = properties.getProperty("name");
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Property 'name' must be specified and cannot be empty.");
        }
        FileType type = getEnumProperty(properties, "type", FileType.class, FileType.JSON);
        NamingPolicy namingPolicy = getEnumProperty(properties, "naming_policy", NamingPolicy.class, NamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        Set<String> knownProperties = Set.of("name", "type", "naming_policy");
        for (String key : properties.stringPropertyNames()) {
            if (!knownProperties.contains(key)) {
                throw new IllegalArgumentException("Unknown property '" + key + "' in configurable.properties. Known properties are: " + knownProperties);
            }
        }

        return new ConfigurableSettings(name, type, namingPolicy);
    }

    private static @NotNull <T extends Enum<T>> T getEnumProperty(Properties properties, String name, Class<T> enumClass, T defaultValue) {
        String enumStr = properties.getProperty(name);
        if (enumStr == null) return defaultValue;

        T enumValue;
        try {
            enumValue = Enum.valueOf(enumClass, enumStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            StringBuilder validTypes = new StringBuilder();
            for (T policy : enumClass.getEnumConstants()) {
                validTypes.append(policy.name()).append(", ");
            }
            validTypes.setLength(validTypes.length() - 2);
            throw new IllegalArgumentException("Invalid " + name + " '" + enumStr + "'. Valid values are \"" + validTypes + "\".", e);
        }
        return enumValue;
    }

    public enum NamingPolicy {
        IDENTITY(UnaryOperator.identity()),
        UPPER_CASE_WITH_UNDERSCORES(s -> separateCamelCase(s, '_').toUpperCase(Locale.ENGLISH)),
        LOWER_CASE_WITH_UNDERSCORES(s -> separateCamelCase(s, '_').toLowerCase(Locale.ENGLISH)),
        LOWER_CASE_WITH_DASHES(s -> separateCamelCase(s, '-').toLowerCase(Locale.ENGLISH)),
        LOWER_CASE_WITH_DOTS(s -> separateCamelCase(s, '.').toLowerCase(Locale.ENGLISH));

        private final UnaryOperator<String> formatter;

        NamingPolicy(UnaryOperator<String> formatter) {
            this.formatter = formatter;
        }

        public String format(String name) {
            return formatter.apply(name);
        }

        private static String separateCamelCase(String name, char separator) {
            StringBuilder result = new StringBuilder();
            int i = 0;

            for(int length = name.length(); i < length; ++i) {
                char character = name.charAt(i);
                if (Character.isUpperCase(character) && !result.isEmpty()) {
                    result.append(separator);
                }

                result.append(character);
            }

            return result.toString();
        }
    }
}
