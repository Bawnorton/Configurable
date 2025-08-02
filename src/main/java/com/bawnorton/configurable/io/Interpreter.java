package com.bawnorton.configurable.io;

import com.bawnorton.configurable.util.GenericHolder;
import com.google.gson.JsonElement;
import com.moandjiezana.toml.Toml;
import java.util.List;

public class Interpreter {
    public static Object interpret(JsonElement element, GenericHolder genericHolder) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        Class<?> expectedType = genericHolder.type();
        if(!genericHolder.isParameterized()) {
            if (expectedType == String.class) {
                return element.getAsString();
            } else if (expectedType == Integer.class || expectedType == int.class) {
                return element.getAsInt();
            } else if (expectedType == Long.class || expectedType == long.class) {
                return element.getAsLong();
            } else if (expectedType == Double.class || expectedType == double.class) {
                return element.getAsDouble();
            } else if (expectedType == Boolean.class || expectedType == boolean.class) {
                return element.getAsBoolean();
            } else if (expectedType == Float.class || expectedType == float.class) {
                return element.getAsFloat();
            } else if (expectedType == Byte.class || expectedType == byte.class) {
                return (byte) element.getAsInt();
            } else if (expectedType == Short.class || expectedType == short.class) {
                return (short) element.getAsInt();
            } else if (expectedType == Character.class || expectedType == char.class) {
                String str = element.getAsString();
                if (str.length() != 1) {
                    throw new IllegalArgumentException("Expected a single character for type char, but got: " + str);
                }
                return str.charAt(0);
            } else if (expectedType.isEnum()) {
                //noinspection unchecked,rawtypes
                return Enum.valueOf((Class<Enum>) expectedType, element.getAsString());
            } else {
                throw new IllegalArgumentException("Unsupported type: " + expectedType.getName());
            }
        } else {
            Class<?>[] genericTypes = genericHolder.genericTypes();
            if(genericTypes.length == 1) {
                Class<?> genericType = genericTypes[0];
                if (List.class.isAssignableFrom(expectedType)) {
                    if (!element.isJsonArray()) {
                        throw new IllegalArgumentException("Expected a JSON array for type List, but got: " + element);
                    }
                    return element.getAsJsonArray()
                                  .asList()
                                  .stream()
                                  .map(item -> interpret(item, new GenericHolder(genericType)))
                                  .toList();
                } else if (expectedType.isArray()) {
                    if (!element.isJsonArray()) {
                        throw new IllegalArgumentException("Expected a JSON array for type array, but got: " + element);
                    }
                    return element.getAsJsonArray()
                                  .asList()
                                  .stream()
                                  .map(item -> interpret(item, new GenericHolder(genericType)))
                                  .toArray();
                } else {
                    throw new IllegalArgumentException("Unsupported generic type: " + expectedType.getName());
                }
            } else {
                throw new IllegalArgumentException("Generic types with more than one parameter are not supported.");
            }
        }
    }

    public static Object interpret(Toml toml, String coordinate, GenericHolder genericHolder) {
        if (toml == null || !toml.contains(coordinate)) {
            return null;
        }
        Class<?> expectedType = genericHolder.type();
        if (!genericHolder.isParameterized()) {

            if (expectedType == String.class) {
                return toml.getString(coordinate);
            } else if (expectedType == Integer.class || expectedType == int.class) {
                Long value = toml.getLong(coordinate);
                if (value == null) {
                    return null;
                }
                return value.intValue();
            } else if (expectedType == Long.class || expectedType == long.class) {
                return toml.getLong(coordinate);
            } else if (expectedType == Double.class || expectedType == double.class) {
                return toml.getDouble(coordinate);
            } else if (expectedType == Boolean.class || expectedType == boolean.class) {
                return toml.getBoolean(coordinate);
            } else if (expectedType == Float.class || expectedType == float.class) {
                return toml.getDouble(coordinate).floatValue();
            } else if (expectedType == Byte.class || expectedType == byte.class) {
                Long value = toml.getLong(coordinate);
                if (value == null) {
                    return null;
                }
                return value.byteValue();
            } else if (expectedType == Short.class || expectedType == short.class) {
                Long value = toml.getLong(coordinate);
                if (value == null) {
                    return null;
                }
                return value.shortValue();
            } else if (expectedType == Character.class || expectedType == char.class) {
                String str = toml.getString(coordinate);
                if (str == null || str.length() != 1) {
                    throw new IllegalArgumentException("Expected a single character for type char, but got: " + str);
                }
                return str.charAt(0);
            } else if (expectedType.isEnum()) {
                String value = toml.getString(coordinate);
                if (value == null) {
                    return null;
                }
                //noinspection unchecked,rawtypes
                return Enum.valueOf((Class<Enum>) expectedType, value);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + expectedType.getName());
            }
        } else {
            Class<?>[] genericTypes = genericHolder.genericTypes();
            if (genericTypes.length == 1) {
                Class<?> genericType = genericTypes[0];
                if (List.class.isAssignableFrom(expectedType) || expectedType.isArray()) {
                    List<?> list = toml.getList(coordinate);
                    if (list == null) return null;

                    for (Object item : list) {
                        if (item == null) continue;

                        if (!genericType.isInstance(item)) {
                            throw new IllegalArgumentException("Expected items of type " + genericType.getName() + " but found " + item.getClass().getName());
                        }
                    }
                    if (expectedType.isArray()) return list.toArray();

                    return list;
                } else {
                    throw new IllegalArgumentException("Unsupported generic type: " + expectedType.getName());
                }
            } else {
                throw new IllegalArgumentException("Generic types with more than one parameter are not supported.");
            }
        }
    }
}
