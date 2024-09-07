package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ControllerType;
import com.bawnorton.configurable.Image;
import com.bawnorton.configurable.OptionType;
import com.bawnorton.configurable.ap.helper.AnnotationHelper;
import javax.lang.model.element.AnnotationMirror;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
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

    public boolean exclude() {
        return overrides == null ? annotation().yacl().exclude() : overrides.getExclude();
    }

    public String category() {
        return overrides == null ? annotation().yacl().category() : overrides.getCategory();
    }

    public OptionType[] type() {
        return overrides == null ? annotation().yacl().type() : overrides.getOptionType();
    }

    public Image image() {
        return overrides == null ? annotation().yacl().image() : overrides.getImage();
    }

    public boolean inheritedImage() {
        if(overrides == null) {
            return false;
        }
        return overrides.getImage().equals(annotation().yacl().image());
    }

    public String value() {
        return annotation.value();
    }

    public String regex() {
        return annotation().regex();
    }

    public String predicate() {
        return annotation().predicate();
    }

    public double min() {
        return annotation.min();
    }

    public double max() {
        return annotation.max();
    }

    public ControllerType controller() {
        return annotation().yacl().controller();
    }

    public String formatter() {
        return annotation().yacl().formatter();
    }

    public String descriptioner() {
        return annotation().yacl().descriptioner();
    }

    public String[] listener() {
        return annotation().yacl().listener();
    }

    public boolean collapsed() {
        return annotation.yacl().collapsed();
    }
}
