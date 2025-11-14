package com.bawnorton.configurable.processor.util;

import com.bawnorton.configurable.util.Either;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

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
			if (element.getSimpleName()
					.contentEquals(methodName) && element.getKind() == ElementKind.METHOD && element instanceof ExecutableElement methodElement) {
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


	public static @Nullable MethodReference validateMethodReference(Either<MethodReference, String> maybeReference, ProcessingEnvironment processingEnv, String validatorMethod, Consumer<String> errorConsumer) {
		if (maybeReference.isRight()) {
			String errorMessage = maybeReference.getRight();
			errorConsumer.accept(errorMessage);
			return null;
		}

		MethodReference reference = maybeReference.getLeft();
		if (!reference.isPublic()) {
			processingEnv.getMessager()
					.printError("Method '%s' must be public".formatted(validatorMethod), reference.methodElement());
			return null;
		}
		if (!reference.isStatic()) {
			processingEnv.getMessager()
					.printError("Method '%s' must be static".formatted(validatorMethod), reference.methodElement());
			return null;
		}
		return reference;
	}

	public static boolean validateParameters(Element annotatedElement, String methodName, MethodReference methodReference, ProcessingEnvironment processingEnv) {
		List<TypeMirror> parameterTypes = methodReference.parameterTypes();
		if (parameterTypes.size() != 1) {
			VariableElement problemElement = methodReference.methodElement().getParameters().get(1);
			processingEnv.getMessager()
					.printError("Method '%s' must have exactly one parameter, but found %d".formatted(methodName, parameterTypes.size()), problemElement);
			return false;
		}

		TypeMirror actualParameterType = parameterTypes.getFirst();
		TypeMirror expectedParameterType = annotatedElement.asType();
		if (expectedParameterType.getKind().isPrimitive()) {
			expectedParameterType = processingEnv.getTypeUtils()
					.boxedClass((PrimitiveType) expectedParameterType)
					.asType();
		}
		if (!processingEnv.getTypeUtils().isSameType(actualParameterType, expectedParameterType)) {
			VariableElement problemElement = methodReference.methodElement().getParameters().getFirst();
			processingEnv.getMessager()
					.printError("Method '%s' must accept a parameter of type '%s', but found '%s'".formatted(methodName, expectedParameterType, actualParameterType), problemElement);
			return false;
		}
		return true;
	}
}
