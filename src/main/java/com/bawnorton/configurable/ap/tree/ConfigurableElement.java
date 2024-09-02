package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ControllerType;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

public record ConfigurableElement(Element element, Configurable annotation, List<ConfigurableElement> children) {
    public String getKey() {
        String key = annotation.value();
        if(key.isEmpty()) {
            key = element.getSimpleName().toString();
        }
        return key;
    }

    public boolean childless() {
        return children.isEmpty();
    }

    public TypeMirror getType() {
        return element.asType();
    }

    public String getTypeName() {
        return getType().toString();
    }

    public String getBoxedType(Types types) {
        TypeMirror mirror = getType();
        if(!mirror.getKind().isPrimitive()) {
            return mirror.toString();
        } else {
            PrimitiveType primitiveType = types.getPrimitiveType(mirror.getKind());
            return types.boxedClass(primitiveType).getSimpleName().toString();
        }
    }

    public TypeKind getTypeKind() {
        return getType().getKind();
    }

    public String getFullyQualifiedTypeName(Types types) {
        return getQualifiedTypeName(types, element);
    }

    public String getOwnerName() {
        return element.getEnclosingElement().getSimpleName().toString();
    }

    public String getFullyQualifiedOwnerName(Types types) {
        Element owner = element.getEnclosingElement();
        return getQualifiedTypeName(types, owner);
    }

    private String getQualifiedTypeName(Types types, Element owner) {
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

    public String getElementConfigName() {
        return getElementName() + "Config";
    }

    /**
     * @return A list of parents, children and their children's children, etc
     */
    public List<ConfigurableElement> getAllChildren() {
        if(childless()) return new ArrayList<>();

        List<ConfigurableElement> children = new ArrayList<>();
        for(ConfigurableElement child : this.children) {
            children.add(child);
            children.addAll(child.getAllChildren());
        }
        return children;
    }

    /**
     * @return A list of parents who's children do not have any children themselves
     */
    public List<ConfigurableElement> disolveMultiLevelParents() {
        List<ConfigurableElement> result = new ArrayList<>();

        boolean allChildrenAreChildless = true;
        for (ConfigurableElement child : this.children) {
            if (!child.childless()) {
                allChildrenAreChildless = false;
                break;
            }
        }

        if (allChildrenAreChildless) {
            result.add(this);  // Add the current element if all its children are childless.
        } else {
            for (ConfigurableElement child : this.children) {
                result.addAll(child.disolveMultiLevelParents());  // Recurse for each child.
            }
        }

        return result;
    }

    public String getCategory() {
        String tab = annotation.category();
        if(tab.isEmpty()) {
            Element enclosingElement = element.getEnclosingElement();
            while(!(enclosingElement instanceof PackageElement)) {
                enclosingElement = enclosingElement.getEnclosingElement();
                if(enclosingElement == null) {
                    throw new IllegalStateException("Cannot find enclosing package of: %s".formatted(element));
                }
            }
            tab = enclosingElement.getSimpleName().toString();
        }
        return tab;
    }

    public ControllerType getControllerType() {
        return annotation.controller();
    }
}
