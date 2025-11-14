package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.CompilationHelper;
import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.Pair;
import com.google.common.io.Resources;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class BaseTest {
	static {
		URL resource = Resources.getResource("configurable.properties");
		Path targetPath = Path.of(System.getProperty("user.dir"), "configurable.properties");
		try {
			Files.createDirectories(targetPath.getParent());
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					Files.deleteIfExists(targetPath);
				} catch (IOException e) {
					throw new RuntimeException("Failed to delete configurable.properties on shutdown", e);
				}
			}));
			Files.copy(resource.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Failed to copy configurable.properties to build resources", e);
		}
	}

	protected static Compilation testCompilationSuccess(String resourceName) {
		Compiler compiler = CompilationHelper.newCompiler();

		Pair<JavaFileObject, JavaFileObject> sourceAndExpected = ConfigurableTestHelper.getSourceAndExpected(resourceName);
		JavaFileObject sourceFile = sourceAndExpected.first();
		Compilation compilation = compiler.compile(sourceFile);
		CompilationHelper.logDiagnostics(compilation);
		CompilationSubject.assertThat(compilation).succeededWithoutWarnings();

		JavaFileObject expectedFile = sourceAndExpected.second();
		CompilationSubject.assertThat(compilation)
				.generatedSourceFile(ConfigurableTestHelper.getConfigLoaderName())
				.hasSourceEquivalentTo(expectedFile);
		return compilation;
	}

	protected static void testCompilationFailure(String resourceName) {
		Compiler compiler = CompilationHelper.newCompiler();
		JavaFileObject sourceFile = JavaFileObjects.forResource(Resources.getResource(resourceName));
		Compilation compilation = compiler.compile(sourceFile);
		CompilationHelper.logDiagnostics(compilation);
		CompilationSubject.assertThat(compilation).failed();
	}

	@NotNull
	protected static ConfigLoader getLoaderFromCompilation(Compilation compilation) {
		ClassLoader generatedClassLoader = CompilationHelper.getGeneratedClassLoader(compilation);
		ConfigLoader instance;
		try {
			Class<?> generatedClass = generatedClassLoader.loadClass(ConfigurableTestHelper.getConfigLoaderName());
			instance = (ConfigLoader) generatedClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("Failed to load generated class", e);
		}
		return instance;
	}

	protected static void assertFileContentEquals(URL expected, Path actual) {
		try {
			String expectedContent = Resources.toString(expected, StandardCharsets.UTF_8);
			String actualContent = Files.readString(actual);
			Assertions.assertEquals(expectedContent, actualContent, "File content does not match");
		} catch (IOException e) {
			throw new AssertionError("Failed to compare file contents", e);
		}
	}
}
