package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericHolder;
import com.google.auto.service.AutoService;
import javax.annotation.processing.Generated;
import sources.validator.CustomMessageProvider;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<Integer> FIELD = FieldReference.builder(value -> CustomMessageProvider.field = value, () -> CustomMessageProvider.field, new GenericHolder(Integer.class), "FIELD").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(CustomMessageProvider::simpleMessageProvider).fallback(true).defaultSupplier(() -> 42).build()).build();

    public static final FieldReference<Integer> FIELD_WITH_INTEGER = FieldReference.builder(value -> CustomMessageProvider.fieldWithInteger = value, () -> CustomMessageProvider.fieldWithInteger, new GenericHolder(Integer.class), "FIELD_WITH_INTEGER").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(CustomMessageProvider::simpleMessageProvider).fallback(true).defaultSupplier(() -> 42).build()).build();

    public static final FieldReference<Integer> FIELD_WITH_LITERAL_MESSAGE = FieldReference.builder(value -> CustomMessageProvider.fieldWithLiteralMessage = value, () -> CustomMessageProvider.fieldWithLiteralMessage, new GenericHolder(Integer.class), "FIELD_WITH_LITERAL_MESSAGE").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "a literal message").fallback(true).defaultSupplier(() -> 42).build()).build();

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
        FIELD_WITH_LITERAL_MESSAGE.load();
    }

    @Override
    public void save() {
        FIELD.save();
        FIELD_WITH_INTEGER.save();
        FIELD_WITH_LITERAL_MESSAGE.save();
    }
}