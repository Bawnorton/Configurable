package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericHolder;
import com.google.auto.service.AutoService;
import javax.annotation.processing.Generated;
import sources.validator.CustomValidator;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<Integer> FIELD = FieldReference.builder(value -> CustomValidator.field = value, () -> CustomValidator.field, new GenericHolder(Integer.class), "FIELD").doesSync(true).validator(ValidatorReference.<Integer>builder().fieldValidator(CustomValidator::simpleValidator).messageProvider(ignored -> "Value for 'FIELD' must be a number. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build();

    public static final FieldReference<Integer> FIELD_WITH_INTEGER = FieldReference.builder(value -> CustomValidator.fieldWithInteger = value, () -> CustomValidator.fieldWithInteger, new GenericHolder(Integer.class), "FIELD_WITH_INTEGER").doesSync(true).validator(ValidatorReference.<Integer>builder().fieldValidator(CustomValidator::simpleValidator).messageProvider(ignored -> "Value for 'FIELD_WITH_INTEGER' must be a number. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build();

    public static final FieldReference<Integer> FIELD_WITH_MAX = FieldReference.builder(value -> CustomValidator.fieldWithMax = value, () -> CustomValidator.fieldWithMax, new GenericHolder(Integer.class), "FIELD_WITH_MAX").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'FIELD_WITH_MAX' must be less than or equal to '100.0'. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build();

    public static final FieldReference<Integer> FIELD_WITH_MIN = FieldReference.builder(value -> CustomValidator.fieldWithMin = value, () -> CustomValidator.fieldWithMin, new GenericHolder(Integer.class), "FIELD_WITH_MIN").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'FIELD_WITH_MIN' must be greater than or equal to '0.0'. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build();

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
        FIELD_WITH_MAX.load();
        FIELD_WITH_MIN.load();
    }

    @Override
    public void save() {
        FIELD.save();
        FIELD_WITH_INTEGER.save();
        FIELD_WITH_MAX.save();
        FIELD_WITH_MIN.save();
    }
}