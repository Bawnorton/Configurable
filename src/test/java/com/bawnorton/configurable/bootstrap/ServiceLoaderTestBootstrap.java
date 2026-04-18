package com.bawnorton.configurable.bootstrap;

import com.bawnorton.configurable.api.serialisation.SerialisationBootstrap;
import com.bawnorton.configurable.api.serialisation.SerialisationRegistrar;
import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.concurrent.atomic.AtomicInteger;

public final class ServiceLoaderTestBootstrap implements SerialisationBootstrap {
	public static final AtomicInteger BOOTSTRAP_CALLS = new AtomicInteger();
	public static final AtomicInteger JSON_READ_CALLS = new AtomicInteger();
	public static final AtomicInteger TOML_READ_CALLS = new AtomicInteger();
	public static final AtomicInteger JSON_WRITE_CALLS = new AtomicInteger();
	public static final AtomicInteger TOML_WRITE_CALLS = new AtomicInteger();
	public static final AtomicInteger BYTEBUF_READ_CALLS = new AtomicInteger();
	public static final AtomicInteger BYTEBUF_WRITE_CALLS = new AtomicInteger();

	@Override
	public void bootstrap(SerialisationRegistrar registrar) {
		BOOTSTRAP_CALLS.incrementAndGet();
		registrar.registerType(
				ServiceLoadedValue.class,
				TypedReader.<ServiceLoadedValue>create()
						.json(TypedReader.JsonReader.contextless(element -> {
							JSON_READ_CALLS.incrementAndGet();
							return new ServiceLoadedValue(element.getAsString());
						}))
						.toml(TypedReader.TomlReader.contextless((config, path) -> {
							TOML_READ_CALLS.incrementAndGet();
							return new ServiceLoadedValue(config.get(path));
						}))
						.object(TypedReader.ObjectReader.contextless(item -> {
							if (item instanceof ServiceLoadedValue value) {
								return value;
							}
							return new ServiceLoadedValue(item.toString());
						}))
						.byteBuf(TypedReader.ByteBufReader.contextless(byteBuf -> {
							BYTEBUF_READ_CALLS.incrementAndGet();
							return new ServiceLoadedValue(ByteBufCodecs.STRING_UTF8.decode(byteBuf));
						})),
				TypedWriter.<ServiceLoadedValue>create()
						.json(TypedWriter.JsonWriter.contextless(value -> {
							JSON_WRITE_CALLS.incrementAndGet();
							return new JsonPrimitive(value.value());
						}))
						.toml(TypedWriter.TomlWriter.contextless(value -> {
							TOML_WRITE_CALLS.incrementAndGet();
							return value.value();
						}))
						.byteBuf(TypedWriter.ByteBufWriter.contextless((byteBuf, value) -> {
							BYTEBUF_WRITE_CALLS.incrementAndGet();
							ByteBufCodecs.STRING_UTF8.encode(byteBuf, value.value());
						}))
		);
	}
}

