package com.bawnorton.configurable.processor.entry;

import com.bawnorton.configurable.processor.element.ConfigurableElement;
import com.bawnorton.configurable.processor.util.AnnotationHelper;
import com.bawnorton.configurable.processor.util.MethodHelper;
import com.bawnorton.configurable.processor.util.MethodReference;
import com.bawnorton.configurable.util.Either;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
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

	public static ConfigurableValidator fromConfigurableElement(String fullName, ConfigurableElement configurableElement, ProcessingEnvironment processingEnv) {
		Double min = configurableElement.isMinSet() ? configurableElement.getMin() : null;
		Double max = configurableElement.isMaxSet() ? configurableElement.getMax() : null;
		boolean isNumeric = configurableElement.isNumeric();
		Element annotatedElement = configurableElement.getAnnotatedElement();
		if (!isNumeric && (min != null || max != null)) {
			processingEnv.getMessager()
					.printError("Min and max values can only be set for numeric fields, but %s is not numeric".formatted(configurableElement.getElementName()), annotatedElement);
			return null;
		}
		if (max != null && min != null && max < min) {
			processingEnv.getMessager()
					.printError("Maximum value '%s' for '%s' is less than minimum value '%s'".formatted(max, configurableElement.getElementName(), min), annotatedElement);
			return null;
		}
		String defaultValue = configurableElement.getRightHandSide();
		if (isNumeric) {
			if (defaultValue.equals("null")) {
				TypeMirror fieldType = annotatedElement.asType();
				if (fieldType.getKind().isPrimitive()) {
					processingEnv.getMessager()
							.printError("Default value for '%s' cannot be null because it is a primitive type".formatted(configurableElement.getElementName()), annotatedElement);
					return null;
				}
			} else {
				String asLower = defaultValue.toLowerCase();
				if (asLower.endsWith("d") || asLower.endsWith("f") || asLower.endsWith("l")) {
					asLower = asLower.substring(0, asLower.length() - 1);
				}
				double asDouble;
				try {
					asDouble = Double.parseDouble(asLower);
				} catch (NumberFormatException e) {
					processingEnv.getMessager()
							.printError("Default value '%s' for '%s' is not a valid number".formatted(defaultValue, configurableElement.getElementName()), annotatedElement);
					return null;
				}
				if (min != null && asDouble < min) {
					processingEnv.getMessager()
							.printError("Default value '%s' for '%s' is less than the minimum value of '%s'".formatted(defaultValue, configurableElement.getElementName(), min), annotatedElement);
					return null;
				}
				if (max != null && asDouble > max) {
					processingEnv.getMessager()
							.printError("Default value '%s' for '%s' is greater than the maximum value of '%s'".formatted(defaultValue, configurableElement.getElementName(), max), annotatedElement);
					return null;
				}
			}
		} else {
			TypeMirror fieldType = annotatedElement.asType();
			if (fieldType instanceof ArrayType arrayType) {
				TypeMirror componentType = arrayType.getComponentType();
				String componentTypeName = processingEnv.getTypeUtils().erasure(componentType).toString();
				defaultValue = "new %s[]%s".formatted(componentTypeName, defaultValue);
			}
		}

		Element classElement = annotatedElement.getEnclosingElement();
		String validatorMethod = configurableElement.getValidatorReference();
		MethodReference validatorReference = null;
		if (!validatorMethod.isEmpty()) {
			if (min != null || max != null) {
				processingEnv.getMessager()
						.printWarning("Min and max values are set for '%s', but a validator method is also specified. The validator method will be used instead of min/max validation.".formatted(fullName), annotatedElement);
				return null;
			}

			Either<MethodReference, String> maybeReference = MethodHelper.getReference(classElement, validatorMethod, processingEnv);
			validatorReference = MethodHelper.validateMethodReference(maybeReference, processingEnv, validatorMethod, message -> {
				AnnotationMirror validatorMirror = configurableElement.getValidatorMirror();
				processingEnv.getMessager().printMessage(
						Diagnostic.Kind.ERROR,
						message,
						annotatedElement,
						validatorMirror,
						AnnotationHelper.getAnnotationValue(validatorMirror, "value")
				);
			});
			if (validatorReference == null) return null;

			TypeMirror actualReturnType = validatorReference.returnType();
			TypeMirror expectedReturnType = processingEnv.getTypeUtils().getPrimitiveType(TypeKind.BOOLEAN);
			if (!processingEnv.getTypeUtils().isAssignable(actualReturnType, expectedReturnType)) {
				processingEnv.getMessager()
						.printError("Return type of method '%s' must be boolean, but found '%s'".formatted(validatorMethod, actualReturnType), validatorReference.methodElement());
				return null;
			}

			if (!MethodHelper.validateParameters(annotatedElement, validatorMethod, validatorReference, processingEnv))
				return null;
		}

		String maybeMessageMethod = configurableElement.getFailureMessageReference();
		boolean fallback = configurableElement.doesFallback();
		String messageLiteral = null;
		MethodReference messageReference = null;
		if (maybeMessageMethod.isEmpty()) {
			if (validatorReference == null) {
				if (isNumeric) {
					if (min != null) {
						messageLiteral = "Value for '%s' must be greater than or equal to '%s'".formatted(fullName, min);
					} else if (max != null) {
						messageLiteral = "Value for '%s' must be less than or equal to '%s'".formatted(fullName, max);
					} else {
						messageLiteral = "Value for '%s' must be a number".formatted(fullName);
					}
				} else {
					messageLiteral = "Value for '%s' is invalid".formatted(fullName);
				}
			} else {
				messageLiteral = "Value for '%s' does not adhere to its validator: '%s'".formatted(fullName, validatorReference.getName());
			}
			if (fallback) {
				messageLiteral += ". Resetting to default value: '%s'".formatted(defaultValue);
			}
		} else {
			Either<MethodReference, String> maybeMessageMethodReference = MethodHelper.getReference(classElement, maybeMessageMethod, processingEnv);
			if (maybeMessageMethodReference.isRight()) { // not a valid method reference so treat it as a literal
				messageLiteral = maybeMessageMethod;
			} else {
				messageReference = MethodHelper.validateMethodReference(maybeMessageMethodReference, processingEnv, maybeMessageMethod, message -> {
				});
				if (messageReference == null) return null;

				TypeMirror messageReturnType = messageReference.returnType();
				TypeMirror expectedMessageReturnType = processingEnv.getElementUtils()
						.getTypeElement(String.class.getCanonicalName())
						.asType();

				if (!processingEnv.getTypeUtils().isAssignable(messageReturnType, expectedMessageReturnType)) {
					processingEnv.getMessager()
							.printError("Return type of method '%s' must be String, but found '%s'".formatted(maybeMessageMethod, messageReturnType), messageReference.methodElement());
					return null;
				}

				if (!MethodHelper.validateParameters(annotatedElement, maybeMessageMethod, messageReference, processingEnv))
					return null;
			}
		}

		return new ConfigurableValidator(fallback, defaultValue, validatorReference, messageReference, messageLiteral, min, max);
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

	public boolean hasMin() {
		return min != null;
	}

	public @Nullable Double getMin() {
		return min;
	}

	public boolean hasMax() {
		return max != null;
	}

	public @Nullable Double getMax() {
		return max;
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
