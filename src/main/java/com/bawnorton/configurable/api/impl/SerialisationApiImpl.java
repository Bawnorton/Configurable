package com.bawnorton.configurable.api.impl;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.api.serialisation.SerialisationBootstrap;
import com.bawnorton.configurable.api.serialisation.TypedHandlerResolver;
import com.bawnorton.configurable.io.SerialisationRegistry;
import com.bawnorton.configurable.io.bootstrap.DefaultSerialisationBootstrap;
import com.bawnorton.configurable.io.typed.TypedHandler;
import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
//?}


@ApiStatus.Internal
public final class SerialisationApiImpl {
	private static final Object BOOTSTRAP_LOCK = new Object();
	private static final DefaultSerialisationBootstrap DEFAULT_BOOTSTRAP = new DefaultSerialisationBootstrap();
	private static final List<SerialisationBootstrap> USER_BOOTSTRAPS = new ArrayList<>();
	private static final SerialisationRegistry REGISTRY = new SerialisationRegistry();
	private static boolean BOOTSTRAPPED = false;

	private SerialisationApiImpl() {
	}

	private static void ensureBootstrapped() {
		if (BOOTSTRAPPED) return;

		synchronized (BOOTSTRAP_LOCK) {
			if (BOOTSTRAPPED) return;

			DEFAULT_BOOTSTRAP.bootstrap(REGISTRY);
			for (SerialisationBootstrap bootstrap : discoverServiceBootstraps()) {
				bootstrap.bootstrap(REGISTRY);
			}
			for (SerialisationBootstrap bootstrap : USER_BOOTSTRAPS) {
				bootstrap.bootstrap(REGISTRY);
			}
			REGISTRY.freeze();
			BOOTSTRAPPED = true;
		}
	}

	private static List<SerialisationBootstrap> discoverServiceBootstraps() {
		List<SerialisationBootstrap> discovered = new ArrayList<>();
		try {
			for (SerialisationBootstrap bootstrap : ServiceLoader.load(SerialisationBootstrap.class, SerialisationApiImpl.class.getClassLoader())) {
				discovered.add(bootstrap);
			}
		} catch (ServiceConfigurationError e) {
			throw new IllegalStateException("Failed to load serialisation bootstrap services", e);
		}

		//? if fabric {
		List<EntrypointContainer<SerialisationBootstrap>> containers = FabricLoader.getInstance()
				.getEntrypointContainers("configurable-serialisation", SerialisationBootstrap.class);
		for(EntrypointContainer<SerialisationBootstrap> container : containers) {
			discovered.add(container.getEntrypoint());
		}
		//?}

		discovered.sort(Comparator.comparingInt(SerialisationBootstrap::priority)
				.reversed()
				.thenComparing(bootstrap -> bootstrap.getClass().getName())
		);

		if (!discovered.isEmpty()) {
			ConfigurableMain.LOGGER.info("Loaded {} serialisation bootstrap service(s)", discovered.size());
		}
		return discovered;
	}

	public static void registerBootstrap(SerialisationBootstrap bootstrap) {
		synchronized (BOOTSTRAP_LOCK) {
			if (BOOTSTRAPPED) {
				throw new IllegalStateException("Serialisation has already been used and can no longer be extended");
			}
			USER_BOOTSTRAPS.add(bootstrap);
		}
	}

	public static <T> void registerType(Class<T> boxedType, Class<T> primitiveType, TypedReader<T> reader, TypedWriter<T> writer) {
		registerBootstrap(registrar -> registrar.registerType(boxedType, primitiveType, reader, writer));
	}

	public static void registerResolver(TypedHandlerResolver resolver) {
		registerBootstrap(registrar -> registrar.registerResolver(resolver));
	}

	public static Object decode(JsonElement element, GenericType genericType) {
		return getTypedHandler(genericType).readFromJson(element);
	}

	public static Object decode(CommentedConfig toml, String path, GenericType genericType) {
		return getTypedHandler(genericType).readFromToml(toml, path);
	}

	public static Object decode(Object item, GenericType genericType) {
		return getTypedHandler(genericType).readFromObject(item);
	}

	public static Object decode(ByteBuf byteBuf, GenericType genericType) {
		return getTypedHandler(genericType).readFromByteBuf(byteBuf);
	}

	public static void encode(ByteBuf byteBuf, Object value, GenericType genericType) {
		getTypedHandler(genericType).writeToByteBuf(byteBuf, value);
	}

	public static JsonElement encodeJson(Object value, GenericType genericType) {
		return getTypedHandler(genericType).writeToJson(value);
	}

	public static Object encodeToml(Object value, GenericType genericType) {
		return getTypedHandler(genericType).writeToToml(value);
	}

	private static @NotNull TypedHandler<?> getTypedHandler(GenericType genericType) {
		ensureBootstrapped();
		return REGISTRY.resolve(genericType);
	}

}

