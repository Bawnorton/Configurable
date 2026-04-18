package com.bawnorton.configurable;

import com.bawnorton.configurable.bootstrap.ServiceLoadedValue;
import com.bawnorton.configurable.bootstrap.ServiceLoaderTestBootstrap;
import com.bawnorton.configurable.api.serialisation.SerialisationApi;
import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SerialisationServiceLoaderTests {
	@Test
	public void testServiceLoaderBootstrapRegistersCustomType() {
		int jsonReadsBefore = ServiceLoaderTestBootstrap.JSON_READ_CALLS.get();

		Object interpreted = SerialisationApi.decode(
				new JsonPrimitive("service-loader"),
				new GenericType(ServiceLoadedValue.class)
		);

		Assertions.assertEquals(new ServiceLoadedValue("service-loader"), interpreted);
		Assertions.assertEquals(jsonReadsBefore + 1, ServiceLoaderTestBootstrap.JSON_READ_CALLS.get());
		Assertions.assertTrue(
				ServiceLoaderTestBootstrap.BOOTSTRAP_CALLS.get() >= 1,
				"Expected ServiceLoader bootstrap to run at least once"
		);
	}

	@Test
	public void testServiceLoaderRegisteredTypeRoundTripsThroughByteBuf() {
		GenericType serviceValueType = new GenericType(ServiceLoadedValue.class);
		ServiceLoadedValue original = new ServiceLoadedValue("round-trip");
		int writesBefore = ServiceLoaderTestBootstrap.BYTEBUF_WRITE_CALLS.get();
		int readsBefore = ServiceLoaderTestBootstrap.BYTEBUF_READ_CALLS.get();

		ByteBuf byteBuf = Unpooled.buffer();
		SerialisationApi.encodeByteBuf(byteBuf, original, serviceValueType);
		Object decoded = SerialisationApi.decode(byteBuf, serviceValueType);

		Assertions.assertEquals(original, decoded);
		Assertions.assertEquals(writesBefore + 1, ServiceLoaderTestBootstrap.BYTEBUF_WRITE_CALLS.get());
		Assertions.assertEquals(readsBefore + 1, ServiceLoaderTestBootstrap.BYTEBUF_READ_CALLS.get());
	}

	@Test
	public void testServiceLoaderRegisteredTypeEncodesToJson() {
		int writesBefore = ServiceLoaderTestBootstrap.JSON_WRITE_CALLS.get();
		GenericType serviceValueType = new GenericType(ServiceLoadedValue.class);

		Object encoded = SerialisationApi.encodeJson(new ServiceLoadedValue("json-write"), serviceValueType);

		Assertions.assertEquals("\"json-write\"", encoded.toString());
		Assertions.assertEquals(writesBefore + 1, ServiceLoaderTestBootstrap.JSON_WRITE_CALLS.get());
	}

	@Test
	public void testServiceLoaderRegisteredTypeEncodesToToml() {
		int writesBefore = ServiceLoaderTestBootstrap.TOML_WRITE_CALLS.get();
		GenericType serviceValueType = new GenericType(ServiceLoadedValue.class);

		Object encoded = SerialisationApi.encodeToml(new ServiceLoadedValue("toml-write"), serviceValueType);

		Assertions.assertEquals("toml-write", encoded);
		Assertions.assertEquals(writesBefore + 1, ServiceLoaderTestBootstrap.TOML_WRITE_CALLS.get());
	}

	@Test
	public void testServiceLoaderRegisteredTypeRoundTripsThroughJson() {
		GenericType serviceValueType = new GenericType(ServiceLoadedValue.class);
		ServiceLoadedValue original = new ServiceLoadedValue("json-round-trip");
		int jsonWritesBefore = ServiceLoaderTestBootstrap.JSON_WRITE_CALLS.get();
		int jsonReadsBefore = ServiceLoaderTestBootstrap.JSON_READ_CALLS.get();

		JsonElement encoded = SerialisationApi.encodeJson(original, serviceValueType);
		Object decoded = SerialisationApi.decode(encoded, serviceValueType);

		Assertions.assertEquals(new JsonPrimitive("json-round-trip"), encoded);
		Assertions.assertEquals(original, decoded);
		Assertions.assertEquals(jsonWritesBefore + 1, ServiceLoaderTestBootstrap.JSON_WRITE_CALLS.get());
		Assertions.assertEquals(jsonReadsBefore + 1, ServiceLoaderTestBootstrap.JSON_READ_CALLS.get());
	}

	@Test
	public void testServiceLoaderRegisteredTypeRoundTripsThroughToml() {
		GenericType serviceValueType = new GenericType(ServiceLoadedValue.class);
		ServiceLoadedValue original = new ServiceLoadedValue("toml-round-trip");
		int tomlWritesBefore = ServiceLoaderTestBootstrap.TOML_WRITE_CALLS.get();
		int tomlReadsBefore = ServiceLoaderTestBootstrap.TOML_READ_CALLS.get();

		Object encoded = SerialisationApi.encodeToml(original, serviceValueType);
		CommentedConfig config = TomlFormat.newConfig();
		config.set("value", encoded);
		Object decoded = SerialisationApi.decode(config, "value", serviceValueType);

		Assertions.assertEquals("toml-round-trip", encoded);
		Assertions.assertEquals(original, decoded);
		Assertions.assertEquals(tomlWritesBefore + 1, ServiceLoaderTestBootstrap.TOML_WRITE_CALLS.get());
		Assertions.assertEquals(tomlReadsBefore + 1, ServiceLoaderTestBootstrap.TOML_READ_CALLS.get());
	}
}

