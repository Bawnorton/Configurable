package com.bawnorton.configurable.api.impl;

import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.processor.ConfigurableSettings;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.Pair;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Properties;


@ApiStatus.Internal
public final class ConfigurableApiImpl {
	public static void saveChanges(ServerLevel level, boolean sync) {
		ConfigLoader configLoader = getCallersConfigLoader();
		ConfigurableLoader.saveChanges(configLoader, level, sync);
	}

	public static void loadFromDisk(ServerLevel level, boolean sync) {
		ConfigLoader configLoader = getCallersConfigLoader();
		ConfigurableLoader.loadFromDisk(configLoader, level, sync);
	}

	/**
	 * Attempts to find the caller's ConfigLoader class based on the stack trace.
	 */
	private static ConfigLoader getCallersConfigLoader() {
		try {
			InputStream in = findConfigFile();
			if (in == null) {
				throw new IllegalStateException("No configurable.properties found in caller's jar");
			}
			Properties properties = new Properties();
			properties.load(in);
			in.close();
			ConfigurableSettings settings = ConfigurableSettings.fromProperties(properties);
			String name = settings.name();
			return ConfigurableLoader.getConfigLoader(name);
		} catch (Exception e) {
			throw new RuntimeException("Could not find ConfigLoader for the caller", e);
		}
	}

	private static @Nullable InputStream findConfigFile() throws IOException {
		Pair<Class<?>, String> caller = findCaller();
		InputStream propertiesStream = checkForMixin(caller);
		if (propertiesStream == null) {
			Class<?> clazz = caller.first();
			CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
			if (codeSource == null) return null;

			URL resource = codeSource.getLocation();
			propertiesStream = configFileFromJarResource(resource);
		}
		return propertiesStream;
	}

	private static Pair<Class<?>, String> findCaller() {
		StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		StackWalker.StackFrame frame = walker.walk(frames -> frames
				.filter(f -> !f.getDeclaringClass().getPackageName().startsWith("com.bawnorton.configurable"))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No suitable caller found in the stack trace.")));
		return Pair.of(frame.getDeclaringClass(), frame.getMethodName());
	}

	private static @Nullable InputStream checkForMixin(Pair<Class<?>, String> caller) throws IOException {
		MixinMerged annotation;
		Class<?> clazz = caller.first();
		String methodName = caller.second();
		Method method = findMethod(clazz, methodName);
		if (method == null) return null;
		if (!method.isAnnotationPresent(MixinMerged.class)) return null;

		annotation = method.getAnnotation(MixinMerged.class);
		String mixinClassName = annotation.mixin();
		ClassLoader classLoader = clazz.getClassLoader();
		URL resource = classLoader.getResource(mixinClassName.replace('.', '/') + ".class");
		if (resource == null) return null;

		return configFileFromJarResource(resource);
	}

	private static Method findMethod(Class<?> clazz, String name) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		return null;
	}


	private static @Nullable InputStream configFileFromJarResource(URL resource) throws IOException {
		if (resource == null || !resource.getPath().contains("!")) {
			return findConfigFile(resource);
		}

		String path = resource.toString();
		int separator = path.indexOf('!');
		String jarPath = path.substring(0, separator);
		URL resourceUrl = URI.create("%s!/META-INF/configurable.properties".formatted(jarPath)).toURL();
		return resourceUrl.openStream();
	}

	private static @Nullable InputStream findConfigFile(@Nullable URL resource) throws IOException {
		ClassLoader classLoader = ConfigurableApiImpl.class.getClassLoader();
		Enumeration<URL> resources = classLoader.getResources("META-INF/configurable.properties");
		if (resource == null) {
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				if (url.getProtocol().equals("file")) {
					return url.openStream();
				}
			}
		} else {
			String path = resource.getPath();
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				String urlPath = url.getPath();
				if (urlPath.contains(path)) {
					return url.openStream();
				}
			}
		}
		return null;
	}
}
