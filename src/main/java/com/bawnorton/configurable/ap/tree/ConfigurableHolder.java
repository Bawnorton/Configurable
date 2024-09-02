package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ControllerType;
import com.bawnorton.configurable.OptionType;
import javax.lang.model.element.AnnotationMirror;
import java.util.Objects;

public final class ConfigurableHolder {
    private final Configurable annotation;
    private final AnnotationMirror configurableMirror;
    private final AnnotationMirror yaclMirror;
    private ConfigurableOverrides overrides;

    public ConfigurableHolder(Configurable annotation, AnnotationMirror configurableMirror) {
        this.annotation = annotation;
        this.configurableMirror = configurableMirror;

        yaclMirror = configurableMirror.getElementValues()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().getSimpleName().contentEquals("yacl"))
                .findFirst()
                .map(entry -> (AnnotationMirror) entry.getValue())
                .orElse(null);
    }

    public void setOverrides(ConfigurableOverrides overrides) {
        this.overrides = overrides;
    }

    public Configurable annotation() {
        return annotation;
    }

    public AnnotationMirror getConfigurableMirror() {
        return configurableMirror;
    }

    public AnnotationMirror getYaclMirror() {
        return yaclMirror;
    }

    public String value() {
        return annotation.value();
    }

    public String regex() {
        return overrides == null ? annotation().regex() : overrides.getRegex();
    }

    public String predicate() {
        return overrides == null ? annotation().predicate() : overrides.getPredicate();
    }

    public double min() {
        return overrides == null ? annotation().min() : overrides.getMin();
    }

    public double max() {
        return overrides == null ? annotation().max() : overrides.getMax();
    }

    public boolean exclude() {
        return overrides == null ? annotation().yacl().exclude() : overrides.getExclude();
    }

    public String category() {
        return overrides == null ? annotation().yacl().category() : overrides.getCategory();
    }

    public ControllerType controller() {
        return overrides == null ? annotation().yacl().controller() : overrides.getControllerType();
    }

    public String formatter() {
        return overrides == null ? annotation().yacl().formatter() : overrides.getFormatter();
    }

    public OptionType[] type() {
        return overrides == null ? annotation().yacl().type() : overrides.getOptionType();
    }

    public String[] listener() {
        return overrides == null ? annotation().yacl().listener() : overrides.getListeners();
    }

    public boolean collapsed() {
        return annotation.yacl().collapsed();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ConfigurableHolder) obj;
        return Objects.equals(this.annotation, that.annotation) &&
               Objects.equals(this.configurableMirror, that.configurableMirror);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, configurableMirror);
    }

    @Override
    public String toString() {
        return "AnnotationHolder[" +
               "annotation=" + annotation + ", " +
               "configurableMirror=" + configurableMirror + ']';
    }
}
