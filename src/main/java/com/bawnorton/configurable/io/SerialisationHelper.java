package com.bawnorton.configurable.io;

import com.bawnorton.configurable.io.typed.TypedHandler;
import com.bawnorton.configurable.io.typed.TypedHandlerCollection;
import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SerialisationHelper {
	private static final TypedHandlerCollection TYPED_HANDLERS;
	private static final TypedHandler<Enum<?>> ENUM_HANDLER;
	private static final TypedHandler<Object> ARRAY_HANDLER;
	private static final TypedHandler<List<?>> LIST_HANDLER;

	static {
		TYPED_HANDLERS = new TypedHandlerCollection();
		TYPED_HANDLERS.register(String.class, null,
				TypedReader.<String>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsString))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::get))
						.object(TypedReader.ObjectReader.contextless(Object::toString))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.STRING_UTF8::decode)),
				TypedWriter.<String>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.STRING_UTF8::encode))
		);
		TYPED_HANDLERS.register(Integer.class, int.class,
				TypedReader.<Integer>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsInt))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::getInt))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).intValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.VAR_INT::decode)),
				TypedWriter.<Integer>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.VAR_INT::encode))
		);
		TYPED_HANDLERS.register(Long.class, long.class,
				TypedReader.<Long>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsLong))
						.toml(TypedReader.TomlReader.contextless((config, path) -> config.<Number>getRaw(path)
								.longValue()))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).longValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.VAR_LONG::decode)),
				TypedWriter.<Long>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.VAR_LONG::encode))
		);
		TYPED_HANDLERS.register(Boolean.class, boolean.class,
				TypedReader.<Boolean>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsBoolean))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::get))
						.object(TypedReader.ObjectReader.contextless(item -> (Boolean) item))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.BOOL::decode)),
				TypedWriter.<Boolean>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.BOOL::encode))
		);
		TYPED_HANDLERS.register(Double.class, double.class,
				TypedReader.<Double>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsDouble))
						.toml(TypedReader.TomlReader.contextless((config, path) -> config.<Number>getRaw(path)
								.doubleValue()))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).doubleValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.DOUBLE::decode)),
				TypedWriter.<Double>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.DOUBLE::encode))
		);
		TYPED_HANDLERS.register(Float.class, float.class,
				TypedReader.<Float>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsFloat))
						.toml(TypedReader.TomlReader.contextless((config, path) -> config.<Number>getRaw(path)
								.floatValue()))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).floatValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.FLOAT::decode)),
				TypedWriter.<Float>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.FLOAT::encode))
		);
		TYPED_HANDLERS.register(Byte.class, byte.class,
				TypedReader.<Byte>create()
						.json(TypedReader.JsonReader.contextless(element -> element.getAsNumber().byteValue()))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::getByte))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).byteValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.BYTE::decode)),
				TypedWriter.<Byte>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.BYTE::encode))
		);
		TYPED_HANDLERS.register(Short.class, short.class,
				TypedReader.<Short>create()
						.json(TypedReader.JsonReader.contextless(element -> element.getAsNumber().shortValue()))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::getShort))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).shortValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.SHORT::decode)),
				TypedWriter.<Short>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.SHORT::encode))
		);
		Function<String, Character> charParser = str -> {
			if (str.length() != 1) {
				throw new IllegalArgumentException("Expected a single character for type char, but got: %s".formatted(str));
			}
			return str.charAt(0);
		};
		TYPED_HANDLERS.register(Character.class, char.class,
				TypedReader.<Character>create()
						.json(TypedReader.JsonReader.contextless(element -> charParser.apply(element.getAsString())))
						.toml(TypedReader.TomlReader.contextless((config, path) -> charParser.apply(config.get(path))))
						.object(TypedReader.ObjectReader.contextless(item -> charParser.apply(item.toString())))
						.byteBuf(TypedReader.ByteBufReader.contextless(byteBuf -> {
							int value = ByteBufCodecs.VAR_INT.decode(byteBuf);
							return (char) value;
						})),
				TypedWriter.<Character>create()
						.byteBuf(TypedWriter.ByteBufWriter.contextless((byteBuf, value) -> ByteBufCodecs.VAR_INT.encode(byteBuf, (int) value)))
		);
		//noinspection unchecked,rawtypes
		ENUM_HANDLER = new TypedHandler<>(
				TypedReader.<Enum<?>>create()
						.json((element, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), element.getAsString()))
						.toml((config, path, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), config.get(path)))
						.object((item, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), item.toString()))
						.byteBuf((byteBuf, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), ByteBufCodecs.STRING_UTF8.decode(byteBuf))),
				TypedWriter.<Enum<?>>create()
						.byteBuf((byteBuf, item, expectedType) -> ByteBufCodecs.STRING_UTF8.encode(byteBuf, item.name()))
		);
		ARRAY_HANDLER = new TypedHandler<>(
				TypedReader.create()
						.json((element, expectedType) -> {
							if (!element.isJsonArray()) {
								throw new IllegalArgumentException("Expected a JSON array for type array, but got: %s".formatted(element));
							}
							JsonArray jsonArray = element.getAsJsonArray();
							int size = jsonArray.size();
							Class<?> componentType = expectedType.type().getComponentType();
							Object array = Array.newInstance(componentType, size);

							for (int i = 0; i < size; i++) {
								JsonElement jsonElement = jsonArray.get(i);
								Object value = interpret(jsonElement, new GenericType(componentType));
								Array.set(array, i, value);
							}
							return array;
						})
						.toml((config, path, expectedType) -> {
							List<?> list = config.get(path);
							if (list == null) return null;

							Class<?> componentType = expectedType.type().getComponentType();
							int size = list.size();
							Object array = Array.newInstance(componentType, size);
							for (int i = 0; i < size; i++) {
								Object item = list.get(i);
								Object value = interpret(item, new GenericType(componentType));
								Array.set(array, i, value);
							}
							return array;
						})
						.object((item, expectedType) -> {
							if (!(item instanceof List<?> list)) {
								throw new IllegalArgumentException("Expected a List for type array, but got: %s".formatted(item));
							}
							Class<?> componentType = expectedType.type().getComponentType();
							int size = list.size();
							Object array = Array.newInstance(componentType, size);
							for (int i = 0; i < size; i++) {
								Object value = interpret(list.get(i), new GenericType(componentType));
								Array.set(array, i, value);
							}
							return array;
						})
						.byteBuf((byteBuf, expectedType) -> {
							int length = ByteBufCodecs.VAR_INT.decode(byteBuf);
							Class<?> componentType = expectedType.type().getComponentType();
							Object array = Array.newInstance(componentType, length);
							for (int i = 0; i < length; i++) {
								Object item = interpet(byteBuf, new GenericType(componentType));
								Array.set(array, i, item);
							}
							return array;
						}),
				TypedWriter.create()
						.byteBuf((byteBuf, item, expectedType) -> {
							int length = Array.getLength(item);
							ByteBufCodecs.VAR_INT.encode(byteBuf, length);
							Class<?> componentType = expectedType.type().getComponentType();
							for (int i = 0; i < length; i++) {
								Object arrayItem = Array.get(item, i);
								encode(byteBuf, arrayItem, new GenericType(componentType));
							}
						})
		);
		LIST_HANDLER = new TypedHandler<>(
				TypedReader.<List<?>>create()
						.json((element, expectedType) -> {
							if (!element.isJsonArray()) {
								throw new IllegalArgumentException("Expected a JSON array for type List, but got: %s".formatted(element));
							}
							List<Object> interpretedList = new ArrayList<>();
							for (JsonElement item : element.getAsJsonArray()) {
								interpretedList.add(interpret(item, expectedType.genericTypes()[0]));
							}
							return interpretedList;
						})
						.toml((config, path, expectedType) -> {
							List<?> list = config.get(path);
							if (list == null) return null;

							List<Object> interpretedList = new ArrayList<>(list.size());
							for (Object item : list) {
								interpretedList.add(interpret(item, expectedType.genericTypes()[0]));
							}
							return interpretedList;
						})
						.object((item, expectedType) -> {
							if (!(item instanceof List<?> list)) {
								throw new IllegalArgumentException("Expected a List for type List, but got: %s".formatted(item));
							}
							List<Object> interpretedList = new ArrayList<>(list.size());
							for (Object listItem : list) {
								interpretedList.add(interpret(listItem, expectedType.genericTypes()[0]));
							}
							return interpretedList;
						})
						.byteBuf((byteBuf, expectedType) -> {
							int length = ByteBufCodecs.VAR_INT.decode(byteBuf);
							List<Object> interpretedList = new ArrayList<>(length);
							for (int i = 0; i < length; i++) {
								Object item = interpet(byteBuf, expectedType.genericTypes()[0]);
								interpretedList.add(item);
							}
							return interpretedList;
						}),
				TypedWriter.<List<?>>create()
						.byteBuf((byteBuf, item, expectedType) -> {
							ByteBufCodecs.VAR_INT.encode(byteBuf, item.size());
							for (Object listItem : item) {
								encode(byteBuf, listItem, expectedType.genericTypes()[0]);
							}
						})
		);
	}

	public static Object interpret(JsonElement element, GenericType genericType) {
		return getTypedHandler(genericType).readFromJson(element);
	}

	public static Object interpret(CommentedConfig toml, String path, GenericType genericType) {
		return getTypedHandler(genericType).readFromToml(toml, path);
	}

	public static Object interpret(Object item, GenericType genericType) {
		return getTypedHandler(genericType).readFromObject(item);
	}

	public static Object interpet(ByteBuf byteBuf, GenericType genericType) {
		return getTypedHandler(genericType).readFromByteBuf(byteBuf);
	}

	public static void encode(ByteBuf byteBuf, Object value, GenericType genericType) {
		getTypedHandler(genericType).writeToByteBuf(byteBuf, value);
	}

	private static @NotNull TypedHandler<?> getTypedHandler(GenericType genericType) {
		TypedHandler<?> handler;
		if (genericType.type().isEnum()) {
			handler = ENUM_HANDLER;
		} else if (genericType.type().isArray()) {
			handler = ARRAY_HANDLER;
		} else if (List.class.isAssignableFrom(genericType.type())) {
			handler = LIST_HANDLER;
		} else {
			handler = TYPED_HANDLERS.getHandlerFor(genericType);
		}
		handler.attachExpectedType(genericType);
		return handler;
	}

	/**
	 * TOML doesn't support array types directly so we need to convert them to lists.
	 * Additionally, lists cant be of array types (List<String[]>), so we need to check for that.
	 * TOML also doesn't support characters, so we convert them to strings.
	 */
	public static Object safeForToml(Object value) {
		if (value == null) return null;

		Class<?> type = value.getClass();
		if (type.isEnum()) {
			return value.toString();
		} else if (type.isArray()) {
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
