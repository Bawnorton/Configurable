package com.bawnorton.configurable.io;

import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    public static Object interpret(JsonElement element, GenericType genericHolder) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        Class<?> expectedType = genericHolder.type();
        if (genericHolder.isRaw()) {
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
                    throw new IllegalArgumentException("Expected a single character for type char, but got: %s".formatted(str));
                }
                return str.charAt(0);
            } else if (expectedType.isEnum()) {
                //noinspection unchecked,rawtypes
                return Enum.valueOf((Class<Enum>) expectedType, element.getAsString());
            } else if (expectedType.isArray()) {
                if (!element.isJsonArray()) {
                    throw new IllegalArgumentException("Expected a JSON array for type array, but got: %s".formatted(element));
                }
                JsonArray jsonArray = element.getAsJsonArray();
                int size = jsonArray.size();
                Class<?> componentType = expectedType.getComponentType();
                Object array = Array.newInstance(componentType, size);

                for (int i = 0; i < size; i++) {
                    JsonElement jsonElement = jsonArray.get(i);
                    Object value = interpret(jsonElement, new GenericType(componentType));
                    Array.set(array, i, value);
                }
                return array;
            } else {
                throw new IllegalArgumentException("Unsupported type: %s".formatted(expectedType.getName()));
            }
        } else {
            GenericType[] genericTypes = genericHolder.genericTypes();
            if (genericTypes.length == 1) {
                GenericType genericType = genericTypes[0];
                if (List.class.isAssignableFrom(expectedType)) {
                    if (!element.isJsonArray()) {
                        throw new IllegalArgumentException("Expected a JSON array for type List, but got: %s".formatted(element));
                    }
                    return element.getAsJsonArray()
                                  .asList()
                                  .stream()
                                  .map(item -> interpret(item, genericType))
                                  .toList();
                } else {
                    throw new IllegalArgumentException("Unsupported generic type: %s".formatted(expectedType.getName()));
                }
            } else {
                throw new IllegalArgumentException("Generic types with more than one parameter are not supported.");
            }
        }
    }

    public static Object interpret(CommentedConfig toml, String path, GenericType genericHolder) {
        if (toml == null || !toml.contains(path)) {
            return null;
        }
        Class<?> expectedType = genericHolder.type();
        if (genericHolder.isRaw()) {
            if (expectedType == String.class) {
                return toml.<String>get(path);
            } else if (expectedType == Integer.class || expectedType == int.class) {
                return toml.<Number>get(path).intValue();
            } else if (expectedType == Long.class || expectedType == long.class) {
                return toml.getLong(path);
            } else if (expectedType == Double.class || expectedType == double.class) {
                return toml.<Number>get(path).doubleValue();
            } else if (expectedType == Boolean.class || expectedType == boolean.class) {
                return toml.<Boolean>get(path);
            } else if (expectedType == Float.class || expectedType == float.class) {
                return toml.<Number>get(path).floatValue();
            } else if (expectedType == Byte.class || expectedType == byte.class) {
                return toml.getByte(path);
            } else if (expectedType == Short.class || expectedType == short.class) {
                return toml.getShort(path);
            } else if (expectedType == Character.class || expectedType == char.class) {
                return toml.<String>get(path).charAt(0);
            } else if (expectedType.isEnum()) {
                //noinspection unchecked,rawtypes
                return toml.getEnum(path, (Class<Enum>) expectedType);
            } else if (expectedType.isArray()) {
                List<?> list = toml.get(path);
                if (list == null) return null;

                Class<?> componentType = expectedType.getComponentType();
                int size = list.size();
                Object array = Array.newInstance(componentType, size);
                for (int i = 0; i < size; i++) {
                    Object item = list.get(i);
                    Object value = interpret(item, new GenericType(componentType));
                    Array.set(array, i, value);
                }
                return array;
            } else {
                throw new IllegalArgumentException("Unsupported type: %s".formatted(expectedType.getName()));
            }
        } else {
            GenericType[] genericTypes = genericHolder.genericTypes();
            if (genericTypes.length == 1) {
                GenericType genericType = genericTypes[0];
                if (List.class.isAssignableFrom(expectedType)) {
                    List<?> list = toml.get(path);
                    if (list == null) return null;

                    List<Object> interpretedList = new ArrayList<>(list.size());
                    for (Object item : list) {
                        interpretedList.add(interpret(item, genericType));
                    }
                    return interpretedList;
                } else {
                    throw new IllegalArgumentException("Unsupported generic type: %s".formatted(expectedType.getName()));
                }
            } else {
                throw new IllegalArgumentException("Generic types with more than one parameter are not supported.");
            }
        }
    }

    private static Object interpret(Object item, GenericType genericHolder) {
        if (item == null) return null;

        Class<?> expectedType = genericHolder.type();
        if (genericHolder.isRaw()) {
            if (expectedType == String.class) {
                return item.toString();
            } else if (expectedType == Integer.class || expectedType == int.class) {
                return ((Number) item).intValue();
            } else if (expectedType == Long.class || expectedType == long.class) {
                return ((Number) item).longValue();
            } else if (expectedType == Double.class || expectedType == double.class) {
                return ((Number) item).doubleValue();
            } else if (expectedType == Boolean.class || expectedType == boolean.class) {
                return item;
            } else if (expectedType == Float.class || expectedType == float.class) {
                return ((Number) item).floatValue();
            } else if (expectedType == Byte.class || expectedType == byte.class) {
                return ((Number) item).byteValue();
            } else if (expectedType == Short.class || expectedType == short.class) {
                return ((Number) item).shortValue();
            } else if (expectedType == Character.class || expectedType == char.class) {
                String str = item.toString();
                if (str.length() != 1) {
                    throw new IllegalArgumentException("Expected a single character for type char, but got: %s".formatted(str));
                }
                return str.charAt(0);
            } else if (expectedType.isEnum()) {
                //noinspection unchecked,rawtypes
                return Enum.valueOf((Class<Enum>) expectedType, item.toString());
            } else if (expectedType.isArray()) {
                if (!(item instanceof List<?> list)) {
                    throw new IllegalArgumentException("Expected a List for type array, but got: %s".formatted(item));
                }
                Class<?> componentType = expectedType.getComponentType();
                int size = list.size();
                Object array = Array.newInstance(componentType, size);
                for (int i = 0; i < size; i++) {
                    Object value = interpret(list.get(i), new GenericType(componentType));
                    Array.set(array, i, value);
                }
                return array;
            } else {
                throw new IllegalArgumentException("Unsupported type: %s".formatted(expectedType.getName()));
            }
        } else {
            GenericType[] genericTypes = genericHolder.genericTypes();
            if (genericTypes.length == 1) {
                GenericType genericType = genericTypes[0];
                if (List.class.isAssignableFrom(expectedType)) {
                    if (!(item instanceof List<?> list)) {
                        throw new IllegalArgumentException("Expected a List for type List, but got: %s".formatted(item));
                    }
                    List<Object> interpretedList = new ArrayList<>(list.size());
                    for (Object listItem : list) {
                        interpretedList.add(interpret(listItem, genericType));
                    }
                    return interpretedList;
                } else {
                    throw new IllegalArgumentException("Unsupported generic type: %s".formatted(expectedType.getName()));
                }
            } else {
                throw new IllegalArgumentException("Generic types with more than one parameter are not supported.");
            }
        }
    }

    /**
     * TOML doesn't support array types directly so we need to convert them to lists.
     * Additionally, lists cant be of array types (List<String[]>), so we need to check for that.
     * TOML also doesn't support characters, so we convert them to strings.
     */
    public static Object safeForToml(Object value) {
        if (value == null) return null;

        Class<?> type = value.getClass();
        if (type.isArray()) {
            int length = Array.getLength(value);
            List<Object> asList = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(value, i);
                asList.add(safeForToml(item));
            }
            return asList;
        } else if (value instanceof List<?> list) {
            List<Object> safeList = new ArrayList<>(list.size());
            for (Object item : list) {
                safeList.add(safeForToml(item));
            }
            return safeList;
        } else if (type == Character.class) {
            return value.toString();
        } else {
            return value;
        }
    }
}
