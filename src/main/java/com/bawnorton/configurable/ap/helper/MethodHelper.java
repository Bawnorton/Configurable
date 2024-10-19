package com.bawnorton.configurable.ap.helper;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Optional;

public class MethodHelper {
    public static TypeMirror getMethodReturnType(Elements elements, String owner, String methodName) {
        TypeElement ownerElement = elements.getTypeElement(owner);

        if (ownerElement == null) return null;

        List<ExecutableElement> methods = ElementFilter.methodsIn(ownerElement.getEnclosedElements());
        Optional<ExecutableElement> matchingMethod = methods.stream()
                .filter(method -> method.getSimpleName().toString().equals(methodName))
                .findFirst();

        return matchingMethod.map(ExecutableElement::getReturnType).orElse(null);
    }
}
