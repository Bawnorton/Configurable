package com.bawnorton.configurable.ap.helper;

import javax.lang.model.element.AnnotationMirror;
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
}
