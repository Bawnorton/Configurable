package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.Configurable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import org.spongepowered.asm.mixin.Mixin;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ConfigurableTree {
    private final Messager messager;
    private final List<ConfigurableElement> roots;

    public ConfigurableTree(Messager messager, Set<? extends Element> elements) {
        this.messager = messager;
        this.roots = constructRoots(elements);
        applyOverrides(roots);
    }

    private void applyOverrides(List<ConfigurableElement> roots) {
        roots.forEach(this::applyOverrides);
    }

    private void applyOverrides(ConfigurableElement root) {
        ConfigurableHolder parentHolder = root.annotationHolder();
        for(ConfigurableElement child : root.children()) {
            ConfigurableHolder childHolder = child.annotationHolder();
            ConfigurableOverrides.create(parentHolder, childHolder);

            if(!child.children().isEmpty()) {
                applyOverrides(child);
            }
        }
    }

    public List<ConfigurableElement> getRoots() {
        return roots;
    }

    private List<ConfigurableElement> constructRoots(Set<? extends Element> elements) {
        Set<ConfigurableElement> rootElements = new HashSet<>();

        Set<Element> orphanedClasses = new HashSet<>();
        Set<Element> orphanedFields = new HashSet<>();
        for (Element element : elements) {
            if (!element.getKind().isClass()) {
                orphanedFields.add(element);
                continue;
            }

            Element enclosing = element.getEnclosingElement();
            if(enclosing.getKind().isClass()) {
                orphanedClasses.add(element);
                continue;
            }

            ConfigurableElement configurableElement = createConfigurableElement(element);
            rootElements.add(configurableElement);
        }

        addOrphaned(rootElements, orphanedClasses);
        addOrphaned(rootElements, orphanedFields);

        List<ConfigurableElement> sorted = new ArrayList<>(rootElements);
        sorted.sort(Comparator.comparing(ConfigurableElement::getKey));
        return sorted;
    }

    private void addOrphaned(Set<ConfigurableElement> rootElements, Set<Element> orphaned) {
        while(!orphaned.isEmpty()) {
            Set<Element> visited = rootElements.stream()
                    .flatMap(element -> {
                        List<ConfigurableElement> relevant = element.getAllChildren();
                        relevant.add(element);
                        return relevant.stream();
                    })
                    .map(ConfigurableElement::element)
                    .collect(Collectors.toSet());
            orphaned.removeAll(visited);

            for(Element orphan : orphaned) {
                ConfigurableElement configurableElement = createConfigurableElement(orphan);
                rootElements.add(configurableElement);
            }
        }
    }

    private ConfigurableElement createConfigurableElement(Element element) {
        Configurable annotation = element.getAnnotation(Configurable.class);
        ConfigurableHolder holder = new ConfigurableHolder(
                annotation,
                getAnnotationMirror(element, Configurable.class.getCanonicalName())
        );
        if (annotation == null) {
            messager.printError("Element \"%s\" does not have Configurable annotation".formatted(element.getSimpleName()), element);
            throw new RuntimeException();
        }

        Set<Modifier> modifiers = element.getModifiers();
        if(element.getKind().isField()) {
            if(modifiers.contains(Modifier.FINAL)) {
                messager.printError("Configurable field \"%s\" cannot be final".formatted(element.getSimpleName()), element);
                throw new RuntimeException();
            } else if (!modifiers.contains(Modifier.STATIC)) {
                messager.printError("Configurable field \"%s\" must be static".formatted(element.getSimpleName()), element);
                throw new RuntimeException();
            } else if(annotation.yacl().collapsed()) {
                messager.printError("Configurable field \"%s\" cannot be collapsed, only classes allowed".formatted(element.getSimpleName()), element);
                throw new RuntimeException();
            }
        } else if (element.getKind().isClass()) {
            if(modifiers.contains(Modifier.PRIVATE)) {
                messager.printError("Configurable class \"%s\" cannot be private".formatted(element.getSimpleName()), element);
                throw new RuntimeException();
            }
        }

        List<ConfigurableElement> children = element.getEnclosedElements()
                .stream()
                .filter(e -> e.getAnnotation(Configurable.class) != null)
                .map(this::createConfigurableElement)
                .sorted(Comparator.comparing(ConfigurableElement::getKey))
                .toList();

        if (element.getKind().isClass() && children.isEmpty()) {
            messager.printError("Configurable class \"%s\" must have at least one Configurable field or class".formatted(element.getSimpleName()), element);
            throw new RuntimeException();
        }

        Element topMost = getTopMostClass(element);
        if(topMost != null) {
            Mixin mixin = topMost.getAnnotation(Mixin.class);
            if(mixin != null) {
                messager.printError("Configurable element \"%s\" must be outside a mixin class".formatted(element.getSimpleName()), element);
                throw new RuntimeException();
            }
        }

        return new ConfigurableElement(element, holder, children);
    }

    private AnnotationMirror getAnnotationMirror(Element element, String annotation) {
        for(AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if(annotationMirror.getAnnotationType().toString().equals(annotation)) {
                return annotationMirror;
            }
        }
        throw new IllegalStateException("No annotation mirror found for %s".formatted(element));
    }

    private Element getTopMostClass(Element element) {
        Element enclosing = element.getEnclosingElement();
        while(enclosing.asType().getKind() != TypeKind.PACKAGE) {
            element = enclosing;
            enclosing = enclosing.getEnclosingElement();
            if(enclosing == null) break;
        }
        return element;
    }
}
