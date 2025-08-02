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

public abstract class BaseTest {
    protected static void testCompilationSuccess(String resourceName) {
        Compiler compiler = CompilationHelper.newCompiler();

        Pair<JavaFileObject, JavaFileObject> sourceAndExpected = ConfigurableTestHelper.getSourceAndExpected(resourceName);
        JavaFileObject sourceFile = sourceAndExpected.first();
        Compilation compilation = compiler.compile(sourceFile);
        CompilationHelper.logDiagnostics(compilation);
        CompilationSubject.assertThat(compilation).succeededWithoutWarnings();

        JavaFileObject expectedFile = sourceAndExpected.second();
        CompilationSubject.assertThat(compilation).generatedSourceFile(ConfigurableTestHelper.getConfigLoaderName()).hasSourceEquivalentTo(expectedFile);
    }

    protected static void testCompilationFailure(String resourceName) {
        Compiler compiler = CompilationHelper.newCompiler();
        JavaFileObject sourceFile = JavaFileObjects.forResource(Resources.getResource(resourceName));
        Compilation compilation = compiler.compile(sourceFile);
        CompilationHelper.logDiagnostics(compilation);
        CompilationSubject.assertThat(compilation).failed();
    }
}
