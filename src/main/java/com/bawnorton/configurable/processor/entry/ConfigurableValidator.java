package com.bawnorton.configurable.processor.entry;

import com.bawnorton.configurable.processor.element.ConfigurableElement;
import com.bawnorton.configurable.processor.util.AnnotationHelper;
import com.bawnorton.configurable.util.Either;
import com.bawnorton.configurable.processor.util.MethodHelper;
import com.bawnorton.configurable.processor.util.MethodReference;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ConfigurableValidator {
    private final boolean fallback;
    private final String defaultValue;
    private final @Nullable MethodReference validatorMethod;
    private final @Nullable MethodReference messageMethod;
    private final @Nullable String messageLiteral;
    private final @Nullable Double min;
    private final @Nullable Double max;

    private ConfigurableValidator(boolean fallback,
            String defaultValue,
            @Nullable MethodReference validatorMethod,
            @Nullable MethodReference messageMethod,
            @Nullable String messageLiteral,
            @Nullable Double min,
            @Nullable Double max) {
        this.fallback = fallback;
        this.defaultValue = defaultValue;
        this.validatorMethod = validatorMethod;
        this.messageMethod = messageMethod;
        this.messageLiteral = messageLiteral;
        this.min = min;
        this.max = max;
    }

    public static ConfigurableValidator fromConfigurableElement(ConfigurableElement configurableElement, ProcessingEnvironment processingEnv) {
        Double min = configurableElement.isMinSet() ? configurableElement.getMin() : null;
        Double max = configurableElement.isMaxSet() ? configurableElement.getMax() : null;
        boolean isNumeric = configurableElement.isNumeric();
        Element annotatedElement = configurableElement.getAnnotatedElement();
        if (!isNumeric && (min != null || max != null)) {
            processingEnv.getMessager().printError("Min and max values can only be set for numeric fields, but %s is not numeric".formatted(configurableElement.getElementName()), annotatedElement);
            return null;
        }
        String defaultValue = configurableElement.getDefaultValue();
        if (isNumeric) {
            double asDouble;
            try {
                asDouble = Double.parseDouble(defaultValue);
            } catch (NumberFormatException e) {
                processingEnv.getMessager().printError("Default value '%s' for '%s' is not a valid number".formatted(defaultValue, configurableElement.getElementName()), annotatedElement);
                return null;
            }
            if (min != null && asDouble < min) {
                processingEnv.getMessager().printError("Default value '%s' for '%s' is less than the minimum value of '%s'".formatted(defaultValue, configurableElement.getElementName(), min), annotatedElement);
                return null;
            }
            if (max != null && asDouble > max) {
                processingEnv.getMessager().printError("Default value '%s' for '%s' is greater than the maximum value of '%s'".formatted(defaultValue, configurableElement.getElementName(), max), annotatedElement);
                return null;
            }
        }

        Element classElement = annotatedElement.getEnclosingElement();
        String validatorMethod = configurableElement.getValidatorReference();
        MethodReference reference = null;
        if(!validatorMethod.isEmpty()) {
            Either<MethodReference, String> maybeReference = MethodHelper.getReference(classElement, validatorMethod, processingEnv);
            reference = validateMethodReference(maybeReference, processingEnv, validatorMethod, message -> {
                AnnotationMirror validatorMirror = configurableElement.getValidatorMirror();
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        message,
                        annotatedElement,
                        validatorMirror,
                        AnnotationHelper.getAnnotationValue(validatorMirror, "value")
                );
            });
            if (reference == null) return null;

            TypeMirror actualReturnType = reference.returnType();
            TypeMirror expectedReturnType = processingEnv.getTypeUtils().getPrimitiveType(TypeKind.BOOLEAN);
            if (!processingEnv.getTypeUtils().isAssignable(actualReturnType, expectedReturnType)) {
                processingEnv.getMessager().printError("Return fileType of method '%s' must be boolean, but found '%s'".formatted(validatorMethod, actualReturnType), reference.methodElement());
                return null;
            }

            List<TypeMirror> parameterTypes = reference.parameterTypes();
            if (parameterTypes.size() != 1) {
                VariableElement problemElement = reference.methodElement().getParameters().get(1);
                processingEnv.getMessager().printError("Method '%s' must have exactly one parameter, but found %d".formatted(validatorMethod, parameterTypes.size()), problemElement);
                return null;
            }

            TypeMirror actualParameterType = parameterTypes.getFirst();
            TypeMirror expectedParameterType = annotatedElement.asType();
            if (expectedParameterType.getKind().isPrimitive()) {
                expectedParameterType = processingEnv.getTypeUtils().boxedClass((PrimitiveType) expectedParameterType).asType();
            }
            if (!processingEnv.getTypeUtils().isSameType(actualParameterType, expectedParameterType)) {
                VariableElement problemElement = reference.methodElement().getParameters().getFirst();
                processingEnv.getMessager().printError("Method '%s' must accept a parameter of fileType '%s', but found '%s'".formatted(validatorMethod, expectedParameterType, actualParameterType), problemElement);
                return null;
            }
        }

        String maybeMessageMethod = configurableElement.getFailureMessageReference();
        boolean fallback = configurableElement.doesFallback();
        String messageLiteral = null;
        MethodReference messageReference = null;
        if(maybeMessageMethod.isEmpty()) {
            if(isNumeric) {
                if(min != null) {
                    messageLiteral = "Value for '%s' must be greater than or equal to '%s'".formatted(configurableElement.getElementName(), min);
                } else if(max != null) {
                    messageLiteral = "Value for '%s' must be less than or equal to '%s'".formatted(configurableElement.getElementName(), max);
                } else {
                    messageLiteral = "Value for '%s' must be a number".formatted(configurableElement.getElementName());
                }
            } else {
                messageLiteral = "Value for '%s' is invalid".formatted(configurableElement.getElementName());
            }
            if(fallback) {
                messageLiteral += ". Resetting to default value: '%s'".formatted(defaultValue);
            }
        } else {
            Either<MethodReference, String> maybeMessageMethodReference = MethodHelper.getReference(classElement, maybeMessageMethod, processingEnv);
            if (maybeMessageMethodReference.isRight()) { // not a valid method reference so treat it as a literal
                messageLiteral = maybeMessageMethod;
            } else {
                messageReference = validateMethodReference(maybeMessageMethodReference, processingEnv, maybeMessageMethod, message -> {});
                if (messageReference == null) return null;

                TypeMirror messageReturnType = messageReference.returnType();
                TypeMirror expectedMessageReturnType = processingEnv.getElementUtils()
                        .getTypeElement(String.class.getCanonicalName())
                        .asType();

                if (!processingEnv.getTypeUtils().isAssignable(messageReturnType, expectedMessageReturnType)) {
                    processingEnv.getMessager().printError("Return fileType of method '%s' must be String, but found '%s'".formatted(maybeMessageMethod, messageReturnType), messageReference.methodElement());
                    return null;
                }

                List<TypeMirror> messageParameterTypes = messageReference.parameterTypes();
                if (messageParameterTypes.size() != 1) {
                    Element problemElement = messageReference.methodElement().getParameters().get(1);
                    processingEnv.getMessager().printError("Method '%s' must have exactly one parameter, but found %d".formatted(maybeMessageMethod, messageParameterTypes.size()), problemElement);
                    return null;
                }

                TypeMirror messageParameterType = messageParameterTypes.getFirst();
                TypeMirror expectedParameterType = annotatedElement.asType();
                if (expectedParameterType.getKind().isPrimitive()) {
                    expectedParameterType = processingEnv.getTypeUtils().boxedClass((PrimitiveType) expectedParameterType).asType();
                }
                if (!processingEnv.getTypeUtils().isSameType(messageParameterType, expectedParameterType)) {
                    Element problemElement = messageReference.methodElement().getParameters().getFirst();
                    processingEnv.getMessager().printError("Message method '%s' must accept a parameter of fileType '%s', but found '%s'".formatted(maybeMessageMethod, expectedParameterType, messageParameterType), problemElement);
                    return null;
                }
            }
        }

        return new ConfigurableValidator(fallback, defaultValue, reference, messageReference, messageLiteral, min, max);
    }

    private static @Nullable MethodReference validateMethodReference(Either<MethodReference, String> maybeReference, ProcessingEnvironment processingEnv, String validatorMethod, Consumer<String> errorConsumer) {
        if (maybeReference.isRight()) {
            String errorMessage = maybeReference.getRight();
            errorConsumer.accept(errorMessage);
            return null;
        }

        MethodReference reference = maybeReference.getLeft();
        if (!reference.isPublic()) {
            processingEnv.getMessager().printError("Method '%s' must be public".formatted(validatorMethod), reference.methodElement());
            return null;
        }
        if (!reference.isStatic()) {
            processingEnv.getMessager().printError("Method '%s' must be static".formatted(validatorMethod), reference.methodElement());
            return null;
        }
        return reference;
    }

    public boolean doesFallback() {
        return fallback;
    }

    public boolean hasValidatorMethod() {
        return validatorMethod != null;
    }

    public @Nullable MethodReference getValidatorMethod() {
        return validatorMethod;
    }

    public boolean hasMessageMethod() {
        return messageMethod != null;
    }

    public @Nullable MethodReference getMessageMethod() {
        return messageMethod;
    }

    public @Nullable String getMessageLiteral() {
        return messageLiteral;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
