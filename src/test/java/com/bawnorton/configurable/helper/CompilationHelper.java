package com.bawnorton.configurable.helper;

import com.bawnorton.configurable.processor.ConfigurableProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import org.junit.platform.commons.logging.Logger;

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
}
