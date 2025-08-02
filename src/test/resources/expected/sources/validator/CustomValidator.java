package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.google.auto.service.AutoService;
import javax.annotation.processing.Generated;
import sources.validator.CustomValidator;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<Integer> FIELD = FieldReference.builder(value -> CustomValidator.field = value, () -> CustomValidator.field).doesSync(true).validator(ValidatorReference.<Integer>builder().fieldValidator(CustomValidator::simpleValidator).messageProvider(ignored -> "Value for 'field' must be a number. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build("FIELD");

    public static final FieldReference<Integer> FIELD_WITH_INTEGER = FieldReference.builder(value -> CustomValidator.fieldWithInteger = value, () -> CustomValidator.fieldWithInteger).doesSync(true).validator(ValidatorReference.<Integer>builder().fieldValidator(CustomValidator::simpleValidator).messageProvider(ignored -> "Value for 'fieldWithInteger' must be a number. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build("FIELD_WITH_INTEGER");

    @Override
    public String getName() {
        return "test-project";
    }

    @Override
    public FileType getFileType() {
        return FileType.TOML;
    }

    @Override
    public void load() {
        FIELD.load();
        FIELD_WITH_INTEGER.load();
    }

    @Override
    public void save() {
        FIELD.save();
        FIELD_WITH_INTEGER.save();
    }
}