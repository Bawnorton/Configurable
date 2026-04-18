package com.bawnorton.configurable.io.bootstrap;

import com.bawnorton.configurable.api.serialisation.SerialisationApi;
import com.bawnorton.configurable.api.serialisation.SerialisationBootstrap;
import com.bawnorton.configurable.api.serialisation.SerialisationRegistrar;
import com.bawnorton.configurable.io.typed.TypedHandler;
import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.codec.ByteBufCodecs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class DefaultSerialisationBootstrap implements SerialisationBootstrap {
	@Override
	public void bootstrap(SerialisationRegistrar registrar) {
		registerPrimitiveTypes(registrar);
		registrar.registerResolver(DefaultSerialisationBootstrap::resolveDefaultCompositeTypes);
	}

	private static void registerPrimitiveTypes(SerialisationRegistrar registrar) {
		registrar.registerType(String.class,
				TypedReader.<String>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsString))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::get))
						.object(TypedReader.ObjectReader.contextless(Object::toString))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.STRING_UTF8::decode)),
				TypedWriter.<String>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.STRING_UTF8::encode))
		);
		registrar.registerType(Integer.class, int.class,
				TypedReader.<Integer>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsInt))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::getInt))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).intValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.VAR_INT::decode)),
				TypedWriter.<Integer>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.VAR_INT::encode))
		);
		registrar.registerType(Long.class, long.class,
				TypedReader.<Long>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsLong))
						.toml(TypedReader.TomlReader.contextless((config, path) -> config.<Number>getRaw(path).longValue()))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).longValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.VAR_LONG::decode)),
				TypedWriter.<Long>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.VAR_LONG::encode))
		);
		registrar.registerType(Boolean.class, boolean.class,
				TypedReader.<Boolean>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsBoolean))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::get))
						.object(TypedReader.ObjectReader.contextless(item -> (Boolean) item))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.BOOL::decode)),
				TypedWriter.<Boolean>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.BOOL::encode))
		);
		registrar.registerType(Double.class, double.class,
				TypedReader.<Double>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsDouble))
						.toml(TypedReader.TomlReader.contextless((config, path) -> config.<Number>getRaw(path).doubleValue()))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).doubleValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.DOUBLE::decode)),
				TypedWriter.<Double>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.DOUBLE::encode))
		);
		registrar.registerType(Float.class, float.class,
				TypedReader.<Float>create()
						.json(TypedReader.JsonReader.contextless(JsonElement::getAsFloat))
						.toml(TypedReader.TomlReader.contextless((config, path) -> config.<Number>getRaw(path).floatValue()))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).floatValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.FLOAT::decode)),
				TypedWriter.<Float>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.FLOAT::encode))
		);
		registrar.registerType(Byte.class, byte.class,
				TypedReader.<Byte>create()
						.json(TypedReader.JsonReader.contextless(element -> element.getAsNumber().byteValue()))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::getByte))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).byteValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.BYTE::decode)),
				TypedWriter.<Byte>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.BYTE::encode))
		);
		registrar.registerType(Short.class, short.class,
				TypedReader.<Short>create()
						.json(TypedReader.JsonReader.contextless(element -> element.getAsNumber().shortValue()))
						.toml(TypedReader.TomlReader.contextless(UnmodifiableConfig::getShort))
						.object(TypedReader.ObjectReader.contextless(item -> ((Number) item).shortValue()))
						.byteBuf(TypedReader.ByteBufReader.contextless(ByteBufCodecs.SHORT::decode)),
				TypedWriter.<Short>create()
						.json(TypedWriter.JsonWriter.contextless(JsonPrimitive::new))
						.toml(TypedWriter.TomlWriter.contextless(value -> value))
						.byteBuf(TypedWriter.ByteBufWriter.contextless(ByteBufCodecs.SHORT::encode))
		);
		Function<String, Character> charParser = str -> {
			if (str.length() != 1) {
				throw new IllegalArgumentException("Expected a single character for type char, but got: %s".formatted(str));
			}
			return str.charAt(0);
		};
		registrar.registerType(Character.class, char.class,
				TypedReader.<Character>create()
						.json(TypedReader.JsonReader.contextless(element -> charParser.apply(element.getAsString())))
						.toml(TypedReader.TomlReader.contextless((config, path) -> charParser.apply(config.get(path))))
						.object(TypedReader.ObjectReader.contextless(item -> charParser.apply(item.toString())))
						.byteBuf(TypedReader.ByteBufReader.contextless(byteBuf -> (char) ByteBufCodecs.VAR_INT.decode(byteBuf).intValue())),
				TypedWriter.<Character>create()
						.json(TypedWriter.JsonWriter.contextless(value -> new JsonPrimitive(String.valueOf(value))))
						.toml(TypedWriter.TomlWriter.contextless(String::valueOf))
						.byteBuf(TypedWriter.ByteBufWriter.contextless((byteBuf, value) -> ByteBufCodecs.VAR_INT.encode(byteBuf, (int) value)))
		);
	}

	private static TypedHandler<?> resolveDefaultCompositeTypes(GenericType expectedType) {
		if (expectedType.type().isEnum()) {
			return enumHandler();
		}
		if (expectedType.type().isArray()) {
			return arrayHandler();
		}
		if (List.class.isAssignableFrom(expectedType.type())) {
			return listHandler();
		}
		return null;
	}

	private static TypedHandler<Enum<?>> enumHandler() {
		//noinspection unchecked,rawtypes
		return new TypedHandler<>(
				TypedReader.<Enum<?>>create()
						.json((element, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), element.getAsString()))
						.toml((config, path, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), config.get(path)))
						.object((item, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), item.toString()))
						.byteBuf((byteBuf, expectedType) -> Enum.valueOf((Class<Enum>) expectedType.type(), ByteBufCodecs.STRING_UTF8.decode(byteBuf))),
				TypedWriter.<Enum<?>>create()
						.json((item, expectedType) -> new JsonPrimitive(item.name()))
						.toml((item, expectedType) -> item.name())
						.byteBuf((byteBuf, item, expectedType) -> ByteBufCodecs.STRING_UTF8.encode(byteBuf, item.name()))
		);
	}

	private static TypedHandler<Object> arrayHandler() {
		return new TypedHandler<>(
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
								Object value = SerialisationApi.decode(jsonElement, new GenericType(componentType));
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
								Object value = SerialisationApi.decode(item, new GenericType(componentType));
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
								Object value = SerialisationApi.decode(list.get(i), new GenericType(componentType));
								Array.set(array, i, value);
							}
							return array;
						})
						.byteBuf((byteBuf, expectedType) -> {
							int length = ByteBufCodecs.VAR_INT.decode(byteBuf);
							Class<?> componentType = expectedType.type().getComponentType();
							Object array = Array.newInstance(componentType, length);
							for (int i = 0; i < length; i++) {
								Object item = SerialisationApi.decode(byteBuf, new GenericType(componentType));
								Array.set(array, i, item);
							}
							return array;
						}),
				TypedWriter.create()
						.json((item, expectedType) -> {
							if (item == null) return JsonNull.INSTANCE;
							int length = Array.getLength(item);
							Class<?> componentType = expectedType.type().getComponentType();
							JsonArray array = new JsonArray(length);
							for (int i = 0; i < length; i++) {
								Object arrayItem = Array.get(item, i);
								array.add(SerialisationApi.encodeJson(arrayItem, new GenericType(componentType)));
							}
							return array;
						})
						.toml((item, expectedType) -> {
							if (item == null) return null;
							int length = Array.getLength(item);
							Class<?> componentType = expectedType.type().getComponentType();
							List<Object> safeValues = new ArrayList<>(length);
							for (int i = 0; i < length; i++) {
								Object arrayItem = Array.get(item, i);
								safeValues.add(SerialisationApi.encodeToml(arrayItem, new GenericType(componentType)));
							}
							return safeValues;
						})
						.byteBuf((byteBuf, item, expectedType) -> {
							int length = Array.getLength(item);
							ByteBufCodecs.VAR_INT.encode(byteBuf, length);
							Class<?> componentType = expectedType.type().getComponentType();
							for (int i = 0; i < length; i++) {
								Object arrayItem = Array.get(item, i);
								SerialisationApi.encodeByteBuf(byteBuf, arrayItem, new GenericType(componentType));
							}
						})
		);
	}

	private static TypedHandler<List<?>> listHandler() {
		return new TypedHandler<>(
				TypedReader.<List<?>>create()
						.json((element, expectedType) -> {
							if (!element.isJsonArray()) {
								throw new IllegalArgumentException("Expected a JSON array for type List, but got: %s".formatted(element));
							}
							List<Object> decodeedList = new ArrayList<>();
							for (JsonElement item : element.getAsJsonArray()) {
								decodeedList.add(SerialisationApi.decode(item, expectedType.genericTypes()[0]));
							}
							return decodeedList;
						})
						.toml((config, path, expectedType) -> {
							List<?> list = config.get(path);
							if (list == null) return null;

							List<Object> decodeedList = new ArrayList<>(list.size());
							for (Object item : list) {
								decodeedList.add(SerialisationApi.decode(item, expectedType.genericTypes()[0]));
							}
							return decodeedList;
						})
						.object((item, expectedType) -> {
							if (!(item instanceof List<?> list)) {
								throw new IllegalArgumentException("Expected a List for type List, but got: %s".formatted(item));
							}
							List<Object> decodeedList = new ArrayList<>(list.size());
							for (Object listItem : list) {
								decodeedList.add(SerialisationApi.decode(listItem, expectedType.genericTypes()[0]));
							}
							return decodeedList;
						})
						.byteBuf((byteBuf, expectedType) -> {
							int length = ByteBufCodecs.VAR_INT.decode(byteBuf);
							List<Object> decodeedList = new ArrayList<>(length);
							for (int i = 0; i < length; i++) {
								Object item = SerialisationApi.decode(byteBuf, expectedType.genericTypes()[0]);
								decodeedList.add(item);
							}
							return decodeedList;
						}),
				TypedWriter.<List<?>>create()
						.json((item, expectedType) -> {
							if (item == null) return JsonNull.INSTANCE;
							JsonArray encodedList = new JsonArray(item.size());
							for (Object listItem : item) {
								encodedList.add(SerialisationApi.encodeJson(listItem, expectedType.genericTypes()[0]));
							}
							return encodedList;
						})
						.toml((item, expectedType) -> {
							if (item == null) return null;
							List<Object> encodedList = new ArrayList<>(item.size());
							for (Object listItem : item) {
								encodedList.add(SerialisationApi.encodeToml(listItem, expectedType.genericTypes()[0]));
							}
							return encodedList;
						})
						.byteBuf((byteBuf, item, expectedType) -> {
							ByteBufCodecs.VAR_INT.encode(byteBuf, item.size());
							for (Object listItem : item) {
								SerialisationApi.encodeByteBuf(byteBuf, listItem, expectedType.genericTypes()[0]);
							}
						})
		);
	}
}

