package com.bawnorton.configurable.api.serialisation;

import com.bawnorton.configurable.api.impl.SerialisationApiImpl;
import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;

public final class SerialisationApi {
	private SerialisationApi() {
	}

	public static void registerBootstrap(SerialisationBootstrap bootstrap) {
		SerialisationApiImpl.registerBootstrap(bootstrap);
	}

	public static <T> void registerType(Class<T> boxedType, Class<T> primitiveType, TypedReader<T> reader, TypedWriter<T> writer) {
		SerialisationApiImpl.registerType(boxedType, primitiveType, reader, writer);
	}

	public static void registerResolver(TypedHandlerResolver resolver) {
		SerialisationApiImpl.registerResolver(resolver);
	}

	public static Object decode(JsonElement element, GenericType genericType) {
		return SerialisationApiImpl.decode(element, genericType);
	}

	public static Object decode(CommentedConfig toml, String path, GenericType genericType) {
		return SerialisationApiImpl.decode(toml, path, genericType);
	}

	public static Object decode(Object item, GenericType genericType) {
		return SerialisationApiImpl.decode(item, genericType);
	}

	public static Object decode(ByteBuf byteBuf, GenericType genericType) {
		return SerialisationApiImpl.decode(byteBuf, genericType);
	}

	public static void encodeByteBuf(ByteBuf byteBuf, Object value, GenericType genericType) {
		SerialisationApiImpl.encode(byteBuf, value, genericType);
	}

	public static JsonElement encodeJson(Object value, GenericType genericType) {
		return SerialisationApiImpl.encodeJson(value, genericType);
	}

	public static Object encodeToml(Object value, GenericType genericType) {
		return SerialisationApiImpl.encodeToml(value, genericType);
	}
}

