package com.bawnorton.configurable.ap.helper;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Objects;
import java.util.Optional;

public class AnnotationHelper {
    public static Optional<AnnotationMirror> getSubAnnotation(AnnotationMirror parent, String name) {
        if(parent == null) return Optional.empty();

        return parent.getElementValues()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().getSimpleName().contentEquals(name))
                .findFirst()
                .map(entry -> (AnnotationMirror) entry.getValue());
    }

    public static boolean isDefaultValue(AnnotationMirror annotation, String methodName) {
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
}
