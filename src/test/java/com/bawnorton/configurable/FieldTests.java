package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import org.junit.jupiter.api.Test;

public class FieldTests extends BaseTest {
    @Test
    public void testCommentedField() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/field/CommentedField.java");
    }

    @Test
    public void testDuplicateField() {
        ConfigurableTestHelper.logModule();
        testCompilationFailure("sources/field/DuplicateField.java");
    }

    @Test
    public void testGroupedFields() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/field/GroupedFields.java");
    }

    @Test
    public void testListField() {
        ConfigurableTestHelper.logModule();
        testCompilationSuccess("sources/field/ListField.java");
    }
}
