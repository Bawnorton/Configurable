package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.google.auto.service.AutoService;
import javax.annotation.processing.Generated;
import sources.field.CommentedField;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<Integer> FIELD = FieldReference.builder(value -> CommentedField.field = value, () -> CommentedField.field).doesSync(true).comment(" A single field annotated with @Configurable.\n"
                                                                                                                                                                         + " Default value is 42.\n").validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'field' must be a number. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build("FIELD");

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
    }

    @Override
    public void save() {
        FIELD.save();
    }
}