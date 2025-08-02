package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import org.junit.jupiter.api.Test;

public class ValidatorTests extends BaseTest {
    @Test
    public void testCustomValidator() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/validator/CustomValidator.java");
    }

    @Test
    public void testCustomMessageProvider() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/validator/CustomMessageProvider.java");
    }

    @Test
    public void testDefaultValueGreaterThanBounds() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/DefaultValueGreaterThanBounds.java");
    }

    @Test
    public void testDefaultValueSmallerThanBounds() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/DefaultValueSmallerThanBounds.java");
    }

    @Test
    public void testMessageProviderDoesntExist() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/validator/MessageProviderDoesntExist.java");
    }

    @Test
    public void testMessageProviderWithMoreThanOneParameter() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithMoreThanOneParameter.java");
    }

    @Test
    public void testMessageProviderWithoutPublicModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithoutPublicModifier.java");
    }

    @Test
    public void testMessageProviderWithoutStaticModifier() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithoutStaticModifier.java");
    }

    @Test
    public void testMessageProviderWithWrongParameterType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithWrongParameterType.java");
    }

    @Test
    public void testMessageProviderWithWrongReturnType() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MessageProviderWithWrongReturnType.java");
    }

    @Test
    public void testMinAndMaxConflict() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/MinAndMaxConflict.java");
    }

    @Test
    public void testNonNumericWithNumericBounds() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/NonNumericWithNumericBounds.java");
    }

    @Test
    public void testValidatorDoesntExist() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorDoesntExist.java");
    }

    @Test
    public void testValidatorWithMoreThanOneParameter() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/validator/ValidatorWithMoreThanOneParameter.java");
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
}
