package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.ControllerType;
import com.bawnorton.configurable.OptionType;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Objects;
import java.util.function.Supplier;

public final class ConfigurableOverrides {
    private final Supplier<String> regexGetter;
    private final Supplier<String> predicateGetter;
    private final Supplier<Double> minGetter;
    private final Supplier<Double> maxGetter;
    private final Supplier<String> categoryGetter;
    private final Supplier<Boolean> excludeGetter;
    private final Supplier<ControllerType> controllerTypeGetter;
    private final Supplier<String> formatterGetter;
    private final Supplier<OptionType[]> optionTypeGetter;
    private final Supplier<String[]> listenerGetter;

    public ConfigurableOverrides(Supplier<String> regexGetter,
            Supplier<String> predicateGetter,
            Supplier<Double> minGetter,
            Supplier<Double> maxGetter,
            Supplier<String> categoryGetter,
            Supplier<Boolean> excludeGetter,
            Supplier<ControllerType> controllerTypeGetter,
            Supplier<String> formatterGetter,
            Supplier<OptionType[]> optionTypeGetter,
            Supplier<String[]> listenerGetter) {
        this.regexGetter = regexGetter;
        this.predicateGetter = predicateGetter;
        this.minGetter = minGetter;
        this.maxGetter = maxGetter;
        this.categoryGetter = categoryGetter;
        this.excludeGetter = excludeGetter;
        this.controllerTypeGetter = controllerTypeGetter;
        this.formatterGetter = formatterGetter;
        this.optionTypeGetter = optionTypeGetter;
        this.listenerGetter = listenerGetter;
    }

    public static void create(ConfigurableHolder parent, ConfigurableHolder child) {
        Builder builder = builder();
        if(isDefaultValue(child.getConfigurableMirror(), "regex")) {
            builder.setRegexGetter(parent::regex);
        } else {
            builder.setRegexGetter(() -> child.annotation().regex());
        }
        if(isDefaultValue(child.getConfigurableMirror(), "predicate")) {
            builder.setPredicateGetter(parent::predicate);
        } else {
            builder.setPredicateGetter(() -> child.annotation().predicate());
        }
        if(isDefaultValue(child.getConfigurableMirror(), "min")) {
            builder.setMinGetter(parent::min);
        } else {
            builder.setMinGetter(() -> child.annotation().min());
        }
        if(isDefaultValue(child.getConfigurableMirror(), "max")) {
            builder.setMaxGetter(parent::max);
        } else {
            builder.setMaxGetter(() -> child.annotation().max());
        }
        if(isDefaultValue(child.getYaclMirror(), "category")) {
            builder.setCategoryGetter(parent::category);
        } else {
            builder.setCategoryGetter(() -> child.annotation().yacl().category());
        }
        if(isDefaultValue(child.getYaclMirror(), "exclude")) {
            builder.setExcludeGetter(parent::exclude);
        } else {
            builder.setExcludeGetter(() -> child.annotation().yacl().exclude());
        }
        if(isDefaultValue(child.getYaclMirror(), "controller")) {
            builder.setControllerTypeGetter(parent::controller);
        } else {
            builder.setControllerTypeGetter(() -> child.annotation().yacl().controller());
        }
        if(isDefaultValue(child.getYaclMirror(), "formatter")) {
            builder.setFormatterGetter(parent::formatter);
        } else {
            builder.setFormatterGetter(() -> child.annotation().yacl().formatter());
        }
        if(isDefaultValue(child.getYaclMirror(), "type")) {
            builder.setOptionTypeGetter(parent::type);
        } else {
            builder.setOptionTypeGetter(() -> child.annotation().yacl().type());
        }
        if(isDefaultValue(child.getYaclMirror(), "listener")) {
            builder.setListenerGetter(parent::listener);
        } else {
            builder.setListenerGetter(() -> child.annotation().yacl().listener());
        }
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

    public String getRegex() {
        return regexGetter.get();
    }

    public String getPredicate() {
        return predicateGetter.get();
    }

    public double getMin() {
        return minGetter.get();
    }

    public double getMax() {
        return maxGetter.get();
    }

    public String getCategory() {
        return categoryGetter.get();
    }

    public boolean getExclude() {
        return excludeGetter.get();
    }

    public ControllerType getControllerType() {
        return controllerTypeGetter.get();
    }

    public String getFormatter() {
        return formatterGetter.get();
    }

    public OptionType[] getOptionType() {
        return optionTypeGetter.get();
    }

    public String[] getListeners() {
        return listenerGetter.get();
    }

    private static Builder builder() {
        return new Builder();
    }

    private static class Builder {
        private Supplier<String> regexGetter;
        private Supplier<String> predicateGetter;
        private Supplier<Double> minGetter;
        private Supplier<Double> maxGetter;
        private Supplier<String> categoryGetter;
        private Supplier<Boolean> excludeGetter;
        private Supplier<ControllerType> controllerTypeGetter;
        private Supplier<String> formatterGetter;
        private Supplier<OptionType[]> optionTypeGetter;
        private Supplier<String[]> listenerGetter;

        public void setRegexGetter(Supplier<String> regexGetter) {
            this.regexGetter = regexGetter;
        }

        public void setPredicateGetter(Supplier<String> predicateGetter) {
            this.predicateGetter = predicateGetter;
        }

        public void setMinGetter(Supplier<Double> minGetter) {
            this.minGetter = minGetter;
        }

        public void setMaxGetter(Supplier<Double> maxGetter) {
            this.maxGetter = maxGetter;
        }

        public void setCategoryGetter(Supplier<String> categoryGetter) {
            this.categoryGetter = categoryGetter;
        }

        public void setExcludeGetter(Supplier<Boolean> excludeGetter) {
            this.excludeGetter = excludeGetter;
        }

        public void setControllerTypeGetter(Supplier<ControllerType> controllerTypeGetter) {
            this.controllerTypeGetter = controllerTypeGetter;
        }

        public void setFormatterGetter(Supplier<String> formatterGetter) {
            this.formatterGetter = formatterGetter;
        }

        public void setOptionTypeGetter(Supplier<OptionType[]> optionTypeGetter) {
            this.optionTypeGetter = optionTypeGetter;
        }

        public void setListenerGetter(Supplier<String[]> listenerGetter) {
            this.listenerGetter = listenerGetter;
        }

        public ConfigurableOverrides build() {
            return new ConfigurableOverrides(
                    regexGetter,
                    predicateGetter,
                    minGetter,
                    maxGetter,
                    categoryGetter,
                    excludeGetter,
                    controllerTypeGetter,
                    formatterGetter,
                    optionTypeGetter,
                    listenerGetter
            );
        }
    }
}
