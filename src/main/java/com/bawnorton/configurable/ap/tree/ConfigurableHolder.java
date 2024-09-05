package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ControllerType;
import com.bawnorton.configurable.OptionType;
import com.bawnorton.configurable.ap.helper.AnnotationHelper;
import javax.lang.model.element.AnnotationMirror;
import java.util.Objects;

public final class ConfigurableHolder {
    private final Configurable annotation;
    private final AnnotationMirror configurableMirror;
    private final AnnotationMirror yaclMirror;
    private final AnnotationMirror imageMirror;
    private ConfigurableOverrides overrides;

    public ConfigurableHolder(Configurable annotation, AnnotationMirror configurableMirror) {
        this.annotation = annotation;
        this.configurableMirror = configurableMirror;

        yaclMirror = AnnotationHelper.getSubAnnotation(configurableMirror, "yacl").orElse(null);
        imageMirror = AnnotationHelper.getSubAnnotation(yaclMirror, "image").orElse(null);
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

    public AnnotationMirror getImageMirror() {
        return imageMirror;
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

    public String descriptioner() {
        return overrides == null ? annotation().yacl().descriptioner() : overrides.getDescriptioner();
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
}
