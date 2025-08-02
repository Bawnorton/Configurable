package com.bawnorton.configurable.processor.element;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;
import com.bawnorton.configurable.processor.ConfigurableSettings;
import com.bawnorton.configurable.processor.util.AnnotationHelper;
import com.bawnorton.configurable.processor.util.TypeHelper;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.Set;

public class ConfigurableElement {
    private final Element annotatedElement;
    private final String elementName;
    private final boolean isNumeric;
    private final String defaultValue;
    private final String comment;
    private final Configurable configurable;
    private final AnnotationMirror validatorMirror;

    public ConfigurableElement(Element annotatedElement, String elementName, boolean isNumeric, String defaultValue, String comment, Configurable configurable, AnnotationMirror validatorMirror) {
        this.annotatedElement = annotatedElement;
        this.elementName = elementName;
        this.isNumeric = isNumeric;
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.configurable = configurable;
        this.validatorMirror = validatorMirror;
    }

    public static ConfigurableElement fromElement(Element element, ProcessingEnvironment processingEnv) {
        Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC)) {
            processingEnv.getMessager().printError("Configurable fields must be public", element);
            return null;
        }
        if (!modifiers.contains(Modifier.STATIC)) {
            processingEnv.getMessager().printError("Configurable fields must be static", element);
            return null;
        }
        if (modifiers.contains(Modifier.FINAL)) {
            processingEnv.getMessager().printError("Configurable fields cannot be final", element);
            return null;
        }
        String elementName = element.getSimpleName().toString();
        boolean isNumeric = TypeHelper.isNumeric(element, processingEnv);
        AnnotationMirror configurableMirror = AnnotationHelper.getAnnotationMirror(element, Configurable.class);
        AnnotationMirror validatorMirror = AnnotationHelper.getNestedAnnotationMirror(configurableMirror, Validator.class);

        String defaultValue = null;
        Trees trees = Trees.instance(processingEnv);
        TreePath treePath = trees.getPath(element);
        if (treePath != null && treePath.getLeaf() instanceof VariableTree variableTree) {
            ExpressionTree initializer = variableTree.getInitializer();
            if (initializer != null) {
                defaultValue = initializer.toString();
            }
        }

        String comment = processingEnv.getElementUtils().getDocComment(element);
        Configurable configurable = element.getAnnotation(Configurable.class);
        return new ConfigurableElement(
                element,
                elementName,
                isNumeric,
                defaultValue,
                comment,
                configurable,
                validatorMirror
        );
    }

    public Element getAnnotatedElement() {
        return annotatedElement;
    }

    public String getElementName() {
        return elementName;
    }

    public String getConfigurableName(ConfigurableSettings settings) {
        if(configurable.value().isEmpty()) {
            return settings.namingPolicy().format(elementName);
        }
        return settings.namingPolicy().format(configurable.value());
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getGroup() {
        return configurable.group();
    }

    public String getComment() {
        return comment;
    }

    public boolean doesSync() {
        return configurable.sync();
    }

    public AnnotationMirror getValidatorMirror() {
        return validatorMirror;
    }

    public String getValidatorReference() {
        return configurable.validator().value();
    }

    public boolean doesFallback() {
        return configurable.validator().fallback();
    }

    public String getFailureMessageReference() {
        return configurable.validator().message();
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public boolean isMinSet() {
        return !AnnotationHelper.isDefaultValue(validatorMirror, "min");
    }

    public Double getMin() {
        return configurable.validator().min();
    }

    public boolean isMaxSet() {
        return !AnnotationHelper.isDefaultValue(validatorMirror, "max");
    }

    public Double getMax() {
        return configurable.validator().max();
    }
}
