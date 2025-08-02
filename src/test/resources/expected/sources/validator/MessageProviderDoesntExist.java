package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericType;
import com.google.auto.service.AutoService;
import javax.annotation.processing.Generated;
import sources.validator.MessageProviderDoesntExist;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<Integer> field = FieldReference.builder(value -> MessageProviderDoesntExist.field = value, () -> MessageProviderDoesntExist.field, new GenericType(Integer.class), "FIELD").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "nonExistentMessageProvider").fallback(true).defaultSupplier(() -> 42).build()).build();

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
        field.load();
    }

    @Override
    public void save() {
        field.save();
    }
}
