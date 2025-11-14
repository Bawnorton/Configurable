package com.bawnorton.configurable.processor.util;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record MethodReference(ExecutableElement methodElement) {
	public TypeMirror enclosingClass() {
		return methodElement.getEnclosingElement().asType();
	}

	public String getName() {
		return methodElement.getSimpleName().toString();
	}

	private Set<Modifier> modifiers() {
		return methodElement.getModifiers();
	}

	public boolean isPublic() {
		return modifiers().contains(Modifier.PUBLIC);
	}

	public boolean isStatic() {
		return modifiers().contains(Modifier.STATIC);
	}

	public TypeMirror returnType() {
		return methodElement.getReturnType();
	}

	public List<TypeMirror> parameterTypes() {
		List<? extends VariableElement> parameters = methodElement.getParameters();
		List<TypeMirror> parameterTypes = new ArrayList<>(parameters.size());
		for (VariableElement parameter : parameters) {
			parameterTypes.add(parameter.asType());
		}
		return parameterTypes;
	}
}
