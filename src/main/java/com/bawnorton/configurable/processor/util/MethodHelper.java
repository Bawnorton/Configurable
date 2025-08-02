package com.bawnorton.configurable.processor.util;

import com.bawnorton.configurable.util.Either;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;

public class MethodHelper {
    public static Either<MethodReference, String> getReference(Element enclosingClass, String methodReference, ProcessingEnvironment processingEnv) {
        int hashIndex = methodReference.indexOf('#');
        if (hashIndex == -1) {
            return getLocalMethodReference(enclosingClass, methodReference);
        } else {
            String className = methodReference.substring(0, hashIndex);
            String methodName = methodReference.substring(hashIndex + 1);
            return getExternalMethodReference(className, methodName, processingEnv);
        }
    }

    private static Either<MethodReference, String> getLocalMethodReference(Element enclosingClass, String methodName) {
        List<? extends Element> classElements = enclosingClass.getEnclosedElements();
        for (Element element : classElements) {
            if (element.getSimpleName().contentEquals(methodName) && element.getKind() == ElementKind.METHOD && element instanceof ExecutableElement methodElement) {
                return Either.left(new MethodReference(methodElement));
            }
        }
        return Either.right("Method '%s' not found in class '%s'".formatted(methodName, enclosingClass.getSimpleName()));
    }

    private static Either<MethodReference, String> getExternalMethodReference(String className, String methodName, ProcessingEnvironment processingEnv) {
        TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(className);
        if (typeElement == null) {
            return Either.right("Class '%s' not found".formatted(className));
        }
        return getLocalMethodReference(typeElement, methodName);
    }
}
