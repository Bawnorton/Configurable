package com.bawnorton.configurable;

import com.bawnorton.configurable.processor.ConfigurableProcessor;
import com.google.common.io.Resources;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;

public class CompilationHelper {
    public static Compilation compile(String resourceName) {
        JavaFileObject source = JavaFileObjects.forResource(Resources.getResource("%s.java".formatted(resourceName)));
        Compilation compilation = Compiler.javac()
                .withProcessors(new ConfigurableProcessor())
                .compile(source);

        compilation.notes().forEach(diagnostics -> ConfigurableTests.LOGGER.info(diagnostics::toString));
        compilation.warnings().forEach(diagnostics -> ConfigurableTests.LOGGER.warn(diagnostics::toString));

        if (compilation.status() != Compilation.Status.SUCCESS) {
            compilation.errors().forEach(diagnostics -> ConfigurableTests.LOGGER.error(diagnostics::toString));

            throw new RuntimeException("Compilation failed");
        }

        return compilation;
    }
}
