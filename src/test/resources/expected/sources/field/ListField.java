package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericHolder;
import com.google.auto.service.AutoService;
import java.util.List;
import javax.annotation.processing.Generated;
import sources.field.ListField;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<List<Integer>> LIST_FIELD = FieldReference.builder(value -> ListField.listField = value, () -> ListField.listField, new GenericHolder(List.class, Integer.class), "LIST_FIELD").doesSync(true).validator(ValidatorReference.<List<Integer>>builder().messageProvider(ignored -> "Value for 'LIST_FIELD' is invalid. Resetting to default value: 'List.of(1, 2, 3, 4, 5)'").fallback(true).defaultSupplier(() -> List.of(1, 2, 3, 4, 5)).build()).build();

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
        LIST_FIELD.load();
    }

    @Override
    public void save() {
        LIST_FIELD.save();
    }
}
