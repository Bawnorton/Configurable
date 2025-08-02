package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.CompilationHelper;
import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import com.bawnorton.configurable.util.Pair;
import com.google.common.io.Resources;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

public class ValidatorTests {
    @Test
    public void testCustomValidator() {
        ConfigurableTestHelper.logModule();
        Compiler compiler = CompilationHelper.newCompiler();

        Pair<JavaFileObject, JavaFileObject> sourceAndExpected = ConfigurableTestHelper.getSourceAndExpected("sources/validator/CustomValidator.java");
        JavaFileObject sourceFile = sourceAndExpected.first();
        Compilation compilation = compiler.compile(sourceFile);
        CompilationHelper.logDiagnostics(compilation);
        CompilationSubject.assertThat(compilation).succeededWithoutWarnings();

        JavaFileObject expectedFile = sourceAndExpected.second();
        CompilationSubject.assertThat(compilation).generatedSourceFile(ConfigurableTestHelper.getConfigLoaderName()).hasSourceEquivalentTo(expectedFile);
    }

    @Test
    public void testMissingValidator() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorDoesntExist.java");
    }

    @Test
    public void testValidatorWithoutPublicModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithoutPublicModifier.java");
    }

    @Test
    public void testValidatorWithoutStaticModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithoutStaticModifier.java");
    }

    @Test
    public void testValidatorWithWrongParameterType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithWrongParameterType.java");
    }

    @Test
    public void testValidatorWithWrongReturnType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithWrongReturnType.java");
    }

    private static void testCompilationFailure(String resourceName) {
        Compiler compiler = CompilationHelper.newCompiler();
        JavaFileObject sourceFile = JavaFileObjects.forResource(Resources.getResource(resourceName));
        Compilation compilation = compiler.compile(sourceFile);
        CompilationHelper.logDiagnostics(compilation);
        CompilationSubject.assertThat(compilation).failed();
    }
}
