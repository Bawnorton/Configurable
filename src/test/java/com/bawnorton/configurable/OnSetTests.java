package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import org.junit.jupiter.api.Test;

public class OnSetTests extends BaseTest {
    @Test
    public void testOnSetDoesntExist() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/onset/OnSetDoesntExist.java");
    }

    @Test
    public void testOnSetWithMoreThanTwoParameters() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/onset/OnSetWithMoreThanTwoParameters.java");
    }

    @Test
    public void testOnSetWithoutPublicModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/onset/OnSetWithoutPublicModifier.java");
    }

    @Test
    public void testOnSetWithoutStaticModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/onset/OnSetWithoutStaticModifier.java");
    }

    @Test
    public void testOnSetWithWrongParameterType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/onset/OnSetWithWrongParameterType.java");
    }

    @Test
    public void testOnSetWithWrongReturnType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/onset/OnSetWithWrongReturnType.java");
    }
}
