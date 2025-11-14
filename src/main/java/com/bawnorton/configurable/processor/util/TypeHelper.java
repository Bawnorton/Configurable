package com.bawnorton.configurable.processor.util;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TypeHelper {
	public static boolean isNumeric(Element element, ProcessingEnvironment processingEnv) {
		Types typeUtils = processingEnv.getTypeUtils();
		Elements elementUtils = processingEnv.getElementUtils();
		TypeMirror type = element.asType();
		TypeMirror boxed = type.getKind().isPrimitive() ? typeUtils.boxedClass((PrimitiveType) type).asType() : type;
		TypeMirror numberType = elementUtils.getTypeElement(Number.class.getCanonicalName()).asType();
		return typeUtils.isAssignable(boxed, numberType);
	}
}
