package com.bawnorton.configurable.ap.util;

import com.bawnorton.configurable.Configurable;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

public record ConfigurableElement(Element element, Configurable annotation, List<ConfigurableElement> children) {
    public String getKey() {
        String key = annotation.key();
        if(key.isEmpty()) {
            key = element.getSimpleName().toString();
        }
        return key;
    }

    public boolean childless() {
        return children.isEmpty();
    }

    public String getType() {
        return element.asType().toString();
    }

    public String getTypeForGeneric(Types types) {
        TypeMirror mirror = element.asType();
        if(!mirror.getKind().isPrimitive()) {
            return mirror.toString();
        } else {
            PrimitiveType primitiveType = types.getPrimitiveType(mirror.getKind());
            return types.boxedClass(primitiveType).getSimpleName().toString();
        }
    }

    public String getFullyQualifiedType(Types types) {
        return getQualifiedType(types, element);
    }

    public String getOwner() {
        return element.getEnclosingElement().getSimpleName().toString();
    }

    public String getFullyQualifiedOwner(Types types) {
        Element owner = element.getEnclosingElement();
        return getQualifiedType(types, owner);
    }

    private String getQualifiedType(Types types, Element owner) {
        TypeMirror mirror = owner.asType();
        if(mirror.getKind().isPrimitive()) {
            PrimitiveType primitiveType = types.getPrimitiveType(mirror.getKind());
            return types.boxedClass(primitiveType).getQualifiedName().toString();
        } else if (mirror instanceof DeclaredType declaredType) {
            TypeElement typeElement = (TypeElement) declaredType.asElement();
            return typeElement.getQualifiedName().toString();
        }
        return mirror.toString();
    }

    public String getElementName() {
        return element.getSimpleName().toString();
    }

    public String getNestedName() {
        return getElementName() + "Config";
    }

    public List<ConfigurableElement> getAllChildren() {
        if(childless()) return new ArrayList<>();

        List<ConfigurableElement> children = new ArrayList<>();
        for(ConfigurableElement child : this.children) {
            children.add(child);
            children.addAll(child.getAllChildren());
        }
        return children;
    }
}
