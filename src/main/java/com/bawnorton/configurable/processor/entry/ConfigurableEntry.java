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
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ConfigurableEntry {
    private final Element annotatedElement;
    private final String name;
    private final String group;
    private final String comment;
    private final boolean sync;
    private final @Nullable MethodReference onSetMethod;
    private final @NotNull ConfigurableValidator validator;

    public ConfigurableEntry(Element annotatedElement, String name, String group, String comment, boolean sync, @Nullable MethodReference onSetMethod, @NotNull ConfigurableValidator validator) {
        this.annotatedElement = annotatedElement;
        this.name = name;
        this.group = group;
        this.comment = comment;
        this.sync = sync;
        this.onSetMethod = onSetMethod;
        this.validator = validator;
    }

    public static ConfigurableEntry createEntry(String name, ConfigurableElement configurableElement, ProcessingEnvironment processingEnv) {
        String group = configurableElement.getGroup();
        boolean sync = configurableElement.doesSync();
        String comment = configurableElement.getComment();
        comment = comment == null ? null : comment.strip();
        String fullName = group == null || group.isEmpty() ? name : "%s.%s".formatted(group, name);

        Element annotatedElement = configurableElement.getAnnotatedElement();
        Element enclosingClass = annotatedElement.getEnclosingElement();
        String onSetMethod = configurableElement.getOnSetReference();
        MethodReference onSetReference = null;
        if (!onSetMethod.isEmpty()) {
            Either<MethodReference, String> maybeReference = MethodHelper.getReference(enclosingClass, onSetMethod, processingEnv);
            onSetReference = MethodHelper.validateMethodReference(maybeReference, processingEnv, onSetMethod, message -> {
                AnnotationMirror onSetMirror = configurableElement.getConfigurableMirror();
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        message,
                        annotatedElement,
                        onSetMirror,
                        AnnotationHelper.getAnnotationValue(onSetMirror, "value")
                );
            });
            if (onSetReference == null) return null;

            TypeMirror actualReturnType = onSetReference.returnType();
            TypeMirror expectedReturnType = processingEnv.getTypeUtils().getNoType(TypeKind.VOID);
            if (!processingEnv.getTypeUtils().isAssignable(actualReturnType, expectedReturnType)) {
                processingEnv.getMessager().printError("Return type of method '%s' must be void, but found '%s'".formatted(onSetMethod, actualReturnType), onSetReference.methodElement());
                return null;
            }

            List<TypeMirror> parameterTypes = onSetReference.parameterTypes();
            if (parameterTypes.size() != 2) {
                VariableElement problemElement = onSetReference.methodElement().getParameters().get(1);
                processingEnv.getMessager().printError("Method '%s' must have exactly two parameters, but found %d".formatted(onSetMethod, parameterTypes.size()), problemElement);
                return null;
            }

            TypeMirror firstParamterType = parameterTypes.getFirst();
            TypeMirror expectedParameterType = annotatedElement.asType();
            if (expectedParameterType.getKind().isPrimitive()) {
                expectedParameterType = processingEnv.getTypeUtils().boxedClass((PrimitiveType) expectedParameterType).asType();
            }
            if (!processingEnv.getTypeUtils().isSameType(firstParamterType, expectedParameterType)) {
                VariableElement problemElement = onSetReference.methodElement().getParameters().getFirst();
                processingEnv.getMessager().printError("Method '%s''s first parameter must be of type '%s', but found '%s'".formatted(onSetMethod, expectedParameterType, firstParamterType), problemElement);
                return null;
            }
            TypeMirror secondParameterType = parameterTypes.get(1);
            if (!processingEnv.getTypeUtils().isSameType(secondParameterType, processingEnv.getTypeUtils().getPrimitiveType(TypeKind.BOOLEAN))) {
                VariableElement problemElement = onSetReference.methodElement().getParameters().get(1);
                processingEnv.getMessager().printError("Method '%s''s second parameter must be of type 'boolean', but found '%s'".formatted(onSetMethod, secondParameterType), problemElement);
                return null;
            }
        }

        ConfigurableValidator validator = ConfigurableValidator.fromConfigurableElement(fullName, configurableElement, processingEnv);
        if (validator == null) return null;

        return new ConfigurableEntry(configurableElement.getAnnotatedElement(), name, group, comment, sync, onSetReference, validator);
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return annotatedElement.getSimpleName().toString();
    }

    public String getReferenceName() {
        String group = getGroup();
        String name = getFieldName();
        if (group.isEmpty()) {
            return name;
        } else {
            return "%s_%s".formatted(group.replaceAll("\\.", "_"), name);
        }
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

    public boolean hasOnSetMethod() {
        return onSetMethod != null;
    }

    public @Nullable MethodReference getOnSetMethod() {
        return onSetMethod;
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
