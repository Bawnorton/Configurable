package com.bawnorton.configurable.util;

import org.jetbrains.annotations.NotNull;

public record GenericType(Class<?> type, GenericType... genericTypes) {

	public static GenericType fromString(String typeString) {
		String[] parts = typeString.split("<", 2);
		Class<?> type;
		try {
			type = Class.forName(parts[0].trim());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Class not found: " + parts[0], e);
		}

		if (parts.length == 1) {
			return new GenericType(type);
		} else {
			String genericPart = parts[1].replace(">", "").trim();
			String[] genericTypes = genericPart.split(",\\s*");
			GenericType[] genericTypeArray = new GenericType[genericTypes.length];
			for (int i = 0; i < genericTypes.length; i++) {
				genericTypeArray[i] = fromString(genericTypes[i]);
			}
			return new GenericType(type, genericTypeArray);
		}
	}

	public boolean isRaw() {
		return genericTypes == null || genericTypes.length == 0;
	}

	@Override
	public @NotNull String toString() {
		if (isRaw()) {
			return type.getCanonicalName();
		} else {
			StringBuilder sb = new StringBuilder(type.getCanonicalName());
			sb.append('<');
			for (int i = 0; i < genericTypes.length; i++) {
				sb.append(genericTypes[i].toString());
				if (i < genericTypes.length - 1) {
					sb.append(", ");
				}
			}
			sb.append('>');
			return sb.toString();
		}
	}
}
