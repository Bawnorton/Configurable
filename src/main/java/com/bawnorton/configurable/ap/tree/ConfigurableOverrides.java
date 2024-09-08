package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.Image;
import com.bawnorton.configurable.OptionType;
import com.bawnorton.configurable.util.Pair;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class ConfigurableOverrides {
    private final Map<String, Pair<Boolean, Supplier<?>>> overrides = new HashMap<>();

    public ConfigurableOverrides(Map<String, Pair<Boolean, Supplier<?>>> overrides) {
        this.overrides.putAll(overrides);
    }

    public static void create(ConfigurableHolder parent, ConfigurableHolder child) {
        AnnotationMirror yaclMirror = child.getYaclMirror();
        child.setOverrides(builder()
                .addOverride(yaclMirror, "category", parent::category, () -> child.annotation().yacl().category())
                .addOverride(yaclMirror, "exclude", parent::exclude, () -> child.annotation().yacl().exclude())
                .addOverride(yaclMirror, "type", parent::type, () -> child.annotation().yacl().type())
                .addOverride(yaclMirror, "image", parent::image, () -> child.annotation().yacl().image())
                .build());
    }

    public String getCategory() {
        return (String) overrides.get("category").second().get();
    }

    public boolean getExclude() {
        return (Boolean) overrides.get("exclude").second().get();
    }

    public OptionType[] getOptionType() {
        return (OptionType[]) overrides.get("type").second().get();
    }

    public Image getImage() {
        return (Image) overrides.get("image").second().get();
    }

    public boolean imageOverridden() {
        return overrides.get("image").first();
    }

    private static Builder builder() {
        return new Builder();
    }

    private static class Builder {
        private final Map<String, Pair<Boolean, Supplier<?>>> overrides = new HashMap<>();

        public <T> Builder addOverride(AnnotationMirror mirror, String name, Supplier<T> override, Supplier<T> natural) {
            boolean isDefaultValue = isDefaultValue(mirror, name);
            overrides.put(name, Pair.of(isDefaultValue, isDefaultValue ? override : natural));
            return this;
        }

        private static boolean isDefaultValue(AnnotationMirror annotation, String methodName) {
            if(annotation == null) return true;

            ExecutableElement methodElement = null;
            for (ExecutableElement executableElement : annotation.getElementValues().keySet()) {
                if (executableElement.getSimpleName().contentEquals(methodName)) {
                    methodElement = executableElement;
                    break;
                }
            }
            if (methodElement == null) return true;

            AnnotationValue defaultValue = methodElement.getDefaultValue();
            AnnotationValue actualValue = annotation.getElementValues().get(methodElement);

            return Objects.equals(actualValue, defaultValue);
        }

        public ConfigurableOverrides build() {
            return new ConfigurableOverrides(overrides);
        }
    }
}
