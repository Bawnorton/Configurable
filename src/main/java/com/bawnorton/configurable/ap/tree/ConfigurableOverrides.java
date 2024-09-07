package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.Image;
import com.bawnorton.configurable.OptionType;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Objects;
import java.util.function.Supplier;

public final class ConfigurableOverrides {
    private final Supplier<String> categoryGetter;
    private final Supplier<Boolean> excludeGetter;
    private final Supplier<OptionType[]> optionTypeGetter;
    private final Supplier<Image> imageGetter;

    public ConfigurableOverrides(Supplier<String> categoryGetter,
            Supplier<Boolean> excludeGetter,
            Supplier<OptionType[]> optionTypeGetter,
            Supplier<Image> imageGetter) {
        this.categoryGetter = categoryGetter;
        this.excludeGetter = excludeGetter;
        this.optionTypeGetter = optionTypeGetter;
        this.imageGetter = imageGetter;
    }

    public static void create(ConfigurableHolder parent, ConfigurableHolder child) {
        Builder builder = builder();
        AnnotationMirror yaclMirror = child.getYaclMirror();
        builder.setCategoryGetter(isDefaultValue(yaclMirror, "category") ? parent::category : () -> child.annotation().yacl().category());
        builder.setExcludeGetter(isDefaultValue(yaclMirror, "exclude") ? parent::exclude : () -> child.annotation().yacl().exclude());
        builder.setOptionTypeGetter(isDefaultValue(yaclMirror, "type") ? parent::type : () -> child.annotation().yacl().type());
        builder.setImageGetter(isDefaultValue(yaclMirror, "image") ? parent::image : () -> child.annotation().yacl().image());

        child.setOverrides(builder.build());
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

    public String getCategory() {
        return categoryGetter.get();
    }

    public boolean getExclude() {
        return excludeGetter.get();
    }

    public OptionType[] getOptionType() {
        return optionTypeGetter.get();
    }

    public Image getImage() {
        return imageGetter.get();
    }

    private static Builder builder() {
        return new Builder();
    }

    private static class Builder {
        private Supplier<String> categoryGetter;
        private Supplier<Boolean> excludeGetter;
        private Supplier<OptionType[]> optionTypeGetter;
        private Supplier<Image> imageGetter;

        public void setCategoryGetter(Supplier<String> categoryGetter) {
            this.categoryGetter = categoryGetter;
        }

        public void setExcludeGetter(Supplier<Boolean> excludeGetter) {
            this.excludeGetter = excludeGetter;
        }

        public void setOptionTypeGetter(Supplier<OptionType[]> optionTypeGetter) {
            this.optionTypeGetter = optionTypeGetter;
        }

        public void setImageGetter(Supplier<Image> imageGetter) {
            this.imageGetter = imageGetter;
        }

        public ConfigurableOverrides build() {
            return new ConfigurableOverrides(
                    categoryGetter,
                    excludeGetter,
                    optionTypeGetter,
                    imageGetter
            );
        }
    }
}
