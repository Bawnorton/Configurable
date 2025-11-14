package com.bawnorton.configurable.helper;

import com.bawnorton.configurable.processor.ConfigurableProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.junit.platform.commons.logging.Logger;

import java.io.InputStream;

public class CompilationHelper {
	public static Compiler newCompiler() {
		return Compiler.javac().withProcessors(new ConfigurableProcessor());
	}

	public static void logDiagnostics(Compilation compilation) {
		Logger logger = ConfigurableTestHelper.LOGGER;
		if (compilation.diagnostics().isEmpty()) {
			logger.info(() -> "No diagnostics found.");
		} else {
			for (var diagnostic : compilation.diagnostics()) {
				switch (diagnostic.getKind()) {
					case ERROR -> logger.error(diagnostic::toString);
					case WARNING, MANDATORY_WARNING -> logger.warn(diagnostic::toString);
					default -> logger.info(diagnostic::toString);
				}
			}
		}
	}

	public static ClassLoader getGeneratedClassLoader(Compilation compilation) {
		return new ClassLoader() {
			@Override
			protected Class<?> findClass(String name) {
				JavaFileObject generatedClazz = compilation.generatedFile(StandardLocation.CLASS_OUTPUT, name.replaceAll("\\.", "/") + ".class")
						.orElseThrow();
				byte[] classBytes = getClassBytes(generatedClazz);
				return defineClass(name, classBytes, 0, classBytes.length);
			}
		};
	}

	private static byte[] getClassBytes(JavaFileObject generatedClazz) {
		try (InputStream inputStream = generatedClazz.openInputStream()) {
			return inputStream.readAllBytes();
		} catch (Exception e) {
			throw new RuntimeException("Failed to read class bytes from " + generatedClazz, e);
		}
	}
}
