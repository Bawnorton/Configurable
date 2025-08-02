package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.CompilationHelper;
import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import com.bawnorton.configurable.util.Pair;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

public class FieldTests {
    @Test
    public void testCommentedField() {
        ConfigurableTestHelper.logModule();
        Compiler compiler = CompilationHelper.newCompiler();

        Pair<JavaFileObject, JavaFileObject> sourceAndExpected = ConfigurableTestHelper.getSourceAndExpected("sources/field/CommentedField.java");
        JavaFileObject sourceFile = sourceAndExpected.first();
        Compilation compilation = compiler.compile(sourceFile);
        CompilationHelper.logDiagnostics(compilation);
        CompilationSubject.assertThat(compilation).succeededWithoutWarnings();

        JavaFileObject expectedFile = sourceAndExpected.second();
        CompilationSubject.assertThat(compilation).generatedSourceFile(ConfigurableTestHelper.getConfigLoaderName()).hasSourceEquivalentTo(expectedFile);
    }
}
