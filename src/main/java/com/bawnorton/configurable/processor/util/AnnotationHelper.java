package com.bawnorton.configurable.processor.util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

public class AnnotationHelper {
    public static boolean isDefaultValue(AnnotationMirror mirror, String attributeName) {
        if(mirror == null) return true;

        ExecutableElement attributeElement = null;
        for (ExecutableElement executableElement : mirror.getElementValues().keySet()) {
            if (executableElement.getSimpleName().contentEquals(attributeName)) {
                attributeElement = executableElement;
                break;
            }
        }
        if (attributeElement == null) return true;

        AnnotationValue defaultValue = attributeElement.getDefaultValue();
        AnnotationValue actualValue = mirror.getElementValues().get(attributeElement);

        return Objects.equals(actualValue, defaultValue);
    }

    public static AnnotationMirror getAnnotationMirror(Element element, Class<? extends Annotation> annotationClass) {
        if (element == null || annotationClass == null) return null;

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotationClass.getCanonicalName())) {
                return annotationMirror;
            }
        }
        return null;
    }

    public static AnnotationMirror getNestedAnnotationMirror(AnnotationMirror mirror, Class<? extends Annotation> annotationClass) {
        if (mirror == null || mirror.getAnnotationType() == null) return null;

        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
        for (ExecutableElement executableElement : elementValues.keySet()) {
            AnnotationValue value = elementValues.get(executableElement);
            if (value.getValue() instanceof AnnotationMirror nestedAnnotation) {
                if (nestedAnnotation.getAnnotationType().toString().equals(annotationClass.getCanonicalName())) {
                    return nestedAnnotation;
                }
            }
        }
        return null;
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror mirror, String attributeName) {
        if (mirror == null || attributeName == null) return null;

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(attributeName)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
