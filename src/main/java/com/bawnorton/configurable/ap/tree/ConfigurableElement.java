package com.bawnorton.configurable.ap.tree;

import com.bawnorton.configurable.ControllerType;
import com.bawnorton.configurable.Image;
import com.bawnorton.configurable.ap.yacl.*;
import com.bawnorton.configurable.ap.yacl.YaclDescriptionImage;
import com.bawnorton.configurable.ap.yacl.YaclDescriptionText;
import com.bawnorton.configurable.ap.yacl.YaclSimpleDescriptionText;
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
import java.util.function.BiFunction;

public record ConfigurableElement(Element element, ConfigurableHolder annotationHolder, List<ConfigurableElement> children) {
    public String getKey() {
        String key = annotationHolder.value();
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
        String qualified = getType().toString();
        return qualified.substring(qualified.lastIndexOf('.') + 1);
    }

    public String getBoxedType(Types types) {
        TypeMirror mirror = getType();
        String boxed;
        if(!mirror.getKind().isPrimitive()) {
            boxed = mirror.toString();
        } else {
            PrimitiveType primitiveType = types.getPrimitiveType(mirror.getKind());
            boxed = types.boxedClass(primitiveType).getSimpleName().toString();
        }
        return boxed.substring(boxed.lastIndexOf('.') + 1);
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
        String tab = annotationHolder.category();
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
        return annotationHolder.controller();
    }

    public Image image() {
        return annotationHolder.image();
    }

    public boolean hasImage() {
        Image image = image();
        return !(image.value().isEmpty() && image.custom().isEmpty());
    }

    public YaclValueFormatter getFormatter(Types types) {
        String formatter = annotationHolder.formatter();
        if(formatter.isEmpty()) {
            return null;
        }
        return getMethodBased(types, YaclValueFormatter::new, formatter);
    }

    public YaclListeners getListeners(Types types) {
        String[] listeners = annotationHolder.listener();
        YaclListeners yaclListeners = new YaclListeners();
        for(String listener : listeners) {
            yaclListeners.addListener(getMethodBased(types, YaclListener::new, listener));
        }
        return yaclListeners;
    }

    public YaclElement getDescriptionText(Types types, String configName, YaclDescriptionText.Factory<?> factory) {
        String descriptioner = annotationHolder.descriptioner();
        if(descriptioner.isEmpty()) {
            return new YaclSimpleDescriptionText(configName, getKey());
        }
        return getMethodBased(types, factory::create, descriptioner);
    }

    public YaclDescriptionImage getOptionDescriptionImage(Types types) {
        if(annotationHolder.inheritedImage()) {
            return getOptionGroupDescriptionImage(types);
        }
        return getImage(types, YaclOptionDescriptionImage::new);
    }

    public YaclDescriptionImage getOptionGroupDescriptionImage(Types types) {
        return getImage(types, YaclOptionGroupDescriptionImage::new);
    }

    private  <T extends YaclDescriptionImage> T getImage(Types types, YaclDescriptionImage.Factory<T> factory) {
        if(!hasImage()) return null;

        Image image = image();
        String custom = image.custom();
        if(custom.isEmpty()) {
            return factory.create(image, null, null);
        }
        return getMethodBased(types, (owner, method) -> factory.create(image, owner, method), custom);
    }

    private <T extends YaclElement> T getMethodBased(Types types, BiFunction<String, String, T> ctor, String methodName) {
        if(methodName.contains("#")) {
            String[] parts = methodName.split("#");
            String owner = parts[0];
            String method = parts[1];
            return ctor.apply(owner, method);
        } else {
            String owner;
            if(element.getKind().isField()) {
                owner = getFullyQualifiedOwnerName(types);
            } else if (element.getKind().isClass()) {
                owner = getFullyQualifiedTypeName(types);
            } else {
                throw new IllegalStateException("Could not determine owner for \"%s\"".formatted(methodName));
            }
            return ctor.apply(owner, methodName);
        }
    }
}
