package com.bawnorton.configurable.processor.entry;

import com.bawnorton.configurable.processor.element.ConfigurableElement;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import org.jetbrains.annotations.NotNull;

public class ConfigurableEntry {
    private final Element annotatedElement;
    private final String name;
    private final String group;
    private final String comment;
    private final boolean sync;
    private final @NotNull ConfigurableValidator validator;

    public ConfigurableEntry(Element annotatedElement, String name, String group, String comment, boolean sync, @NotNull ConfigurableValidator validator) {
        this.annotatedElement = annotatedElement;
        this.name = name;
        this.group = group;
        this.comment = comment;
        this.sync = sync;
        this.validator = validator;
    }

    public static ConfigurableEntry createEntry(String name, ConfigurableElement element, ProcessingEnvironment processingEnv) {
        String group = element.getGroup();
        boolean sync = element.doesSync();
        String comment = element.getComment();
        ConfigurableValidator validator = ConfigurableValidator.fromConfigurableElement(name, element, processingEnv);
        if (validator == null) return null;

        return new ConfigurableEntry(element.getAnnotatedElement(), name, group, comment, sync, validator);
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return annotatedElement.getSimpleName().toString();
    }

    public boolean doesSync() {
        return sync;
    }

    public String getGroup() {
        return group;
    }

    public String getComment() {
        return comment;
    }

    public boolean hasComment() {
        return comment != null && !comment.isEmpty();
    }

    public boolean hasGroup() {
        return group != null && !group.isEmpty();
    }

    public @NotNull ConfigurableValidator getValidator() {
        return validator;
    }

    public TypeMirror getTypeMirror(ProcessingEnvironment processingEnv) {
        TypeMirror type = annotatedElement.asType();
        if (type.getKind().isPrimitive()) {
            type = processingEnv.getTypeUtils().boxedClass((PrimitiveType) type).asType();
        }
        return type;
    }

    public TypeMirror getEnclosingClassTypeMirror() {
        return annotatedElement.getEnclosingElement().asType();
    }
}
